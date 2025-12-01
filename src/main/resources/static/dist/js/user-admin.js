$(document).ready(function () {
  // ==========================================
  // 1. KONFIGURASI GLOBAL
  // ==========================================
  const API_WILAYAH_URL = "https://www.emsifa.com/api-wilayah-indonesia/api";

  // Inisialisasi Select2
  $(".select2").select2({ theme: "bootstrap4" });

  // Custom Validasi jQuery: Regex Username
  $.validator.addMethod(
    "usernameRegex",
    function (value, element) {
      return this.optional(element) || /^[a-zA-Z0-9_]+$/.test(value);
    },
    "Username tidak boleh mengandung spasi atau karakter spesial."
  );

  // Override pesan default
  $.extend($.validator.messages, {
    required: "Isilah Form Ini!",
    email: "Format email tidak valid (contoh: user@domain.com)",
    usernameRegex: "Username hanya boleh huruf dan angka (tanpa spasi).",
  });

  // Validasi Real-time Select2
  $(".select2").on("change", function () {
    $(this).valid();
  });

  // Inject CSS Border Error
  $(
    "<style>.is-invalid-border { border-color: #dc3545 !important; }</style>"
  ).appendTo("head");

  function checkRoleVisibility() {
    var selectedRoles = $("#roleSelect").val() || [];

    // Reset Tampilan
    $("#wrapperDataPribadi").slideUp();
    $("#sectionDataPekerjaan").slideUp();
    $("#wrapperDetailInstansi").slideUp();

    $(".group-asesi-asesor").hide();
    $(".group-asesor-only").hide();
    $(".group-asesi-only").hide();

    if (selectedRoles.length > 0) {
      $("#wrapperDataPribadi").slideDown();

      var isAsesi =
        selectedRoles.includes("Asesi") || selectedRoles.includes("ASESI");
      var isAsesor =
        selectedRoles.includes("Asesor") || selectedRoles.includes("ASESOR");

      if (isAsesi || isAsesor) {
        $(".group-asesi-asesor").show();
        $("#sectionDataPekerjaan").slideDown();
      }

      if (isAsesi) $(".group-asesi-only").show();
      if (isAsesor) $(".group-asesor-only").show();

      checkJobVisibility(isAsesi);
    }
  }

  function checkJobVisibility(isAsesi) {
    var jobId = $("#selectPekerjaan").val() || $("#inputPekerjaan").val();

    if (isAsesi && jobId && jobId != "1") {
      $("#wrapperDetailInstansi").slideDown();
      $("#inputCompanyName").prop("required", true);
    } else {
      $("#wrapperDetailInstansi").slideUp();
      $("#inputCompanyName").prop("required", false);
    }
  }

  $("#roleSelect").on("change", checkRoleVisibility);

  $("#selectPekerjaan").on("change", function () {
    $("#inputPekerjaan").val($(this).val());
    var isAsesi =
      ($("#roleSelect").val() || []).includes("Asesi") ||
      ($("#roleSelect").val() || []).includes("ASESI");
    checkJobVisibility(isAsesi);
  });

  // ==========================================
  // 3. LOAD DATA JSON & API WILAYAH (ADD & EDIT)
  // ==========================================

  // -- PENDIDIKAN --
  var savedEduId = $("#selectPendidikan").data("selected");
  fetch("/dist/js/education.json")
    .then((res) => res.json())
    .then((data) => {
      let options = '<option value="">Pilih Pendidikan...</option>';
      data.forEach((item) => {
        let selected = item.id == savedEduId ? "selected" : "";
        options += `<option value="${item.id}" ${selected}>${item.name}</option>`;
      });
      $("#selectPendidikan").html(options);
    });
  $("#selectPendidikan").on("change", function () {
    $("#inputPendidikan").val($(this).val());
  });

  // -- PEKERJAAN --
  var savedJobId = $("#selectPekerjaan").data("selected");
  fetch("/dist/js/jobs.json")
    .then((res) => res.json())
    .then((data) => {
      let options = '<option value="">Pilih Pekerjaan...</option>';
      data.forEach((item) => {
        let selected = item.id == savedJobId ? "selected" : "";
        options += `<option value="${item.id}" data-name="${item.name}" ${selected}>${item.name}</option>`;
      });
      $("#selectPekerjaan").html(options);
      setTimeout(checkRoleVisibility, 500);
    });

  // -- WILAYAH (Helper Function) --
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
        if (savedId && nextLoadCallback) nextLoadCallback();
      });
  }

  // Chain Load untuk Edit
  var savedProv = $("#selectProvinsi").data("selected");
  var savedCity = $("#selectKota").data("selected");
  var savedDist = $("#selectKecamatan").data("selected");
  var savedSubDist = $("#selectKelurahan").data("selected");

  loadRegion(
    `${API_WILAYAH_URL}/provinces.json`,
    "#selectProvinsi",
    savedProv,
    function () {
      loadRegion(
        `${API_WILAYAH_URL}/regencies/${savedProv}.json`,
        "#selectKota",
        savedCity,
        function () {
          loadRegion(
            `${API_WILAYAH_URL}/districts/${savedCity}.json`,
            "#selectKecamatan",
            savedDist,
            function () {
              loadRegion(
                `${API_WILAYAH_URL}/villages/${savedDist}.json`,
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

  // Event Listeners Wilayah
  $("#selectProvinsi").on("change", function () {
    let id = $(this).val();
    $("#inputProvinsi").val(id);
    $("#selectKota")
      .html('<option value="">Loading...</option>')
      .prop("disabled", true);
    $("#selectKecamatan")
      .html('<option value="">Pilih Kecamatan...</option>')
      .prop("disabled", true);
    $("#selectKelurahan")
      .html('<option value="">Pilih Kelurahan...</option>')
      .prop("disabled", true);
    if (id)
      loadRegion(
        `${API_WILAYAH_URL}/regencies/${id}.json`,
        "#selectKota",
        null,
        null
      );
  });

  $("#selectKota").on("change", function () {
    let id = $(this).val();
    $("#inputKota").val(id);
    $("#selectKecamatan")
      .html('<option value="">Loading...</option>')
      .prop("disabled", true);
    $("#selectKelurahan")
      .html('<option value="">Pilih Kelurahan...</option>')
      .prop("disabled", true);
    if (id)
      loadRegion(
        `${API_WILAYAH_URL}/districts/${id}.json`,
        "#selectKecamatan",
        null,
        null
      );
  });

  $("#selectKecamatan").on("change", function () {
    let id = $(this).val();
    $("#inputKecamatan").val(id);
    $("#selectKelurahan")
      .html('<option value="">Loading...</option>')
      .prop("disabled", true);
    if (id)
      loadRegion(
        `${API_WILAYAH_URL}/villages/${id}.json`,
        "#selectKelurahan",
        null,
        null
      );
  });

  $("#selectKelurahan").on("change", function () {
    $("#inputKelurahan").val($(this).val());
  });

  // ==========================================
  // 4. VALIDASI FORM & AJAX SUBMIT
  // ==========================================

  // Konfigurasi Validasi Universal
  var validationConfig = {
    ignore: function (index, element) {
      if ($(element).attr("id") === "signatureInput") return false; // Jangan ignore signature

      // Khusus Select2: Cek visible container
      if ($(element).hasClass("select2-hidden-accessible")) {
        return $(element).next(".select2-container").is(":hidden");
      }
      return $(element).is(":hidden");
    },

    // --- ATURAN VALIDASI ---
    // --- ATURAN VALIDASI ---
    rules: {
      email: {
        required: true,
        email: true,
        // VALIDASI REAL-TIME SERVER
        remote: {
          url: "/admin/api/check-email",
          type: "GET",
          data: {
            email: function () {
              return $("input[name='email']").val();
            },
            id: function () {
              return $("input[name='id']").val();
            }, // Kirim ID untuk mode Edit
          },
        },
      },
      username: {
        required: true,
        usernameRegex: true,
        // VALIDASI REAL-TIME SERVER
        remote: {
          url: "/admin/api/check-username",
          type: "GET",
          data: {
            username: function () {
              return $("input[name='username']").val();
            },
            id: function () {
              return $("input[name='id']").val();
            },
          },
        },
      },
      fullName: { required: true },
      provinceId: { required: true },
      cityId: { required: true },
      districtId: { required: true },
      subDistrictId: { required: true },
      jobTypeId: { required: true },
      signatureBase64: { required: true },
    },

    // --- PESAN ERROR KHUSUS ---
    messages: {
      signatureBase64: "Isi tanda tangan terlebih dahulu",
      username: {
        remote: "Username sudah terdaftar", // Pesan merah yang muncul di bawah input
      },
      email: {
        remote: "Email sudah terdaftar", // Pesan merah yang muncul di bawah input
      },
    },

    // --- PESAN VALIDASI KHUSUS ---
    messages: {
      email: {
        remote: "Email sudah terdaftar. Silakan gunakan email lain.",
      },
      username: {
        remote: "Username sudah digunakan. Silakan pilih username lain.",
      },
      signatureBase64: "Isi tanda tangan terlebih dahulu",
    },

    invalidHandler: function (event, validator) {
      // Cek jumlah error
      var errors = validator.numberOfInvalids();

      if (errors) {
        // Munculkan SweetAlert Peringatan
        Swal.fire({
          icon: "warning",
          title: "Perhatian!",
          text: "Harap isi semua data dengan tanda (*)", // Pesan sesuai request
          confirmButtonColor: "#3085d6",
          confirmButtonText: "Oke",
        }).then(() => {
          // FITUR TAMBAHAN: Scroll otomatis ke field error pertama
          // Agar user tidak bingung mencari mana yang kurang
          if (validator.errorList.length > 0) {
            var firstErrorElement = $(validator.errorList[0].element);

            // Khusus Tanda Tangan (karena inputnya hidden)
            if (firstErrorElement.attr("id") === "signatureInput") {
              $("html, body").animate(
                {
                  scrollTop: $("#btnTriggerSignature").offset().top - 200,
                },
                500
              );
            } else {
              // Input biasa
              $("html, body").animate(
                {
                  scrollTop: firstErrorElement.offset().top - 150,
                },
                500
              );
              firstErrorElement.focus();
            }
          }
        });
      }
    },

    errorElement: "span",
    errorPlacement: function (error, element) {
      error.addClass("invalid-feedback");
      if (
        element.hasClass("select2") ||
        element.hasClass("select2-hidden-accessible")
      ) {
        error.insertAfter(element.next(".select2"));
      } else if (element.attr("id") === "signatureInput") {
        error.insertAfter("#btnTriggerSignature");
        error.css("display", "block");
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
      if ($(element).attr("id") === "signatureInput") {
        $("#btnTriggerSignature")
          .addClass("btn-outline-danger")
          .removeClass("btn-outline-primary btn-success");
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
      if ($(element).attr("id") === "signatureInput") {
        $("#btnTriggerSignature").removeClass("btn-outline-danger");
      }
    },
  };

  // Handler Submit
  function commonSubmitHandler(form, successTitle) {
    var formData = new FormData(form);

    // --- VALIDASI MANUAL TANDA TANGAN ---
    // Pastikan user tidak bypass
    var sigVal = $("#signatureInput").val();
    if (!sigVal || sigVal.trim() === "") {
      // Scroll ke tombol
      $("html, body").animate(
        { scrollTop: $("#btnTriggerSignature").offset().top - 200 },
        500
      );

      // PENTING: SWEETALERT ERROR SESUAI REQUEST (TIDAK TUTUP FORM)
      Swal.fire({
        icon: "warning",
        title: "Isi Tanda Tangan Terlebih Dahulu",
        confirmButtonColor: "#3085d6",
        confirmButtonText: "Oke",
      });

      // Trigger error text merah di bawah tombol
      $("#signatureInput").valid();
      return false;
    }

    Swal.fire({
      title: "Menyimpan Data...",
      text: "Mohon tunggu sebentar",
      allowOutsideClick: false,
      didOpen: () => {
        Swal.showLoading();
      },
    });

    $.ajax({
      url: $(form).attr("action"),
      type: "POST",
      data: formData,
      processData: false,
      contentType: false,
      success: function (response) {
        // Parse JSON response dari Controller
        var res =
          typeof response === "string" ? JSON.parse(response) : response;

        Swal.fire({
          title: "Berhasil!",
          text: res.message,
          icon: "success",
          showDenyButton: true, // Tombol Tambahan (Lihat Detail)
          showCancelButton: true, // Tombol Batal/Tetap
          confirmButtonText: "OK (Lihat List)",
          denyButtonText: "Lihat Detail",
          cancelButtonText:
            $(form).attr("id") === "formTambahUser" ? "Tambah" : "Kembali",
          confirmButtonColor: "#3085d6", // Biru
          denyButtonColor: "#17a2b8", // Teal/Info
          cancelButtonColor: "#28a745", // Hijau
        }).then((result) => {
          if (result.isConfirmed) {
            // KLIK OK -> LIST
            window.location.href = "/admin/data-pengguna";
          } else if (result.isDenied) {
            // KLIK LIHAT DETAIL -> Halaman Detail
            // Kita gunakan ID yang dikirim dari controller
            window.location.href = "/admin/data-pengguna/view-users/" + res.id;
          } else {
            if ($(form).attr("id") === "formTambahUser") {
              form.reset();
              $(".select2").val(null).trigger("change");
              if (typeof signaturePad !== "undefined") signaturePad.clear();
              $("#signatureInput").val("");
              $("#signaturePreview").hide();
              $("#btnTriggerSignature")
                .removeClass(
                  "btn-success btn-outline-success btn-outline-danger"
                )
                .addClass("btn-outline-primary")
                .html('<i class="fas fa-pen-nib"></i> Masukkan Tanda Tangan');
              $("html, body").animate({ scrollTop: 0 }, "fast");
            }
          }
        });
      },
    });
    return false;
  }

  $("#formTambahUser").validate(
    $.extend({}, validationConfig, {
      submitHandler: function (form) {
        commonSubmitHandler(form, "Berhasil!");
      },
    })
  );

  $("#formEditUser").validate(
    $.extend({}, validationConfig, {
      submitHandler: function (form) {
        commonSubmitHandler(form, "Berhasil!");
      },
    })
  );

  // ==========================================
  // 5. TANDA TANGAN LOGIKA
  // ==========================================
  var canvas = document.getElementById("signature-canvas");
  if (canvas) {
    var signaturePad = new SignaturePad(canvas, {
      backgroundColor: "rgba(255, 255, 255, 0)",
      penColor: "rgb(0, 0, 0)",
    });

    function resizeCanvas() {
      var ratio = Math.max(window.devicePixelRatio || 1, 1);
      var data = signaturePad.toData();
      canvas.width = canvas.offsetWidth * ratio;
      canvas.height = canvas.offsetHeight * ratio;
      canvas.getContext("2d").scale(ratio, ratio);
      signaturePad.clear();
      signaturePad.fromData(data);
    }

    $("#modalSignature").on("shown.bs.modal", function () {
      resizeCanvas();
      var existingSignature = $("#signatureInput").val();
      if (existingSignature && existingSignature.trim() !== "") {
        signaturePad.fromDataURL(existingSignature, { ratio: 1.5 });
      }
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
      if (file.type !== "image/png") {
        Swal.fire({ icon: "error", title: "Hanya format PNG diperbolehkan." });
        this.value = "";
        return;
      }
      var reader = new FileReader();
      reader.onload = function (event) {
        signaturePad.fromDataURL(event.target.result);
      };
      reader.readAsDataURL(file);
    });

    $("#btnSaveSignature").on("click", function () {
      // JIKA KOSONG -> TETAP KOSONGKAN HIDDEN & SHOW ERROR ALERT
      if (signaturePad.isEmpty()) {
        $("#signatureInput").val("");
        $("#signaturePreview").hide();

        // Reset tombol
        $("#btnTriggerSignature")
          .removeClass("btn-success btn-outline-success")
          .addClass("btn-outline-primary")
          .html('<i class="fas fa-pen-nib"></i> Masukkan Tanda Tangan');

        // ALERT SESUAI REQUEST (TIDAK TUTUP MODAL)
        Swal.fire({
          icon: "warning",
          title: "Isi Tanda Tangan Terlebih Dahulu",
          confirmButtonText: "Oke",
          confirmButtonColor: "#3085d6",
        });
        // Modal tetap terbuka, user harus isi
      } else {
        // JIKA TERISI -> SIMPAN & TUTUP MODAL
        var dataURL = signaturePad.toDataURL("image/png");
        $("#signatureInput").val(dataURL);

        $("#btnTriggerSignature")
          .removeClass("btn-outline-primary btn-outline-danger")
          .addClass("btn-outline-success")
          .html('<i class="fas fa-eye"></i> Lihat Tanda Tangan');

        $("#imgPreview").attr("src", dataURL);
        $("#signaturePreview").show();

        // Hapus error validasi jika ada
        $("#signatureInput").valid();

        // Tutup Modal
        $("#modalSignature")
          .find('[data-dismiss="modal"]')
          .first()
          .trigger("click");
      }
    });

    window.addEventListener("resize", resizeCanvas);
  }
});
