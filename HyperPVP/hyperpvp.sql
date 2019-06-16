-- phpMyAdmin SQL Dump
-- version 4.8.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jun 16, 2019 at 07:34 AM
-- Server version: 10.3.13-MariaDB
-- PHP Version: 7.2.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `hyperpvp`
--

-- --------------------------------------------------------

--
-- Table structure for table `pincodes`
--

CREATE TABLE `pincodes` (
  `id` int(11) NOT NULL,
  `code` varchar(5) NOT NULL,
  `name` varchar(20) NOT NULL,
  `email` varchar(256) NOT NULL,
  `password` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `servers`
--

CREATE TABLE `servers` (
  `id` int(255) NOT NULL,
  `port` int(123) NOT NULL,
  `name` varchar(255) NOT NULL,
  `bungee_name` varchar(25) NOT NULL,
  `current_type` varchar(3) NOT NULL,
  `current_name` text NOT NULL,
  `status` tinyint(1) NOT NULL,
  `mins_left` int(3) NOT NULL,
  `team_one` int(11) NOT NULL,
  `team_two` int(11) NOT NULL,
  `map_status` enum('STARTING','PLAYING','FINISHED') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `servers`
--

INSERT INTO `servers` (`id`, `port`, `name`, `bungee_name`, `current_type`, `current_name`, `status`, `mins_left`, `team_one`, `team_two`, `map_status`) VALUES
(1, 25565, 'Thor', 'thor', 'DTC', 'Blood Forest', 1, 30, 0, 0, 'STARTING');

-- --------------------------------------------------------

--
-- Table structure for table `servers_users`
--

CREATE TABLE `servers_users` (
  `id` int(11) NOT NULL,
  `server_id` varchar(20) NOT NULL,
  `user` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `site_links`
--

CREATE TABLE `site_links` (
  `id` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `logged_in` tinyint(1) NOT NULL,
  `staff` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `username` varchar(20) NOT NULL,
  `password` varchar(255) NOT NULL DEFAULT '',
  `salt` varchar(255) DEFAULT NULL,
  `pin` varchar(255) DEFAULT NULL,
  `rank` int(11) NOT NULL DEFAULT 1,
  `last_online` bigint(255) NOT NULL DEFAULT 0,
  `email` varchar(100) DEFAULT NULL,
  `kills` int(11) NOT NULL DEFAULT 0,
  `deaths` int(11) NOT NULL DEFAULT 0,
  `won_match` tinyint(1) NOT NULL DEFAULT 0,
  `leaked_core` int(11) NOT NULL DEFAULT 0,
  `broke_monument` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users_friendrequest`
--

CREATE TABLE `users_friendrequest` (
  `id` int(11) NOT NULL,
  `sender` varchar(20) NOT NULL,
  `receiver` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users_friends`
--

CREATE TABLE `users_friends` (
  `id` int(11) NOT NULL,
  `sender` varchar(250) NOT NULL,
  `receiver` varchar(250) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users_statistics`
--

CREATE TABLE `users_statistics` (
  `id` int(11) NOT NULL,
  `from_id` int(20) NOT NULL,
  `to_id` int(20) NOT NULL,
  `type` enum('death','kill','core','monument') NOT NULL,
  `time` bigint(20) NOT NULL,
  `map` varchar(25) NOT NULL,
  `mode` varchar(5) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `pincodes`
--
ALTER TABLE `pincodes`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `servers`
--
ALTER TABLE `servers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `servers_users`
--
ALTER TABLE `servers_users`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users_friendrequest`
--
ALTER TABLE `users_friendrequest`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users_friends`
--
ALTER TABLE `users_friends`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users_statistics`
--
ALTER TABLE `users_statistics`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `pincodes`
--
ALTER TABLE `pincodes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `servers`
--
ALTER TABLE `servers`
  MODIFY `id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `servers_users`
--
ALTER TABLE `servers_users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users_friendrequest`
--
ALTER TABLE `users_friendrequest`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users_friends`
--
ALTER TABLE `users_friends`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users_statistics`
--
ALTER TABLE `users_statistics`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
