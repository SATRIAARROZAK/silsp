// $(document).ready(function () {
//   // Inisialisasi Select2
//   $(".select2").select2({ theme: "bootstrap4" });
//   var fileStore = {}; // Tambahkan ini di paling atas $(document).ready

//   // Simpan data bukti yang sudah diupload (untuk Tab 7)
//   var uploadedPortofolio = [];

//   // ============================================
//   // 1. PENGAJUAN SKEMA (FIXED: SELECT SKEMA TIDAK DISABLE)
//   // ============================================

//   // A. Saat Skema Dipilih -> Load Jadwal
//   $("#selectSkema2").on("change", function () {
//     var skemaId = $(this).val();

//     // Reset Jadwal & Anggaran
//     var $jadwal = $("#selectJadwal");
//     $jadwal.empty().append('<option value="">-- Memuat Jadwal... --</option>');

//     // Kosongkan form anggaran & sembunyikan tombol reset
//     $("#inputSumberAnggaran").val("");
//     $("#inputPemberiAnggaran").val("");
//     $("#wrapperBtnReset").hide();

//     // API Call Jadwal
//     $.ajax({
//       url: "/api/jadwal-by-skema/" + skemaId,
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
//           $jadwal.prop("disabled", false);
//         } else {
//           $jadwal.append('<option value="">Tidak ada jadwal tersedia</option>');
//           $jadwal.prop("disabled", true);
//         }
//       },
//       error: function () {
//         $jadwal.empty().append('<option value="">Gagal memuat jadwal</option>');
//       },
//     });

//     // API Call Detail Skema
//     $.ajax({
//       url: "/api/skema-detail/" + skemaId,
//       type: "GET",
//       success: function (res) {
//         renderPersyaratan(res.requirements);
//         renderTujuanAsesmen(res.units, res.namaSkema);
//         renderBuktiKompetensi(res.units);
//       },
//     });
//   });

//   // B. Saat Jadwal Dipilih -> KUNCI SKEMA & Tampilkan Tombol X
//   $("#selectJadwal").on("change", function () {
//     var jadwalId = $(this).val();

//     if (jadwalId) {
//       // Isi Anggaran
//       $.get("/api/jadwal-detail/" + jadwalId, function (res) {
//         $("#inputSumberAnggaran").val(res.sumberAnggaran);
//         $("#inputPemberiAnggaran").val(res.pemberiAnggaran);
//       });

//       // Kunci Skema
//       $("#selectSkema2").prop("disabled", true);
//       // Tampilkan Tombol Reset
//       $("#wrapperBtnReset").show();
//     }
//   });

//   // C. Saat Tombol Silang (X) Diklik -> RESET
//   $("#btnResetJadwal").on("click", function () {
//     // 1. Reset Select2 Jadwal
//     $("#selectJadwal").val("").trigger("change");

//     // 2. Kosongkan Form Anggaran
//     $("#inputSumberAnggaran").val("");
//     $("#inputPemberiAnggaran").val("");

//     // 3. Sembunyikan Tombol X
//     $("#wrapperBtnReset").hide();

//     // 4. Buka Kunci Skema (ENABLE)
//     // Option di dalam selectSkema2 TIDAK AKAN HILANG karena tidak di-init ulang
//     $("#selectSkema2").prop("disabled", false);
//   });

//   // ============================================
//   // 2. DATA PEMOHON (LOGIKA PEKERJAAN & CLEAR DATA)
//   // ============================================
//   function toggleJobDetails() {
//     // Ambil value dropdown pekerjaan
//     var jobTypeId = String($("#selectPekerjaan").val());

//     // Ambil elemen container dan semua input didalamnya
//     var $section = $("#jobDetailsSection");
//     var $inputs = $section.find("input, textarea");

//     // Asumsi: ID '1' adalah 'Tidak Bekerja' di database TypePekerjaan
//     // Sesuaikan ID ini jika di database Anda berbeda
//     if (jobTypeId && jobTypeId !== "1") {
//       // KONDISI: BEKERJA
//       $section.slideDown();

//       // 1. Reset required semua dulu
//       $inputs.prop("required", false);

//       // 2. Set HANYA Company Name yang wajib
//       $section.find('input[name="companyName"]').prop("required", true);
//     } else {
//       // KONDISI: TIDAK BEKERJA / BELUM PILIH
//       $section.slideUp();

//       // 1. Hapus validasi required
//       $inputs.prop("required", false);

//       // 2. BERSIHKAN DATA (PENTING!)
//       // Ini memastikan data yang sempat diisi jadi kosong lagi agar tidak terkirim ke DB
//       $inputs.val("");
//     }
//   }

//   // PENTING: Jalankan saat halaman selesai dimuat (untuk handle data edit/validasi error)
//   setTimeout(function () {
//     toggleJobDetails();
//   }, 100);

//   // Jalankan setiap kali dropdown berubah
//   $("#selectPekerjaan").on("change", toggleJobDetails);

//   // ============================================
//   // 3, 4, 5. MODAL UPLOAD FILE
//   // ============================================

//   $(document).on("click", ".btn-upload-modal", function (e) {
//     e.preventDefault();
//     var targetId = $(this).data("id");
//     var type = $(this).data("type");
//     var label = $(this).closest("tr").find("td:eq(1)").text().trim();

//     $("#uploadModalLabel").text("Upload " + label);
//     $("#uploadTargetId").val(targetId);
//     $("#uploadType").val(type);
//     $("#uploadModal").modal("show");
//   });

//   // PERBAIKAN: Fungsi Tombol Batal & Close (X) secara Manual
//   $("#btnBatalUpload, #uploadModal .close").on("click", function () {
//     $("#uploadModal").modal("hide");
//     // Reset input file agar saat dibuka lagi bersih
//     $("#fileInput").val("");
//     $(".custom-file-label").text("Pilih file");
//   });

//   // Handle Simpan Upload
//   $("#btnSimpanUpload").on("click", function () {
//     var fileInput = $("#fileInput")[0];

//     // Validasi File Kosong
//     if (fileInput.files.length === 0) {
//       Swal.fire("Error", "Pilih file terlebih dahulu!", "error");
//       return;
//     }

//     var file = fileInput.files[0];
//     var targetId = $("#uploadTargetId").val();
//     var type = $("#uploadType").val();

//     // SIMPAN FILE KE MEMORI
//     fileStore[targetId] = file;

//     // --- PERBAIKAN 2: MEMBUAT URL PREVIEW DARI FILE LOKAL ---
//     var blobUrl = URL.createObjectURL(file);

//     // Update UI Tombol -> Jadi "Lihat" (href ke blobUrl) dan "Hapus"
//     var btnHtml = `
//             <div class="action-buttons">
//                 <a href="${blobUrl}" target="_blank" class="btn btn-sm btn-outline-info" title="Lihat File">
//                     <i class="fas fa-file"></i>
//                 </a>
//                 <button type="button" class="btn btn-sm btn-outline-danger btn-delete-upload" data-id="${targetId}" data-type="${type}">
//                     <i class="fas fa-trash"></i>
//                 </button>
//             </div>
//             <input type="hidden" name="upload_status_${targetId}" value="1">
//         `;

//     // Cari tombol asal dan ganti dengan tombol baru
//     $('.btn-upload-modal[data-id="' + targetId + '"]')
//       .parent()
//       .html(btnHtml);

//     // LOGIKA TAB 7: Tambahkan ke list Bukti Relevan jika tipe Portofolio
//     if (type === "portofolio") {
//       var docName = $("#uploadModalLabel").text().replace("Upload ", "");
//       uploadedPortofolio.push({ id: targetId, text: docName });
//       refreshBuktiRelevanDropdown();
//     }

//     $("#uploadModal").modal("hide");
//     $("#fileInput").val(""); // Reset input file
//     $(".custom-file-label").text("Pilih file");
//   });

//   // Handle Hapus Upload

//   $(document).on("click", ".btn-delete-upload", function () {
//     var targetId = $(this).data("id");
//     var type = $(this).data("type");
//     var container = $(this).closest("td");

//     // Kembalikan tombol upload awal
//     var resetHtml = `
//             <div class="action-buttons">
//                     <button class="btn btn-sm btn-outline-primary btn-upload-modal" data-id="${targetId}" data-type="${type}">
//                     <i class="fas fa-upload mr-1"></i> Upload
//                     </button>
//                 </div>
//         `;
//     container.html(resetHtml);

//     delete fileStore[targetId];

//     // Hapus dari list Portofolio jika perlu
//     if (type === "portofolio") {
//       uploadedPortofolio = uploadedPortofolio.filter(function (item) {
//         return item.id !== targetId;
//       });
//       refreshBuktiRelevanDropdown();
//     }
//   });

//   // ============================================
//   // 6. TUJUAN ASESMEN (NAMA SKEMA & RADIO)
//   // ============================================
//   function renderTujuanAsesmen(units, namaSkema) {
//     // Update Judul Skema
//     $("#namaSkemaLabel").text(namaSkema);

//     // Render List Unit
//     var html = "";
//     if (units && units.length > 0) {
//       units.forEach(function (u, idx) {
//         html += `
//                 <tr>
//                     <td class="text-center">${idx + 1}</td>
//                     <td class="font-weight-bold">${u.code}</td>
//                     <td>${u.title}</td>`;
//       });
//     } else {
//       html =
//         '<tr><td colspan="3" class="text-center">Tidak ada unit kompetensi.</td></tr>';
//     }
//     $("#listUnitTujuan").html(html);

//     // Reset Radio Button (Uncheck All)
//     $('input[name="tujuanAsesmen"]').prop("checked", false);
//   }

//   // ============================================
//   // 7. BUKTI KOMPETENSI (CHECKBOX LOGIC & DROPDOWN)
//   // ============================================

//   // Logic Checkbox K vs BK (Mutual Exclusion)
//   $(document).on("change", ".cb-kompetensi", function () {
//     var name = $(this).attr("name"); // kompeten_CODE_NO
//     var val = $(this).val(); // K atau BK

//     if ($(this).is(":checked")) {
//       // Uncheck yang lainnya dalam grup nama yang sama
//       $('input[name="' + name + '"]')
//         .not(this)
//         .prop("checked", false);
//     }
//   });

//   function refreshBuktiRelevanDropdown() {
//     var $selects = $(".select-bukti-relevan");

//     // Simpan value lama agar tidak hilang saat refresh opsi
//     $selects.each(function () {
//       var $sel = $(this);
//       var currentVal = $sel.val();

//       $sel.empty(); // Kosongkan opsi

//       if (uploadedPortofolio.length > 0) {
//         // Tambahkan opsi dari array portofolio yg sudah diupload
//         uploadedPortofolio.forEach(function (item) {
//           var isSelected =
//             currentVal && currentVal.includes(item.id) ? "selected" : "";
//           $sel.append(
//             `<option value="${item.id}" ${isSelected}>${item.text}</option>`
//           );
//         });
//       } else {
//         // Tidak ada opsi
//         // Select2 placeholder akan muncul otomatis jika kosong
//       }

//       // Trigger change untuk update tampilan select2
//       $sel.trigger("change");
//     });
//   }

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

//   // Helper Tab 3 (Persyaratan - Tetap Sama)
//   function renderPersyaratan(reqs) {
//     var html = "";
//     if (reqs && reqs.length > 0) {
//       reqs.forEach(function (r, idx) {
//         html += `
//             <tr>
//                 <td class="text-center">${idx + 1}</td>
//                <td>${r} <input type="hidden" name="nama_syarat_${idx}" value="${r}"></td>
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

//   // ============================================
//   // SUBMIT SEMUA DATA (FULL STACK)
//   // ============================================
// });



$(document).ready(function () {
  $(".select2").select2({ theme: "bootstrap4" });

  // Store uploaded files temporarily (File Object & Metadata)
  // Structure: { id: "unique_id", type: "jenis", label: "nama_dokumen", file: FileObject }
  var uploadQueue = [];

  // ============================================
  // 1. LOGIKA PILIH SKEMA (Enable & Dinamis)
  // ============================================
  $("#selectSkema2").on("change", function () {
    var skemaId = $(this).val();
    var $jadwal = $("#selectJadwal");

    // Reset UI Jadwal & Anggaran
    $jadwal.empty().append('<option value="">-- Memuat Jadwal... --</option>');
    $("#inputSumberAnggaran").val("");
    $("#inputPemberiAnggaran").val("");
    $("#wrapperBtnReset").hide();

    // Fetch Jadwal
    $.ajax({
      url: "/api/jadwal-by-skema/" + skemaId,
      type: "GET",
      success: function (data) {
        $jadwal.empty();
        if (data.length > 0) {
          $jadwal.append('<option value="">-- Pilih Jadwal --</option>');
          data.forEach(function (item) {
            $jadwal.append(`<option value="${item.id}">${item.text}</option>`);
          });
          $jadwal.prop("disabled", false);
        } else {
          $jadwal.append('<option value="">Tidak ada jadwal tersedia</option>');
          $jadwal.prop("disabled", true);
        }
      },
      error: function () {
        $jadwal.empty().append('<option value="">Gagal memuat</option>');
      }
    });

    // Fetch Detail Skema
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

  // Saat Jadwal Dipilih -> Isi Anggaran
  $("#selectJadwal").on("change", function () {
    var jadwalId = $(this).val();
    if (jadwalId) {
      $.get("/api/jadwal-detail/" + jadwalId, function (res) {
        $("#inputSumberAnggaran").val(res.sumberAnggaran);
        $("#inputPemberiAnggaran").val(res.pemberiAnggaran);
      });
      // Kita TIDAK disable skema, user bebas ganti.
    }
  });

  // ============================================
  // 2. DATA PEMOHON (Logic Toggle)
  // ============================================
  function toggleJobDetails() {
    var jobTypeId = String($("#selectPekerjaan").val());
    var $section = $("#jobDetailsSection"); // Sesuai ID di HTML baru
    var $inputs = $section.find("input, textarea");

    // ID '1' = Tidak Bekerja (Sesuaikan dengan DB Anda)
    if (jobTypeId && jobTypeId !== "1") {
      $section.slideDown();
      $inputs.prop("required", false); // Reset dulu
      $section.find('input[name="companyName"]').prop("required", true); // Wajib cuma nama PT
    } else {
      $section.slideUp();
      $inputs.prop("required", false);
      $inputs.val(""); // BERSIHKAN DATA (Null di DB)
    }
  }

  setTimeout(toggleJobDetails, 100);
  $("#selectPekerjaan").on("change", toggleJobDetails);

  // ============================================
  // 3, 4, 5. MODAL UPLOAD FILE
  // ============================================
  $(document).on("click", ".btn-upload-modal", function (e) {
    e.preventDefault();
    var targetId = $(this).data("id"); // e.g., portofolio_1
    var type = $(this).data("type");
    var label = $(this).closest("tr").find("td:eq(1)").text().trim();

    $("#uploadModalLabel").text("Upload " + label);
    $("#uploadTargetId").val(targetId);
    $("#uploadType").val(type);
    $("#uploadModal").modal("show");
  });

  // Tombol Batal & Close
  $("#btnBatalUpload, #uploadModal .close").on("click", function () {
    $("#uploadModal").modal("hide");
    $("#fileInput").val("");
    $(".custom-file-label").text("Pilih file");
  });

  // Tombol Simpan (Preview + Masuk Queue)
  $("#btnSimpanUpload").on("click", function () {
    var fileInput = $("#fileInput")[0];
    if (fileInput.files.length === 0) {
      Swal.fire("Error", "Pilih file terlebih dahulu!", "error");
      return;
    }

    var file = fileInput.files[0];
    var targetId = $("#uploadTargetId").val();
    var type = $("#uploadType").val();
    var label = $("#uploadModalLabel").text().replace("Upload ", "");

    // 1. Simpan ke Queue Global
    // Hapus entry lama jika re-upload
    uploadQueue = uploadQueue.filter(u => u.id !== targetId);
    uploadQueue.push({
        id: targetId,
        type: type, // 'persyaratan', 'administrasi', 'portofolio'
        label: label,
        file: file
    });

    // 2. Update UI Preview
    var blobUrl = URL.createObjectURL(file);
    var btnHtml = `
            <div class="action-buttons">
                <a href="${blobUrl}" target="_blank" class="btn btn-sm btn-info" title="Lihat File"><i class="fas fa-eye"></i></a>
                <button type="button" class="btn btn-sm btn-danger btn-delete-upload" data-id="${targetId}" data-type="${type}"><i class="fas fa-trash"></i></button>
            </div>`;
    
    $('.btn-upload-modal[data-id="' + targetId + '"]').parent().html(btnHtml);

    // 3. Update Dropdown Tab 7 jika Portofolio
    if (type === "portofolio") refreshBuktiRelevanDropdown();

    $("#uploadModal").modal("hide");
    $("#fileInput").val("");
    $(".custom-file-label").text("Pilih file");
  });

  // Hapus Upload
  $(document).on("click", ".btn-delete-upload", function () {
    var targetId = $(this).data("id");
    var type = $(this).data("type");
    
    // Hapus dari Queue
    uploadQueue = uploadQueue.filter(u => u.id !== targetId);

    // Reset UI
    var container = $(this).closest("td");
    var resetHtml = `
            <div class="action-buttons">
                <button class="btn btn-sm btn-outline-primary btn-upload-modal" data-id="${targetId}" data-type="${type}">
                    <i class="fas fa-upload mr-1"></i> Upload
                </button>
            </div>`;
    container.html(resetHtml);

    if (type === "portofolio") refreshBuktiRelevanDropdown();
  });

  // ============================================
  // 6. TUJUAN ASESMEN
  // ============================================
  function renderTujuanAsesmen(units, namaSkema) {
    $("#namaSkemaLabel").text(namaSkema);
    var html = "";
    if (units && units.length > 0) {
      units.forEach((u, idx) => {
        html += `<tr><td class="text-center">${idx + 1}</td><td class="font-weight-bold">${u.code}</td><td>${u.title}</td></tr>`;
      });
    } else {
      html = '<tr><td colspan="3" class="text-center">Tidak ada unit.</td></tr>';
    }
    $("#listUnitTujuan").html(html);
    $('input[name="tujuanAsesmen"]').prop("checked", false);
  }

  // ============================================
  // 7. BUKTI KOMPETENSI (APL-02)
  // ============================================
  $(document).on("change", ".cb-kompetensi", function () {
    var name = $(this).attr("name");
    if ($(this).is(":checked")) {
      $('input[name="' + name + '"]').not(this).prop("checked", false);
    }
  });

  function refreshBuktiRelevanDropdown() {
    var $selects = $(".select-bukti-relevan");
    $selects.each(function () {
      var $sel = $(this);
      var currentVal = $sel.val();
      $sel.empty();

      // Filter hanya tipe portofolio dari queue
      var portofolios = uploadQueue.filter(u => u.type === 'portofolio');

      if (portofolios.length > 0) {
        portofolios.forEach(function (item) {
          var isSelected = currentVal && currentVal.includes(item.id) ? "selected" : "";
          $sel.append(`<option value="${item.id}" ${isSelected}>${item.label}</option>`);
        });
      }
      $sel.trigger("change");
    });
  }

  function renderBuktiKompetensi(units) {
    var container = $("#containerBuktiKompetensi");
    container.empty();
    if (!units || units.length === 0) return;

    units.forEach(function (unit, idx) {
      var tableHtml = `
            <div class="card card-outline card-secondary mb-4 mt-3">
                <div class="card-header"><h3 class="card-title font-weight-bold">Unit Kompetensi Ke-${idx + 1}<br><small>${unit.code} - ${unit.title}</small></h3></div>
                <div class="card-body p-0">
                    <table class="table table-bordered table-sm">
                        <thead class="bg-light text-center">
                            <tr><th width="50%">Dapatkah Saya?</th><th width="5%">K</th><th width="5%">BK</th><th width="40%">Bukti Relevan</th></tr>
                        </thead>
                        <tbody>`;
      
      if (unit.elements) {
        unit.elements.forEach(function (el) {
          var kukListHtml = '<ul class="pl-3 mb-0" style="list-style-type: none;">';
          if (el.kuks) {
            el.kuks.forEach((kuk) => {
              kukListHtml += `<li class="mb-2 ml-4 text-justify" style="white-space: pre-wrap; display: block;">${kuk}</li>`;
            });
          }
          kukListHtml += "</ul>";
          
          // Data attributes untuk memudahkan pengambilan data saat submit
          tableHtml += `
            <tr class="row-asesmen" data-unit="${unit.code}" data-elemen="${el.no}">
                <td class="align-top py-3 px-2">
                    <div class="font-weight-bold mb-2">${el.no}. Elemen: ${el.name}</div>
                    <small class="text-muted ml-3">Kriteria Unjuk Kerja:</small>
                    ${kukListHtml}
                </td>
                <td class="text-center align-middle"><input type="checkbox" class="cb-kompetensi" name="kompeten_${unit.code}_${el.no}" value="K"></td>
                <td class="text-center align-middle"><input type="checkbox" class="cb-kompetensi" name="kompeten_${unit.code}_${el.no}" value="BK"></td>
                <td class="align-middle px-3">
                    <select class="form-control select2 select-bukti-relevan" multiple="multiple" data-placeholder="Pilih Bukti" style="width: 100%;"></select>
                </td>
            </tr>`;
        });
      }
      tableHtml += `</tbody></table></div></div>`;
      container.append(tableHtml);
    });
    $(".select2").select2({ theme: "bootstrap4", placeholder: "Pilih Bukti" });
    refreshBuktiRelevanDropdown();
  }

  function renderPersyaratan(reqs) {
    var html = "";
    if (reqs && reqs.length > 0) {
      reqs.forEach((r, idx) => {
        html += `
            <tr>
                <td class="text-center">${idx + 1}</td>
                <td>${r}</td>
                <td class="text-center align-middle">
                     <div class="action-buttons">
                        <button class="btn btn-sm btn-outline-primary btn-upload-modal" data-id="syarat_${idx}" data-type="syarat"><i class="fas fa-upload mr-1"></i> Upload</button>
                    </div>
                </td>
            </tr>`;
      });
    } else {
        html = '<tr><td colspan="3">Tidak ada persyaratan.</td></tr>';
    }
    $("#tablePersyaratanBody").html(html);
  }


  // ============================================
  // FINAL SUBMIT (KIRIM SEMUA DATA)
  // ============================================
  $("#form-pengajuan-skema").on("submit", function (e) {
    e.preventDefault();

    Swal.fire({
        title: "Sedang mengirim...",
        didOpen: () => Swal.showLoading()
    });

    var formData = new FormData();

    // 1. CONSTRUCT JSON DATA
    var dataJson = {
        // Tab 1
        skemaId: $("#selectSkema2").val(),
        jadwalId: $("#selectJadwal").val(),
        
        // Tab 2 (Data Pemohon)
        dataPemohon: {
            nik: $("input[name='nik']").val(),
            fullName: $("input[name='fullName']").val(),
            birthPlace: $("input[name='birthPlace']").val(),
            birthDate: $("input[name='birthDate']").val(),
            gender: $("select[name='gender']").val(),
            address: $("textarea[name='address']").val(),
            postalCode: $("input[name='postalCode']").val(),
            email: $("input[name='email']").val(),
            phoneNumber: $("input[name='phoneNumber']").val(),
            educationId: $("#selectPendidikan").val(),
            jobTypeId: $("#selectPekerjaan").val(),
            // Optional Job Details
            companyName: $("input[name='companyName']").val(),
            position: $("input[name='position']").val(),
            officePhone: $("input[name='officePhone']").val(),
            officeEmail: $("input[name='officeEmail']").val(),
            officeFax: $("input[name='officeFax']").val(),
            officeAddress: $("textarea[name='officeAddress']").val()
        },
        
        // Tab 6
        tujuanAsesmen: $("input[name='tujuanAsesmen']:checked").val(),
        
        // Tab 3, 4, 5 (Metadata File)
        listBukti: uploadQueue.map(u => ({
            jenis: u.type.toUpperCase(), // 'SYARAT', 'ADMINISTRASI', 'PORTOFOLIO'
            nama: u.label,
            tempId: u.id
        })),

        // Tab 7 (Asesmen)
        listAsesmen: []
    };

    // Collect Tab 7 Data
    $(".row-asesmen").each(function() {
        var unit = $(this).data("unit");
        var elemen = $(this).data("elemen");
        var status = $(this).find(".cb-kompetensi:checked").val(); // K atau BK
        var buktiRefs = $(this).find(".select-bukti-relevan").val(); // Array IDs

        if(status) {
            dataJson.listAsesmen.push({
                kodeUnit: unit,
                noElemen: String(elemen),
                status: status,
                buktiRefIds: buktiRefs
            });
        }
    });

    // Validasi Sederhana
    if (!dataJson.skemaId || !dataJson.jadwalId) {
        Swal.fire("Gagal", "Pilih Skema dan Jadwal!", "error");
        return;
    }
    
    // 2. APPEND JSON & FILES TO FORMDATA
    formData.append("data", JSON.stringify(dataJson));

    // Append Files (sesuai urutan di uploadQueue)
    uploadQueue.forEach(u => {
        formData.append("files", u.file);
    });

    // 3. AJAX SEND
    $.ajax({
        url: $(this).attr("action"),
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            var res = (typeof response === 'string') ? JSON.parse(response) : response;
            Swal.fire("Berhasil", res.message, "success").then(() => {
                window.location.href = "/asesi/daftar-sertifikasi"; // Redirect
            });
        },
        error: function(xhr) {
            Swal.fire("Gagal", "Terjadi kesalahan saat mengirim data.", "error");
        }
    });
  });

});