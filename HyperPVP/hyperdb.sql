-- phpMyAdmin SQL Dump
-- version 4.0.4
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jan 24, 2014 at 04:44 AM
-- Server version: 5.6.12-log
-- PHP Version: 5.4.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `hyperpvp`
--
CREATE DATABASE IF NOT EXISTS `hyperpvp` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `hyperpvp`;

-- --------------------------------------------------------

--
-- Table structure for table `pincodes`
--

CREATE TABLE IF NOT EXISTS `pincodes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(5) NOT NULL,
  `name` varchar(20) NOT NULL,
  `email` varchar(256) NOT NULL,
  `password` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `servers`
--

CREATE TABLE IF NOT EXISTS `servers` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `port` int(123) NOT NULL,
  `name` varchar(255) NOT NULL,
  `bungee_name` varchar(25) NOT NULL,
  `current_type` varchar(3) NOT NULL,
  `current_name` text NOT NULL,
  `status` tinyint(1) NOT NULL,
  `mins_left` int(3) NOT NULL,
  `team_one` int(11) NOT NULL,
  `team_two` int(11) NOT NULL,
  `map_status` enum('STARTING','PLAYING','FINISHED') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=2 ;

--
-- Dumping data for table `servers`
--

INSERT INTO `servers` (`id`, `port`, `name`, `bungee_name`, `current_type`, `current_name`, `status`, `mins_left`, `team_one`, `team_two`, `map_status`) VALUES
(1, 25565, 'Thor', 'thor', 'FFA', 'Suburban Ruins', 3, 10, 0, 0, 'PLAYING');

-- --------------------------------------------------------

--
-- Table structure for table `servers_users`
--

CREATE TABLE IF NOT EXISTS `servers_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `server_id` varchar(20) NOT NULL,
  `user` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `site_links`
--

CREATE TABLE IF NOT EXISTS `site_links` (
  `id` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `logged_in` tinyint(1) NOT NULL,
  `staff` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL DEFAULT '',
  `rank` int(11) NOT NULL DEFAULT '1',
  `last_online` bigint(255) NOT NULL DEFAULT '0',
  `email` varchar(100) NOT NULL,
  `kills` int(11) NOT NULL DEFAULT '0',
  `deaths` int(11) NOT NULL DEFAULT '0',
  `won_match` tinyint(1) NOT NULL DEFAULT '0',
  `leaked_core` int(11) NOT NULL DEFAULT '0',
  `broke_monument` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `users_friendrequest`
--

CREATE TABLE IF NOT EXISTS `users_friendrequest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `to` varchar(20) NOT NULL,
  `from` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=20 ;

--
-- Dumping data for table `users_friendrequest`
--

INSERT INTO `users_friendrequest` (`id`, `to`, `from`) VALUES
(4, 'RastaLulz', 'Dinglydell'),
(5, 'TesoMayn', 'Dinglydell'),
(8, 'Pastalulz', '_AlexM'),
(9, 'RastaLulz', '_AlexM'),
(10, 'rardahji', '_AlexM'),
(13, 'Pastalulz', 'Dinglydell'),
(15, 'Prenom', 'My4rxs'),
(16, 'Prenom', 'SpiritusGhost'),
(18, 'cpdude1000', '_AlexM'),
(19, 'cnpi', '_AlexM');

-- --------------------------------------------------------

--
-- Table structure for table `users_friends`
--

CREATE TABLE IF NOT EXISTS `users_friends` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user` varchar(250) NOT NULL,
  `friend` varchar(250) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `users_statistics`
--

CREATE TABLE IF NOT EXISTS `users_statistics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_id` int(20) NOT NULL,
  `to_id` int(20) NOT NULL,
  `type` enum('death','kill','core','monument') NOT NULL,
  `time` bigint(20) NOT NULL,
  `map` varchar(25) NOT NULL,
  `mode` varchar(5) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
