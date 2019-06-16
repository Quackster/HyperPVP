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

class Ranks extends Controller {
	public function __construct() {
		parent::__construct();
	}

	public function kills() {
	
		$this->load_view("leaderboard/leaderboard_kills");
		$this->view->data->tab = 'ranks';
		$this->view->publish();
	}
    
    public function deaths() {
	
		$this->load_view("leaderboard/leaderboard_deaths");
		$this->view->data->tab = 'ranks';
		$this->view->publish();
	}
    
     public function core() {
	
		$this->load_view("leaderboard/leaderboard_core");
		$this->view->data->tab = 'ranks';
		$this->view->publish();
	}
    
     public function monument() {
	
		$this->load_view("leaderboard/leaderboard_monument");
		$this->view->data->tab = 'ranks';
		$this->view->publish();
	}
}