-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.5.8-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for vertxlib
DROP DATABASE IF EXISTS `vertxlib`;
CREATE DATABASE IF NOT EXISTS `vertxlib` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `vertxlib`;

-- Dumping structure for table vertxlib.books
DROP TABLE IF EXISTS `books`;
CREATE TABLE IF NOT EXISTS `books` (
  `bookId` int(11) NOT NULL AUTO_INCREMENT,
  `bookName` varchar(50) NOT NULL,
  PRIMARY KEY (`bookId`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- Dumping data for table vertxlib.books: ~12 rows (approximately)
DELETE FROM `books`;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` (`bookId`, `bookName`) VALUES
	(1, 'City of Celeste'),
	(2, 'Rogue\'s Legacy'),
	(3, 'Flies of Steel'),
	(4, 'War of the Rose'),
	(5, 'Trap the Spectre'),
	(6, 'Cheat the Night'),
	(7, 'Before the Storm'),
	(8, 'Scars of Truth'),
	(9, 'Black Silk'),
	(10, 'The Holiday Bride');
/*!40000 ALTER TABLE `books` ENABLE KEYS */;

-- Dumping structure for table vertxlib.lending_log
DROP TABLE IF EXISTS `lending_log`;
CREATE TABLE IF NOT EXISTS `lending_log` (
  `bookId` int(11) NOT NULL,
  `borrowerId` int(11) NOT NULL,
  KEY `FK_lending_log_books` (`bookId`),
  KEY `FK_lending_log_people` (`borrowerId`),
  CONSTRAINT `FK_lending_log_books` FOREIGN KEY (`bookId`) REFERENCES `books` (`bookId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_lending_log_people` FOREIGN KEY (`borrowerId`) REFERENCES `people` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table vertxlib.lending_log: ~0 rows (approximately)
DELETE FROM `lending_log`;
/*!40000 ALTER TABLE `lending_log` DISABLE KEYS */;
INSERT INTO `lending_log` (`bookId`, `borrowerId`) VALUES
	(4, 4),
	(7, 6),
	(1, 5),
	(10, 5),
	(2, 4),
	(3, 3),
	(5, 6),
	(6, 4),
	(8, 5),
	(9, 6),
	(4, 3),
	(6, 1);
/*!40000 ALTER TABLE `lending_log` ENABLE KEYS */;

-- Dumping structure for table vertxlib.people
DROP TABLE IF EXISTS `people`;
CREATE TABLE IF NOT EXISTS `people` (
  `firstName` varchar(50) NOT NULL,
  `lastName` varchar(50) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rights` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- Dumping data for table vertxlib.people: ~6 rows (approximately)
DELETE FROM `people`;
/*!40000 ALTER TABLE `people` DISABLE KEYS */;
INSERT INTO `people` (`firstName`, `lastName`, `id`, `rights`) VALUES
	('Bill', 'Papadas', 1, 'admin'),
	('Marcel', 'Jacobs', 2, 'employee'),
	('Kye', 'Vickers', 3, 'employee'),
	('Clarice', 'Calderon', 4, 'borrower'),
	('Jimmie', 'Carlson', 5, 'borrower'),
	('Stacie', 'Blankenship', 6, 'borrower');
/*!40000 ALTER TABLE `people` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
