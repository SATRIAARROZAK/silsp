$(document).ready(function () {
  // Inisialisasi Select2
  $(".select2").select2({ theme: "bootstrap4" });
  var fileStore = {}; // Tambahkan ini di paling atas $(document).ready

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
        // $("#inputSumberAnggaran").val(res.sumberAnggaran);
        // $("#inputPemberiAnggaran").val(res.pemberiAnggaran);

        // Isi Tampilan (Nama)
        $("#inputSumberAnggaran").val(res.sumberAnggaranNama);
        $("#inputPemberiAnggaran").val(res.pemberiAnggaranNama);

        // Isi Data Hidden (ID) - PERBAIKAN DISINI
        $("#hiddenSumberAnggaranId").val(res.sumberAnggaranId);
        $("#hiddenPemberiAnggaranId").val(res.pemberiAnggaranId);
      });

      // Kunci Skema
      $("#selectSkema2").prop("disabled", true);
      // Tampilkan Tombol Reset
      $("#wrapperBtnReset").show();
      // Tambahkan input hidden untuk skemaId agar tetap terkirim meski select disabled
      if ($("#hiddenSkemaId").length === 0) {
        $("<input>")
          .attr({
            type: "hidden",
            id: "hiddenSkemaId",
            name: "skemaId",
            value: $("#selectSkema2").val(),
          })
          .appendTo("#form-pengajuan-skema");
      } else {
        $("#hiddenSkemaId").val($("#selectSkema2").val());
      }
    }
  });

  // C. Saat Tombol Silang (X) Diklik -> RESET
  $("#btnResetJadwal").on("click", function () {
    $("#selectJadwal").val("").trigger("change");
    // $("#inputSumberAnggaran").val("");
    // $("#inputPemberiAnggaran").val("");
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
    // Ambil value dropdown pekerjaan
    var jobTypeId = String($("#selectPekerjaan").val());

    // Ambil elemen container dan semua input didalamnya
    var $section = $("#jobDetailsSection");
    var $inputs = $section.find("input, textarea");

    // Asumsi: ID '1' adalah 'Tidak Bekerja' di database TypePekerjaan
    // Sesuaikan ID ini jika di database Anda berbeda
    if (jobTypeId && jobTypeId !== "1") {
      // KONDISI: BEKERJA
      $section.slideDown();

      // 1. Reset required semua dulu
      $inputs.prop("required", false);

      // 2. Set HANYA Company Name yang wajib
      $section.find('input[name="companyName"]').prop("required", true);
    } else {
      // KONDISI: TIDAK BEKERJA / BELUM PILIH
      $section.slideUp();

      // 1. Hapus validasi required
      $inputs.prop("required", false);

      // 2. BERSIHKAN DATA (PENTING!)
      // Ini memastikan data yang sempat diisi jadi kosong lagi agar tidak terkirim ke DB
      $inputs.val("");
    }
  }

  // PENTING: Jalankan saat halaman selesai dimuat (untuk handle data edit/validasi error)
  setTimeout(function () {
    toggleJobDetails();
  }, 100);

  // Jalankan setiap kali dropdown berubah
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

  // PERBAIKAN: Fungsi Tombol Batal & Close (X) secara Manual
  $("#btnBatalUpload, #uploadModal .close").on("click", function () {
    $("#uploadModal").modal("hide");
    // Reset input file agar saat dibuka lagi bersih
    $("#fileInput").val("");
    $(".custom-file-label").text("Pilih file");
  });

  // Handle Simpan Upload
  $("#btnSimpanUpload").on("click", function () {
    var fileInput = $("#fileInput")[0];

    // Validasi File Kosong
    if (fileInput.files.length === 0) {
      Swal.fire("Error", "Pilih file terlebih dahulu!", "error");
      return;
    }

    var file = fileInput.files[0];
    var targetId = $("#uploadTargetId").val();
    var type = $("#uploadType").val();

    // SIMPAN FILE KE MEMORI
    fileStore[targetId] = file;

    // --- PERBAIKAN 2: MEMBUAT URL PREVIEW DARI FILE LOKAL ---
    var blobUrl = URL.createObjectURL(file);

    // Update UI Tombol -> Jadi "Lihat" (href ke blobUrl) dan "Hapus"
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

    // Cari tombol asal dan ganti dengan tombol baru
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
    $("#fileInput").val(""); // Reset input file
    $(".custom-file-label").text("Pilih file");
  });

  // Handle Hapus Upload

  $(document).on("click", ".btn-delete-upload", function () {
    var targetId = $(this).data("id");
    var type = $(this).data("type");
    var container = $(this).closest("td");

    // Kembalikan tombol upload awal
    var resetHtml = `
            <div class="action-buttons">
                    <button class="btn btn-sm btn-outline-primary btn-upload-modal" data-id="${targetId}" data-type="${type}">
                    <i class="fas fa-upload mr-1"></i> Upload
                    </button>
                </div>
        `;
    container.html(resetHtml);

    delete fileStore[targetId];

    // Hapus dari list Portofolio jika perlu
    if (type === "portofolio") {
      uploadedPortofolio = uploadedPortofolio.filter(function (item) {
        return item.id !== targetId;
      });
      refreshBuktiRelevanDropdown();
    }
  });

  // ============================================
  // 6. TUJUAN ASESMEN (NAMA SKEMA & RADIO)
  // ============================================
  function renderTujuanAsesmen(units, namaSkema) {
    // Update Judul Skema
    $("#namaSkemaLabel").text(namaSkema);

    // Render List Unit
    var html = "";
    if (units && units.length > 0) {
      units.forEach(function (u, idx) {
        html += `
                <tr>
                    <td class="text-center">${idx + 1}</td>
                    <td class="font-weight-bold">${u.code}</td>
                    <td>${u.title}</td>`;
      });
    } else {
      html =
        '<tr><td colspan="3" class="text-center">Tidak ada unit kompetensi.</td></tr>';
    }
    $("#listUnitTujuan").html(html);

    // Reset Radio Button (Uncheck All)
    $('input[name="tujuanAsesmen"]').prop("checked", false);
  }

  // ============================================
  // 7. BUKTI KOMPETENSI (CHECKBOX LOGIC & DROPDOWN)
  // ============================================

  // Logic Checkbox K vs BK (Mutual Exclusion)
  $(document).on("change", ".cb-kompetensi", function () {
    var name = $(this).attr("name"); // kompeten_CODE_NO
    var val = $(this).val(); // K atau BK

    if ($(this).is(":checked")) {
      // Uncheck yang lainnya dalam grup nama yang sama
      $('input[name="' + name + '"]')
        .not(this)
        .prop("checked", false);
    }
  });

  function refreshBuktiRelevanDropdown() {
    var $selects = $(".select-bukti-relevan");

    // Simpan value lama agar tidak hilang saat refresh opsi
    $selects.each(function () {
      var $sel = $(this);
      var currentVal = $sel.val();

      $sel.empty(); // Kosongkan opsi

      if (uploadedPortofolio.length > 0) {
        // Tambahkan opsi dari array portofolio yg sudah diupload
        uploadedPortofolio.forEach(function (item) {
          var isSelected =
            currentVal && currentVal.includes(item.id) ? "selected" : "";
          $sel.append(
            `<option value="${item.id}" ${isSelected}>${item.text}</option>`
          );
        });
      } else {
        // Tidak ada opsi
        // Select2 placeholder akan muncul otomatis jika kosong
      }

      // Trigger change untuk update tampilan select2
      $sel.trigger("change");
    });
  }

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
  //                                 <th style="width: 15%" class="align-middle">Bukti Relevan</th>
  //                             </tr>
  //                         </thead>
  //                         <tbody>
  //             `;

  //       if (unit.elements) {
  //         unit.elements.forEach(function (el) {
  //           // List KUK
  //           var kukListHtml =
  //             '<ul class="pl-3 mb-0" style="list-style-type: none;">';
  //           if (el.kuks) {
  //             el.kuks.forEach(function (kuk) {
  //               kukListHtml += `<li class="mb-2 ml-5 text-justify" style=" white-space: pre-wrap; display: block;">${kuk}
  //                              </li>`;
  //             });
  //           }
  //           kukListHtml += "</ul>";

  //           // Header Elemen
  //           var elemenHeader = `<div class="font-weight-bold mb-2 ml-0">${el.no}. Elemen: ${el.name}</div>`;

  //           tableHtml += `
  //                         <tr>
  //                             <td class="align-top py-3 px-2">
  //                                 ${elemenHeader}
  //                                 <small class="text-muted ml-3">Kriteria Unjuk Kerja:</small>
  //                                 ${kukListHtml}
  //                             </td>
  //                             <td class="text-center align-middle">
  //                                 <input type="checkbox" class="cb-kompetensi" name="kompeten_${unit.code}_${el.no}" value="K">
  //                             </td>
  //                             <td class="text-center align-middle">
  //                                 <input type="checkbox" class="cb-kompetensi" name="kompeten_${unit.code}_${el.no}" value="BK">
  //                             </td>
  //                             <td class="align-middle px-3">
  //                                 <select class="select2 select-bukti-relevan" multiple="multiple" data-placeholder="Pilih Bukti" style="width: 100%;">
  //                                     </select>
  //                             </td>
  //                         </tr>
  //                     `;
  //         });
  //       }

  //       tableHtml += `</tbody></table></div></div>`;
  //       container.append(tableHtml);
  //     });

  //     // Init ulang select2 di elemen baru
  //     $(".select2").select2({ theme: "bootstrap4" });

  //     // Panggil refresh sekali untuk mengisi jika sudah ada yg diupload sebelumnya
  //     refreshBuktiRelevanDropdown();
  //   }

  //   function renderBuktiKompetensi(units) {
  //     var container = $("#containerBuktiKompetensi");
  //     container.empty();
  //     if (!units || units.length === 0) return;

  //     units.forEach(function (unit, idx) {
  //       var tableHtml = `
  //             <div class="card card-outline card-secondary mb-4 mt-3">
  //                 <div class="card-header"><h3 class="card-title font-weight-bold">Unit Kompetensi Ke-${
  //                   idx + 1
  //                 } <small>${unit.code} - ${unit.title}</small></h3></div>
  //                 <div class="card-body p-0"><table class="table table-bordered table-sm"><thead class="bg-light text-center"><tr><th style="width: 50%">Dapatkah Saya?</th><th style="width: 5%">K</th><th style="width: 5%">BK</th><th style="width: 15%">Bukti Relevan</th></tr></thead><tbody>`;

  //       if (unit.elements) {
  //         unit.elements.forEach(function (el) {
  //           // Render KUK List
  //           var kukListHtml =
  //             '<ul class="pl-3 mb-0" style="list-style-type: none;">';
  //           if (el.kuks) {
  //             // Asumsi el.kuks berisi object {id: 1, name: "Text"} dari API baru
  //             // Jika masih string, Anda perlu update API Java dulu
  //             el.kuks.forEach(function (kuk, kIdx) {
  //               // Jika API mengirim string, kita tidak punya ID KUK.
  //               // *SANGAT PENTING*: Update API Java agar mengirim ID KUK.
  //               // Sementara pakai index simulasi jika terpaksa
  //               var kukId = kuk.id
  //                 ? kuk.id
  //                 : "kuk_" + unit.code + "_" + el.no + "_" + kIdx;
  //               var kukText = kuk.name ? kuk.name : kuk; // Adjust based on API structure

  //               kukListHtml += `<li class="mb-2 text-justify" style=" white-space: pre-wrap; display: block;">${
  //                 el.no
  //               }.${kIdx + 1} ${kukText}</li>`;
  //             });
  //           }
  //           kukListHtml += "</ul>";

  //           // Render Row per Elemen (bukan per KUK, sesuai request layout "Dapatkah Saya" digabung)
  //           // Tapi untuk input radio K/BK dan Bukti, ini harus per KUK atau per Elemen?
  //           // Standar APL-02 biasanya per KUK.
  //           // Tapi di layout Anda, 1 Elemen punya banyak KUK, dan hanya 1 baris K/BK.
  //           // Ini berarti penilaiannya per Elemen?
  //           // Jika ya:
  //           tableHtml += `
  //             <tr>
  //                 <td class="align-top py-3 px-2">
  //                     <div class="font-weight-bold">${el.no}. Elemen: ${el.name}</div>
  //                     <small class="text-muted ml-3">Kriteria Unjuk Kerja:</small>
  //                     ${kukListHtml}
  //                 </td>
  //                 <td class="text-center align-middle"><input type="checkbox" class="cb-kompetensi" name="kompeten_elemen_${unit.code}_${el.no}" value="K"></td>
  //                 <td class="text-center align-middle"><input type="checkbox" class="cb-kompetensi" name="kompeten_elemen_${unit.code}_${el.no}" value="BK"></td>
  //                 <td class="align-middle px-3">
  //                     <select class="select2 select-bukti-relevan" multiple="multiple" name="bukti_elemen_${unit.code}_${el.no}" style="width: 100%;"></select>
  //                 </td>
  //             </tr>`;
  //         });
  //       }
  //       tableHtml += `</tbody></table></div></div>`;
  //       container.append(tableHtml);
  //     });
  //     $(".select2").select2({ theme: "bootstrap4" });
  //     refreshBuktiRelevanDropdown();
  //   }

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
              kukListHtml += `<li class="mb-2 ml-5 text-justify" style="white-space: pre-wrap; display: block;">${kuk}</li>`;
            });
          }
          kukListHtml += "</ul>";
          var elemenHeader = `<div class="font-weight-bold mb-2 ml-0">${el.no}. Elemen: ${el.name}</div>`;

          // GUNAKAN ID ELEMEN (el.id) PADA ATRIBUT NAME
          // Format baru: kompeten_elemen_{ID}
          tableHtml += `
                        <tr>
                            <td class="align-top py-3 px-2">
                                ${elemenHeader}
                                <small class="text-muted ml-3">Kriteria Unjuk Kerja:</small>
                                ${kukListHtml}
                            </td>
                            <td class="text-center align-middle">
                                <input type="checkbox" class="cb-kompetensi" name="kompeten_elemen_${el.id}" value="K">
                            </td>
                            <td class="text-center align-middle">
                                <input type="checkbox" class="cb-kompetensi" name="kompeten_elemen_${el.id}" value="BK">
                            </td>
                            <td class="align-middle px-3">
                                <select class="form-control select2 select-bukti-relevan" 
                                        name="bukti_elemen_${el.id}" 
                                        multiple="multiple" 
                                        data-placeholder="Pilih Bukti" 
                                        style="width: 100%;">
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

  // Helper Tab 3 (Persyaratan - Tetap Sama)
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
  // 8. FINAL SUBMIT (MENGUMPULKAN SEMUA DATA)
  // ============================================
  $("#form-pengajuan-skema").on("submit", function (e) {
    e.preventDefault();

    // Validasi HTML5
    if (!this.checkValidity()) {
      e.stopPropagation();
      $(this).addClass("was-validated");
      // Swal Error
      return;
    }

    var formData = new FormData(this);

    // Append Files dari Memory
    for (var key in fileStore) {
      formData.append(key, fileStore[key]);
    }

    // Append Data APL-02 (Bukti Relevan)
    // Karena select2 multiple tidak otomatis terkirim rapi dalam satu key mapping
    $(".select-bukti-relevan").each(function () {
      // Kita perlu tau ini bukti untuk KUK yang mana.
      // Tadi di renderBuktiKompetensi kita belum kasih 'name' yang unik.
      // Mari kita perbaiki di fungsi renderBuktiKompetensi di bawah.
    });

    $.ajax({
      url: $(this).attr("action"),
      type: "POST",
      data: formData,
      processData: false,
      contentType: false,
      success: function (response) {
        var res = JSON.parse(response);
        // Swal Success -> Redirect
        window.location.href = "/asesi/daftar-sertifikasi";
      },
      error: function (xhr) {
        // Swal Error
        console.log(xhr.responseText);
      },
    });
  });
});

// $(document).ready(function () {
//   // Inisialisasi Select2
//   $(".select2").select2({ theme: "bootstrap4" });

//   var fileStore = {};
//   var uploadedPortofolio = [];

//   // ============================================
//   // 1. PENGAJUAN SKEMA
//   // ============================================

//   // A. Saat Skema Dipilih -> Load Jadwal
//   $("#selectSkema2").on("change", function () {
//     var skemaId = $(this).val();

//     var $jadwal = $("#selectJadwal");
//     $jadwal.empty().append('<option value="">-- Memuat Jadwal... --</option>');

//     $("#inputSumberAnggaran").val("");
//     $("#inputPemberiAnggaran").val("");
//     $("#wrapperBtnReset").hide();

//     $.ajax({
//       url: "/api/asesi/jadwal-by-skema/" + skemaId,
//       type: "GET",
//       success: function (data) {
//         $jadwal.empty();
//         if (data.length > 0) {
//           $jadwal.append('<option value="">-- Pilih Jadwal --</option>');
//           data.forEach(function (item) {
//             $jadwal.append(
//               '<option value="' + item.id + '">' + item.text + "</option>"
//             );
//           });
//           // Kita tidak perlu memanipulasi prop('disabled') karena di HTML sudah dihilangkan
//         } else {
//           $jadwal.append('<option value="">Tidak ada jadwal tersedia</option>');
//         }
//       },
//       error: function () {
//         $jadwal.empty().append('<option value="">Gagal memuat jadwal</option>');
//       },
//     });

//     // Load Detail Skema (Unit, KUK, dll)
//     $.ajax({
//       url: "/api/asesi/skema-detail/" + skemaId,
//       type: "GET",
//       success: function (res) {
//         renderPersyaratan(res.requirements);
//         renderTujuanAsesmen(res.units, res.namaSkema);
//         renderBuktiKompetensi(res.units);
//       },
//     });
//   });

//   // B. Saat Jadwal Dipilih -> Kunci Skema
//   $("#selectJadwal").on("change", function () {
//     var jadwalId = $(this).val();
//     if (jadwalId) {
//       $.get("/api/asesi/jadwal-detail/" + jadwalId, function (res) {
//         $("#inputSumberAnggaran").val(res.sumberAnggaran);
//         $("#inputPemberiAnggaran").val(res.pemberiAnggaran);
//       });
//       // Skema di-disable agar user tidak ganti skema sembarangan setelah pilih jadwal
//       // TAPI: Saat submit form, disabled field tidak terkirim.
//       // TRICK: Kita biarkan disabled visual, tapi saat submit kita enable sebentar atau gunakan hidden input.
//       // Cara paling aman untuk UX ini:
//       $("#selectSkema2").prop("disabled", true);
//       $("#wrapperBtnReset").show();

//       // Tambahkan input hidden untuk skemaId agar tetap terkirim meski select disabled
//       if ($("#hiddenSkemaId").length === 0) {
//         $("<input>")
//           .attr({
//             type: "hidden",
//             id: "hiddenSkemaId",
//             name: "skemaId",
//             value: $("#selectSkema2").val(),
//           })
//           .appendTo("#form-pengajuan-skema");
//       } else {
//         $("#hiddenSkemaId").val($("#selectSkema2").val());
//       }
//     }
//   });

//   // C. Tombol Reset Jadwal
//   $("#btnResetJadwal").on("click", function () {
//     $("#selectJadwal").val("").trigger("change");
//     $("#inputSumberAnggaran").val("");
//     $("#inputPemberiAnggaran").val("");
//     $("#wrapperBtnReset").hide();

//     // Buka kunci skema & Hapus hidden input
//     $("#selectSkema2").prop("disabled", false);
//     $("#hiddenSkemaId").remove();
//   });

//   // ============================================
//   // 2. DATA PEMOHON (LOGIKA PEKERJAAN)
//   // ============================================

//   function toggleJobDetails() {
//     var jobTypeId = String($("#selectPekerjaan").val());
//     var $section = $("#jobDetailsSection");
//     var $inputs = $section.find("input, textarea");

//     // ID '1' = Tidak Bekerja / Belum Bekerja (Sesuaikan dengan DB Anda)
//     // Jika value kosong atau ID=1
//     if (!jobTypeId || jobTypeId === "1") {
//       // KONDISI: TIDAK BEKERJA
//       $section.slideUp();

//       // 1. Hapus Required
//       $inputs.prop("required", false);

//       // 2. KOSONGKAN NILAI (Agar bersih saat dikirim ke DB)
//       $inputs.val("");
//     } else {
//       // KONDISI: BEKERJA
//       $section.slideDown();

//       // 1. Reset required (semua optional dulu)
//       $inputs.prop("required", false);

//       // 2. Wajib isi Nama Perusahaan saja
//       $section.find('input[name="companyName"]').prop("required", true);
//     }
//   }

//   // Jalankan saat load & change
//   setTimeout(toggleJobDetails, 200); // Delay dikit biar select2 ready
//   $("#selectPekerjaan").on("change", toggleJobDetails);

//   // ============================================
//   // 3. MODAL UPLOAD & SUBMIT
//   // ============================================

//   // ... (Kode Modal Upload Anda sebelumnya sudah benar, pertahankan) ...
//   // ... Pastikan tombol Simpan menggunakan URL.createObjectURL ...

//   $(document).on("click", ".btn-upload-modal", function (e) {
//     e.preventDefault();
//     var targetId = $(this).data("id");
//     var type = $(this).data("type");
//     var label = $(this).closest("tr").find("td:eq(1)").text().trim();

//     $("#uploadModalLabel").text("Upload " + label);
//     $("#uploadTargetId").val(targetId);
//     $("#uploadType").val(type);
//     $("#fileInput").val("").next(".custom-file-label").html("Pilih file"); // Reset
//     $("#uploadModal").modal("show");
//   });

//   $("#btnSimpanUpload").on("click", function () {
//     var fileInput = $("#fileInput")[0];
//     if (fileInput.files.length === 0) {
//       // alert / swal warning
//       return;
//     }
//     var file = fileInput.files[0];
//     var targetId = $("#uploadTargetId").val();
//     var type = $("#uploadType").val();

//     // Simpan ke memory
//     fileStore[targetId] = file;

//     // Preview Link
//     var blobUrl = URL.createObjectURL(file);

//     // Update Button UI
//     var btnHtml = `
//         <div class="action-buttons">
//             <a href="${blobUrl}" target="_blank" class="btn btn-sm btn-info" title="Lihat File"><i class="fas fa-eye"></i></a>
//             <button type="button" class="btn btn-sm btn-danger btn-delete-upload" data-id="${targetId}" data-type="${type}"><i class="fas fa-trash"></i></button>
//         </div>
//         <input type="hidden" name="upload_check_${targetId}" value="1">
//     `;
//     // input hidden upload_check_... bisa dipakai utk validasi backend kalau mau

//     $('.btn-upload-modal[data-id="' + targetId + '"]')
//       .parent()
//       .html(btnHtml);

//     // Update Dropdown Portofolio
//     if (type === "portofolio") {
//       var docName = $("#uploadModalLabel").text().replace("Upload ", "");
//       uploadedPortofolio.push({ id: targetId, text: docName });
//       refreshBuktiRelevanDropdown();
//     }
//     $("#uploadModal").modal("hide");
//   });

//   // ============================================
//   // 4. SUBMIT FORM UTAMA
//   // ============================================
//   $("#form-pengajuan-skema").on("submit", function (e) {
//     e.preventDefault();

//     // Validasi HTML5
//     if (!this.checkValidity()) {
//       e.stopPropagation();
//       $(this).addClass("was-validated");
//       // Swal Error
//       return;
//     }

//     var formData = new FormData(this);

//     // Append Files dari Memory
//     for (var key in fileStore) {
//       formData.append(key, fileStore[key]);
//     }

//     // Append Data APL-02 (Bukti Relevan)
//     // Karena select2 multiple tidak otomatis terkirim rapi dalam satu key mapping
//     $(".select-bukti-relevan").each(function () {
//       // Kita perlu tau ini bukti untuk KUK yang mana.
//       // Tadi di renderBuktiKompetensi kita belum kasih 'name' yang unik.
//       // Mari kita perbaiki di fungsi renderBuktiKompetensi di bawah.
//     });

//     $.ajax({
//       url: $(this).attr("action"),
//       type: "POST",
//       data: formData,
//       processData: false,
//       contentType: false,
//       success: function (response) {
//         var res = JSON.parse(response);
//         // Swal Success -> Redirect
//         window.location.href = "/asesi/daftar-sertifikasi";
//       },
//       error: function (xhr) {
//         // Swal Error
//         console.log(xhr.responseText);
//       },
//     });
//   });

//   // ... (Fungsi Render Lainnya tetap sama) ...

//   // UPDATE PENTING DI RENDER BUKTI KOMPETENSI:
//   // Tambahkan attribut name pada select bukti agar terkirim ke server
//   function renderBuktiKompetensi(units) {
//     // ... loop units ...
//     // ... loop elements ...
//     // ... loop kuks ...
//     // Di bagian HTML Table Row KUK:
//     /*
//       <select class="select2 select-bukti-relevan"
//               multiple="multiple"
//               name="bukti_${kuk.id}"  <-- PASTIKAN INI ADA (ID KUK DARI DB)
//               data-placeholder="Pilih Bukti"
//               style="width: 100%;">
//       </select>
//       */
//     // Pastikan API backend /skema-detail mengirimkan ID KUK juga, bukan cuma namanya.
//   }

//   // Fungsi Render Bukti Kompetensi yang lengkap (revisi)
//   function renderBuktiKompetensi(units) {
//     var container = $("#containerBuktiKompetensi");
//     container.empty();
//     if (!units || units.length === 0) return;

//     units.forEach(function (unit, idx) {
//       var tableHtml = `
//             <div class="card card-outline card-secondary mb-4 mt-3">
//                 <div class="card-header"><h3 class="card-title font-weight-bold">Unit Kompetensi Ke-${
//                   idx + 1
//                 } <small>${unit.code} - ${unit.title}</small></h3></div>
//                 <div class="card-body p-0"><table class="table table-bordered table-sm"><thead class="bg-light text-center"><tr><th style="width: 50%">Dapatkah Saya?</th><th style="width: 5%">K</th><th style="width: 5%">BK</th><th style="width: 15%">Bukti Relevan</th></tr></thead><tbody>`;

//       if (unit.elements) {
//         unit.elements.forEach(function (el) {
//           // Render KUK List
//           var kukListHtml =
//             '<ul class="pl-3 mb-0" style="list-style-type: none;">';
//           if (el.kuks) {
//             // Asumsi el.kuks berisi object {id: 1, name: "Text"} dari API baru
//             // Jika masih string, Anda perlu update API Java dulu
//             el.kuks.forEach(function (kuk, kIdx) {
//               // Jika API mengirim string, kita tidak punya ID KUK.
//               // *SANGAT PENTING*: Update API Java agar mengirim ID KUK.
//               // Sementara pakai index simulasi jika terpaksa
//               var kukId = kuk.id
//                 ? kuk.id
//                 : "kuk_" + unit.code + "_" + el.no + "_" + kIdx;
//               var kukText = kuk.name ? kuk.name : kuk; // Adjust based on API structure

//               kukListHtml += `<li class="mb-2 text-justify">${el.no}.${
//                 kIdx + 1
//               } ${kukText}</li>`;
//             });
//           }
//           kukListHtml += "</ul>";

//           // Render Row per Elemen (bukan per KUK, sesuai request layout "Dapatkah Saya" digabung)
//           // Tapi untuk input radio K/BK dan Bukti, ini harus per KUK atau per Elemen?
//           // Standar APL-02 biasanya per KUK.
//           // Tapi di layout Anda, 1 Elemen punya banyak KUK, dan hanya 1 baris K/BK.
//           // Ini berarti penilaiannya per Elemen?
//           // Jika ya:
//           tableHtml += `
//             <tr>
//                 <td class="align-top py-3 px-2">
//                     <div class="font-weight-bold">${el.no}. Elemen: ${el.name}</div>
//                     <small class="text-muted ml-3">Kriteria Unjuk Kerja:</small>
//                     ${kukListHtml}
//                 </td>
//                 <td class="text-center align-middle"><input type="checkbox" class="cb-kompetensi" name="kompeten_elemen_${unit.code}_${el.no}" value="K"></td>
//                 <td class="text-center align-middle"><input type="checkbox" class="cb-kompetensi" name="kompeten_elemen_${unit.code}_${el.no}" value="BK"></td>
//                 <td class="align-middle px-3">
//                     <select class="select2 select-bukti-relevan" multiple="multiple" name="bukti_elemen_${unit.code}_${el.no}" style="width: 100%;"></select>
//                 </td>
//             </tr>`;
//         });
//       }
//       tableHtml += `</tbody></table></div></div>`;
//       container.append(tableHtml);
//     });
//     $(".select2").select2({ theme: "bootstrap4" });
//     refreshBuktiRelevanDropdown();
//   }
// });
