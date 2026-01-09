// $(document).ready(function () {
//   // Inisialisasi Select2
//   $(".select2").select2({ theme: "bootstrap4" });

//   // Konfigurasi Validasi
//   var validator = $("#formSurat").validate({
//     ignore: [],
//     rules: {
//       jadwalId: { required: true },
//       asesorId: { required: true },
//       skemaId: { required: true },
//     },
//     messages: {
//       jadwalId: "Pilih Jadwal!",
//       asesorId: "Pilih Asesor!",
//       skemaId: "Pilih Skema!",
//     },
//     errorElement: "span",
//     errorPlacement: function (error, element) {
//       error.addClass("invalid-feedback");
//       if (element.hasClass("select2-hidden-accessible")) {
//         error.insertAfter(element.next(".select2"));
//       } else {
//         element.closest(".form-group").append(error);
//       }
//     },
//     highlight: function (element) {
//       $(element).addClass("is-invalid");
//       if ($(element).hasClass("select2-hidden-accessible")) {
//         $(element)
//           .next(".select2")
//           .find(".select2-selection")
//           .addClass("is-invalid-border");
//       }
//     },
//     unhighlight: function (element) {
//       $(element).removeClass("is-invalid");
//       if ($(element).hasClass("select2-hidden-accessible")) {
//         $(element)
//           .next(".select2")
//           .find(".select2-selection")
//           .removeClass("is-invalid-border");
//       }
//     },

//     // SUBMIT HANDLER KHUSUS
//     submitHandler: function (form) {
//       var jadwalId = $("#selectJadwal").val();
//       var asesorId = $("#selectAsesor").val();

//       // 1. Cek Duplikat via AJAX
//       Swal.fire({
//         title: "Memeriksa Data...",
//         allowOutsideClick: false,
//         didOpen: () => Swal.showLoading(),
//       });

//       $.ajax({
//         url: "/admin/api/check-surat-tugas",
//         method: "GET",
//         data: { jadwalId: jadwalId, asesorId: asesorId },
//         success: function (exists) {
//           Swal.close();

//           if (exists) {
//             // JIKA SUDAH ADA SURAT TUGAS
//             Swal.fire({
//               icon: "error",
//               title: "Gagal Generate!",
//               text: "Asesor tersebut SUDAH DITUGASKAN pada jadwal ini. Mohon pilih asesor lain atau cek daftar surat tugas.",
//               confirmButtonColor: "#d33",
//             });
//           } else {
//             // JIKA AMAN -> SUBMIT FORM (Download PDF)
//             // Karena target="_blank", halaman ini tidak akan reload/pindah.
//             // Kita submit form secara native DOM agar terdownload di tab baru.
//             form.submit();

//             // Tampilkan pesan sukses di halaman ini
//             Swal.fire({
//               icon: "success",
//               title: "Berhasil!",
//               text: "Surat tugas sedang diunduh dan telah disimpan ke database.",
//               showConfirmButton: false,
//               timer: 3000,
//             }).then(() => {
//               // Reload agar nomor surat baru tergenerate
//               location.reload();
//             });
//           }
//         },
//         error: function () {
//           Swal.close();
//           Swal.fire(
//             "Error",
//             "Gagal menghubungi server untuk pengecekan data.",
//             "error"
//           );
//         },
//       });

//       // PENTING: Return false agar form tidak tersubmit ganda otomatis oleh jQuery Validate
//       return false;
//     },
//   });

//   // Integrasi Select2 dengan Validasi (Hapus error saat dipilih)
//   $(".select2").on("change", function () {
//     $(this).valid();
//   });
// });
// $(document).ready(function () {
//   $(".select2").select2({ theme: "bootstrap4" });

//   // ==========================================
//   // LOGIKA DINAMIS: HANYA JADWAL -> SKEMA
//   // ==========================================
//   $("#selectJadwal").on("change", function () {
//     var jadwalId = $(this).val();

//     // Reset Dropdown Skema Saja (Asesor Biarkan)
//     $("#selectSkema")
//       .empty()
//       .append('<option value="">-- Loading... --</option>')
//       .prop("disabled", true);

//     if (jadwalId) {
//       // Panggil API Internal
//       $.ajax({
//         url: "/admin/api/internal/jadwal/" + jadwalId + "/details",
//         method: "GET",
//         success: function (data) {
//           // ISI DROPDOWN SKEMA
//           var optSkema = '<option value="" selected>-- Pilih Skema --</option>';

//           if (data.schemas && data.schemas.length > 0) {
//             data.schemas.forEach(function (s) {
//               optSkema += `<option value="${s.id}">${s.name}</option>`;
//             });
//             $("#selectSkema").html(optSkema).prop("disabled", false);
//           } else {
//             $("#selectSkema").html(
//               '<option value="">Tidak ada skema di jadwal ini</option>'
//             );
//           }

//           // NOTE: Asesor TIDAK disentuh/direset
//         },
//         error: function () {
//           Swal.fire("Error", "Gagal memuat detail jadwal.", "error");
//           $("#selectSkema").html('<option value="">Gagal memuat data</option>');
//         },
//       });
//     } else {
//       // Jika Jadwal di-unselect
//       $("#selectSkema")
//         .html(
//           '<option value="" selected>-- Pilih Jadwal Terlebih Dahulu --</option>'
//         )
//         .prop("disabled", true);
//     }
//   });
// });
$(document).ready(function () {
  $(".select2").select2({ theme: "bootstrap4" });

  // ==========================================
  // LOGIKA DINAMIS: JADWAL -> ASESOR & SKEMA
  // ==========================================

  $("#selectJadwal").on("change", function () {
    var jadwalId = $(this).val();
    $("#selectAsesor")
      .empty()
      .append('<option value="">-- Loading... --</option>')
      .prop("disabled", true);
    $("#selectSkema")
      .empty()
      .append('<option value="">-- Loading... --</option>')
      .prop("disabled", true);

    if (jadwalId) {
      $.ajax({
        url: "/admin/api/internal/jadwal/" + jadwalId + "/details",
        method: "GET",
        success: function (data) {
          var optAsesor = '<option value="">-- Pilih Asesor --</option>';
          if (data.asesors.length > 0) {
            data.asesors.forEach(function (a) {
              optAsesor += `<option value="${a.id}">${a.name} (${
                a.noMet || "Belum ada MET"
              })</option>`;
            });
            $("#selectAsesor").html(optAsesor).prop("disabled", false);
          } else {
            $("#selectAsesor").html(
              '<option value="">Tidak ada asesor di jadwal ini</option>'
            );
          }
          var optSkema = '<option value="" selected>-- Pilih Skema --</option>';
          if (data.schemas && data.schemas.length > 0) {
            data.schemas.forEach(function (s) {
              optSkema += `<option value="${s.id}">${s.name}</option>`;
            });
            $("#selectSkema").html(optSkema).prop("disabled", false);
          } else {
            $("#selectSkema").html('<option value="">Tidak ada skema</option>');
          }
        },
      });
    } else {
      $("#selectAsesor")
        .html('<option value="" selected>-- Pilih Jadwal Dulu --</option>')
        .prop("disabled", true);
      $("#selectSkema")
        .html('<option value="" selected>-- Pilih Jadwal Dulu --</option>')
        .prop("disabled", true);
    }
  });

  // Konfigurasi Validasi
  var validator = $("#formSurat").validate({
    ignore: [],
    rules: {
      jadwalId: { required: true },
      asesorId: { required: true },
      skemaId: { required: true },
    },
    messages: {
      jadwalId: "Pilih Jadwal!",
      asesorId: "Pilih Asesor!",
      skemaId: "Pilih Skema!",
    },
    errorElement: "span",
    errorPlacement: function (error, element) {
      error.addClass("invalid-feedback");
      if (element.hasClass("select2-hidden-accessible")) {
        error.insertAfter(element.next(".select2"));
      } else {
        element.closest(".form-group").append(error);
      }
    },
    highlight: function (element) {
      $(element).addClass("is-invalid");
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .addClass("is-invalid-border");
      }
    },
    unhighlight: function (element) {
      $(element).removeClass("is-invalid");
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .removeClass("is-invalid-border");
      }
    },

    // SUBMIT HANDLER KHUSUS
    submitHandler: function (form) {
      var jadwalId = $("#selectJadwal").val();
      var asesorId = $("#selectAsesor").val();

      // 1. Cek Duplikat via AJAX
      Swal.fire({
        title: "Memeriksa Data...",
        allowOutsideClick: false,
        didOpen: () => Swal.showLoading(),
      });

      $.ajax({
        url: "/admin/api/check-surat-tugas",
        method: "GET",
        data: { jadwalId: jadwalId, asesorId: asesorId },
        success: function (exists) {
          Swal.close();

          if (exists) {
            // JIKA SUDAH ADA SURAT TUGAS
            Swal.fire({
              icon: "error",
              title: "Gagal Generate!",
              text: "Asesor tersebut SUDAH DITUGASKAN pada jadwal ini. Mohon pilih asesor lain atau cek daftar surat tugas.",
              confirmButtonColor: "#d33",
            });
          } else {
            // JIKA AMAN -> SUBMIT FORM (Download PDF)
            // Karena target="_blank", halaman ini tidak akan reload/pindah.
            // Kita submit form secara native DOM agar terdownload di tab baru.
            form.submit();

            // Tampilkan pesan sukses di halaman ini
            Swal.fire({
              icon: "success",
              title: "Berhasil!",
              text: "Surat tugas sedang diunduh dan telah disimpan ke database.",
              showConfirmButton: false,
              timer: 3000,
            }).then(() => {
              // Reload agar nomor surat baru tergenerate
              location.reload();
            });
          }
        },
        error: function () {
          Swal.close();
          Swal.fire(
            "Error",
            "Gagal menghubungi server untuk pengecekan data.",
            "error"
          );
        },
      });

      // PENTING: Return false agar form tidak tersubmit ganda otomatis oleh jQuery Validate
      return false;
    },
  });

  // Integrasi Select2 dengan Validasi (Hapus error saat dipilih)
  //   $(".select2").on("change", function () {
  //     $(this).valid();
  //   });

  // ==========================================
  // ERROR HANDLING SAAT SUBMIT (Cek Duplikat)
  // ==========================================
  // Karena form target="_blank" (download file), kita tidak bisa menangkap response error JSON dengan mudah via AJAX biasa.
  // Trik: Kita biarkan submit, jika error backend akan melempar teks error.
  // User akan melihat tab baru berisi pesan error jika duplikat.
});
