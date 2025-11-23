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
  // Change Pekerjaan -> Simpan ID
  $("#selectPekerjaan").on("change", function () {
    var id = $(this).val();
    // UBAH DISINI: Simpan ID ke input hidden
    $("#inputPekerjaan").val(id);

    //         // ... (Logika slideUp/Down detail instansi tetap jalan berdasarkan ID) ...
    //     });
    //   $("#selectPekerjaan").on("change", function () {
    //     var jobId = $(this).val(); // ID pekerjaan (1 = Tidak Bekerja)
    //     var jobName = $(this).find(":selected").data("name");
    //     $("#inputPekerjaan").val(jobName); // Simpan nama ke input hidden

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

  // 2. LOGIKA PENDIDIKAN & PEKERJAAN
  // --------------------------------

  // Load Pendidikan
  fetch("/data/education.json")
    .then((res) => res.json())
    .then((data) => {
      let options = '<option value="">Pilih Pendidikan...</option>';
      data.forEach((item) => {
        // Value = ID (1, 2, 3...)
        options += `<option value="${item.id}">${item.name}</option>`;
      });
      $("#selectPendidikan").html(options);
    });

  // Change Pendidikan -> Simpan ID
  $("#selectPendidikan").on("change", function () {
    // UBAH DISINI: Ambil value (ID), bukan data-name
    $("#inputPendidikan").val($(this).val());
  });

  // Load Pekerjaan
  fetch("/data/jobs.json")
    .then((res) => res.json())
    .then((data) => {
      let options = '<option value="">Pilih Pekerjaan...</option>';
      data.forEach((item) => {
        options += `<option value="${item.id}" data-name="${item.name}">${item.name}</option>`;
      });
      $("#selectPekerjaan").html(options);
    });

  // --- LOAD DATA JSON STATIS (Pendidikan & Pekerjaan) ---

  // 1. Load Pendidikan
  //   fetch("/dist/js/education.json")
  //     .then((response) => response.json())
  //     .then((data) => {
  //       let options = '<option value="">Pilih Pendidikan</option>';
  //       data.forEach((item) => {
  //         // Menyimpan nama di atribut data-name
  //         options += `<option value="${item.id}" data-name="${item.name}">${item.name}</option>`;
  //       });
  //       $("#selectPendidikan").html(options);
  //     })
  //     .catch((error) => console.error("Error loading education:", error));

  // 2. Load Pekerjaan
  //   fetch("/dist/js/jobs.json")
  //     .then((response) => response.json())
  //     .then((data) => {
  //       let options = '<option value="">Pilih Pekerjaan</option>';
  //       data.forEach((item) => {
  //         options += `<option value="${item.id}" data-name="${item.name}">${item.name}</option>`;
  //       });
  //       $("#selectPekerjaan").html(options);
  //     })
  //     .catch((error) => console.error("Error loading jobs:", error));

  // --- EVENT LISTENER UNTUK MENYIMPAN NAMA KE INPUT HIDDEN ---

  // Saat Pendidikan dipilih
  //   $("#selectPendidikan").on("change", function () {
  //     // Ambil 'data-name' dari option yang dipilih
  //     const namaPendidikan = $(this).find(":selected").data("name");
  //     // Masukkan ke input hidden agar terkirim ke server
  //     $("#inputPendidikan").val(namaPendidikan);
  //   });

  //   // Saat Pekerjaan dipilih
  //   $("#selectPekerjaan").on("change", function () {
  //     const namaPekerjaan = $(this).find(":selected").data("name");
  //     $("#inputPekerjaan").val(namaPekerjaan);
  //   });

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

  // 1. LOGIKA API WILAYAH
  // ---------------------
  const apiBaseUrl = "https://www.emsifa.com/api-wilayah-indonesia/api";

  // Load Provinsi
  fetch(`${apiBaseUrl}/provinces.json`)
    .then((res) => res.json())
    .then((data) => {
      let options = '<option value="">Pilih Provinsi...</option>';
      data.forEach((el) => {
        // Value adalah ID (misal: 11), data-name adalah Nama (ACEH)
        options += `<option value="${el.id}" data-name="${el.name}">${el.name}</option>`;
      });
      $("#selectProvinsi").html(options);
    });

  // Change Provinsi -> Simpan ID ke hidden input
  $("#selectProvinsi").on("change", function () {
    const id = $(this).val();
    // UBAH DISINI: Simpan ID ke input hidden, bukan nama
    $("#inputProvinsi").val(id);

    // ... (Logika fetch kota reset child tetap sama) ...
    if (id) {
      fetch(`${apiBaseUrl}/regencies/${id}.json`)
        .then((res) => res.json())
        .then((data) => {
          let opt = '<option value="">Pilih Kota/Kab...</option>';
          data.forEach(
            (el) => (opt += `<option value="${el.id}">${el.name}</option>`)
          );
          $("#selectKota").html(opt).prop("disabled", false);
        });
    }
  });

  // Change Kota -> Simpan ID
  $("#selectKota").on("change", function () {
    const id = $(this).val();
    $("#inputKota").val(id); // Simpan ID (contoh: 1101)

    // ... (Logika fetch kecamatan) ...
    if (id) {
      fetch(`${apiBaseUrl}/districts/${id}.json`)
        .then((res) => res.json())
        .then((data) => {
          let opt = '<option value="">Pilih Kecamatan...</option>';
          data.forEach(
            (el) => (opt += `<option value="${el.id}">${el.name}</option>`)
          );
          $("#selectKecamatan").html(opt).prop("disabled", false);
        });
    }
  });

  // Change Kecamatan -> Simpan ID
  $("#selectKecamatan").on("change", function () {
    const id = $(this).val();
    $("#inputKecamatan").val(id); // Simpan ID

    // ... (Logika fetch kelurahan) ...
    if (id) {
      fetch(`${apiBaseUrl}/villages/${id}.json`)
        .then((res) => res.json())
        .then((data) => {
          let opt = '<option value="">Pilih Kelurahan...</option>';
          data.forEach(
            (el) => (opt += `<option value="${el.id}">${el.name}</option>`)
          );
          $("#selectKelurahan").html(opt).prop("disabled", false);
        });
    }
  });

  // Change Kelurahan -> Simpan ID
  $("#selectKelurahan").on("change", function () {
    $("#inputKelurahan").val($(this).val()); // Simpan ID
  });

  // CSS Tambahan (Inject via JS agar praktis)
  // Agar border Select2 menjadi merah saat error
  $(
    "<style>.is-invalid-border { border-color: #dc3545 !important; }</style>"
  ).appendTo("head");
});
