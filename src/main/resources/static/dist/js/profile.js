$(document).ready(function () {
    // ==========================================
    // 1. PREVIEW FOTO REALTIME
    // ==========================================
    $("#avatarInput").change(function () {
        const file = this.files[0];
        if (file) {
            // Validasi Ukuran (Max 2MB)
            if (file.size > 2 * 1024 * 1024) {
                Swal.fire("Error", "Ukuran foto maksimal 2MB!", "error");
                this.value = ""; // Reset input
                return;
            }

            // Preview
            let reader = new FileReader();
            reader.onload = function (event) {
                $("#avatarPreview").attr("src", event.target.result);
            };
            reader.readAsDataURL(file);
        }
    });

    // ==========================================
    // 2. LOGIKA MODAL PASSWORD
    // ==========================================
    $("#btnApplyPass").click(function () {
        var current = $("#m_current").val();
        var newVal = $("#m_new").val();
        var confirm = $("#m_confirm").val();

        // Validasi Sederhana di Modal
        if (!current || !newVal || !confirm) {
            Swal.fire("Peringatan", "Mohon lengkapi semua kolom password", "warning");
            return;
        }

        if (newVal.length < 8) {
            Swal.fire("Peringatan", "Password baru minimal 8 karakter", "warning");
            return;
        }

        if (newVal !== confirm) {
            $("#passMsg").removeClass("d-none");
            return;
        } else {
            $("#passMsg").addClass("d-none");
        }

        // Jika Valid, Pindahkan ke Hidden Input di Form Utama
        $("#h_current").val(current);
        $("#h_new").val(newVal);
        $("#h_confirm").val(confirm);

        // Tutup Modal & Beri Feedback
        $("#modalChangePassword").modal("hide");
        Swal.fire({
            icon: "success",
            title: "Password Siap Disimpan",
            text: 'Klik tombol "Simpan Perubahan" di bawah untuk memproses.',
            timer: 2000,
        });
    });

    if ($(".mask-nik").length) {
        $(".mask-nik").inputmask({
            mask: "99.99.99.999999.9999",
            placeholder: "", // Jangan tampilkan underscore
            removeMaskOnSubmit: false, // Kita handle manual di backend cleaningnya
            showMaskOnHover: false,
        });

        // Validasi Realtime Panjang NIK
        $(".mask-nik").on("keyup blur", function () {
            var val = $(this).inputmask("unmaskedvalue"); // Ambil nilai asli angka saja
            var isValid = val.length === 16;

            if (!isValid && val.length > 0) {
                $(this).addClass("is-invalid");
                // Cari atau buat pesan error
                var feedback = $(this).next(".invalid-feedback");
                if (feedback.length === 0) {
                    $(this).after(
                        '<div class="invalid-feedback">NIK harus terdiri dari 16 digit angka.</div>'
                    );
                } else {
                    feedback.text("NIK harus terdiri dari 16 digit angka.");
                }
            } else {
                $(this).removeClass("is-invalid");
            }
        });
    }

    if ($(".mask-met").length) {
        $(".mask-met").inputmask({
            mask: "000.999999.9999",
            placeholder: "000.xxxxxx.xxxx",
            showMaskOnHover: false,
        });
    }

    // ==========================================
    // 3. LOGIKA TANDA TANGAN (MODAL)
    // ==========================================
    //   var canvas = document.getElementById("signature-canvas");
    //   if (canvas) {
    //     var signaturePad = new SignaturePad(canvas, {
    //       backgroundColor: "rgba(255, 255, 255, 0)",
    //       penColor: "rgb(0, 0, 0)",
    //     });

    //     function resizeCanvas() {
    //       var ratio = Math.max(window.devicePixelRatio || 1, 1);
    //       var data = signaturePad.toData();
    //       canvas.width = canvas.offsetWidth * ratio;
    //       canvas.height = canvas.offsetHeight * ratio;
    //       canvas.getContext("2d").scale(ratio, ratio);
    //       signaturePad.clear();
    //       signaturePad.fromData(data);
    //     }

    //     $("#modalSignatureProfile").on("shown.bs.modal", function () {
    //       resizeCanvas();
    //       var existingSignature = $("#signatureInputProfile").val();
    //       if (existingSignature && existingSignature.trim() !== "") {
    //         signaturePad.fromDataURL(existingSignature, { ratio: 1.5 });
    //       }
    //     });

    //     $("#btnClear").on("click", function () {
    //       signaturePad.clear();
    //     });
    //     $("#btnUpload").on("click", function () {
    //       $("#uploadSigFile").click();
    //     });

    //     $("#uploadSigFile").on("change", function (e) {
    //       var file = e.target.files[0];
    //       if (!file) return;
    //       if (file.type !== "image/png") {
    //         Swal.fire({ icon: "error", title: "Hanya format PNG diperbolehkan." });
    //         this.value = "";
    //         return;
    //       }
    //       var reader = new FileReader();
    //       reader.onload = function (event) {
    //         signaturePad.fromDataURL(event.target.result);
    //       };
    //       reader.readAsDataURL(file);
    //     });

    //     $("#btnSaveSignatureProfile").on("click", function () {
    //       // JIKA KOSONG -> TETAP KOSONGKAN HIDDEN & SHOW ERROR ALERT
    //       if (signaturePad.isEmpty()) {
    //         $("#signatureInputProfile").val("");
    //         $("#signaturePreview").hide();

    //         // Reset tombol
    //         $("#btnTriggerSignatureProfile")
    //           .removeClass("btn-success btn-outline-success")
    //           .addClass("btn-outline-primary")
    //           .html('<i class="fas fa-pen-nib"></i> Masukkan Tanda Tangan');

    //         // ALERT SESUAI REQUEST (TIDAK TUTUP MODAL)
    //         Swal.fire({
    //           icon: "warning",
    //           title: "Isi Tanda Tangan Terlebih Dahulu",
    //           confirmButtonText: "Oke",
    //           confirmButtonColor: "#3085d6",
    //         });
    //         // Modal tetap terbuka, user harus isi
    //       } else {
    //         // JIKA TERISI -> SIMPAN & TUTUP MODAL
    //         var dataURL = signaturePad.toDataURL("image/png");
    //         $("#signatureInputProfile").val(dataURL);

    //         $("#btnTriggerSignatureProfile")
    //           .removeClass("btn-outline-primary btn-outline-danger")
    //           .addClass("btn-outline-success")
    //           .html('<i class="fas fa-eye"></i> Lihat Tanda Tangan');

    //         $("#imgPreview").attr("src", dataURL);
    //         $("#signaturePreview").show();

    //         // Hapus error validasi jika ada
    //         $("#signatureInputProfile").valid();
    //         // Tutup Modal
    //         $("#modalSignatureProfile")
    //           .find('[data-dismiss="modal"]')
    //           .first()
    //           .trigger("click");
    //       }
    //     });

    //     window.addEventListener("resize", resizeCanvas);
    //   }
    // var canvas = document.getElementById("signature-canvas");
    // var signaturePad = new SignaturePad(canvas, { backgroundColor: 'rgba(255, 255, 255, 0)', penColor: 'rgb(0, 0, 0)' });

    // function resizeCanvas() {
    //     var ratio = Math.max(window.devicePixelRatio || 1, 1);
    //     canvas.width = canvas.offsetWidth * ratio;
    //     canvas.height = canvas.offsetHeight * ratio;
    //     canvas.getContext("2d").scale(ratio, ratio);
    //     // Jangan clear jika ada data, reload data lama jika perlu
    // }

    // $("#modalSignature").on("shown.bs.modal", function () {
    //     resizeCanvas();
    //     // (Opsional) Load existing signature jika mau edit ulang
    // });

    // $("#btnClearSig").click(function(){ signaturePad.clear(); });

    // $("#btnSaveSig").click(function(){
    //     if (signaturePad.isEmpty()) {
    //         Swal.fire('Warning', 'Tanda tangan kosong!', 'warning');
    //     } else {
    //         var dataURL = signaturePad.toDataURL('image/png');
    //         // Masukkan ke Hidden Input Form Utama
    //         $("#signatureInput").val(dataURL);

    //         // Update Preview Kecil
    //         $("#smallSigPreview").attr("src", dataURL);
    //         $("#sigPreviewContainer").show();

    //         $("#modalSignature").modal("hide");
    //         Swal.fire({
    //             icon: 'success',
    //             title: 'Tanda Tangan Diperbarui',
    //             text: 'Klik tombol "Simpan Perubahan" untuk memproses.',
    //             timer: 1500
    //         });
    //     }
    // });

    // ==========================================
    // 4. VALIDASI FORM UTAMA (REQUIRED)
    // ==========================================
    $("#formProfile").validate({
        ignore: [], // Jangan ignore hidden field (agar signature/pass tervalidasi jika required)
        rules: {
            postalCode: {
                required: true,
                digits: true,
                minlength: 5,
                maxlength: 5,
            },

            fullName: { required: true },
            email: { required: true, email: true },
            noTelp: { required: true, number: true },
            // Tambahkan field lain sesuai kebutuhan
            // address: { required: true },
            // nik: { required: true, number: true, minlength: 16 }
        },
        messages: {
            postalCode: {
                required: "Kode pos wajib diisi",
                digits: "Hanya boleh angka",
                minlength: "Kode pos harus 5 digit",
                maxlength: "Kode pos harus 5 digit",
            },
            fullName: "Nama lengkap wajib diisi",
            email: "Format email tidak valid",
            noTelp: "Nomor telepon wajib diisi angka",
        },
        errorElement: "span",
        errorPlacement: function (error, element) {
            error.addClass("invalid-feedback");
            element.closest(".form-group").append(error);
        },
        highlight: function (element) {
            $(element).addClass("is-invalid");
        },
        unhighlight: function (element) {
            $(element).removeClass("is-invalid");
        },

        // ==========================================
        // 5. SUBMIT VIA AJAX
        // ==========================================
        submitHandler: function (form) {
            var formData = new FormData(form);

            Swal.fire({
                title: "Menyimpan Profil...",
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
                success: function (response) {
                    var res =
                        typeof response === "string" ? JSON.parse(response) : response;
                    Swal.fire({
                        icon: "success",
                        title: "Berhasil!",
                        text: res.message,
                    }).then(() => {
                        location.reload(); // Refresh halaman agar data/foto terupdate
                    });
                },
                error: function (xhr) {
                    var msg = "Terjadi kesalahan server";
                    try {
                        msg = JSON.parse(xhr.responseText).message;
                    } catch (e) { }
                    Swal.fire("Gagal!", msg, "error");
                },
            });
            return false;
        },
    });
});
