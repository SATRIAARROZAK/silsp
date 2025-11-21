// =======================================================================
// JAVASCRIPT UNTUK USERS ADMIN
// =======================================================================
$(document).ready(function () {
  // Inisialisasi plugin
  // bsCustomFileInput.init();
  // $('.select2').select2();

  /**
   * Fungsi untuk validasi format email.
   * @param {string} email - Alamat email yang akan divalidasi.
   * @returns {boolean} - True jika valid, false jika tidak.
   */
  function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  /**
   * Fungsi untuk validasi kekuatan password.
   * @param {string} password - Password yang akan divalidasi.
   * @returns {object} - Mengembalikan objek { isValid: boolean, errors: array }.
   */
  function validatePassword(password) {
    let errors = [];
    if (password.length < 8) {
      errors.push("Minimal 8 karakter.");
    }
    if (!/[a-z]/.test(password)) {
      errors.push("Harus ada huruf kecil.");
    }
    if (!/[A-Z]/.test(password)) {
      errors.push("Harus ada huruf besar.");
    }
    if (!/\d/.test(password)) {
      errors.push("Harus ada angka.");
    }
    return {
      isValid: errors.length === 0,
      errors: errors,
    };
  }

  /**
   * Fungsi utama untuk memvalidasi seluruh form.
   * @returns {object} - Mengembalikan objek { isValid: boolean, firstInvalidElement: jQuery|null }.
   */
  function validateUserForm() {
    let isValid = true;
    let firstInvalidElement = null;
    const form = $("#form-tambah-user");

    form.find(".is-invalid").removeClass("is-invalid");

    form.find("input[required], select[required]").each(function () {
      const input = $(this);
      const feedback = input.closest(".form-group").find(".invalid-feedback");
      let isFieldValid = true;
      let errorMessage = input.data("error");

      if (!input.val() || input.val().trim() === "") {
        isFieldValid = false;
        // errorMessage = "Field ini tidak boleh kosong.";
      } else {
        // Validasi spesifik untuk email
        if (input.attr("id") === "emailAdmin" && !isValidEmail(input.val())) {
          isFieldValid = false;
          errorMessage = "Format email tidak valid (contoh: user@domain.com).";
        }
        // Validasi spesifik untuk password
        if (input.attr("id") === "password") {
          const passwordValidation = validatePassword(input.val());
          if (!passwordValidation.isValid) {
            isFieldValid = false;
            errorMessage = passwordValidation.errors.join(" ");
          }
        }
      }

      if (!isFieldValid) {
        isValid = false;
        input.addClass("is-invalid");
        feedback.text(errorMessage);

        if (input.attr("id") === "role") {
          input
            .next(".select2-container")
            .find(".select2-selection--single")
            .addClass("is-invalid");
        }
        if (!firstInvalidElement) {
          firstInvalidElement = input;
        }
      }
    });

    return { isValid: isValid, firstInvalidElement: firstInvalidElement };
  }

  // --- EVENT HANDLERS ---

  // 1. Saat tombol Simpan (submit) ditekan
  $("#form-tambah-user").on("submit", function (e) {
    e.preventDefault();
    const validationResult = validateUserForm();
    if (validationResult.isValid) {
      Swal.fire("Sukses!", "Data user berhasil disimpan.", "success");
      // this.submit();
    } else {
      if (validationResult.firstInvalidElement) {
        validationResult.firstInvalidElement.focus();
      }
    }
  });

  // 2. Hapus error secara real-time saat pengguna mengisi form
  $("#form-tambah-user").on("input change", ".is-invalid", function () {
    const input = $(this);
    const feedback = input.closest(".form-group").find(".invalid-feedback");
    let isFieldValid = true;

    if (!input.val() || input.val().trim() === "") {
      isFieldValid = false;
    } else {
      if (input.attr("id") === "emailAdmin" && !isValidEmail(input.val())) {
        isFieldValid = false;
      }
      if (input.attr("id") === "password") {
        const passwordValidation = validatePassword(input.val());
        if (!passwordValidation.isValid) {
          isFieldValid = false;
        }
      }
    }

    if (isFieldValid) {
      input.removeClass("is-invalid");
      feedback.text(""); // Kosongkan pesan error
      if (input.hasClass("select2")) {
        input
          .next(".select2-container")
          .find(".select2-selection--single")
          .removeClass("is-invalid");
      }
    }
  });

  //   // 2. Event Listener ketika Role berubah
  //   $("#roleSelect").on("change", function () {
  //     // Ambil value role yang dipilih (hasilnya array string, misal ["ADMIN", "ASESI"])
  //     var selectedRoles = $(this).val();

  //     // --- RESET TAMPILAN (Sembunyikan dulu semua) ---
  //     $("#sectionDataPribadi").hide();
  //     $("#sectionDataPekerjaan").hide();

  //     // Sembunyikan field-field spesifik di dalam Data Pribadi
  //     $(".group-asesi-asesor").hide(); // Class untuk field gabungan Asesi & Asesor
  //     $(".group-asesor-only").hide(); // Class untuk field khusus Asesor
  //     $(".group-asesi-only").hide(); // Class untuk field khusus Asesi

  //     // --- LOGIKA PENAMPILAN ---

  //     // Jika ada role yang dipilih (tidak kosong)
  //     if (selectedRoles && selectedRoles.length > 0) {
  //       // A. Tampilkan Form Data Pribadi Utama (Nama, TTL, NIK, dll) untuk SEMUA role
  //       $("#sectionDataPribadi").slideDown();

  //       // B. Cek apakah ada role ASESI atau ASESOR
  //       var isAsesi = selectedRoles.includes("ASESI");
  //       var isAsesor = selectedRoles.includes("ASESOR");
  //       var isAdmin = selectedRoles.includes("ADMIN");

  //       // Jika Role mengandung ASESI atau ASESOR
  //       if (isAsesi || isAsesor) {
  //         // 1. Tampilkan Form Data Pekerjaan
  //         $("#sectionDataPekerjaan").slideDown();

  //         // 2. Tampilkan Field Gabungan (Alamat Detail, No Telp, Pendidikan)
  //         $(".group-asesi-asesor").show();
  //       }

  //       // Jika Role mengandung ASESI saja
  //       if (isAsesi) {
  //         $(".group-asesi-only").show(); // Kewarganegaraan, Kode Pos
  //       }

  //       // Jika Role mengandung ASESOR saja
  //       if (isAsesor) {
  //         $(".group-asesor-only").show(); // No MET
  //       }

  //       // Catatan: Jika User memilih ADMIN + ASESI, maka field Admin (sederhana)
  //       // tertimpa logika Asesi (lengkap). Ini perilaku yang benar (Superset).
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
});
