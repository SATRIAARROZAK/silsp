// ========================================================
// JAVASCRIPT UTAMA TUK ADMIN (LIST, ADD, EDIT, VIEW)
// ========================================================

$(document).ready(function () {
  // ==========================================
  // 1. KONFIGURASI GLOBAL
  // ==========================================
  const API_WILAYAH_URL = "https://www.emsifa.com/api-wilayah-indonesia/api";

  // Inisialisasi Select2
  // $(".select2").select2({ theme: "bootstrap4" });

  // CSS Inject untuk Border Merah Select2 saat Error
  $(
    "<style>.is-invalid-border { border-color: #dc3545 !important; }</style>"
  ).appendTo("head");

  

  // ==========================================
  // 2. LOGIKA DELETE (LIST PAGE)
  // ==========================================
  $(".delete-button-tuk").on("click", function (e) {
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

  // ==========================================
  // 3. HELPER FORMAT NAMA WILAYAH
  // ==========================================
  function formatRegionName(name) {
    return name.replace(/\w\S*/g, function (txt) {
      var exceptions = ["DKI", "DI", "NAD", "ADM"];
      if (exceptions.includes(txt.toUpperCase())) return txt.toUpperCase();
      return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    });
  }

  // ==========================================
  // 4. LOGIKA WILAYAH (ADD & EDIT)
  // ==========================================
  // Hanya jalan jika ada dropdown provinsi
  if ($("#selectProvinsi").length) {
    var savedProvId = $("#selectProvinsi").data("selected");
    var savedCityId = $("#selectKota").data("selected");

    // Fungsi Helper Load Region
    function loadRegion(url, selectElement, savedId, callback) {
      fetch(url)
        .then((res) => res.json())
        .then((data) => {
          let opts = '<option value="">Pilih...</option>';
          data.forEach((el) => {
            let selected = el.id == savedId ? "selected" : "";
            opts += `<option value="${el.id}" ${selected}>${el.name}</option>`;
          });
          selectElement.html(opts).prop("disabled", false);

          if (callback) callback();
        });
    }

    // CHAIN LOADING: Provinsi -> Kota (Untuk Edit)
    loadRegion(
      `${API_WILAYAH_URL}/provinces.json`,
      $("#selectProvinsi"),
      savedProvId,
      function () {
        if (savedProvId) {
          loadRegion(
            `${API_WILAYAH_URL}/regencies/${savedProvId}.json`,
            $("#selectKota"),
            savedCityId
          );
        }
      }
    );

    // EVENT LISTENER: GANTI PROVINSI
    $("#selectProvinsi").on("change", function () {
      let id = $(this).val();
      $("#selectKota")
        .html('<option value="">Loading...</option>')
        .prop("disabled", true);
      if (id) {
        loadRegion(
          `${API_WILAYAH_URL}/regencies/${id}.json`,
          $("#selectKota"),
          null
        );
      }
    });
  }

  // ==========================================
  // 5. LOGIKA HALAMAN VIEW (Detail TUK)
  // ==========================================
  if ($("#viewProv").length) {
    function setViewRegionName(url, elementId, idToFind) {
      if (!idToFind) {
        $("#" + elementId).val("-");
        return;
      }
      $.ajax({
        url: API_WILAYAH_URL + url,
        method: "GET",
        success: function (data) {
          let found = data.find((item) => item.id == idToFind);
          if (found) {
            $("#" + elementId).val(formatRegionName(found.name));
          } else {
            $("#" + elementId).val(idToFind);
          }
        },
        error: function () {
          $("#" + elementId).val("Error Loading");
        },
      });
    }

    let provId = $("#viewProv").data("id");
    setViewRegionName("/provinces.json", "viewProv", provId);

    let cityId = $("#viewCity").data("id");
    if (provId && cityId)
      setViewRegionName(`/regencies/${provId}.json`, "viewCity", cityId);
    else $("#viewCity").val("-");
  }

  // ==========================================
  // 6. LOGIKA HALAMAN LIST (Convert ID -> Nama)
  // ==========================================
  if ($('div[id^="wilayah-"]').length > 0) {
    $('div[id^="wilayah-"]').each(function () {
      var element = $(this);
      var provId = element.data("prov");
      var cityId = element.data("city");

      if (provId) {
        $.ajax({
          url: API_WILAYAH_URL + "/provinces.json",
          method: "GET",
        }).then(function (data) {
          let found = data.find((i) => i.id == provId);
          if (found)
            element.find(".prov-name").text(formatRegionName(found.name));
        });
      } else {
        element.find(".prov-name").text("-");
      }

      if (provId && cityId) {
        $.ajax({
          url: API_WILAYAH_URL + `/regencies/${provId}.json`,
          method: "GET",
        }).then(function (data) {
          let found = data.find((i) => i.id == cityId);
          if (found)
            element.find(".city-name").text(formatRegionName(found.name));
        });
      } else {
        element.find(".city-name").text("-");
      }
    });
  }

  // ==========================================
  // 7. VALIDASI & SUBMIT (ADD & EDIT)
  // ==========================================
  var validationConfig = {
    ignore: function (index, element) {
      // Validasi Select2 yang visible
      if ($(element).hasClass("select2-hidden-accessible")) {
        return $(element).next(".select2-container").is(":hidden");
      }
      return $(element).is(":hidden");
    },
    rules: {
      namaTuk: { required: true },
      jenisTukId: { required: true },
      noTelp: { required: true, number: true },
      email: { required: true, email: true },
      provinceId: { required: true },
      cityId: { required: true },
      alamat: { required: true },
    },
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
    highlight: function (element) {
      $(element).addClass("is-invalid").removeClass("is-valid");
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .addClass("is-invalid-border");
      }
    },
    unhighlight: function (element) {
      $(element).removeClass("is-invalid").addClass("is-valid");
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element)
          .next(".select2")
          .find(".select2-selection")
          .removeClass("is-invalid-border");
      }
    },

    // --- FITUR BARU: INVALID HANDLER (SCROLL KE ERROR) ---
    invalidHandler: function (event, validator) {
      var errors = validator.numberOfInvalids();
      if (errors) {
        Swal.fire({
          icon: "warning",
          title: "Perhatian!",
          text: "Harap isi semua data dengan tanda (*)",
          confirmButtonColor: "#3085d6",
          confirmButtonText: "Oke",
        }).then(() => {
          if (validator.errorList.length > 0) {
            var firstErrorElement = $(validator.errorList[0].element);

            // Scroll ke elemen error pertama
            $("html, body").animate(
              {
                scrollTop: firstErrorElement.offset().top - 150,
              },
              500
            );

            // Coba fokus
            firstErrorElement.focus();
          }
        });
      }
    },

    // Submit Handler
    submitHandler: function (form) {
      var formData = new FormData(form);
      var isEdit = $(form).attr("id") === "form-edit-tuk";

      Swal.fire({
        title: isEdit ? "Memperbarui..." : "Menyimpan...",
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
      return false;
    },
  };

  // Terapkan Validasi
  $("#form-tambah-tuk").validate(validationConfig);
  $("#form-edit-tuk").validate(validationConfig);
});
