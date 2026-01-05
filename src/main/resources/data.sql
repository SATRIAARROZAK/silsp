SELECT * FROM ref_educations;
SELECT * FROM type_pendidikan;

-- DATA PENDIDIKAN --
INSERT INTO type_pendidikan (id_jenis_pendidikan, jenis_pendidikan) VALUES (1, 'SD');
INSERT INTO type_pendidikan (id_jenis_pendidikan, jenis_pendidikan) VALUES (2, 'SMP');
INSERT INTO type_pendidikan (id_jenis_pendidikan, jenis_pendidikan) VALUES (3, 'SMA/Sederajat');
INSERT INTO type_pendidikan (id_jenis_pendidikan, jenis_pendidikan) VALUES (4, 'D2');
INSERT INTO type_pendidikan (id_jenis_pendidikan, jenis_pendidikan) VALUES (5, 'D3');
INSERT INTO type_pendidikan (id_jenis_pendidikan, jenis_pendidikan) VALUES (6, 'D4');
INSERT INTO type_pendidikan (id_jenis_pendidikan, jenis_pendidikan) VALUES (7, 'S1');
INSERT INTO type_pendidikan (id_jenis_pendidikan, jenis_pendidikan) VALUES (8, 'S2');
INSERT INTO type_pendidikan (id_jenis_pendidikan, jenis_pendidikan) VALUES (9, 'S3');
INSERT INTO type_pendidikan (id_jenis_pendidikan, jenis_pendidikan) VALUES (10, 'D1');

-- DATA JENIS PEKERJAAN --
INSERT INTO type_pekerjaan (id_jenis_pekerjaan, jenis_pekerjaan) VALUES ( 1,'Belum/Tidak Bekerja');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan, jenis_pekerjaan) VALUES ( 2,'Mengurus Rumah Tangga');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan, jenis_pekerjaan) VALUES ( 3,'Pelajar/Mahasiswa');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan, jenis_pekerjaan) VALUES ( 4,'Pensiunan');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan, jenis_pekerjaan) VALUES ( 5,'Pegawai Negeri Sipil (PNS)');
INSERT INTO type_pekerjaan  (id_jenis_pekerjaan, jenis_pekerjaan) VALUES ( 6,'Tentara Nasional Indonesia (TNI)');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan, jenis_pekerjaan) VALUES ( 7,'Kepolisian RI (POLRI)');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan, jenis_pekerjaan) VALUES ( 8,'Perdagangan');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan, jenis_pekerjaan) VALUES ( 9,'Petani/Pekebun');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (10,'Peternak');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (11,'Nelayan/Perikanan');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (12,'Industri');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (13,'Konstruksi');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (14,'Transportasi');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (15,'Karyawan Swasta');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (16,'Karyawan BUMN');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (17,'Karyawan BUMD');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (18,'Karyawan Honorer');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (19,'Buruh Harian Lepas');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (20,'Buruh Tani/Perkebunan');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (21,'Buruh Nelayan/Perikanan');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (22,'Buruh Peternakan');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (23,'Pembantu Rumah Tangga');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (24,'Tukang Cukur');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (25,'Tukang Listrik');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (26,'Tukang Batu');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (27,'Tukang Kayu');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (28,'Tukang Sol Sepatu');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (29,'Tukang Las/Pandai Besi');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (30,'Tukang Jahit');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (31,'Tukang Gigi');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (32,'Penata Rias');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (33,'Penata Busana');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (34,'Penata Rambut');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (35,'Mekanik');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (36,'Seniman');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (37,'Tabib');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (38,'Paraji');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (39,'Perancang Busana');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (40,'Penterjemah');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (41,'Imam Masjid');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (42,'Pendeta');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (43,'Pastor');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (44,'Wartawan');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (45,'Ustadz/Mubaligh');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (46,'Juru Masak');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (47,'Promotor Acara');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (48,'Anggota DPR-RI');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (49,'Anggota DPD');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (50,'Anggota BPK');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (51,'Presiden');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (52,'Wakil Presiden');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (53,'Anggota Mahkamah');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (54,'Konstitusi');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (55,'Anggota Kabinet/Kementrian');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (56,'Duta Besar');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (57,'Gubernur');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (58,'Wakil Gubernur');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (59,'Bupati');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (60,'Wakil Bupati');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (61,'Walikota');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (62,'Wakil Walikota');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (63,'Anggota DPRD Provinsi/Kab/Kota');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (64,'Dosen');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (65,'Guru');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (66,'Pilot');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (67,'Pengacara');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (68,'Notaris');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (69,'Arsitek');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (70,'Akuntan');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (71,'Konsultan');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (72,'Dokter');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (73,'Bidan');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (74,'Perawat');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (75,'Apoteker');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (76,'Psikiater/Psikolog');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (77,'Penyiar Televisi');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (78,'Penyiar Radio');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (79,'Pelaut');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (80,'Peneliti');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (81,'Sopir');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (82,'Pialang');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (83,'Paranormal');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (84,'Pedagang');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (85,'Perangkat Desa');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (86,'Kepala Desa');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (87,'Biarawati');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (88,'Wiraswasta');
INSERT INTO type_pekerjaan (id_jenis_pekerjaan,  jenis_pekerjaan) VALUES (89,'Lainny');
-- Pastikan nama role SAMA dengan value di HTML option value="ADMIN"
INSERT INTO roles (id_role, nama_role) VALUES (1, 'Admin');
INSERT INTO roles (id_role, nama_role) VALUES (2, 'Asesi');
INSERT INTO roles (id_role, nama_role) VALUES (3, 'Asesor');
INSERT INTO roles (id_role, nama_role) VALUES (4, 'Direktur');

-- DATA JENIS SKEMA DAN MODE SKEMA --
INSERT INTO type_skema (nama_jenis_skema) VALUES ('KKNI'), ('OKUPASI'), ('KLASTER');
INSERT INTO type_skema_pengajuan (jenis_skema_pengajuan) VALUES ('Mandiri'), ('Referensi BNSP');
INSERT INTO type_tuk (jenis_skema_pengajuan) VALUES ('Mandiri'), ('Referensi BNSP');

-- 1. Tabel Referensi Sumber Anggaran
CREATE TABLE IF NOT EXISTS ref_sumber_anggaran (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nama_sumber VARCHAR(255) NOT NULL
);

-- Insert Data Sumber Anggaran
INSERT INTO ref_sumber_anggaran (id, nama_sumber) VALUES 
(1, 'Sumber anggaran dari APBN'),
(2, 'Sumber anggaran dari APBD'),
(3, 'Sumber anggaran biaya dari perusahaan'),
(4, 'Sumber anggaran biaya mandiri'),
(5, 'Sumber anggaran APBN SET BNSP'),
(6, 'Sumber anggaran APBN BLK');

-- 2. Tabel Referensi Pemberi Anggaran (Kementrian/Lembaga)
CREATE TABLE IF NOT EXISTS ref_pemberi_anggaran (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nama_instansi VARCHAR(255) NOT NULL
);

-- Insert Data Pemberi Anggaran (Sebagian data dari list Anda, silakan lengkapi jika perlu)
INSERT INTO ref_pemberi_anggaran (id, nama_instansi) VALUES 
(1, 'Kementerian Koordinator Bidang Politik, Hukum, dan Keamanan'),
(2, 'Kementerian Koordinator Bidang Perekonomian'),
(3, 'Kementerian Koordinator Bidang Pembangunan Manusia dan Kebudayaan'),
(4, 'Kementerian Koordinator Bidang Kemaritiman'),
(5, 'Kementerian Dalam Negeri'),
(6, 'Kementerian Luar Negeri'),
(7, 'Kementerian Pertahanan'),
(8, 'Kementerian Agama'),
(9, 'Kementerian Hukum dan Hak Asasi Manusia'),
(10, 'Kementerian Keuangan'),
(11, 'Kementerian Pendidikan dan Kebudayaan'),
(12, 'Kementerian Riset, Teknologi, dan Pendidikan Tinggi'),
(13, 'Kementerian Sosial'),
(14, 'Kementerian Kesehatan'),
(15, 'Kementerian Ketenagakerjaan'),
(16, 'Kementerian Perindustrian'),
(17, 'Kementerian Perdagangan'),
(18, 'Kementerian Energi dan Sumber Daya Mineral'),
(19, 'Kementerian Pekerjaan Umum dan Perumahan Rakyat'),
(20, 'Kementerian Perhubungan'),
(21, 'Kementerian Komunikasi dan Informatika'),
(22, 'Kementerian Pertanian'),
(23, 'Kementerian Lingkungan Hidup dan Kehutanan'),
(24, 'Kementerian Kelautan dan Perikanan'),
(25, 'Kementerian Desa, Pembangunan Daerah Tertinggal, dan Transmigrasi'),
(26, 'Kementerian Agraria dan Tata Ruang'),
(27, 'Kementerian Perencanaan Pembangunan Nasional'),
(28, 'Kementerian Pendayagunaan Aparatur Negara dan Reformasi Birokrasi'),
(29, 'Kementerian Badan Usaha Milik Negara'),
(30, 'Kementerian Koperasi dan Usaha Kecil dan Menengah'),
(31, 'Kementerian Pariwisata'),
(32, 'Kementerian Pemberdayaan Perempuan dan Perlindungan Anak'),
(33, 'Kementerian Pemuda dan Olahraga'),
(34, 'Kementerian Sekretariat Negara'),
(35, 'Lembaga Sandi Negara'),
(36, 'Badan Kepegawaian Negara'),
(37, 'Lembaga Administrasi Negara'),
(38, 'Lembaga Penerbangan dan Antariksa Nasional'),
(39, 'Lembaga Ilmu Pengetahuan Indonesia'),
(40, 'Badan Tenaga Nuklir Nasional'),
(41, 'Badan Pusat Statistik'),
(42, 'Arsip Nasional Republik Indonesia'),
(43, 'Badan Informasi Geospasial'),
(44, 'Badan Koordinasi Keluarga Berencana Nasional (BKKBN)'),
(45, 'Badan Koordinasi Penanaman Modal'),
(46, 'Badan Pengkajian dan Penerapan Teknologi'),
(47, 'Badan Pemeriksa Keuangan dan Pembangunan'),
(48, 'Perpustakaan Nasional'),
(49, 'Badan Standarisasi Nasional'),
(50, 'Badan Pengawas Obat dan Makanan'),
(51, 'Lembaga Ketahanan Nasional RI'),
(52, 'Badan Meteorologi Klimatologi dan Geofisika'),
(53, 'Badan Narkotika Nasional'),
(54, 'Badan Nasional Penanggulangan Bencana'),
(55, 'Badan Nasional Penempatan dan Perlindungan Tenaga Kerja Indonesia'),
(58, 'Lembaga Kebijakan Pengadaan Barang/Jasa Pemerintah'),
(59, 'Badan Nasional Penanggulangan Terorisme'),
(60, 'Badan Ekonomi Kreatif'),
(61, 'Badan Pengawas Tenaga Nuklir'),
(62, 'Biaya dari Pemda'),
(63, 'MPR - RI'),
(64, 'Sekretariat Jenderal DPR - RI'),
(65, 'DPD - RI'),
(66, 'MA - RI'),
(67, 'MK - RI'),
(68, 'Komisi Yudisial'),
(69, 'BPK - RI'),
(70, 'Kejaksaan Agung RI'),
(71, 'Kepolisian RI'),
(72, 'TNI'),
(73, 'Sekretaris Kabinet'),
(74, 'Arsip Nasional (ANRI)'),
(75, 'Badan Inteligen Negara'),
(76, 'Badan Kependudukan dan Keluarga Berencana Nasional'),
(77, 'Badan Informasi Geospasial (BIG)'),
(78, 'Badan Meteorologi, Klimatologi, dan Geofisika'),
(79, 'Badan Nasional Penanggulangan Bencana (BNPB)'),
(80, 'Badan Nasional Penempatan dan Perlindungan Tenaga Kerja Indonesia (BNP2TKI)'),
(81, 'Badan Pengawas Keuangan dan Pembangunan (BPKP)'),
(82, 'Badan Pengkajian dan Penerapan Teknologi (BPPT)'),
(83, 'Badan SAR Nasional'),
(84, 'Badan Standardisasi Nasional'),
(85, 'Perpustakaan Nasional Republik Indonesia'),
(86, 'Akademi Ilmu Pengetahuan Indonesia'),
(87, 'Badan Amil Zakat Nasional'),
(88, 'Badan Koordinasi Keamanan Laut'),
(89, 'Badan Nasional Pengelola Perbatasan'),
(90, 'Badan Nasional Sertifikasi Profesi'),
(91, 'Badan Olahraga Profesional'),
(92, 'Badan Pelaksana Pengelola Masjid Istiqlal'),
(93, 'Badan Pendukung Pengembangan Sistem Penyediaan Air Minum'),
(94, 'Badan Pengatur Hilir Minyak dan Gas'),
(95, 'Badan Pengawas Pasar Tenaga Listrik'),
(96, 'Badan Pengawas Pemilihan Umum'),
(97, 'Badan Pengelola Dana Abadi Umat'),
(98, 'Badan Pengembangan Wilayahan Surabaya-Madura'),
(99, 'Badan Pengusahaan Kawasan Perdagangan Bebas dan Pelabuhan Bebas Batam'),
(100, 'Biaya Mandiri'),
(101, 'Badan Pengusahaan Kawasan Perdagangan Bebas dan Pelabuhan Bebas Bintan'),
(102, 'Badan Pengusahaan Kawasan Perdagangan Bebas dan Pelabuhan Bebas Karimun'),
(103, 'Badan Pengusahaan Kawasan Perdagangan Bebas dan Pelabuhan Sabang'),
(104, 'Badan Perlindungan Konsumen Nasional'),
(105, 'Badan Pertimbangan Kepegawaian'),
(106, 'Badan Pertimbangan Kesehatan Nasional'),
(107, 'Badan Pertimbangan Perfilman Nasional'),
(108, 'Badan Standarisasi dan Akreditasi Nasional Keolahragaan'),
(109, 'Dewan Energi Nasional'),
(110, 'Dewan Jaminan Sosial Nasional'),
(111, 'Dewan Kawasan Perdagangan Bebas dan Pelabuhan Bebas Batam'),
(112, 'Dewan Kawasan Perdagangan Bebas dan Pelabuhan Bebas Bintan'),
(113, 'Dewan Kawasan Perdagangan Bebas dan Pelabuhan Bebas Karimun'),
(114, 'Dewan Ketahanan Pangan'),
(115, 'Dewan Koperasi Indonesia'),
(116, 'Dewan Nasional Kawasan Ekonomi Khusus'),
(117, 'Dewan Pengupahan Nasional'),
(118, 'Dewan Pers'),
(119, 'Dewan Pertimbangan Otonomi Daerah'),
(120, 'Dewan Pertimbangan Presiden'),
(121, 'Dewan Riset Nasional'),
(122, 'Dewan Sumber Daya Air Nasional'),
(123, 'Dewan Teknologi Informasi dan Komunikasi Nasional'),
(124, 'Komisi Banding Merek'),
(125, 'Komisi Banding Paten'),
(126, 'Komisi Informasi Pusat'),
(127, 'Komisi Kejaksaan'),
(128, 'Komisi Kepolisian Nasional'),
(129, 'Komisi Nasional Anti Kekerasan Terhadap Perempuan'),
(130, 'Komisi Nasional Hak Asasi Manusia'),
(131, 'Komisi Nasional Lanjut Usia'),
(132, 'Komisi Pemberantasan Korupsi'),
(133, 'Komisi Pemilihan Umum'),
(134, 'Komisi Pengawas Haji Indonesia'),
(135, 'Komisi Pengawas Persaingan Usaha'),
(136, 'Komisi Penyiaran Indonesia'),
(137, 'Komisi Perlindungan Anak Indonesia'),
(138, 'Komite Akreditasi Nasional'),
(139, 'Komite Inovasi Nasional'),
(140, 'Komite Kebijakan Percepatan Penyediaan Infrastruktur'),
(141, 'Komite Nasional Keselamatan Transportasi'),
(142, 'Komite Nasional Pengendalian Flu Burung (Avian Influenza) dan Kesiapsiagaan Menghadapi Pandemi Influenza'),
(143, 'Komite Olah Raga Nasional Indonesia'),
(144, 'Komite Privatisasi Perusahaan Perseroan'),
(145, 'Komite Standar Akuntansi Pemerintah'),
(146, 'Komite Standar Nasional untuk Satuan Ukuran'),
(147, 'Konsil Kedokteran Indonesia'),
(148, 'Lembaga Kerja Sama Tripartit'),
(149, 'Lembaga Perlindungan Saksi dan Korban'),
(150, 'Lembaga Produktivitas Nasional'),
(151, 'Lembaga Sensor Film'),
(152, 'Majelis Disiplin Tenaga Kesehatan'),
(153, 'Ombudsman'),
(154, 'Pusat Pelaporan dan Analisis Transaksi Keuangan'),
(155, 'Tim Koordinasi Penanggulangan Kemiskinan'),
(156, 'Unit Kerja Presiden Bidang Pengawasan dan Pengendalian Pembangunan(UKP- 4)'),
(157, 'Lembaga Penyiaran Publik Radio Republik Indonesia'),
(158, 'Lembaga Penyiaran Publik Televisi Republik Indonesia'),
(159, 'BPJS Kesehatan'),
(160, 'BPJS Ketenagakerjaan');