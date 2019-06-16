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

class Api extends Controller {
	public function __construct() {
		parent::__construct();

		$this->load_view('error');
	}

	public function servers() {
		
        $servers = R::getAll("SELECT * FROM `servers`");
        $output = "";
        
        foreach ($servers as $server) {
        
            $output .= "," . $server["bungee_name"];
        }
        
        echo substr($output, 1);
	}
    
    public function serverData() {
                
        if (!isset($_GET['server'])) {
            $_GET['server'] = "";
        }
        
        $json = false;
        
        if (isset($_GET['json'])) {
        
            $json = true;
        }
        
        if ($_GET['server'] == "") {
            if ($json) {
                echo json_encode(array("error" => "The server field is blank."));
            } else {
                echo ("error:The server field is blank.");
            }
            return;
        }
        
        $count  = count(R::getAll("SELECT * FROM `servers` WHERE `bungee_name` = ?", array($_GET['server'])));

		if($count == 0) {
        
            if ($json) {
                echo json_encode(array("error" => "The server does not exist."));
            } else {
                echo ("error:The server does not exist.");
            }
            
            return;
		}
        
        $output = R::getAll("SELECT * FROM `servers` WHERE `bungee_name` = ?", array($_GET['server']));
        $output = $output[0];
        unset($output["id"]);
        unset($output["port"]);
        
        if ($json) {
        
            echo json_encode($output);
        } else {
       
            $str = "";

            foreach ($output as $key => $value) {
                $str .= "," . $key . ":" . $value;
            }

            echo substr($str, 1);
        }
	}
    
    public function playerData() {
                
        if (!isset($_GET['username'])) {
            $_GET['username'] = "";
        }
        
        $json = false;
        
        if (isset($_GET['json'])) {
        
            $json = true;
        }
        
        if ($_GET['username'] == "") {
            if ($json) {
                echo json_encode(array("error" => "The username field is blank."));
            } else {
                echo ("error:The username field is blank.");
            }
            return;
        }
        
        $output = R::getAll("SELECT * FROM `users` WHERE `username` = ?", array($_GET['username']));
        
        if (count($output) == 0) {
        
            if ($json) {
                echo json_encode(array("error" => "The player does not exist."));
            } else {
                echo ("error:The player does not exist.");
            }
            
            return;
		}
        
        $output = $output[0];
        unset($output["id"]);
        unset($output["password"]);
        unset($output["email"]);
        unset($output["won_match"]);
        
        if ($json) {

            echo json_encode($output);
            
        } else {

            $str = "";

            foreach ($output as $key => $value) {
                $str .= "," . $key . ":" . $value;
            }

            echo substr($str, 1);
        }
    }
    
    public function playerStats() {
                
        if (!isset($_GET['username'])) {
            $_GET['username'] = "";
        }
        
        $json = false;
        
        if (isset($_GET['json'])) {
        
            $json = true;
        }
        
        if ($_GET['username'] == "") {
            if ($json) {
                echo json_encode(array("error" => "The username field is blank."));
            } else {
                echo ("error:The username field is blank.");
            }
            return;
        }
        
        $output = R::getAll("SELECT * FROM `users` WHERE `username` = ?", array($_GET['username']));
        
        if (count($output) == 0) {
        
            if ($json) {
                echo json_encode(array("error" => "The player does not exist."));
            } else {
                echo ("error:The player does not exist.");
            }
            
            return;
		}
        
        $data = UserDao::getUser($_GET['username']);
        
        $stats = UserDao::loadStdClass(R::getAll('SELECT * FROM `users_statistics` WHERE `from_id` = ? ORDER BY id DESC LIMIT 25', array($data->id)));
        $first = array();
        
        
        if ($json) {

            echo json_encode($output);
            
        } else {

            $str = "";

            foreach ($output as $key => $value) {
                $str .= "," . $key . ":" . $value;
            }

            echo substr($str, 1);
        }
    }
}