$(document).ready(function () {
  // Inisialisasi Select2
  $(".select2").select2({ theme: "bootstrap4" });
  var fileStore = {}; // Simpan file yang diupload

  // Simpan data bukti yang sudah diupload (untuk Tab 7)
  var uploadedPortofolio = [];

  // ============================================
  // 1. PENGAJUAN SKEMA (FIXED: SELECT SKEMA TIDAK DISABLE)
  // ============================================

  // A. Saat Skema Dipilih -> Load Jadwal
  $("#selectSkema2").on("change", function () {
    var skemaId = $(this).val();

    // Reset Jadwal & Anggaran
    var $jadwal = $("#selectJadwal");
    $jadwal.empty().append('<option value="">-- Memuat Jadwal... --</option>');

    // Kosongkan form anggaran & sembunyikan tombol reset
    $("#inputSumberAnggaran").val("");
    $("#inputPemberiAnggaran").val("");
    $("#wrapperBtnReset").hide();

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

    $('.btn-upload-modal[data-id="' + targetId + '"]')
      .parent()
      .html(btnHtml);

    // LOGIKA TAB 7: Tambahkan ke list Bukti Relevan jika tipe Portofolio
    if (type === "portofolio") {
      var docName = $("#uploadModalLabel").text().replace("Upload ", "");
      uploadedPortofolio.push({ id: targetId, text: docName });
      refreshBuktiRelevanDropdown();
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

    if (type === "portofolio") {
      uploadedPortofolio = uploadedPortofolio.filter(function (item) {
        return item.id !== targetId;
      });
      refreshBuktiRelevanDropdown();
    }
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

  // ============================================
  // 7. BUKTI KOMPETENSI - FIXED VERSION
  // ============================================

  // PERBAIKAN 1: Logic Radio Button (bukan checkbox)
  $(document).on("change", ".radio-kompetensi", function () {
    // Radio button otomatis exclusive, tidak perlu logic tambahan
    console.log(
      "Rekomendasi dipilih:",
      $(this).attr("name"),
      "=",
      $(this).val()
    );
  });

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

  // PERBAIKAN 2: Render dengan RADIO BUTTON dan NAME yang BENAR
  //   function renderBuktiKompetensi(units) {
  //     var container = $("#containerBuktiKompetensi");
  //     container.empty();

  //     if (!units || units.length === 0) return;

  //     units.forEach(function (unit, idx) {
  //       var tableHtml = `
  //             <div class="card card-outline card-secondary mb-4 mt-3">
  //                 <div class="card-header">
  //                     <h3 class="card-title font-weight-bold">
  //                         Unit Kompetensi Ke-${idx + 1}<br>
  //                         <small>${unit.code} - ${unit.title}</small>
  //                     </h3>
  //                 </div>
  //                 <div class="card-body p-0">
  //                     <table class="table table-bordered table-sm">
  //                         <thead class="bg-light text-center">
  //                             <tr>
  //                                 <th style="width: 50%" class="align-middle">Dapatkah Saya?</th>
  //                                 <th style="width: 5%" class="align-middle">K</th>
  //                                 <th style="width: 5%" class="align-middle">BK</th>
  //                                 <th style="width: 40%" class="align-middle">Bukti Relevan</th>
  //                             </tr>
  //                         </thead>
  //                         <tbody>
  //             `;

  //       if (unit.elements) {
  //         unit.elements.forEach(function (el) {
  //           // Render KUK List
  //           var kukListHtml =
  //             '<ul class="pl-3 mb-0" style="list-style-type: none;">';
  //           if (el.kuks) {
  //             el.kuks.forEach(function (kuk) {
  //               kukListHtml += `<li class="mb-2 ml-5 text-justify" style="white-space: pre-wrap; display: block;">${kuk}</li>`;
  //             });
  //           }
  //           kukListHtml += "</ul>";

  //           var elemenHeader = `<div class="font-weight-bold mb-2 ml-0">${el.no}. Elemen: ${el.name}</div>`;

  //           // PERBAIKAN CRITICAL: Gunakan RADIO BUTTON dengan name yang sama per elemen
  //           // Format: kompeten_elemen_{ID_ELEMEN}
  //           tableHtml += `
  //                         <tr>
  //                             <td class="align-top py-3 px-2">
  //                                 ${elemenHeader}
  //                                 <small class="text-muted ml-3">Kriteria Unjuk Kerja:</small>
  //                                 ${kukListHtml}
  //                             </td>
  //                             <td class="text-center align-middle">
  //                                 <input type="radio"
  //                                        class="radio-kompetensi"
  //                                        name="kompeten_elemen_${el.id}"
  //                                        value="K">
  //                             </td>
  //                             <td class="text-center align-middle">
  //                                 <input type="radio"
  //                                        class="radio-kompetensi"
  //                                        name="kompeten_elemen_${el.id}"
  //                                        value="BK">
  //                             </td>
  //                             <td class="align-middle px-3">
  //                                 <select class="form-control select2 select-bukti-relevan"
  //                                         name="bukti_elemen_${el.id}"
  //                                         multiple="multiple"
  //                                         data-placeholder="Pilih Bukti"
  //                                         style="width: 100%;">
  //                                 </select>
  //                             </td>
  //                         </tr>
  //                     `;
  //         });
  //       }
  //       tableHtml += `</tbody></table></div></div>`;
  //       container.append(tableHtml);
  //     });

  //     // Re-init Select2
  //     $(".select2").select2({ theme: "bootstrap4" });
  //     refreshBuktiRelevanDropdown();
  //   }
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

    $(".select2").select2({ theme: "bootstrap4", placeholder: "Pilih Bukti" });
    refreshBuktiRelevanDropdown();
  }

  // ============================================
  // 8. FINAL SUBMIT (MENGUMPULKAN DATA JADI JSON)
  // ============================================
  //   $("#form-pengajuan-skema").on("submit", function (e) {
  //     e.preventDefault();

  //     // 1. Validasi Manual
  //     if (!$("#selectSkema2").val() || !$("#selectJadwal").val()) {
  //       Swal.fire(
  //         "Peringatan",
  //         "Mohon pilih Skema dan Jadwal terlebih dahulu.",
  //         "warning"
  //       );
  //       return;
  //     }

  //     var formData = new FormData(this);

  //     // 2. Masukkan File dari Memory
  //     for (var key in fileStore) {
  //       formData.append(key, fileStore[key]);
  //     }

  //     // 3. PACKING DATA TAB 7 (APL-02) MENJADI JSON
  //     // Ini solusi untuk error "FileCountLimitExceeded"
  //     var apl02Data = [];
  //     var incomplete = false;

  //     $(".tr-elemen").each(function () {
  //       var elId = $(this).data("elemen-id");

  //       // Ambil Radio K/BK yang checked
  //       var status = $(this).find("input[type='radio']:checked").val();

  //       // Ambil Bukti ID (Array)
  //       var buktiIds = $(this).find(".select-bukti-relevan").val(); // Returns array ['id1', 'id2']

  //       // Validasi: Harus pilih K/BK
  //       if (!status) {
  //         incomplete = true;
  //         $(this).addClass("table-danger"); // Highlight merah
  //       } else {
  //         $(this).removeClass("table-danger");

  //         // Push ke array
  //         apl02Data.push({
  //           elemenId: elId,
  //           status: status,
  //           buktiIds: buktiIds || [], // Kirim array kosong jika tidak ada bukti
  //         });
  //       }
  //     });

  //     if (incomplete) {
  //       Swal.fire(
  //         "Peringatan",
  //         "Mohon lengkapi penilaian (K/BK) untuk semua elemen di Tab 7.",
  //         "warning"
  //       );
  //       // Pindah tab ke 7 otomatis
  //       $("#tab7-link").tab("show");
  //       return;
  //     }

  //     // Masukkan JSON String ke FormData
  //     formData.append("apl02Json", JSON.stringify(apl02Data));

  //     // Validasi Hidden Skema ID (Fix Disable Form)
  //     if (!formData.get("skemaId")) {
  //       var skemaIdValue = $("#hiddenSkemaId").val() || $("#selectSkema2").val();
  //       if (skemaIdValue) formData.set("skemaId", skemaIdValue);
  //     }

  //     Swal.fire({
  //       title: "Mengirim Data...",
  //       text: "Mohon tunggu sebentar.",
  //       allowOutsideClick: false,
  //       didOpen: () => {
  //         Swal.showLoading();
  //       },
  //     });

  //     $.ajax({
  //       url: $(this).attr("action"),
  //       type: "POST",
  //       data: formData,
  //       processData: false,
  //       contentType: false,
  //       success: function (response) {
  //         var res =
  //           typeof response === "string" ? JSON.parse(response) : response;
  //         Swal.fire("Sukses", res.message, "success").then(() => {
  //           window.location.href = "/asesi/daftar-sertifikasi";
  //         });
  //       },
  //       error: function (xhr) {
  //         var msg = "Terjadi kesalahan";
  //         try {
  //           msg = JSON.parse(xhr.responseText).message;
  //         } catch (e) {}
  //         Swal.fire("Gagal", msg, "error");
  //       },
  //     });
  //   });

  // Helper Tab 3
  function renderPersyaratan(reqs) {
    var html = "";
    if (reqs && reqs.length > 0) {
      reqs.forEach(function (r, idx) {
        html += `
            <tr>
                <td class="text-center">${idx + 1}</td>
                <td>${r} <input type="hidden" name="nama_syarat_${idx}" value="${r}"></td>
                <td class="text-center align-middle">
                    <button class="btn btn-sm btn-outline-primary btn-upload-modal" data-id="syarat_${idx}" data-type="syarat">
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

    // 1. Validasi Manual
    if (!$("#selectSkema2").val() || !$("#selectJadwal").val()) {
      Swal.fire(
        "Peringatan",
        "Mohon pilih Skema dan Jadwal terlebih dahulu.",
        "warning"
      );
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
      asesmenMandiri: [],
    };

    // Harvest Data Tab 7 (Looping TR)
    var incomplete = false;
    $(".tr-elemen").each(function () {
      var elId = $(this).data("elemen-id");
      var status = $(this).find("input[type='radio']:checked").val();
      var buktiIds = $(this).find(".select-bukti-relevan").val(); // Array

      if (!status) {
        incomplete = true;
        $(this).addClass("table-danger");
      } else {
        $(this).removeClass("table-danger");
        requestData.asesmenMandiri.push({
          elemenId: elId,
          status: status,
          buktiIds: buktiIds || [],
        });
      }
    });

    if (incomplete) {
      Swal.fire(
        "Peringatan",
        "Mohon lengkapi penilaian (K/BK) untuk semua elemen di Tab 7.",
        "warning"
      );
      $("#tab7-link").tab("show");
      return;
    }

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

  // ============================================
  // 8. FINAL SUBMIT - FIXED VERSION
  // ============================================
  //   $("#form-pengajuan-skema").on("submit", function (e) {
  //     e.preventDefault();

  //     // PERBAIKAN VALIDASI: Cek hidden input juga
  //     var skemaId = $("#hiddenSkemaId").val() || $("#selectSkema2").val();
  //     var jadwalId = $("#selectJadwal").val();

  //     console.log(
  //       "Validasi Submit - Skema ID:",
  //       skemaId,
  //       "| Jadwal ID:",
  //       jadwalId
  //     );

  //     if (!skemaId || !jadwalId) {
  //       Swal.fire(
  //         "Peringatan",
  //         "Mohon pilih Skema dan Jadwal terlebih dahulu.",
  //         "warning"
  //       );
  //       return;
  //     }

  //     // PERBAIKAN: Validasi Tab 7 - Pastikan semua elemen punya rekomendasi
  //     var allElemenRadios = $(".radio-kompetensi");
  //     var elemenGroups = {};

  //     allElemenRadios.each(function () {
  //       var name = $(this).attr("name");
  //       if (!elemenGroups[name]) {
  //         elemenGroups[name] = false;
  //       }
  //       if ($(this).is(":checked")) {
  //         elemenGroups[name] = true;
  //       }
  //     });

  //     var missingRekomendasi = [];
  //     for (var groupName in elemenGroups) {
  //       if (!elemenGroups[groupName]) {
  //         missingRekomendasi.push(groupName);
  //       }
  //     }

  //     if (missingRekomendasi.length > 0) {
  //       Swal.fire(
  //         "Peringatan",
  //         "Ada " +
  //           missingRekomendasi.length +
  //           " elemen yang belum diberi rekomendasi K/BK di Tab 7. Mohon lengkapi semua rekomendasi.",
  //         "warning"
  //       );
  //       return;
  //     }

  //     // Buat FormData
  //     var formData = new FormData(this);

  //     // PERBAIKAN CRITICAL: Pastikan skemaId terkirim
  //     // Jika select disabled, ambil dari hidden input
  //     if (!formData.get("skemaId")) {
  //       var skemaIdValue = $("#hiddenSkemaId").val();
  //       if (skemaIdValue) {
  //         formData.set("skemaId", skemaIdValue);
  //         console.log("Manual append skemaId:", skemaIdValue);
  //       }
  //     }

  //     // Masukkan File dari Memory
  //     for (var key in fileStore) {
  //       formData.append(key, fileStore[key]);
  //     }

  //     // DEBUG: Log data yang akan dikirim
  //     console.log("=== DATA YANG AKAN DIKIRIM ===");
  //     for (var pair of formData.entries()) {
  //       console.log(pair[0] + " = " + pair[1]);
  //     }

  //     Swal.fire({
  //       title: "Sedang Mengirim...",
  //       text: "Mohon jangan tutup halaman ini.",
  //       allowOutsideClick: false,
  //       didOpen: () => {
  //         Swal.showLoading();
  //       },
  //     });

  //     $.ajax({
  //       url: $(this).attr("action"),
  //       type: "POST",
  //       data: formData,
  //       processData: false,
  //       contentType: false,
  //       success: function (response) {
  //         var res =
  //           typeof response === "string" ? JSON.parse(response) : response;

  //         Swal.fire({
  //           title: "Berhasil!",
  //           text: res.message,
  //           icon: "success",
  //         }).then(() => {
  //           window.location.href = "/asesi/daftar-sertifikasi";
  //         });
  //       },
  //       error: function (xhr) {
  //         var msg = "Terjadi kesalahan server.";
  //         try {
  //           var err = JSON.parse(xhr.responseText);
  //           msg = err.message;
  //         } catch (e) {
  //           msg = xhr.responseText || msg;
  //         }

  //         console.error("Error Response:", xhr.responseText);
  //         Swal.fire("Gagal", msg, "error");
  //       },
  //     });
  //   });
});
