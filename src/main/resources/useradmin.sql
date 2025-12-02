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

-- Dumping structure for table silsp_db.ref_educations
CREATE TABLE IF NOT EXISTS `ref_educations` (
  `id` bigint NOT NULL,
  `name` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.ref_job_types
CREATE TABLE IF NOT EXISTS `ref_job_types` (
  `id` bigint NOT NULL,
  `name` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.roles
CREATE TABLE IF NOT EXISTS `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE latin1_bin NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

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

-- Dumping structure for table silsp_db.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `birth_place` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `citizenship` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `city_id` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `company_name` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `district_id` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `email` varchar(255) COLLATE latin1_bin NOT NULL,
  `full_name` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `gender` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `nik` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `no_met` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `office_address` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `office_email` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `office_fax` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `office_phone` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `password` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `phone_number` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `position` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `postal_code` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `province_id` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `signature_base64` text COLLATE latin1_bin,
  `sub_district_id` varchar(255) COLLATE latin1_bin DEFAULT NULL,
  `username` varchar(255) COLLATE latin1_bin NOT NULL,
  `job_type_id` bigint DEFAULT NULL,
  `education_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`),
  KEY `FKrmwedo3mwk3lc6i8ql0rjwjy8` (`job_type_id`),
  KEY `FKtau120d5gs94q9aaf5c2p0gsv` (`education_id`),
  CONSTRAINT `FKrmwedo3mwk3lc6i8ql0rjwjy8` FOREIGN KEY (`job_type_id`) REFERENCES `ref_job_types` (`id`),
  CONSTRAINT `FKtau120d5gs94q9aaf5c2p0gsv` FOREIGN KEY (`education_id`) REFERENCES `ref_educations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

-- Dumping structure for table silsp_db.user_roles
CREATE TABLE IF NOT EXISTS `user_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

-- Data exporting was unselected.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
