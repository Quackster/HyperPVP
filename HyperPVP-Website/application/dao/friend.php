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

class FriendDao {
	
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
	
	public static function hasFriendRequest($from, $to) {
	
		$count = count(R::getAll("SELECT id FROM `users_friendrequest` WHERE `sender` = ? AND `receiver` = ?", array($from, $to)));
		return $count > 0;
	}
	
	
	public static function getFriendRequests($to) {
	
		$query = R::getAll("SELECT * FROM `users_friendrequest` WHERE `receiver` = ?", array($to));
		return self::loadStdClass($query);
	}
	
	public static function getFriendRequest($id) {
	
		$query = R::getAll("SELECT * FROM `users_friendrequest` WHERE `id` = ?", array($id));
		return self::loadStdClass($query)[0];
	}
}