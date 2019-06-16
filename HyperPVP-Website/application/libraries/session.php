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

class Session {
	public static function auth() {
		if(Session::isAuthed()) {
			return UserDao::get(Sierra::getSession());
		} else {
			return null;
		}
	}

	public static function isAuthed() {
		if(isset(Sierra::getSession()->auth)) {
			if(is_numeric(Sierra::getSession()->auth)) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public static function hasHousekeeping() {
		if(Session::auth()->rank >= Sierra::getConfig()->site->housekeeping_rank)
			return true;
		else
			return false;
	}

	public static function getAuth() {
		return (isset(Sierra::getSession()->auth) ? Sierra::getSession()->auth : null);
	}

	public static function set($id) {
		$_SESSION['auth'] = $id;
	}

	public static function setOwn($key, $value) {
		$_SESSION[$key] = $value;
	}

	public static function deAuth() {
		unset($_SESSION['auth']);
	}

	public static function destroy() {
		session_destroy();
	}
	
	public static function is_auth() {
		return isset($_SESSION['auth']);
	}

	public static function kill($key) {
		unset($_SESSION[$key]);
	}
}