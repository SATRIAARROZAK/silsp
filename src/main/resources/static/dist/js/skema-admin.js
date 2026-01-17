
// ========================================================
// JAVASCRIPT UTAMA SKEMA ADMIN (LIST, ADD, EDIT, VIEW)
// ========================================================

$(document).ready(function () {
  // --- KONFIGURASI UMUM ---
  var activeSummernoteInstance = null;
  const formKey = "skemaFormData";

  // ==========================================
  // HANDLE UPLOAD FILE: LABEL & PREVIEW
  // ==========================================
  $(document).on("change", ".custom-file-input", function (event) {
    var input = $(this);
    var fileName = input.val().split("\\").pop(); // Ambil nama file
    var label = input.next(".custom-file-label"); // Label Bootstrap
    var file = event.target.files[0]; // File object asli

    // 1. Update Label dengan Nama File
    if (fileName) {
      label.addClass("selected").html(fileName);
    } else {
      label.html("Pilih file");
    }

    // 2. Buat Preview Link (Blob URL)
    // Hapus tombol preview lama jika user mengganti file lagi
    input.closest(".form-group").find(".preview-temp-link").remove();

    if (file) {
      // Pastikan file yang dipilih adalah PDF
      if (file.type === "application/pdf") {
        // Membuat URL sementara dari file lokal
        var fileUrl = URL.createObjectURL(file);

        // Membuat elemen tombol preview
        var previewHtml = `
            <div class="mt-2 preview-temp-link animate__animated animate__fadeIn">
                <a href="${fileUrl}" target="_blank" class="btn btn-sm btn-outline-info">
                    <i class="fas fa-file mr-1"></i> Lihat Dokument
                </a>
                <small class="d-block text-muted mt-1">*Klik tombol di atas untuk melihat preview dokumen sebelum disimpan.</small>
            </div>
        `;

        // Menyelipkan tombol di bawah elemen input-group
        input.closest(".input-group").after(previewHtml);
      }
    }
  });

  // Inisialisasi Toast SweetAlert
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

  // ========================================================
  // 1. LOGIKA LIST PAGE (skema-list.html)
  // ========================================================

  // Konfirmasi Delete
  $(".delete-button-skema").on("click",  function (e) {
    e.preventDefault();
    var link = $(this).attr("href");

    Swal.fire({
      title: "Yakin Hapus Skema?",
      text: "Data unit dan persyaratan didalamnya juga akan terhapus permanen!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Ya, Hapus!",
      cancelButtonText: "Batal",
    }).then((result) => {
      if (result.isConfirmed) {
        window.location.href = link;
      }
    });
  });

  // Logika Icon Collapse
  $(document).on("show.bs.collapse", ".collapse", function () {
    var id = $(this).attr("id");
    var btn = $('button[data-target="#' + id + '"]');
    btn.find("i").removeClass("fa-folder-plus").addClass("fa-folder-minus");
    btn.removeClass("btn-primary").addClass("btn-info");
  });

  $(document).on("hide.bs.collapse", ".collapse", function () {
    var id = $(this).attr("id");
    var btn = $('button[data-target="#' + id + '"]');
    btn.find("i").removeClass("fa-folder-minus").addClass("fa-folder-plus");
    btn.removeClass("btn-info").addClass("btn-primary");
  });

  // ========================================================
  // 2. LOGIKA ADD & EDIT PAGE (FORM DINAMIS)
  // ========================================================

  var $formAdd = $("#form-tambah-skema");
  var $formEdit = $("#form-edit-skema");

  if ($formAdd.length || $formEdit.length) {
    // A. FORM DINAMIS (UNIT SKEMA)
    // ----------------------------
    $(document).on("click", "#add-unit-button", function () {
      var template = $("#unit-skema-container .unit-skema-row:first");
      var newUnitRow = template.clone();
      newUnitRow.find("input").val("");
      newUnitRow.find(".remove-unit-button").show();
      $("#unit-skema-container").append(newUnitRow);
    });

    $(document).on("click", ".remove-unit-button", function () {
      if ($("#unit-skema-container .unit-skema-row").length > 1) {
        $(this).closest(".unit-skema-row").remove();
      } else {
        Toast.fire({
          icon: "error",
          title: "Minimal harus ada satu unit skema.",
        });
      }
    });

    // ========================================================
    // B. SUMMERNOTE PERSYARATAN (SHARED TOOLBAR & HIGHLIGHT)
    // ========================================================

    // --- FUNGSI UPDATE STATUS TOMBOL (BARU) ---
    // Fungsi ini mengecek style di posisi kursor dan mewarnai tombol
    function updateToolbarState() {
      const commands = {
        bold: '[data-command="bold"]',
        italic: '[data-command="italic"]',
        underline: '[data-command="underline"]',
        insertUnorderedList: '[data-command="insertUnorderedList"]',
        insertOrderedList: '[data-command="insertOrderedList"]',
      };

      for (const [cmd, selector] of Object.entries(commands)) {
        const btn = $(selector);
        // document.queryCommandState adalah cara native browser cek format teks
        if (document.queryCommandState(cmd)) {
          // Jika aktif: Ganti jadi abu-abu gelap (active)
          btn.removeClass("btn-default").addClass("btn-secondary active");
        } else {
          // Jika tidak: Kembali ke default (putih/terang)
          btn.removeClass("btn-secondary active").addClass("btn-default");
        }
      }
    }

    function initializeSummernote(element) {
      element.summernote({
        height: 100,
        toolbar: [], // Toolbar default dimatikan
        callbacks: {
          // Saat masuk editor
          onFocus: function () {
            activeSummernoteInstance = $(this);
            $(".note-editor").removeClass("border-primary");
            $(this).next(".note-editor").addClass("border-primary");

            // Cek status tombol saat pertama kali klik masuk
            updateToolbarState();
          },
          // Saat mengetik (Keyup) -> Cek status bold/italic dsb
          onKeyup: function (e) {
            updateToolbarState();
          },
          // Saat klik mouse (pindah kursor) -> Cek status
          onMouseup: function (e) {
            updateToolbarState();
          },
          // Saat paste -> Cek status
          onPaste: function (e) {
            setTimeout(updateToolbarState, 100);
          },
        },
      });
    }

    // Event handler Toolbar Header
    $("#custom-summernote-toolbar").on("click", "button", function (e) {
      e.preventDefault();
      var command = $(this).data("command");

      if (activeSummernoteInstance && command) {
        // Jalankan perintah
        activeSummernoteInstance.summernote(command);

        // Langsung update visual tombol agar terlihat "tertekan"
        updateToolbarState();

        // Kembalikan fokus ke editor agar user bisa langsung ketik
        // activeSummernoteInstance.summernote('focus');
      } else {
        Toast.fire({
          icon: "error",
          title:
            "Silakan klik di dalam kotak teks persyaratan terlebih dahulu.",
        });
      }
    });

    // Init Awal
    if ($(".summernote-persyaratan").length > 0) {
      $(".summernote-persyaratan").each(function () {
        initializeSummernote($(this));
      });
    }

    // Tambah Baris Persyaratan
    $(document).on("click", "#add-persyaratan-button", function () {
      var newRowHTML = `
                <div class="persyaratan-row row align-items-center mb-3">
                    <div class="col-11">
                        <textarea class="form-control summernote-persyaratan" name="persyaratan[]"></textarea>
                    </div>
                    <div class="col-1">
                        <button type="button" class="btn btn-outline-danger remove-persyaratan-button">
                            <i class="fas fa-trash"></i>
                        </button>
                    </div>
                </div>`;
      var newRow = $(newRowHTML);
      $("#persyaratan-container").append(newRow);
      initializeSummernote(newRow.find(".summernote-persyaratan"));
    });

    // Hapus Baris Persyaratan
    $(document).on("click", ".remove-persyaratan-button", function () {
      if ($("#persyaratan-container .persyaratan-row").length > 1) {
        var row = $(this).closest(".persyaratan-row");
        row.find(".summernote-persyaratan").summernote("destroy");
        row.remove();
      } else {
        Toast.fire({
          icon: "error",
          title: "Minimal harus ada satu persyaratan.",
        });
      }
    });

    // --- D. LOCAL STORAGE (AUTO SAVE) - KHUSUS HALAMAN ADD ---
    // Kita hanya aktifkan Auto Save di Form Tambah, jangan di Edit (agar data DB tidak tertimpa draft)
    if ($formAdd.length) {
      function saveFormDataToLocalStorage() {
        const formData = {};
        // TAB 1
        formData.namaSkema = $("#namaSkema").val();
        formData.kodeSkema = $("#kodeSkema").val();
        formData.noSkkni = $("#noSkkni").val();
        formData.level = $("#levelSkema").val();
        formData.tahun = $("#tahunSkkni").val();
        formData.jenisSkema = $("#jenisSkema").val();
        formData.modeSkema = $("#modeSkema").val();
        formData.tanggal_penetapan = $("#tanggal_penetapan").val();
        // // File nama saja (tidak bisa simpan file binary di localStorage)
        // if ($("#fileSkema").val())
        //   formData.fileSkemaName = $("#fileSkema").val().split("\\").pop();

        // TAB 2 (Unit)
        formData.unitSkema = [];
        $("#unit-skema-container .unit-skema-row").each(function () {
          const unit = {
            kodeUnit: $(this).find('input[name="kodeUnit[]"]').val(),
            judulUnit: $(this).find('input[name="judulUnit[]"]').val(),
          };
          formData.unitSkema.push(unit);
        });

        // TAB 3 (Persyaratan)
        formData.persyaratan = [];
        $("#persyaratan-container .persyaratan-row").each(function () {
          formData.persyaratan.push(
            $(this).find(".summernote-persyaratan").summernote("code")
          );
        });

        formData.activeTab = $(".nav-tabs .nav-link.active").attr("id");
        localStorage.setItem(formKey, JSON.stringify(formData));
      }

      function loadFormDataFromLocalStorage() {
        const savedData = localStorage.getItem(formKey);
        if (!savedData) return;

        const formData = JSON.parse(savedData);

        // Restore Tab 1
        if (formData.namaSkema) $("#namaSkema").val(formData.namaSkema);
        if (formData.kodeSkema) $("#kodeSkema").val(formData.kodeSkema);
        if (formData.noSkkni) $("#noSkkni").val(formData.noSkkni);
        if (formData.level) $("#levelSkema").val(formData.level);
        if (formData.tahun) $("#tahunSkkni").val(formData.tahun);
        if (formData.tanggal_penetapan)
          $("#tanggal_penetapan").val(formData.tanggal_penetapan);
        if (formData.jenisSkema)
          $("#jenisSkema").val(formData.jenisSkema).trigger("change");
        if (formData.modeSkema)
          $("#modeSkema").val(formData.modeSkema).trigger("change");
        // if (formData.fileSkemaName)
        //   $('.custom-file-label[for="fileSkema"]').text(formData.fileSkemaName);
        // PERBAIKAN: RESET LABEL FILE KE DEFAULT
        // Pastikan label selalu "Pilih file" saat refresh, karena input file pasti kosong
        $('.custom-file-label[for="fileSkema"]')
          .html("Pilih file")
          .removeClass("selected");

        // Restore Tab 2 (Unit)
        if (formData.unitSkema && formData.unitSkema.length > 0) {
          const unitContainer = $("#unit-skema-container");
          // Sisakan 1, hapus sisanya (reset)
          unitContainer.find(".unit-skema-row:not(:first)").remove();

          formData.unitSkema.forEach(function (unit, index) {
            var row;
            if (index === 0) row = unitContainer.find(".unit-skema-row:first");
            else {
              row = unitContainer.find(".unit-skema-row:first").clone();
              unitContainer.append(row);
            }
            row.find('input[name="kodeUnit[]"]').val(unit.kodeUnit);
            row.find('input[name="judulUnit[]"]').val(unit.judulUnit);
          });
        }

        // Restore Tab 3 (Persyaratan)
        if (formData.persyaratan && formData.persyaratan.length > 0) {
          const reqContainer = $("#persyaratan-container");
          reqContainer.empty(); // Kosongkan dulu

          formData.persyaratan.forEach(function (reqContent) {
            var newRowHTML = `
                        <div class="persyaratan-row row align-items-center mb-3">
                            <div class="col-11"><textarea class="form-control summernote-persyaratan" name="persyaratan[]"></textarea></div>
                            <div class="col-1"><button type="button" class="btn btn-outline-danger remove-persyaratan-button"><i class="fas fa-trash"></i></button></div>
                        </div>`;
            var newReqRow = $(newRowHTML);
            reqContainer.append(newReqRow);
            initializeSummernote(newReqRow.find(".summernote-persyaratan"));
            newReqRow
              .find(".summernote-persyaratan")
              .summernote("code", reqContent);
          });
        }

        if (formData.activeTab) $("#" + formData.activeTab).tab("show");
      }

      // Debounce function
      function debounce(func, delay) {
        let timeout;
        return function (...args) {
          const context = this;
          clearTimeout(timeout);
          timeout = setTimeout(() => func.apply(context, args), delay);
        };
      }
      const debouncedSave = debounce(saveFormDataToLocalStorage, 500);

      // Jalankan Load saat awal
      loadFormDataFromLocalStorage();

      // Listeners untuk Auto Save
      $("#form-tambah-skema").on(
        "input change",
        "input, select, textarea",
        debouncedSave
      );
      $(document).on(
        "click",
        "#add-unit-button, .remove-unit-button, #add-persyaratan-button, .remove-persyaratan-button",
        function () {
          setTimeout(saveFormDataToLocalStorage, 200);
        }
      );
      $(".next-tab, .prev-tab").on("click", saveFormDataToLocalStorage);
    } // End if form Add

    // --- E. FUNGSI DIRTY CHECK (UNTUK TOMBOL BATAL) ---
    function isFormDirty() {
      let isDirty = false;
      // Cek Input Teks di Tab 1
      $("#skema-sertifikasi")
        .find(
          'input[type="text"], input[type="number"], input[type="date"], select'
        )
        .each(function () {
          if ($(this).val() && $(this).val().trim() !== "") {
            isDirty = true;
            return false;
          }
        });
      if (isDirty) return true;

      // Cek File
      if ($("#fileSkema").val()) return true;

      // Cek Jumlah Baris Tab 2 & 3
      if (
        $("#unit-skema-container .unit-skema-row").length > 1 ||
        $("#persyaratan-container .persyaratan-row").length > 1
      )
        return true;

      // Cek Isi Baris Pertama Tab 2
      $("#unit-skema-container .unit-skema-row:first")
        .find("input")
        .each(function () {
          if ($(this).val().trim() !== "") {
            isDirty = true;
            return false;
          }
        });
      if (isDirty) return true;

      // Cek Isi Summernote Pertama
      const firstNote = $(
        "#persyaratan-container .summernote-persyaratan"
      ).first();
      if (firstNote.length > 0 && !firstNote.summernote("isEmpty")) return true;

      return false;
    }

    // Event Listener Tombol Batal
    $(document).on("click", "#cancel-button", function (e) {
      e.preventDefault();
      const targetUrl = $(this).attr("href");

      // Jika form terisi (kotor), tanya user dulu
      if (isFormDirty()) {
        Swal.fire({
          title: "Apakah Anda yakin?",
          text: "Semua data yang belum disimpan akan dihapus.",
          icon: "warning",
          showCancelButton: true,
          confirmButtonColor: "#3085d6",
          cancelButtonColor: "#d33",
          confirmButtonText: "Ya, batalkan!",
          cancelButtonText: "Tidak",
        }).then((result) => {
          if (result.isConfirmed) {
            localStorage.removeItem(formKey);
            Toast.fire({ icon: "success", text: "Input telah dibersihkan." });
            setTimeout(function () {
              window.location.href = targetUrl;
            }, 1000);
          }
        });
      } else {
        // Jika kosong, langsung pindah
        localStorage.removeItem(formKey);
        window.location.href = targetUrl;
      }
    });

    // C. VALIDASI TAB
    // ---------------
    function validateTab(tabElement) {
      let isValid = true;
      let firstInvalidElement = null;

      tabElement.find(".is-invalid").removeClass("is-invalid");
      tabElement.find(".note-editor").removeClass("border border-danger");

      // Validasi Input
      tabElement.find("input[required], select[required]").each(function () {
        if (!$(this).val() || $(this).val().trim() === "") {
          isValid = false;
          $(this).addClass("is-invalid");
          if (!firstInvalidElement) firstInvalidElement = $(this);
        }
      });

      // Validasi Summernote
      tabElement.find(".summernote-persyaratan").each(function () {
        if ($(this).summernote("isEmpty")) {
          // Cek jika element aslinya required (meskipun hidden)
          // Di HTML Anda sudah set 'required', tapi summernote hide element asli.
          // Kita anggap semua persyaratan wajib diisi jika listnya ada.
          isValid = false;
          $(this).next(".note-editor").addClass("border border-danger");
          if (!firstInvalidElement) firstInvalidElement = $(this);
        }
      });

      return { isValid, firstInvalidElement };
    }

    $("form").on("input change", ".is-invalid", function () {
      $(this).removeClass("is-invalid");
    });

    // --- TOMBOL NAVIGASI TAB ---
    $(".next-tab").on("click", function () {
      const currentTab = $(this).closest(".tab-pane");
      const validationResult = validateTab(currentTab);

      if (validationResult.isValid) {
        const targetTabId = $(this).data("target-tab");
        $("#" + targetTabId).tab("show");
      } else {
        // Validasi gagal: Tampilkan Toast dan fokus
        Toast.fire({
          icon: "error",
          title: "Harap isi semua kolom yang wajib diisi.",
        });
        if (validationResult.firstInvalidElement) {
          validationResult.firstInvalidElement.focus();
          // Jika itu summernote, fokus secara spesifik
          if (
            validationResult.firstInvalidElement.hasClass(
              "summernote-persyaratan"
            )
          ) {
            validationResult.firstInvalidElement.summernote("focus");
          }
        }
      }
    });

    $(".prev-tab").on("click", function () {
      const targetTabId = $(this).data("target-tab");
      $("#" + targetTabId).tab("show");
    });

    $(".card-tabs .nav-tabs .nav-link").on("click", function (e) {
      e.preventDefault();
      Toast.fire({
        icon: "info",
        title: 'Gunakan tombol "Selanjutnya" atau "Sebelumnya".',
      });
      return false;
    });

    // D. SUBMIT FORM
    // --------------
    $("#form-tambah-skema, #form-edit-skema").on("submit", function (e) {
      e.preventDefault();
      var isEdit = $(this).attr("id") === "form-edit-skema";

      let allValid = true;
      $(".tab-pane").each(function () {
        if (!validateTab($(this)).isValid) allValid = false;
      });

      if (!allValid) {
        Toast.fire({
          icon: "error",
          title: "Masih ada data yang kosong. Cek semua tab!",
        });
        return;
      }

      var formData = new FormData(this);

      Swal.fire({
        title: isEdit ? "Memperbarui Data..." : "Menyimpan Data...",
        didOpen: () => Swal.showLoading(),
      });

      $.ajax({
        url: $(this).attr("action"),
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (res) {
          var response = typeof res === "string" ? JSON.parse(res) : res;
          if (!isEdit) localStorage.removeItem("skemaFormData");

          Swal.fire({
            title: "Berhasil!",
            text: response.message,
            icon: "success",
          }).then(() => {
            window.location.href = "/admin/skema";
          });
        },
        error: function (xhr) {
          var msg = "Terjadi kesalahan server";
          try {
            msg = JSON.parse(xhr.responseText).message;
          } catch (e) {}
          Swal.fire("Gagal", msg, "error");
        },
      });
    });
  } // End if form exists
});
