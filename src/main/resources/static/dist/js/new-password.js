$(document).ready(function () {
        // Helper Function untuk Reset Error saat mengetik
        function clearError(groupElement) {
          $(groupElement).removeClass("is-invalid");
        }

        $("#password").on("input", function () {
          clearError("#passGroup");
        });
        $("#confPassword").on("input", function () {
          clearError("#confGroup");
        });

        // SUBMIT LOGIC
        $("#resetForm").on("submit", function (e) {
          e.preventDefault();

          var p1 = $("#password").val();
          var p2 = $("#confPassword").val();
          var isValid = true;

          // 1. Validasi Password Kosong
          if (!p1) {
            $("#passGroup").addClass("is-invalid");
            $("#passError").text("Mohon Masukan Kata Sandi");
            isValid = false;
          }
          // 2. Validasi Minimal 8 Karakter
          else if (p1.length < 8) {
            $("#passGroup").addClass("is-invalid");
            $("#passError").text("Minimal 8 karakter");
            isValid = false;
          }

          // 3. Validasi Konfirmasi Kosong
          if (!p2) {
            $("#confGroup").addClass("is-invalid");
            $("#confError").text("Mohon Masukan Konfirmasi Kata Sandi");
            isValid = false;
          }
          // 4. Validasi Kecocokan
          else if (p1 !== p2) {
            $("#confGroup").addClass("is-invalid");
            $("#confError").text("Kata sandi tidak cocok!");
            isValid = false;
          }

          if (!isValid) return; // Stop jika ada error

          // KIRIM DATA
          var formData = $(this).serialize();
          Swal.fire({
            title: "Menyimpan...",
            didOpen: () => Swal.showLoading(),
          });

          $.post($(this).attr("action"), formData, function (res) {
            var response = typeof res === "string" ? JSON.parse(res) : res;

            // SUKSES
            Swal.fire({
              icon: "success",
              title: "Sukses!",
              text: "Kata Sandi Berhasil Dibuat",
              confirmButtonText: "Login Sekarang",
              confirmButtonColor: "#28a745",
            }).then((result) => {
              if (result.isConfirmed) {
                window.location.href = "/login";
              }
            });
          }).fail(function (xhr) {
            var msg = "Token kedaluwarsa atau terjadi kesalahan";
            if (xhr.responseJSON && xhr.responseJSON.message)
              msg = xhr.responseJSON.message;
            Swal.fire("Gagal", msg, "error");
          });
        });
      });