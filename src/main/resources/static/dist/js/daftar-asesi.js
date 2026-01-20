$(document).ready(function () {
  $(".select2").select2({ theme: "bootstrap4" });

  // ============================================
  // 1. LOGIKA PILIH SKEMA (TRIGGER TAB 1, 3, 6, 7)
  // ============================================
  $("#selectSkema").on("change", function () {
    var skemaId = $(this).val();

    // A. Fetch Jadwal (Tab 1)
    $.ajax({
      url: "/api/jadwal-by-skema/" + skemaId,
      type: "GET",
      success: function (data) {
        var $jadwal = $("#selectJadwal");
        $jadwal.empty().append('<option value="">-- Pilih Jadwal --</option>');

        if (data.length > 0) {
          $jadwal.prop("disabled", false);
          data.forEach(function (item) {
            $jadwal.append(
              '<option value="' + item.id + '">' + item.text + "</option>"
            );
          });
        } else {
          $jadwal.append('<option value="">Tidak ada jadwal tersedia</option>');
          $jadwal.prop("disabled", true);
        }
        // Reset Anggaran
        $("#inputSumberAnggaran").val("");
        $("#inputPemberiAnggaran").val("");
      },
    });

    // B. Fetch Detail Skema (Untuk Tab 3, 6, 7)
    $.ajax({
      url: "/api/skema-detail/" + skemaId,
      type: "GET",
      success: function (res) {
        renderPersyaratan(res.requirements); // Tab 3
        renderTujuanAsesmen(res.units); // Tab 6
        renderBuktiKompetensi(res.units); // Tab 7
      },
    });
  });

  // ============================================
  // 2. LOGIKA PILIH JADWAL (TRIGGER ANGGARAN)
  // ============================================
  $("#selectJadwal").on("change", function () {
    var jadwalId = $(this).val();
    if (jadwalId) {
      $.get("/api/jadwal-detail/" + jadwalId, function (res) {
        $("#inputSumberAnggaran").val(res.sumberAnggaran);
        $("#inputPemberiAnggaran").val(res.pemberiAnggaran);
      });
    }
  });

  // ============================================
  // 3. LOGIKA DATA PEMOHON (JOB DISABLE)
  // ============================================
  // Cek awal (karena data di-load dari server)
  toggleJobInputs();

  $("#selectStatusPekerjaan").on("change", function () {
    toggleJobInputs();
  });

  function toggleJobInputs() {
    var jobText = $("#selectStatusPekerjaan option:selected")
      .text()
      .toLowerCase();
    var $inputs = $(".job-input");

    // Asumsi "tidak bekerja" atau "belum bekerja" men-disable form
    // Ubah logika sesuai data master Anda
    if (
      jobText.includes("tidak") ||
      jobText.includes("belum") ||
      $("#selectStatusPekerjaan").val() === ""
    ) {
      // Logic: User minta "Disabled tapi terlihat"
      // Namun jika user ingin mengisi, "Wajib memilih jenis pekerjaan"
      // Jadi jika val kosong -> disable. Jika ada isi -> enable.
      if ($("#selectStatusPekerjaan").val() === "") {
        $inputs.prop("disabled", true);
      } else {
        $inputs.prop("disabled", false);
      }
    } else {
      $inputs.prop("disabled", false);
    }
  }

  // ============================================
  // RENDER FUNCTIONS (MEMBUAT HTML DINAMIS)
  // ============================================

  // TAB 3: PERSYARATAN
  function renderPersyaratan(reqs) {
    var html = "";
    if (reqs && reqs.length > 0) {
      reqs.forEach(function (r, idx) {
        html += `
                    <tr>
                        <td class="text-center">${idx + 1}</td>
                        <td>${r}</td> <td class="align-middle">
                                  <div class="action-buttons">
                                    <a
                                      th:href="@{/}"
                                      class="btn btn-sm btn-outline-info"
                                      title="Lihat"
                                    >
                                      <i class="fas fa-upload me-2"
                                        >Upload File</i
                                      >
                                    </a>
                                  </div>
                                </td>
                    </tr>`;
      });
    } else {
      html =
        '<tr><td colspan="3" class="text-center">Tidak ada persyaratan khusus.</td></tr>';
    }
    $("#tablePersyaratanBody").html(html);
  }

  // TAB 6: LIST UNIT
  function renderTujuanAsesmen(units) {
    var html = "";
    if (units && units.length > 0) {
      units.forEach(function (u, idx) {
        html += ` 
         <tr>
          <td class="text-center">${idx + 1}</td>
          <td class="font-weight-bold">${u.code}</td>
          <td> ${u.title}</td>
          </tr>
         
        `;
      });
    } else {
      html =
        '<tr><td colspan="3" class="text-center">Tidak ada skema yang dipilih.</td></tr>';
    }
    $("#listUnitTujuan").html(html);
  }

  // TAB 7: BUKTI KOMPETENSI (APL-02 LAYOUT)
  function renderBuktiKompetensi(units) {
    var container = $("#containerBuktiKompetensi");
    container.empty();

    if (!units || units.length === 0) return;

    units.forEach(function (unit, idx) {
      var tableHtml = `
                <div class="card card-outline card-secondary mb-4 mt-3"> <div class="card-header">
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
                                    <th style="width: 15%" class="align-middle">Bukti Relevan</th>
                                </tr>
                            </thead>
                            <tbody>
                `;

      // Loop Elements
      if (unit.elements) {
        unit.elements.forEach(function (el) {
          // Header Elemen (Merged Row)

          // Loop KUK (Kriteria Unjuk Kerja)
          // KUK digabung dalam satu sel list untuk menghemat ruang, atau per baris?
          // Sesuai layout yang diminta user: "Dapatkah Saya?" berisi list KUK.

          var kukListHtml =
            '<ul class="pl-3 mb-0" style="list-style-type: none; white-space: pre-wrap; display: block;">';
          if (el.kuks) {
            el.kuks.forEach(function (kuk) {
              // Contoh: 1.1 Judul KUK
              kukListHtml += `<li"><span class="font-weight-bold text-left"
                                  > ${el.no}.    Elemen: ${el.name}<br/><small>Kriteria Unit Kerja:</small><br/></span>${kuk}</li>`;
            });
          }
          kukListHtml += "</ul>";

          tableHtml += `
                            <tr>
                                <td class="align-top py-3">${kukListHtml}</td>
                                <td class="text-center align-middle">
                                    <input type="checkbox" name="kompeten_${unit.code}_${el.no}" value="K">
                                </td>
                                <td class="text-center align-middle">
                                    <input type="checkbox" name="kompeten_${unit.code}_${el.no}" value="BK">
                                </td>
                                <td class="align-middle px-3">
                                    <select class="form-control select2" multiple="multiple" data-placeholder="Pilih Bukti" style="width: 100%;">
                                        <option value="ijazah">Ijazah</option>
                                        <option value="sertifikat">Sertifikat Pelatihan</option>
                                        <option value="sk">Surat Keterangan Kerja</option>
                                        <option value="laporan">Laporan Pekerjaan</option>
                                    </select>
                                </td>
                            </tr>
                        `;
        });
      }

      tableHtml += `
                            </tbody>
                        </table>
                    </div>
                </div>`;

      container.append(tableHtml);
    });

    // TAB 7: BUKTI KOMPETENSI (APL-02 LAYOUT)
    //   function renderBuktiKompetensi(units) {
    //     var container = $("#containerBuktiKompetensi");
    //     container.empty();

    //     if (!units || units.length === 0) return;

    //     units.forEach(function (unit, idx) {
    //       var tableHtml = `
    //                 <div class="card card-outline card-secondary mb-4">
    //                     <div class="card-body p-0">
    //                         <table class="table table-bordered table-sm">
    //                           <tr>
    //                               <th rowspan="2" class="text-center align-middle">
    //                                 Unit Kompetensi ${idx + 1}
    //                               </th>
    //                               <th>Kode Unit</th>
    //                               <td colspan="4">${unit.code}</td>
    //                             </tr>

    //                             <tr>
    //                               <th>Judul Unit</th>
    //                               <td colspan="4">
    //                               ${unit.title}
    //                                 Mengidentifikasi Kebutuhan Data untuk Proses
    //                                 Bisnis
    //                               </td>
    //                             </tr>

    //                              <tr>
    //                               <td colspan="3" class="align-middle">
    //                                 <span class="font-weight-bold"
    //                                   >${no}. Elemen: ${el.name} Menentukan bentuk aset data
    //                                   organisasi dan aspek-aspek
    //                                   pengelolaannya</span
    //                                 >
    //                                 <br />

    //                                 <ul>
    //                                   <li>
    //                                     <small>Kriteria Unit Kerja:</small><br />
    //                                      style="
    //                                   white-space: pre-wrap;
    //                                   display: block;
    //                                   text-align: left;
    //                                 "
    //                                      ${kuk}
    //                                     1.1 Latar belakang dan aktivitas organisasi
    //                                     diidentifikasi sesuai dengan fungsi-fungsi
    //                                     organisasi. <br />
    //                                     1.2 Data yang harus dikelola sebagai aset
    //                                     perusahaan diidentifikasi sesuai dengan
    //                                     aktivitas organisasi. <br />
    //                                     1.3 Penanganan aset data ditentukan sesuai
    //                                     dengan data management best practice.
    //                                     <br />
    //                                     1.4 Strategi kelola aset data ditentukan
    //                                     sesuai dengan rencana strategis organisasi.
    //                                     <br />
    //                                     1.5 Peningkatan kualitas proses kelola aset
    //                                     data secara berkesinambungan ditentukan
    //                                     berdasarkan strategi kelola aset data.
    //                                     <br />
    //                                   </li>
    //                                 </ul>
    //                               </td>
    //                               <td class="align-middle text-center">
    //                                 <div class="form-group">
    //                                   <div class="form-check">
    //                                     <input
    //                                       class="form-check-input"
    //                                       type="checkbox"
    //                                     />
    //                                   </div>
    //                                 </div>
    //                               </td>
    //                               <td class="align-middle text-center">
    //                                 <div class="form-group">
    //                                   <div class="form-check">
    //                                     <input
    //                                       class="form-check-input"
    //                                       type="checkbox"
    //                                     />
    //                                   </div>
    //                                 </div>
    //                               </td>
    //                               <td class="align-middle">
    //                                 <select
    //                                   class="select2"
    //                                   multiple="multiple"
    //                                   placeholder="Pilih Bukti"
    //                                   required
    //                                 >
    //                                   <option value="">Laporan Projek</option>
    //                                   <option value="">Sertifikat Pelatihan</option>
    //                                   <option value="">Surat Tugas</option>
    //                                 </select>
    //                               </td>
    //                             </tr>

    //                              <tr class="text-center">
    //                               <th colspan="3" class="text-center align-middle">
    //                                 Dapatkah Saya?
    //                               </th>

    //                               <th style="width: 5%">K</th>
    //                               <th style="width: 5%">BK</th>

    //                               <th style="width: 20%">Bukti Relevan</th>
    //                             </tr>

    //                             <thead class="bg-light text-center">
    //                                 <tr>
    //                                     <th style="width: 50%" class="align-middle">Dapatkah Saya?</th>
    //                                     <th style="width: 5%" class="align-middle">K</th>
    //                                     <th style="width: 5%" class="align-middle">BK</th>
    //                                     <th style="width: 40%" class="align-middle">Bukti Relevan</th>
    //                                 </tr>
    //                             </thead>
    //                             <tbody>
    //                 `;

    //       // Loop Elements
    //       if (unit.elements) {
    //         unit.elements.forEach(function (el) {
    //           // Header Elemen (Merged Row)
    //           tableHtml += `
    //                             <tr class="bg-light">
    //                                 <td colspan="4" class="font-weight-bold pl-3">
    //                                     Elemen: ${el.name}
    //                                 </td>
    //                             </tr>
    //                         `;

    //           // Loop KUK (Kriteria Unjuk Kerja)
    //           // KUK digabung dalam satu sel list untuk menghemat ruang, atau per baris?
    //           // Sesuai layout yang diminta user: "Dapatkah Saya?" berisi list KUK.

    //           var kukListHtml =
    //             '<ul class="pl-3 mb-0" style="list-style-type: none;">';
    //           if (el.kuks) {
    //             el.kuks.forEach(function (kuk, kIdx) {
    //               // Contoh: 1.1 Judul KUK
    //               kukListHtml += `<li class="mb-2"><small class="font-weight-bold text-muted">${
    //                 el.no
    //               }.${kIdx + 1}</small> ${kuk}</li>`;
    //             });
    //           }
    //           kukListHtml += "</ul>";

    //           tableHtml += `
    //                             <tr>
    //                                 <td class="align-top py-3">${kukListHtml}</td>
    //                                 <td class="text-center align-middle">
    //                                     <input type="checkbox" name="kompeten_${unit.code}_${el.no}" value="K">
    //                                 </td>
    //                                 <td class="text-center align-middle">
    //                                     <input type="checkbox" name="kompeten_${unit.code}_${el.no}" value="BK">
    //                                 </td>
    //                                 <td class="align-middle px-3">
    //                                     <select class="form-control select2" multiple="multiple" data-placeholder="Pilih Bukti" style="width: 100%;">
    //                                         <option value="ijazah">Ijazah</option>
    //                                         <option value="sertifikat">Sertifikat Pelatihan</option>
    //                                         <option value="sk">Surat Keterangan Kerja</option>
    //                                         <option value="laporan">Laporan Pekerjaan</option>
    //                                     </select>
    //                                 </td>
    //                             </tr>
    //                         `;
    //         });
    //       }

    //       tableHtml += `
    //                             </tbody>
    //                         </table>
    //                     </div>
    //                 </div>`;

    //       container.append(tableHtml);
    //     });

    // Re-init select2 for new dynamic elements
    $(".select2").select2({ theme: "bootstrap4" });
  }
});
