// =======================================================================
// JAVASCRIPT UNTUK USERS ADMIN
// =======================================================================
$(document).ready(function () {
  // Aktifkan DataTables agar bisa search & paging otomatis
  $("#tableUser").DataTable({
    responsive: true,
    lengthChange: false,
    autoWidth: false,
    searching: false,
    language: {
      emptyTable: "Tidak ada data yang tersedia",
      search: "Cari:",
      paginate: {
        first: "Awal",
        last: "Akhir",
        next: "Lanjut",
        previous: "Mundur",
      },
    },
  });

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

  //   ===================================================================
  // 3. LOAD DATA JSON STATIS (Pendidikan & Pekerjaan)
  // =======================================================================

  // 1. PENDIDIKAN
  fetch("/dist/js/education.json")
    .then((res) => res.json())
    .then((data) => {
      let options = '<option value="">Pilih Pendidikan...</option>';
      data.forEach((item) => {
        // value="${item.id}" -> Ini ID (1, 2, 3...)
        options += `<option value="${item.id}">${item.name}</option>`;
      });
      $("#selectPendidikan").html(options);
    });

  $("#selectPendidikan").on("change", function () {
    // Ambil ID dari value option, simpan ke hidden input
    $("#inputPendidikan").val($(this).val());
  });

  // 2. PEKERJAAN
  fetch("/dist/js/jobs.json")
    .then((res) => res.json())
    .then((data) => {
      let options = '<option value="">Pilih Pekerjaan...</option>';
      data.forEach((item) => {
        // value="${item.id}" -> Ini ID
        options += `<option value="${item.id}" data-name="${item.name}">${item.name}</option>`;
      });
      $("#selectPekerjaan").html(options);
    });

  $("#selectPekerjaan").on("change", function () {
    // Simpan ID ke hidden input
    $("#inputPekerjaan").val($(this).val());
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

    // --- TAMBAHAN BARU: SUBMIT HANDLER (AJAX) ---
    submitHandler: function (form) {
      // 1. Persiapkan Data (Termasuk File Upload)
      var formData = new FormData(form);

      // 2. Tampilkan Loading (Opsional, agar user tahu proses berjalan)
      Swal.fire({
        title: "Menyimpan Data...",
        text: "Mohon tunggu sebentar",
        allowOutsideClick: false,
        didOpen: () => {
          Swal.showLoading();
        },
      });

      // 3. Kirim via AJAX
      $.ajax({
        url: $(form).attr("action"),
        type: "POST",
        data: formData,
        processData: false, // Wajib false untuk FormData
        contentType: false, // Wajib false untuk FormData
        success: function (response) {
          // 4. JIKA SUKSES -> TAMPILKAN POPUP PILIHAN
          Swal.fire({
            title: "Berhasil!",
            text: "Pengguna Berhasil ditambahkan",
            icon: "success",
            showCancelButton: true,
            confirmButtonText: "OK",
            cancelButtonText: "Tambah",
            confirmButtonColor: "#3085d6", // Warna Biru
            cancelButtonColor: "#28a745", // Warna Hijau (Tombol Tambah)
            reverseButtons: true, // Tukar posisi tombol agar lebih natural
          }).then((result) => {
            if (result.isConfirmed) {
              // PILIHAN 1: KLIK "OK" -> PINDAH KE HALAMAN LIST
              window.location.href = "/admin/data-pengguna"; // Sesuaikan URL list pengguna Anda
            } else if (result.dismiss === Swal.DismissReason.cancel) {
              // PILIHAN 2: KLIK "TAMBAH LAGI" -> RESET FORM

              // A. Reset Form HTML standar
              form.reset();

              // B. Reset Select2 (Dropdown)
              $(".select2").val(null).trigger("change");

              // C. Reset Validasi (Hilangkan merah-merah)
              var validator = $("#formTambahUser").validate();
              validator.resetForm();
              $(".is-invalid").removeClass("is-invalid");
              $(".is-valid").removeClass("is-valid");
              $(".is-invalid-border").removeClass("is-invalid-border");

              // D. Reset Input Hidden (ID Wilayah/Pekerjaan)
              $('input[type="hidden"]').val("");

              // E. Reset Signature Pad & Preview
              signaturePad.clear();
              $("#signatureInput").val("");
              $("#imgPreview").attr("src", "");
              $("#signaturePreview").hide();

              // Reset tombol signature ke awal
              $("#btnTriggerSignature")
                .removeClass(
                  "btn-success btn-outline-success btn-outline-danger"
                )
                .addClass("btn-outline-primary")
                .html('<i class="fas fa-pen-nib"></i> Masukkan Tanda Tangan');

              // F. Reset Tampilan Section (Role Logic)
              // Trigger change pada roleSelect kosong untuk menyembunyikan form detail
              $("#roleSelect").trigger("change");

              // Scroll ke atas
              $("html, body").animate({ scrollTop: 0 }, "fast");
            }
          });
        },
        error: function (xhr, status, error) {
          // JIKA GAGAL
          Swal.fire({
            icon: "error",
            title: "Gagal Menyimpan",
            text: "Terjadi kesalahan pada server. Silakan coba lagi.",
          });
          console.error(error);
        },
      });

      return false; // Mencegah submit form standar HTML (Page Reload)
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
    .then((res) => res.json())
    .then((data) => {
      let options = '<option value="">Pilih Provinsi...</option>';
      data.forEach((el) => {
        // Value = ID (misal 11), Text = Nama (ACEH)
        options += `<option value="${el.id}" data-name="${el.name}">${el.name}</option>`;
      });
      $("#selectProvinsi").html(options);
    });

  // Change Provinsi
  $("#selectProvinsi").on("change", function () {
    const id = $(this).val();
    $("#inputProvinsi").val(id); // SIMPAN ID (11)

    // Reset Child
    $("#selectKota")
      .html('<option value="">Pilih Kota/Kab...</option>')
      .prop("disabled", true);
    // ... (Reset Kecamatan/Kelurahan juga) ...

    if (id) {
      fetch(`${apiBaseUrl}/regencies/${id}.json`)
        .then((res) => res.json())
        .then((data) => {
          let opt = '<option value="">Pilih Kota/Kab...</option>';
          data.forEach(
            (el) =>
              (opt += `<option value="${el.id}" data-name="${el.name}">${el.name}</option>`)
          );
          $("#selectKota").html(opt).prop("disabled", false);
        });
    }
  });

  // Change Kota
  $("#selectKota").on("change", function () {
    const id = $(this).val();
    $("#inputKota").val(id); // SIMPAN ID (1101)

    if (id) {
      fetch(`${apiBaseUrl}/districts/${id}.json`)
        .then((res) => res.json())
        .then((data) => {
          let opt = '<option value="">Pilih Kecamatan...</option>';
          data.forEach(
            (el) =>
              (opt += `<option value="${el.id}" data-name="${el.name}">${el.name}</option>`)
          );
          $("#selectKecamatan").html(opt).prop("disabled", false);
        });
    }
  });

  // Change Kecamatan
  $("#selectKecamatan").on("change", function () {
    const id = $(this).val();
    $("#inputKecamatan").val(id); // SIMPAN ID

    if (id) {
      fetch(`${apiBaseUrl}/villages/${id}.json`)
        .then((res) => res.json())
        .then((data) => {
          let opt = '<option value="">Pilih Kelurahan...</option>';
          data.forEach(
            (el) =>
              (opt += `<option value="${el.id}" data-name="${el.name}">${el.name}</option>`)
          );
          $("#selectKelurahan").html(opt).prop("disabled", false);
        });
    }
  });

  // Change Kelurahan
  $("#selectKelurahan").on("change", function () {
    $("#inputKelurahan").val($(this).val()); // SIMPAN ID
  });

  // CSS Tambahan (Inject via JS agar praktis)
  // Agar border Select2 menjadi merah saat error
  $(
    "<style>.is-invalid-border { border-color: #dc3545 !important; }</style>"
  ).appendTo("head");
});
