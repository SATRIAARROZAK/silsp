// =======================================================================
// JAVASCRIPT UTAMA UNTUK MANAJEMEN PENGGUNA (ADD, EDIT, VIEW, LIST)
// =======================================================================

$(document).ready(function () {
  // ==========================================
  // 1. KONFIGURASI GLOBAL & UTILS
  // ==========================================
  const API_WILAYAH_URL = "https://www.emsifa.com/api-wilayah-indonesia/api";

  // // Inisialisasi Select2
  // $(".select2").select2({
  //   theme: "bootstrap4",
  // });

  // Override pesan error default jQuery Validate
  $.extend($.validator.messages, {
    required: "Isilah Form Ini!",
    email: "Harap gunakan format email yang benar (contoh: user@domain.com)",
    url: "Harap masukkan URL yang valid.",
    date: "Harap masukkan tanggal yang valid.",
    number: "Harap masukkan angka yang valid.",
    digits: "Hanya boleh angka.",
  });

  // Validasi Real-time saat Select2 berubah
  $(".select2").on("change", function () {
    $(this).valid();
  });

  // CSS Inject untuk Border Merah Select2 saat Error
  $(
    "<style>.is-invalid-border { border-color: #dc3545 !important; }</style>"
  ).appendTo("head");

  // ==========================================
  // 2. LOGIKA DELETE (SWEETALERT)
  // ==========================================
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

  // ==========================================
  // 3. LOGIKA ROLE & PEKERJAAN (SHOW/HIDE)
  // ==========================================

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

      var isAsesi = selectedRoles.includes("Asesi") || selectedRoles.includes("ASESI");
      var isAsesor = selectedRoles.includes("Asesor") || selectedRoles.includes("ASESOR");

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
    var isAsesi = ($("#roleSelect").val() || []).includes("Asesi") || ($("#roleSelect").val() || []).includes("ASESI");
    checkJobVisibility(isAsesi);
  });

  // ==========================================
  // 4. LOAD DATA JSON STATIS
  // ==========================================

  // Load Pendidikan
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

  // Load Pekerjaan
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

  // ==========================================
  // 5. LOGIKA API WILAYAH
  // ==========================================
  
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

  var savedProv = $("#selectProvinsi").data("selected");
  var savedCity = $("#selectKota").data("selected");
  var savedDist = $("#selectKecamatan").data("selected");
  var savedSubDist = $("#selectKelurahan").data("selected");

  loadRegion(`${API_WILAYAH_URL}/provinces.json`, "#selectProvinsi", savedProv, function () {
      loadRegion(`${API_WILAYAH_URL}/regencies/${savedProv}.json`, "#selectKota", savedCity, function () {
          loadRegion(`${API_WILAYAH_URL}/districts/${savedCity}.json`, "#selectKecamatan", savedDist, function () {
              loadRegion(`${API_WILAYAH_URL}/villages/${savedDist}.json`, "#selectKelurahan", savedSubDist, null);
          });
      });
  });

  $("#selectProvinsi").on("change", function () {
    let id = $(this).val();
    $("#inputProvinsi").val(id);
    $("#selectKota").html('<option value="">Loading...</option>').prop("disabled", true);
    $("#selectKecamatan").html('<option value="">Pilih Kecamatan...</option>').prop("disabled", true);
    $("#selectKelurahan").html('<option value="">Pilih Kelurahan...</option>').prop("disabled", true);

    if (id) {
      loadRegion(`${API_WILAYAH_URL}/regencies/${id}.json`, "#selectKota", null, null);
    }
  });

  $("#selectKota").on("change", function () {
    let id = $(this).val();
    $("#inputKota").val(id);
    $("#selectKecamatan").html('<option value="">Loading...</option>').prop("disabled", true);
    $("#selectKelurahan").html('<option value="">Pilih Kelurahan...</option>').prop("disabled", true);

    if (id) {
      loadRegion(`${API_WILAYAH_URL}/districts/${id}.json`, "#selectKecamatan", null, null);
    }
  });

  $("#selectKecamatan").on("change", function () {
    let id = $(this).val();
    $("#inputKecamatan").val(id);
    $("#selectKelurahan").html('<option value="">Loading...</option>').prop("disabled", true);

    if (id) {
      loadRegion(`${API_WILAYAH_URL}/villages/${id}.json`, "#selectKelurahan", null, null);
    }
  });

  $("#selectKelurahan").on("change", function () {
    $("#inputKelurahan").val($(this).val());
  });


  // ==========================================
  // 6. VALIDASI FORM & AJAX SUBMIT
  // ==========================================
  
  function commonSubmitHandler(form, successTitle, successText) {
    var formData = new FormData(form);

    // --- VALIDASI TANDA TANGAN FINAL SEBELUM SUBMIT ---
    var sigVal = $("#signatureInput").val();
    if (!sigVal || sigVal.trim() === "") {
        Swal.fire({
            icon: 'warning',
            title: 'Isi tanda tangan terlebih dahulu', // Pesan sesuai request
            confirmButtonColor: '#3085d6',
        });
        // Scroll ke tombol tanda tangan agar user sadar
        $('html, body').animate({
            scrollTop: $("#btnTriggerSignature").offset().top - 200
        }, 500);
        
        // Munculkan error di bawah tombol juga
        $("#signatureInput").valid(); 
        return false; // Batalkan submit
    }
    // --------------------------------------------------

    Swal.fire({
      title: "Menyimpan Data...",
      text: "Mohon tunggu sebentar",
      allowOutsideClick: false,
      didOpen: () => { Swal.showLoading(); },
    });

    $.ajax({
      url: $(form).attr("action"),
      type: "POST",
      data: formData,
      processData: false,
      contentType: false,
      success: function (response) {
        Swal.fire({
          title: successTitle,
          text: successText,
          icon: "success",
          showCancelButton: true,
          confirmButtonText: "OK (Lihat List)",
          cancelButtonText: "Tetap Disini",
          confirmButtonColor: "#3085d6",
          cancelButtonColor: "#28a745",
        }).then((result) => {
          if (result.isConfirmed) {
            window.location.href = "/admin/data-pengguna";
          } else {
            if ($(form).attr('id') === 'formTambahUser') {
                form.reset();
                $(".select2").val(null).trigger("change");
                if(typeof signaturePad !== 'undefined') signaturePad.clear();
                $("#signatureInput").val("");
                $("#signaturePreview").hide();
                $("#btnTriggerSignature")
                    .removeClass("btn-success btn-outline-success btn-outline-danger")
                    .addClass("btn-outline-primary")
                    .html('<i class="fas fa-pen-nib"></i> Masukkan Tanda Tangan');
                $("html, body").animate({ scrollTop: 0 }, "fast");
            }
          }
        });
      },
      error: function (xhr) {
        Swal.fire({
          icon: "error",
          title: "Gagal Menyimpan",
          text: "Terjadi kesalahan pada server. Silakan coba lagi.",
        });
        console.error(xhr);
      },
    });
    return false;
  }

  var validationConfig = {
    ignore: function (index, element) {
      if ($(element).attr("id") === "signatureInput") return false; // Jangan ignore signature
      if ($(element).hasClass("select2-hidden-accessible")) {
        return $(element).next(".select2-container").is(":hidden");
      }
      return $(element).is(":hidden");
    },
    rules: {
      email: { required: true, email: true },
      username: { required: true },
      fullName: { required: true },
      province: { required: true },
      city: { required: true },
      district: { required: true },
      subDistrict: { required: true },
      jobType: { required: true },
      // Signature WAJIB diisi baik Add maupun Edit
      signatureBase64: { required: true }, 
    },
    messages: {
        // Custom message khusus signature (Optional, bisa pakai default "Isilah form ini")
        signatureBase64: "Isi tanda tangan terlebih dahulu" 
    },
    errorElement: "span",
    errorPlacement: function (error, element) {
      error.addClass("invalid-feedback");
      if (element.hasClass("select2") || element.hasClass("select2-hidden-accessible")) {
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
        $(element).next(".select2").find(".select2-selection").addClass("is-invalid-border");
      }
      if ($(element).attr("id") === "signatureInput") {
        $("#btnTriggerSignature").addClass("btn-outline-danger").removeClass("btn-outline-primary btn-success");
      }
    },
    unhighlight: function (element) {
      $(element).removeClass("is-invalid").addClass("is-valid");
      if ($(element).hasClass("select2-hidden-accessible")) {
        $(element).next(".select2").find(".select2-selection").removeClass("is-invalid-border");
      }
      if ($(element).attr("id") === "signatureInput") {
        $("#btnTriggerSignature").removeClass("btn-outline-danger");
      }
    }
  };

  // Terapkan Validasi Form Tambah
  $("#formTambahUser").validate($.extend({}, validationConfig, {
    submitHandler: function(form) {
        commonSubmitHandler(form, "Berhasil!", "Pengguna Berhasil ditambahkan");
    }
  }));

  // Terapkan Validasi Form Edit
  $("#formEditUser").validate($.extend({}, validationConfig, {
    submitHandler: function(form) {
        commonSubmitHandler(form, "Berhasil!", "Data Pengguna Berhasil diperbarui");
    }
  }));


  // ==========================================================
  // 7. TANDA TANGAN LOGIKA
  // ==========================================================
  
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
          Swal.fire({ icon: "error", title: "Hanya file format PNG yang diperbolehkan." });
          this.value = "";
          return;
        }
        var reader = new FileReader();
        reader.onload = function (event) {
          signaturePad.fromDataURL(event.target.result);
        };
        reader.readAsDataURL(file);
      });

      // TOMBOL SIMPAN DI MODAL
      $("#btnSaveSignature").on("click", function () {
        
        // JIKA KOSONG (USER HAPUS CANVAS) -> TETAP SIMPAN KOSONG KE HIDDEN
        // TAPI NANTI AKAN DICEGAH OLEH VALIDASI UTAMA SAAT SUBMIT FORM
        if (signaturePad.isEmpty()) {
          $("#signatureInput").val(""); 
          $("#signaturePreview").hide();
          $("#imgPreview").attr("src", "");
          
          var btnTrigger = $("#btnTriggerSignature");
          btnTrigger
            .removeClass("btn-success btn-outline-success")
            .addClass("btn-outline-primary")
            .html('<i class="fas fa-pen-nib"></i> Masukkan Tanda Tangan');
        } 
        else {
          var dataURL = signaturePad.toDataURL("image/png");
          $("#signatureInput").val(dataURL);

          var btnTrigger = $("#btnTriggerSignature");
          btnTrigger
            .removeClass("btn-outline-primary btn-outline-danger")
            .addClass("btn-outline-success")
            .html('<i class="fas fa-eye"></i> Lihat Tanda Tangan');

          $("#imgPreview").attr("src", dataURL);
          $("#signaturePreview").show();
        }

        // TRIGGER VALIDASI SAAT ITU JUGA
        // Agar jika user mengisi, error merah langsung hilang
        $("#signatureInput").valid(); 
        
        // Tutup Modal
        $("#modalSignature").find('[data-dismiss="modal"]').first().trigger("click");
      });

      window.addEventListener("resize", resizeCanvas);
  }

  // ==========================================================
  // 8. LOGIKA HALAMAN VIEW
  // ==========================================================
  if ($("#viewProv").length) {
    function setRegionName(url, elementId, fallbackId) {
      if (!fallbackId) { $("#" + elementId).val("-"); return; }
      $.ajax({
        url: API_WILAYAH_URL + url,
        method: "GET",
        success: function (data) {
          let found = data.find((item) => item.id == fallbackId);
          $("#" + elementId).val(found ? found.name : fallbackId);
        },
        error: function () { $("#" + elementId).val("Error Loading"); },
      });
    }

    let provId = $("#viewProv").data("id");
    setRegionName("/provinces.json", "viewProv", provId);

    let cityId = $("#viewCity").data("id");
    if (provId && cityId) setRegionName(`/regencies/${provId}.json`, "viewCity", cityId);

    let distId = $("#viewDist").data("id");
    if (cityId && distId) setRegionName(`/districts/${cityId}.json`, "viewDist", distId);

    let subDistId = $("#viewSubDist").data("id");
    if (distId && subDistId) setRegionName(`/villages/${distId}.json`, "viewSubDist", subDistId);
  }

});