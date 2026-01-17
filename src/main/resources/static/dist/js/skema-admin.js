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
  $(".delete-button-skema").on("click", function (e) {
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
    // ========================================================
    // LOGIKA TAB 2: FORM DINAMIS (UNIT SKEMA)
    // ========================================================

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
    // LOGIKA TAB 3: ELEMEN (Sinkronisasi dengan Tab 2)
    // ========================================================

    // Fungsi untuk me-refresh opsi dropdown Unit di Tab 3
    function syncUnitDropdowns() {
      // 1. Ambil data dari Tab 2
      var units = [];
      $("#unit-skema-container .unit-skema-row").each(function () {
        var code = $(this).find('input[name="kodeUnit[]"]').val();
        var title = $(this).find('input[name="judulUnit[]"]').val();
        if (code && title) {
          units.push({ code: code, title: title });
          // units.push({ code: code, title: code + " - " + title });
        }
      });

      // 2. Loop semua dropdown di Tab 3 dan update opsinya
      $("#unit-elemen-container .select-unit-ref").each(function () {
        var currentVal = $(this).val(); // Simpan nilai yang sedang dipilih
        var $select = $(this);
        $select.empty(); // Kosongkan
        $select.append(
          '<option value="" disabled selected>-- Pilih Judul Unit --</option>'
        );

        units.forEach(function (u) {
          $select.append(`<option value="${u.code}">${u.title}</option>`);
        });

        // Restore nilai jika masih ada
        if (currentVal) {
          $select.val(currentVal);
        }
      });
    }

    // Event saat Tab 3 (Elemen) diklik/ditampilkan
    $('a[href="#unit-elemen"]').on("shown.bs.tab", function (e) {
      syncUnitDropdowns();
    });

    // Juga panggil saat tombol "Selanjutnya" yang mengarah ke Unit Elemen diklik
    $('.next-tab[data-target-tab="unit-elemen-tab"]').on("click", function () {
      // Timeout kecil untuk memastikan tab render dulu (opsional)
      setTimeout(syncUnitDropdowns, 100);
    });

    // Tambah Baris Elemen
    $(document).on("click", "#add-elemen-button", function () {
      // Clone baris pertama
      var template = $("#unit-elemen-container .unit-elemen-row:first");
      var newRow = template.clone();

      // Reset inputan
      newRow.find("input").val("");
      newRow.find("select").val("");

      // Pastikan tombol hapus terlihat
      newRow.find(".remove-elemen-button").show();

      // Append ke container
      $("#unit-elemen-container").append(newRow);

      // Sinkronkan dropdown di baris baru ini
      // (Sebenarnya sudah tercopy opsinya dari clone, tapi untuk memastikan data terbaru)
      syncUnitDropdowns();
    });

    // Hapus Baris Elemen
    $(document).on("click", ".remove-elemen-button", function () {
      if ($("#unit-elemen-container .unit-elemen-row").length > 1) {
        $(this).closest(".unit-elemen-row").remove();
      } else {
        Toast.fire({
          icon: "error",
          title: "Minimal harus ada satu elemen.",
        });
      }
    });

    // ========================================================
    // LOGIKA TAB 4: KUK (Sinkronisasi dengan Tab 3) - BARU!
    // ========================================================
    function syncElementDropdowns() {
      // 1. Ambil data dari Tab 3 (Elemen)
      var elements = [];
      $("#unit-elemen-container .unit-elemen-row").each(function () {
        var unitRef = $(this).find('select[name="elemenKodeUnitRef[]"]').val();
        var no = $(this).find('input[name="noElemen[]"]').val();
        var nama = $(this).find('input[name="namaElemen[]"]').val();

        // Kita butuh Key Unik untuk menghubungkan KUK ke Elemen
        // Key: KodeUnit + "||" + NoElemen
        if (unitRef && no && nama) {
          var uniqueKey = unitRef + "||" + no;
          var label = `[Unit ${unitRef}] Elemen ${no}: ${nama}`;
          elements.push({ value: uniqueKey, label: label });
        }
      });

      // 2. Isi Dropdown di Tab 4
      // Perhatikan selector: hanya cari .select-unit-ref yg ada di dalam #kuk-container
      $("#kuk-container .select-kuk-ref").each(function () {
        var currentVal = $(this).val();
        var $select = $(this);
        $select
          .empty()
          .append(
            '<option value="" disabled selected>-- Pilih Nama Elemen --</option>'
          );
        elements.forEach(function (el) {
          $select.append(`<option value="${el.value}">${el.label}</option>`);
        });
        if (currentVal) $select.val(currentVal);
      });
    }

    // Trigger Sync saat Tab 4 dibuka
    $('a[href="#kuk"]').on("shown.bs.tab", syncElementDropdowns);
    $('.next-tab[data-target-tab="kuk-tab"]').on("click", function () {
      setTimeout(syncElementDropdowns, 100);
    });

    // Tambah Row KUK
    $(document).on("click", "#add-kuk-button", function () {
      // Pastikan ID tombol di HTML adalah add-kuk-button
      var template = $("#kuk-container .kuk-row:first");
      var newRow = template.clone();
      newRow.find("textarea").val("");
      newRow.find("select").val("");
      newRow.find(".remove-kuk-button").show(); // Pastikan class tombol hapus di HTML adalah remove-kuk-button
      $("#kuk-container").append(newRow);
      syncElementDropdowns();
    });

    // Hapus Row KUK
    $(document).on("click", ".remove-kuk-button", function () {
      if ($("#kuk-container .kuk-row").length > 1) {
        $(this).closest(".kuk-row").remove();
      } else {
        Toast.fire({ icon: "error", title: "Minimal satu KUK." });
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
    if ($formAdd.length) {
      function saveFormDataToLocalStorage() {
        const formData = {};

        // --- TAB 1: DATA DASAR ---
        formData.namaSkema = $("#namaSkema").val();
        formData.kodeSkema = $("#kodeSkema").val();
        formData.noSkkni = $("#noSkkni").val();
        formData.level = $("#levelSkema").val();
        formData.tahun = $("#tahunSkkni").val();
        formData.jenisSkema = $("#jenisSkema").val();
        formData.modeSkema = $("#modeSkema").val();
        formData.tanggal_penetapan = $("#tanggal_penetapan").val();

        // --- TAB 2: UNIT SKEMA ---
        formData.unitSkema = [];
        $("#unit-skema-container .unit-skema-row").each(function () {
          const unit = {
            kodeUnit: $(this).find('input[name="kodeUnit[]"]').val(),
            judulUnit: $(this).find('input[name="judulUnit[]"]').val(),
          };
          formData.unitSkema.push(unit);
        });

        // --- TAB 3: UNIT ELEMEN (BARU) ---
        formData.unitElemen = [];
        $("#unit-elemen-container .unit-elemen-row").each(function () {
          const elemen = {
            unitRef: $(this).find('select[name="elemenKodeUnitRef[]"]').val(), // Ambil value select
            no: $(this).find('input[name="noElemen[]"]').val(),
            nama: $(this).find('input[name="namaElemen[]"]').val(),
          };
          formData.unitElemen.push(elemen);
        });

        // --- TAB 4 (KUK) ---
        formData.kukData = [];
        $("#kuk-container .kuk-row").each(function () {
          formData.kukData.push({
            elementRef: $(this).find('select[name="elemenKukRef[]"]').val(), // Value "Unit||No"
            kuk: $(this).find('textarea[name="kuk"]').val(),
          });
        });

        // Tab 5
        formData.persyaratan = [];
        $("#persyaratan-container .persyaratan-row").each(function () {
          formData.persyaratan.push(
            $(this).find(".summernote-persyaratan").summernote("code")
          );
        });

        // Simpan Tab Aktif
        formData.activeTab = $(".nav-tabs .nav-link.active").attr("id");

        localStorage.setItem(formKey, JSON.stringify(formData));
      }

      function loadFormDataFromLocalStorage() {
        const savedData = localStorage.getItem(formKey);
        if (!savedData) return;

        const formData = JSON.parse(savedData);

        // 1. Restore Tab 1
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

        // Reset Label File
        $('.custom-file-label[for="fileSkema"]')
          .html("Pilih file")
          .removeClass("selected");

        // 2. Restore Tab 2 (Unit)
        if (formData.unitSkema && formData.unitSkema.length > 0) {
          const unitContainer = $("#unit-skema-container");
          unitContainer.find(".unit-skema-row:not(:first)").remove(); // Reset

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

        // --- PENTING: SINKRONISASI DROPDOWN UNIT ---
        // Karena Tab 3 butuh data Tab 2, kita wajib panggil syncUnitDropdowns()
        // setelah Tab 2 direstore, agar dropdown di Tab 3 terisi opsinya.
        syncUnitDropdowns();

        // 3. Restore Tab 3 (Elemen) - (BARU)
        if (formData.unitElemen && formData.unitElemen.length > 0) {
          const elemenContainer = $("#unit-elemen-container");
          elemenContainer.find(".unit-elemen-row:not(:first)").remove(); // Reset

          formData.unitElemen.forEach(function (el, index) {
            var row;
            if (index === 0)
              row = elemenContainer.find(".unit-elemen-row:first");
            else {
              row = elemenContainer.find(".unit-elemen-row:first").clone();
              // Saat clone, opsi dropdown ikut ter-copy, jadi aman
              elemenContainer.append(row);
            }

            // Set Nilai Input
            row.find('input[name="noElemen[]"]').val(el.no);
            row.find('input[name="namaElemen[]"]').val(el.nama);

            // Set Nilai Select (Unit Reference)
            if (el.unitRef) {
              row.find('select[name="elemenKodeUnitRef[]"]').val(el.unitRef);
            }
          });
        }

        syncElementDropdowns(); // Sync untuk Tab 4

        // --- RESTORE TAB 4 (KUK) ---
        if (formData.kukData && formData.kukData.length > 0) {
          const c = $("#kuk-container");
          c.find(".kuk-row:not(:first)").remove();
          formData.kukData.forEach((k, i) => {
            var row =
              i === 0
                ? c.find(".kuk-row:first")
                : c.find(".kuk-row:first").clone().appendTo(c);
            row.find('textarea[name="kuk"]').val(k.kuk);
            if (k.elementRef)
              row.find('select[name="elemenKukRef[]"]').val(k.elementRef);
          });
        }

        // 4. Restore Tab 4 (Persyaratan)
        if (formData.persyaratan && formData.persyaratan.length > 0) {
          const reqContainer = $("#persyaratan-container");
          reqContainer.empty();

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

        // Restore Tab Aktif
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

      // Listener tombol tambah/hapus (Update untuk Elemen juga)
      $("#form-tambah-skema").on(
        "input change",
        "input, select, textarea",
        debouncedSave
      );
      $(document).on(
        "click",
        "#add-unit-button, .remove-unit-button, #add-elemen-button, .remove-elemen-button, #add-kuk-button, .remove-kuk-button, #add-persyaratan-button, .remove-persyaratan-button",
        function () {
          setTimeout(saveFormDataToLocalStorage, 200);
        }
      );
      $(".next-tab, .prev-tab").on("click", saveFormDataToLocalStorage);
    }

    // --- E. FUNGSI DIRTY CHECK (UNTUK TOMBOL BATAL) ---
    function isFormDirty() {
      let isDirty = false;

      // 1. Cek Tab 1
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
      if ($("#fileSkema").val()) return true;

      // 2. Cek Jumlah Baris (Unit, Elemen, Persyaratan)
      if (
        $("#unit-skema-container .unit-skema-row").length > 1 ||
        $("#unit-elemen-container .unit-elemen-row").length > 1 || // Cek Elemen
        $("#kuk-container .kuk-row").length > 1 || // Cek Elemen
        $("#persyaratan-container .persyaratan-row").length > 1
      )
        return true;

      // 3. Cek Isi Baris Pertama Unit
      $("#unit-skema-container .unit-skema-row:first")
        .find("input")
        .each(function () {
          if ($(this).val().trim() !== "") {
            isDirty = true;
            return false;
          }
        });
      if (isDirty) return true;

      // 4. Cek Isi Baris Pertama Elemen (BARU)
      const firstElemenRow = $("#unit-elemen-container .unit-elemen-row:first");
      if (
        firstElemenRow.find('input[name="noElemen[]"]').val().trim() !== "" ||
        firstElemenRow.find('input[name="namaElemen[]"]').val().trim() !== "" ||
        firstElemenRow.find('select[name="elemenKodeUnitRef[]"]').val() !== null
      ) {
        return true;
      }

      // 4. Cek Isi Baris Pertama Elemen (BARU)
      const firstKuk = $("#kuk-container .kuk-row:first");
      if (
        firstKuk.find('textarea[name="kuk"]').val().trim() !== "" ||
        firstKuk.find('select[name="elemenKukRef[]"]').val() !== null
      )
        return true;

      // 5. Cek Isi Summernote
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

      // A. Ambil Data Unit (Tab 2)
      var unitsArray = [];
      $("#unit-skema-container .unit-skema-row").each(function () {
        var kode = $(this).find('input[name="kodeUnit[]"]').val();
        var judul = $(this).find('input[name="judulUnit[]"]').val();
        if (kode && judul) {
          unitsArray.push({ kode: kode, judul: judul });
        }
      });

      // B. Ambil Data Elemen (Tab 3)
      var elementsArray = [];
      $("#unit-elemen-container .unit-elemen-row").each(function () {
        var unitRef = $(this).find('select[name="elemenKodeUnitRef[]"]').val();
        var no = $(this).find('input[name="noElemen[]"]').val();
        var nama = $(this).find('input[name="namaElemen[]"]').val();
        if (unitRef && no && nama) {
          elementsArray.push({ unitRef: unitRef, no: no, nama: nama });
        }
      });

      var kuksArray = [];
      $("#kuk-container .kuk-row").each(function () {
        var elRef = $(this).find('select[name="elemenKukRef[]"]').val(); // Value "UnitCode||NoElemen"
        var kuk = $(this).find('textarea[name="kuk"]').val();
        if (elRef && kuk) kuksArray.push({ elementRef: elRef, kuk: kuk });
      });

      // C. Ambil Data Persyaratan (Tab 4)
      var reqArray = [];
      $("#persyaratan-container .summernote-persyaratan").each(function () {
        var content = $(this).summernote("code");
        if (content && !$(this).summernote("isEmpty")) {
          reqArray.push(content);
        }
      });

      // 3. Masukkan JSON String ke FormData
      formData.append("unitsJson", JSON.stringify(unitsArray));
      formData.append("elementsJson", JSON.stringify(elementsArray));
      formData.append("kuksJson", JSON.stringify(kuksArray)); // Append KUK
      formData.append("requirementsJson", JSON.stringify(reqArray));

      // 4. Hapus Input Array Asli dari FormData (Agar tidak membebani limit server)
      // Ini mencegah error "FileCountLimitExceeded" / "TooManyParameters"
      formData.delete("kodeUnit[]");
      formData.delete("judulUnit[]");
      formData.delete("elemenKodeUnitRef[]");
      formData.delete("noElemen[]");
      formData.delete("namaElemen[]");
      formData.delete("elemenKukRef[]");
      formData.delete("kuk"); // Hapus input text area KUK
      formData.delete("persyaratan[]");

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
