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

class Command extends Controller {
	public function __construct() {
		parent::__construct();
	}

	public function handle() {

		if ($_GET['key'] == "" || $_GET['command'] == "" || $_GET['params'] == "") {
			echo "Blank.";
			return;
		}

		$key = $_GET['key'];
		
		if ($key != "douevenagro123") {
			echo "Key mismatch!";
			return;
		}
		
		$args = explode(",", $_GET['params']);
		
		if ($_GET['command'] == "profile") {
			
			$users = R::dispense('users');
			$users->username = $args[0];
			$users->last_online = $args[1];
			R::store($users);
			return;
		}
		
		if ($_GET['command'] == "exists") {
			echo UserDao::exists("username", $args[0]) ? "true" : "false";
			return;
		}
		
		if ($_GET['command'] == "update_user") {
			R::exec("UPDATE users SET " . $args[1] . " = '" . $args[2] . "' WHERE username = '" . $args[0] . "'");
			return;
		}
		
		if ($_GET['command'] == "select_user") {
			//R::exec("UPDATE users SET " . $args[1] . " = '" . $args[2] . "' WHERE username = '" . $args[0] . "'");
			
			$user = UserDao::getUser($args[0]);
			echo $user->$args[1];
			return;
		}
		
		if ($_GET['command'] == "update_servers") {
		
			if (strpos($args[2], "_"))
			{
				$args[2] = str_replace("_", " ", $args[2]);
			}
			
		
			R::exec("UPDATE servers SET " . $args[1] . " = '" . $args[2] . "' WHERE id = '" . $args[0] . "'");
			return;
		}
		
		if ($_GET['command'] == "user_status") {
		
			if ($args[0] == "online") {
			
			
				$server = R::dispense('servers_users');
				$server->server_id = $args[2];
				$server->user = $args[1];
				R::store($server);
				return;
				
			} else if ($args[0] == "offline") {
			
				R::exec("DELETE FROM servers_users WHERE user = '" . $args[1] . "'");
				return;
				
			}
		}
		
		if ($_GET['command'] == "kills") {
			$obj = UserDao::getUser($args[0]);
			echo $obj->kills;
			return;
		}
		
		if ($_GET['command'] == "deaths") {
			$obj = UserDao::getUser($args[0]);
			echo $obj->deaths;
			return;
		}
		
		if ($_GET['command'] == "statistic") {
		
			if ($args[1] == "kill") {
			
				$server = R::dispense('users_statistics');
				$server->from = $args[0];
				$server->target = $args[2];
				$server->type = "kill";
				R::store($server);
                
                $killer = UserDao::getUser($args[0]);
                R::exec("UPDATE users SET kills = '" . ($killer->kills + 1) . "' WHERE username = ?", array($args[0]));
                
                $killed = UserDao::getUser($args[2]);
                R::exec("UPDATE users SET deaths = '" . ($killed->deaths + 1) . "' WHERE username = ?", array($args[2]));
				
				$server = R::dispense('users_statistics');
				$server->from = $args[2];
				$server->target = $args[0];
				$server->type = "death";
				R::store($server);
		
			} else if ($args[1] == "death") {
			
				$server = R::dispense('users_statistics');
				$server->from = $args[2];
				$server->target = $args[0];
				$server->type = "kill";
				R::store($server);
                
                $killer = UserDao::getUser($args[2]);
                R::exec("UPDATE users SET kills = '" . ($killer->kills + 1) . "' WHERE username = ?", array($args[2]));
                
                $killed = UserDao::getUser($args[0]);
                R::exec("UPDATE users SET deaths = '" . ($killed->deaths + 1) . "' WHERE username = ?", array($args[0]));
				
				
				$server = R::dispense('users_statistics');
				$server->from = $args[0];
				$server->target = $args[2];
				$server->type = "death";
				R::store($server);

				
			}
		}
		
		if ($_GET['command'] == "nousersonline") {
			R::exec("DELETE FROM servers_users WHERE server_id = '" . $args[0] . "'");
		}
		
		if ($_GET['command'] == "pin_exists") {
			echo SiteDao::pinExists($args[0]) ? "true" : "false";
		}
		
		if ($_GET['command'] == "get_pin") {
			echo SiteDao::getPin($args[0]);
		}
		
		if ($_GET['command'] == "new_pin") {
			$pin = R::dispense('pincodes');
			$pin->code = $args[0];
			$pin->name = $args[1];
			$pin->email = $args[2];
			$pin->password = $args[3];
			R::store($pin);
		}
	}
}