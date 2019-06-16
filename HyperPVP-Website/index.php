<?php
/*
	 /'\_/`\                                    
	/\      \     __      ___      __     ___   
	\ \ \__\ \  /'__`\  /' _ `\  /'_ `\  / __`\ 
	 \ \ \_/\ \/\ \L\.\_/\ \/\ \/\ \L\ \/\ \L\ \
	  \ \_\\ \_\ \__/.\_\ \_\ \_\ \____ \ \____/
	   \/_/ \/_/\/__/\/_/\/_/\/_/\/___L\ \/___/ 
	                               /\____/      
	                               \_/__/       
	@author Leon Hartley
*/

ob_start();
session_start();

define("START", microtime(true));
define("APP_PATH", 'application');

/*
|--------------------------------------------------------------------------
| Fix IP addresses.
|--------------------------------------------------------------------------
*/
if (isset($_SERVER['HTTP_CF_CONNECTING_IP'])) {
	$_SERVER['HTTP_X_FORWARDED_FOR'] = $_SERVER['HTTP_CF_CONNECTING_IP'];
	$_SERVER['HTTP_CLIENT_IP'] = $_SERVER['HTTP_CF_CONNECTING_IP'];
	$_SERVER['REMOTE_ADDR'] = $_SERVER['HTTP_CF_CONNECTING_IP'];
}

/*
|--------------------------------------------------------------------------
| Fix SSL detection.
|--------------------------------------------------------------------------
*/
if (isset($_SERVER['HTTP_CF_VISITOR'])) {
	if (preg_match('/https/i', $_SERVER['HTTP_CF_VISITOR'])) {
		$_SERVER['HTTPS'] = 'On';
		$_SERVER['HTTP_X_FORWARDED_PORT'] = 443;
		$_SERVER['SERVER_PORT'] = 443;
	}
}

require APP_PATH . "/sierra.php";
$Sierra = new Sierra();