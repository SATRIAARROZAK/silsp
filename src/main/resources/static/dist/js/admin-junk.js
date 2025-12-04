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

  // ---------------------------------------------------
  // 1. LOGIKA CHANGE ROLE (Admin/Asesi/Asesor)
  // ---------------------------------------------------
  $("#roleSelect").on("change", function () {
    var selectedRoles = $(this).val() || [];

    // --- RESET TAMPILAN AWAL ---
    // Sembunyikan Section Utama
    $("#wrapperDataPribadi").slideUp();
    $("#sectionDataPekerjaan").slideUp();
    // Sembunyikan Detail Instansi (Form Bawah)
    $("#wrapperDetailInstansi").slideUp();

    // Sembunyikan field-field spesifik di Data Pribadi
    $(".group-asesi-asesor").hide();
    $(".group-asesor-only").hide();
    $(".group-asesi-only").hide();

    // --- LOGIKA UTAMA ---
    if (selectedRoles.length > 0) {
      // A. Data Pribadi selalu muncul jika ada role
      $("#wrapperDataPribadi").slideDown();

      var isAsesi = selectedRoles.includes("Asesi");
      var isAsesor = selectedRoles.includes("Asesor");

      // B. Handle Field Data Pribadi (Sama seperti sebelumnya)
      if (isAsesi || isAsesor) {
        $(".group-asesi-asesor").show(); // Telp, Pendidikan, dll
      }
      if (isAsesi) $(".group-asesi-only").show();
      if (isAsesor) $(".group-asesor-only").show();

      // C. HANDLE DATA PEKERJAAN (Poin Inti Request Anda)

      // Kondisi 1: Jika ASESI atau ASESOR, Tampilkan Section Utama (Dropdown Jenis Pekerjaan)
      if (isAsesi || isAsesor) {
        $("#sectionDataPekerjaan").slideDown();
      }

      // Kondisi 2: Logika Detail Instansi
      if (isAsesi) {
        // Jika ASESI, kita cek dropdown pekerjaannya untuk tentukan form detail
        // Trigger manual agar logika 'change' dropdown jalan saat ganti role
        $("#selectPekerjaan").trigger("change");
      } else if (isAsesor && !isAsesi) {
        // Jika HANYA ASESOR (Bukan Asesi), Pastikan Detail Instansi Hilang
        // Sesuai request: "Asesor data pekerjaan hanya menampilkan column jenis pekerjaan"
        $("#wrapperDetailInstansi").slideUp();
        $("#inputCompanyName").prop("required", false); // Matikan validasi
      }
    }
  });

  // ---------------------------------------------------
  // 2. LOGIKA CHANGE JENIS PEKERJAAN
  // ---------------------------------------------------
  $("#selectPekerjaan").on("change", function () {
    var jobId = $(this).val(); // ID pekerjaan (1 = Tidak Bekerja)
    var jobName = $(this).find(":selected").data("name");
    $("#inputPekerjaan").val(jobName); // Simpan nama ke input hidden

    // Cek Role User Saat Ini
    var currentRoles = $("#roleSelect").val() || [];
    var isAsesi = currentRoles.includes("Asesi");

    // KITA HANYA PROSES DETAIL INSTANSI JIKA DIA ADALAH 'ASESI'
    if (isAsesi) {
      // Jika ID = 1 (Belum/Tidak Bekerja) ATAU Kosong
      if (jobId === "1" || jobId === "") {
        // Sembunyikan Form Detail
        $("#wrapperDetailInstansi").slideUp();

        // Hapus Validasi Required
        $("#inputCompanyName").prop("required", false);

        // (Opsional) Reset nilai input jika user berubah pikiran
        $("#wrapperDetailInstansi").find("input, textarea").val("");
      } else {
        // Jika Bekerja
        $("#wrapperDetailInstansi").slideDown();

        // Hanya Nama Instansi yang WAJIB
        $("#inputCompanyName").prop("required", true);

        // Sisanya (Jabatan, Email, dll) biarkan default (Opsional)
        // Tidak perlu coding apa-apa karena di HTML tidak ada 'required'
      }
    } else {
      // Jika BUKAN Asesi (Misal murni Asesor), Detail tetap sembunyi
      $("#wrapperDetailInstansi").slideUp();
      $("#inputCompanyName").prop("required", false);
    }
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
      Swal.fire({ icon: "warning", title: "Silakan tanda tangan dulu!" });
    } else {
      try {
        var dataURL = signaturePad.toDataURL("image/png");
        $("#signatureInput").val(dataURL);

        // --- BARIS BARU: PAKSA VALIDASI ULANG ---
        // Agar pesan error "Isilah Form Ini!" langsung hilang saat user klik simpan
        $("#signatureInput").valid();
        // ----------------------------------------

        // ... (Sisa kode update tampilan tombol & close modal tetap sama) ...
        var btnTrigger = $("#btnTriggerSignature");
        btnTrigger
          .removeClass("btn-outline-primary btn-outline-danger")
          .addClass("btn-outline-success");
        btnTrigger.html('<i class="fas fa-eye"></i> Lihat Tanda Tangan');

        $("#imgPreview").attr("src", dataURL);
        $("#signaturePreview").show();

        $("#modalSignature")
          .find('[data-dismiss="modal"]')
          .first()
          .trigger("click");
      } catch (error) {
        console.error(error);
      }
    }
  });

  // --- 1. GLOBAL OVERRIDE PESAN ERROR (PENTING) ---
  // Kode ini mengubah pesan default "This field is required" menjadi "Isilah Form Ini!"
  // untuk SEMUA field di halaman ini sekaligus.
  $.extend($.validator.messages, {
    required: "Isilah Form Ini!",
    email: "Harap gunakan format email yang benar (contoh: user@domain.com)",
    url: "Harap masukkan URL yang valid.",
    date: "Harap masukkan tanggal yang valid.",
    number: "Harap masukkan angka yang valid.",
    digits: "Hanya boleh angka.",
  });

  // --- 2. KONFIGURASI VALIDASI FORM ---

  // ... (Kode Global Override $.validator.messages TETAP ADA) ...
  // ... (Kode Logic Role & API Wilayah TETAP ADA) ...

  // --- 1. EVENT LISTENER AGAR VALIDASI LANGSUNG JALAN (REAL-TIME) ---
  // Masalah Select2: Validasi tidak otomatis hilang/muncul saat user memilih opsi.
  // Solusi: Kita paksa validasi saat event 'change'.
  $(".select2").on("change", function () {
    $(this).valid(); // Memicu pengecekan ulang pada elemen ini
  });

  // ... (Kode Global Override Message $.extend tetap ada) ...
  // ... (Kode Signature Pad tetap ada) ...

  // ==========================================
  // 1. KONFIGURASI VALIDASI (DIPERBARUI)
  // ==========================================
  $("#formTambahUser").validate({
    // LOGIKA IGNORE: Jangan validasi elemen yang benar-benar tersembunyi
    ignore: function (index, element) {
      // A. Tanda Tangan (Jangan ignore walau hidden)
      if ($(element).attr("id") === "signatureInput") {
        return false;
      }

      // B. Select2 (Cek apakah container dropdown-nya terlihat?)
      if ($(element).hasClass("select2-hidden-accessible")) {
        // Jika container Select2 terlihat, maka elemen ini WAJIB divalidasi
        // Jika container tersembunyi (misal karena Role logic), maka ignore.
        return $(element).next(".select2-container").is(":hidden");
      }

      // C. Default: Ignore elemen lain yang hidden (CSS display:none)
      return $(element).is(":hidden");
    },

    // RULES EKSPLISIT (INI KUNCINYA)
    // Dengan menulis disini, validasi akan tetap jalan walau awalnya disabled
    rules: {
      email: { required: true, email: true },
      username: { required: true },
      fullName: { required: true },

      // --- VALIDASI WILAYAH ---
      province: { required: true },
      city: { required: true }, // Wajibkan Kota
      district: { required: true }, // Wajibkan Kecamatan
      subDistrict: { required: true }, // Wajibkan Kelurahan

      // --- VALIDASI PEKERJAAN ---
      jobType: { required: true }, // Wajibkan Jenis Pekerjaan

      signatureBase64: { required: true },
    },

    // ERROR PLACEMENT (Posisi Pesan Error)
    errorElement: "span",
    errorPlacement: function (error, element) {
      error.addClass("invalid-feedback");

      if (
        element.hasClass("select2") ||
        element.hasClass("select2-hidden-accessible")
      ) {
        // Taruh error di bawah dropdown Select2
        error.insertAfter(element.next(".select2"));
      } else if (element.attr("id") === "signatureInput") {
        error.insertAfter("#btnTriggerSignature");
        error.css("display", "block");
      } else {
        element.closest(".form-group").append(error);
      }
    },

    // HIGHLIGHT (Border Merah)
    highlight: function (element) {
      $(element).addClass("is-invalid").removeClass("is-valid");
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .addClass("is-invalid-border");
      }
      // Khusus Tanda Tangan
      if ($(element).attr("id") === "signatureInput") {
        $("#btnTriggerSignature")
          .addClass("btn-outline-danger")
          .removeClass("btn-outline-success btn-outline-primary");
      }
    },

    // UNHIGHLIGHT (Hapus Border Merah)
    unhighlight: function (element) {
      $(element).removeClass("is-invalid").addClass("is-valid");
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .removeClass("is-invalid-border");
      }
      if ($(element).attr("id") === "signatureInput") {
        $("#btnTriggerSignature").removeClass("btn-outline-danger");
      }
    },
  });

  // Trigger Validasi Saat Select2 Berubah (Agar error langsung hilang saat dipilih)
  $(".select2").on("change", function () {
    $(this).valid();
  });

  // ==========================================
  // 2. LOGIKA API WILAYAH (DIPERBARUI)
  // ==========================================
  const apiBaseUrl = "https://www.emsifa.com/api-wilayah-indonesia/api";

  // Load Provinsi
  fetch(`${apiBaseUrl}/provinces.json`)
    .then((response) => response.json())
    .then((provinces) => {
      let options = '<option value="">Pilih Provinsi...</option>';
      provinces.forEach((el) => {
        options += `<option value="${el.id}" data-name="${el.name}">${el.name}</option>`;
      });
      $("#selectProvinsi").html(options);
    });

  // Logic Provinsi -> Kota
  $("#selectProvinsi").on("change", function () {
    const provId = $(this).val();
    $("#inputProvinsi").val($(this).find(":selected").data("name"));

    // RESET CHILD (PENTING: Hapus class is-invalid agar bersih saat reset)
    $("#selectKota")
      .html('<option value="">Pilih Kota/Kab...</option>')
      .prop("disabled", true)
      .removeClass("is-invalid")
      .next(".select2")
      .find(".select2-selection")
      .removeClass("is-invalid-border");
    $("#selectKecamatan")
      .html('<option value="">Pilih Kecamatan...</option>')
      .prop("disabled", true)
      .removeClass("is-invalid")
      .next(".select2")
      .find(".select2-selection")
      .removeClass("is-invalid-border");
    $("#selectKelurahan")
      .html('<option value="">Pilih Kelurahan...</option>')
      .prop("disabled", true)
      .removeClass("is-invalid")
      .next(".select2")
      .find(".select2-selection")
      .removeClass("is-invalid-border");

    if (provId) {
      fetch(`${apiBaseUrl}/regencies/${provId}.json`)
        .then((response) => response.json())
        .then((data) => {
          let options = '<option value="">Pilih Kota/Kab...</option>'; // Value kosong PENTING untuk validasi
          data.forEach((el) => {
            options += `<option value="${el.id}" data-name="${el.name}">${el.name}</option>`;
          });
          // Buka disabled
          $("#selectKota").html(options).prop("disabled", false);
        });
    }
  });

  // Logic Kota -> Kecamatan
  $("#selectKota").on("change", function () {
    const cityId = $(this).val();
    $("#inputKota").val($(this).find(":selected").data("name"));

    $("#selectKecamatan")
      .html('<option value="">Pilih Kecamatan...</option>')
      .prop("disabled", true)
      .removeClass("is-invalid")
      .next(".select2")
      .find(".select2-selection")
      .removeClass("is-invalid-border");
    $("#selectKelurahan")
      .html('<option value="">Pilih Kelurahan...</option>')
      .prop("disabled", true)
      .removeClass("is-invalid")
      .next(".select2")
      .find(".select2-selection")
      .removeClass("is-invalid-border");

    if (cityId) {
      fetch(`${apiBaseUrl}/districts/${cityId}.json`)
        .then((res) => res.json())
        .then((data) => {
          let options = '<option value="">Pilih Kecamatan...</option>';
          data.forEach((el) => {
            options += `<option value="${el.id}" data-name="${el.name}">${el.name}</option>`;
          });
          $("#selectKecamatan").html(options).prop("disabled", false);
        });
    }
  });

  // Logic Kecamatan -> Kelurahan
  $("#selectKecamatan").on("change", function () {
    const distId = $(this).val();
    $("#inputKecamatan").val($(this).find(":selected").data("name"));

    $("#selectKelurahan")
      .html('<option value="">Pilih Kelurahan...</option>')
      .prop("disabled", true)
      .removeClass("is-invalid")
      .next(".select2")
      .find(".select2-selection")
      .removeClass("is-invalid-border");

    if (distId) {
      fetch(`${apiBaseUrl}/villages/${distId}.json`)
        .then((res) => res.json())
        .then((data) => {
          let options = '<option value="">Pilih Kelurahan...</option>';
          data.forEach((el) => {
            options += `<option value="${el.id}" data-name="${el.name}">${el.name}</option>`;
          });
          $("#selectKelurahan").html(options).prop("disabled", false);
        });
    }
  });

  // Simpan nama kelurahan
  $("#selectKelurahan").on("change", function () {
    $("#inputKelurahan").val($(this).find(":selected").data("name"));
  });

  // CSS Tambahan (Inject via JS agar praktis)
  // Agar border Select2 menjadi merah saat error
  $(
    "<style>.is-invalid-border { border-color: #dc3545 !important; }</style>"
  ).appendTo("head");
});


  // --- TOMBOL SIMPAN (SUBMIT FORM) ---
  //   $("#form-tambah-skema").on("submit", function (e) {
  //     e.preventDefault(); // Mencegah reload

  //     let isAllTabsValid = true;
  //     let firstInvalidTabId = null;
  //     let elementToFocus = null;

  //     // 1. VALIDASI TIAP TAB (Logic Anda yang sudah ada tetap dipakai)
  //     $(".tab-pane").each(function () {
  //       const tab = $(this);
  //       const validationResult = validateTab(tab);
  //       if (!validationResult.isValid && isAllTabsValid) {
  //         isAllTabsValid = false;
  //         firstInvalidTabId = tab.attr("id");
  //         elementToFocus = validationResult.firstInvalidElement;
  //       }
  //     });

  //     // 2. JIKA VALID, KIRIM VIA AJAX
  //     if (isAllTabsValid) {
  //       // Persiapkan FormData (Untuk File + Input Text + Array)
  //       var formData = new FormData(this);

  //       // SweetAlert Loading
  //       Swal.fire({
  //         title: "Menyimpan Data...",
  //         text: "Mohon tunggu sebentar",
  //         allowOutsideClick: false,
  //         didOpen: () => {
  //           Swal.showLoading();
  //         },
  //       });

  //       // AJAX Request
  //       $.ajax({
  //         url: $(this).attr("action"), // Mengambil url dari th:action form
  //         type: "POST",
  //         data: formData,
  //         processData: false, // Wajib false untuk FormData
  //         contentType: false, // Wajib false untuk FormData
  //         success: function (response) {
  //           // Parse response jika string
  //           var res =
  //             typeof response === "string" ? JSON.parse(response) : response;

  //           // Hapus localStorage
  //           localStorage.removeItem("skemaFormData");

  //           Swal.fire({
  //             title: "Sukses!",
  //             text: res.message,
  //             icon: "success",
  //           }).then(() => {
  //             // Redirect ke halaman list skema
  //             window.location.href = "/admin/skema";
  //           });
  //         },
  //         error: function (xhr) {
  //           var errorMsg = "Terjadi kesalahan server";
  //           try {
  //             var json = JSON.parse(xhr.responseText);
  //             errorMsg = json.message;
  //           } catch (e) {}

  //           Swal.fire("Gagal", errorMsg, "error");
  //         },
  //       });
  //     } else {
  //       // Logic Fokus ke Tab yang Error (Sudah benar di kode Anda)
  //       if (firstInvalidTabId) {
  //         $('.nav-tabs a[href="#' + firstInvalidTabId + '"]').tab("show");
  //         setTimeout(() => {
  //           if (elementToFocus) {
  //             if (elementToFocus.hasClass("summernote-persyaratan")) {
  //               elementToFocus.summernote("focus");
  //             } else {
  //               elementToFocus.focus();
  //             }
  //           }
  //         }, 250);
  //       }
  //       Toast.fire({ icon: "error", title: "Harap lengkapi semua data wajib!" });
  //     }
  //   });


  <!DOCTYPE html>
<html
  lang="en"
  xmlns:th="http://www.thymeleaf.org"
  xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
  layout:decorate="~{layouts/app}"
>
  <head th:replace="~{fragments/header :: header('Detail Skema')}"></head>

  <body>
    <div layout:fragment="content">
      <div class="container-fluid">
        <div class="row">
          <div class="col-12">
            <div class="card card-secondary">
              <div class="card-header">
                <h3 class="card-title">1. Detail Skema Sertifikasi</h3>
                <div class="card-tools">
                  <button
                    type="button"
                    class="btn btn-tool"
                    data-card-widget="collapse"
                    title="Collapse"
                  >
                    <i class="fas fa-minus"></i>
                  </button>
                </div>
              </div>

              <div class="card-body">
                <div class="form-group">
                  <label for="namaSkema">Nama Skema</label>
                  <input
                    type="text"
                    class="form-control bg-white"
                    th:value="${skema.name}"
                    readonly
                    disabled
                  />
                </div>
                <div class="row">
                  <div class="col-md-3">
                    <div class="form-group">
                      <label for="kodeSkema">Kode Skema</label>
                      <input
                        type="text"
                        class="form-control bg-white"
                        th:value="${skema.code}"
                        readonly
                        disabled
                      />
                    </div>
                  </div>
                  <div class="col-md-3">
                    <div class="form-group">
                      <label for="exampleInputLevel">Level</label>
                      <input
                        type="text"
                        type="number"
                        class="form-control bg-white"
                        th:value="${skema.level}"
                        readonly
                        disabled
                      />
                    </div>
                  </div>
                  <div class="col-md-3">
                    <div class="form-group">
                      <label for="exampleInputLevel">No. SKKNI</label>
                      <input
                        type="text"
                        class="form-control bg-white"
                        th:value="${skema.noSkkni}"
                        readonly
                        disabled
                      />
                    </div>
                  </div>
                  <div class="col-md-3">
                    <div class="form-group">
                      <label for="exampleInputLevel">Tahun SKKNI</label>
                      <input
                        type="text"
                        class="form-control bg-white"
                        th:value="${skema.skkniYear}"
                        readonly
                        disabled
                      />
                    </div>
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-6">
                    <div class="form-group">
                      <label>Jenis Skema</label>
                      <input
                        type="text"
                        class="form-control bg-white"
                        th:value="${skema.schemaType?.name ?: '-'}"
                        readonly
                        disabled
                      />
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="form-group">
                      <label>Mode Skema</label>
                      <input
                        type="text"
                        class="form-control bg-white"
                        th:value="${skema.schemaMode?.name ?: '-'}"
                        readonly
                        disabled
                      />
                    </div>
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-6">
                    <div class="form-group">
                      <label>Tanggal Penetapan</label>
                      <input
                        type="text"
                        class="form-control bg-white"
                        th:value="${#temporals.format(skema.establishmentDate, 'dd MMMM yyyy')}"
                        readonly
                        disabled
                      />
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="form-group">
                      <label>File Dokumen</label>
                      <ul class="list-unstyled mt-2">
                        <li th:if="${skema.documentPath}">
                          <a
                            th:href="@{'/uploads/skema/' + ${skema.documentPath}}"
                            target="_blank"
                            class="btn-link text-primary font-weight-bold"
                            th:download="${skema.name} + '.pdf'"
                          >
                            <i class="far fa-fw fa-file-pdf mr-1"></i>

                            <span th:text="${skema.name} + '.pdf'"
                              >NamaSkema.pdf</span
                            >
                          </a>
                        </li>
                        <li
                          th:unless="${skema.documentPath}"
                          class="text-muted font-italic"
                        >
                          Tidak ada file diupload.
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
              <!-- /.card-body -->
            </div>
            <!-- /.card -->

            <div class="card card-secondary">
              <div class="card-header">
                <h3 class="card-title">2. Unit Skema</h3>
                <div class="card-tools">
                  <button
                    type="button"
                    class="btn btn-tool"
                    data-card-widget="collapse"
                    title="Collapse"
                  >
                    <i class="fas fa-minus"></i>
                  </button>
                </div>
              </div>

              <div class="card-body">
                <div class="row">
                  <div class="table-responsive">
                    <table
                      class="table table-valign-middle table-sm table-bordered nested-table"
                    >
                      <thead class="thead-light">
                        <tr class="text-center">
                          <th>No</th>
                          <th>Kode Unit</th>
                          <th>Judul Unit</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr th:each="unit, iter : ${skema.units}">
                          <td class="text-center" th:text="${iter.count}">1</td>
                          <td
                            class="text-center font-weight-bold text-dark"
                            th:text="${unit.code}"
                          >
                            Kode
                          </td>
                          <td th:text="${unit.title}">Judul</td>
                        </tr>
                        <tr th:if="${#lists.isEmpty(skema.units)}">
                          <td
                            colspan="3"
                            class="text-center text-muted font-italic py-3"
                          >
                            Tidak ada unit kompetensi.
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
              <!-- /.card-body -->
            </div>
            <!-- /.card -->

            <div class="card card-secondary">
              <div class="card-header">
                <h3 class="card-title">3. Persyaratan Dasar</h3>
                <div class="card-tools">
                  <button
                    type="button"
                    class="btn btn-tool"
                    data-card-widget="collapse"
                    title="Collapse"
                  >
                    <i class="fas fa-minus"></i>
                  </button>
                </div>
              </div>

              <div class="card-body">
                <div class="row">
                  <div class="table-responsive">
                    <table
                      class="table table-valign-middle table-sm table-bordered nested-table"
                    >
                      <thead class="thead-light text-center">
                        <tr>
                          <th>No</th>
                          <th>Daftar Persyaratan</th>
                        </tr>
                      </thead>
                      <tbody>
                        <tr th:each="req, iter : ${skema.requirements}">
                          <td
                            class="text-center align-middle"
                            th:text="${iter.count}"
                          >
                            1
                          </td>
                          <td
                            class="align-middle"
                            th:utext="${req.description}"
                          >
                            Persyaratan
                          </td>
                        </tr>
                        <tr th:if="${#lists.isEmpty(skema.requirements)}">
                          <td
                            colspan="2"
                            class="text-center text-muted font-italic py-3"
                          >
                            Tidak ada persyaratan dasar.
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
              <!-- /.card-body -->
            </div>
            <!-- /.card -->
            <div class="card-footer mt-3 text-left">
              <a
                th:href="@{'/admin/skema/edit/' + ${skema.id}}"
                class="btn btn-warning px-4"
              >
                <i class="fas fa-edit mr-1"></i> Edit
              </a>
              <a th:href="@{/admin/skema}" class="btn btn-outline-info"
                >Batal</a
              >
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>


<a
                            th:href="@{'/uploads/skema/' + ${skema.documentPath}}"
                            target="_blank"
                            class="btn-link text-primary font-weight-bold"
                            th:download="${skema.name} + '.pdf'"
                          >
                            <i class="far fa-fw fa-file-pdf mr-1"></i>

                            <span th:text="${skema.name} + '.pdf'"
                              >NamaSkema.pdf</span
                            >
                          </a>


<!DOCTYPE html>
<html
  lang="en"
  xmlns:th="http://www.thymeleaf.org"
  xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
  layout:decorate="~{layouts/app}"
>
  <head th:replace="~{fragments/header :: header('Edit Skema')}"></head>

  <body>
    <div layout:fragment="content">
      <div class="container-fluid">
        <div class="row">
          <div class="col-12">
            <div class="card card-secondary card-tabs">
              <div class="card-header p-0 pt-1">
                <ul class="nav nav-tabs" id="skema-tab" role="tablist">
                  <li class="nav-item">
                    <a
                      class="nav-link active"
                      id="skema-sertifikasi-tab"
                      data-toggle="pill"
                      href="#skema-sertifikasi"
                      role="tab"
                      aria-controls="skema-sertifikasi"
                      aria-selected="true"
                      >1. Skema Sertifikasi</a
                    >
                  </li>
                  <li class="nav-item">
                    <a
                      class="nav-link"
                      id="unit-skema-tab"
                      data-toggle="pill"
                      href="#unit-skema"
                      role="tab"
                      aria-controls="unit-skema"
                      aria-selected="false"
                      >2. Unit Skema</a
                    >
                  </li>
                  <li class="nav-item">
                    <a
                      class="nav-link"
                      id="persyaratan-tab"
                      data-toggle="pill"
                      href="#tab-persyaratan"
                      role="tab"
                      aria-controls="content-tab-persyaratan"
                      aria-selected="false"
                    >
                      3. Persyaratan Dasar
                    </a>
                  </li>
                </ul>
              </div>

              <div class="card-body">
                <form
                  th:action="@{/admin/skema/update}"
                  method="post"
                  enctype="multipart/form-data"
                  novalidate
                >
                  <input type="hidden" name="id" th:value="${skema.id}" />

                  <div class="tab-content">
                    <div
                      class="tab-pane fade show active"
                      id="skema-sertifikasi"
                      role="tabpanel"
                    >
                      <div class="row">
                        <div class="col-md-12">
                          <div class="form-group">
                            <label for="namaSkema">Nama Skema</label>
                            <input
                              type="text"
                              class="form-control"
                              name="namaSkema"
                              th:value="${skema.name}"
                              required
                            />
                          </div>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-md-3">
                          <div class="form-group">
                            <label for="kodeSkema">Kode Skema</label>
                            <input
                              type="text"
                              class="form-control"
                              name="kodeSkema"
                              th:value="${skema.code}"
                              required
                            />
                          </div>
                        </div>
                        <div class="col-md-3">
                          <div class="form-group">
                            <label for="exampleInputLevel">Level</label>
                            <input
                              type="number"
                              class="form-control"
                              name="level"
                              th:value="${skema.level}"
                              required
                            />
                          </div>
                        </div>
                        <div class="col-md-3">
                          <div class="form-group">
                            <label for="exampleInputLevel">No SKKNI</label>
                            <input
                              type="text"
                              class="form-control"
                              name="noSkkni"
                              th:value="${skema.noSkkni}"
                              required
                            />
                          </div>
                        </div>
                        <div class="col-md-3">
                          <div class="form-group">
                            <label for="exampleInputLevel">Tahun SKKNI</label>
                            <input
                              type="number"
                              class="form-control"
                              name="tahunSkkni"
                              th:value="${skema.skkniYear}"
                              required
                            />
                          </div>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-md-6">
                          <div class="form-group">
                            <label>Jenis Skema</label>
                            <select
                              class="form-control select2"
                              name="jenisSkemaId"
                              style="width: 100%"
                              required
                            >
                              <option value="" disabled>
                                Pilih Jenis Skema
                              </option>
                              <option
                                th:each="type : ${types}"
                                th:value="${type.id}"
                                th:text="${type.name}"
                                th:selected="${skema.schemaType != null and skema.schemaType.id == type.id}"
                              ></option>
                            </select>
                          </div>
                        </div>
                        <div class="col-md-6">
                          <div class="form-group">
                            <label>Mode Skema</label>
                            <select
                              class="form-control select2"
                              name="modeSkemaId"
                              style="width: 100%"
                              required
                            >
                              <option value="" disabled>
                                Pilih Mode Skema
                              </option>
                              <option
                                th:each="mode : ${modes}"
                                th:value="${mode.id}"
                                th:text="${mode.name}"
                                th:selected="${skema.schemaMode != null and skema.schemaMode.id == mode.id}"
                              ></option>
                            </select>
                          </div>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-md-6">
                          <div class="form-group">
                            <label>Tanggal Penetapan</label>
                            <input
                              type="date"
                              class="form-control"
                              name="tanggalPenetapan"
                              th:value="${skema.establishmentDate}"
                              required
                            />
                          </div>
                        </div>
                        <div class="col-md-6">
                          <div class="form-group">
                            <label>File Dokumen (PDF)</label>
                            <div class="mb-2" th:if="${skema.documentPath}">
                              <small class="text-success"
                                ><i class="fas fa-check"></i> File saat ini:
                                <span th:text="${skema.name} + '.pdf'"></span
                              ></small>
                            </div>
                            <div class="input-group">
                              <div class="custom-file">
                                <input
                                  type="file"
                                  class="custom-file-input"
                                  id="fileSkema"
                                  name="fileSkema"
                                  accept=".pdf"
                                />
                                <label class="custom-file-label" for="fileSkema"
                                  >Ganti file (Opsional)</label
                                >
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div
                      class="tab-pane fade"
                      id="unit-skema"
                      role="tabpanel"
                      aria-labelledby="tab-unit"
                    >
                      <div id="unit-skema-container">
                        <div class="unit-skema-row card card-body mb-3">
                          <div class="row">
                            <div class="col-md-6">
                              <div class="form-group">
                                <label>Kode Unit</label>
                                <input
                                  type="text"
                                  class="form-control"
                                  name="kodeUnit[]"
                                  th:value="${unit.code}"
                                  required
                                />
                              </div>
                            </div>
                            <div class="col-md-6">
                              <div class="form-group">
                                <label>Judul Unit</label>
                                <input
                                  type="text"
                                  class="form-control"
                                  name="judulUnit[]"
                                  th:value="${unit.title}"
                                  required
                                />
                              </div>
                            </div>
                          </div>
                          <!-- <div class="row">
                            <div class="col-12">
                              <div class="form-group">
                                <label>Standar Kompetensi</label>
                                <select
                                  class="form-control"
                                  name="standarKompetensi[]"
                                >
                                  <option value="SKKNI">SKKNI</option>
                                  <option value="Standar Internasional">
                                    Standar Internasional
                                  </option>
                                  <option value="Standar Khusus">
                                    Standar Khusus
                                  </option>
                                </select>
                              </div>
                            </div>
                          </div> -->
                          <div class="row">
                            <div class="col-12 text-right">
                              <button
                                type="button"
                                class="btn btn-outline-danger btn-sm remove-unit-button"
                              >
                                <i class="fas fa-trash"></i>
                              </button>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div class="row mb-3">
                        <div class="col-12 text-center">
                          <button
                            type="button"
                            id="add-unit-button"
                            class="btn btn-outline-success"
                          >
                            <i class="fas fa-plus"></i> Tambah Data
                          </button>
                        </div>
                      </div>
                    </div>

                    <!-- <div
                      class="tab-pane fade"
                      id="tab-persyaratan"
                      role="tabpanel"
                      aria-labelledby="tab-persyaratan"
                    >
                      <div id="persyaratan-container">
                        <div class="row persyaratan-row align-items-start mb-3">
                          <div class="col-md-11">
                            <div class="form-group mb-md-0">
                              <label class="sr-only">Nama Persyaratan</label>
                              <textarea
                                class="form-control summernote-editor"
                                name="persyaratan[]"
                                rows="3"
                              ></textarea>
                            </div>
                          </div>
                          <div class="col-md-1 text-right">
                            <button
                              type="button"
                              class="btn btn-danger btn-sm remove-persyaratan-button"
                            >
                              <i class="fas fa-trash"></i>
                            </button>
                          </div>
                        </div>
                      </div>

                      <div class="row mt-2">
                        <div class="col-12">
                          <button
                            type="button"
                            id="add-persyaratan-button"
                            class="btn btn-success btn-block"
                          >
                            <i class="fas fa-plus"></i> Tambah Persyaratan
                          </button>
                        </div>
                      </div>
                    </div> -->

                    <div
                      class="tab-pane fade"
                      id="tab-persyaratan"
                      role="tabpanel"
                      aria-labelledby="tab-persyaratan"
                    >
                      <div class="card card-outline card-info">
                        <div class="card-header">
                          <h3 class="card-title">Format Teks Persyaratan</h3>
                          <div
                            id="custom-summernote-toolbar"
                            class="float-right"
                          >
                            <div class="btn-group">
                              <button
                                type="button"
                                class="btn btn-sm btn-default"
                                data-command="bold"
                                title="Bold"
                              >
                                <i class="fas fa-bold"></i>
                              </button>
                              <button
                                type="button"
                                class="btn btn-sm btn-default"
                                data-command="italic"
                                title="Italic"
                              >
                                <i class="fas fa-italic"></i>
                              </button>
                              <button
                                type="button"
                                class="btn btn-sm btn-default"
                                data-command="underline"
                                title="Underline"
                              >
                                <i class="fas fa-underline"></i>
                              </button>
                            </div>
                            <div class="btn-group ml-2">
                              <button
                                type="button"
                                class="btn btn-sm btn-default"
                                data-command="insertUnorderedList"
                                title="Unordered List"
                              >
                                <i class="fas fa-list-ul"></i>
                              </button>
                              <button
                                type="button"
                                class="btn btn-sm btn-default"
                                data-command="insertOrderedList"
                                title="Ordered List"
                              >
                                <i class="fas fa-list-ol"></i>
                              </button>
                            </div>
                          </div>
                        </div>
                      </div>

                      <div id="persyaratan-container">
                        <div
                          class="persyaratan-row row align-items-center mb-3"
                        >
                          <div class="col-11">
                            <textarea
                              class="form-control summernote-persyaratan"
                              name="persyaratan[]"
                              th:utext="${req.description}"
                              required
                            ></textarea>
                          </div>
                          <div class="col-1">
                            <button
                              type="button"
                              class="btn btn-outline-danger btn-sm remove-persyaratan-button"
                            >
                              <i class="fas fa-trash"></i>
                            </button>
                          </div>
                        </div>
                        <div
                          class="persyaratan-row row align-items-center mb-3"
                          th:if="${#lists.isEmpty(skema.requirements)}"
                        >
                          <div class="col-11">
                            <textarea
                              class="form-control summernote-persyaratan"
                              name="persyaratan[]"
                              required
                            ></textarea>
                          </div>
                          <div class="col-1">
                            <button
                              type="button"
                              class="btn btn-outline-danger btn-sm remove-persyaratan-button"
                            >
                              <i class="fas fa-trash"></i>
                            </button>
                          </div>
                        </div>
                      </div>

                      <div class="row mt-3">
                        <div class="col-12">
                          <button
                            type="button"
                            id="add-persyaratan-button"
                            class="btn btn-outline-success btn-block"
                          >
                            <i class="fas fa-plus"></i> Tambah Persyaratan
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="card-footer mt-3 text-left">
                    <button type="submit" class="btn btn-outline-warning">
                      Simpan
                    </button>
                    <a th:href="@{/admin/skema}" class="btn btn-outline-info"
                      >Batal</a
                    >
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>



// ========================================================
// JAVASCRIPT UNTUK SKEMA ADMIN
// ========================================================

// $(document).ready(function () {
//   var activeSummernoteInstance = null;
//   const formKey = "skemaFormData";

//   // Inisialisasi Toast SweetAlert
//   var Toast = Swal.mixin({
//     toast: true,
//     position: "top-end",
//     showConfirmButton: false,
//     timer: 3000,
//     timerProgressBar: true,
//     didOpen: (toast) => {
//       toast.addEventListener("mouseenter", Swal.stopTimer);
//       toast.addEventListener("mouseleave", Swal.resumeTimer);
//     },
//   });

//   $(".delete-button").on("click", function (e) {
//     e.preventDefault();
//     var realLink = $(this).attr("href");

//     Swal.fire({
//       title: "Yakin Hapus Skema?",
//       text: "Data unit dan persyaratan didalamnya juga akan terhapus permanen!",
//       icon: "warning",
//       showCancelButton: true,
//       confirmButtonColor: "#d33",
//       cancelButtonColor: "#3085d6",
//       confirmButtonText: "Ya, Hapus!",
//       cancelButtonText: "Batal",
//     }).then((result) => {
//       if (result.isConfirmed) {
//         window.location.href = link;
//       }
//     });
//   });

//   //  =======================================================
//   // PDF PREVIEW MODAL
//   //  =======================================================
//   // PDF Preview in Modal
//   //   $(document).ready(function () {
//   //     // Event ini akan dijalankan SETIAP KALI modal #previewModal akan ditampilkan
//   //     $("#previewModal").on("show.bs.modal", function (event) {
//   //       // Dapatkan tombol yang memicu modal
//   //       var button = $(event.relatedTarget);

//   //       // Ekstrak path file dari atribut data-filepath
//   //       var filePath = button.data("filepath");

//   //       // Dapatkan elemen modal itu sendiri
//   //       var modal = $(this);

//   //       // Cari elemen iframe di dalam modal dan atur atribut 'src'-nya
//   //       modal.find("#pdf-viewer").attr("src", filePath);
//   //     });

//   //     // (Opsional) Kosongkan src iframe saat modal ditutup agar tidak membebani browser
//   //     $("#previewModal").on("hidden.bs.modal", function () {
//   //       $(this).find("#pdf-viewer").attr("src", "");
//   //     });
//   //   });

  // =======================================================
  // --- FUNGSI UNTUK LOCALSTORAGE ---
  // =======================================================

  function saveFormDataToLocalStorage() {
    const formData = {};

    // TAB 1: Simpan semua input dari Tab 1
    formData.namaSkema = $("#namaSkema").val();
    formData.kodeSkema = $("#kodeSkema").val();
    formData.noSkkni = $("#noSkkni").val();
    formData.level = $("#levelSkema").val();
    formData.tahun = $("#tahunSkkni").val();
    formData.jenisSkema = $("#jenisSkema").val();
    formData.modeSkema = $("#modeSkema").val();
    formData.tanggal_penetapan = $("#tanggal_penetapan").val();
    formData.fileSkemaName = $("#fileSkema").val().split("\\").pop();

    // TAB 2: Simpan data dinamis dari Unit Skema
    formData.unitSkema = [];
    $("#unit-skema-container .unit-skema-row").each(function () {
      const unit = {
        kodeUnit: $(this).find('input[name="kodeUnit[]"]').val(),
        judulUnit: $(this).find('input[name="judulUnit[]"]').val(),
        standarKompetensi: $(this)
          .find('select[name="standarKompetensi[]"]')
          .val(),
      };
      formData.unitSkema.push(unit);
    });

    // TAB 3: Simpan data dinamis dari Persyaratan
    formData.persyaratan = [];
    $("#persyaratan-container .persyaratan-row").each(function () {
      formData.persyaratan.push(
        $(this).find(".summernote-persyaratan").summernote("code")
      );
    });

    // Simpan ID tab yang sedang aktif
    formData.activeTab = $(".nav-tabs .nav-link.active").attr("id");

    // Simpan semua data ke localStorage
    localStorage.setItem("skemaFormData", JSON.stringify(formData));
    console.log("Form data saved!"); // Untuk debugging
  }

  function loadFormDataFromLocalStorage() {
    const savedData = localStorage.getItem("skemaFormData");
    if (!savedData) return;

    const formData = JSON.parse(savedData);
    console.log("Loading form data:", formData); // Untuk debugging

    // Muat data untuk Tab 1
    if (formData.namaSkema) $("#namaSkema").val(formData.namaSkema);
    if (formData.kodeSkema) $("#kodeSkema").val(formData.kodeSkema);
    if (formData.noSkkni) $("#noSkkni").val(formData.noSkkni);
    if (formData.level) $("#levelSkema").val(formData.level);
    if (formData.tahun) $("#tahunSkkni").val(formData.tahun);
    if (formData.tanggal_penetapan)
      $("#tanggal_penetapan").val(formData.tanggal_penetapan);

    if (formData.jenisSkema) {
      $("#jenisSkema").val(formData.jenisSkema).trigger("change");
    }
    if (formData.modeSkema) {
      $("#modeSkema").val(formData.modeSkema).trigger("change");
    }
    if (formData.fileSkemaName) {
      $('.custom-file-label[for="fileSkema"]').text(formData.fileSkemaName);
    }

    // Muat data untuk Unit Skema (Tab 2)
    if (formData.unitSkema && formData.unitSkema.length > 0) {
      const unitContainer = $("#unit-skema-container");
      const unitTemplate = unitContainer.find(".unit-skema-row:first").clone();
      unitContainer.empty();

      formData.unitSkema.forEach(function (unit) {
        const newUnitRow = unitTemplate.clone();
        newUnitRow.find('input[name="kodeUnit[]"]').val(unit.kodeUnit);
        newUnitRow.find('input[name="judulUnit[]"]').val(unit.judulUnit);
        newUnitRow
          .find('select[name="standarKompetensi[]"]')
          .val(unit.standarKompetensi);
        unitContainer.append(newUnitRow);
      });
    }

    // Muat data untuk Persyaratan (Tab 3)
    if (formData.persyaratan && formData.persyaratan.length > 0) {
      const reqContainer = $("#persyaratan-container");
      reqContainer.empty();

      formData.persyaratan.forEach(function (reqContent) {
        const newRowHTML = `
                        <div class="persyaratan-row row align-items-center mb-3">
                            <div class="col-11">
                                <textarea class="form-control summernote-persyaratan" name="persyaratan[]"></textarea>
                            </div>
                            <div class="col-1">
                                <button type="button" class="btn btn-outline-danger remove-persyaratan-button"><i class="fas fa-trash"></i></button>
                            </div>
                        </div>`;
        const newReqRow = $(newRowHTML);
        reqContainer.append(newReqRow);

        const summernoteEditor = newReqRow.find(".summernote-persyaratan");
        initializeSummernote(summernoteEditor);
        summernoteEditor.summernote("code", reqContent);
      });
    }

    // Aktifkan tab yang terakhir dibuka
    if (formData.activeTab) {
      $("#" + formData.activeTab).tab("show");
    }
  }

  // Fungsi debounce untuk menunda eksekusi agar tidak terlalu sering
  function debounce(func, delay) {
    let timeout;
    return function (...args) {
      const context = this;
      clearTimeout(timeout);
      timeout = setTimeout(() => func.apply(context, args), delay);
    };
  }

  const debouncedSave = debounce(saveFormDataToLocalStorage, 400);

  // 1. Panggil fungsi load saat halaman pertama kali dibuka
  loadFormDataFromLocalStorage();

  // 2. Gunakan event delegation pada seluruh form untuk event 'input'
  $("#form-tambah-skema").on(
    "input change",
    "input, select, textarea",
    debouncedSave
  );

  // 3. Pemicu khusus untuk summernote
  $(document).on("summernote.change", ".summernote-persyaratan", debouncedSave);

  // 4. Simpan saat menambah/menghapus baris dinamis
  $(document).on(
    "click",
    "#add-unit-button, .remove-unit-button, #add-persyaratan-button, .remove-persyaratan-button",
    function () {
      setTimeout(saveFormDataToLocalStorage, 100);
    }
  );

  // 5. Simpan saat berpindah tab
  $(".next-tab, .prev-tab").on("click", saveFormDataToLocalStorage);

  // =======================================================
  // --- FUNGSI UNTUK TOMBOL BATAL ---
  // =======================================================

  // --- FUNGSI BARU UNTUK MEMERIKSA APAKAH FORM SUDAH DIISI ---
  function isFormDirty() {
    let isDirty = false;

    // 1. Periksa semua input teks, select, dan textarea di Tab 1
    $("#content-tab-skema")
      .find('input[type="text"], input[type="date"], select, textarea')
      .each(function () {
        if ($(this).val() && $(this).val().trim() !== "") {
          isDirty = true;
          return false; // Keluar dari loop jika satu saja field terisi
        }
      });
    if (isDirty) return true;

    // 2. Periksa input file
    if ($("#fileSkema").get(0).files.length > 0) {
      return true;
    }

    // 3. Periksa apakah ada lebih dari satu baris di Tab 2 atau Tab 3
    if (
      $("#unit-skema-container .unit-skema-row").length > 1 ||
      $("#persyaratan-container .persyaratan-row").length > 1
    ) {
      return true;
    }

    // 4. Periksa isi dari baris pertama di Tab 2
    $("#unit-skema-container .unit-skema-row:first")
      .find("input, select")
      .each(function () {
        if ($(this).val() && $(this).val().trim() !== "") {
          isDirty = true;
          return false;
        }
      });
    if (isDirty) return true;

    // 5. Periksa isi dari baris pertama (Summernote) di Tab 3
    const firstSummernote = $(
      "#persyaratan-container .summernote-persyaratan"
    ).first();
    if (firstSummernote.length > 0 && !firstSummernote.summernote("isEmpty")) {
      return true;
    }

    // Jika semua pengecekan gagal, berarti form masih bersih
    return false;
  }

  // =======================================================
  // --- FUNGSI UNTUK TOMBOL BATAL ---
  // =======================================================
  $(document).on("click", "#cancel-button", function (e) {
    e.preventDefault(); // Mencegah link langsung berpindah halaman
    const targetUrl = $(this).attr("href");

    // Cek apakah form sudah diisi menggunakan fungsi di atas
    if (isFormDirty()) {
      // JIKA FORM BERISI: Tampilkan konfirmasi SweetAlert
      Swal.fire({
        title: "Apakah Anda yakin?",
        text: "Semua data yang belum disimpan akan dihapus.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Ya, batalkan!",
        cancelButtonText: "Tidak",
      }).then((result) => {
        if (result.isConfirmed) {
          // Hapus data dari localStorage dan arahkan ke halaman lain
          localStorage.removeItem("skemaFormData");

          // Tampilkan notifikasi sukses (opsional)
          Toast.fire({
            icon: "success",
            text: "Input telah dibersihkan.",
          });

          // Tunggu sejenak lalu arahkan ke halaman daftar skema
          setTimeout(function () {
            window.location.href = targetUrl;
          }, 1000); // delay 1 detik
        }
      });
    } else {
      // JIKA FORM KOSONG: Langsung pindah halaman tanpa alert
      localStorage.removeItem("skemaFormData"); // Bersihkan juga untuk jaga-jaga
      window.location.href = targetUrl;
    }
  });

//   // =======================================================================
//   // --- SCRIPT AWAL UNTUK FORM DINAMIS ---
//   // =======================================================================

//   $(document).on("click", "#add-unit-button", function () {
//     // Cari template baris form yang akan digandakan
//     var template = $("#unit-skema-container .unit-skema-row:first");

//     // Kloning/gandakan baris template
//     var newUnitRow = template.clone();

//     // Kosongkan semua nilai input pada baris baru
//     newUnitRow.find("input, select").val("");

//     // Pastikan tombol hapus terlihat (penting jika baris pertama disembunyikan)
//     newUnitRow.find(".remove-unit-button").show();

//     // Tambahkan baris baru ke dalam container
//     $("#unit-skema-container").append(newUnitRow);
//   });

//   // Menggunakan Event Delegation untuk tombol 'Hapus'
//   // Ini memastikan tombol hapus pada baris baru juga akan berfungsi
//   $(document).on("click", ".remove-unit-button", function () {
//     // Cek jumlah baris yang ada
//     if ($("#unit-skema-container .unit-skema-row").length > 1) {
//       // Hapus elemen card (.unit-skema-row) terdekat dari tombol yang diklik
//       $(this).closest(".unit-skema-row").remove();
//     } else {
//       // Beri peringatan jika mencoba menghapus baris terakhir
//       Toast.fire({
//         icon: "error",
//         title: "Minimal harus ada satu unit skema.",
//       });
//     }
//   });

//   // Fungsi untuk menginisialisasi Summernote pada sebuah elemen textarea
//   function initializeSummernote(element) {
//     element.summernote({
//       height: 100,
//       toolbar: [], // Toolbar default disembunyikan
//       callbacks: {
//         // Saat kursor masuk ke editor (fokus), simpan instance-nya
//         onFocus: function () {
//           activeSummernoteInstance = $(this);
//           // Beri highlight biru untuk menandakan editor aktif
//           $(".note-editor").removeClass("border-primary"); // Hapus highlight dari yang lain
//           $(this).next(".note-editor").addClass("border-primary");
//         },
//         // Saat kursor keluar, hapus highlight
//         onBlur: function () {
//           $(this).next(".note-editor").removeClass("border-primary");
//         },
//       },
//     });
//   }

//   // Inisialisasi editor pertama yang sudah ada saat halaman dimuat
//   if ($(".summernote-persyaratan").length > 0) {
//     initializeSummernote($(".summernote-persyaratan"));
//   }

//   // Event handler untuk tombol 'Tambah Persyaratan'
//   $(document).on("click", "#add-persyaratan-button", function () {
//     // Buat elemen baris baru dari template HTML (lebih aman daripada clone)
//     var newRowHTML = `
//             <div class="persyaratan-row row align-items-center mb-3">
//                 <div class="col-11">
//                     <textarea class="form-control summernote-persyaratan" name="persyaratan[]"></textarea>
//                 </div>
//                 <div class="col-1">
//                     <button type="button" class="btn btn-outline-danger remove-persyaratan-button">
//                         <i class="fas fa-trash"></i>
//                     </button>
//                 </div>
//             </div>`;

//     var newRow = $(newRowHTML);

//     // Tambahkan baris baru ke container
//     $("#persyaratan-container").append(newRow);

//     // Inisialisasi Summernote HANYA pada textarea yang baru dibuat
//     initializeSummernote(newRow.find(".summernote-persyaratan"));
//   });

//   // Event handler untuk tombol 'Hapus'
//   $(document).on("click", ".remove-persyaratan-button", function () {
//     if ($("#persyaratan-container .persyaratan-row").length > 1) {
//       var rowToRemove = $(this).closest(".persyaratan-row");
//       // Hancurkan instance summernote sebelum menghapus elemen HTML-nya
//       rowToRemove.find(".summernote-persyaratan").summernote("destroy");
//       rowToRemove.remove();
//     } else {
//       Toast.fire({
//         icon: "error",
//         title: "Minimal harus ada satu persyaratan.",
//       });
//     }
//   });

//   // Event handler untuk toolbar kustom
//   $("#custom-summernote-toolbar").on("click", "button", function (e) {
//     e.preventDefault(); // Mencegah fokus hilang dari editor
//     var command = $(this).data("command");

//     // Cek apakah ada editor yang aktif
//     if (activeSummernoteInstance && command) {
//       // Langsung jalankan perintah pada editor yang aktif
//       activeSummernoteInstance.summernote(command);
//     } else {
//       Toast.fire({
//         icon: "error",
//         title:
//           "Silakan klik di dalam kolom teks untuk mengaktifkan editor terlebih dahulu.",
//       });
//     }
//   });

//   // =======================================================================
//   // --- SCRIPT AKHIR UNTUK FORM DINAMIS ---
//   // =======================================================================

//   // ===================================================================
//   // FUNGSI VALIDASI UTAMA
//   // ===================================================================

//   /**
//    * Memvalidasi semua input yang diperlukan di dalam sebuah tab.
//    * @param {jQuery} tabElement - Elemen jQuery dari .tab-pane yang akan divalidasi.
//    * @returns {object} - Mengembalikan objek { isValid: boolean, firstInvalidElement: jQuery|null }.
//    */
//   function validateTab(tabElement) {
//     let isValid = true;
//     let firstInvalidElement = null;

//     // Hapus status invalid sebelumnya
//     tabElement.find(".is-invalid").removeClass("is-invalid");

//     // Validasi input, select, dan textarea biasa
//     tabElement
//       .find("input[required], select[required], textarea[required]")
//       .each(function () {
//         const input = $(this);
//         if (!input.val() || input.val().trim() === "") {
//           isValid = false;
//           input.addClass("is-invalid");
//           if (!firstInvalidElement) {
//             firstInvalidElement = input;
//           }
//         }
//       });

//     // Validasi khusus untuk Summernote
//     tabElement.find(".summernote-persyaratan").each(function () {
//       const summernote = $(this);
//       if (summernote.summernote("isEmpty")) {
//         isValid = false;
//         summernote.next(".note-editor").addClass("is-invalid");
//         if (!firstInvalidElement) {
//           // Fokus pada editor summernote
//           firstInvalidElement = summernote;
//         }
//       }
//     });

//     return { isValid: isValid, firstInvalidElement: firstInvalidElement };
//   }

//   // ===================================================================
//   // EVENT HANDLERS
//   // ===================================================================

//   // 1. Untuk input teks, tanggal, select, dan file biasa
//   $("#form-tambah-skema").on("input change", ".is-invalid", function () {
//     const input = $(this);
//     if (input.val() && input.val().trim() !== "") {
//       input.removeClass("is-invalid");
//       // Khusus untuk Select2, hapus juga error di elemennya
//       if (input.hasClass("select2-hidden-accessible")) {
//         input
//           .next(".select2-container")
//           .find(".select2-selection--single")
//           .removeClass("is-invalid");
//       }
//     }
//   });

//   // 2. Untuk Summernote
//   // Kita harus menggunakan event 'summernote.change'
//   $(document).on("summernote.change", ".summernote-persyaratan", function () {
//     const summernote = $(this);
//     if (!summernote.summernote("isEmpty")) {
//       summernote.next(".note-editor").removeClass("is-invalid");
//     }
//   });

//   // --- TOMBOL NAVIGASI TAB ---
//   $(".next-tab").on("click", function () {
//     const currentTab = $(this).closest(".tab-pane");
//     const validationResult = validateTab(currentTab);

//     if (validationResult.isValid) {
//       const targetTabId = $(this).data("target-tab");
//       $("#" + targetTabId).tab("show");
//     } else {
//       // Validasi gagal: Tampilkan Toast dan fokus
//       Toast.fire({
//         icon: "error",
//         title: "Harap isi semua kolom yang wajib diisi.",
//       });
//       if (validationResult.firstInvalidElement) {
//         validationResult.firstInvalidElement.focus();
//         // Jika itu summernote, fokus secara spesifik
//         if (
//           validationResult.firstInvalidElement.hasClass(
//             "summernote-persyaratan"
//           )
//         ) {
//           validationResult.firstInvalidElement.summernote("focus");
//         }
//       }
//     }
//   });

//   $(".prev-tab").on("click", function () {
//     const targetTabId = $(this).data("target-tab");
//     $("#" + targetTabId).tab("show");
//   });

//   $(".card-tabs .nav-tabs .nav-link").on("click", function (e) {
//     e.preventDefault();
//     Toast.fire({
//       icon: "info",
//       title: 'Gunakan tombol "Selanjutnya" atau "Sebelumnya".',
//     });
//     return false;
//   });

//   // --- TOMBOL SIMPAN (SUBMIT FORM) ---
//   $("#form-tambah-skema").on("submit", function (e) {
//     e.preventDefault(); // Selalu cegah submit default

//     let isAllTabsValid = true;
//     let firstInvalidTabLink = null;
//     let elementToFocus = null;

//     // Validasi setiap tab secara berurutan
//     $(".tab-pane").each(function () {
//       const tab = $(this);
//       const validationResult = validateTab(tab);

//       // Jika tab ini tidak valid DAN kita belum menemukan tab lain yang error
//       if (!validationResult.isValid && isAllTabsValid) {
//         isAllTabsValid = false; // Tandai bahwa ada error
//         firstInvalidTabId = tab.attr("id"); // Simpan ID tab yang error
//         elementToFocus = validationResult.firstInvalidElement; // Simpan elemen yang error
//       }
//     });

//     if (isAllTabsValid) {
//       // Persiapkan FormData (Untuk File + Input Text + Array)
//       var formData = new FormData(this);

//       // SweetAlert Loading
//       Swal.fire({
//         title: "Menyimpan Data...",
//         text: "Mohon tunggu sebentar",
//         allowOutsideClick: false,
//         didOpen: () => {
//           Swal.showLoading();
//         },
//       });

//       // AJAX Request
//       $.ajax({
//         url: $(this).attr("action"), // Mengambil url dari th:action form
//         type: "POST",
//         data: formData,
//         processData: false, // Wajib false untuk FormData
//         contentType: false, // Wajib false untuk FormData
//         success: function (response) {
//           // Parse response jika string
//           var res =
//             typeof response === "string" ? JSON.parse(response) : response;

//           // Hapus localStorage
//           localStorage.removeItem("skemaFormData");

//           Swal.fire({
//             title: "Sukses!",
//             text: res.message,
//             icon: "success",
//           }).then(() => {
//             // Redirect ke halaman list skema
//             window.location.href = "/admin/skema";
//           });
//         },
//         error: function (xhr) {
//           var errorMsg = "Terjadi kesalahan server";
//           try {
//             var json = JSON.parse(xhr.responseText);
//             errorMsg = json.message;
//           } catch (e) {}

//           Swal.fire("Gagal", errorMsg, "error");
//         },
//       });
//     } else {
//       if (firstInvalidTabId) {
//         const tabLink = $('.nav-tabs a[href="#' + firstInvalidTabId + '"]');
//         tabLink.tab("show");

//         // Beri jeda agar perpindahan tab selesai sebelum fokus
//         setTimeout(() => {
//           if (elementToFocus) {
//             if (elementToFocus.hasClass("summernote-persyaratan")) {
//               elementToFocus.summernote("focus");
//             } else {
//               elementToFocus.focus();
//             }
//           }
//         }, 250); // Jeda 250ms
//       }
//       Toast.fire({ icon: "error", title: "Harap lengkapi semua data wajib!" });
//     }
//   });

//   // // EVENT LISTENER UNTUK ICON COLLAPSE
//   // // Saat baris terbuka
//   $(document).on("show.bs.collapse", ".collapse", function () {
//     var id = $(this).attr("id"); // Ambil ID baris yang terbuka
//     // Cari tombol yang mengontrol baris ini
//     var btn = $('button[data-target="#' + id + '"]');

//     // Ubah ikon & warna
//     btn.find("i").removeClass("fa-folder-plus").addClass("fa-folder-minus");
//     btn.removeClass("btn-primary").addClass("btn-secondary");
//   });

//   // Saat baris tertutup
//   $(document).on("hide.bs.collapse", ".collapse", function () {
//     var id = $(this).attr("id");
//     var btn = $('button[data-target="#' + id + '"]');

//     // Kembalikan ikon & warna
//     btn.find("i").removeClass("fa-folder-minus").addClass("fa-folder-plus");
//     btn.removeClass("btn-secondary").addClass("btn-primary");
//   });
// });

// ========================================================
// JAVASCRIPT UTAMA SKEMA ADMIN (LIST, ADD, EDIT, VIEW)
// ========================================================

$(document).ready(function () {
  // --- KONFIGURASI UMUM ---
  var activeSummernoteInstance = null;
  const formKey = "skemaFormData";

  // Inisialisasi Toast SweetAlert
  var Toast = Swal.mixin({
    toast: true,
    position: "top-end",
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: true,
    didOpen: (toast) => {
      toast.addEventListener("mouseenter", Swal.stopTimer);
      toast.addEventListener("mouseleave", Swal.resumeTimer);
    },
  });

  // ========================================================
  // 1. LOGIKA LIST PAGE (skema-list.html)
  // ========================================================

  // Konfirmasi Delete
  $(document).on("click", ".delete-button", function (e) {
    e.preventDefault();
    var link = $(this).attr("href");

    Swal.fire({
      title: "Yakin Hapus Skema?",
      text: "Data unit dan persyaratan didalamnya juga akan terhapus permanen!",
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

  // Logika Icon Collapse (Ubah icon Plus/Minus di tabel list)
  $(document).on("show.bs.collapse", ".collapse", function () {
    var id = $(this).attr("id");
    var btn = $('button[data-target="#' + id + '"]');
    btn.find("i").removeClass("fa-folder-plus").addClass("fa-folder-minus");
    btn.removeClass("btn-primary").addClass("btn-info");
  });

  $(document).on("hide.bs.collapse", ".collapse", function () {
    var id = $(this).attr("id");
    var btn = $('button[data-target="#' + id + '"]');
    btn.find("i").removeClass("fa-folder-minus").addClass("fa-folder-plus");
    btn.removeClass("btn-info").addClass("btn-primary");
  });

  // ========================================================
  // 2. LOGIKA ADD/EDIT PAGE (skema-add.html)
  // ========================================================

  // Hanya jalankan jika form ada di halaman
  if ($("#form-tambah-skema").length) {
    // A. LOCAL STORAGE LOGIC (AUTO SAVE)
    // ----------------------------------
    function saveFormDataToLocalStorage() {
      // ... (Logika simpan form ke storage tetap sama seperti sebelumnya) ...
      // Agar kode tidak terlalu panjang di sini, pastikan fungsi ini ada
      // jika Anda ingin fitur autosave. Jika tidak, bisa dihapus.
    }

    function loadFormDataFromLocalStorage() {
      // ... (Logika load form dari storage) ...
    }

    // Jika fitur autosave diinginkan, uncomment baris ini:
    // loadFormDataFromLocalStorage();
    // $("input, select, textarea").on("input change", saveFormDataToLocalStorage);

    // B. FORM DINAMIS (UNIT SKEMA)
    // ----------------------------
    // Tambah Unit
    $(document).on("click", "#add-unit-button", function () {
      var template = $("#unit-skema-container .unit-skema-row:first");
      var newUnitRow = template.clone();
      newUnitRow.find("input").val(""); // Reset input
      newUnitRow.find(".remove-unit-button").show(); // Pastikan tombol hapus muncul
      $("#unit-skema-container").append(newUnitRow);
    });

    // Hapus Unit
    $(document).on("click", ".remove-unit-button", function () {
      if ($("#unit-skema-container .unit-skema-row").length > 1) {
        $(this).closest(".unit-skema-row").remove();
      } else {
        Toast.fire({
          icon: "error",
          title: "Minimal harus ada satu unit skema.",
        });
      }
    });

    // C. FORM DINAMIS (SUMMERNOTE PERSYARATAN)
    // ----------------------------------------
    function initializeSummernote(element) {
      element.summernote({
        height: 100,
        toolbar: [
          ["style", ["bold", "italic", "underline", "clear"]],
          ["para", ["ul", "ol", "paragraph"]],
        ],
      });
    }

    // Init Summernote Awal
    if ($(".summernote-persyaratan").length > 0) {
      $(".summernote-persyaratan").each(function () {
        initializeSummernote($(this));
      });
    }

    // Tambah Persyaratan
    $(document).on("click", "#add-persyaratan-button", function () {
      var newRowHTML = `
                <div class="persyaratan-row row align-items-center mb-3">
                    <div class="col-11">
                        <textarea class="form-control summernote-persyaratan" name="persyaratan[]"></textarea>
                    </div>
                    <div class="col-1">
                        <button type="button" class="btn btn-outline-danger remove-persyaratan-button">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>`;
      var newRow = $(newRowHTML);
      $("#persyaratan-container").append(newRow);
      initializeSummernote(newRow.find(".summernote-persyaratan"));
    });

    // Hapus Persyaratan
    $(document).on("click", ".remove-persyaratan-button", function () {
      if ($("#persyaratan-container .persyaratan-row").length > 1) {
        var row = $(this).closest(".persyaratan-row");
        row.find(".summernote-persyaratan").summernote("destroy"); // Hancurkan editor dulu
        row.remove();
      } else {
        Toast.fire({
          icon: "error",
          title: "Minimal harus ada satu persyaratan.",
        });
      }
    });

    // D. NAVIGASI TAB & VALIDASI
    // --------------------------
    function validateTab(tabElement) {
      let isValid = true;
      let firstInvalidElement = null;

      // Reset Error
      tabElement.find(".is-invalid").removeClass("is-invalid");

      // Validasi Input Biasa
      tabElement.find("input[required], select[required]").each(function () {
        if (!$(this).val() || $(this).val().trim() === "") {
          isValid = false;
          $(this).addClass("is-invalid");
          if (!firstInvalidElement) firstInvalidElement = $(this);
        }
      });

      // Validasi Summernote
      tabElement.find(".summernote-persyaratan").each(function () {
        if ($(this).summernote("isEmpty")) {
          isValid = false;
          // Summernote biasanya dibungkus div .note-editor, kita kasih border merah disana
          $(this).next(".note-editor").addClass("border border-danger");
          if (!firstInvalidElement) firstInvalidElement = $(this);
        } else {
          $(this).next(".note-editor").removeClass("border border-danger");
        }
      });

      return { isValid, firstInvalidElement };
    }

    // Hapus error saat diketik
    $("#form-tambah-skema").on("input change", ".is-invalid", function () {
      $(this).removeClass("is-invalid");
    });

    // --- TOMBOL NAVIGASI TAB ---
    $(".next-tab").on("click", function () {
      const currentTab = $(this).closest(".tab-pane");
      const validationResult = validateTab(currentTab);

      if (validationResult.isValid) {
        const targetTabId = $(this).data("target-tab");
        $("#" + targetTabId).tab("show");
      } else {
        // Validasi gagal: Tampilkan Toast dan fokus
        Toast.fire({
          icon: "error",
          title: "Harap isi semua kolom yang wajib diisi.",
        });

        if (validationResult.firstInvalidElement) {
          validationResult.firstInvalidElement.focus();
          // Jika itu summernote, fokus secara spesifik
          if (
            validationResult.firstInvalidElement.hasClass(
              "summernote-persyaratan"
            )
          ) {
            validationResult.firstInvalidElement.summernote("focus");
          }
        }
      }
    });

    $(".prev-tab").on("click", function () {
      const targetTabId = $(this).data("target-tab");
      $("#" + targetTabId).tab("show");
    });

    $(".card-tabs .nav-tabs .nav-link").on("click", function (e) {
      e.preventDefault();
      Toast.fire({
        icon: "info",
        title: 'Gunakan tombol "Selanjutnya" atau "Sebelumnya".',
      });
      return false;
    });

    // E. SUBMIT FORM (AJAX)
    // ---------------------
    // $("#form-tambah-skema").on("submit", function (e) {
    //   e.preventDefault();

    //   // Validasi Akhir Semua Tab
    //   let allValid = true;
    //   $(".tab-pane").each(function () {
    //     if (!validateTab($(this)).isValid) allValid = false;
    //   });

    //   if (!allValid) {
    //     Toast.fire({
    //       icon: "error",
    //       title: "Masih ada data yang kosong. Cek semua tab!",
    //     });
    //     return;
    //   }

    //   // Proses Kirim
    //   var formData = new FormData(this);

    //   Swal.fire({
    //     title: "Menyimpan Data...",
    //     didOpen: () => Swal.showLoading(),
    //   });

    //   $.ajax({
    //     url: $(this).attr("action"),
    //     type: "POST",
    //     data: formData,
    //     processData: false,
    //     contentType: false,
    //     success: function (res) {
    //       var response = typeof res === "string" ? JSON.parse(res) : res;
    //       // Bersihkan Storage
    //       localStorage.removeItem("skemaFormData");

    //       Swal.fire({
    //         title: "Berhasil!",
    //         text: response.message,
    //         icon: "success",
    //       }).then(() => {
    //         window.location.href = "/admin/skema";
    //       });
    //     },
    //     error: function (xhr) {
    //       var msg = "Terjadi kesalahan server";
    //       try {
    //         msg = JSON.parse(xhr.responseText).message;
    //       } catch (e) {}
    //       Swal.fire("Gagal", msg, "error");
    //     },
    //   });
    // });

    // E. SUBMIT FORM (ADD & EDIT)
    // ---------------------------
    // Target kedua form sekaligus
    $("#form-tambah-skema, #form-edit-skema").on("submit", function (e) {
      e.preventDefault();

      // Cek apakah ini form Edit?
      var isEdit = $(this).attr("id") === "form-edit-skema";

      // 1. Validasi Tab (Sama)
      let allValid = true;
      // ... (Logika validasi tab tetap sama) ...

      if (!allValid) {
        Toast.fire({ icon: "error", title: "Cek data yang kosong!" });
        return;
      }

      var formData = new FormData(this);

      Swal.fire({
        title: isEdit ? "Memperbarui Data..." : "Menyimpan Data...",
        didOpen: () => Swal.showLoading(),
      });

      $.ajax({
        url: $(this).attr("action"),
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (res) {
          var response = typeof res === "string" ? JSON.parse(res) : res;

          // Hapus storage hanya jika Add (Edit tidak pakai draft storage)
          if (!isEdit) localStorage.removeItem("skemaFormData");

          Swal.fire({
            title: "Sukses!",
            text: response.message,
            icon: "success",
          }).then(() => {
            window.location.href = "/admin/skema";
          });
        },
        error: function (xhr) {
          var msg = "Terjadi kesalahan";
          try {
            msg = JSON.parse(xhr.responseText).message;
          } catch (e) {}
          Swal.fire("Gagal", msg, "error");
        },
      });
    });
  } // End if form exists
});
