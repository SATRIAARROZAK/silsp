$(document).ready(function() {
    // Inisialisasi Select2
    // $('.select2').select2({theme: 'bootstrap4'});

    // ==================================================================
    // 1. FETCH API BNSP (Via Java Proxy untuk menghindari CORS)
    // ==================================================================
    
    // A. Sumber Anggaran
    $.ajax({
        url: '/api/proxy/jenis-anggaran',
        method: 'GET',
        success: function(response) {
            // Sesuaikan dengan struktur JSON API BNSP
            // Biasanya response.data atau langsung array
            var data = response.data || response; 
            var options = '<option value="">-- Pilih Sumber Anggaran --</option>';
            
            if(Array.isArray(data)) {
                data.forEach(function(item) {
                    // Sesuaikan 'id' dan 'label' dengan field asli API BNSP
                    options += `<option value="${item.label || item.nama_anggaran}">${item.label || item.nama_anggaran}</option>`;
                });
            }
            $('#apiAnggaran').html(options);
        },
        error: function() {
            $('#apiAnggaran').html('<option value="">Gagal memuat data API</option>');
        }
    });

    // B. Pemberi Anggaran (Kementrian)
    $.ajax({
        url: '/api/proxy/kementrian',
        method: 'GET',
        success: function(response) {
            var data = response.data || response;
            var options = '<option value="">-- Pilih Pemberi Anggaran --</option>';
            
            if(Array.isArray(data)) {
                data.forEach(function(item) {
                     // Sesuaikan 'nama_kementrian' dengan field asli API
                    options += `<option value="${item.nama_kementrian || item.label}">${item.nama_kementrian || item.label}</option>`;
                });
            }
            $('#apiPemberi').html(options);
        },
        error: function() {
            $('#apiPemberi').html('<option value="">Gagal memuat data API</option>');
        }
    });


    // ==================================================================
    // 2. LOGIKA PILIH ASESOR (Fix Button Click)
    // ==================================================================
    var selectedAsesorIds = [];

    $('#btnPilihAsesor').click(function() {
        // Ambil data dari select2
        var id = $('#selectAsesor').val();
        // Ambil text nama dari option yang dipilih
        var nama = $('#selectAsesor option:selected').text();
        // Ambil data-nomet dari atribut HTML
        var noMet = $('#selectAsesor option:selected').data('nomet');

        // Validasi
        if (!id) {
            Swal.fire('Peringatan', 'Silakan pilih asesor terlebih dahulu', 'warning');
            return;
        }
        if (selectedAsesorIds.includes(id)) {
            Swal.fire('Info', 'Asesor ini sudah ditambahkan', 'info');
            return;
        }

        // Tambah ke Array
        selectedAsesorIds.push(id);
        $('#emptyAsesor').hide();

        // Render Baris Tabel
        var rowHtml = `
            <tr id="row-asesor-${id}">
                <td>${noMet ? noMet : '<span class="text-warning">Belum ada No. MET</span>'}</td>
                <td>${nama}</td>
                <td class="text-center">
                    <button type="button" class="btn btn-danger btn-xs" onclick="hapusAsesor('${id}')">
                        <i class="fas fa-trash"></i>
                    </button>
                    <input type="hidden" name="assessorIds" value="${id}">
                </td>
            </tr>
        `;
        $('#tbodyAsesor').append(rowHtml);
        
        // Reset Select
        $('#selectAsesor').val('').trigger('change');
    });

    // Fungsi Hapus Asesor (Harus global window agar bisa dipanggil onclick)
    window.hapusAsesor = function(id) {
        $(`#row-asesor-${id}`).remove();
        selectedAsesorIds = selectedAsesorIds.filter(item => item !== id);
        if (selectedAsesorIds.length === 0) $('#emptyAsesor').show();
    };


    // ==================================================================
    // 3. LOGIKA PILIH SKEMA & LOAD UNIT (Fix Unit Not Showing)
    // ==================================================================
    var selectedSchemaIds = [];

    $('#btnPilihSkema').click(function() {
        var id = $('#selectSkema').val();
        var namaSkema = $('#selectSkema option:selected').text();

        if (!id) {
            Swal.fire('Peringatan', 'Silakan pilih skema terlebih dahulu', 'warning');
            return;
        }
        if (selectedSchemaIds.includes(id)) {
            Swal.fire('Info', 'Skema ini sudah ditambahkan', 'info');
            return;
        }

        // Tampilkan Loading
        Swal.fire({title: 'Memuat Unit...', didOpen: () => Swal.showLoading()});

        // Panggil Internal API untuk dapat detail unit
        $.ajax({
            url: `/api/internal/skema/${id}/units`,
            method: 'GET',
            success: function(units) {
                Swal.close();
                selectedSchemaIds.push(id);
                $('#emptySkema').hide();

                // Buat Tabel Unit
                var unitRows = '';
                if (units && units.length > 0) {
                    units.forEach(function(u, index) {
                        unitRows += `
                            <tr>
                                <td>${index + 1}</td>
                                <td>${u.code}</td>
                                <td>${u.title}</td>
                            </tr>
                        `;
                    });
                } else {
                    unitRows = '<tr><td colspan="3" class="text-center">Tidak ada unit kompetensi terdaftar.</td></tr>';
                }

                // Render Card Skema
                var cardHtml = `
                    <div class="card card-outline card-success mb-3" id="panel-skema-${id}">
                        <div class="card-header">
                            <h3 class="card-title font-weight-bold"><i class="fas fa-book mr-2"></i>${namaSkema}</h3>
                            <div class="card-tools">
                                <button type="button" class="btn btn-tool" onclick="hapusSkema('${id}')">
                                    <i class="fas fa-times text-danger"></i> Hapus
                                </button>
                                <input type="hidden" name="schemaIds" value="${id}">
                            </div>
                        </div>
                        <div class="card-body p-0">
                            <table class="table table-sm table-hover">
                                <thead style="background-color: #f4f6f9">
                                    <tr>
                                        <th style="width: 10px">#</th>
                                        <th style="width: 200px">Kode Unit</th>
                                        <th>Judul Unit Kompetensi</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    ${unitRows}
                                </tbody>
                            </table>
                        </div>
                    </div>
                `;
                $('#schemaContainer').append(cardHtml);
                $('#selectSkema').val('').trigger('change');
            },
            error: function() {
                Swal.fire('Error', 'Gagal mengambil data unit skema. Pastikan API Internal berjalan.', 'error');
            }
        });
    });

    window.hapusSkema = function(id) {
        $(`#panel-skema-${id}`).remove();
        selectedSchemaIds = selectedSchemaIds.filter(item => item !== id);
        if (selectedSchemaIds.length === 0) $('#emptySkema').show();
    };

});