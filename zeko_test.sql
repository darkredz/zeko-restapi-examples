# ************************************************************
# Sequel Pro SQL dump
# Version 5438
#
# https://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 8.0.15)
# Database: zeko_test
# Generation Time: 2020-04-28 20:10:44 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table address
# ------------------------------------------------------------

DROP TABLE IF EXISTS `address`;

CREATE TABLE `address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `street1` varchar(45) DEFAULT NULL,
  `street2` varchar(45) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_address_user_idx` (`user_id`),
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;

INSERT INTO `address` (`id`, `street1`, `street2`, `user_id`)
VALUES
	(1,'Jalan 123','Taman Tun',1),
	(2,'Jalan Gembira','Taman OUG',2),
	(3,'Jalan Bunga','Taman Negara',1);

/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(45) DEFAULT NULL,
  `type` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;

INSERT INTO `role` (`id`, `role_name`, `type`)
VALUES
	(1,'admin',1),
	(2,'super admin',1),
	(3,'normal user',2),
	(4,'moderator',3),
	(5,'super moderator',3);

/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `last_name` varchar(45) NOT NULL,
  `password` varchar(155) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `age` tinyint(4) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `notified` tinyint(1) DEFAULT NULL,
  `status` tinyint(3) DEFAULT '0',
  `created_at` datetime DEFAULT NULL,
  `last_access_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_email_UN` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;

INSERT INTO `user` (`id`, `first_name`, `last_name`, `password`, `age`, `dob`, `email`, `notified`, `status`, `created_at`, `last_access_at`)
VALUES
	(1,'John','Smith','58:5b424064373664303462:a5c270929ed0fbf3cf520f6465136274c161190c4a527a50be5e0114ed9d06beee6d3ebd0a135daa079fb974c63a3ac121a3bc6d3e3cde32de6a3a291f4f7ac8',31,'1987-01-23','asd@gmail.com',1,1,'2020-04-23 12:23:34','2020-04-28 15:04:50'),
	(2,'O\'Connor','John','58:5b424064373664303462:a5c270929ed0fbf3cf520f6465136274c161190c4a527a50be5e0114ed9d06beee6d3ebd0a135daa079fb974c63a3ac121a3bc6d3e3cde32de6a3a291f4f7ac8',22,'1990-04-30','lon@email.com',0,1,'2020-04-24 18:44:12','2020-04-24 21:34:03'),
	(3,'Joey','Tan','58:5b424064373664303462:a5c270929ed0fbf3cf520f6465136274c161190c4a527a50be5e0114ed9d06beee6d3ebd0a135daa079fb974c63a3ac121a3bc6d3e3cde32de6a3a291f4f7ac8',43,'2000-12-11','tutu@gmail.com',0,0,'2020-04-24 21:04:58','2020-04-24 21:54:38'),
	(4,'Sheng Hong','Leng','58:5b42403162616562626661:5a7529214ea254ca2c948c8d68d5d9664d7d2f6bdd085bc75d6f1e02baf6f83ca275cfe74b65b3313edc7e302a6eaf535bd6284108e7003b1e9f9c622da06f87',NULL,'1987-01-23','darkredz4@gmail.com',NULL,1,'2020-04-28 18:58:22','2020-04-28 18:58:22');

/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table user_has_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_has_role`;

CREATE TABLE `user_has_role` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `fk_user_has_role_role1_idx` (`role_id`),
  KEY `fk_user_has_role_user1_idx` (`user_id`),
  CONSTRAINT `fk_user_has_role_role1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  CONSTRAINT `fk_user_has_role_user1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `user_has_role` WRITE;
/*!40000 ALTER TABLE `user_has_role` DISABLE KEYS */;

INSERT INTO `user_has_role` (`user_id`, `role_id`)
VALUES
	(2,1),
	(1,2),
	(2,3),
	(1,4),
	(2,5);

/*!40000 ALTER TABLE `user_has_role` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
