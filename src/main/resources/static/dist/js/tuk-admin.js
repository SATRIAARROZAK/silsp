$(document).ready(function () {
  // ==========================================
  // 1. KONFIGURASI VALIDASI & STYLE
  // ==========================================

  // Inisialisasi Select2
  $(".select2").select2({ theme: "bootstrap4" });

  // CSS Inject untuk Border Merah Select2 saat Error (Sama seperti user-admin.js)
  $(
    "<style>.is-invalid-border { border-color: #dc3545 !important; }</style>"
  ).appendTo("head");

  // Override pesan error default
  $.extend($.validator.messages, {
    required: "Isilah Form Ini!",
    email: "Format email tidak valid",
    number: "Harus berupa angka",
  });

  // Validasi Real-time saat Select2 berubah
  $(".select2").on("change", function () {
    $(this).valid();
  });

  // ==========================================
  // 2. API WILAYAH (Provinsi -> Kota)
  // ==========================================
  const API_WILAYAH_URL = "https://www.emsifa.com/api-wilayah-indonesia/api";

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

  // Change Provinsi -> Load Kota
  $("#selectProvinsi").on("change", function () {
    let id = $(this).val();
    $("#selectKota")
      .html('<option value="">Loading...</option>')
      .prop("disabled", true);

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

  // ==========================================
  // 3. VALIDASI FORM & SUBMIT HANDLER (AJAX)
  // ==========================================
  $("#form-tambah-tuk").validate({
    // LOGIKA IGNORE: Jangan validasi elemen hidden, KECUALI Select2 yang terlihat
    ignore: function (index, element) {
      if ($(element).hasClass("select2-hidden-accessible")) {
        return $(element).next(".select2-container").is(":hidden");
      }
      return $(element).is(":hidden");
    },

    // ATURAN VALIDASI (Sesuai name di HTML)
    rules: {
      namaTuk: { required: true },
      jenisTukId: { required: true },
      noTelp: { required: true, number: true },
      email: { required: true, email: true },
      provinceId: { required: true },
      cityId: { required: true },
      alamat: { required: true },
    },

    // PENEMPATAN PESAN ERROR (Sama seperti User Admin)
    errorElement: "span",
    errorPlacement: function (error, element) {
      error.addClass("invalid-feedback");
      if (
        element.hasClass("select2") ||
        element.hasClass("select2-hidden-accessible")
      ) {
        error.insertAfter(element.next(".select2"));
      } else {
        element.closest(".form-group").append(error);
      }
    },

    // HIGHLIGHT (BORDER MERAH)
    highlight: function (element) {
      $(element).addClass("is-invalid").removeClass("is-valid");
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .addClass("is-invalid-border");
      }
    },

    // UNHIGHLIGHT (HAPUS BORDER MERAH)
    unhighlight: function (element) {
      $(element).removeClass("is-invalid").addClass("is-valid");
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .removeClass("is-invalid-border");
      }
    },

    // --- SUBMIT HANDLER (AJAX) ---
    // Hanya dijalankan jika form VALID
    submitHandler: function (form) {
      var formData = new FormData(form);

      Swal.fire({
        title: "Menyimpan...",
        text: "Mohon tunggu sebentar",
        allowOutsideClick: false,
        didOpen: () => Swal.showLoading(),
      });

      $.ajax({
        url: $(form).attr("action"),
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (res) {
          var response = typeof res === "string" ? JSON.parse(res) : res;
          Swal.fire({
            title: "Berhasil!",
            text: response.message,
            icon: "success",
          }).then(() => {
            window.location.href = "/admin/tuk";
          });
        },
        error: function (xhr) {
          var msg = "Terjadi kesalahan server";
          try {
            msg = JSON.parse(xhr.responseText).message;
          } catch (e) {}
          Swal.fire("Gagal", msg, "error");
        },
      });

      return false; // Mencegah refresh halaman
    },
  });

  // ==========================================
  // 4. LOGIKA LIST PAGE (CONVERT ID WILAYAH -> NAMA)
  // ==========================================

  // Cek apakah kita ada di halaman list (apakah ada elemen dengan ID wilayah-*)
  if ($('div[id^="wilayah-"]').length > 0) {
    const API_URL = "https://www.emsifa.com/api-wilayah-indonesia/api";

    // Fungsi Helper untuk Fetch Nama
    // Menggunakan Async/Await agar kode lebih bersih, atau Promise biasa
    function getRegionName(url, idToFind) {
      return $.ajax({
        url: API_URL + url,
        method: "GET",
      }).then(function (data) {
        let found = data.find((item) => item.id == idToFind);
        return found ? found.name : "-";
      });
    }

    // Loop setiap baris data TUK
    $('div[id^="wilayah-"]').each(function () {
      var element = $(this);
      var provId = element.data("prov");
      var cityId = element.data("city");

      // 1. Ambil Nama Provinsi
      if (provId) {
        getRegionName("/provinces.json", provId).then(function (name) {
          // --- PERBAIKAN LOGIKA FORMAT NAMA ---
          var formattedName = name.replace(/\w\S*/g, function (txt) {
            // Daftar singkatan yang HARUS KAPITAL (tambahkan jika ada lagi)
            var exceptions = ["DKI", "DIY", "NAD"];

            // Cek apakah kata ini ada di daftar exception?
            // Jika ada, biarkan kapital semua. Jika tidak, Title Case.
            if (exceptions.includes(txt.toUpperCase())) {
              return txt.toUpperCase();
            } else {
              return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
            }
          });
          // ------------------------------------

          element.find(".prov-name").text(formattedName);
        });
      } else {
        element.find(".prov-name").text("-");
      }

      // 2. Ambil Nama Kota
      if (provId && cityId) {
        getRegionName(`/regencies/${provId}.json`, cityId).then(function (
          name
        ) {
          // Gunakan logika yang sama untuk Kota
          var formattedName = name.replace(/\w\S*/g, function (txt) {
            var exceptions = ["DKI", "DIY", "NAD", "ADM"]; // ADM untuk Kepulauan Seribu
            if (exceptions.includes(txt.toUpperCase())) {
              return txt.toUpperCase();
            } else {
              return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
            }
          });

          element.find(".city-name").text(formattedName);
        });
      } else {
        element.find(".city-name").text("-");
      }
    });
  }

  // (Jangan lupa tambahkan logika Delete SweetAlert juga disini jika belum ada)
  $(document).on("click", ".delete-button", function (e) {
    e.preventDefault();
    var link = $(this).attr("href");
    Swal.fire({
      title: "Yakin Hapus TUK?",
      text: "Data tidak dapat dikembalikan!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Ya, Hapus!",
    }).then((result) => {
      if (result.isConfirmed) window.location.href = link;
    });
  });
});
