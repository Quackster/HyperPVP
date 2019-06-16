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

class Index extends Controller {
	public function __construct() {
		parent::__construct();
	}

	public function base() {

		//if(Session::isAuthed()) { Router::sendTo('me'); }
		
		$this->load_view("index");

		//$this->view->bind("page->description", 'Make friends, join the fun, get noticed!');
		$this->view->data->tab = 'index';

		if(isset($_SESSION['error'])) {
			$this->view->data->error = $_SESSION['error'];

			unset($_SESSION['error']);
		}

		$this->view->publish();
	}

	public function servers() {
	
		$this->load_view("servers");
		$this->view->data->tab = 'servers';
		$this->view->publish();
	}
	
	public function maps() {
	
		$this->load_view("maps");
		$this->view->data->tab = 'maps';
		$this->view->publish();
	}
	
	public function kits() {
	
		$this->load_view("kits");
		$this->view->data->tab = 'kits';
		$this->view->publish();
	}
	
	public function rules() {
	
		$this->load_view("rules");
		$this->view->data->tab = 'rules';
		$this->view->publish();
	}
    
    public function shop() {
	
		$this->load_view("shop");
		$this->view->data->tab = 'shop';
		$this->view->publish();
	}
    
    public function tos() {
	
		$this->load_view("tos");
		$this->view->data->tab = 'tos';
		$this->view->publish();
	}
	
	public function logout() {
		Session::destroy();
		Router::sendTo();
	}

	public function maintenance() {
		$this->load_view('maintenance');
		$this->view->publish();
	}
}