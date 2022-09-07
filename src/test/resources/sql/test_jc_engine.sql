/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 80020
Source Host           : localhost:3306
Source Database       : test_jc_engine

Target Server Type    : MYSQL
Target Server Version : 80020
File Encoding         : 65001

Date: 2022-09-07 11:28:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user_game_record
-- ----------------------------
DROP TABLE IF EXISTS `user_game_record`;
CREATE TABLE `user_game_record` (
  `userID` int NOT NULL,
  `dateTime` datetime NOT NULL,
  `gameType` int NOT NULL,
  `duration` int NOT NULL,
  PRIMARY KEY (`userID`,`dateTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `userID` int NOT NULL AUTO_INCREMENT,
  `nickname` varchar(16) NOT NULL,
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
