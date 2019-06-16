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

class SiteDao {
	/*public static function get($key) {
		$cfg = R::load('site_config', 1);

		return $cfg->$key;
	}
    
    public static function system($key) {
		$cfg = R::load('system', $key);
		return $cfg->value;
	}*/
	
	public static function loadStdClass($query)
	{		
		$array = array();
		foreach($query as $a) {
			$obj = new StdClass();
			foreach($a as $key => $val) {
				$obj->$key = $val;
			}
			$array[] = $obj;
		}
		return $array;
	}
	
	public static function pinExists($value) {
		$count  = count(R::getAll("SELECT pin FROM `users` WHERE `pin` = :value", array(":value" => $value)));

		if($count != 1) {
			return false;
		}

		return true;
	}
	
	public static function getPin($user) {
		$pin = R::getAll("SELECT * FROM `user` WHERE `pin` = :value", array(":value" => $user));
		return $pin[0]['code'];
	}
	
	public static function getServers() {
	
		$query = R::getAll('SELECT * FROM `servers`', array());
		return self::loadStdClass($query);
	}
    
    public static function getServer($name) {
	
		$query = R::findOne('servers', ' bungee_name = ? ', array($name));
		return $query;
	}
	
	public static function getTopRanks() {
	
		$query = R::getAll('SELECT * FROM `users` WHERE kills <> 0 ORDER BY kills DESC LIMIT 20', array());
		return self::loadStdClass($query);
	}
    
    public static function getTopRanksByName($name) {
	
		$query = R::getAll('SELECT * FROM `users` WHERE ' . $name . ' <> 0 ORDER BY ' . $name . ' DESC LIMIT 20', array());
		return self::loadStdClass($query);
	}
	
	public static function getUsers($id) {
	
		$query = R::getAll('SELECT * FROM `servers_users` WHERE server_id = ?', array($id));
		return self::loadStdClass($query);
	}
}