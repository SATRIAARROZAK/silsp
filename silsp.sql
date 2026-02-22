-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.4.3 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.8.0.6908
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for silsp_db
CREATE DATABASE IF NOT EXISTS `silsp_db` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_bin */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `silsp_db`;

-- Dumping structure for table silsp_db.jadwal_ujiasesor
CREATE TABLE IF NOT EXISTS `jadwal_ujiasesor` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_asesor` bigint DEFAULT NULL,
  `id_jadwal` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8ykgqmp0638gm55y8slbdkcn` (`id_asesor`),
  KEY `FKchjdprv572gnv70hjex470x2a` (`id_jadwal`),
  CONSTRAINT `FK8ykgqmp0638gm55y8slbdkcn` FOREIGN KEY (`id_asesor`) REFERENCES `users` (`id_user`),
  CONSTRAINT `FKchjdprv572gnv70hjex470x2a` FOREIGN KEY (`id_jadwal`) REFERENCES `jadwal_ujisertifikasi` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.jadwal_ujisertifikasi
CREATE TABLE IF NOT EXISTS `jadwal_ujisertifikasi` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `kode_jadwal_bnsp` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `kode_jadwal` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `nama_jadwal` varchar(255) COLLATE latin1_bin NOT NULL,
  `kuota_peserta` int DEFAULT NULL,
  `tanggal_mulai` date DEFAULT NULL,
  `id_pemberi_anggaran` bigint DEFAULT NULL,
  `id_sumber_anggaran` bigint DEFAULT NULL,
  `id_tuk` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKtdwidvb0u8jhsw66152ais4i6` (`kode_jadwal`),
  KEY `FKitsj64mx1d369lwv4nd6tyt3g` (`id_pemberi_anggaran`),
  KEY `FKqsw616b5rxl5duec8npcc27vk` (`id_sumber_anggaran`),
  KEY `FKcb3nhi0l0acuon9oibrbu8rd9` (`id_tuk`),
  CONSTRAINT `FKcb3nhi0l0acuon9oibrbu8rd9` FOREIGN KEY (`id_tuk`) REFERENCES `tuk` (`id_tuk`),
  CONSTRAINT `FKitsj64mx1d369lwv4nd6tyt3g` FOREIGN KEY (`id_pemberi_anggaran`) REFERENCES `tb_pemberi_anggaran` (`id`),
  CONSTRAINT `FKqsw616b5rxl5duec8npcc27vk` FOREIGN KEY (`id_sumber_anggaran`) REFERENCES `tb_sumber_anggaran` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.jadwal_ujiskema
CREATE TABLE IF NOT EXISTS `jadwal_ujiskema` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_jadwal` bigint DEFAULT NULL,
  `id_skema` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKggrb3aph8hkivjsnef0e9crh9` (`id_jadwal`),
  KEY `FKqhv31bgk0vddstdbptsvn1gma` (`id_skema`),
  CONSTRAINT `FKggrb3aph8hkivjsnef0e9crh9` FOREIGN KEY (`id_jadwal`) REFERENCES `jadwal_ujisertifikasi` (`id`),
  CONSTRAINT `FKqhv31bgk0vddstdbptsvn1gma` FOREIGN KEY (`id_skema`) REFERENCES `skema` (`id_skema`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.persyaratan_skema
CREATE TABLE IF NOT EXISTS `persyaratan_skema` (
  `id_persyaratan_skema` bigint NOT NULL AUTO_INCREMENT,
  `deskripsi` text COLLATE latin1_bin,
  `id_skema` bigint DEFAULT NULL,
  PRIMARY KEY (`id_persyaratan_skema`),
  KEY `FKkpcg63cduobex93g9pv89nhrq` (`id_skema`),
  CONSTRAINT `FKkpcg63cduobex93g9pv89nhrq` FOREIGN KEY (`id_skema`) REFERENCES `skema` (`id_skema`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.roles
CREATE TABLE IF NOT EXISTS `roles` (
  `id_role` bigint NOT NULL AUTO_INCREMENT,
  `nama_role` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id_role`),
  UNIQUE KEY `UK4j0qsk8hha5n9q5aqpnsir82f` (`nama_role`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.skema
CREATE TABLE IF NOT EXISTS `skema` (
  `id_skema` bigint NOT NULL AUTO_INCREMENT,
  `kode_skema` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `file_dokumen` text COLLATE latin1_bin,
  `tanggal_penetapan` date DEFAULT NULL,
  `level_skkni` int DEFAULT NULL,
  `nama_skema` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `no_skkni` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `tahun_skkni` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `id_pengajuan_skema` bigint DEFAULT NULL,
  `id_jenis_skema` bigint DEFAULT NULL,
  PRIMARY KEY (`id_skema`),
  KEY `FKgbvg2po9hlroxnvytodjf09ej` (`id_pengajuan_skema`),
  KEY `FK12avep2gj6girhtxpj2ql1pkd` (`id_jenis_skema`),
  CONSTRAINT `FK12avep2gj6girhtxpj2ql1pkd` FOREIGN KEY (`id_jenis_skema`) REFERENCES `type_skema` (`id_jenis_skema`),
  CONSTRAINT `FKgbvg2po9hlroxnvytodjf09ej` FOREIGN KEY (`id_pengajuan_skema`) REFERENCES `type_skema_pengajuan` (`id_pengajuan_skema`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.spring_session
CREATE TABLE IF NOT EXISTS `spring_session` (
  `PRIMARY_ID` char(36) COLLATE latin1_bin NOT NULL,
  `SESSION_ID` char(36) COLLATE latin1_bin NOT NULL,
  `CREATION_TIME` bigint NOT NULL,
  `LAST_ACCESS_TIME` bigint NOT NULL,
  `MAX_INACTIVE_INTERVAL` int NOT NULL,
  `EXPIRY_TIME` bigint NOT NULL,
  `PRINCIPAL_NAME` varchar(100) COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`PRIMARY_ID`),
  UNIQUE KEY `SPRING_SESSION_IX1` (`SESSION_ID`),
  KEY `SPRING_SESSION_IX2` (`EXPIRY_TIME`),
  KEY `SPRING_SESSION_IX3` (`PRINCIPAL_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.spring_session_attributes
CREATE TABLE IF NOT EXISTS `spring_session_attributes` (
  `SESSION_PRIMARY_ID` char(36) COLLATE latin1_bin NOT NULL,
  `ATTRIBUTE_NAME` varchar(200) COLLATE latin1_bin NOT NULL,
  `ATTRIBUTE_BYTES` blob NOT NULL,
  PRIMARY KEY (`SESSION_PRIMARY_ID`,`ATTRIBUTE_NAME`),
  CONSTRAINT `SPRING_SESSION_ATTRIBUTES_FK` FOREIGN KEY (`SESSION_PRIMARY_ID`) REFERENCES `spring_session` (`PRIMARY_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_asesmen_bukti_relasi
CREATE TABLE IF NOT EXISTS `tb_asesmen_bukti_relasi` (
  `asesmen_id` bigint NOT NULL,
  `portofolio_id` bigint NOT NULL,
  KEY `FKk621xx6f64g89i7s8qmhkk50k` (`portofolio_id`),
  KEY `FKpjatfkmtv3udkwpahqivdt210` (`asesmen_id`),
  CONSTRAINT `FKk621xx6f64g89i7s8qmhkk50k` FOREIGN KEY (`portofolio_id`) REFERENCES `tb_bukti_portofolio` (`id`),
  CONSTRAINT `FKpjatfkmtv3udkwpahqivdt210` FOREIGN KEY (`asesmen_id`) REFERENCES `tb_asesmen_mandiri` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_asesmen_mandiri
CREATE TABLE IF NOT EXISTS `tb_asesmen_mandiri` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `rekomendasi_asesi` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `permohonan_id` bigint DEFAULT NULL,
  `unit_elemen_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3d7gl34rqxggxevq9l4aq92ox` (`permohonan_id`),
  KEY `FKjsokkssyrx0q1dhwk9g05m6d5` (`unit_elemen_id`),
  CONSTRAINT `FK3d7gl34rqxggxevq9l4aq92ox` FOREIGN KEY (`permohonan_id`) REFERENCES `tb_permohonan_sertifikasi` (`id`),
  CONSTRAINT `FKjsokkssyrx0q1dhwk9g05m6d5` FOREIGN KEY (`unit_elemen_id`) REFERENCES `tb_unit_elemen` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_bukti_administrasi
CREATE TABLE IF NOT EXISTS `tb_bukti_administrasi` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_path` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `jenis_bukti` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `permohonan_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrxafnuml33knyk3mmpc1uxokx` (`permohonan_id`),
  CONSTRAINT `FKrxafnuml33knyk3mmpc1uxokx` FOREIGN KEY (`permohonan_id`) REFERENCES `tb_permohonan_sertifikasi` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_bukti_persyaratan
CREATE TABLE IF NOT EXISTS `tb_bukti_persyaratan` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_path` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `status` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `permohonan_id` bigint DEFAULT NULL,
  `persyaratan_skema_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKge75x1gq5sq3pq9iwhec34qk8` (`permohonan_id`),
  KEY `FKoax2wn3pgp3rgqmfl140fjg0q` (`persyaratan_skema_id`),
  CONSTRAINT `FKge75x1gq5sq3pq9iwhec34qk8` FOREIGN KEY (`permohonan_id`) REFERENCES `tb_permohonan_sertifikasi` (`id`),
  CONSTRAINT `FKoax2wn3pgp3rgqmfl140fjg0q` FOREIGN KEY (`persyaratan_skema_id`) REFERENCES `persyaratan_skema` (`id_persyaratan_skema`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_bukti_portofolio
CREATE TABLE IF NOT EXISTS `tb_bukti_portofolio` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_path` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `nama_dokumen` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `permohonan_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6hvkkinet8v4tj05w4opb2emi` (`permohonan_id`),
  CONSTRAINT `FK6hvkkinet8v4tj05w4opb2emi` FOREIGN KEY (`permohonan_id`) REFERENCES `tb_permohonan_sertifikasi` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_kuk_skema
CREATE TABLE IF NOT EXISTS `tb_kuk_skema` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nama_kuk` text COLLATE latin1_bin,
  `id_elemen` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs7hnepmijf6f4hl3fdr9ofmk1` (`id_elemen`),
  CONSTRAINT `FKs7hnepmijf6f4hl3fdr9ofmk1` FOREIGN KEY (`id_elemen`) REFERENCES `tb_unit_elemen` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_notification
CREATE TABLE IF NOT EXISTS `tb_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dibuat` datetime(6) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `deskripsi` varchar(255) COLLATE latin1_bin NOT NULL,
  `link` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `judul` varchar(255) COLLATE latin1_bin NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs69itqf43ptalbocpylwetaar` (`user_id`),
  CONSTRAINT `FKs69itqf43ptalbocpylwetaar` FOREIGN KEY (`user_id`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_pemberi_anggaran
CREATE TABLE IF NOT EXISTS `tb_pemberi_anggaran` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nama_instansi` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=161 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_permohonan_sertifikasi
CREATE TABLE IF NOT EXISTS `tb_permohonan_sertifikasi` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status_permohonan` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `tanggal_permohonan` datetime(6) DEFAULT NULL,
  `tujuan_asesmen` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `jadwal_id` bigint DEFAULT NULL,
  `pemberi_anggaran_id` bigint DEFAULT NULL,
  `skema_id` bigint DEFAULT NULL,
  `sumber_anggaran_id` bigint DEFAULT NULL,
  `catatan_revisi` text COLLATE latin1_bin,
  `asesor_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5nxv5v616e5q8inksxv7hhnvy` (`user_id`),
  KEY `FK8xnvky65ox7y4adpfb27k63h3` (`jadwal_id`),
  KEY `FK3r1vu3pdotbege63cwf27u3lm` (`pemberi_anggaran_id`),
  KEY `FKbdh3qfa65pw3glxpcqls9e5k3` (`skema_id`),
  KEY `FK8mvhb7hr504uy7dxpefsd64ks` (`sumber_anggaran_id`),
  KEY `FKna5ql1e9lsc1usfrgmethffh0` (`asesor_id`),
  CONSTRAINT `FK3r1vu3pdotbege63cwf27u3lm` FOREIGN KEY (`pemberi_anggaran_id`) REFERENCES `tb_pemberi_anggaran` (`id`),
  CONSTRAINT `FK5nxv5v616e5q8inksxv7hhnvy` FOREIGN KEY (`user_id`) REFERENCES `users` (`id_user`),
  CONSTRAINT `FK8mvhb7hr504uy7dxpefsd64ks` FOREIGN KEY (`sumber_anggaran_id`) REFERENCES `tb_sumber_anggaran` (`id`),
  CONSTRAINT `FK8xnvky65ox7y4adpfb27k63h3` FOREIGN KEY (`jadwal_id`) REFERENCES `jadwal_ujisertifikasi` (`id`),
  CONSTRAINT `FKbdh3qfa65pw3glxpcqls9e5k3` FOREIGN KEY (`skema_id`) REFERENCES `skema` (`id_skema`),
  CONSTRAINT `FKna5ql1e9lsc1usfrgmethffh0` FOREIGN KEY (`asesor_id`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_sumber_anggaran
CREATE TABLE IF NOT EXISTS `tb_sumber_anggaran` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `jenis_sumber_anggaran` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_surat_tugas
CREATE TABLE IF NOT EXISTS `tb_surat_tugas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bulan_romawi` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `nomor_surat` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `tahun` int DEFAULT NULL,
  `tanggal_surat` date DEFAULT NULL,
  `id_asesor` bigint DEFAULT NULL,
  `id_jadwal` bigint DEFAULT NULL,
  `id_skema` bigint DEFAULT NULL,
  `file_path` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKiwm9reweysuc9eeogwdigrp8w` (`nomor_surat`),
  KEY `FKaqplf72huaftahhn42xhljarq` (`id_asesor`),
  KEY `FKb6jua0ljo60snw979i2myoeje` (`id_jadwal`),
  KEY `FK5msr2p1eds7l1f7vs67eeqi7l` (`id_skema`),
  CONSTRAINT `FK5msr2p1eds7l1f7vs67eeqi7l` FOREIGN KEY (`id_skema`) REFERENCES `skema` (`id_skema`),
  CONSTRAINT `FKaqplf72huaftahhn42xhljarq` FOREIGN KEY (`id_asesor`) REFERENCES `users` (`id_user`),
  CONSTRAINT `FKb6jua0ljo60snw979i2myoeje` FOREIGN KEY (`id_jadwal`) REFERENCES `jadwal_ujisertifikasi` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tb_unit_elemen
CREATE TABLE IF NOT EXISTS `tb_unit_elemen` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nama_elemen` text COLLATE latin1_bin,
  `no_elemen` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `id_unit_skema` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs4jcopai7dc5y4dfjaunnyfug` (`id_unit_skema`),
  CONSTRAINT `FKs4jcopai7dc5y4dfjaunnyfug` FOREIGN KEY (`id_unit_skema`) REFERENCES `unit_skema` (`id_unit_skema`)
) ENGINE=InnoDB AUTO_INCREMENT=194 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.token_reset_password
CREATE TABLE IF NOT EXISTS `token_reset_password` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) DEFAULT NULL,
  `token_resetpassword` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt8m99d83rc0n18v8yqgvgi649` (`user_id`),
  CONSTRAINT `FK2fipv3ct5o0fnl4ijti9jdbf2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id_user`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.tuk
CREATE TABLE IF NOT EXISTS `tuk` (
  `id_tuk` bigint NOT NULL AUTO_INCREMENT,
  `alamat` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `id_kota` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `kode_tuk` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `email` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `nama_tuk` varchar(255) COLLATE latin1_bin NOT NULL,
  `no_telepon` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `id_provinsi` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `id_jenis_tuk` bigint DEFAULT NULL,
  PRIMARY KEY (`id_tuk`),
  UNIQUE KEY `UKp0ay50xd5u9cl88wb3p0nm42o` (`kode_tuk`),
  KEY `FKhpwi93rb84gokq2p7qapqwmh7` (`id_jenis_tuk`),
  CONSTRAINT `FKhpwi93rb84gokq2p7qapqwmh7` FOREIGN KEY (`id_jenis_tuk`) REFERENCES `type_tuk` (`id_jenis_tuk`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.type_pekerjaan
CREATE TABLE IF NOT EXISTS `type_pekerjaan` (
  `id_jenis_pekerjaan` bigint NOT NULL,
  `jenis_pekerjaan` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id_jenis_pekerjaan`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.type_pendidikan
CREATE TABLE IF NOT EXISTS `type_pendidikan` (
  `id_jenis_pendidikan` bigint NOT NULL,
  `jenis_pendidikan` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id_jenis_pendidikan`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.type_skema
CREATE TABLE IF NOT EXISTS `type_skema` (
  `id_jenis_skema` bigint NOT NULL AUTO_INCREMENT,
  `nama_jenis_skema` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id_jenis_skema`),
  UNIQUE KEY `UK7xtq3761hgono251etp3npqju` (`nama_jenis_skema`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.type_skema_pengajuan
CREATE TABLE IF NOT EXISTS `type_skema_pengajuan` (
  `id_pengajuan_skema` bigint NOT NULL AUTO_INCREMENT,
  `nama_pengajuan_skema` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id_pengajuan_skema`),
  UNIQUE KEY `UKg7faysbehkj8uneeou0p69xpu` (`nama_pengajuan_skema`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.type_tuk
CREATE TABLE IF NOT EXISTS `type_tuk` (
  `id_jenis_tuk` bigint NOT NULL AUTO_INCREMENT,
  `jenis_tuk` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id_jenis_tuk`),
  UNIQUE KEY `UKqntb7octtwjsmw8mljkxdky1t` (`jenis_tuk`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.unit_skema
CREATE TABLE IF NOT EXISTS `unit_skema` (
  `id_unit_skema` bigint NOT NULL AUTO_INCREMENT,
  `kode_unit` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `judul_unit` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `id_skema` bigint DEFAULT NULL,
  PRIMARY KEY (`id_unit_skema`),
  KEY `FKa7pwd5xp0s6ewuq4b61vnsn3j` (`id_skema`),
  CONSTRAINT `FKa7pwd5xp0s6ewuq4b61vnsn3j` FOREIGN KEY (`id_skema`) REFERENCES `skema` (`id_skema`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.users
CREATE TABLE IF NOT EXISTS `users` (
  `id_user` bigint NOT NULL AUTO_INCREMENT,
  `alamat` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `tempat_lahir` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `kewarganegaraan` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `id_kota` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `nama_tempat_kerja` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `id_kecamatan` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `email` varchar(255) COLLATE latin1_bin NOT NULL,
  `nama_lengkap` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `jenis_kelamin` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `nik` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `no_met` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `alamat_kantor` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `email_kantor` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `fax_kantor` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `telepon_kantor` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `password` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `no_telepon` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `jabatan` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `kode_pos` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `id_provinsi` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `tanda_tangan` longtext COLLATE latin1_bin,
  `username` varchar(255) COLLATE latin1_bin NOT NULL,
  `id_jenis_pendidikan` bigint DEFAULT NULL,
  `id_jenis_pekerjaan` bigint DEFAULT NULL,
  `avatar` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  PRIMARY KEY (`id_user`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `FKlpgrt5ov7onv29pkm49883145` (`id_jenis_pendidikan`),
  KEY `FKl5vq8fmv3511ldmv4a0at69bg` (`id_jenis_pekerjaan`),
  CONSTRAINT `FKl5vq8fmv3511ldmv4a0at69bg` FOREIGN KEY (`id_jenis_pekerjaan`) REFERENCES `type_pekerjaan` (`id_jenis_pekerjaan`),
  CONSTRAINT `FKlpgrt5ov7onv29pkm49883145` FOREIGN KEY (`id_jenis_pendidikan`) REFERENCES `type_pendidikan` (`id_jenis_pendidikan`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.user_roles
CREATE TABLE IF NOT EXISTS `user_roles` (
  `id_user` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`id_user`,`role_id`),
  KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
  CONSTRAINT `FK9ihrn1kwsu0a99doxpm7jbkdb` FOREIGN KEY (`id_user`) REFERENCES `users` (`id_user`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id_role`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
