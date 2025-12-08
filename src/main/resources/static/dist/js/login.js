// ==========================================
// FUNGSI TOGGLE PASSWORD (LIHAT/SEMBUNYI)
// ==========================================
function togglePassword() {
  var passwordInput = document.getElementById("password");
  var eyeIcon = document.getElementById("eyeIcon");

  if (passwordInput.type === "password") {
    passwordInput.type = "text";
    eyeIcon.classList.remove("fa-eye-slash");
    eyeIcon.classList.add("fa-eye");
  } else {
    passwordInput.type = "password";
    eyeIcon.classList.remove("fa-eye");
    eyeIcon.classList.add("fa-eye-slash");
  }
}

// ==========================================
// VALIDASI FORM SEBELUM SUBMIT
// ==========================================
// $(document).ready(function () {
//   $("#loginForm").on("submit", function (e) {
//     var isValid = true;

//     // Helper untuk menampilkan error pada parent input-group
//     function setError(inputElement) {
//       // Cari parent .input-group dan tambahkan class is-invalid
//       $(inputElement).closest(".input-group").addClass("is-invalid");
//       // Tampilkan pesan error di bawahnya
//       $(inputElement).closest(".form-group").find(".invalid-feedback").show();
//     }

//     function removeError(inputElement) {
//       $(inputElement).closest(".input-group").removeClass("is-invalid");
//       $(inputElement).closest(".form-group").find(".invalid-feedback").hide();
//     }

//     // 1. Validasi Username/Email
//     var username = $("#username");
//     if (username.val().trim() === "") {
//       setError(username);
//       isValid = false;
//     } else {
//       removeError(username);
//     }

//     // 2. Validasi Password
//     var password = $("#password");
//     if (password.val().trim() === "") {
//       setError(password);
//       isValid = false;
//     } else {
//       removeError(password);
//     }

//     // Jika tidak valid, stop submit
//     if (!isValid) return;

//     // 2. Validasi Server Side (AJAX)
//     var btn = $(".btn-login");
//     var originalText = btn.text();
//     btn.text("Memuat...").prop("disabled", true);

//     $.ajax({
//       url: $(this).attr("action"),
//       type: "POST",
//       data: $(this).serialize(),
//       success: function (response) {
//         if (response.status === "success") {
//           window.location.href = response.redirectUrl;
//         }
//       },
//       error: function (xhr) {
//         btn.text(originalText).prop("disabled", false);

//         var res = JSON.parse(xhr.responseText);

//         // Reset error lama
//         resetErrors();

//         // --- PERUBAHAN TAMPILAN AMAN ---
//         // Tampilkan error di KEDUA kolom agar hacker bingung mana yang salah
//         showFieldError("username", res.message); // "Username atau Password salah"
//         showFieldError("password", res.message); // "Username atau Password salah"

//         // Kosongkan password agar user mengetik ulang (UX standard)
//         $("#password").val("");
//       },
//     });
//   });

//   // Hilangkan error saat user mengetik
//   $("#username, #password").on("input", function () {
//     $(this).closest(".input-group").removeClass("is-invalid");
//     $(this).closest(".form-group").find(".invalid-feedback").hide();
//   });
// });

// ==========================================
// VALIDASI & AJAX LOGIN
// ==========================================
$(document).ready(function () {
  // Fungsi Helper Menampilkan Error
  function showFieldError(inputName, message) {
    var input = $("#" + inputName); // selector ID
    // Tambahkan border merah ke parent .input-group
    input.closest(".input-group").addClass("is-invalid");
    // Tampilkan teks pesan error
    var feedback = input.closest(".form-group").find(".invalid-feedback");
    feedback.text(message); // Update teks sesuai respon server
    feedback.show();
  }

  // Fungsi Helper Reset Error
  function resetErrors() {
    $(".input-group").removeClass("is-invalid");
    $(".invalid-feedback").hide();
  }

  // --- SUBMIT HANDLER ---
  $("#loginForm").on("submit", function (e) {
    e.preventDefault(); // Mencegah reload halaman

    var isValid = true;
    var usernameVal = $("#username").val().trim();
    var passwordVal = $("#password").val().trim();

    // 1. Validasi Client Side (Kosong)
    if (usernameVal === "") {
      showFieldError("username", "Harap isi email atau username");
      isValid = false;
    } else {
      // Reset error username sementara
      $("#username").closest(".input-group").removeClass("is-invalid");
      $("#username").closest(".form-group").find(".invalid-feedback").hide();
    }

    if (passwordVal === "") {
      showFieldError("password", "Harap Masukan Password");
      isValid = false;
    } else {
      // Reset error password sementara
      $("#password").closest(".input-group").removeClass("is-invalid");
      $("#password").closest(".form-group").find(".invalid-feedback").hide();
    }

    if (!isValid) return; // Stop jika kosong

    // 2. Validasi Server Side (AJAX)
    // Tampilkan loading di tombol (opsional)
    var btn = $(".btn-login");
    var originalText = btn.text();
    btn.text("Memuat...").prop("disabled", true);

    $.ajax({
      url: $(this).attr("action"), // Mengarah ke /login
      type: "POST",
      data: $(this).serialize(), // Mengirim username & password
      success: function (response) {
        // LOGIN SUKSES
        if (response.status === "success") {
          window.location.href = response.redirectUrl;
        }
      },
      error: function (xhr) {
        btn.text(originalText).prop("disabled", false);

        // Reset error visual sebelumnya
        resetErrors();

        try {
          // Coba parsing JSON dari server
          var res = JSON.parse(xhr.responseText);

          // Tampilkan pesan error (Versi Aman/Generic)
          showFieldError("username", res.message);
          showFieldError("password", res.message);

          // Kosongkan password
          $("#password").val("");
        } catch (e) {
          // JIKA PARSING GAGAL (Biasanya karena error 403 Forbidden CSRF atau 500 HTML)
          console.error("Respon bukan JSON:", xhr.responseText);

          // Tampilkan pesan fallback
          alert(
            "Gagal Login. Kemungkinan sesi habis atau masalah server. Silakan refresh halaman."
          );
        }
      },
    });
  });

  // Hapus error saat mengetik
  $("#username, #password").on("input", function () {
    $(this).closest(".input-group").removeClass("is-invalid");
    $(this).closest(".form-group").find(".invalid-feedback").hide();
  });
});
