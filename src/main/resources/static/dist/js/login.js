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
$(document).ready(function () {
  $("#loginForm").on("submit", function (e) {
    var isValid = true;

    // Helper untuk menampilkan error pada parent input-group
    function setError(inputElement) {
        // Cari parent .input-group dan tambahkan class is-invalid
        $(inputElement).closest('.input-group').addClass('is-invalid');
        // Tampilkan pesan error di bawahnya
        $(inputElement).closest('.form-group').find('.invalid-feedback').show();
    }

    function removeError(inputElement) {
        $(inputElement).closest('.input-group').removeClass('is-invalid');
        $(inputElement).closest('.form-group').find('.invalid-feedback').hide();
    }

    // 1. Validasi Username/Email
    var username = $("#username");
    if (username.val().trim() === "") {
      setError(username);
      isValid = false;
    } else {
      removeError(username);
    }

    // 2. Validasi Password
    var password = $("#password");
    if (password.val().trim() === "") {
      setError(password);
      isValid = false;
    } else {
      removeError(password);
    }

    // Jika tidak valid, stop submit
    if (!isValid) {
      e.preventDefault();
    }
  });

  // Hilangkan error saat user mengetik
  $("#username, #password").on("input", function () {
    $(this).closest('.input-group').removeClass('is-invalid');
    $(this).closest('.form-group').find('.invalid-feedback').hide();
  });
});