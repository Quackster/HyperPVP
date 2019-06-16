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

class User extends Controller {
	public function __construct() {
		parent::__construct();
	}

	public function profile() {

			$profileName = explode('/', $_GET["do"]);
			
			$this->load_view("profile");
			$this->view->data->username = $profileName[1];
			
			if ($this->view->data->username == "")  {
				$this->load_view("error");
				$this->view->bind("error->title", "No such user exists"); 
				$this->view->bind("error->message", "I'm sorry. The user does not exist. ##1");
				$this->view->publish();
				return;
			}
			
			if (!UserDao::exists("username", $this->view->data->username)) {
				$this->load_view("error");
				$this->view->bind("error->title", "No such user exists"); 
				$this->view->bind("error->message", "I'm sorry. The user does not exist. ##2");
				$this->view->publish();
				return;
			}
		
			$row = UserDao::getUser($this->view->data->username);
			$this->view->data->user = $row;
			
			$this->view->bind("user->name", $row->username);
			$this->view->bind("user->last_online",  humanTiming($row->last_online));
			$this->view->bind("user->kills", $row->kills);
			$this->view->bind("user->deaths", $row->deaths);
			$this->view->bind("user->monuments", $row->broke_monument);
			$this->view->bind("user->cores", $row->leaked_core);
			
			
            $k = $row->kills;
            $d = $row->deaths;
            
			if ($k == 0 || $d == 0) {
				$kdRatio = 0;
				$kdPercent = 0;
			} else {
			
				$kdRatio = round($k / $d, 3);
				$kdPercent = round($k / ($k + $d), 2) * 100;
			
			}
			$this->view->bind("user->kdratio", $kdRatio);
			$this->view->bind("user->kdpercent", $kdPercent);
			
			$i = 0;
			foreach (UserDao::getFriendsNoLimit($row->id) as $friend) {
				$i++;
			}
			
			
			$this->view->bind("user->friends", $i);
			$this->view->publish();
	}
    	
	public function register() {
	
		if (Session::is_auth()) {
			Router::sendTo("profile/" . Session::auth()->username);
			return;
		}
	
		$this->load_view("user/register");
		$this->view->data->tab = 'register';
		$this->view->publish();
	
	}
	
	public function recent() {
	
		$this->load_view("user/recent");
		$this->view->data->tab = 'recent';
		$this->view->publish();
	
	}
	
	public function login() {
	
		$form = new Form("post", array('username', 'password'));
	
		if($form->check()) {
			$form->produce();

			$username = $form->field->username;
			$password = $form->field->password;
			
			$user = UserDao::getByKey('username', $username); 
			$salted_password = hash("sha256", $password . $user[0]['salt']);
			
			if(!UserDao::exists('username', $username)) {
				$this->load_view("error");
				$this->view->bind("error->title", "Login"); 
				$this->view->bind("error->message", "That user is not registered!");
				$this->view->publish();
				return;
			}

			if($user[0]['password'] == $salted_password) {
				Session::set($user[0]['id']);
				Router::sendTo("home");
			} else {
				$this->load_view("error");
				$this->view->bind("error->title", "Login"); 
				$this->view->bind("error->message", "You have entered the wrong password.");
				$this->view->publish();
			}
		} else {
			$this->load_view("user/login");
			$this->view->data->tab = 'login';
			$this->view->publish();
		}
	}
	
	public function logout() {
		
		if (Session::isAuthed()) {
			Session::deAuth();
			
			$this->load_view("error");
			$this->view->data->tab = 'logout';
			$this->view->bind("error->title", "Logout"); 
			$this->view->bind("error->message", "You have successfully logged out.");
			$this->view->publish();
		} else {
			Router::sendTo("home");
		}
	}
	
	public function redeem() {
	
		if (Session::isAuthed()) {
			Router::sendTo("profile/" . Session::getAuth()->username);
			return;
		}
	
		if (count($_POST) == 0) {
			Router::sendTo("register");
			return;
		}
	
		if (!isset($_POST['pin'])) {
			$_POST['pin'] = "";
		}
		
		if (!isset($_POST['username'])) {
			$_POST['username'] = "";
		}
		
		if (!isset($_POST['password'])) {
			$_POST['password'] = "";
		}
	
		if ($_POST['pin'] == "") {
			$this->load_view("error");
			$this->view->bind("error->title", "Invalid PIN"); 
			$this->view->bind("error->message", "You have entered an invalid PIN code.");
			$this->view->publish();
			return;
		}
		
		if ($_POST['username'] == "") {
			$this->load_view("error");
			$this->view->bind("error->title", "Invalid username"); 
			$this->view->bind("error->message", "You have entered an invalid username.");
			$this->view->publish();
			return;
		}
		
		if ($_POST['password'] == "" || strlen($_POST['password']) < 8) {
			$this->load_view("error");
			$this->view->bind("error->title", "Invalid password");
			if ($_POST['password'] == "") {
				$this->view->bind("error->message", "You have entered an invalid password.");
			} else {
				$this->view->bind("error->message", "Your password is too short, it needs to be at least 8 characters long.");
			};
			
			$this->view->publish();
			return;
		}
		
		if (!SiteDao::pinExists($_POST['pin']))
		{
			$this->load_view("error");
			$this->view->bind("error->title", "Invalid PIN"); 
			$this->view->bind("error->message", "You have entered an invalid PIN code.");
			$this->view->publish();
			return;
		}
		
		$pin_data = UserDao::getPinData($_POST['pin']);		
		$username = $_POST['username'];

		if ($pin_data->username != $username) {
			$this->load_view("error");
			$this->view->bind("error->title", "Invalid username"); 
			$this->view->bind("error->message", "You have entered an invalid username.");
			$this->view->publish();
			return;
		}
		
		$salt = randString(16);
		$password = hash("sha256", $_POST['password'] . $salt);
		
		R::exec("UPDATE `users` SET `password` = :password, `salt` = :salt, `pin` = '' WHERE `id` = '" . $pin_data->id . "'", 
			array(
			":password" => $password, 
			":salt" => $salt));
		
		Session::set($pin_data->id);
		Router::sendTo("home");
		
	}
}