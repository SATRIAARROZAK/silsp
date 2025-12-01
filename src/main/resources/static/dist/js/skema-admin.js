// ========================================================
// JAVASCRIPT UNTUK SKEMA ADMIN
// ========================================================

$(document).ready(function () {
  var activeSummernoteInstance = null;
  const formKey = "skemaFormData";

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

  //  =======================================================
  // PDF PREVIEW MODAL
  //  =======================================================
  // PDF Preview in Modal
  $(document).ready(function () {
    // Event ini akan dijalankan SETIAP KALI modal #previewModal akan ditampilkan
    $("#previewModal").on("show.bs.modal", function (event) {
      // Dapatkan tombol yang memicu modal
      var button = $(event.relatedTarget);

      // Ekstrak path file dari atribut data-filepath
      var filePath = button.data("filepath");

      // Dapatkan elemen modal itu sendiri
      var modal = $(this);

      // Cari elemen iframe di dalam modal dan atur atribut 'src'-nya
      modal.find("#pdf-viewer").attr("src", filePath);
    });

    // (Opsional) Kosongkan src iframe saat modal ditutup agar tidak membebani browser
    $("#previewModal").on("hidden.bs.modal", function () {
      $(this).find("#pdf-viewer").attr("src", "");
    });
  });

  // =======================================================
  // --- FUNGSI UNTUK LOCALSTORAGE ---
  // =======================================================

  function saveFormDataToLocalStorage() {
    const formData = {};

    // TAB 1: Simpan semua input dari Tab 1
    formData.namaSkema = $("#namaSkema").val();
    formData.kodeSkema = $("#kodeSkema").val();
    formData.noSkkni = $("#noSkkni").val();
    formData.level = $("#levelSkema").val();
    formData.tahun = $("#tahunSkkni").val();
    formData.jenisSkema = $("#jenisSkema").val();
    formData.modeSkema = $("#modeSkema").val();
    formData.tanggal_penetapan = $("#tanggal_penetapan").val();
    formData.fileSkemaName = $("#fileSkema").val().split("\\").pop();

    // TAB 2: Simpan data dinamis dari Unit Skema
    formData.unitSkema = [];
    $("#unit-skema-container .unit-skema-row").each(function () {
      const unit = {
        kodeUnit: $(this).find('input[name="kodeUnit[]"]').val(),
        judulUnit: $(this).find('input[name="judulUnit[]"]').val(),
        standarKompetensi: $(this)
          .find('select[name="standarKompetensi[]"]')
          .val(),
      };
      formData.unitSkema.push(unit);
    });

    // TAB 3: Simpan data dinamis dari Persyaratan
    formData.persyaratan = [];
    $("#persyaratan-container .persyaratan-row").each(function () {
      formData.persyaratan.push(
        $(this).find(".summernote-persyaratan").summernote("code")
      );
    });

    // Simpan ID tab yang sedang aktif
    formData.activeTab = $(".nav-tabs .nav-link.active").attr("id");

    // Simpan semua data ke localStorage
    localStorage.setItem("skemaFormData", JSON.stringify(formData));
    console.log("Form data saved!"); // Untuk debugging
  }

  function loadFormDataFromLocalStorage() {
    const savedData = localStorage.getItem("skemaFormData");
    if (!savedData) return;

    const formData = JSON.parse(savedData);
    console.log("Loading form data:", formData); // Untuk debugging

    // Muat data untuk Tab 1
    if (formData.namaSkema) $("#namaSkema").val(formData.namaSkema);
    if (formData.kodeSkema) $("#kodeSkema").val(formData.kodeSkema);
    if (formData.noSkkni) $("#noSkkni").val(formData.noSkkni);
    if (formData.level) $("#levelSkema").val(formData.level);
    if (formData.tahun) $("#tahunSkkni").val(formData.tahun);
    if (formData.tanggal_penetapan)
      $("#tanggal_penetapan").val(formData.tanggal_penetapan);

    if (formData.jenisSkema) {
      $("#jenisSkema").val(formData.jenisSkema).trigger("change");
    }
    if (formData.modeSkema) {
      $("#modeSkema").val(formData.modeSkema).trigger("change");
    }
    if (formData.fileSkemaName) {
      $('.custom-file-label[for="fileSkema"]').text(formData.fileSkemaName);
    }

    // Muat data untuk Unit Skema (Tab 2)
    if (formData.unitSkema && formData.unitSkema.length > 0) {
      const unitContainer = $("#unit-skema-container");
      const unitTemplate = unitContainer.find(".unit-skema-row:first").clone();
      unitContainer.empty();

      formData.unitSkema.forEach(function (unit) {
        const newUnitRow = unitTemplate.clone();
        newUnitRow.find('input[name="kodeUnit[]"]').val(unit.kodeUnit);
        newUnitRow.find('input[name="judulUnit[]"]').val(unit.judulUnit);
        newUnitRow
          .find('select[name="standarKompetensi[]"]')
          .val(unit.standarKompetensi);
        unitContainer.append(newUnitRow);
      });
    }

    // Muat data untuk Persyaratan (Tab 3)
    if (formData.persyaratan && formData.persyaratan.length > 0) {
      const reqContainer = $("#persyaratan-container");
      reqContainer.empty();

      formData.persyaratan.forEach(function (reqContent) {
        const newRowHTML = `
                        <div class="persyaratan-row row align-items-center mb-3">
                            <div class="col-11">
                                <textarea class="form-control summernote-persyaratan" name="persyaratan[]"></textarea>
                            </div>
                            <div class="col-1">
                                <button type="button" class="btn btn-outline-danger remove-persyaratan-button"><i class="fas fa-trash"></i></button>
                            </div>
                        </div>`;
        const newReqRow = $(newRowHTML);
        reqContainer.append(newReqRow);

        const summernoteEditor = newReqRow.find(".summernote-persyaratan");
        initializeSummernote(summernoteEditor);
        summernoteEditor.summernote("code", reqContent);
      });
    }

    // Aktifkan tab yang terakhir dibuka
    if (formData.activeTab) {
      $("#" + formData.activeTab).tab("show");
    }
  }

  // Fungsi debounce untuk menunda eksekusi agar tidak terlalu sering
  function debounce(func, delay) {
    let timeout;
    return function (...args) {
      const context = this;
      clearTimeout(timeout);
      timeout = setTimeout(() => func.apply(context, args), delay);
    };
  }

  const debouncedSave = debounce(saveFormDataToLocalStorage, 400);

  // 1. Panggil fungsi load saat halaman pertama kali dibuka
  loadFormDataFromLocalStorage();

  // 2. Gunakan event delegation pada seluruh form untuk event 'input'
  $("#form-tambah-skema").on(
    "input change",
    "input, select, textarea",
    debouncedSave
  );

  // 3. Pemicu khusus untuk summernote
  $(document).on("summernote.change", ".summernote-persyaratan", debouncedSave);

  // 4. Simpan saat menambah/menghapus baris dinamis
  $(document).on(
    "click",
    "#add-unit-button, .remove-unit-button, #add-persyaratan-button, .remove-persyaratan-button",
    function () {
      setTimeout(saveFormDataToLocalStorage, 100);
    }
  );

  // 5. Simpan saat berpindah tab
  $(".next-tab, .prev-tab").on("click", saveFormDataToLocalStorage);

  // =======================================================
  // --- FUNGSI UNTUK TOMBOL BATAL ---
  // =======================================================

  // --- FUNGSI BARU UNTUK MEMERIKSA APAKAH FORM SUDAH DIISI ---
  function isFormDirty() {
    let isDirty = false;

    // 1. Periksa semua input teks, select, dan textarea di Tab 1
    $("#content-tab-skema")
      .find('input[type="text"], input[type="date"], select, textarea')
      .each(function () {
        if ($(this).val() && $(this).val().trim() !== "") {
          isDirty = true;
          return false; // Keluar dari loop jika satu saja field terisi
        }
      });
    if (isDirty) return true;

    // 2. Periksa input file
    if ($("#fileSkema").get(0).files.length > 0) {
      return true;
    }

    // 3. Periksa apakah ada lebih dari satu baris di Tab 2 atau Tab 3
    if (
      $("#unit-skema-container .unit-skema-row").length > 1 ||
      $("#persyaratan-container .persyaratan-row").length > 1
    ) {
      return true;
    }

    // 4. Periksa isi dari baris pertama di Tab 2
    $("#unit-skema-container .unit-skema-row:first")
      .find("input, select")
      .each(function () {
        if ($(this).val() && $(this).val().trim() !== "") {
          isDirty = true;
          return false;
        }
      });
    if (isDirty) return true;

    // 5. Periksa isi dari baris pertama (Summernote) di Tab 3
    const firstSummernote = $(
      "#persyaratan-container .summernote-persyaratan"
    ).first();
    if (firstSummernote.length > 0 && !firstSummernote.summernote("isEmpty")) {
      return true;
    }

    // Jika semua pengecekan gagal, berarti form masih bersih
    return false;
  }

  // =======================================================
  // --- FUNGSI UNTUK TOMBOL BATAL ---
  // =======================================================
  $(document).on("click", "#cancel-button", function (e) {
    e.preventDefault(); // Mencegah link langsung berpindah halaman
    const targetUrl = $(this).attr("href");

    // Cek apakah form sudah diisi menggunakan fungsi di atas
    if (isFormDirty()) {
      // JIKA FORM BERISI: Tampilkan konfirmasi SweetAlert
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
          // Hapus data dari localStorage dan arahkan ke halaman lain
          localStorage.removeItem("skemaFormData");

          // Tampilkan notifikasi sukses (opsional)
          Toast.fire({
            icon: "success",
            text: "Input telah dibersihkan.",
          });

          // Tunggu sejenak lalu arahkan ke halaman daftar skema
          setTimeout(function () {
            window.location.href = targetUrl;
          }, 1000); // delay 1 detik
        }
      });
    } else {
      // JIKA FORM KOSONG: Langsung pindah halaman tanpa alert
      localStorage.removeItem("skemaFormData"); // Bersihkan juga untuk jaga-jaga
      window.location.href = targetUrl;
    }
  });

  // =======================================================================
  // --- SCRIPT AWAL UNTUK FORM DINAMIS ---
  // =======================================================================

  $(document).on("click", "#add-unit-button", function () {
    // Cari template baris form yang akan digandakan
    var template = $("#unit-skema-container .unit-skema-row:first");

    // Kloning/gandakan baris template
    var newUnitRow = template.clone();

    // Kosongkan semua nilai input pada baris baru
    newUnitRow.find("input, select").val("");

    // Pastikan tombol hapus terlihat (penting jika baris pertama disembunyikan)
    newUnitRow.find(".remove-unit-button").show();

    // Tambahkan baris baru ke dalam container
    $("#unit-skema-container").append(newUnitRow);
  });

  // Menggunakan Event Delegation untuk tombol 'Hapus'
  // Ini memastikan tombol hapus pada baris baru juga akan berfungsi
  $(document).on("click", ".remove-unit-button", function () {
    // Cek jumlah baris yang ada
    if ($("#unit-skema-container .unit-skema-row").length > 1) {
      // Hapus elemen card (.unit-skema-row) terdekat dari tombol yang diklik
      $(this).closest(".unit-skema-row").remove();
    } else {
      // Beri peringatan jika mencoba menghapus baris terakhir
      Toast.fire({
        icon: "error",
        title: "Minimal harus ada satu unit skema.",
      });
    }
  });

  // Fungsi untuk menginisialisasi Summernote pada sebuah elemen textarea
  function initializeSummernote(element) {
    element.summernote({
      height: 100,
      toolbar: [], // Toolbar default disembunyikan
      callbacks: {
        // Saat kursor masuk ke editor (fokus), simpan instance-nya
        onFocus: function () {
          activeSummernoteInstance = $(this);
          // Beri highlight biru untuk menandakan editor aktif
          $(".note-editor").removeClass("border-primary"); // Hapus highlight dari yang lain
          $(this).next(".note-editor").addClass("border-primary");
        },
        // Saat kursor keluar, hapus highlight
        onBlur: function () {
          $(this).next(".note-editor").removeClass("border-primary");
        },
      },
    });
  }

  // Inisialisasi editor pertama yang sudah ada saat halaman dimuat
  if ($(".summernote-persyaratan").length > 0) {
    initializeSummernote($(".summernote-persyaratan"));
  }

  // Event handler untuk tombol 'Tambah Persyaratan'
  $(document).on("click", "#add-persyaratan-button", function () {
    // Buat elemen baris baru dari template HTML (lebih aman daripada clone)
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

    // Tambahkan baris baru ke container
    $("#persyaratan-container").append(newRow);

    // Inisialisasi Summernote HANYA pada textarea yang baru dibuat
    initializeSummernote(newRow.find(".summernote-persyaratan"));
  });

  // Event handler untuk tombol 'Hapus'
  $(document).on("click", ".remove-persyaratan-button", function () {
    if ($("#persyaratan-container .persyaratan-row").length > 1) {
      var rowToRemove = $(this).closest(".persyaratan-row");
      // Hancurkan instance summernote sebelum menghapus elemen HTML-nya
      rowToRemove.find(".summernote-persyaratan").summernote("destroy");
      rowToRemove.remove();
    } else {
      Toast.fire({
        icon: "error",
        title: "Minimal harus ada satu persyaratan.",
      });
    }
  });

  // Event handler untuk toolbar kustom
  $("#custom-summernote-toolbar").on("click", "button", function (e) {
    e.preventDefault(); // Mencegah fokus hilang dari editor
    var command = $(this).data("command");

    // Cek apakah ada editor yang aktif
    if (activeSummernoteInstance && command) {
      // Langsung jalankan perintah pada editor yang aktif
      activeSummernoteInstance.summernote(command);
    } else {
      Toast.fire({
        icon: "error",
        title:
          "Silakan klik di dalam kolom teks untuk mengaktifkan editor terlebih dahulu.",
      });
    }
  });

  // =======================================================================
  // --- SCRIPT AKHIR UNTUK FORM DINAMIS ---
  // =======================================================================

  // ===================================================================
  // FUNGSI VALIDASI UTAMA
  // ===================================================================

  /**
   * Memvalidasi semua input yang diperlukan di dalam sebuah tab.
   * @param {jQuery} tabElement - Elemen jQuery dari .tab-pane yang akan divalidasi.
   * @returns {object} - Mengembalikan objek { isValid: boolean, firstInvalidElement: jQuery|null }.
   */
  function validateTab(tabElement) {
    let isValid = true;
    let firstInvalidElement = null;

    // Hapus status invalid sebelumnya
    tabElement.find(".is-invalid").removeClass("is-invalid");

    // Validasi input, select, dan textarea biasa
    tabElement
      .find("input[required], select[required], textarea[required]")
      .each(function () {
        const input = $(this);
        if (!input.val() || input.val().trim() === "") {
          isValid = false;
          input.addClass("is-invalid");
          if (!firstInvalidElement) {
            firstInvalidElement = input;
          }
        }
      });

    // Validasi khusus untuk Summernote
    tabElement.find(".summernote-persyaratan").each(function () {
      const summernote = $(this);
      if (summernote.summernote("isEmpty")) {
        isValid = false;
        summernote.next(".note-editor").addClass("is-invalid");
        if (!firstInvalidElement) {
          // Fokus pada editor summernote
          firstInvalidElement = summernote;
        }
      }
    });

    return { isValid: isValid, firstInvalidElement: firstInvalidElement };
  }

  // ===================================================================
  // EVENT HANDLERS
  // ===================================================================

  // 1. Untuk input teks, tanggal, select, dan file biasa
  $("#form-tambah-skema").on("input change", ".is-invalid", function () {
    const input = $(this);
    if (input.val() && input.val().trim() !== "") {
      input.removeClass("is-invalid");
      // Khusus untuk Select2, hapus juga error di elemennya
      if (input.hasClass("select2-hidden-accessible")) {
        input
          .next(".select2-container")
          .find(".select2-selection--single")
          .removeClass("is-invalid");
      }
    }
  });

  // 2. Untuk Summernote
  // Kita harus menggunakan event 'summernote.change'
  $(document).on("summernote.change", ".summernote-persyaratan", function () {
    const summernote = $(this);
    if (!summernote.summernote("isEmpty")) {
      summernote.next(".note-editor").removeClass("is-invalid");
    }
  });

  // --- TOMBOL NAVIGASI TAB ---
//   $(".next-tab").on("click", function () {
//     const currentTab = $(this).closest(".tab-pane");
//     const validationResult = validateTab(currentTab);

//     if (validationResult.isValid) {
//       const targetTabId = $(this).data("target-tab");
//       $("#" + targetTabId).tab("show");
//     } else {
//       // Validasi gagal: Tampilkan Toast dan fokus
//       Toast.fire({
//         icon: "error",
//         title: "Harap isi semua kolom yang wajib diisi.",
//       });
//       if (validationResult.firstInvalidElement) {
//         validationResult.firstInvalidElement.focus();
//         // Jika itu summernote, fokus secara spesifik
//         if (
//           validationResult.firstInvalidElement.hasClass(
//             "summernote-persyaratan"
//           )
//         ) {
//           validationResult.firstInvalidElement.summernote("focus");
//         }
//       }
//     }
//   });

//   $(".prev-tab").on("click", function () {
//     const targetTabId = $(this).data("target-tab");
//     $("#" + targetTabId).tab("show");
//   });

//   $(".card-tabs .nav-tabs .nav-link").on("click", function (e) {
//     e.preventDefault();
//     Toast.fire({
//       icon: "info",
//       title: 'Gunakan tombol "Selanjutnya" atau "Sebelumnya".',
//     });
//     return false;
//   });

  // --- TOMBOL SIMPAN (SUBMIT FORM) ---
  $("#form-tambah-skema").on("submit", function (e) {
    e.preventDefault(); // Selalu cegah submit default

    let isAllTabsValid = true;
    let firstInvalidTabLink = null;
    let elementToFocus = null;

    // Validasi setiap tab secara berurutan
    $(".tab-pane").each(function () {
      const tab = $(this);
      const validationResult = validateTab(tab);

      // Jika tab ini tidak valid DAN kita belum menemukan tab lain yang error
      if (!validationResult.isValid && isAllTabsValid) {
        isAllTabsValid = false; // Tandai bahwa ada error
        firstInvalidTabId = tab.attr("id"); // Simpan ID tab yang error
        elementToFocus = validationResult.firstInvalidElement; // Simpan elemen yang error
      }
    });

    if (isAllTabsValid) {
      // Jika semua valid, hapus data local storage dan submit form
      localStorage.removeItem(formKey);
      // Untuk simulasi, kita tampilkan alert sukses. Ganti dengan this.submit() asli
      Swal.fire("Sukses!", "Formulir berhasil disimpan.", "success");
      // this.submit();
    } else {
      if (firstInvalidTabId) {
        const tabLink = $('.nav-tabs a[href="#' + firstInvalidTabId + '"]');
        tabLink.tab("show");

        // Beri jeda agar perpindahan tab selesai sebelum fokus
        setTimeout(() => {
          if (elementToFocus) {
            if (elementToFocus.hasClass("summernote-persyaratan")) {
              elementToFocus.summernote("focus");
            } else {
              elementToFocus.focus();
            }
          }
        }, 250); // Jeda 250ms
      }
    }
  });
});
