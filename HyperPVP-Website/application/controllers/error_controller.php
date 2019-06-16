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

class Error extends Controller {
	public function __construct() {
		parent::__construct();

		$this->load_view('error');
	}

	public function notFound() {
		$this->view->bind("error->title", "File not found");
		$this->view->bind("error->message", "I'm sorry. The file you requested could not be found.");

		$this->view->publish();
	}

	public function unexpected() {
		$this->view->bind("error->title", "Unexpected error");
		$this->view->bind("error->message", "It seems we've encountered an unexpected error. Please contact the site administrator and/or try again later.");

		$this->view->publish();
	}
}