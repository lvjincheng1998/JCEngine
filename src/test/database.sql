CREATE DATABASE IF NOT EXISTS test default charset utf8 COLLATE utf8_general_ci;

use test;

DROP TABLE IF EXISTS `prop`;
CREATE TABLE `prop` (
  `auto_id` int NOT NULL AUTO_INCREMENT,
  `player_id` int NOT NULL,
  `prop_id` int NOT NULL,
  `prop_count` int NOT NULL,
  PRIMARY KEY (`auto_id`)
) ENGINE=InnoDB AUTO_INCREMENT=325 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `weapon`;
CREATE TABLE `weapon` (
  `id` int NOT NULL AUTO_INCREMENT,
  `player_id` int NOT NULL,
  `weapon_id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `prop` VALUES ('1', '1', '11', '111');
INSERT INTO `prop` VALUES ('2', '2', '22', '222');
INSERT INTO `prop` VALUES ('3', '3', '33', '333');

INSERT INTO `weapon` VALUES ('1', '1000', '1001');
INSERT INTO `weapon` VALUES ('2', '2000', '2001');
INSERT INTO `weapon` VALUES ('3', '3000', '3001');