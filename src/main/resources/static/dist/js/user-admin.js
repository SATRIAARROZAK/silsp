// =======================================================================
// JAVASCRIPT UNTUK USERS ADMIN
// =======================================================================
$(document).ready(function () {
  //   // --- FUNSI VALIDASI ---
  //   /**
  //    * Fungsi untuk validasi format email.
  //    * @param {string} email - Alamat email yang akan divalidasi.
  //    * @returns {boolean} - True jika valid, false jika tidak.
  //    */
  //   function isValidEmail(email) {
  //     const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  //     return emailRegex.test(email);
  //   }

  //   /**
  //    * Fungsi untuk validasi kekuatan password.
  //    * @param {string} password - Password yang akan divalidasi.
  //    * @returns {object} - Mengembalikan objek { isValid: boolean, errors: array }.
  //    */
  //   function validatePassword(password) {
  //     let errors = [];
  //     if (password.length < 8) {
  //       errors.push("Minimal 8 karakter.");
  //     }
  //     if (!/[a-z]/.test(password)) {
  //       errors.push("Harus ada huruf kecil.");
  //     }
  //     if (!/[A-Z]/.test(password)) {
  //       errors.push("Harus ada huruf besar.");
  //     }
  //     if (!/\d/.test(password)) {
  //       errors.push("Harus ada angka.");
  //     }
  //     return {
  //       isValid: errors.length === 0,
  //       errors: errors,
  //     };
  //   }

  //   /**
  //    * Fungsi utama untuk memvalidasi seluruh form.
  //    * @returns {object} - Mengembalikan objek { isValid: boolean, firstInvalidElement: jQuery|null }.
  //    */
  //   function validateUserForm() {
  //     let isValid = true;
  //     let firstInvalidElement = null;
  //     const form = $("#form-tambah-user");

  //     form.find(".is-invalid").removeClass("is-invalid");

  //     form.find("input[required], select[required]").each(function () {
  //       const input = $(this);
  //       const feedback = input.closest(".form-group").find(".invalid-feedback");
  //       let isFieldValid = true;
  //       let errorMessage = input.data("error");

  //       if (!input.val() || input.val().trim() === "") {
  //         isFieldValid = false;
  //         // errorMessage = "Field ini tidak boleh kosong.";
  //       } else {
  //         // Validasi spesifik untuk email
  //         if (input.attr("id") === "emailAdmin" && !isValidEmail(input.val())) {
  //           isFieldValid = false;
  //           errorMessage = "Format email tidak valid (contoh: user@domain.com).";
  //         }
  //         // Validasi spesifik untuk password
  //         if (input.attr("id") === "password") {
  //           const passwordValidation = validatePassword(input.val());
  //           if (!passwordValidation.isValid) {
  //             isFieldValid = false;
  //             errorMessage = passwordValidation.errors.join(" ");
  //           }
  //         }
  //       }

  //       if (!isFieldValid) {
  //         isValid = false;
  //         input.addClass("is-invalid");
  //         feedback.text(errorMessage);

  //         if (input.attr("id") === "role") {
  //           input
  //             .next(".select2-container")
  //             .find(".select2-selection--single")
  //             .addClass("is-invalid");
  //         }
  //         if (!firstInvalidElement) {
  //           firstInvalidElement = input;
  //         }
  //       }
  //     });

  //     return { isValid: isValid, firstInvalidElement: firstInvalidElement };
  //   }

  // --- EVENT HANDLERS ---

  //   // 1. Saat tombol Simpan (submit) ditekan
  //   $("#form-tambah-user").on("submit", function (e) {
  //     e.preventDefault();
  //     const validationResult = validateUserForm();
  //     if (validationResult.isValid) {
  //       Swal.fire("Sukses!", "Data user berhasil disimpan.", "success");
  //       // this.submit();
  //     } else {
  //       if (validationResult.firstInvalidElement) {
  //         validationResult.firstInvalidElement.focus();
  //       }
  //     }
  //   });

  //   // 2. Hapus error secara real-time saat pengguna mengisi form
  //   $("#form-tambah-user").on("input change", ".is-invalid", function () {
  //     const input = $(this);
  //     const feedback = input.closest(".form-group").find(".invalid-feedback");
  //     let isFieldValid = true;

  //     if (!input.val() || input.val().trim() === "") {
  //       isFieldValid = false;
  //     } else {
  //       if (input.attr("id") === "emailAdmin" && !isValidEmail(input.val())) {
  //         isFieldValid = false;
  //       }
  //       if (input.attr("id") === "password") {
  //         const passwordValidation = validatePassword(input.val());
  //         if (!passwordValidation.isValid) {
  //           isFieldValid = false;
  //         }
  //       }
  //     }

  //     if (isFieldValid) {
  //       input.removeClass("is-invalid");
  //       feedback.text(""); // Kosongkan pesan error
  //       if (input.hasClass("select2")) {
  //         input
  //           .next(".select2-container")
  //           .find(".select2-selection--single")
  //           .removeClass("is-invalid");
  //       }
  //     }
  //   });

  // Logic Tampil/Sembunyi Form
  $("#roleSelect").on("change", function () {
    var selectedRoles = $(this).val();

    // 1. Reset: Sembunyikan Wrapper Utama & Field Spesifik
    $("#wrapperDataPribadi").slideUp();
    $("#wrapperDataPekerjaan").slideUp();
    $(".group-asesi-asesor").hide();
    $(".group-asesor-only").hide();
    $(".group-asesi-only").hide();

    // 2. Cek Logic
    if (selectedRoles && selectedRoles.length > 0) {
      // A. Data Pribadi (Nama, Tgl Lahir, NIK, TTD) selalu muncul jika ada role
      $("#wrapperDataPribadi").slideDown();

      var isAsesi = selectedRoles.includes("Asesi");
      var isAsesor = selectedRoles.includes("Asesor");

      // B. Logic Data Pekerjaan & Field Gabungan
      if (isAsesi || isAsesor) {
        $("#wrapperDataPekerjaan").slideDown(); // Bagian 3
        $(".group-asesi-asesor").show(); // Telp, Pendidikan, Detail Alamat
      }

      // C. Logic Spesifik Asesi
      if (isAsesi) {
        $(".group-asesi-only").show(); // Kewarganegaraan, Kode Pos
      }

      // D. Logic Spesifik Asesor
      if (isAsesor) {
        $(".group-asesor-only").show(); // No MET
      }
    }
  });

  // URL API Wilayah Indonesia
  const apiBaseUrl = "https://www.emsifa.com/api-wilayah-indonesia/api";

  // --- 1. FUNGSI FETCH DATA ---

  // Load Provinsi saat halaman siap
  fetch(`${apiBaseUrl}/provinces.json`)
    .then((response) => response.json())
    .then((provinces) => {
      let data = provinces;
      let options = '<option value="">Pilih Provinsi</option>';
      data.forEach((element) => {
        options += `<option value="${element.id}" data-name="${element.name}">${element.name}</option>`;
      });
      $("#selectProvinsi").html(options);
    });

  // --- 2. EVENT LISTENER (CASCADING DROPDOWN) ---

  // Ketika Provinsi Dipilih -> Ambil Kota
  $("#selectProvinsi").on("change", function () {
    const provId = $(this).val();
    const provName = $(this).find(":selected").data("name");

    // Simpan Nama Provinsi ke Input Hidden
    $("#inputProvinsi").val(provName);

    // Reset child dropdowns
    $("#selectKota")
      .html('<option value="">Loading...</option>')
      .prop("disabled", true);
    $("#selectKecamatan")
      .html('<option value="">Pilih Kecamatan</option>')
      .prop("disabled", true);
    $("#selectKelurahan")
      .html('<option value="">Pilih Kelurahan</option>')
      .prop("disabled", true);

    if (provId) {
      fetch(`${apiBaseUrl}/regencies/${provId}.json`)
        .then((response) => response.json())
        .then((regencies) => {
          let options = '<option value="">Pilih Kota/Kab</option>';
          regencies.forEach((element) => {
            options += `<option value="${element.id}" data-name="${element.name}">${element.name}</option>`;
          });
          $("#selectKota").html(options).prop("disabled", false);
        });
    }
  });

  // Ketika Kota Dipilih -> Ambil Kecamatan
  $("#selectKota").on("change", function () {
    const cityId = $(this).val();
    const cityName = $(this).find(":selected").data("name");

    $("#inputKota").val(cityName);

    $("#selectKecamatan")
      .html('<option value="">Loading...</option>')
      .prop("disabled", true);
    $("#selectKelurahan")
      .html('<option value="">Pilih Kelurahan</option>')
      .prop("disabled", true);

    if (cityId) {
      fetch(`${apiBaseUrl}/districts/${cityId}.json`)
        .then((response) => response.json())
        .then((districts) => {
          let options = '<option value="">Pilih Kecamatan</option>';
          districts.forEach((element) => {
            options += `<option value="${element.id}" data-name="${element.name}">${element.name}</option>`;
          });
          $("#selectKecamatan").html(options).prop("disabled", false);
        });
    }
  });

  // Ketika Kecamatan Dipilih -> Ambil Kelurahan
  $("#selectKecamatan").on("change", function () {
    const distId = $(this).val();
    const distName = $(this).find(":selected").data("name");

    $("#inputKecamatan").val(distName);

    $("#selectKelurahan")
      .html('<option value="">Loading...</option>')
      .prop("disabled", true);

    if (distId) {
      fetch(`${apiBaseUrl}/villages/${distId}.json`)
        .then((response) => response.json())
        .then((villages) => {
          let options = '<option value="">Pilih Kelurahan...</option>';
          villages.forEach((element) => {
            options += `<option value="${element.id}" data-name="${element.name}">${element.name}</option>`;
          });
          $("#selectKelurahan").html(options).prop("disabled", false);
        });
    }
  });

  // Ketika Kelurahan Dipilih -> Simpan Nama Kelurahan
  $("#selectKelurahan").on("change", function () {
    const subDistName = $(this).find(":selected").data("name");
    $("#inputKelurahan").val(subDistName);
  });

  // --- LOAD DATA JSON STATIS (Pendidikan & Pekerjaan) ---

  // 1. Load Pendidikan
  fetch("/dist/js/education.json")
    .then((response) => response.json())
    .then((data) => {
      let options = '<option value="">Pilih Pendidikan</option>';
      data.forEach((item) => {
        // Menyimpan nama di atribut data-name
        options += `<option value="${item.id}" data-name="${item.name}">${item.name}</option>`;
      });
      $("#selectPendidikan").html(options);
    })
    .catch((error) => console.error("Error loading education:", error));

  // 2. Load Pekerjaan
  fetch("/dist/js/jobs.json")
    .then((response) => response.json())
    .then((data) => {
      let options = '<option value="">Pilih Pekerjaan</option>';
      data.forEach((item) => {
        options += `<option value="${item.id}" data-name="${item.name}">${item.name}</option>`;
      });
      $("#selectPekerjaan").html(options);
    })
    .catch((error) => console.error("Error loading jobs:", error));

  // --- EVENT LISTENER UNTUK MENYIMPAN NAMA KE INPUT HIDDEN ---

  // Saat Pendidikan dipilih
  $("#selectPendidikan").on("change", function () {
    // Ambil 'data-name' dari option yang dipilih
    const namaPendidikan = $(this).find(":selected").data("name");
    // Masukkan ke input hidden agar terkirim ke server
    $("#inputPendidikan").val(namaPendidikan);
  });

  // Saat Pekerjaan dipilih
  $("#selectPekerjaan").on("change", function () {
    const namaPekerjaan = $(this).find(":selected").data("name");
    $("#inputPekerjaan").val(namaPekerjaan);
  });

  // --- 1. INISIALISASI VARIABLE ---
  var wrapper = document.getElementById("signature-pad");
  var canvas = document.getElementById("signature-canvas");
  var signaturePad;

  // --- 2. FUNGSI RESIZE CANVAS (Agar tidak pecah/blur) ---
  function resizeCanvas() {
    var ratio = Math.max(window.devicePixelRatio || 1, 1);
    // Set dimensi canvas sesuai ukuran layar
    canvas.width = canvas.offsetWidth * ratio;
    canvas.height = canvas.offsetHeight * ratio;
    canvas.getContext("2d").scale(ratio, ratio);

    // Jika sudah ada signaturePad, clear dulu agar bersih saat resize ulang
    if (signaturePad) {
      // signaturePad.clear(); // Opsional: Hapus atau biarkan (biasanya clear saat resize)
    }
  }

  // --- 3. EVENT SAAT MODAL DIBUKA ---
  $("#modalSignature").on("shown.bs.modal", function () {
    // Resize canvas saat modal muncul (PENTING! Jika tidak, canvas akan error size-nya)
    resizeCanvas();

    var existingSignature = $("#signatureInput").val();

    // Jika sudah ada tanda tangan tersimpan (Base64), muat ke canvas
    if (existingSignature && existingSignature.trim() !== "") {
      signaturePad.fromDataURL(existingSignature, { ratio: 1.5 });
    }

    // Inisialisasi SignaturePad
    if (!signaturePad) {
      signaturePad = new SignaturePad(canvas, {
        backgroundColor: "rgba(255, 255, 255, 0)", // Transparan
        penColor: "rgb(0, 0, 0)", // Warna Tinta Hitam
      });
    }
  });

  // --- 1. SAAT MODAL DIBUKA (Load Tanda Tangan Lama) ---
  //   $("#modalSignature").on("shown.bs.modal", function () {
  //     resizeCanvas(); // Atur ukuran dulu

  //     var existingSignature = $("#signatureInput").val();

  //     // Jika sudah ada tanda tangan tersimpan (Base64), muat ke canvas
  //     if (existingSignature && existingSignature.trim() !== "") {
  //       signaturePad.fromDataURL(existingSignature, { ratio: 1 });
  //     }
  //   });

  // --- 4. TOMBOL HAPUS KANVAS ---
  $("#btnClear").on("click", function () {
    if (signaturePad) {
      signaturePad.clear();
    }
  });

  // --- 3. UPLOAD PNG ---
  $("#btnUpload").on("click", function () {
    $("#uploadSigFile").click();
  });

  $("#uploadSigFile").on("change", function (e) {
    var file = e.target.files[0];

    if (!file) return;

    // Validasi Tipe File (Hanya PNG)
    if (file.type !== "image/png") {
      Swal.fire({
        icon: "error",
        title: "Hanya file format PNG yang diperbolehkan.",
      });
      this.value = ""; // Reset input
      return;
    }

    var reader = new FileReader();
    reader.onload = function (event) {
      // Fitur hebat SignaturePad: bisa load langsung dari Data URL
      // Ini otomatis menggambar image ke canvas
      signaturePad.fromDataURL(event.target.result);
    };
    reader.readAsDataURL(file);
  });

  // --- 5. TOMBOL UPLOAD SIGNATURE (Upload Gambar ke Canvas) ---
  //   $("#btnUpload").on("click", function () {
  //     $("#uploadSigFile").click(); // Trigger input file tersembunyi
  //   });

  //   // Saat file dipilih
  //   $("#uploadSigFile").on("change", function (e) {
  //     var file = e.target.files[0];
  //     if (file) {
  //       var reader = new FileReader();
  //       reader.onload = function (event) {
  //         var img = new Image();
  //         img.onload = function () {
  //           // Gambar image ke canvas
  //           var ctx = canvas.getContext("2d");
  //           // Bersihkan dulu
  //           signaturePad.clear();
  //           // Draw image (fit to canvas)
  //           // Rasio aspek gambar agar tidak gepeng bisa ditambahkan di sini jika mau kompleks
  //           // Untuk simpelnya kita draw full canvas dengan padding sedikit
  //           ctx.drawImage(img, 20, 20, 600, 200);
  //         };
  //         img.src = event.target.result;
  //       };
  //       reader.readAsDataURL(file);
  //     }
  //   });

  // --- 6. TOMBOL SIMPAN ---
  $("#btnSaveSignature").on("click", function () {
    if (signaturePad.isEmpty()) {
      Swal.fire({
        icon: "warning",
        title: "Silakan tanda tangan atau upload gambar terlebih dahulu!",
      });
    } else {
      try {
        // A. Ambil data Base64 dari canvas
        var dataURL = signaturePad.toDataURL("image/png");

        // B. Simpan ke Input Hidden
        $("#signatureInput").val(dataURL);

        // C. Ubah Tampilan Tombol Pemicu
        var btnTrigger = $("#btnTriggerSignature");
        btnTrigger
          .removeClass("btn-outline-primary")
          .addClass("btn-outline-success");
        btnTrigger.html('<i class="fas fa-eye"></i> Lihat Tanda Tangan');

        // D. Tampilkan Preview (Pastikan elemen imgPreview ada di HTML)
        $("#imgPreview").attr("src", dataURL);
        $("#signaturePreview").show();

        // --- SOLUSI PERBAIKAN DISINI ---
        // Cara 1: Cara Standar
        // $('#modalSignature').modal('hide');

        // Cara 2: Cara Paksa (Simulasi Klik Tombol Close)
        // Ini mencari tombol apa saja di dalam modal yang punya fungsi tutup, lalu di-klik otomatis
        $("#modalSignature")
          .find('[data-dismiss="modal"]')
          .first()
          .trigger("click");
      } catch (error) {
        console.error("Terjadi error saat menyimpan: ", error);
      }
    }
  });

  // --- KONFIGURASI VALIDASI FORM ---
  $("#formTambahUser").validate({
    // Abaikan field yang sedang disembunyikan (Logic Role Hide/Show aman)
    ignore: ":hidden",

    // 1. ATURAN VALIDASI
    rules: {
      username: { required: true },
      email: {
        required: true,
        email: true, // Memaksa format @domain.com
      },
      roles: { required: true },
      // Tambahkan field lain sesuai name-nya jika perlu spesifik
      // Field yang punya attr 'required' di HTML otomatis ter-cover
    },

    // 2. PESAN ERROR
    messages: {
      // Default untuk semua field 'required'
      required: "Isilah Form Ini!",

      email: {
        required: "Isilah Form Ini!",
        email:
          "Harap gunakan format email yang benar (contoh: user@domain.com)",
      },
      username: "Isilah Form Ini!",
      roles: "Isilah Form Ini!",
    },

    // 3. PENGATURAN TAMPILAN (BOOTSTRAP 4)
    errorElement: "span",
    errorPlacement: function (error, element) {
      error.addClass("invalid-feedback"); // Class bootstrap untuk teks error merah

      // Logic penempatan pesan error
      if (
        element.hasClass("select2") ||
        element.hasClass("select2-hidden-accessible")
      ) {
        // Khusus Select2, taruh error setelah elemen span select2
        error.insertAfter(element.next(".select2"));
      } else {
        // Input biasa
        element.closest(".form-group").append(error);
      }
    },

    // Saat Error (Kosong/Salah) -> Border Merah
    highlight: function (element, errorClass, validClass) {
      $(element).addClass("is-invalid").removeClass("is-valid");

      // Khusus Select2 border fix
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .addClass("is-invalid-border");
      }
    },

    // Saat Sukses (Terisi Benar) -> Border Hijau (Standar)
    // Jika Anda MAU MERAH SAAT SUKSES (Sesuai request), ubah 'is-valid' jadi 'is-invalid'
    // tapi saya sarankan tetap 'is-valid' (hijau) agar user tidak bingung.
    unhighlight: function (element, errorClass, validClass) {
      $(element).removeClass("is-invalid").addClass("is-valid");

      // Khusus Select2
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .removeClass("is-invalid-border");
      }
    },
  });
});
