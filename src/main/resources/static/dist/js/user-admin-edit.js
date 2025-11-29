// =======================================================================
// JAVASCRIPT UNTUK USERS ADMIN
// =======================================================================
$(document).ready(function () {
  // ==========================================================
  // USER LIST ADMIN SCRIPTS
  // ==========================================================

  // ---------------------------------------------------
  // KONFIRMASI DELETE DENGAN SWEETALERT2
  // ---------------------------------------------------
  $(".delete-button").on("click", function (e) {
    e.preventDefault();
    var realLink = $(this).attr("href");

    Swal.fire({
      title: "Yakin Ingin Hapus?",
      text: "Data yang dihapus tidak dapat dikembalikan!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Ya, Hapus Permanen!",
      cancelButtonText: "Batal",
    }).then((result) => {
      if (result.isConfirmed) {
        window.location.href = realLink;
      }
    });
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

  // --- 1. EVENT LISTENER AGAR VALIDASI LANGSUNG JALAN (REAL-TIME) ---
  // Masalah Select2: Validasi tidak otomatis hilang/muncul saat user memilih opsi.
  // Solusi: Kita paksa validasi saat event 'change'.
  $(".select2").on("change", function () {
    $(this).valid(); // Memicu pengecekan ulang pada elemen ini
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

  // ==========================================================
  // VALIDASI DAN SIMPAN TAMBAH PENGGUNA
  // ==========================================================
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

  // =========================================================
  // USER VIEW ADMIN SCRIPTS
  // =========================================================
  if ($("#viewProv").length) {
    const apiBaseUrl = "https://www.emsifa.com/api-wilayah-indonesia/api";

    // Fungsi Fetch yang lebih sederhana & kuat
    function setRegionName(url, elementId, fallbackId) {
      if (!fallbackId) {
        $("#" + elementId).val("-");
        return;
      }
      $.ajax({
        url: apiBaseUrl + url,
        method: "GET",
        success: function (data) {
          // Cari ID yang cocok (pakai == agar string "11" cocok dengan int 11)
          let found = data.find((item) => item.id == fallbackId);
          if (found) {
            $("#" + elementId).val(found.name);
          } else {
            $("#" + elementId).val(fallbackId); // Jika tidak ketemu, tampilkan ID
          }
        },
        error: function () {
          $("#" + elementId).val("Error Loading");
        },
      });
    }

    // Eksekusi Berurutan
    let provId = $("#viewProv").data("id");
    setRegionName("/provinces.json", "viewProv", provId);

    let cityId = $("#viewCity").data("id");
    if (provId && cityId)
      setRegionName(`/regencies/${provId}.json`, "viewCity", cityId);

    let distId = $("#viewDist").data("id");
    if (cityId && distId)
      setRegionName(`/districts/${cityId}.json`, "viewDist", distId);

    let subDistId = $("#viewSubDist").data("id");
    if (distId && subDistId)
      setRegionName(`/villages/${distId}.json`, "viewSubDist", subDistId);
  }

  // ========================================================
  //  USER EDIT ADMIN SCRIPTS
  // ========================================================

  // // Init Plugins
  // $(".select2").select2({ theme: "bootstrap4" });
  // // (Init Signature Pad code here... sama seperti user-add)

  // ==========================================
  // 1. LOAD DATA PENDIDIKAN & PEKERJAAN (AUTO SELECT)
  // ==========================================

  // Load Pendidikan
  var savedEduId = $("#selectPendidikan").data("selected");
  fetch("/dist/js/education.json")
    .then((res) => res.json())
    .then((data) => {
      let opts = '<option value="">Pilih Pendidikan...</option>';
      data.forEach((item) => {
        let selected = item.id == savedEduId ? "selected" : "";
        opts += `<option value="${item.id}" ${selected}>${item.name}</option>`;
      });
      $("#selectPendidikan").html(opts);
    });

  // Simpan perubahan ke hidden input
  $("#selectPendidikan").on("change", function () {
    $("#inputPendidikan").val($(this).val());
  });

  // Load Pekerjaan
  var savedJobId = $("#selectPekerjaan").data("selected");
  fetch("/dist/js/jobs.json")
    .then((res) => res.json())
    .then((data) => {
      let opts = '<option value="">Pilih Pekerjaan...</option>';
      data.forEach((item) => {
        let selected = item.id == savedJobId ? "selected" : "";
        opts += `<option value="${item.id}" ${selected}>${item.name}</option>`;
      });
      $("#selectPekerjaan").html(opts).trigger("change"); // Trigger change untuk logic detail instansi
    });

  // ==========================================
  // 2. LOGIKA API WILAYAH (CASCADING + AUTO SELECT)
  // ==========================================
  // const apiBaseUrl = "https://www.emsifa.com/api-wilayah-indonesia/api";

  // Ambil ID yang tersimpan di database (dari attribut data-selected)
  var savedProv = $("#selectProvinsi").data("selected");
  var savedCity = $("#selectKota").data("selected");
  var savedDist = $("#selectKecamatan").data("selected");
  var savedSubDist = $("#selectKelurahan").data("selected");

  // Fungsi Helper untuk Load Wilayah
  function loadRegion(url, selectId, savedId, nextLoadCallback) {
    fetch(url)
      .then((res) => res.json())
      .then((data) => {
        let opts = `<option value="">Pilih...</option>`;
        data.forEach((el) => {
          let selected = el.id == savedId ? "selected" : "";
          opts += `<option value="${el.id}" data-name="${el.name}" ${selected}>${el.name}</option>`;
        });
        $(selectId).html(opts).prop("disabled", false);

        // Jika ada data tersimpan, lanjut load anak-nya
        if (savedId && nextLoadCallback) nextLoadCallback();
      });
  }

  // Rantai Loading (Chain) untuk Edit: Prov -> Kota -> Kec -> Kel
  loadRegion(
    `${apiBaseUrl}/provinces.json`,
    "#selectProvinsi",
    savedProv,
    function () {
      loadRegion(
        `${apiBaseUrl}/regencies/${savedProv}.json`,
        "#selectKota",
        savedCity,
        function () {
          loadRegion(
            `${apiBaseUrl}/districts/${savedCity}.json`,
            "#selectKecamatan",
            savedDist,
            function () {
              loadRegion(
                `${apiBaseUrl}/villages/${savedDist}.json`,
                "#selectKelurahan",
                savedSubDist,
                null
              );
            }
          );
        }
      );
    }
  );

  // Event Listener Perubahan (Sama seperti User Add)
  // Saat user mengganti Provinsi manual, reset bawahnya
  $("#selectProvinsi").on("change", function () {
    let id = $(this).val();
    $("#inputProvinsi").val(id); // Simpan ID
    $("#selectKota")
      .html('<option value="">Loading...</option>')
      .prop("disabled", true);
    // ... reset lainnya ...
    if (id) {
      fetch(`${apiBaseUrl}/regencies/${id}.json`)
        .then((res) => res.json())
        .then((data) => {
          let opts = '<option value="">Pilih Kota...</option>';
          data.forEach(
            (el) => (opts += `<option value="${el.id}">${el.name}</option>`)
          );
          $("#selectKota").html(opts).prop("disabled", false);
        });
    }
  });
  // (Copy paste event listener Kota -> Kec -> Kel dari user-add.html ke sini)
  // Pastikan update hidden input val() dengan ID

  // ==========================================
  // 3. LOGIKA ROLE & PEKERJAAN (TRIGGER ON LOAD)
  // ==========================================

  function checkRoleVisibility() {
    var selectedRoles = $("#roleSelect").val() || [];

    // Reset
    $("#wrapperDataPribadi").slideUp();
    $("#sectionDataPekerjaan").slideUp();
    $("#wrapperDetailInstansi").slideUp();
    $(".group-asesi-asesor").hide();
    $(".group-asesi-only").hide();
    $(".group-asesor-only").hide();

    if (selectedRoles.length > 0) {
      $("#wrapperDataPribadi").slideDown();

      var isAsesi = selectedRoles.includes("Asesi");
      var isAsesor = selectedRoles.includes("Asesor");

      if (isAsesi || isAsesor) {
        $(".group-asesi-asesor").show();
        $("#sectionDataPekerjaan").slideDown();
      }
      if (isAsesi) $(".group-asesi-only").show();
      if (isAsesor) $(".group-asesor-only").show();

      // Cek Detail Pekerjaan
      checkJobVisibility(isAsesi);
    }
  }

  function checkJobVisibility(isAsesi) {
    var jobId = $("#selectPekerjaan").val(); // Ambil nilai saat ini

    if (isAsesi && jobId && jobId !== "1") {
      $("#wrapperDetailInstansi").slideDown();
      $("#inputCompanyName").prop("required", true);
    } else {
      $("#wrapperDetailInstansi").slideUp();
      $("#inputCompanyName").prop("required", false);
    }
  }

  // Event Listener Role Change
  $("#roleSelect").on("change", checkRoleVisibility);

  // Event Listener Job Change
  $("#selectPekerjaan").on("change", function () {
    $("#inputPekerjaan").val($(this).val()); // Simpan ID
    var isAsesi = ($("#roleSelect").val() || []).includes("Asesi");
    checkJobVisibility(isAsesi);
  });

  // PENTING: Trigger logic saat halaman pertama kali dibuka (untuk Edit)
  // Beri sedikit delay agar Select2 Job terisi dulu oleh fetch
  setTimeout(checkRoleVisibility, 500);

  // =======================================================================
  // JAVASCRIPT UNTUK USERS ADMIN (ADD & EDIT)
  // =======================================================================

  // // 1. INISIALISASI PLUGIN
  // // ------------------------------------------
  // $(".select2").select2({ theme: "bootstrap4" });

  // // Override pesan error default jQuery Validate
  // $.extend($.validator.messages, {
  //   required: "Isilah Form Ini!",
  //   email: "Format email tidak valid",
  // });

  // // 2. FUNGSI LOGIKA TAMPILAN (REUSABLE)
  // // ------------------------------------------
  // // Fungsi ini dipanggil saat halaman load (Edit) & saat select berubah
  // function toggleSections(selectedRoles) {
  //   // A. Reset Tampilan (Sembunyikan Dulu)
  //   $("#wrapperDataPribadi").slideUp();
  //   $("#sectionDataPekerjaan").slideUp();
  //   $(".group-asesi-asesor").hide();
  //   $(".group-asesor-only").hide();
  //   $(".group-asesi-only").hide();

  //   // B. Logika Tampilkan
  //   if (selectedRoles && selectedRoles.length > 0) {
  //     // Data Pribadi selalu muncul jika ada role
  //     $("#wrapperDataPribadi").slideDown();

  //     var isAsesi =
  //       selectedRoles.includes("Asesi") || selectedRoles.includes("Asesi");
  //     var isAsesor =
  //       selectedRoles.includes("Asesor") || selectedRoles.includes("Asesor");

  //     // Field Gabungan (Asesi & Asesor)
  //     if (isAsesi || isAsesor) {
  //       $(".group-asesi-asesor").show();
  //       $("#sectionDataPekerjaan").slideDown(); // Section Pekerjaan
  //     }

  //     // Field Khusus
  //     if (isAsesi) $(".group-asesi-only").show();
  //     if (isAsesor) $(".group-asesor-only").show();

  //     // Logika detail instansi (jika Asesi) akan ditangani event listener pekerjaan
  //     if (isAsesi) {
  //       $("#selectPekerjaan").trigger("change");
  //     }
  //   }
  // }

  // 3. EVENT LISTENER ROLE
  // ------------------------------------------
  // $("#roleSelect").on("change", function () {
  //   var roles = $(this).val() || [];
  //   toggleSections(roles);
  // });

  // 4. LOGIKA KHUSUS HALAMAN EDIT
  // ------------------------------------------
  // if ($("#formEditUser").length > 0) {
  //   // A. Pre-select Role dari Database
  //   var currentRolesString = $("#roleSelect").data("current-roles"); // Baca data-attribute
  //   if (currentRolesString) {
  //     // Ubah string "ADMIN,ASESI" menjadi array ["ADMIN", "Asesi"]
  //     var rolesArray = currentRolesString.split(",");
  //     $("#roleSelect").val(rolesArray).trigger("change"); // Trigger change agar form bawah muncul!
  //   }
  // }

  // 4. LOGIKA KHUSUS HALAMAN EDIT
  // ------------------------------------------
  // if ($("#formEditUser").length > 0) {
  //   // A. Pre-select Role dari Database
  //   var currentRolesString = $("#roleSelect").data("current-roles"); // Contoh: "ADMIN,ASESI"

  //   if (currentRolesString) {
  //     var rawRoles = currentRolesString.split(",");

  //     // --- PERBAIKAN DISINI ---
  //     // Kita ubah format dari DB ("ADMIN") menjadi format HTML ("Admin")
  //     var formattedRoles = rawRoles.map(function (role) {
  //       // Ubah huruf pertama jadi Besar, sisanya Kecil
  //       // Contoh: "ADMIN" -> "Admin", "ASESI" -> "Asesi"
  //       return role.charAt(0).toUpperCase() + role.slice(1).toLowerCase();
  //     });

  //     // Masukkan nilai yang sudah diformat ke Select2
  //     $("#roleSelect").val(formattedRoles).trigger("change");
  //   }
  // }

  // // 5. VALIDASI & SUBMIT (ADD USER)
  // // ------------------------------------------
  // if ($("#formTambahUser").length > 0) {
  //   $("#formTambahUser").validate({
  //     ignore: ":hidden",
  //     rules: {
  //       username: { required: true },
  //       email: { required: true, email: true },
  //       password: { required: true, minlength: 6 }, // Password WAJIB saat Tambah
  //       roles: { required: true },
  //     },
  //     submitHandler: function (form) {
  //       handleAjaxSubmit(form, "Pengguna berhasil ditambahkan");
  //     },
  //   });
  // }

  // // 6. VALIDASI & SUBMIT (EDIT USER)
  // // ------------------------------------------
  // if ($("#formEditUser").length > 0) {
  //   $("#formEditUser").validate({
  //     ignore: ":hidden",
  //     rules: {
  //       username: { required: true },
  //       email: { required: true, email: true },
  //       // Password TIDAK WAJIB saat Edit
  //       roles: { required: true },
  //     },
  //     submitHandler: function (form) {
  //       handleAjaxSubmit(form, "Data pengguna berhasil diperbarui");
  //     },
  //   });
  // }

  // // 7. FUNGSI SUBMIT AJAX (REUSABLE)
  // // ------------------------------------------
  // function handleAjaxSubmit(form, successMessage) {
  //   var formData = new FormData(form);

  //   Swal.fire({
  //     title: "Menyimpan Data...",
  //     didOpen: () => {
  //       Swal.showLoading();
  //     },
  //   });

  //   $.ajax({
  //     url: $(form).attr("action"),
  //     type: "POST",
  //     data: formData,
  //     processData: false,
  //     contentType: false,
  //     success: function (response) {
  //       Swal.fire({
  //         title: "Berhasil!",
  //         text: successMessage,
  //         icon: "success",
  //       }).then(() => {
  //         window.location.href = "/admin/data-pengguna";
  //       });
  //     },
  //     error: function (xhr) {
  //       Swal.fire("Gagal", "Terjadi kesalahan server", "error");
  //       console.error(xhr);
  //     },
  //   });
  // }

  // ==========================================================
  // LOGIKA TAMPILAN SHOW/HIDE (REUSABLE)
  // ==========================================================
  // function toggleSections(selectedRoles) {
  //   // Normalisasi array roles menjadi UPPERCASE agar konsisten
  //   // (Misal: ["Admin", "asesi"] -> ["ADMIN", "ASESI"])
  //   const rolesChecked = (selectedRoles || []).map((r) => r.toUpperCase());

  //   // A. Reset Tampilan (Sembunyikan Dulu)
  //   $("#wrapperDataPribadi").slideUp();
  //   $("#sectionDataPekerjaan").slideUp();
  //   $("#wrapperDetailInstansi").slideUp(); // Reset detail instansi juga

  //   $(".group-asesi-asesor").hide();
  //   $(".group-asesor-only").hide();
  //   $(".group-asesi-only").hide();

  //   // B. Logika Tampilkan
  //   if (rolesChecked.length > 0) {
  //     // Data Pribadi selalu muncul jika ada role apapun
  //     $("#wrapperDataPribadi").slideDown();

  //     var isAsesi = rolesChecked.includes("Asesi");
  //     var isAsesor = rolesChecked.includes("Asesor");

  //     // Field Gabungan (Asesi & Asesor)
  //     if (isAsesi || isAsesor) {
  //       $(".group-asesi-asesor").show();
  //       $("#sectionDataPekerjaan").slideDown(); // Section Pekerjaan muncul
  //     }

  //     // Field Khusus
  //     if (isAsesi) $(".group-asesi-only").show();
  //     if (isAsesor) $(".group-asesor-only").show();

  //     // Trigger ulang logic pekerjaan untuk menampilkan/menyembunyikan detail instansi
  //     // jika user adalah ASESI
  //     if (isAsesi) {
  //       $("#selectPekerjaan").trigger("change");
  //     }
  //   }
  // }

  // // EVENT LISTENER ROLE CHANGE
  // $("#roleSelect").on("change", function () {
  //   var roles = $(this).val() || [];
  //   toggleSections(roles);
  // });

  // // ==========================================================
  // // LOGIKA PRE-SELECT ROLE DI HALAMAN EDIT (SOLUSI MASALAH ANDA)
  // // ==========================================================
  // if ($("#formEditUser").length > 0) {
  //   // Ambil data roles dari atribut HTML (format: "ADMIN,ASESI")
  //   var currentRolesString = $("#roleSelect").data("current-roles");

  //   if (currentRolesString) {
  //     var rawRoles = currentRolesString.split(",");

  //     // Transformasi Format: "ADMIN" -> "Admin" (Agar cocok dengan value option HTML)
  //     var formattedRoles = rawRoles.map(function (role) {
  //       if (!role) return "";
  //       // Ubah huruf pertama jadi Besar, sisanya Kecil
  //       return role.charAt(0).toUpperCase() + role.slice(1).toLowerCase();
  //     });

  //     // Masukkan nilai yang sudah diformat ke Select2 dan TRIGGER CHANGE
  //     // Trigger change penting agar form bagian bawah langsung muncul!
  //     $("#roleSelect").val(formattedRoles).trigger("change");
  //   }
  // }

  // // ==========================================================
  // // TANDA TANGAN LOGIKA
  // // =========================================================
  // // --- 1. INISIALISASI VARIABLE ---
  // var wrapper = document.getElementById("signature-pad");
  // var canvas = document.getElementById("signature-canvas");
  // var signaturePad;

  // // --- 2. FUNGSI RESIZE CANVAS (Agar tidak pecah/blur) ---
  // function resizeCanvas() {

  //   var ratio = Math.max(window.devicePixelRatio || 1, 1);
  //   // Set dimensi canvas sesuai ukuran layar
  //   canvas.width = canvas.offsetWidth * ratio;
  //   canvas.height = canvas.offsetHeight * ratio;
  //   canvas.getContext("2d").scale(ratio, ratio);

  //   // Jika sudah ada signaturePad, clear dulu agar bersih saat resize ulang
  //   if (signaturePad) {
  //     // signaturePad.clear(); // Opsional: Hapus atau biarkan (biasanya clear saat resize)
  //   }
  // }

  // // --- 3. EVENT SAAT MODAL DIBUKA ---
  // $("#modalSignature").on("shown.bs.modal", function () {
  //   // Resize canvas saat modal muncul (PENTING! Jika tidak, canvas akan error size-nya)
  //   resizeCanvas();

  //   var existingSignature = $("#signatureInput").val();

  //   // Jika sudah ada tanda tangan tersimpan (Base64), muat ke canvas
  //   if (existingSignature && existingSignature.trim() !== "") {
  //     signaturePad.fromDataURL(existingSignature, { ratio: 1.5 });
  //   }

  //   // Inisialisasi SignaturePad
  //   if (!signaturePad) {
  //     signaturePad = new SignaturePad(canvas, {
  //       backgroundColor: "rgba(255, 255, 255, 0)", // Transparan
  //       penColor: "rgb(0, 0, 0)", // Warna Tinta Hitam
  //     });
  //   }
  // });

  // // --- 4. TOMBOL HAPUS KANVAS ---
  // $("#btnClear").on("click", function () {
  //   if (signaturePad) {
  //     signaturePad.clear();
  //   }
  // });

  // // --- 3. UPLOAD PNG ---
  // $("#btnUpload").on("click", function () {
  //   $("#uploadSigFile").click();
  // });

  // $("#uploadSigFile").on("change", function (e) {
  //   var file = e.target.files[0];

  //   if (!file) return;

  //   // Validasi Tipe File (Hanya PNG)
  //   if (file.type !== "image/png") {
  //     Swal.fire({
  //       icon: "error",
  //       title: "Hanya file format PNG yang diperbolehkan.",
  //     });
  //     this.value = ""; // Reset input
  //     return;
  //   }

  //   var reader = new FileReader();
  //   reader.onload = function (event) {
  //     // Fitur hebat SignaturePad: bisa load langsung dari Data URL
  //     // Ini otomatis menggambar image ke canvas
  //     signaturePad.fromDataURL(event.target.result);
  //   };
  //   reader.readAsDataURL(file);
  // });

  // // --- 6. TOMBOL SIMPAN ---
  // $("#btnSaveSignature").on("click", function () {
  //   if (signaturePad.isEmpty()) {
  //     Swal.fire({ icon: "warning", title: "Silakan tanda tangan dulu!" });
  //   } else {
  //     try {
  //       var dataURL = signaturePad.toDataURL("image/png");
  //       $("#signatureInput").val(dataURL);

  //       // --- BARIS BARU: PAKSA VALIDASI ULANG ---
  //       // Agar pesan error "Isilah Form Ini!" langsung hilang saat user klik simpan
  //       $("#signatureInput").valid();
  //       // ----------------------------------------

  //       // ... (Sisa kode update tampilan tombol & close modal tetap sama) ...
  //       var btnTrigger = $("#btnTriggerSignature");
  //       btnTrigger
  //         .removeClass("btn-outline-primary btn-outline-danger")
  //         .addClass("btn-outline-success");
  //       btnTrigger.html('<i class="fas fa-eye"></i> Lihat Tanda Tangan');

  //       $("#imgPreview").attr("src", dataURL);
  //       $("#signaturePreview").show();

  //       $("#modalSignature")
  //         .find('[data-dismiss="modal"]')
  //         .first()
  //         .trigger("click");
  //     } catch (error) {
  //       console.error(error);
  //     }
  //   }
  // });

  // // ==========================================
  // // LOGIKA TANDA TANGAN (EDIT MODE)
  // // ==========================================

  // var canvas = document.getElementById("signature-canvas");
  // var signaturePad = new SignaturePad(canvas, {
  //   backgroundColor: "rgba(255, 255, 255, 0)",
  //   penColor: "rgb(0, 0, 0)",
  // });

  // function resizeCanvas() {
  //   var ratio = Math.max(window.devicePixelRatio || 1, 1);
  //   var data = signaturePad.toData(); // Simpan goresan saat ini (jika ada)
  //   canvas.width = canvas.offsetWidth * ratio;
  //   canvas.height = canvas.offsetHeight * ratio;
  //   canvas.getContext("2d").scale(ratio, ratio);
  //   signaturePad.clear();
  //   signaturePad.fromData(data); // Kembalikan goresan
  // }

  // // 1. SAAT MODAL DIBUKA -> LOAD TANDA TANGAN DARI INPUT HIDDEN
  // $("#modalSignature").on("shown.bs.modal", function () {
  //   resizeCanvas();

  //   // Ambil value dari input hidden (yang sudah diisi Thymeleaf dari DB)
  //   var existingSignature = $("#signatureInput").val();

  //   // Jika ada isinya, gambar ulang di canvas
  //   if (existingSignature && existingSignature.trim() !== "") {
  //     // fromDataURL: Fungsi ajaib SignaturePad untuk merender Base64 ke Canvas
  //     signaturePad.fromDataURL(existingSignature, { ratio: 1 });
  //   }
  // });

  // // 2. TOMBOL HAPUS (Bersihkan Canvas & Input Hidden)
  // $("#btnClear").on("click", function () {
  //   signaturePad.clear();
  // });

  // // 3. TOMBOL SIMPAN (Update Input Hidden & Preview)
  // $("#btnSaveSignature").on("click", function () {
  //   if (signaturePad.isEmpty()) {
  //     // Jika user menghapus kanvas lalu simpan -> Berarti hapus tanda tangan
  //     $("#signatureInput").val(""); // Kosongkan input
  //     $("#signaturePreview").hide(); // Sembunyikan preview
  //     $("#btnTriggerSignature").html(
  //       '<i class="fas fa-pen-nib"></i> Masukkan Tanda Tangan'
  //     );
  //     $("#modalSignature").modal("hide");
  //   } else {
  //     // Ambil data baru
  //     var dataURL = signaturePad.toDataURL("image/png");

  //     // Update Input Hidden
  //     $("#signatureInput").val(dataURL);

  //     // Update Preview Image
  //     $("#imgPreview").attr("src", dataURL);
  //     $("#signaturePreview").show();

  //     // Update Text Tombol
  //     $("#btnTriggerSignature").html(
  //       '<i class="fas fa-pen-nib"></i> Ubah Tanda Tangan'
  //     );

  //     // Tutup Modal
  //     $("#modalSignature").modal("hide");
  //   }
  // });

  // ==========================================================
  // LOGIKA TANDA TANGAN (TERINTEGRASI: ADD & EDIT)
  // ==========================================================

  $(document).ready(function () {
    // --- 1. INISIALISASI VARIABLE ---
    var canvas = document.getElementById("signature-canvas");
    // Inisialisasi SignaturePad sekali saja
    var signaturePad = new SignaturePad(canvas, {
      backgroundColor: "rgba(255, 255, 255, 0)", // Transparan
      penColor: "rgb(0, 0, 0)", // Warna Tinta Hitam
    });

    // --- 2. FUNGSI RESIZE CANVAS ---
    // Penting agar canvas tajam di layar HP/Retina
    function resizeCanvas() {
      var ratio = Math.max(window.devicePixelRatio || 1, 1);

      // Simpan data saat ini agar tidak hilang saat resize
      var data = signaturePad.toData();

      canvas.width = canvas.offsetWidth * ratio;
      canvas.height = canvas.offsetHeight * ratio;
      canvas.getContext("2d").scale(ratio, ratio);

      signaturePad.clear(); // Bersihkan context
      signaturePad.fromData(data); // Kembalikan coretan (jika ada)
    }

    // --- 3. EVENT SAAT MODAL DIBUKA ---
    $("#modalSignature").on("shown.bs.modal", function () {
      resizeCanvas(); // Atur ukuran canvas

      // Cek apakah ada data tersimpan di Input Hidden?
      var existingSignature = $("#signatureInput").val();

      // Jika ada (Mode Edit / Revisi), muat gambar ke canvas
      if (existingSignature && existingSignature.trim() !== "") {
        signaturePad.fromDataURL(existingSignature, { ratio: 1.5 });
      }
    });

    // --- 4. TOMBOL HAPUS / BERSIHKAN KANVAS ---
    $("#btnClear").on("click", function () {
      signaturePad.clear();
    });

    // --- 5. FITUR UPLOAD PNG ---
    $("#btnUpload").on("click", function () {
      $("#uploadSigFile").click();
    });

    $("#uploadSigFile").on("change", function (e) {
      var file = e.target.files[0];
      if (!file) return;

      // Validasi Tipe File
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
        // Load gambar ke canvas (bisa ditimpa coretan jika mau)
        signaturePad.fromDataURL(event.target.result);
      };
      reader.readAsDataURL(file);
    });

    // --- 6. TOMBOL SIMPAN (LOGIKA GABUNGAN PINTAR) ---
    $("#btnSaveSignature").on("click", function () {
      // KONDISI A: Canvas Kosong (User menghapus tanda tangan)
      if (signaturePad.isEmpty()) {
        // Kita kosongkan nilai input hidden
        $("#signatureInput").val("");

        // Sembunyikan preview
        $("#signaturePreview").hide();
        $("#imgPreview").attr("src", "");

        // Reset tombol pemicu ke tampilan awal
        var btnTrigger = $("#btnTriggerSignature");
        btnTrigger
          .removeClass("btn-success btn-outline-success")
          .addClass("btn-outline-primary")
          .html('<i class="fas fa-pen-nib"></i> Masukkan Tanda Tangan');
      }
      // KONDISI B: Ada Tanda Tangan (Baru / Edit)
      else {
        // Ambil data Base64
        var dataURL = signaturePad.toDataURL("image/png");

        // Update Input Hidden
        $("#signatureInput").val(dataURL);

        // Update UI (Tombol jadi Hijau & Text Berubah)
        var btnTrigger = $("#btnTriggerSignature");
        btnTrigger
          .removeClass("btn-outline-primary btn-outline-danger")
          .addClass("btn-outline-success")
          .html('<i class="fas fa-eye"></i> Lihat Tanda Tangan');

        // Tampilkan Preview
        $("#imgPreview").attr("src", dataURL);
        $("#signaturePreview").show();
      }

      // --- VALIDASI & TUTUP MODAL ---

      // 1. Trigger Validasi jQuery Validate
      // Ini kuncinya: Biarkan aturan 'rules' di validasi form yang menentukan
      // apakah field kosong itu boleh (valid) atau error (invalid).
      if ($("#signatureInput").valid()) {
        // Jika valid (atau required=false), tutup modal
        $("#modalSignature")
          .find('[data-dismiss="modal"]')
          .first()
          .trigger("click");
      } else {
        // Jika tidak valid (Required tapi kosong), Munculkan Alert
        // Opsional: Tutup modal tetap dilakukan agar user liat error di form utama
        $("#modalSignature")
          .find('[data-dismiss="modal"]')
          .first()
          .trigger("click");

        // Atau bisa pakai Swal jika mau tetap di modal
        // Swal.fire({ icon: "warning", title: "Tanda tangan wajib diisi!" });
      }
    });

    // Listener Window Resize (Opsional: Agar canvas menyesuaikan jika layar diputar/diubah)
    window.addEventListener("resize", resizeCanvas);
  });
});
