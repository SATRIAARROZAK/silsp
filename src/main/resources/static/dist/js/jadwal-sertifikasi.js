$(document).ready(function () {
  // Inisialisasi Select2
  //   $(".select2").select2({ theme: "bootstrap4" });
  // Handle Tombol Expand/Collapse
  $(".btn-expand").on("click", function () {
    var targetId = $(this).data("target");
    var icon = $(this).find("i");
    var row = $(targetId);

    // Toggle visibility row detail
    if (row.hasClass("d-none")) {
      row.removeClass("d-none"); // Show
      icon.removeClass("fa-plus").addClass("fa-minus"); // Ganti icon jadi minus
      $(this).removeClass("btn-outline-info").addClass("btn-danger");
    } else {
      row.addClass("d-none"); // Hide
      icon.removeClass("fa-minus").addClass("fa-plus"); // Ganti icon jadi plus
      $(this).removeClass("btn-danger").addClass("btn-outline-info");
    }
  });

  var selectedAsesorIds = [];
  var selectedSchemaIds = [];

  // ==================================================================
  // 0. INIT EDIT MODE (BACA DATA YANG SUDAH ADA) - FITUR BARU
  // ==================================================================
  // Loop semua input hidden asesor yang sudah dirender server
  $("input[name='assessorIds']").each(function () {
    selectedAsesorIds.push($(this).val());
  });

  // Loop semua input hidden skema yang sudah dirender server
  $("input[name='schemaIds']").each(function () {
    selectedSchemaIds.push($(this).val());
  });

  // Jika ini mode edit, sembunyikan pesan "Belum ada data" jika array terisi
  if (selectedAsesorIds.length > 0) $("#emptyAsesor").hide();
  if (selectedSchemaIds.length > 0) $("#emptySkema").hide();

  // Update nomor urut awal
  updateRowNumbers("#tbodyAsesor");
  updateRowNumbers("#tbodySkema");

  // ==================================================================
  // FUNGSI HELPER: UPDATE NOMOR TABEL
  // ==================================================================
  function updateRowNumbers(tbodyId) {
    $(tbodyId)
      .find("tr:visible")
      .not('[id^="empty"]')
      .each(function (index) {
        $(this)
          .find("td:first")
          .text(index + 1);
      });
  }

  // ==================================================================
  // 1. LOGIKA PILIH ASESOR (Tabel: No, Nama, No MET, Aksi)
  // ==================================================================
  $("#btnPilihAsesor").click(function () {
    var id = $("#selectAsesor").val();
    var nama = $("#selectAsesor option:selected").text();
    var noMet = $("#selectAsesor option:selected").data("nomet");

    if (!id) {
      Swal.fire(
        "Peringatan",
        "Silakan pilih asesor terlebih dahulu",
        "warning"
      );
      return;
    }
    if (selectedAsesorIds.includes(id)) {
      Swal.fire("Info", "Asesor ini sudah ditambahkan", "info");
      return;
    }

    selectedAsesorIds.push(id);
    $("#emptyAsesor").hide();
    $("#errorTabelAsesor").addClass("d-none"); // Sembunyikan error jika ada

    var rowHtml = `
            <tr id="row-asesor-${id}">
                <td class="text-center"></td> <td>${nama}</td>
                <td>${
                  noMet
                    ? noMet
                    : '<span class="text-warning small">Belum ada No. MET</span>'
                }</td>
                <td class="align-middle">
                            <div class="action-buttons">
                              <button type="button" class="btn btn-danger btn-xs" onclick="hapusAsesor('${id}')">
                        <i class="fas fa-trash"></i>
                    </button>
                    <input type="hidden" name="assessorIds" value="${id}">
                            </div>
                          </td>
               
            </tr>
        `;
    $("#tbodyAsesor").append(rowHtml);
    updateRowNumbers("#tbodyAsesor");

    // Reset Select
    $("#selectAsesor").val("").trigger("change");
  });

  window.hapusAsesor = function (id) {
    $(`#row-asesor-${id}`).remove();
    selectedAsesorIds = selectedAsesorIds.filter((item) => item !== id);
    if (selectedAsesorIds.length === 0) $("#emptyAsesor").show();
    updateRowNumbers("#tbodyAsesor");
  };

  // ==================================================================
  // 2. LOGIKA PILIH SKEMA (Tabel: No, Nama Skema, Aksi)
  // ==================================================================
  $("#btnPilihSkema").click(function () {
    var id = $("#selectSkema").val();
    var namaSkema = $("#selectSkema option:selected").text();

    if (!id) {
      Swal.fire("Peringatan", "Silakan pilih skema terlebih dahulu", "warning");
      return;
    }
    if (selectedSchemaIds.includes(id)) {
      Swal.fire("Info", "Skema ini sudah ditambahkan", "info");
      return;
    }

    selectedSchemaIds.push(id);
    $("#emptySkema").hide();
    $("#errorTabelSkema").addClass("d-none"); // Sembunyikan error

    var rowHtml = `
            <tr id="row-skema-${id}">
                <td class="text-center"></td>
                <td>${namaSkema}</td>
                <td class="text-center">
                    <button type="button" class="btn btn-danger btn-xs" onclick="hapusSkema('${id}')">
                        <i class="fas fa-trash"></i>
                    </button>
                    <input type="hidden" name="schemaIds" value="${id}">
                </td>
            </tr>
        `;
    $("#tbodySkema").append(rowHtml);
    updateRowNumbers("#tbodySkema");

    // Reset Select
    $("#selectSkema").val("").trigger("change");
  });

  window.hapusSkema = function (id) {
    $(`#row-skema-${id}`).remove();
    selectedSchemaIds = selectedSchemaIds.filter((item) => item !== id);
    if (selectedSchemaIds.length === 0) $("#emptySkema").show();
    updateRowNumbers("#tbodySkema");
  };

  var validationConfig = {
    // Abaikan elemen hidden KECUALI Select2 (karena Select2 menyembunyikan select asli)
    ignore: function (index, element) {
      if ($(element).hasClass("select2-hidden-accessible")) {
        // Validasi select2 hanya jika parent-nya visible
        return $(element).next(".select2-container").is(":hidden");
      }
      return $(element).is(":hidden");
    },

    // RULES: Harus sesuai attribut name="" di HTML (Bukan ID!)
    rules: {
      name: { required: true }, // Field: Nama Jadwal
      tukId: { required: true }, // Field: Pilih TUK
      bnspCode: { required: true }, // Field: Kode BNSP
      startDate: { required: true }, // Field: Tanggal
      quota: { required: true, min: 1 }, // Field: Kuota
      budgetSource: { required: true }, // Field: Sumber Anggaran
      budgetProvider: { required: true }, // Field: Pemberi Anggaran
    },

    // Pesan Error Custom (Opsional, jika tidak pakai default)
    messages: {
      name: "Nama jadwal wajib diisi",
      tukId: "Mohon pilih TUK",
      bnspCode: "Kode BNSP wajib diisi",
      startDate: "Tentukan tanggal pelaksanaan",
      quota: "Kuota minimal 1 peserta",
      budgetSource: "Pilih sumber anggaran",
      budgetProvider: "Pilih pemberi anggaran",
    },

    errorElement: "span",
    errorPlacement: function (error, element) {
      error.addClass("invalid-feedback");
      // Penanganan khusus untuk Select2
      if (
        element.hasClass("select2") ||
        element.hasClass("select2-hidden-accessible")
      ) {
        error.insertAfter(element.next(".select2"));
      } else {
        element.closest(".form-group").append(error);
      }
    },

    highlight: function (element) {
      $(element).addClass("is-invalid").removeClass("is-valid");
      // Highlight border Select2
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .addClass("is-invalid-border");
      }
    },

    unhighlight: function (element) {
      $(element).removeClass("is-invalid").addClass("is-valid");
      // Remove Highlight Select2
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .removeClass("is-invalid-border");
      }
    },

    // Handler jika ada error pada input standar (Scroll ke atas)
    invalidHandler: function (event, validator) {
      Swal.fire({
        icon: "warning",
        title: "Perhatian!",
        text: "Harap lengkapi semua formulir yang wajib diisi.",
        confirmButtonColor: "#3085d6",
      }).then(() => {
        if (validator.errorList.length > 0) {
          var firstErrorElement = $(validator.errorList[0].element);
          // Scroll animasi
          $("html, body").animate(
            {
              scrollTop:
                firstErrorElement.closest(".form-group").offset().top - 100,
            },
            500
          );
        }
      });
    },

    // --- INI TEMPAT VALIDASI TABEL (CUSTOM LOGIC) ---
    // submitHandler hanya jalan jika semua rules di atas LULUS (Valid)
    submitHandler: function (form) {
      var isTableValid = true;

      // 1. Validasi Tabel Asesor
      if (selectedAsesorIds.length === 0) {
        $("#errorTabelAsesor").removeClass("d-none"); // Munculkan text error merah
        isTableValid = false;
      } else {
        $("#errorTabelAsesor").addClass("d-none");
      }

      // 2. Validasi Tabel Skema
      if (selectedSchemaIds.length === 0) {
        $("#errorTabelSkema").removeClass("d-none");
        isTableValid = false;
      } else {
        $("#errorTabelSkema").addClass("d-none");
      }

      // Jika tabel kosong, batalkan submit & beri alert
      if (!isTableValid) {
        Swal.fire({
          icon: "error",
          title: "Data Belum Lengkap",
          text: "Harap pilih minimal 1 Asesor dan 1 Skema!",
          confirmButtonColor: "#d33",
        });
        return false; // Stop submit
      }

      // JIKA SEMUA VALID:
      Swal.fire({
        title: "Menyimpan Jadwal...",
        text: "Mohon tunggu sebentar",
        allowOutsideClick: false,
        didOpen: () => Swal.showLoading(),
      });

      // Lanjutkan submit form secara native
      form.submit();
    },
  };

  // Terapkan konfigurasi ke Form
  $("#formJadwal").validate(validationConfig);

  // ==================================================================
  // 3. VALIDASI FORM SEBELUM SUBMIT
  // ==================================================================
  //   $("#formJadwal").on("submit", function (e) {
  //     var isValid = true;
  //     var firstError = null;

  //     // Fungsi helper untuk error
  //     function setError(id, messageId) {
  //       $(id).addClass("is-invalid");
  //       $(messageId).removeClass("d-none");
  //       if (!firstError) firstError = id;
  //       isValid = false;
  //     }

  //     function clearError(id, messageId) {
  //       $(id).removeClass("is-invalid");
  //       $(messageId).addClass("d-none");
  //     }

  //     // 1. Validasi Input Standar
  //     var inputs = [
  //       { id: "#inputNamaJadwal" },
  //       { id: "#inputBnspCode" },
  //       { id: "#inputDate" },
  //       { id: "#inputQuota" },
  //     ];
  //     inputs.forEach((el) => {
  //       if (!$(el.id).val()) {
  //         $(el.id).addClass("is-invalid");
  //         if (!firstError) firstError = el.id;
  //         isValid = false;
  //       } else {
  //         $(el.id).removeClass("is-invalid");
  //       }
  //     });

  //     // 2. Validasi Select2 (TUK, Sumber, Pemberi)
  //     var selects = [
  //       { id: "#selectTuk", err: "#errorTuk" },
  //       { id: "#selectSumber", err: "#errorSumber" },
  //       { id: "#selectPemberi", err: "#errorPemberi" },
  //     ];
  //     selects.forEach((el) => {
  //       if (!$(el.id).val()) {
  //         setError(el.id + " + .select2 .select2-selection", el.err); // Target border select2
  //       } else {
  //         clearError(el.id + " + .select2 .select2-selection", el.err);
  //       }
  //     });

  //     // 3. Validasi Tabel (Minimal 1 Data)
  //     if (selectedAsesorIds.length === 0) {
  //       $("#errorTabelAsesor").removeClass("d-none");
  //       isValid = false;
  //     } else {
  //       $("#errorTabelAsesor").addClass("d-none");
  //     }

  //     if (selectedSchemaIds.length === 0) {
  //       $("#errorTabelSkema").removeClass("d-none");
  //       isValid = false;
  //     } else {
  //       $("#errorTabelSkema").addClass("d-none");
  //     }

  //     // JIKA ADA ERROR
  //     if (!isValid) {
  //       e.preventDefault(); // Stop submit
  //       Swal.fire({
  //         icon: "error",
  //         title: "Data Belum Lengkap",
  //         text: "Mohon lengkapi semua kolom",
  //       }).then(() => {
  //         if (firstError) {
  //           $("html, body").animate(
  //             { scrollTop: $(firstError).offset().top - 100 },
  //             500
  //           );
  //         }
  //       });
  //     } else {
  //       // Jika Valid
  //       Swal.fire({
  //         title: "Menyimpan...",
  //         didOpen: () => Swal.showLoading(),
  //       });
  //     }
  //   });

  //   // Hapus error saat user mulai mengisi/memilih
  //   $("input").on("input", function () {
  //     $(this).removeClass("is-invalid");
  //   });
  //   $(".select2").on("change", function () {
  //     $(this)
  //       .next(".select2")
  //       .find(".select2-selection")
  //       .removeClass("is-invalid");
  //     $(this).parent().find(".text-danger.small").addClass("d-none");
  //   });

  // Konfirmasi Delete
  $(".delete-button-jadwal").on("click", function (e) {
    e.preventDefault();
    var link = $(this).attr("href");

    Swal.fire({
      title: "Yakin Hapus Jadwal?",
      text: "Data didalamnya juga akan terhapus permanen!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Ya, Hapus!",
      cancelButtonText: "Batal",
    }).then((result) => {
      if (result.isConfirmed) {
        window.location.href = link;
      }
    });
  });
});
