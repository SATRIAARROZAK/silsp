$(document).ready(function () {
  // Inisialisasi Select2
  $(".select2").select2({ theme: "bootstrap4" });
  var fileStore = {}; // Simpan file yang diupload

  // Simpan data bukti yang sudah diupload (untuk Tab 7)
  var uploadedPortofolio = [];

  // --- HELPER: FUNGSI SET ERROR ---
  function setError(input, message) {
    var formGroup = input.closest(".form-group");
    input.addClass("is-invalid");
    // Hapus pesan lama jika ada
    formGroup.find(".invalid-feedback").remove();
    // Tambah pesan baru
    formGroup.append(
      '<div class="invalid-feedback d-block">' + message + "</div>"
    );
  }

  function clearError(input) {
    var formGroup = input.closest(".form-group");
    input.removeClass("is-invalid");
    formGroup.find(".invalid-feedback").remove();
  }

  // Helper khusus untuk validasi tombol upload
  function setUploadError(button, message) {
    // Cari container tombol (td)
    var td = button.closest("td");
    // Tambah border merah pada tombol jika belum ada
    button.addClass("btn-outline-danger").removeClass("btn-outline-primary");
    // Tambah pesan error di bawah tombol
    if (td.find(".text-danger").length === 0) {
      td.append(
        '<small class="text-danger text-center d-block mt-1">' +
          message +
          "</small>"
      );
    }
  }

  // Helper untuk tombol upload
  function setAllUploadButtonsError(containerId, isError) {
    var $btns = $(containerId + " .btn-upload-modal");
    if (isError) {
      $btns.addClass("btn-outline-danger").removeClass("btn-outline-primary");
      $(containerId + " .table").addClass("border border-danger");
    } else {
      $btns.removeClass("btn-outline-danger").addClass("btn-outline-primary");
      $(containerId + " .table").removeClass("border border-danger");
      $(containerId + " .alert-danger-global").remove(); // Hapus alert error global jika ada
    }
  }

  function clearUploadError(button) {
    var td = button.closest("td");
    button.removeClass("btn-outline-danger").addClass("btn-outline-primary");
    td.find(".text-danger").remove();
  }

  // ============================================
  // 1. PENGAJUAN SKEMA (FIXED: SELECT SKEMA TIDAK DISABLE)
  // ============================================

  // A. Saat Skema Dipilih -> Load Jadwal
  $("#selectSkema2").on("change", function () {
    if ($(this).val())
      clearError($(this).next(".select2-container").find(".select2-selection"));
  });

  $("#selectSkema2").on("change", function () {
    var skemaId = $(this).val();

    // Reset Jadwal & Anggaran
    var $jadwal = $("#selectJadwal");
    $jadwal.empty().append('<option value="">-- Memuat Jadwal... --</option>');

    // Kosongkan form anggaran & sembunyikan tombol reset
    $("#inputSumberAnggaran").val("");
    $("#inputPemberiAnggaran").val("");
    $("#wrapperBtnReset").hide();

    // Clear Error Skema (Visual Select2)
    clearError($(this).next(".select2-container").find(".select2-selection"));

    // API Call Jadwal
    $.ajax({
      url: "/api/jadwal-by-skema/" + skemaId,
      type: "GET",
      success: function (data) {
        $jadwal.empty();
        if (data.length > 0) {
          $jadwal.append('<option value="">-- Pilih Jadwal --</option>');
          data.forEach(function (item) {
            $jadwal.append(
              '<option value="' + item.id + '">' + item.text + "</option>"
            );
          });
          $jadwal.prop("disabled", false);
        } else {
          $jadwal.append('<option value="">Tidak ada jadwal tersedia</option>');
          $jadwal.prop("disabled", true);
        }
      },
      error: function () {
        $jadwal.empty().append('<option value="">Gagal memuat jadwal</option>');
      },
    });

    // API Call Detail Skema
    $.ajax({
      url: "/api/skema-detail/" + skemaId,
      type: "GET",
      success: function (res) {
        renderPersyaratan(res.requirements);
        renderTujuanAsesmen(res.units, res.namaSkema);
        renderBuktiKompetensi(res.units);
      },
    });
  });

  $("#selectJadwal").on("change", function () {
    if ($(this).val())
      clearError($(this).next(".select2-container").find(".select2-selection"));
  });

  // B. Saat Jadwal Dipilih -> KUNCI SKEMA & Tampilkan Tombol X
  $("#selectJadwal").on("change", function () {
    var jadwalId = $(this).val();

    if (jadwalId) {
      // Isi Anggaran
      $.get("/api/jadwal-detail/" + jadwalId, function (res) {
        // Isi Tampilan (Nama)
        $("#inputSumberAnggaran").val(res.sumberAnggaranNama);
        $("#inputPemberiAnggaran").val(res.pemberiAnggaranNama);

        // Isi Data Hidden (ID)
        $("#hiddenSumberAnggaranId").val(res.sumberAnggaranId);
        $("#hiddenPemberiAnggaranId").val(res.pemberiAnggaranId);
      });

      // PERBAIKAN CRITICAL: Simpan value skema SEBELUM disable
      var skemaValue = $("#selectSkema2").val();

      // Kunci Skema
      $("#selectSkema2").prop("disabled", true);

      // Tampilkan Tombol Reset
      $("#wrapperBtnReset").show();

      // PENTING: Buat hidden input dengan value yang sudah disimpan
      if ($("#hiddenSkemaId").length === 0) {
        $("<input>")
          .attr({
            type: "hidden",
            id: "hiddenSkemaId",
            name: "skemaId",
            value: skemaValue,
          })
          .insertAfter("#selectSkema2");
      } else {
        $("#hiddenSkemaId").val(skemaValue);
      }

      // Clear Error Jadwal
      clearError($(this).next(".select2-container").find(".select2-selection"));
      console.log("Hidden skemaId created/updated:", skemaValue);
    }
  });

  // C. Saat Tombol Silang (X) Diklik -> RESET
  $("#btnResetJadwal").on("click", function () {
    $("#selectJadwal").val("").trigger("change");
    $("#hiddenSumberAnggaranId").val("");
    $("#hiddenPemberiAnggaranId").val("");
    $("#inputSumberAnggaran").val("");
    $("#inputPemberiAnggaran").val("");
    $("#wrapperBtnReset").hide();

    // Buka kunci skema & Hapus hidden input
    $("#selectSkema2").prop("disabled", false);
    $("#hiddenSkemaId").remove();
  });

  // ============================================
  // 2. DATA PEMOHON (LOGIKA PEKERJAAN & CLEAR DATA)
  // ============================================
  $(
    "#tab2 input[required], #tab2 select[required], #tab2 textarea[required]"
  ).on("input change", function () {
    if ($(this).val()) {
      clearError($(this));
      // Khusus Select2
      if ($(this).hasClass("select2-hidden-accessible")) {
        clearError(
          $(this).next(".select2-container").find(".select2-selection")
        );
      }
    }
  });

  function toggleJobDetails() {
    var jobTypeId = String($("#selectPekerjaan").val());
    var $section = $("#jobDetailsSection");
    var $inputs = $section.find("input, textarea");

    if (jobTypeId && jobTypeId !== "1") {
      // KONDISI: BEKERJA
      $section.slideDown();
      $inputs.prop("required", false);
      $section.find('input[name="companyName"]').prop("required", true);
    } else {
      // KONDISI: TIDAK BEKERJA / BELUM PILIH
      $section.slideUp();
      $inputs.prop("required", false);
      $inputs.val("");
      $inputs.removeClass("is-invalid"); // Bersihkan error visual jika di-hide
    }
  }

  setTimeout(function () {
    toggleJobDetails();
  }, 100);

  $("#selectPekerjaan").on("change", toggleJobDetails);

  // ============================================
  // 3, 4, 5. MODAL UPLOAD FILE
  // ============================================

  $(document).on("click", ".btn-upload-modal", function (e) {
    e.preventDefault();
    var targetId = $(this).data("id");
    var type = $(this).data("type");
    var label = $(this).closest("tr").find("td:eq(1)").text().trim();

    $("#uploadModalLabel").text("Upload " + label);
    $("#uploadTargetId").val(targetId);
    $("#uploadType").val(type);
    $("#uploadModal").modal("show");
  });

  $("#btnBatalUpload, #uploadModal .close").on("click", function () {
    $("#uploadModal").modal("hide");
    $("#fileInput").val("");
    $(".custom-file-label").text("Pilih file");
  });

  // Handle Simpan Upload
  $("#btnSimpanUpload").on("click", function () {
    var fileInput = $("#fileInput")[0];

    if (fileInput.files.length === 0) {
      Swal.fire("Error", "Pilih file terlebih dahulu!", "error");
      return;
    }

    var file = fileInput.files[0];
    var targetId = $("#uploadTargetId").val();
    var type = $("#uploadType").val();

    // SIMPAN FILE KE MEMORI
    fileStore[targetId] = file;

    var blobUrl = URL.createObjectURL(file);

    // Update UI Tombol
    var btnHtml = `
            <div class="action-buttons">
                <a href="${blobUrl}" target="_blank" class="btn btn-sm btn-outline-info" title="Lihat File">
                    <i class="fas fa-file"></i>
                </a>
                <button type="button" class="btn btn-sm btn-outline-danger btn-delete-upload" data-id="${targetId}" data-type="${type}">
                    <i class="fas fa-trash"></i>
                </button>
            </div>
            <input type="hidden" name="upload_status_${targetId}" value="1">
        `;

    // $('.btn-upload-modal[data-id="' + targetId + '"]')
    //   .parent()
    //   .html(btnHtml);

    var $btnOrigin = $('.btn-upload-modal[data-id="' + targetId + '"]');
    var $td = $btnOrigin.closest("td");
    $td.html(btnHtml);
    $td.find(".text-danger").remove(); // Hapus pesan error di row ini
    clearUploadError($btnOrigin);

    // LOGIKA TAB 7: Tambahkan ke list Bukti Relevan jika tipe Portofolio
    // Logic Khusus Portofolio (Tab 5 -> Tab 7)
    if (type === "portofolio") {
      var docName = $("#uploadModalLabel").text().replace("Upload ", "");
      uploadedPortofolio.push({ id: targetId, text: docName });
      refreshBuktiRelevanDropdown();

      // Jika sudah ada 1 portofolio, bersihkan error global di Tab 5
      if (uploadedPortofolio.length >= 1) {
        // $("#tab5 .table").removeClass("border border-danger");
        $("#error-tab5-global").remove();
        setAllUploadButtonsError("#tab5", false);
      }
    }

    // Logic Khusus Persyaratan (Tab 3) - Cek minimal 1
    if (type === "syarat") {
      // Cek jumlah syarat terupload
      if ($('input[name^="upload_status_syarat_"]').length >= 1) {
        // $("#tab3 .table").removeClass("border border-danger");
        $("#error-tab3-global").remove();
        setAllUploadButtonsError("#tab3", false);
      }
    }

    // Logic Khusus Administrasi (Tab 4) - Cek Semua
    if (type === "administrasi") {
      // Cek apakah KTP dan Foto sudah ada
      // (Asumsi ID administrasi_1 dan administrasi_2)
      // Kita bisa cek via fileStore keys atau DOM
      // Nanti divalidasi ulang saat submit akhir
    }

    $("#uploadModal").modal("hide");
    $("#fileInput").val("");
    $(".custom-file-label").text("Pilih file");
  });

  // Handle Hapus Upload
  $(document).on("click", ".btn-delete-upload", function () {
    var targetId = $(this).data("id");
    var type = $(this).data("type");
    var container = $(this).closest("td");

    var resetHtml = `
            <div class="action-buttons">
                <button class="btn btn-sm btn-outline-primary btn-upload-modal" data-id="${targetId}" data-type="${type}">
                    <i class="fas fa-upload mr-1"></i> Upload
                </button>
            </div>
        `;
    container.html(resetHtml);

    delete fileStore[targetId];

    // Cek ulang jumlah upload untuk validasi min 1 (Balik merah jika 0)
    if (type === "portofolio") {
      uploadedPortofolio = uploadedPortofolio.filter(function (item) {
        return item.id !== targetId;
      });
      refreshBuktiRelevanDropdown();
      if (uploadedPortofolio.length === 0)
        setAllUploadButtonsError("#tab5", true);
    }

    if (type === "syarat") {
      if ($('input[name^="upload_status_syarat_"]').length === 0)
        setAllUploadButtonsError("#tab3", true);
    }

    // if (type === "portofolio") {
    //   uploadedPortofolio = uploadedPortofolio.filter(function (item) {
    //     return item.id !== targetId;
    //   });
    //   refreshBuktiRelevanDropdown();
    // }
  });

  // ============================================
  // 6. TUJUAN ASESMEN
  // ============================================
  function renderTujuanAsesmen(units, namaSkema) {
    $("#namaSkemaLabel").text(namaSkema);

    var html = "";
    if (units && units.length > 0) {
      units.forEach(function (u, idx) {
        html += `
                <tr>
                    <td class="text-center">${idx + 1}</td>
                    <td class="font-weight-bold">${u.code}</td>
                    <td>${u.title}</td>
                </tr>`;
      });
    } else {
      html =
        '<tr><td colspan="3" class="text-center">Tidak ada unit kompetensi.</td></tr>';
    }
    $("#listUnitTujuan").html(html);

    $('input[name="tujuanAsesmen"]').prop("checked", false);
  }

  // Realtime Validation Radio Tujuan
  $('input[name="tujuanAsesmen"]').on("change", function () {
    $("#error-tujuan-asesmen").remove(); // Hapus pesan error
    $(".custom-control-label").removeClass("text-danger");
  });

  // ============================================
  // 7. BUKTI KOMPETENSI - FIXED VERSION
  // ============================================

  // PERBAIKAN 1: Logic Radio Button (bukan checkbox)
  $(document).on(
    "change",
    ".radio-kompetensi, .select-bukti-relevan",
    function () {
      validateElemenRow($(this).closest("tr"));
      // Radio button otomatis exclusive, tidak perlu logic tambahan
      console.log(
        "Rekomendasi dipilih:",
        $(this).attr("name"),
        "=",
        $(this).val()
      );
    }
  );

  // Fungsi validasi per baris elemen
  function validateElemenRow($row) {
    var status = $row.find("input[type='radio']:checked").val(); // K atau BK
    var buktiIds = $row.find(".select-bukti-relevan").val(); // Array bukti

    var isValid = false;

    if (!status) {
      // Belum pilih rekomendasi -> Invalid
      isValid = false;
    } else if (status === "BK") {
      // Jika BK, bukti tidak wajib -> Valid
      isValid = true;
      // Opsional: Disable select bukti jika BK
      // $row.find(".select-bukti-relevan").prop("disabled", true);
    } else if (status === "K") {
      // Jika K, bukti WAJIB dipilih -> Cek length
      if (buktiIds && buktiIds.length > 0) {
        isValid = true;
      } else {
        isValid = false; // K tapi belum pilih bukti
      }
    }

    if (isValid) {
      $row.removeClass("table-danger");
    } else {
      // Jangan langsung merah saat user baru klik K (beri kesempatan pilih bukti)
      // Tapi saat submit nanti akan dicek lagi
      // Untuk realtime UX, kita bisa biarkan saja atau beri hint visual
    }
    return isValid;
  }

  function refreshBuktiRelevanDropdown() {
    var $selects = $(".select-bukti-relevan");

    $selects.each(function () {
      var $sel = $(this);
      var currentVal = $sel.val();

      $sel.empty();

      if (uploadedPortofolio.length > 0) {
        uploadedPortofolio.forEach(function (item) {
          var isSelected =
            currentVal && currentVal.includes(item.id) ? "selected" : "";
          $sel.append(
            `<option value="${item.id}" ${isSelected}>${item.text}</option>`
          );
        });
      }

      $sel.trigger("change");
    });
  }
  // ============================================
  // FUNGSI RENDER TAB 7 (UPDATE: Tambah data-id pada TR)
  // ============================================
  function renderBuktiKompetensi(units) {
    var container = $("#containerBuktiKompetensi");
    container.empty();

    if (!units || units.length === 0) return;

    units.forEach(function (unit, idx) {
      var tableHtml = `
            <div class="card card-outline card-secondary mb-4 mt-3">
                <div class="card-header">
                    <h3 class="card-title font-weight-bold">
                        Unit Kompetensi Ke-${idx + 1}<br>
                        <small>${unit.code} - ${unit.title}</small>
                    </h3>
                </div>
                <div class="card-body p-0">
                    <table class="table table-bordered table-sm">
                        <thead class="bg-light text-center">
                            <tr>
                                <th style="width: 50%" class="align-middle">Dapatkah Saya?</th>
                                <th style="width: 5%" class="align-middle">K</th>
                                <th style="width: 5%" class="align-middle">BK</th>
                                <th style="width: 40%" class="align-middle">Bukti Relevan</th>
                            </tr>
                        </thead>
                        <tbody>
            `;

      if (unit.elements) {
        unit.elements.forEach(function (el) {
          // Render KUK List
          var kukListHtml =
            '<ul class="pl-3 mb-0" style="list-style-type: none;">';
          if (el.kuks) {
            el.kuks.forEach(function (kuk, kIdx) {
              // Handle jika kuk object atau string
              var kukText = typeof kuk === "object" ? kuk.name : kuk;
              kukListHtml += `<li class="mb-2 ml-5 text-justify" style="white-space: pre-wrap; display: block;">${kuk}</li>`;
            });
          }
          kukListHtml += "</ul>";
          var elemenHeader = `<div class="font-weight-bold mb-2 ml-0">${el.no}. Elemen: ${el.name}</div>`;

          // TAMBAHKAN CLASS 'tr-elemen' DAN ATRIBUT 'data-elemen-id'
          tableHtml += `
            <tr class="tr-elemen" data-elemen-id="${el.id}">
                <td class="align-top py-3 px-2">
                    ${elemenHeader}
                    <small class="text-muted ml-3">Kriteria Unjuk Kerja:</small>
                    ${kukListHtml}
                </td>
                
                <td class="text-center align-middle">
                    <input type="radio" class="radio-kompetensi" name="kompeten_elemen_${el.id}" value="K">
                </td>
                <td class="text-center align-middle">
                    <input type="radio" class="radio-kompetensi" name="kompeten_elemen_${el.id}" value="BK">
                </td>
                
                <td class="align-middle px-3">
                    <select class="form-control select2 select-bukti-relevan" multiple="multiple" data-placeholder="Pilih Bukti" style="width: 100%;">
                    </select>
                </td>
            </tr>
          `;
        });
      }
      tableHtml += `</tbody></table></div></div>`;
      container.append(tableHtml);
    });

    $(".select2").select2({ theme: "bootstrap4" });
    refreshBuktiRelevanDropdown();
  }

  // Helper Tab 3
  //   function renderPersyaratan(reqs) {
  //     var html = "";
  //     if (reqs && reqs.length > 0) {
  //       reqs.forEach(function (r, idx) {
  //         html += `
  //             <tr>
  //                 <td class="text-center">${idx + 1}</td>
  //                 <td>${r} <input type="hidden" name="nama_syarat_${idx}" value="${r}"></td>
  //                 <td class="text-center align-middle">
  //                     <button class="btn btn-sm btn-outline-primary btn-upload-modal" data-id="syarat_${idx}" data-type="syarat">
  //                         <i class="fas fa-upload mr-1"></i> Upload
  //                     </button>
  //                 </td>
  //             </tr>`;
  //       });
  //     } else {
  //       html =
  //         '<tr><td colspan="3" class="text-center">Tidak ada persyaratan khusus.</td></tr>';
  //     }
  //     $("#tablePersyaratanBody").html(html);
  //   }

  // Helper Tab 3
  function renderPersyaratan(reqs) {
    var html = "";
    if (reqs && reqs.length > 0) {
      reqs.forEach(function (r, idx) {
        // r sekarang adalah object {id: 1, description: "..."}
        // Kita gunakan r.id sebagai key unik

        html += `
            <tr>
                <td class="text-center">${idx + 1}</td>
                <td>${r.description}</td> 
                <td class="text-center align-middle">
                    <button class="btn btn-sm btn-outline-primary btn-upload-modal" 
                            data-id="syarat_${r.id}" 
                            data-type="syarat">
                        <i class="fas fa-upload mr-1"></i> Upload
                    </button>
                </td>
            </tr>`;
      });
    } else {
      html =
        '<tr><td colspan="3" class="text-center">Tidak ada persyaratan khusus.</td></tr>';
    }
    $("#tablePersyaratanBody").html(html);
  }

  // ============================================
  // 8. FINAL SUBMIT (JSON API APPROACH)
  // ============================================
  $("#form-pengajuan-skema").on("submit", function (e) {
    e.preventDefault();

    var isValid = true;
    var firstErrorTab = null;

    // --- VALIDASI TAB 1: SKEMA & JADWAL ---
    if (!$("#selectSkema2").val()) {
      setError(
        $("#selectSkema2")
          .next(".select2-container")
          .find(".select2-selection"),
        "Pilih Skema terlebih dahulu."
      );
      isValid = false;
      if (!firstErrorTab) firstErrorTab = "#tab1-link";
    }
    if (!$("#selectJadwal").val()) {
      setError(
        $("#selectJadwal")
          .next(".select2-container")
          .find(".select2-selection"),
        "Pilih Jadwal terlebih dahulu."
      );
      isValid = false;
      if (!firstErrorTab) firstErrorTab = "#tab1-link";
    }

    // 1. Validasi Manual
    if (!$("#selectSkema2").val() || !$("#selectJadwal").val()) {
      Swal.fire(
        "Peringatan",
        "Mohon pilih Skema dan Jadwal terlebih dahulu.",
        "warning"
      );
      return;
    }

    // --- VALIDASI TAB 2: DATA PEMOHON ---
    // Gunakan HTML5 checkValidity untuk input required standard
    var tab2Inputs = $("#tab2").find(
      "input[required], select[required], textarea[required]"
    );
    tab2Inputs.each(function () {
      if (!$(this).val()) {
        setError($(this), "Field ini wajib diisi.");
        // Khusus Select2
        if ($(this).hasClass("select2-hidden-accessible")) {
          setError(
            $(this).next(".select2-container").find(".select2-selection"),
            "Wajib dipilih."
          );
        }
        isValid = false;
        if (!firstErrorTab) firstErrorTab = "#tab2-link";
      }
    });

    // --- VALIDASI TAB 3: PERSYARATAN (Minimal 1 Upload) ---
    var syaratCount = 0;
    // Hitung berapa file yang ada di memory dengan prefix "syarat_"
    for (var key in fileStore) {
      if (key.startsWith("syarat_")) syaratCount++;
    }

    if (syaratCount < 1) {
      $("#error-tab3-global").remove();
      $("#tab3 .table").before(
        '<div id="error-tab3-global" class="alert alert-danger p-2">Wajib mengupload minimal 1 Persyaratan Dasar!</div>'
      );
      $("#tab3 .table").addClass("border border-danger");

      // Highlight tombol upload
      $("#tab3 .btn-upload-modal")
        .addClass("btn-outline-danger")
        .removeClass("btn-outline-primary");

      isValid = false;
      if (!firstErrorTab) firstErrorTab = "#tab3-link";
    } else {
      // Valid (Visual Reset)
      $("#error-tab3-global").remove();
      $("#tab3 .table").removeClass("border border-danger");
    }

    // --- VALIDASI TAB 4: BUKTI ADMINISTRASI (Wajib Semua: KTP & Foto) ---
    // Asumsi ID: administrasi_1 (KTP), administrasi_2 (Foto)
    var admin1 = fileStore["administrasi_1"];
    var admin2 = fileStore["administrasi_2"];

    if (!admin1) {
      setUploadError(
        $('.btn-upload-modal[data-id="administrasi_1"]'),
        "KTP Wajib diupload!"
      );
      isValid = false;
      if (!firstErrorTab) firstErrorTab = "#tab4-link";
    } else {
      clearUploadError($('.btn-upload-modal[data-id="administrasi_1"]'));
    }

    if (!admin2) {
      setUploadError(
        $('.btn-upload-modal[data-id="administrasi_2"]'),
        "Pas Foto Wajib diupload!"
      );
      isValid = false;
      if (!firstErrorTab) firstErrorTab = "#tab4-link";
    } else {
      clearUploadError($('.btn-upload-modal[data-id="administrasi_2"]'));
    }

    // --- VALIDASI TAB 5: BUKTI PORTOFOLIO (Minimal 1 Upload) ---
    var portofolioCount = 0;
    for (var key in fileStore) {
      if (key.startsWith("portofolio_")) portofolioCount++;
    }

    if (portofolioCount < 1) {
      $("#error-tab5-global").remove();
      $("#tab5 .table").before(
        '<div id="error-tab5-global" class="alert alert-danger p-2">Wajib mengupload minimal 1 Bukti Portofolio!</div>'
      );
      $("#tab5 .table").addClass("border border-danger");
      $("#tab5 .btn-upload-modal")
        .addClass("btn-outline-danger")
        .removeClass("btn-outline-primary");
      isValid = false;
      if (!firstErrorTab) firstErrorTab = "#tab5-link";
    } else {
      $("#error-tab5-global").remove();
      $("#tab5 .table").removeClass("border border-danger");
    }

    // --- VALIDASI TAB 6: TUJUAN ASESMEN ---
    if (!$("input[name='tujuanAsesmen']:checked").val()) {
      $("#error-tujuan-asesmen").remove();
      $("#tab6 .form-group").append(
        '<div id="error-tujuan-asesmen" class="text-danger mt-2">Harap pilih tujuan asesmen.</div>'
      );
      isValid = false;
      if (!firstErrorTab) firstErrorTab = "#tab6-link";
    }

    // JIKA TAB 1-6 ADA YANG INVALID
    if (!isValid) {
      if (firstErrorTab) $(firstErrorTab).tab("show");
      Swal.fire(
        "Gagal",
        "Mohon isi semua data pada formulir pendaftaran.",
        "error"
      );
      return;
    }

    // --- STEP 2: VALIDASI KHUSUS TAB 7 (APL-02) ---
    // Hanya dijalankan jika Tab 1-6 sudah valid

    var asesmenMandiriData = [];
    var missingRekomendasiCount = 0;
    var missingBuktiCount = 0;

    $(".tr-elemen").each(function () {
      var elId = $(this).data("elemen-id");
      var status = $(this).find("input[type='radio']:checked").val();
      var buktiIds = $(this).find(".select-bukti-relevan").val();

      var rowIsComplete = true;

      if (!status) {
        missingRekomendasiCount++;
        rowIsComplete = false;
      } else if (status === "K" && (!buktiIds || buktiIds.length === 0)) {
        missingBuktiCount++;
        rowIsComplete = false;
      }

      if (!rowIsComplete) {
        $(this).addClass("table-danger");
      } else {
        $(this).removeClass("table-danger");
        asesmenMandiriData.push({
          elemenId: elId,
          status: status,
          buktiIds: buktiIds || [],
        });
      }
    });

    // PERBAIKAN 2: SweetAlert Detail untuk Tab 7
    if (missingRekomendasiCount > 0 || missingBuktiCount > 0) {
      var msg = "";
      if (missingRekomendasiCount > 0)
        msg +=
          "<li><b>" +
          missingRekomendasiCount +
          "</b> Terdapat Elemen belum diberi rekomendasi (K/BK).</li>";
      if (missingBuktiCount > 0)
        msg +=
          "<li><b>" +
          missingBuktiCount +
          "</b> Terdapat Elemen Kompeten (K) belum menyertakan Bukti Relevan.</li>";

      Swal.fire({
        title: "Data Asesmen Belum Lengkap",
        html: "<ul style='text-align: left;'>" + msg + "</ul>",
        icon: "warning",
      });

      $("#tab7-link").tab("show");
      return;
    }

    // --- CONSTRUCT JSON OBJECT ---
    // Kita bangun objek data secara manual agar terstruktur rapi

    var requestData = {
      // Tab 1 & 6
      skemaId: $("#selectSkema2").val(), // Ambil dari select (krn sudah tidak disabled)
      jadwalId: $("#selectJadwal").val(),
      sumberAnggaranId: $("#hiddenSumberAnggaranId").val(),
      pemberiAnggaranId: $("#hiddenPemberiAnggaranId").val(),
      tujuanAsesmen: $("input[name='tujuanAsesmen']:checked").val(),

      // Tab 2: Data Pemohon
      dataPemohon: {
        nik: $("input[name='nik']").val(),
        fullName: $("input[name='fullName']").val(),
        birthPlace: $("input[name='birthPlace']").val(),
        birthDate: $("input[name='birthDate']").val(),
        gender: $("select[name='gender']").val(),
        provinceId: $("#selectProvinsi").val(),
        cityId: $("#selectKota").val(),
        districtId: $("#selectKecamatan").val(),
        address: $("textarea[name='address']").val(),
        postalCode: $("input[name='postalCode']").val(),
        email: $("input[name='email']").val(),
        phoneNumber: $("input[name='phoneNumber']").val(),
        educationId: $("#selectPendidikan").val(),
        jobTypeId: $("#selectPekerjaan").val(),
        // Detail Pekerjaan (Optional)
        companyName: $("input[name='companyName']").val(),
        position: $("input[name='position']").val(),
        officePhone: $("input[name='officePhone']").val(),
        officeEmail: $("input[name='officeEmail']").val(),
        officeFax: $("input[name='officeFax']").val(),
        officeAddress: $("textarea[name='officeAddress']").val(),
      },

      // Tab 7: Asesmen Mandiri (APL-02)
      asesmenMandiri: asesmenMandiriData,
    };

    // --- PREPARE FORM DATA ---
    var formData = new FormData();

    // 1. Masukkan JSON sebagai String
    formData.append("jsonData", JSON.stringify(requestData));

    // 2. Masukkan File (Binary) dari Memory
    var fileCount = 0;
    for (var key in fileStore) {
      formData.append(key, fileStore[key]);
      fileCount++;
    }

    // DEBUG
    console.log("JSON Payload:", requestData);
    console.log("File Count:", fileCount);

    Swal.fire({
      title: "Mengirim Data...",
      text: "Mohon tunggu sebentar, data sedang diproses.",
      allowOutsideClick: false,
      didOpen: () => {
        Swal.showLoading();
      },
    });

    // --- AJAX SEND ---
    $.ajax({
      url: $(this).attr("action"),
      type: "POST",
      data: formData,
      processData: false, // Wajib false agar FormData tidak diproses string
      contentType: false, // Wajib false agar header multipart diset browser
      success: function (response) {
        var res =
          typeof response === "string" ? JSON.parse(response) : response;
        Swal.fire("Sukses", res.message, "success").then(() => {
          window.location.href = "/asesi/daftar-sertifikasi";
        });
      },
      error: function (xhr) {
        var msg = "Terjadi kesalahan server.";
        try {
          var err = JSON.parse(xhr.responseText);
          msg = err.message;
        } catch (e) {}
        console.error(xhr.responseText);
        Swal.fire("Gagal", msg, "error");
      },
    });
  });
});
