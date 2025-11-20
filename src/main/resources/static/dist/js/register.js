document
  .getElementById("registerForm")
  .addEventListener("submit", function (event) {
    // 1. Reset status validasi
    let isValid = true;
    document.querySelectorAll(".input").forEach((input) => {
      input.classList.remove("is-invalid");
    });

    // 2. Validasi Username
    const username = document.getElementById("username");
    if (username.value.trim() === "") {
      showError(username, "Username tidak boleh kosong.");
      isValid = false;
    }

    // 3. Validasi Email
    const email = document.getElementById("email");
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email.value)) {
      showError(email, "Format email tidak valid.");
      isValid = false;
    }

    // 4. Validasi Password
    const password = document.getElementById("password");
    if (password.value.length < 8) {
      showError(password, "Password minimal 8 karakter.");
      isValid = false;
    }

    // 5. Validasi Konfirmasi Password
    const confirmPassword = document.getElementById("confirmPassword");
    if (password.value !== confirmPassword.value) {
      showError(confirmPassword, "Password tidak cocok.");
      isValid = false;
    } else if (confirmPassword.value.trim() === "") {
      showError(confirmPassword, "Konfirmasi password tidak boleh kosong.");
      isValid = false;
    }

    // 6. LOGIKA PENGIRIMAN (Bagian yang diperbaiki)
    if (!isValid) {
      // Jika TIDAK valid, kita cegah pengiriman form
      event.preventDefault();
    } else {
      // Jika VALID, biarkan form terkirim secara alami ke server.
      // Tidak perlu 'this.submit()' manual karena kita tidak memanggil preventDefault() di sini.
      // Form akan otomatis mencari th:action="@{/register}"
    }
  });

function showError(inputElement, message) {
  inputElement.classList.add("is-invalid");
  const errorElement = inputElement.nextElementSibling;
  if (errorElement && errorElement.classList.contains("invalid-feedback")) {
    errorElement.textContent = message;
  }
}
