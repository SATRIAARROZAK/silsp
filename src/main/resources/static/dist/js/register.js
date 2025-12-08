$(document).ready(function () {
  // 1. LOGIKA GANTI ROLE (TAMPILKAN FORM KHUSUS)
  $("#roleSelect").on("change", function () {
    var role = $(this).val();

    // Reset Tampilan
    $("#section-asesi").slideUp();
    $("#section-asesor").slideUp();

    // Reset Requirement
    $("#noMet").prop("required", false);

    if (role === "Asesi") {
      $("#section-asesi").slideDown();
    } else if (role === "Asesor") {
      $("#section-asesor").slideDown();
      $("#noMet").prop("required", true); // Wajibkan No MET
    }
  });

  // 2. HELPER ERROR
  function setError(input, message) {
    var group = $(input).closest(".input-group");
    group.addClass("is-invalid");
    group.parent().find(".invalid-feedback").text(message).show();
  }

  function resetError(input) {
    var group = $(input).closest(".input-group");
    group.removeClass("is-invalid");
    group.parent().find(".invalid-feedback").hide();
  }

  // Hapus error saat mengetik
  $("input, select").on("input change", function () {
    resetError(this);
  });

  // 3. SUBMIT HANDLER
  $("#registerForm").on("submit", function (e) {
    e.preventDefault();
    var isValid = true;

    // A. Validasi Field Umum
    $(this)
      .find("input[required], select[required]")
      .each(function () {
        if ($(this).val().trim() === "") {
          setError(this, "Kolom ini wajib diisi");
          isValid = false;
        }
      });

    // B. Validasi Password Length
    var pass = $("#password").val();
    if (pass.length < 6) {
      setError($("#password"), "Password minimal 6 karakter");
      isValid = false;
    }

    if (!isValid) return;

    // C. AJAX POST
    var btn = $(".btn-submit");
    var originalText = btn.text();
    btn.text("Memproses...").prop("disabled", true);

    $.ajax({
      url: $(this).attr("action"),
      type: "POST",
      data: $(this).serialize(),
      success: function (response) {
        var res =
          typeof response === "string" ? JSON.parse(response) : response;

        Swal.fire({
          icon: "success",
          title: "Berhasil!",
          text: res.message,
          confirmButtonText: "Ke Halaman Login",
          confirmButtonColor: "#3085d6",
        }).then((result) => {
          if (result.isConfirmed) {
            window.location.href = "/login";
          }
        });
      },
      error: function (xhr) {
        btn.text(originalText).prop("disabled", false);
        var res = { message: "Terjadi kesalahan server" };
        try {
          res = JSON.parse(xhr.responseText);
        } catch (e) {}

        if (res.field) {
          // Jika error spesifik (username/email)
          setError($("#" + res.field), res.message);
          // Scroll ke error
          $("html, body").animate(
            { scrollTop: $("#" + res.field).offset().top - 100 },
            500
          );
        } else {
          Swal.fire("Gagal", res.message, "error");
        }
      },
    });
  });
});
