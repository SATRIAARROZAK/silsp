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
// LOGIKA VALIDASI & ERROR HANDLING
// ==========================================
$(document).ready(function () {
  // --- FUNGSI BANTUAN VISUAL ---
  function setError(inputElement, message) {
    // Tambahkan class error ke parent (input-group) agar border merah satu paket
    $(inputElement).closest(".input-group").addClass("is-invalid");

    // Ubah teks pesan error dan tampilkan
    var feedback = $(inputElement)
      .closest(".form-group")
      .find(".invalid-feedback");
    feedback.text(message);
    feedback.show();
  }

  function removeError(inputElement) {
    $(inputElement).closest(".input-group").removeClass("is-invalid");
    // Kembalikan pesan default (opsional) dan sembunyikan
    var feedback = $(inputElement)
      .closest(".form-group")
      .find(".invalid-feedback");
    feedback.hide();
  }

  // --- 1. CEK ERROR DARI SERVER (Login Gagal) ---
  // Spring Security melempar balik ke /login?error jika gagal
  const urlParams = new URLSearchParams(window.location.search);
  if (urlParams.has("error")) {
    // Tandai KEDUA kolom sebagai salah (Security Best Practice)
    // Pesan: "Data pengguna dan password salah" sesuai request
    setError($("#username"), "Data pengguna dan password salah");
    setError($("#password"), "Data pengguna dan password salah");
  }

  // --- 2. VALIDASI FORM SAAT SUBMIT (Client Side) ---
  $("#loginForm").on("submit", function (e) {
    var isValid = true;

    // Validasi Username Kosong
    var username = $("#username");
    if (username.val().trim() === "") {
      setError(username, "Harap isi email atau username");
      isValid = false;
    } else {
      // Jika form di-submit ulang dan isi sudah benar, hapus error lama
      // Kecuali jika error dari server (kita biarkan server yang handle redirect)
      if (!urlParams.has("error")) removeError(username);
    }

    // Validasi Password Kosong
    var password = $("#password");
    if (password.val().trim() === "") {
      setError(password, "Harap Masukan Password");
      isValid = false;
    } else {
      if (!urlParams.has("error")) removeError(password);
    }

    // Jika tidak valid, stop submit
    if (!isValid) {
      e.preventDefault();
    }
  });

  // --- 3. HAPUS ERROR SAAT MENGETIK ---
  // Memberikan feedback instan ke user
  $("#username, #password").on("input", function () {
    removeError(this);

    // Jika ada parameter ?error di URL, kita hapus agar bersih saat user mencoba lagi
    if (urlParams.has("error")) {
      // Hapus parameter query tanpa reload halaman agar validasi server visualnya hilang
      var newUrl =
        window.location.protocol +
        "//" +
        window.location.host +
        window.location.pathname;
      window.history.pushState({ path: newUrl }, "", newUrl);
    }
  });
});
