 $(document).ready(function () {
        const emailInput = $("#email");
        const emailGroup = $("#emailGroup");
        const emailError = $("#emailError");

        // Regex Email Standar (harus ada @ dan domain)
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+[^\s@]+$/;

        // 1. VALIDASI REALTIME (Saat mengetik)
        emailInput.on("input", function () {
          var val = $(this).val().trim();

          if (val === "") {
            // Jika kosong (saat mengetik dihapus habis), hilangkan error dulu biar bersih
            emailGroup.removeClass("is-invalid");
          } else if (!emailRegex.test(val)) {
            // Format Salah
            emailGroup.addClass("is-invalid");
            emailError.text("Gunakan Format @domain");
          } else {
            // Valid
            emailGroup.removeClass("is-invalid");
          }
        });
        $("#forgotForm").on("submit", function (e) {
          e.preventDefault();

          var val = emailInput.val().trim();
          var Toast = Swal.mixin({
            toast: true,
            position: "top-end",
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            didOpen: (toast) => {
              toast.addEventListener("mouseenter", Swal.stopTimer);
              toast.addEventListener("mouseleave", Swal.resumeTimer);
            },
          });

          // Validasi Kosong
          if (!val) {
            emailGroup.addClass("is-invalid");
            emailError.text("Mohon Isi Email");
            emailInput.focus();
            return;
          }

          // Validasi Format Final
          if (!emailRegex.test(val)) {
            emailGroup.addClass("is-invalid");
            emailError.text("Gunakan Format @domain");
            emailInput.focus();
            return;
          }

          var formData = $(this).serialize();

          Swal.fire({
            title: "Sedang Memproses...",
            text: "Mohon tunggu sebentar",
            allowOutsideClick: false,
            didOpen: () => Swal.showLoading(),
          });

          $.post($(this).attr("action"), formData, function (response) {
            var res =
              typeof response === "string" ? JSON.parse(response) : response;

            // 2. FITUR DIRECT KE LOGIN + TOAST
            // Simpan pesan sukses di LocalStorage agar bisa muncul di halaman Login
            localStorage.setItem("resetSuccessMessage", res.message); // SUKSES: SweetAlert Timer & Redirect
            Toast.fire({
              icon: "success",
              title: "Data tautan reset kata sandi berhasil dikirimkan",
            }).then(() => {
              // Pindah ke Login setelah alert
              window.location.href = "/login";
            });
          }).fail(function (xhr) {
            var msg = "Terjadi kesalahan sistem";
            if (xhr.responseJSON && xhr.responseJSON.message) {
              msg = xhr.responseJSON.message;
            }
            Swal.fire("Gagal", msg, "error");
          });
        });
      });