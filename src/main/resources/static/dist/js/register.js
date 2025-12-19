// $(document).ready(function () {
//   // 1. INISIALISASI PLUGIN
//   const API_WILAYAH_URL = "https://www.emsifa.com/api-wilayah-indonesia/api";

//   $(function () {
//     //Initialize Select2 Elements
//     $(".select2").select2();

//     //Initialize Select2 Elements
//     $(".select2bs4").select2({
//       theme: "bootstrap4",
//     });

//     //Date picker
//     $("#reservationdate").datetimepicker({
//       format: "DD/MM/YYYY",
//     });
//   });

//   // Override pesan error default jQuery Validate
//   $.extend($.validator.messages, {
//     required: "Isilah form ini",
//     email: "Format email tidak valid",
//     number: "Harus berupa angka",
//     minlength: $.validator.format("Masukkan minimal {0} karakter"),
//     remote: "Data ini sudah digunakan, silakan ganti",
//   });

//   // Validasi Real-time Select2
//   $(".select2").on("change", function () {
//     $(this).valid();
//   });

//   // ==========================================
//   // 2. LOGIKA ROLE (FLEXIBLE FORM) - DIPERBARUI
//   // ==========================================
//   $("#roleSelect").on("change", function () {
//     var role = $(this).val();

//     // PERUBAHAN: Tidak perlu menampilkan #wrapperDataPribadi karena sudah visible dari awal

//     // Reset Field Role Spesifik (Sembunyikan keduanya dulu)
//     $(".group-asesi").hide();
//     $(".group-asesor").hide();

//     // Tampilkan sesuai pilihan
//     if (role === "Asesi") {
//       $(".group-asesi").show();
//       $("[name='citizenship']").prop("required", true);
//       $("[name='noMet']").prop("required", false);
//       $("#selectPekerjaan").prop("required", false);
//     } else if (role === "Asesor") {
//       $(".group-asesor").show();
//       $("[name='citizenship']").prop("required", false);
//       $("[name='noMet']").prop("required", true);
//       $("#selectPekerjaan").prop("required", true);
//     }
//   });

//   // ==========================================
//   // 3. LOAD DATA (PENDIDIKAN & PEKERJAAN)
//   // ==========================================
//   fetch("/dist/js/education.json")
//     .then((res) => res.json())
//     .then((data) => {
//       let opts = '<option value="">Pilih Pendidikan...</option>';
//       data.forEach(
//         (item) => (opts += `<option value="${item.id}">${item.name}</option>`)
//       );
//       $("#selectPendidikan").html(opts);
//     });

//   fetch("/dist/js/jobs.json")
//     .then((res) => res.json())
//     .then((data) => {
//       let opts = '<option value="">Pilih Pekerjaan...</option>';
//       data.forEach(
//         (item) => (opts += `<option value="${item.id}">${item.name}</option>`)
//       );
//       $("#selectPekerjaan").html(opts);
//     });

//   // ==========================================
//   // 4. API WILAYAH (CASCADING)
//   // ==========================================
//   fetch(`${API_WILAYAH_URL}/provinces.json`)
//     .then((res) => res.json())
//     .then((data) => {
//       let opts = '<option value="">Pilih Provinsi...</option>';
//       data.forEach(
//         (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
//       );
//       $("#selectProvinsi").html(opts);
//     });

//   $("#selectProvinsi").on("change", function () {
//     let id = $(this).val();
//     $("#inputProvinsi").val(id);
//     $("#selectKota")
//       .html('<option value="">Loading...</option>')
//       .prop("disabled", true);

//     if (id) {
//       fetch(`${API_WILAYAH_URL}/regencies/${id}.json`)
//         .then((res) => res.json())
//         .then((data) => {
//           let opts = '<option value="">Pilih Kota/Kab...</option>';
//           data.forEach(
//             (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
//           );
//           $("#selectKota").html(opts).prop("disabled", false);
//         });
//     }
//   });

//   $("#selectKota").on("change", function () {
//     let id = $(this).val();
//     $("#inputKota").val(id);
//     $("#selectKecamatan")
//       .html('<option value="">Loading...</option>')
//       .prop("disabled", true);

//     if (id) {
//       fetch(`${API_WILAYAH_URL}/districts/${id}.json`)
//         .then((res) => res.json())
//         .then((data) => {
//           let opts = '<option value="">Pilih Kecamatan...</option>';
//           data.forEach(
//             (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
//           );
//           $("#selectKecamatan").html(opts).prop("disabled", false);
//         });
//     }
//   });

//   $("#selectKecamatan").on("change", function () {
//     let id = $(this).val();
//     $("#inputKecamatan").val(id);
//     $("#selectKelurahan")
//       .html('<option value="">Loading...</option>')
//       .prop("disabled", true);

//     if (id) {
//       fetch(`${API_WILAYAH_URL}/villages/${id}.json`)
//         .then((res) => res.json())
//         .then((data) => {
//           let opts = '<option value="">Pilih Kelurahan...</option>';
//           data.forEach(
//             (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
//           );
//           $("#selectKelurahan").html(opts).prop("disabled", false);
//         });
//     }
//   });

//   $("#selectKelurahan").on("change", function () {
//     $("#inputKelurahan").val($(this).val());
//   });

//   // ==========================================
//   // 5. MODAL PASSWORD
//   // ==========================================
//   $("#savePassword").on("click", function () {
//     var p1 = $("#modalPass1").val();
//     var p2 = $("#modalPass2").val();

//     if (p1.length < 8) {
//       Swal.fire("Error", "Password minimal 8 karakter!", "error");
//       return;
//     }

//     if (p1 !== p2) {
//       $("#passMismatch").show();
//       return;
//     } else {
//       $("#passMismatch").hide();
//     }

//     $("#realPassword").val(p1);
//     $("#passwordStatus").addClass("d-none");
//     $("#passwordSuccess").removeClass("d-none");
//     $("#modalPassword").modal("hide");
//     $("#realPassword").valid();
//   });

//   var canvas = document.getElementById("signature-canvas");
//   if (canvas) {
//     var signaturePad = new SignaturePad(canvas, {
//       backgroundColor: "rgba(255, 255, 255, 0)",
//       penColor: "rgb(0, 0, 0)",
//     });

//     function resizeCanvas() {
//       var ratio = Math.max(window.devicePixelRatio || 1, 1);
//       var data = signaturePad.toData();
//       canvas.width = canvas.offsetWidth * ratio;
//       canvas.height = canvas.offsetHeight * ratio;
//       canvas.getContext("2d").scale(ratio, ratio);
//       signaturePad.clear();
//       signaturePad.fromData(data);
//     }

//     $("#modalSignature").on("shown.bs.modal", function () {
//       resizeCanvas();
//       var existingSignature = $("#signatureInput").val();
//       if (existingSignature && existingSignature.trim() !== "") {
//         signaturePad.fromDataURL(existingSignature, { ratio: 1.5 });
//       }
//     });
//     $("#btnClear").on("click", function () {
//       signaturePad.clear();
//     });
//     $("#btnUpload").on("click", function () {
//       $("#uploadSigFile").click();
//     });

//     $("#uploadSigFile").on("change", function (e) {
//       var file = e.target.files[0];
//       if (!file) return;
//       if (file.type !== "image/png") {
//         Swal.fire({ icon: "error", title: "Hanya format PNG diperbolehkan." });
//         this.value = "";
//         return;
//       }
//       var reader = new FileReader();
//       reader.onload = function (event) {
//         signaturePad.fromDataURL(event.target.result);
//       };
//       reader.readAsDataURL(file);
//     });

//     $("#btnSaveSignature").on("click", function () {
//       // JIKA KOSONG -> TETAP KOSONGKAN HIDDEN & SHOW ERROR ALERT
//       if (signaturePad.isEmpty()) {
//         $("#signatureInput").val("");
//         $("#signaturePreview").hide();

//         // Reset tombol
//         $("#btnTriggerSignature")
//           .removeClass("btn-success btn-outline-success")
//           .addClass("btn-outline-primary")
//           .html('<i class="fas fa-pen-nib"></i> Masukkan Tanda Tangan');

//         // ALERT SESUAI REQUEST (TIDAK TUTUP MODAL)
//         Swal.fire({
//           icon: "warning",
//           title: "Isi Tanda Tangan Terlebih Dahulu",
//           confirmButtonText: "Oke",
//           confirmButtonColor: "#3085d6",
//         });
//         // Modal tetap terbuka, user harus isi
//       } else {
//         // JIKA TERISI -> SIMPAN & TUTUP MODAL
//         var dataURL = signaturePad.toDataURL("image/png");
//         $("#signatureInput").val(dataURL);

//         $("#btnTriggerSignature")
//           .removeClass("btn-outline-primary btn-outline-danger")
//           .addClass("btn-outline-success")
//           .html('<i class="fas fa-eye"></i> Lihat Tanda Tangan');

//         $("#imgPreview").attr("src", dataURL);
//         $("#signaturePreview").show();

//         // Hapus error validasi jika ada
//         $("#signatureInput").valid();

//         // Tutup Modal
//         $("#modalSignature")
//           .find('[data-dismiss="modal"]')
//           .first()
//           .trigger("click");
//       }
//     });

//     window.addEventListener("resize", resizeCanvas);
//   }

//   // ==========================================
//   // 7. VALIDASI FORM & SUBMIT (SUPER AMAN)
//   // ==========================================
//   $("#registerForm").validate({
//     ignore: ":hidden:not(#realPassword, #signatureInput)",
//     rules: {
//       username: {
//         required: true,
//         minlength: 3,
//         remote: {
//           url: "/api/check-duplicate",
//           type: "GET",
//           data: {
//             username: function () {
//               return $("[name='username']").val();
//             },
//           },
//         },
//       },
//       email: {
//         required: true,
//         email: true,
//         remote: {
//           url: "/api/check-duplicate",
//           type: "GET",
//           data: {
//             email: function () {
//               return $("[name='email']").val();
//             },
//           },
//         },
//       },
//       roles: { required: true },
//       fullName: { required: true },
//       nik: { required: true, number: true, minlength: 16 },
//       password: { required: true },
//       signatureBase64: { required: true },
//     },
//     messages: {
//       username: { remote: "Username sudah digunakan!" },
//       email: { remote: "Email sudah terdaftar!" },
//       password: { required: "Silakan buat kata sandi dulu" },
//       signatureBase64: { required: "Tanda tangan wajib diisi" },
//     },
//     errorElement: "span",
//     errorPlacement: function (error, element) {
//       error.addClass("invalid-feedback");
//       if (element.hasClass("select2-hidden-accessible")) {
//         error.insertAfter(element.next(".select2"));
//       } else if (element.attr("name") == "password") {
//         error.insertAfter("#passwordStatus");
//         error.css("display", "block");
//       } else if (element.attr("name") == "signatureBase64") {
//         error.insertAfter("#btnTriggerSignature");
//         error.css("display", "block");
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
//     invalidHandler: function (event, validator) {
//       var errors = validator.numberOfInvalids();
//       if (errors) {
//         Swal.fire({
//           icon: "error",
//           title: "Data Belum Lengkap",
//           text: "Mohon periksa kembali form yang berwarna merah.",
//           confirmButtonColor: "#d33",
//         }).then(() => {
//           if (validator.errorList.length > 0) {
//             $("html, body").animate(
//               {
//                 scrollTop: $(validator.errorList[0].element).offset().top - 100,
//               },
//               500
//             );
//           }
//         });
//       }
//     },
//     submitHandler: function (form) {
//       var formData = new FormData(form);

//       Swal.fire({
//         title: "Mendaftarkan Akun...",
//         allowOutsideClick: false,
//         didOpen: () => Swal.showLoading(),
//       });

//       $.ajax({
//         url: $(form).attr("action"),
//         type: "POST",
//         data: formData,
//         processData: false,
//         contentType: false,
//         success: function (response) {
//           var res =
//             typeof response === "string" ? JSON.parse(response) : response;
//           Swal.fire({
//             icon: "success",
//             title: "Berhasil!",
//             text: res.message,
//             confirmButtonText: "Login Sekarang",
//             confirmButtonColor: "#28a745",
//           }).then((result) => {
//             if (result.isConfirmed) {
//               window.location.href = "/login";
//             }
//           });
//         },
//         error: function (xhr) {
//           var msg = "Terjadi kesalahan server";
//           try {
//             msg = JSON.parse(xhr.responseText).message;
//           } catch (e) {}
//           Swal.fire("Gagal!", msg, "error");
//         },
//       });
//     },
//   });
// });

// $(document).ready(function () {
//   // 1. INISIALISASI PLUGIN
//   $(".select2").select2({ theme: "bootstrap4" });
//   const API_WILAYAH_URL = "https://www.emsifa.com/api-wilayah-indonesia/api";

//   // Override pesan error default jQuery Validate
//   $.extend($.validator.messages, {
//     required: "Isilah form ini",
//     email: "Format email tidak valid",
//     number: "Harus berupa angka",
//     minlength: $.validator.format("Masukkan minimal {0} karakter"),
//     remote: "Data ini sudah digunakan, silakan ganti", // Pesan duplikat
//   });

//   // Validasi Real-time Select2
//   $(".select2").on("change", function () {
//     $(this).valid();
//   });

//   // ==========================================
//   // 2. LOGIKA ROLE (FLEXIBLE FORM)
//   // ==========================================
//   // $("#roleSelect").on("change", function() {
//   //     var role = $(this).val();

//   //     // Tampilkan wrapper data pribadi
//   //     $("#wrapperDataPribadi").slideDown();

//   //     // Reset Field Role Spesifik
//   //     $(".group-asesi").hide();
//   //     $(".group-asesor").hide();

//   //     // Update Validasi (Hapus required dari elemen hidden)
//   //     // Kita gunakan jQuery Validate 'ignore: :hidden' secara global nanti

//   //     if (role === 'Asesi') {
//   //         $(".group-asesi").show();
//   //         $("[name='citizenship']").prop('required', true);
//   //         $("[name='noMet']").prop('required', false);
//   //         $("#selectPekerjaan").prop('required', false);
//   //     } else if (role === 'Asesor') {
//   //         $(".group-asesor").show();
//   //         $("[namecitizenship']").prop('required', false);
//   //         $("[name='noMet']").prop('required', true); // No MET Wajib Asesor
//   //         $("#selectPekerjaan").prop('required', true); // Pekerjaan Wajib Asesor
//   //     }
//   // });

//   $("#roleSelect").on("change", function () {
//     var role = $(this).val();

//     // Tampilkan wrapper data pribadi
//     $("#wrapperDataPribadi").slideDown();

//     // PERUBAHAN: Tidak perlu menampilkan #wrapperDataPribadi karena sudah visible dari awal

//     // Reset Field Role Spesifik (Sembunyikan keduanya dulu)
//     $(".group-asesi").hide();
//     $(".group-asesor").hide();

//     // Tampilkan sesuai pilihan
//     if (role === "Asesi") {
//       $(".group-asesi").show();
//       $("[name='citizenship']").prop("required", true);
//       $("[name='noMet']").prop("required", false);
//       $("#selectPekerjaan").prop("required", false);
//     } else if (role === "Asesor") {
//       $(".group-asesor").show();
//       $("[name='citizenship']").prop("required", false);
//       $("[name='noMet']").prop("required", true);
//       $("#selectPekerjaan").prop("required", true);
//     }
//   });

//   // ==========================================
//   // 3. LOAD DATA (PENDIDIKAN & PEKERJAAN)
//   // ==========================================
//   // Pendidikan
//   fetch("/dist/js/education.json")
//     .then((res) => res.json())
//     .then((data) => {
//       let opts = '<option value="">Pilih Pendidikan...</option>';
//       data.forEach(
//         (item) => (opts += `<option value="${item.id}">${item.name}</option>`)
//       );
//       $("#selectPendidikan").html(opts);
//     });

//   // Pekerjaan
//   fetch("/dist/js/jobs.json")
//     .then((res) => res.json())
//     .then((data) => {
//       let opts = '<option value="">Pilih Pekerjaan...</option>';
//       data.forEach(
//         (item) => (opts += `<option value="${item.id}">${item.name}</option>`)
//       );
//       $("#selectPekerjaan").html(opts);
//     });

//   // ==========================================
//   // 4. API WILAYAH (CASCADING)
//   // ==========================================
//   // Load Provinsi
//   fetch(`${API_WILAYAH_URL}/provinces.json`)
//     .then((res) => res.json())
//     .then((data) => {
//       let opts = '<option value="">Pilih Provinsi...</option>';
//       data.forEach(
//         (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
//       );
//       $("#selectProvinsi").html(opts);
//     });

//   // Change Provinsi -> Kota
//   $("#selectProvinsi").on("change", function () {
//     let id = $(this).val();
//     $("#inputProvinsi").val(id); // Simpan ID
//     $("#selectKota")
//       .html('<option value="">Loading...</option>')
//       .prop("disabled", true);
//     // ... reset child lainnya ...

//     if (id) {
//       fetch(`${API_WILAYAH_URL}/regencies/${id}.json`)
//         .then((res) => res.json())
//         .then((data) => {
//           let opts = '<option value="">Pilih Kota/Kab...</option>';
//           data.forEach(
//             (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
//           );
//           $("#selectKota").html(opts).prop("disabled", false);
//         });
//     }
//   });

//   // Change Kota -> Kecamatan
//   $("#selectKota").on("change", function () {
//     let id = $(this).val();
//     $("#inputKota").val(id);
//     $("#selectKecamatan")
//       .html('<option value="">Loading...</option>')
//       .prop("disabled", true);

//     if (id) {
//       fetch(`${API_WILAYAH_URL}/districts/${id}.json`)
//         .then((res) => res.json())
//         .then((data) => {
//           let opts = '<option value="">Pilih Kecamatan...</option>';
//           data.forEach(
//             (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
//           );
//           $("#selectKecamatan").html(opts).prop("disabled", false);
//         });
//     }
//   });

//   // Change Kecamatan -> Kelurahan
//   $("#selectKecamatan").on("change", function () {
//     let id = $(this).val();
//     $("#inputKecamatan").val(id);
//     $("#selectKelurahan")
//       .html('<option value="">Loading...</option>')
//       .prop("disabled", true);

//     if (id) {
//       fetch(`${API_WILAYAH_URL}/villages/${id}.json`)
//         .then((res) => res.json())
//         .then((data) => {
//           let opts = '<option value="">Pilih Kelurahan...</option>';
//           data.forEach(
//             (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
//           );
//           $("#selectKelurahan").html(opts).prop("disabled", false);
//         });
//     }
//   });

//   // Change Kelurahan
//   $("#selectKelurahan").on("change", function () {
//     $("#inputKelurahan").val($(this).val());
//   });

//   // ==========================================
//   // 5. MODAL PASSWORD
//   // ==========================================
//   $("#savePassword").on("click", function () {
//     var p1 = $("#modalPass1").val();
//     var p2 = $("#modalPass2").val();

//     // Validasi Min 8
//     if (p1.length < 8) {
//       Swal.fire("Error", "Password minimal 8 karakter!", "error");
//       return;
//     }

//     // Validasi Cocok
//     if (p1 !== p2) {
//       $("#passMismatch").show();
//       return;
//     } else {
//       $("#passMismatch").hide();
//     }

//     // Simpan ke Hidden Input
//     $("#realPassword").val(p1);

//     // Update UI
//     $("#passwordStatus").addClass("d-none");
//     $("#passwordSuccess").removeClass("d-none");
//     $("#modalPassword").modal("hide");

//     // Trigger validasi ulang form utama
//     $("#realPassword").valid();
//   });

//   // ==========================================
//   // 6. TANDA TANGAN (FIX MODAL CLOSE)
//   // ==========================================
//   var canvas = document.getElementById("signature-canvas");
//   if (canvas) {
//     var signaturePad = new SignaturePad(canvas, {
//       backgroundColor: "rgba(255, 255, 255, 0)",
//       penColor: "rgb(0, 0, 0)",
//     });

//     function resizeCanvas() {
//       var ratio = Math.max(window.devicePixelRatio || 1, 1);
//       canvas.width = canvas.offsetWidth * ratio;
//       canvas.height = canvas.offsetHeight * ratio;
//       canvas.getContext("2d").scale(ratio, ratio);
//       signaturePad.clear();
//     }

//     $("#modalSignature").on("shown.bs.modal", function () {
//       resizeCanvas();
//     });
//     $("#btnClear").on("click", function () {
//       signaturePad.clear();
//     });
//     $("#btnUpload").on("click", function () {
//       $("#uploadSigFile").click();
//     });

//     $("#uploadSigFile").on("change", function (e) {
//       var file = e.target.files[0];
//       if (!file) return;
//       var reader = new FileReader();
//       reader.onload = function (event) {
//         signaturePad.fromDataURL(event.target.result);
//       };
//       reader.readAsDataURL(file);
//     });

//     $("#btnSaveSignature").on("click", function () {
//       if (signaturePad.isEmpty()) {
//         Swal.fire({ icon: "warning", title: "Tanda tangan masih kosong!" });
//       } else {
//         var dataURL = signaturePad.toDataURL("image/png");
//         $("#signatureInput").val(dataURL);

//         // Update Tampilan Tombol
//         $("#btnTriggerSignature")
//           .removeClass("btn-outline-primary")
//           .addClass("btn-outline-success")
//           .html('<i class="fas fa-eye"></i> Lihat Tanda Tangan');

//         $("#imgPreview").attr("src", dataURL);
//         $("#signaturePreview").show();

//         // PERBAIKAN: Tutup modal secara eksplisit
//         $("#modalSignature").modal("hide");

//         // Trigger validasi
//         $("#signatureInput").valid();
//       }
//     });
//   }

//   // ==========================================
//   // 7. VALIDASI FORM & SUBMIT (SUPER AMAN)
//   // ==========================================
//   $("#registerForm").validate({
//     ignore: ":hidden:not(#realPassword, #signatureInput)", // Validasi hidden password & signature
//     rules: {
//       username: {
//         required: true,
//         minlength: 3,
//         remote: {
//           url: "/api/check-duplicate",
//           type: "GET",
//           data: {
//             username: function () {
//               return $("[name='username']").val();
//             },
//           },
//         },
//       },
//       email: {
//         required: true,
//         email: true,
//         remote: {
//           url: "/api/check-duplicate",
//           type: "GET",
//           data: {
//             email: function () {
//               return $("[name='email']").val();
//             },
//           },
//         },
//       },
//       roles: { required: true },
//       fullName: { required: true },
//       nik: { required: true, number: true, minlength: 16 },
//       // ... rules lain disesuaikan ...
//       password: { required: true },
//       signatureBase64: { required: true },
//     },
//     messages: {
//       username: { remote: "Username sudah digunakan!" },
//       email: { remote: "Email sudah terdaftar!" },
//       password: { required: "Silakan buat kata sandi dulu" },
//       signatureBase64: { required: "Tanda tangan wajib diisi" },
//     },
//     errorElement: "span",
//     errorPlacement: function (error, element) {
//       error.addClass("invalid-feedback");
//       if (element.hasClass("select2-hidden-accessible")) {
//         error.insertAfter(element.next(".select2"));
//       } else if (element.attr("name") == "password") {
//         error.insertAfter("#passwordStatus"); // Taruh dibawah status
//         error.css("display", "block");
//       } else if (element.attr("name") == "signatureBase64") {
//         error.insertAfter("#btnTriggerSignature");
//         error.css("display", "block");
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
//     // SCROLL TO ERROR
//     invalidHandler: function (event, validator) {
//       var errors = validator.numberOfInvalids();
//       if (errors) {
//         Swal.fire({
//           icon: "error",
//           title: "Data Belum Lengkap",
//           text: "Mohon periksa kembali form yang berwarna merah.",
//           confirmButtonColor: "#d33",
//         }).then(() => {
//           if (validator.errorList.length > 0) {
//             $("html, body").animate(
//               {
//                 scrollTop: $(validator.errorList[0].element).offset().top - 100,
//               },
//               500
//             );
//           }
//         });
//       }
//     },
//     // SUBMIT AJAX
//     submitHandler: function (form) {
//       var formData = new FormData(form);

//       Swal.fire({
//         title: "Mendaftarkan Akun...",
//         allowOutsideClick: false,
//         didOpen: () => Swal.showLoading(),
//       });

//       $.ajax({
//         url: $(form).attr("action"),
//         type: "POST",
//         data: formData,
//         processData: false,
//         contentType: false,
//         success: function (response) {
//           var res =
//             typeof response === "string" ? JSON.parse(response) : response;
//           Swal.fire({
//             icon: "success",
//             title: "Berhasil!",
//             text: res.message,
//             confirmButtonText: "Login Sekarang",
//             confirmButtonColor: "#28a745",
//           }).then((result) => {
//             if (result.isConfirmed) {
//               window.location.href = "/login";
//             }
//           });
//         },
//         error: function (xhr) {
//           var msg = "Terjadi kesalahan server";
//           try {
//             msg = JSON.parse(xhr.responseText).message;
//           } catch (e) {}
//           Swal.fire("Gagal!", msg, "error");
//         },
//       });
//     },
//   });
// });

$(document).ready(function () {
  // 1. INISIALISASI PLUGIN
  $(".select2").select2({ theme: "bootstrap4" });
  const API_WILAYAH_URL = "https://www.emsifa.com/api-wilayah-indonesia/api";

  // Override pesan error default jQuery Validate
  $.extend($.validator.messages, {
    required: "Isilah form ini",
    email: "Format email tidak valid",
    number: "Harus berupa angka",
    minlength: $.validator.format("Masukkan minimal {0} karakter"),
    remote: "Data ini sudah digunakan, silakan ganti", // Pesan duplikat
  });

  // Validasi Real-time Select2
  $(".select2").on("change", function () {
    $(this).valid();
  });

  // ==========================================
  // 2. LOGIKA ROLE (FLEXIBLE FORM)
  // ==========================================
  $("#roleSelect").on("change", function () {
    var role = $(this).val();

    // Tampilkan wrapper data pribadi
    $("#wrapperDataPribadi").slideDown();

    // // Reset Field Role Spesifik
    // $(".group-asesi").hide();
    // $(".group-asesor").hide();

    // // Update Validasi (Hapus required dari elemen hidden)
    // // Kita gunakan jQuery Validate 'ignore: :hidden' secara global nanti

    // if (role === "Asesi") {
    //   $(".group-asesi").show();
    //   $("[name='citizenship']").prop("required", true);
    //   $("[name='noMet']").prop("required", false);
    //   $("#selectPekerjaan").prop("required", false);
    // } else if (role === "Asesor") {
    //   $(".group-asesor").show();
    //   $("[namecitizenship']").prop("required", false);
    //   $("[name='noMet']").prop("required", true); // No MET Wajib Asesor
    //   $("#selectPekerjaan").prop("required", true); // Pekerjaan Wajib Asesor
    // }

    // Reset Field Role Spesifik (Sembunyikan keduanya dulu)
    $(".group-asesi").hide();
    $(".group-asesor").hide();

    // Tampilkan sesuai pilihan
    if (role === "Asesi") {
      $(".group-asesi").show();
      $("[name='citizenship']").prop("required", true);
      $("[name='noMet']").prop("required", false);
      $("#selectPekerjaan").prop("required", false);
    } else if (role === "Asesor") {
      $(".group-asesor").show();
      $("[name='citizenship']").prop("required", false);
      $("[name='noMet']").prop("required", true);
      $("#selectPekerjaan").prop("required", true);
    }
  });

  // ==========================================
  // 3. LOAD DATA (PENDIDIKAN & PEKERJAAN)
  // ==========================================
  // Pendidikan
  fetch("/dist/js/education.json")
    .then((res) => res.json())
    .then((data) => {
      let opts = '<option value="">Pilih Pendidikan...</option>';
      data.forEach(
        (item) => (opts += `<option value="${item.id}">${item.name}</option>`)
      );
      $("#selectPendidikan").html(opts);
    });

  // Pekerjaan
  fetch("/dist/js/jobs.json")
    .then((res) => res.json())
    .then((data) => {
      let opts = '<option value="">Pilih Pekerjaan...</option>';
      data.forEach(
        (item) => (opts += `<option value="${item.id}">${item.name}</option>`)
      );
      $("#selectPekerjaan").html(opts);
    });

  // ==========================================
  // 4. API WILAYAH (CASCADING)
  // ==========================================
  // Load Provinsi
  fetch(`${API_WILAYAH_URL}/provinces.json`)
    .then((res) => res.json())
    .then((data) => {
      let opts = '<option value="">Pilih Provinsi...</option>';
      data.forEach(
        (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
      );
      $("#selectProvinsi").html(opts);
    });

  // Change Provinsi -> Kota
  $("#selectProvinsi").on("change", function () {
    let id = $(this).val();
    $("#inputProvinsi").val(id); // Simpan ID
    $("#selectKota")
      .html('<option value="">Loading...</option>')
      .prop("disabled", true);
    // ... reset child lainnya ...

    if (id) {
      fetch(`${API_WILAYAH_URL}/regencies/${id}.json`)
        .then((res) => res.json())
        .then((data) => {
          let opts = '<option value="">Pilih Kota/Kab...</option>';
          data.forEach(
            (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
          );
          $("#selectKota").html(opts).prop("disabled", false);
        });
    }
  });

  // Change Kota -> Kecamatan
  $("#selectKota").on("change", function () {
    let id = $(this).val();
    $("#inputKota").val(id);
    $("#selectKecamatan")
      .html('<option value="">Loading...</option>')
      .prop("disabled", true);

    if (id) {
      fetch(`${API_WILAYAH_URL}/districts/${id}.json`)
        .then((res) => res.json())
        .then((data) => {
          let opts = '<option value="">Pilih Kecamatan...</option>';
          data.forEach(
            (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
          );
          $("#selectKecamatan").html(opts).prop("disabled", false);
        });
    }
  });

  // Change Kecamatan -> Kelurahan
  $("#selectKecamatan").on("change", function () {
    let id = $(this).val();
    $("#inputKecamatan").val(id);
    $("#selectKelurahan")
      .html('<option value="">Loading...</option>')
      .prop("disabled", true);

    if (id) {
      fetch(`${API_WILAYAH_URL}/villages/${id}.json`)
        .then((res) => res.json())
        .then((data) => {
          let opts = '<option value="">Pilih Kelurahan...</option>';
          data.forEach(
            (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
          );
          $("#selectKelurahan").html(opts).prop("disabled", false);
        });
    }
  });

  // Change Kelurahan
  $("#selectKelurahan").on("change", function () {
    $("#inputKelurahan").val($(this).val());
  });

  // ==========================================
  // 5. MODAL PASSWORD
  // ==========================================
  $("#savePassword").on("click", function () {
    var p1 = $("#modalPass1").val();
    var p2 = $("#modalPass2").val();

    // Validasi Min 8
    if (p1.length < 8) {
      Swal.fire("Error", "Password minimal 8 karakter!", "error");
      return;
    }

    // Validasi Cocok
    if (p1 !== p2) {
      $("#passMismatch").show();
      return;
    } else {
      $("#passMismatch").hide();
    }

    // Simpan ke Hidden Input
    $("#realPassword").val(p1);

    // Update UI
    $("#passwordStatus").addClass("d-none");
    $("#passwordSuccess").removeClass("d-none");
    $("#modalPassword").modal("hide");

    // Trigger validasi ulang form utama
    $("#realPassword").valid();
  });

  // ==========================================
  // 6. TANDA TANGAN (FIX MODAL CLOSE)
  // ==========================================
  var canvas = document.getElementById("signature-canvas");
  if (canvas) {
    var signaturePad = new SignaturePad(canvas, {
      backgroundColor: "rgba(255, 255, 255, 0)",
      penColor: "rgb(0, 0, 0)",
    });

    function resizeCanvas() {
      var ratio = Math.max(window.devicePixelRatio || 1, 1);
      canvas.width = canvas.offsetWidth * ratio;
      canvas.height = canvas.offsetHeight * ratio;
      canvas.getContext("2d").scale(ratio, ratio);
      signaturePad.clear();
    }

    $("#modalSignature").on("shown.bs.modal", function () {
      resizeCanvas();
    });
    $("#btnClear").on("click", function () {
      signaturePad.clear();
    });
    $("#btnUpload").on("click", function () {
      $("#uploadSigFile").click();
    });

    $("#uploadSigFile").on("change", function (e) {
      var file = e.target.files[0];
      if (!file) return;
      var reader = new FileReader();
      reader.onload = function (event) {
        signaturePad.fromDataURL(event.target.result);
      };
      reader.readAsDataURL(file);
    });

    $("#btnSaveSignature").on("click", function () {
      if (signaturePad.isEmpty()) {
        Swal.fire({ icon: "warning", title: "Tanda tangan masih kosong!" });
      } else {
        var dataURL = signaturePad.toDataURL("image/png");
        $("#signatureInput").val(dataURL);

        // Update Tampilan Tombol
        $("#btnTriggerSignature")
          .removeClass("btn-outline-primary")
          .addClass("btn-outline-success")
          .html('<i class="fas fa-eye"></i> Lihat Tanda Tangan');

        $("#imgPreview").attr("src", dataURL);
        $("#signaturePreview").show();

        // PERBAIKAN: Tutup modal secara eksplisit
        $("#modalSignature").modal("hide");

        // Trigger validasi
        $("#signatureInput").valid();
      }
    });
  }

  // ==========================================
  // 7. VALIDASI FORM & SUBMIT (SUPER AMAN)
  // ==========================================
  $("#registerForm").validate({
    ignore: ":hidden:not(#realPassword, #signatureInput)", // Validasi hidden password & signature
    rules: {
      username: {
        required: true,
        minlength: 3,
        remote: {
          url: "/api/check-duplicate",
          type: "GET",
          data: {
            username: function () {
              return $("[name='username']").val();
            },
          },
        },
      },
      email: {
        required: true,
        email: true,
        remote: {
          url: "/api/check-duplicate",
          type: "GET",
          data: {
            email: function () {
              return $("[name='email']").val();
            },
          },
        },
      },
      roles: { required: true },
      fullName: { required: true },
      nik: { required: true, number: true, minlength: 16 },
      // ... rules lain disesuaikan ...
      password: { required: true },
      signatureBase64: { required: true },
    },
    messages: {
      username: { remote: "Username sudah digunakan!" },
      email: { remote: "Email sudah terdaftar!" },
      password: { required: "Silakan buat kata sandi dulu" },
      signatureBase64: { required: "Tanda tangan wajib diisi" },
    },
    errorElement: "span",
    errorPlacement: function (error, element) {
      error.addClass("invalid-feedback");
      if (element.hasClass("select2-hidden-accessible")) {
        error.insertAfter(element.next(".select2"));
      } else if (element.attr("name") == "password") {
        error.insertAfter("#passwordStatus"); // Taruh dibawah status
        error.css("display", "block");
      } else if (element.attr("name") == "signatureBase64") {
        error.insertAfter("#btnTriggerSignature");
        error.css("display", "block");
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
    // SCROLL TO ERROR
    invalidHandler: function (event, validator) {
      var errors = validator.numberOfInvalids();
      if (errors) {
        Swal.fire({
          icon: "error",
          title: "Data Belum Lengkap",
          text: "Mohon periksa kembali form yang berwarna merah.",
          confirmButtonColor: "#d33",
        }).then(() => {
          if (validator.errorList.length > 0) {
            $("html, body").animate(
              {
                scrollTop: $(validator.errorList[0].element).offset().top - 100,
              },
              500
            );
          }
        });
      }
    },
    // SUBMIT AJAX
    submitHandler: function (form) {
      var formData = new FormData(form);

      Swal.fire({
        title: "Mendaftarkan Akun...",
        allowOutsideClick: false,
        didOpen: () => Swal.showLoading(),
      });

      $.ajax({
        url: $(form).attr("action"),
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
          var res =
            typeof response === "string" ? JSON.parse(response) : response;
          Swal.fire({
            icon: "success",
            title: "Berhasil!",
            text: res.message,
            confirmButtonText: "Login Sekarang",
            confirmButtonColor: "#28a745",
          }).then((result) => {
            if (result.isConfirmed) {
              window.location.href = "/login";
            }
          });
        },
        error: function (xhr) {
          var msg = "Terjadi kesalahan server";
          try {
            msg = JSON.parse(xhr.responseText).message;
          } catch (e) {}
          Swal.fire("Gagal!", msg, "error");
        },
      });
    },
  });
});
