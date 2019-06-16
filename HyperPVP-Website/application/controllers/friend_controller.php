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

class Friend extends Controller {
	public function __construct() {
		parent::__construct();
	}

	public function request() {
	
		if (!Session::isAuthed()) {
			$this->load_view("error");
			$this->view->bind("error->title", "Friend Request");  
			$this->view->bind("error->message", "You can't perform this action when you're not logged in!");
			$this->view->publish();
			return;
		} 
		
		if (!isset($_GET['id'])) {
			$_GET['id'] == "";
		}
		
		if ($_GET['id'] == "") {
			$this->load_view("error");
			$this->view->bind("error->title", "Friend Request"); 
			$this->view->bind("error->message", "Your friend request cannot be blank!");
			$this->view->publish();
			return;
		}
		
		if ($_GET['id'] == Session::auth()->id) {
			$this->load_view("error");
			$this->view->bind("error->title", "Friend Request"); 
			$this->view->bind("error->message", "Your friend request cannot be yourself!");
			$this->view->publish();
			return;
		}
		
		if (!UserDao::exists("id", $_GET['id'])) {
			$this->load_view("error");
			$this->view->bind("error->title", "Friend Request"); 
			$this->view->bind("error->message", "Sorry, that name doesn't exist in the user database!");
			$this->view->publish();
			return;
		}
		
		if (FriendDao::hasFriendRequest(Session::auth()->id, $_GET['id'])) {
			$this->load_view("error");
			$this->view->bind("error->title", "Friend Request"); 
			$this->view->bind("error->message", "You have already sent them a friend request!");
			$this->view->publish();
			return;
		}
		
		$row = UserDao::getId($_GET['id']);
		
		if ($_GET['is_sure'] == "0") {
			$this->load_view("error");
			$this->view->bind("error->title", "Friend Request"); 
			$this->view->bind("error->message", '<p><h4>Friend Request Confirmation</h4></p><p>Are you sure you want to send a friend request to '. $row->username .'?</p><p><p><a href="{$site->url}/friend/request?is_sure=1&id=' . $_GET['id'] .'"><button class="btn btn-default" type="button">I\'m sure!</button></a></p>');
			$this->view->publish();
			return;
		}
		
		
		$friend = R::dispense('users_friendrequest');
        $friend->receiver = $_GET['id'];
        $friend->sender = Session::auth()->id;
        $id = R::store($friend);
		
		$this->load_view("error");
	    $this->view->bind("error->title", "Friend Request"); 
	    $this->view->bind("error->message", "You have sent a friend request to <strong>$row->username</strong>, please wait until they accept it");
		$this->view->publish();
	}
	
	public function requests() {
	
		if (!Session::isAuthed()) {
			$this->load_view("error");
			$this->view->bind("error->title", "Friend Requests"); 
			$this->view->bind("error->message", "You can't perform this action when you're not logged in!");
			$this->view->publish();
			return;
		} 
		
		$this->load_view("user/requests");
		$this->view->publish();
	} 
	
	public function accept() {
	
		if (!Session::isAuthed()) {
			$this->load_view("error");
			$this->view->bind("error->title", "Accept Friend"); 
			$this->view->bind("error->message", "You can't perform this action when you're not logged in!");
			$this->view->publish();
			return;
		} 
	
		if (!isset($_GET['id'])) {
			$_GET['id'] = 0;
		}
		
		$friend_request = FriendDao::getFriendRequest($_GET['id']);
		
		if (!FriendDao::hasFriendRequest($friend_request->sender, Session::auth()->id)) {
			$this->load_view("error");
			$this->view->bind("error->title", "Accept Friend");
			$this->view->bind("error->message", "You have tried to accept a friend request which never existed.");
			$this->view->publish();
			return;
		}
		
		if ($friend_request->receiver != Session::auth()->id) {
			$this->load_view("error");
			$this->view->bind("error->title", "Accept Friend");
			$this->view->bind("error->message", "Could not accept friend request.");
			$this->view->publish();
			return;
		}
		
		R::exec("DELETE FROM users_friendrequest WHERE `sender` = ? AND `receiver` = ?", array($friend_request->sender, Session::auth()->id));
		R::exec("DELETE FROM users_friendrequest WHERE `receiver` = ? AND `sender` = ?", array($friend_request->sender, Session::auth()->id));
		
		$friend = R::dispense('users_friends');
		$friend->receiver = Session::auth()->id;
        $friend->sender = $friend_request->sender;
        R::store($friend);
		
		$this->load_view("error");
		$this->view->bind("error->title", "Accept Friend");
		$this->view->bind("error->message", "You have accepted <strong>" . UserDao::getId($friend_request->sender)->username . "</strong>'s friend request!");
		$this->view->publish();
	} 
	
	public function deny() {
	
		if (!Session::isAuthed()) {
			$this->load_view("error");
			$this->view->bind("error->title", "Deny Friend Request"); 
			$this->view->bind("error->message", "You can't perform this action when you're not logged in!");
			$this->view->publish();
			return;
		} 
	
		if (!isset($_GET['id'])) {
			$_GET['id'] = 0;
		}	
				
		$friend_request = FriendDao::getFriendRequest($_GET['id']);
		
		if (!FriendDao::hasFriendRequest($friend_request->sender, Session::auth()->id)) {
			$this->load_view("error");
			$this->view->bind("error->title", "Deny Friend Request");  
			$this->view->bind("error->message", "Could not deny friend request.");
			$this->view->publish();
			return;
		}
		
		if (!UserDao::exists("id", $friend_request->sender)) {
			$this->load_view("error");
			$this->view->bind("error->title", "Deny Friend Request"); 
			$this->view->bind("error->message", "You have tried to remove a friend which never existed!");
			$this->view->publish();
			return;
		}
		
		if ($friend_request->receiver != Session::auth()->id) {
			$this->load_view("error");
			$this->view->bind("error->title", "Deny Friend Request"); 
			$this->view->bind("error->message", "Could not deny friend request.");
			$this->view->publish();
			return;
		}
		
		R::exec("DELETE FROM users_friendrequest WHERE `sender` = ? AND `receiver` = ?", array($friend_request->sender, Session::auth()->id));
		R::exec("DELETE FROM users_friendrequest WHERE `receiver` = ? AND `sender` = ?", array($friend_request->sender, Session::auth()->id));
		
		$this->load_view("error");
		$this->view->bind("error->title", "Deny Friend Request"); 
		$this->view->bind("error->message", "You have denied <strong>" . UserDao::getId($friend_request->sender)->username . "</strong>'s friend request!");
		$this->view->publish();
	}
	
	public function remove() {
	
		if (!Session::isAuthed()) {
			$this->load_view("error");
			$this->view->bind("error->title", "Remove Friend"); 
			$this->view->bind("error->message", "You can't perform this action when you're not logged in!");
			$this->view->publish();
			return;
		} 
	
		if (!isset($_GET['id'])) {
			$_GET['id'] = "";
		}
		
		if (!isset($_GET['is_sure'])) {
			$_GET['is_sure'] = "0";
		}
		
		$_GET['id'] = htmlspecialchars($_GET['id'], ENT_QUOTES, 'UTF-8');
		$row = UserDao::getId($_GET['id']);
		
		if (!UserDao::exists("id", $row->id)) {
			$this->load_view("error");
			$this->view->bind("error->title", "Remove Friend"); 
			$this->view->bind("error->message", "You have tried to remove a friend which never existed!");
			$this->view->publish();
			return;
		}
		
		if (!UserDao::isFriend(Session::auth()->id, $row->id)) {
			$this->load_view("error");
			$this->view->bind("error->title", "Remove Friend"); 
			$this->view->bind("error->message", "You have tried to remove a friend which never existed!");
			$this->view->publish();
			return;
		}
		
		if ($_GET['is_sure'] == "0") {
			$this->load_view("error");
			$this->view->bind("error->title", "Remove Friend"); 
			$this->view->bind("error->message", '<p><h4>Remove Friend Confirmation</h4></p><p>Are you sure you want to remove '. $row->username .' as a friend?</p><p><p><a href="{$site->url}/friend/remove?is_sure=1&id=' . $_GET['id'] .'"><button class="btn btn-default" type="button">I\'m sure!</button></a></p>');
			$this->view->publish();
			return;
		}
	
		R::exec("DELETE FROM users_friends WHERE `sender` = ? AND `receiver` = ?", array($row->id, Session::auth()->id));
		R::exec("DELETE FROM users_friends WHERE `receiver` = ? AND `sender` = ?", array($row->id, Session::auth()->id));
		
		$this->load_view("error");
		$this->view->bind("error->title", "Friend Remove"); 
		$this->view->bind("error->message", "You have removed " . $row->username . " as a friend!");
		$this->view->publish();
	}
}