$(document).ready(function () {
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
