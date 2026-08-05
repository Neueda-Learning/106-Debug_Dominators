-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: payment_db
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `audit_id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` bigint DEFAULT NULL,
  `entity_name` varchar(50) NOT NULL,
  `entity_id` bigint NOT NULL,
  `action_type` enum('CREATE','UPDATE','DELETE','PAYMENT','REFUND','CRYPTO','CAMPAIGN','CONTRIBUTION','LOGIN','LOGOUT') NOT NULL,
  `performed_by` enum('SYSTEM','ADMIN','CUSTOMER','PAYMENT_GATEWAY') NOT NULL,
  `action_description` varchar(255) NOT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `action_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `request_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`audit_id`),
  KEY `fk_audit_payment` (`payment_id`),
  CONSTRAINT `fk_audit_payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
INSERT INTO `audit_log` VALUES (1,1,'PAYMENT',1,'PAYMENT','CUSTOMER','Payment initiated by customer','192.168.1.10','2026-08-04 06:14:19',NULL),(2,1,'PAYMENT',1,'UPDATE','SYSTEM','Payment validated successfully','192.168.1.10','2026-08-04 06:14:19',NULL),(3,1,'PAYMENT',1,'UPDATE','PAYMENT_GATEWAY','Payment completed successfully','192.168.1.10','2026-08-04 06:14:19',NULL),(4,4,'PAYMENT',4,'PAYMENT','CUSTOMER','Hospital payment initiated','192.168.1.11','2026-08-04 06:14:19',NULL),(5,4,'PAYMENT',4,'UPDATE','PAYMENT_GATEWAY','Payment failed due to insufficient balance','192.168.1.11','2026-08-04 06:14:19',NULL),(6,5,'PAYMENT',5,'PAYMENT','CUSTOMER','International payment initiated','192.168.1.12','2026-08-04 06:14:19',NULL),(7,10,'CRYPTO_PAYMENT',10,'CRYPTO','SYSTEM','Bitcoin transaction submitted','192.168.1.13','2026-08-04 06:14:19',NULL),(8,10,'CRYPTO_PAYMENT',10,'UPDATE','PAYMENT_GATEWAY','Blockchain confirmation received','192.168.1.13','2026-08-04 06:14:19',NULL),(9,11,'REFUND',11,'REFUND','ADMIN','Refund approved','192.168.1.14','2026-08-04 06:14:19',NULL),(10,11,'REFUND',11,'UPDATE','SYSTEM','Refund completed successfully','192.168.1.14','2026-08-04 06:14:19',NULL),(11,21,'CONTRIBUTION',21,'CONTRIBUTION','CUSTOMER','NGO donation completed','192.168.1.15','2026-08-04 06:14:19',NULL),(12,24,'CONTRIBUTION',24,'CONTRIBUTION','CUSTOMER','Medical crowdfunding contribution','192.168.1.16','2026-08-04 06:14:19',NULL),(13,27,'CRYPTO_PAYMENT',27,'CRYPTO','SYSTEM','USDT investment completed','192.168.1.17','2026-08-04 06:14:19',NULL),(14,28,'REFUND',28,'REFUND','ADMIN','Duplicate payment refunded','192.168.1.18','2026-08-04 06:14:19',NULL),(15,34,'PAYMENT',34,'PAYMENT','CUSTOMER','SIP investment completed','192.168.1.19','2026-08-04 06:14:19',NULL),(16,39,'REFUND',39,'UPDATE','ADMIN','Flight cancellation refund processed','192.168.1.20','2026-08-04 06:14:19',NULL),(17,42,'PAYMENT',42,'UPDATE','PAYMENT_GATEWAY','International payment under verification','192.168.1.21','2026-08-04 06:14:19',NULL),(18,45,'CRYPTO_PAYMENT',45,'CRYPTO','SYSTEM','Bitcoin wallet recharge completed','192.168.1.22','2026-08-04 06:14:19',NULL),(19,46,'REFUND',46,'UPDATE','ADMIN','Crowdfunding donation refunded','192.168.1.23','2026-08-04 06:14:19',NULL),(20,50,'PAYMENT',50,'PAYMENT','CUSTOMER','Hotel reservation payment completed','192.168.1.24','2026-08-04 06:14:19',NULL);
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campaign`
--

DROP TABLE IF EXISTS `campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campaign` (
  `campaign_id` bigint NOT NULL AUTO_INCREMENT,
  `campaign_code` varchar(30) NOT NULL,
  `campaign_title` varchar(150) NOT NULL,
  `organizer_name` varchar(100) NOT NULL,
  `category` enum('MEDICAL','EDUCATION','DISASTER_RELIEF','ANIMAL_WELFARE','SOCIAL_CAUSE') NOT NULL,
  `goal_amount` decimal(15,2) NOT NULL,
  `collected_amount` decimal(15,2) DEFAULT '0.00',
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `campaign_status` enum('ACTIVE','COMPLETED','CANCELLED') NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(100) DEFAULT 'ADMIN',
  PRIMARY KEY (`campaign_id`),
  UNIQUE KEY `campaign_code` (`campaign_code`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaign`
--

LOCK TABLES `campaign` WRITE;
/*!40000 ALTER TABLE `campaign` DISABLE KEYS */;
INSERT INTO `campaign` VALUES (1,'CMP001','Cancer Treatment Fund','Helping Hands Foundation','MEDICAL',1000000.00,425000.00,'2026-01-01','2026-12-31','ACTIVE','Fundraising for cancer treatment.','2026-08-04 06:04:16','ADMIN'),(2,'CMP002','Education For Rural Children','Bright Future NGO','EDUCATION',500000.00,310000.00,'2026-02-01','2026-11-30','ACTIVE','Scholarships for underprivileged children.','2026-08-04 06:04:16','ADMIN'),(3,'CMP003','Flood Relief Kerala','Relief India','DISASTER_RELIEF',2000000.00,1750000.00,'2026-03-01','2026-09-30','ACTIVE','Emergency flood relief support.','2026-08-04 06:04:16','ADMIN'),(4,'CMP004','Animal Shelter Expansion','Care For Animals','ANIMAL_WELFARE',300000.00,120000.00,'2026-01-15','2026-08-30','ACTIVE','Expansion of rescue shelter.','2026-08-04 06:04:16','ADMIN'),(5,'CMP005','Village Water Project','Hope Foundation','SOCIAL_CAUSE',700000.00,690000.00,'2026-01-10','2026-07-31','COMPLETED','Providing clean drinking water.','2026-08-04 06:04:16','ADMIN'),(6,'CMP006','Heart Surgery Support','Life Care Trust','MEDICAL',800000.00,800000.00,'2026-02-15','2026-06-30','COMPLETED','Heart surgery funding.','2026-08-04 06:04:16','ADMIN'),(7,'CMP007','School Digital Library','EduTech Society','EDUCATION',450000.00,150000.00,'2026-04-01','2026-10-31','ACTIVE','Digital learning resources.','2026-08-04 06:04:16','ADMIN'),(8,'CMP008','Earthquake Relief Nepal','Global Relief','DISASTER_RELIEF',2500000.00,900000.00,'2026-03-15','2026-12-31','ACTIVE','Relief operations for earthquake victims.','2026-08-04 06:04:16','ADMIN'),(9,'CMP009','Street Animal Rescue','Animal First','ANIMAL_WELFARE',350000.00,50000.00,'2026-05-01','2026-12-31','ACTIVE','Rescue and rehabilitation.','2026-08-04 06:04:16','ADMIN'),(10,'CMP010','Women Skill Development','Empower Foundation','SOCIAL_CAUSE',600000.00,250000.00,'2026-01-20','2026-09-30','ACTIVE','Vocational training programs.','2026-08-04 06:04:16','ADMIN');
/*!40000 ALTER TABLE `campaign` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contribution`
--

DROP TABLE IF EXISTS `contribution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contribution` (
  `contribution_id` bigint NOT NULL AUTO_INCREMENT,
  `campaign_id` bigint NOT NULL,
  `payment_id` bigint NOT NULL,
  `contributor_name` varchar(100) NOT NULL,
  `contributor_email` varchar(100) DEFAULT NULL,
  `contribution_amount` decimal(15,2) NOT NULL,
  `contribution_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `contribution_status` enum('PENDING','SUCCESS','FAILED','REFUNDED') NOT NULL,
  `anonymous_donation` tinyint(1) DEFAULT '0',
  `message` varchar(255) DEFAULT NULL,
  `receipt_number` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`contribution_id`),
  UNIQUE KEY `payment_id` (`payment_id`),
  UNIQUE KEY `receipt_number` (`receipt_number`),
  KEY `fk_contribution_campaign` (`campaign_id`),
  CONSTRAINT `fk_contribution_campaign` FOREIGN KEY (`campaign_id`) REFERENCES `campaign` (`campaign_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_contribution_payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contribution`
--

LOCK TABLES `contribution` WRITE;
/*!40000 ALTER TABLE `contribution` DISABLE KEYS */;
INSERT INTO `contribution` VALUES (1,1,21,'Rahul Sharma','rahul@gmail.com',5000.00,'2026-08-04 06:08:21','SUCCESS',0,'Wishing for a speedy recovery.',NULL),(2,1,24,'Priya Verma','priya@gmail.com',3500.00,'2026-08-04 06:08:21','SUCCESS',0,'Stay strong.',NULL),(3,2,36,'Amit Singh','amit@gmail.com',3100.00,'2026-08-04 06:08:21','SUCCESS',0,'Education changes lives.',NULL),(4,3,46,'Sneha Kapoor','sneha@gmail.com',3200.00,'2026-08-04 06:08:21','REFUNDED',0,'Hope everything gets better.',NULL),(5,4,17,'Rohit Mehta','rohit@gmail.com',5200.00,'2026-08-04 06:08:21','SUCCESS',1,NULL,NULL),(6,5,34,'Anjali Gupta','anjali@gmail.com',55000.00,'2026-08-04 06:08:21','SUCCESS',0,'Happy to contribute.',NULL),(7,6,19,'Karan Arora','karan@gmail.com',120000.00,'2026-08-04 06:08:21','SUCCESS',0,'Best wishes.',NULL),(8,7,13,'Neha Bansal','neha@gmail.com',45000.00,'2026-08-04 06:08:21','SUCCESS',0,'Supporting education.',NULL),(9,8,42,'John Smith','john@gmail.com',98000.00,'2026-08-04 06:08:21','PENDING',0,'International donation.',NULL),(10,9,45,'David Lee','david@gmail.com',150000.00,'2026-08-04 06:08:21','SUCCESS',1,NULL,NULL),(11,10,48,'Emily Clark','emily@gmail.com',25000.00,'2026-08-04 06:08:21','SUCCESS',0,'Skill development matters.',NULL),(12,3,29,'Vikas Jain','vikas@gmail.com',6500.00,'2026-08-04 06:08:21','SUCCESS',0,'Helping flood victims.',NULL),(13,2,25,'Pooja Malhotra','pooja@gmail.com',12000.00,'2026-08-04 06:08:21','SUCCESS',0,'Every child deserves education.',NULL),(14,1,11,'Nitin Khanna','nitin@gmail.com',2400.00,'2026-08-04 06:08:21','REFUNDED',0,'Duplicate contribution refunded.',NULL),(15,8,31,'Sophia Brown','sophia@gmail.com',9200.00,'2026-08-04 06:08:21','PENDING',0,'Hope this helps.',NULL);
/*!40000 ALTER TABLE `contribution` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `country_master`
--

DROP TABLE IF EXISTS `country_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `country_master` (
  `country_code` varchar(10) NOT NULL,
  `country_name` varchar(50) DEFAULT NULL,
  `currency_code` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`country_code`),
  KEY `currency_code` (`currency_code`),
  CONSTRAINT `country_master_ibfk_1` FOREIGN KEY (`currency_code`) REFERENCES `currency_master` (`currency_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country_master`
--

LOCK TABLES `country_master` WRITE;
/*!40000 ALTER TABLE `country_master` DISABLE KEYS */;
INSERT INTO `country_master` VALUES ('AE','United Arab Emirates','AED'),('DE','Germany','EUR'),('FR','France','EUR'),('GB','United Kingdom','GBP'),('IN','India','INR'),('SG','Singapore','SGD'),('US','United States','USD');
/*!40000 ALTER TABLE `country_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `crypto_payment`
--

DROP TABLE IF EXISTS `crypto_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `crypto_payment` (
  `crypto_id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` bigint NOT NULL,
  `crypto_currency` enum('BTC','ETH','USDT','BNB','SOL') NOT NULL,
  `wallet_address` varchar(150) NOT NULL,
  `transaction_hash` varchar(120) NOT NULL,
  `blockchain_network` varchar(50) NOT NULL,
  `exchange_rate` decimal(15,4) NOT NULL,
  `crypto_amount` decimal(18,8) NOT NULL,
  `network_fee` decimal(18,8) DEFAULT '0.00000000',
  `confirmation_status` enum('PENDING','CONFIRMED','FAILED') NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `exchange_rate_id` bigint DEFAULT NULL,
  PRIMARY KEY (`crypto_id`),
  UNIQUE KEY `transaction_hash` (`transaction_hash`),
  KEY `fk_crypto_payment` (`payment_id`),
  CONSTRAINT `fk_crypto_payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `crypto_payment`
--

LOCK TABLES `crypto_payment` WRITE;
/*!40000 ALTER TABLE `crypto_payment` DISABLE KEYS */;
INSERT INTO `crypto_payment` VALUES (1,10,'BTC','bc1q4d8n7p9k2m5x6z8a1b3c5d7e9f1g2h3j4k5','BTC_TXN_100001','Bitcoin',84500.2500,0.11600000,0.00015000,'CONFIRMED','2026-08-04 06:03:08',NULL),(2,18,'ETH','0x4bA89c21dEF2A6fE45B6d8C3A987654321ABCDE1','ETH_TXN_100002','Ethereum',3400.5500,2.61720000,0.00300000,'PENDING','2026-08-04 06:03:08',NULL),(3,27,'USDT','TQ8Lk7JH9Yw2nM4aBc8PqRsT123456789ABCDE','USDT_TXN_100003','TRON',1.0000,150000.00000000,1.00000000,'CONFIRMED','2026-08-04 06:03:08',NULL),(4,37,'ETH','0x89ABcD456Ef1234567890ABcdEf987654321ABCD','ETH_TXN_100004','Ethereum',3525.7800,12.76340000,0.00450000,'PENDING','2026-08-04 06:03:08',NULL),(5,45,'BTC','bc1q8m6x4v2z9y7n5p3r1t8k6l4h2g9f5d3s1a8','BTC_TXN_100005','Bitcoin',86020.4800,1.74370000,0.00025000,'CONFIRMED','2026-08-04 06:03:08',NULL);
/*!40000 ALTER TABLE `crypto_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `currency_master`
--

DROP TABLE IF EXISTS `currency_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `currency_master` (
  `currency_code` varchar(10) NOT NULL,
  `currency_name` varchar(50) NOT NULL,
  `symbol` varchar(10) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `is_crypto` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`currency_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `currency_master`
--

LOCK TABLES `currency_master` WRITE;
/*!40000 ALTER TABLE `currency_master` DISABLE KEYS */;
INSERT INTO `currency_master` VALUES ('AED','UAE Dirham','AED','UAE',0),('BTC','Bitcoin','?','Global',1),('ETH','Ethereum','?','Global',1),('EUR','Euro','?','European Union',0),('GBP','British Pound','£','United Kingdom',0),('INR','Indian Rupee','?','India',0),('SGD','Singapore Dollar','S$','Singapore',0),('USD','US Dollar','$','USA',0),('USDT','Tether','?','Global',1);
/*!40000 ALTER TABLE `currency_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exchange_rate`
--

DROP TABLE IF EXISTS `exchange_rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exchange_rate` (
  `exchange_rate_id` bigint NOT NULL AUTO_INCREMENT,
  `base_currency` varchar(10) NOT NULL,
  `target_currency` varchar(10) NOT NULL,
  `exchange_rate` decimal(18,6) NOT NULL,
  `exchange_provider` varchar(100) NOT NULL,
  `effective_date` date NOT NULL,
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `remarks` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`exchange_rate_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exchange_rate`
--

LOCK TABLES `exchange_rate` WRITE;
/*!40000 ALTER TABLE `exchange_rate` DISABLE KEYS */;
INSERT INTO `exchange_rate` VALUES (1,'USD','INR',83.250000,'Reserve Bank','2026-08-01','2026-08-04 06:10:39','Daily exchange rate',1),(2,'EUR','INR',96.750000,'Reserve Bank','2026-08-01','2026-08-04 06:10:39','Daily exchange rate',1),(3,'GBP','INR',111.400000,'Reserve Bank','2026-08-01','2026-08-04 06:10:39','Daily exchange rate',1),(4,'AED','INR',22.650000,'Reserve Bank','2026-08-01','2026-08-04 06:10:39','Daily exchange rate',1),(5,'SGD','INR',65.900000,'Reserve Bank','2026-08-01','2026-08-04 06:10:39','Daily exchange rate',1),(6,'JPY','INR',0.570000,'Reserve Bank','2026-08-01','2026-08-04 06:10:39','Daily exchange rate',1),(7,'BTC','USD',84500.250000,'CoinMarketCap','2026-08-01','2026-08-04 06:10:39','Bitcoin exchange rate',1),(8,'ETH','USD',3400.550000,'CoinMarketCap','2026-08-01','2026-08-04 06:10:39','Ethereum exchange rate',1),(9,'USDT','USD',1.000000,'CoinMarketCap','2026-08-01','2026-08-04 06:10:39','Stable coin',1),(10,'BNB','USD',720.340000,'CoinMarketCap','2026-08-01','2026-08-04 06:10:39','BNB exchange rate',1),(11,'SOL','USD',182.750000,'CoinMarketCap','2026-08-01','2026-08-04 06:10:39','Solana exchange rate',1),(12,'USD','EUR',0.860000,'European Central Bank','2026-08-01','2026-08-04 06:10:39','Forex rate',1),(13,'USD','GBP',0.750000,'Bank of England','2026-08-01','2026-08-04 06:10:39','Forex rate',1),(14,'EUR','USD',1.160000,'European Central Bank','2026-08-01','2026-08-04 06:10:39','Forex rate',1),(15,'INR','USD',0.012000,'Reserve Bank','2026-08-01','2026-08-04 06:10:39','Forex rate',1);
/*!40000 ALTER TABLE `exchange_rate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `notification_id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` bigint DEFAULT NULL,
  `notification_type` enum('PAYMENT_SUCCESS','PAYMENT_FAILED','PAYMENT_PROCESSING','REFUND_SUCCESS','REFUND_REQUESTED','CRYPTO_CONFIRMATION','CAMPAIGN_DONATION','SYSTEM_ALERT') NOT NULL,
  `notification_title` varchar(100) NOT NULL,
  `notification_message` varchar(255) NOT NULL,
  `delivery_channel` enum('EMAIL','SMS','IN_APP') NOT NULL,
  `notification_status` enum('PENDING','SENT','FAILED') NOT NULL,
  `sent_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_read` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`notification_id`),
  KEY `fk_notification_payment` (`payment_id`),
  CONSTRAINT `fk_notification_payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (1,1,'PAYMENT_SUCCESS','Payment Successful','Your electricity bill payment was successful.','EMAIL','SENT','2026-08-04 06:12:20','2026-08-04 06:12:20',0),(2,4,'PAYMENT_FAILED','Payment Failed','Hospital payment could not be processed.','EMAIL','SENT','2026-08-04 06:12:20','2026-08-04 06:12:20',0),(3,5,'PAYMENT_PROCESSING','Payment Processing','International payment is under verification.','EMAIL','PENDING',NULL,'2026-08-04 06:12:20',0),(4,10,'CRYPTO_CONFIRMATION','Crypto Transaction Confirmed','Bitcoin payment confirmed successfully.','EMAIL','SENT','2026-08-04 06:12:20','2026-08-04 06:12:20',0),(5,11,'REFUND_SUCCESS','Refund Completed','Refund has been credited successfully.','EMAIL','SENT','2026-08-04 06:12:20','2026-08-04 06:12:20',0),(6,21,'CAMPAIGN_DONATION','Donation Successful','Thank you for supporting the NGO campaign.','EMAIL','SENT','2026-08-04 06:12:20','2026-08-04 06:12:20',0),(7,28,'REFUND_SUCCESS','Refund Processed','Your grocery payment has been refunded.','SMS','SENT','2026-08-04 06:12:20','2026-08-04 06:12:20',0),(8,39,'REFUND_SUCCESS','Flight Refund Completed','Refund for cancelled flight has been processed.','EMAIL','SENT','2026-08-04 06:12:20','2026-08-04 06:12:20',0),(9,42,'PAYMENT_PROCESSING','International Payment','Awaiting overseas bank confirmation.','EMAIL','PENDING',NULL,'2026-08-04 06:12:20',0),(10,45,'CRYPTO_CONFIRMATION','Bitcoin Wallet Recharge','Blockchain confirmation received.','EMAIL','SENT','2026-08-04 06:12:20','2026-08-04 06:12:20',0),(11,46,'REFUND_SUCCESS','Donation Refund','Duplicate donation refunded successfully.','EMAIL','SENT','2026-08-04 06:12:20','2026-08-04 06:12:20',0),(12,50,'PAYMENT_SUCCESS','Hotel Booking Successful','Your hotel booking payment was successful.','SMS','SENT','2026-08-04 06:12:20','2026-08-04 06:12:20',0);
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` varchar(50) NOT NULL,
  `reference_number` varchar(100) NOT NULL,
  `source_account` varchar(30) NOT NULL,
  `destination_account` varchar(30) NOT NULL,
  `amount` decimal(15,2) NOT NULL,
  `currency` varchar(10) NOT NULL,
  `payment_method` enum('UPI','CREDIT_CARD','DEBIT_CARD','NET_BANKING','WALLET','CRYPTO') NOT NULL,
  `source_country` varchar(50) NOT NULL,
  `destination_country` varchar(50) NOT NULL,
  `idempotency_key` varchar(100) DEFAULT NULL,
  `retry_count` int DEFAULT '0',
  `status` enum('CREATED','VALIDATED','PROCESSING','COMPLETED','FAILED','REFUNDED','CANCELLED') NOT NULL,
  `error_code` varchar(50) DEFAULT NULL,
  `error_message` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `payment_id` (`payment_id`),
  UNIQUE KEY `reference_number` (`reference_number`),
  UNIQUE KEY `idempotency_key` (`idempotency_key`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,'PAY000001','REF000001','','',1200.00,'INR','UPI','India','India',NULL,0,'COMPLETED',NULL,NULL,'Electricity Bill','2026-08-04 05:45:14','2026-08-04 05:45:14'),(2,'PAY000002','REF000002','','',850.00,'INR','UPI','India','India',NULL,0,'COMPLETED',NULL,NULL,'Water Bill','2026-08-04 05:45:14','2026-08-04 05:45:14'),(3,'PAY000003','REF000003','','',4500.00,'INR','NET_BANKING','India','India',NULL,0,'COMPLETED',NULL,NULL,'College Tuition Fee','2026-08-04 05:45:14','2026-08-04 05:45:14'),(4,'PAY000004','REF000004','','',3200.00,'INR','DEBIT_CARD','India','India',NULL,0,'FAILED',NULL,NULL,'Hospital Consultation','2026-08-04 05:45:14','2026-08-04 05:45:14'),(5,'PAY000005','REF000005','','',15000.00,'USD','CREDIT_CARD','USA','Canada',NULL,0,'PROCESSING',NULL,NULL,'Flight Booking','2026-08-04 05:45:14','2026-08-04 05:45:14'),(6,'PAY000006','REF000006','','',7200.00,'EUR','DEBIT_CARD','Germany','France',NULL,0,'COMPLETED',NULL,NULL,'Hotel Booking','2026-08-04 05:45:14','2026-08-04 05:45:14'),(7,'PAY000007','REF000007','','',2500.00,'INR','WALLET','India','India',NULL,0,'CREATED',NULL,NULL,'Food Delivery','2026-08-04 05:45:14','2026-08-04 05:45:14'),(8,'PAY000008','REF000008','','',6000.00,'GBP','CREDIT_CARD','UK','India',NULL,0,'VALIDATED',NULL,NULL,'Shopping Purchase','2026-08-04 05:45:14','2026-08-04 05:45:14'),(9,'PAY000009','REF000009','','',950.00,'INR','UPI','India','India',NULL,0,'COMPLETED',NULL,NULL,'Mobile Recharge','2026-08-04 05:45:14','2026-08-04 05:45:14'),(10,'PAY000010','REF000010','','',9800.00,'AED','CRYPTO','UAE','India',NULL,0,'COMPLETED',NULL,NULL,'Bitcoin Purchase','2026-08-04 05:45:14','2026-08-04 05:45:14'),(11,'PAY000011','REF000011','','',2400.00,'INR','UPI','India','India',NULL,0,'REFUNDED',NULL,NULL,'Duplicate Payment','2026-08-04 05:45:14','2026-08-04 05:45:14'),(12,'PAY000012','REF000012','','',18500.00,'USD','NET_BANKING','USA','UK',NULL,0,'PROCESSING',NULL,NULL,'Business Transfer','2026-08-04 05:45:14','2026-08-04 05:45:14'),(13,'PAY000013','REF000013','','',45000.00,'INR','NET_BANKING','India','India',NULL,0,'COMPLETED',NULL,NULL,'Car Loan EMI','2026-08-04 05:45:14','2026-08-04 05:45:14'),(14,'PAY000014','REF000014','','',320.00,'INR','UPI','India','India',NULL,0,'FAILED',NULL,NULL,'Gas Bill','2026-08-04 05:45:14','2026-08-04 05:45:14'),(15,'PAY000015','REF000015','','',1450.00,'INR','DEBIT_CARD','India','India',NULL,0,'COMPLETED',NULL,NULL,'Amazon Shopping','2026-08-04 05:45:14','2026-08-04 05:45:14'),(16,'PAY000016','REF000016','','',2700.00,'SGD','CREDIT_CARD','Singapore','Australia',NULL,0,'VALIDATED',NULL,NULL,'Hotel Reservation','2026-08-04 05:45:14','2026-08-04 05:45:14'),(17,'PAY000017','REF000017','','',5200.00,'INR','WALLET','India','India',NULL,0,'COMPLETED',NULL,NULL,'Insurance Premium','2026-08-04 05:45:14','2026-08-04 05:45:14'),(18,'PAY000018','REF000018','','',8900.00,'EUR','CRYPTO','France','Germany',NULL,0,'PROCESSING',NULL,NULL,'Ethereum Purchase','2026-08-04 05:45:14','2026-08-04 05:45:14'),(19,'PAY000019','REF000019','','',120000.00,'INR','NET_BANKING','India','India',NULL,0,'COMPLETED',NULL,NULL,'Home Down Payment','2026-08-04 05:45:14','2026-08-04 05:45:14'),(20,'PAY000020','REF000020','','',1100.00,'INR','UPI','India','India',NULL,0,'COMPLETED',NULL,NULL,'Internet Broadband Bill','2026-08-04 05:45:14','2026-08-04 05:45:14'),(21,'PAY000021','REF000021','','',5000.00,'INR','UPI','India','India',NULL,0,'COMPLETED',NULL,NULL,'NGO Donation','2026-08-04 05:46:10','2026-08-04 05:46:10'),(22,'PAY000022','REF000022','','',25000.00,'USD','CREDIT_CARD','USA','USA',NULL,0,'COMPLETED',NULL,NULL,'Laptop Purchase','2026-08-04 05:46:10','2026-08-04 05:46:10'),(23,'PAY000023','REF000023','','',1750.00,'INR','WALLET','India','India',NULL,0,'FAILED',NULL,NULL,'Movie Tickets','2026-08-04 05:46:10','2026-08-04 05:46:10'),(24,'PAY000024','REF000024','','',3500.00,'INR','UPI','India','India',NULL,0,'PROCESSING',NULL,NULL,'Medical Crowdfunding Donation','2026-08-04 05:46:10','2026-08-04 05:46:10'),(25,'PAY000025','REF000025','','',12000.00,'GBP','NET_BANKING','UK','India',NULL,0,'COMPLETED',NULL,NULL,'University Fee','2026-08-04 05:46:10','2026-08-04 05:46:10'),(26,'PAY000026','REF000026','','',999.00,'INR','DEBIT_CARD','India','India',NULL,0,'VALIDATED',NULL,NULL,'Netflix Subscription','2026-08-04 05:46:10','2026-08-04 05:46:10'),(27,'PAY000027','REF000027','','',150000.00,'AED','CRYPTO','UAE','India',NULL,0,'COMPLETED',NULL,NULL,'USDT Investment','2026-08-04 05:46:10','2026-08-04 05:46:10'),(28,'PAY000028','REF000028','','',2200.00,'INR','UPI','India','India',NULL,0,'REFUNDED',NULL,NULL,'Duplicate Grocery Payment','2026-08-04 05:46:10','2026-08-04 05:46:10'),(29,'PAY000029','REF000029','','',6500.00,'EUR','CREDIT_CARD','Germany','Italy',NULL,0,'COMPLETED',NULL,NULL,'Holiday Booking','2026-08-04 05:46:10','2026-08-04 05:46:10'),(30,'PAY000030','REF000030','','',780.00,'INR','UPI','India','India',NULL,0,'CREATED',NULL,NULL,'Mobile Recharge','2026-08-04 05:46:10','2026-08-04 05:46:10'),(31,'PAY000031','REF000031','','',9200.00,'USD','NET_BANKING','USA','Canada',NULL,0,'PROCESSING',NULL,NULL,'Business Invoice','2026-08-04 05:46:10','2026-08-04 05:46:10'),(32,'PAY000032','REF000032','','',450.00,'INR','WALLET','India','India',NULL,0,'COMPLETED',NULL,NULL,'Metro Card Recharge','2026-08-04 05:46:10','2026-08-04 05:46:10'),(33,'PAY000033','REF000033','','',18500.00,'INR','DEBIT_CARD','India','India',NULL,0,'FAILED',NULL,NULL,'Hospital Admission','2026-08-04 05:46:10','2026-08-04 05:46:10'),(34,'PAY000034','REF000034','','',55000.00,'INR','NET_BANKING','India','India',NULL,0,'COMPLETED',NULL,NULL,'SIP Investment','2026-08-04 05:46:10','2026-08-04 05:46:10'),(35,'PAY000035','REF000035','','',2400.00,'SGD','CREDIT_CARD','Singapore','Australia',NULL,0,'VALIDATED',NULL,NULL,'Hotel Advance','2026-08-04 05:46:10','2026-08-04 05:46:10'),(36,'PAY000036','REF000036','','',3100.00,'INR','UPI','India','India',NULL,0,'COMPLETED',NULL,NULL,'Education Crowdfunding Donation','2026-08-04 05:46:10','2026-08-04 05:46:10'),(37,'PAY000037','REF000037','','',45000.00,'EUR','CRYPTO','France','Germany',NULL,0,'PROCESSING',NULL,NULL,'Ethereum Wallet Transfer','2026-08-04 05:46:10','2026-08-04 05:46:10'),(38,'PAY000038','REF000038','','',1800.00,'INR','UPI','India','India',NULL,0,'COMPLETED',NULL,NULL,'Electric Scooter EMI','2026-08-04 05:46:10','2026-08-04 05:46:10'),(39,'PAY000039','REF000039','','',7600.00,'USD','CREDIT_CARD','USA','UK',NULL,0,'REFUNDED',NULL,NULL,'Cancelled Flight Ticket','2026-08-04 05:46:10','2026-08-04 05:46:10'),(40,'PAY000040','REF000040','','',1350.00,'INR','DEBIT_CARD','India','India',NULL,0,'COMPLETED',NULL,NULL,'Pharmacy Purchase','2026-08-04 05:46:10','2026-08-04 05:46:10'),(41,'PAY000041','REF000041','','',2100.00,'INR','UPI','India','India',NULL,0,'COMPLETED',NULL,NULL,'Water Supply Bill','2026-08-04 05:46:49','2026-08-04 05:46:49'),(42,'PAY000042','REF000042','','',98000.00,'USD','NET_BANKING','USA','Germany',NULL,0,'PROCESSING',NULL,NULL,'International Business Payment','2026-08-04 05:46:49','2026-08-04 05:46:49'),(43,'PAY000043','REF000043','','',4500.00,'INR','DEBIT_CARD','India','India',NULL,0,'FAILED',NULL,NULL,'Medical Insurance Premium','2026-08-04 05:46:49','2026-08-04 05:46:49'),(44,'PAY000044','REF000044','','',1750.00,'INR','WALLET','India','India',NULL,0,'COMPLETED',NULL,NULL,'Online Grocery Purchase','2026-08-04 05:46:49','2026-08-04 05:46:49'),(45,'PAY000045','REF000045','','',150000.00,'AED','CRYPTO','UAE','India',NULL,0,'COMPLETED',NULL,NULL,'Bitcoin Wallet Recharge','2026-08-04 05:46:49','2026-08-04 05:46:49'),(46,'PAY000046','REF000046','','',3200.00,'INR','UPI','India','India',NULL,0,'REFUNDED',NULL,NULL,'Duplicate Crowdfunding Donation','2026-08-04 05:46:49','2026-08-04 05:46:49'),(47,'PAY000047','REF000047','','',8900.00,'GBP','CREDIT_CARD','UK','France',NULL,0,'VALIDATED',NULL,NULL,'Conference Registration Fee','2026-08-04 05:46:49','2026-08-04 05:46:49'),(48,'PAY000048','REF000048','','',25000.00,'EUR','NET_BANKING','Germany','Italy',NULL,0,'COMPLETED',NULL,NULL,'Business Equipment Purchase','2026-08-04 05:46:49','2026-08-04 05:46:49'),(49,'PAY000049','REF000049','','',750.00,'INR','UPI','India','India',NULL,0,'CREATED',NULL,NULL,'DTH Recharge','2026-08-04 05:46:49','2026-08-04 05:46:49'),(50,'PAY000050','REF000050','','',6400.00,'SGD','CREDIT_CARD','Singapore','Australia',NULL,0,'COMPLETED',NULL,NULL,'Hotel Reservation','2026-08-04 05:46:49','2026-08-04 05:46:49');
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_history`
--

DROP TABLE IF EXISTS `payment_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_history` (
  `history_id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` bigint NOT NULL,
  `old_status` enum('CREATED','VALIDATED','PROCESSING','COMPLETED','FAILED','REFUNDED','CANCELLED') NOT NULL,
  `new_status` enum('CREATED','VALIDATED','PROCESSING','COMPLETED','FAILED','REFUNDED','CANCELLED') NOT NULL,
  `event_type` varchar(50) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `changed_by` varchar(100) NOT NULL,
  `changed_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`history_id`),
  KEY `fk_payment_history` (`payment_id`),
  CONSTRAINT `fk_payment_history` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_history`
--

LOCK TABLES `payment_history` WRITE;
/*!40000 ALTER TABLE `payment_history` DISABLE KEYS */;
INSERT INTO `payment_history` VALUES (1,1,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:09'),(2,1,'VALIDATED','PROCESSING',NULL,'Payment sent to gateway','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(3,1,'PROCESSING','COMPLETED',NULL,'Transaction completed successfully','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(4,2,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:09'),(5,2,'VALIDATED','PROCESSING',NULL,'Payment sent to gateway','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(6,2,'PROCESSING','COMPLETED',NULL,'Transaction completed successfully','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(7,3,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:09'),(8,3,'VALIDATED','PROCESSING',NULL,'Payment sent to gateway','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(9,3,'PROCESSING','COMPLETED',NULL,'Transaction completed successfully','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(10,4,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:09'),(11,4,'VALIDATED','PROCESSING',NULL,'Payment sent to gateway','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(12,4,'PROCESSING','FAILED',NULL,'Insufficient account balance','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(13,5,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:09'),(14,5,'VALIDATED','PROCESSING',NULL,'International payment initiated','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(15,6,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:09'),(16,6,'VALIDATED','PROCESSING',NULL,'Payment sent to gateway','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(17,6,'PROCESSING','COMPLETED',NULL,'Transaction completed successfully','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(18,7,'CREATED','CREATED',NULL,'Payment initiated by customer','CUSTOMER','2026-08-04 05:54:09'),(19,8,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:09'),(20,9,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:09'),(21,9,'VALIDATED','PROCESSING',NULL,'Payment sent to gateway','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(22,9,'PROCESSING','COMPLETED',NULL,'Transaction completed successfully','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(23,10,'CREATED','VALIDATED',NULL,'Wallet address verified','SYSTEM','2026-08-04 05:54:09'),(24,10,'VALIDATED','PROCESSING',NULL,'Blockchain transaction initiated','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(25,10,'PROCESSING','COMPLETED',NULL,'Blockchain confirmation received','PAYMENT_GATEWAY','2026-08-04 05:54:09'),(26,11,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:47'),(27,11,'VALIDATED','PROCESSING',NULL,'Payment sent to gateway','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(28,11,'PROCESSING','COMPLETED',NULL,'Payment completed successfully','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(29,11,'COMPLETED','REFUNDED',NULL,'Duplicate payment refunded','ADMIN','2026-08-04 05:54:47'),(30,12,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:47'),(31,12,'VALIDATED','PROCESSING',NULL,'International payment under verification','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(32,13,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:47'),(33,13,'VALIDATED','PROCESSING',NULL,'EMI payment forwarded to bank','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(34,13,'PROCESSING','COMPLETED',NULL,'Loan EMI paid successfully','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(35,14,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:47'),(36,14,'VALIDATED','PROCESSING',NULL,'Gas bill payment initiated','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(37,14,'PROCESSING','FAILED',NULL,'Payment gateway timeout','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(38,15,'CREATED','VALIDATED',NULL,'Payment request validated successfully','SYSTEM','2026-08-04 05:54:47'),(39,15,'VALIDATED','PROCESSING',NULL,'Merchant accepted payment','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(40,15,'PROCESSING','COMPLETED',NULL,'Order payment completed','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(41,16,'CREATED','VALIDATED',NULL,'International card verified','SYSTEM','2026-08-04 05:54:47'),(42,17,'CREATED','VALIDATED',NULL,'Insurance premium validated','SYSTEM','2026-08-04 05:54:47'),(43,17,'VALIDATED','PROCESSING',NULL,'Payment forwarded to insurer','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(44,17,'PROCESSING','COMPLETED',NULL,'Insurance premium paid','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(45,18,'CREATED','VALIDATED',NULL,'Blockchain wallet verified','SYSTEM','2026-08-04 05:54:47'),(46,18,'VALIDATED','PROCESSING',NULL,'Awaiting blockchain confirmations','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(47,19,'CREATED','VALIDATED',NULL,'High-value transaction verified','SYSTEM','2026-08-04 05:54:47'),(48,19,'VALIDATED','PROCESSING',NULL,'Transaction sent to bank','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(49,19,'PROCESSING','COMPLETED',NULL,'High-value payment completed','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(50,20,'CREATED','VALIDATED',NULL,'Broadband bill validated','SYSTEM','2026-08-04 05:54:47'),(51,20,'VALIDATED','PROCESSING',NULL,'Bill payment initiated','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(52,20,'PROCESSING','COMPLETED',NULL,'Broadband bill paid successfully','PAYMENT_GATEWAY','2026-08-04 05:54:47'),(53,21,'CREATED','VALIDATED',NULL,'Donation request validated','SYSTEM','2026-08-04 05:55:37'),(54,21,'VALIDATED','PROCESSING',NULL,'Payment sent to NGO account','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(55,21,'PROCESSING','COMPLETED',NULL,'NGO donation completed','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(56,22,'CREATED','VALIDATED',NULL,'Credit card verified','SYSTEM','2026-08-04 05:55:37'),(57,22,'VALIDATED','PROCESSING',NULL,'Merchant authorization successful','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(58,22,'PROCESSING','COMPLETED',NULL,'Laptop purchase completed','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(59,23,'CREATED','VALIDATED',NULL,'Wallet payment validated','SYSTEM','2026-08-04 05:55:37'),(60,23,'VALIDATED','PROCESSING',NULL,'Wallet payment initiated','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(61,23,'PROCESSING','FAILED',NULL,'Wallet balance insufficient','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(62,24,'CREATED','VALIDATED',NULL,'Donation verified','SYSTEM','2026-08-04 05:55:37'),(63,24,'VALIDATED','PROCESSING',NULL,'Medical crowdfunding payment under processing','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(64,25,'CREATED','VALIDATED',NULL,'University fee validated','SYSTEM','2026-08-04 05:55:37'),(65,25,'VALIDATED','PROCESSING',NULL,'International payment initiated','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(66,25,'PROCESSING','COMPLETED',NULL,'University fee paid successfully','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(67,26,'CREATED','VALIDATED',NULL,'Subscription payment validated','SYSTEM','2026-08-04 05:55:37'),(68,27,'CREATED','VALIDATED',NULL,'Crypto wallet verified','SYSTEM','2026-08-04 05:55:37'),(69,27,'VALIDATED','PROCESSING',NULL,'Blockchain transaction submitted','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(70,27,'PROCESSING','COMPLETED',NULL,'USDT investment successful','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(71,28,'CREATED','VALIDATED',NULL,'Duplicate payment validated','SYSTEM','2026-08-04 05:55:37'),(72,28,'VALIDATED','PROCESSING',NULL,'Payment completed','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(73,28,'PROCESSING','COMPLETED',NULL,'Payment successful','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(74,28,'COMPLETED','REFUNDED',NULL,'Duplicate grocery payment refunded','ADMIN','2026-08-04 05:55:37'),(75,29,'CREATED','VALIDATED',NULL,'International card verified','SYSTEM','2026-08-04 05:55:37'),(76,29,'VALIDATED','PROCESSING',NULL,'Holiday booking initiated','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(77,29,'PROCESSING','COMPLETED',NULL,'Holiday booking completed','PAYMENT_GATEWAY','2026-08-04 05:55:37'),(78,30,'CREATED','CREATED',NULL,'Recharge request created','CUSTOMER','2026-08-04 05:55:37'),(79,31,'CREATED','VALIDATED',NULL,'Business invoice validated','SYSTEM','2026-08-04 05:56:04'),(80,31,'VALIDATED','PROCESSING',NULL,'International business payment initiated','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(81,32,'CREATED','VALIDATED',NULL,'Wallet payment validated','SYSTEM','2026-08-04 05:56:04'),(82,32,'VALIDATED','PROCESSING',NULL,'Metro recharge initiated','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(83,32,'PROCESSING','COMPLETED',NULL,'Metro recharge successful','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(84,33,'CREATED','VALIDATED',NULL,'Hospital payment validated','SYSTEM','2026-08-04 05:56:04'),(85,33,'VALIDATED','PROCESSING',NULL,'Hospital payment initiated','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(86,33,'PROCESSING','FAILED',NULL,'Card transaction declined','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(87,34,'CREATED','VALIDATED',NULL,'Investment payment validated','SYSTEM','2026-08-04 05:56:04'),(88,34,'VALIDATED','PROCESSING',NULL,'SIP investment initiated','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(89,34,'PROCESSING','COMPLETED',NULL,'SIP investment successful','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(90,35,'CREATED','VALIDATED',NULL,'International hotel booking validated','SYSTEM','2026-08-04 05:56:04'),(91,36,'CREATED','VALIDATED',NULL,'Education donation validated','SYSTEM','2026-08-04 05:56:04'),(92,36,'VALIDATED','PROCESSING',NULL,'Crowdfunding contribution initiated','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(93,36,'PROCESSING','COMPLETED',NULL,'Contribution transferred successfully','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(94,37,'CREATED','VALIDATED',NULL,'Ethereum wallet verified','SYSTEM','2026-08-04 05:56:04'),(95,37,'VALIDATED','PROCESSING',NULL,'Blockchain transaction submitted','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(96,38,'CREATED','VALIDATED',NULL,'EMI payment validated','SYSTEM','2026-08-04 05:56:04'),(97,38,'VALIDATED','PROCESSING',NULL,'EMI payment initiated','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(98,38,'PROCESSING','COMPLETED',NULL,'EMI payment completed successfully','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(99,39,'CREATED','VALIDATED',NULL,'Flight booking payment validated','SYSTEM','2026-08-04 05:56:04'),(100,39,'VALIDATED','PROCESSING',NULL,'Payment sent to airline','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(101,39,'PROCESSING','COMPLETED',NULL,'Flight booking payment successful','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(102,39,'COMPLETED','REFUNDED',NULL,'Flight cancelled and refund processed','ADMIN','2026-08-04 05:56:04'),(103,40,'CREATED','VALIDATED',NULL,'Pharmacy payment validated','SYSTEM','2026-08-04 05:56:04'),(104,40,'VALIDATED','PROCESSING',NULL,'Payment sent to pharmacy','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(105,40,'PROCESSING','COMPLETED',NULL,'Medicine payment completed','PAYMENT_GATEWAY','2026-08-04 05:56:04'),(106,41,'CREATED','VALIDATED',NULL,'Utility bill payment validated','SYSTEM','2026-08-04 05:56:41'),(107,41,'VALIDATED','PROCESSING',NULL,'Water bill payment initiated','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(108,41,'PROCESSING','COMPLETED',NULL,'Water bill paid successfully','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(109,42,'CREATED','VALIDATED',NULL,'International payment validated','SYSTEM','2026-08-04 05:56:41'),(110,42,'VALIDATED','PROCESSING',NULL,'Awaiting overseas bank confirmation','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(111,43,'CREATED','VALIDATED',NULL,'Insurance premium validated','SYSTEM','2026-08-04 05:56:41'),(112,43,'VALIDATED','PROCESSING',NULL,'Insurance payment initiated','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(113,43,'PROCESSING','FAILED',NULL,'Transaction declined by issuing bank','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(114,44,'CREATED','VALIDATED',NULL,'Wallet payment validated','SYSTEM','2026-08-04 05:56:41'),(115,44,'VALIDATED','PROCESSING',NULL,'Merchant accepted wallet payment','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(116,44,'PROCESSING','COMPLETED',NULL,'Online grocery payment completed','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(117,45,'CREATED','VALIDATED',NULL,'Crypto wallet verified','SYSTEM','2026-08-04 05:56:41'),(118,45,'VALIDATED','PROCESSING',NULL,'Blockchain transaction initiated','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(119,45,'PROCESSING','COMPLETED',NULL,'Bitcoin wallet recharge successful','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(120,46,'CREATED','VALIDATED',NULL,'Crowdfunding donation validated','SYSTEM','2026-08-04 05:56:41'),(121,46,'VALIDATED','PROCESSING',NULL,'Donation transferred successfully','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(122,46,'PROCESSING','COMPLETED',NULL,'Donation completed','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(123,46,'COMPLETED','REFUNDED',NULL,'Duplicate donation refunded','ADMIN','2026-08-04 05:56:41'),(124,47,'CREATED','VALIDATED',NULL,'Conference registration verified','SYSTEM','2026-08-04 05:56:41'),(125,48,'CREATED','VALIDATED',NULL,'Business purchase validated','SYSTEM','2026-08-04 05:56:41'),(126,48,'VALIDATED','PROCESSING',NULL,'International supplier payment initiated','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(127,48,'PROCESSING','COMPLETED',NULL,'Business equipment payment completed','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(128,49,'CREATED','CREATED',NULL,'Recharge request initiated','CUSTOMER','2026-08-04 05:56:41'),(129,50,'CREATED','VALIDATED',NULL,'Hotel reservation validated','SYSTEM','2026-08-04 05:56:41'),(130,50,'VALIDATED','PROCESSING',NULL,'Hotel booking payment initiated','PAYMENT_GATEWAY','2026-08-04 05:56:41'),(131,50,'PROCESSING','COMPLETED',NULL,'Hotel reservation payment completed','PAYMENT_GATEWAY','2026-08-04 05:56:41');
/*!40000 ALTER TABLE `payment_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_method_master`
--

DROP TABLE IF EXISTS `payment_method_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_method_master` (
  `method_id` bigint NOT NULL AUTO_INCREMENT,
  `method_code` varchar(20) NOT NULL,
  `method_name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`method_id`),
  UNIQUE KEY `method_code` (`method_code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_method_master`
--

LOCK TABLES `payment_method_master` WRITE;
/*!40000 ALTER TABLE `payment_method_master` DISABLE KEYS */;
INSERT INTO `payment_method_master` VALUES (1,'UPI','Unified Payments Interface','Instant bank transfer',1),(2,'CC','Credit Card','Visa/MasterCard/RuPay Credit Card',1),(3,'DC','Debit Card','Visa/MasterCard/RuPay Debit Card',1),(4,'NB','Net Banking','Internet Banking',1),(5,'WALLET','Digital Wallet','Wallet Payments',1),(6,'CRYPTO','Cryptocurrency','Blockchain Payment',1);
/*!40000 ALTER TABLE `payment_method_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refund`
--

DROP TABLE IF EXISTS `refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refund` (
  `refund_id` bigint NOT NULL AUTO_INCREMENT,
  `payment_id` bigint NOT NULL,
  `refund_reference` varchar(50) NOT NULL,
  `refund_amount` decimal(15,2) NOT NULL,
  `refund_reason` varchar(255) NOT NULL,
  `refund_status` enum('REQUESTED','APPROVED','PROCESSING','COMPLETED','REJECTED') NOT NULL,
  `refund_method` enum('ORIGINAL_PAYMENT_METHOD','BANK_TRANSFER','WALLET') NOT NULL,
  `initiated_by` enum('CUSTOMER','ADMIN','SYSTEM') NOT NULL,
  `refund_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `remarks` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`refund_id`),
  UNIQUE KEY `refund_reference` (`refund_reference`),
  KEY `fk_refund_payment` (`payment_id`),
  CONSTRAINT `fk_refund_payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refund`
--

LOCK TABLES `refund` WRITE;
/*!40000 ALTER TABLE `refund` DISABLE KEYS */;
INSERT INTO `refund` VALUES (1,11,'RFD000001',2400.00,'Duplicate Payment','COMPLETED','ORIGINAL_PAYMENT_METHOD','CUSTOMER','2026-08-04 06:00:58','Refund credited to original payment method'),(2,28,'RFD000002',2200.00,'Duplicate Grocery Payment','COMPLETED','ORIGINAL_PAYMENT_METHOD','CUSTOMER','2026-08-04 06:00:58','Duplicate transaction reversed'),(3,39,'RFD000003',7600.00,'Flight Cancelled','COMPLETED','BANK_TRANSFER','ADMIN','2026-08-04 06:00:58','Refund approved after airline cancellation'),(4,46,'RFD000004',3200.00,'Duplicate Crowdfunding Donation','COMPLETED','ORIGINAL_PAYMENT_METHOD','ADMIN','2026-08-04 06:00:58','Donation refunded successfully'),(5,4,'RFD000005',3200.00,'Failed Hospital Payment','REQUESTED','ORIGINAL_PAYMENT_METHOD','CUSTOMER','2026-08-04 06:00:58','Customer requested refund'),(6,14,'RFD000006',320.00,'Gateway Timeout','PROCESSING','ORIGINAL_PAYMENT_METHOD','SYSTEM','2026-08-04 06:00:58','Refund under gateway verification'),(7,23,'RFD000007',1750.00,'Wallet Transaction Failed','APPROVED','WALLET','SYSTEM','2026-08-04 06:00:58','Approved for wallet credit'),(8,33,'RFD000008',18500.00,'Hospital Payment Failure','REQUESTED','BANK_TRANSFER','CUSTOMER','2026-08-04 06:00:58','Pending admin verification'),(9,43,'RFD000009',4500.00,'Insurance Premium Failure','REJECTED','BANK_TRANSFER','ADMIN','2026-08-04 06:00:58','Refund rejected due to policy validation'),(10,5,'RFD000010',15000.00,'International Transaction Reversal','PROCESSING','BANK_TRANSFER','ADMIN','2026-08-04 06:00:58','Awaiting international banking approval');
/*!40000 ALTER TABLE `refund` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refund_reason_master`
--

DROP TABLE IF EXISTS `refund_reason_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refund_reason_master` (
  `reason_id` bigint NOT NULL AUTO_INCREMENT,
  `reason_name` varchar(100) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`reason_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refund_reason_master`
--

LOCK TABLES `refund_reason_master` WRITE;
/*!40000 ALTER TABLE `refund_reason_master` DISABLE KEYS */;
INSERT INTO `refund_reason_master` VALUES (1,'Duplicate Payment','Duplicate transaction'),(2,'Gateway Timeout','Gateway timeout'),(3,'Customer Request','Refund requested by customer'),(4,'Fraud Detection','Suspicious transaction'),(5,'Flight Cancellation','Travel cancellation'),(6,'Order Cancellation','Merchant cancelled order'),(7,'Payment Failure','Transaction failure');
/*!40000 ALTER TABLE `refund_reason_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_master`
--

DROP TABLE IF EXISTS `status_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status_master` (
  `status_id` bigint NOT NULL AUTO_INCREMENT,
  `status_name` varchar(50) DEFAULT NULL,
  `module_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`status_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_master`
--

LOCK TABLES `status_master` WRITE;
/*!40000 ALTER TABLE `status_master` DISABLE KEYS */;
INSERT INTO `status_master` VALUES (1,'CREATED','PAYMENT'),(2,'VALIDATED','PAYMENT'),(3,'PROCESSING','PAYMENT'),(4,'COMPLETED','PAYMENT'),(5,'FAILED','PAYMENT'),(6,'REFUNDED','PAYMENT'),(7,'CANCELLED','PAYMENT'),(8,'REQUESTED','REFUND'),(9,'APPROVED','REFUND'),(10,'REJECTED','REFUND');
/*!40000 ALTER TABLE `status_master` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-05  5:37:51
