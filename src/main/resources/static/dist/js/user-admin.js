// =======================================================================
// JAVASCRIPT UNTUK USERS ADMIN
// =======================================================================
$(document).ready(function () {
  // --- FUNSI VALIDASI ---
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
      let options = '<option value="">Pilih Provinsi...</option>';
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
      .html('<option value="">Pilih Kecamatan...</option>')
      .prop("disabled", true);
    $("#selectKelurahan")
      .html('<option value="">Pilih Kelurahan...</option>')
      .prop("disabled", true);

    if (provId) {
      fetch(`${apiBaseUrl}/regencies/${provId}.json`)
        .then((response) => response.json())
        .then((regencies) => {
          let options = '<option value="">Pilih Kota/Kab...</option>';
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
      .html('<option value="">Pilih Kelurahan...</option>')
      .prop("disabled", true);

    if (cityId) {
      fetch(`${apiBaseUrl}/districts/${cityId}.json`)
        .then((response) => response.json())
        .then((districts) => {
          let options = '<option value="">Pilih Kecamatan...</option>';
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
      let options = '<option value="">Pilih Pendidikan...</option>';
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
      let options = '<option value="">Pilih Pekerjaan...</option>';
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
});
