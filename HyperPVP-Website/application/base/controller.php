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

class Controller {
	protected $model;
	protected $view;

	public function __construct() {
		R::setup('mysql:host=' . Sierra::getConfig()->db->host . ';dbname=' . Sierra::getConfig()->db->database, Sierra::getConfig()->db->username, Sierra::getConfig()->db->password);
		R::setStrictTyping(false);
		if(!Sierra::getConfig()->db->development_mode) {
			R::freeze(true);
		}

		if(Session::isAuthed()) {
			if(Session::auth()->ip != $_SERVER['REMOTE_ADDR']) {
				$user = R::load('users', Session::auth()->id);
				//$user->ip = $_SERVER['REMOTE_ADDR'];

				//R::store($user);
			}

			/*if(BanDao::exists('value', Session::auth()->username)) {
				die("<h1>Banned</h1>");
			}*/
		}
	}

	public function getModel($model) {
		$model_name = $model . '_model';

		if(file_exists(APP_PATH . '/models/' . $model_name . '.php')) {
			require APP_PATH . '/models/' . $model_name . '.php';
		} else {
			die("Oops! Failed to locate the model: " . $model_name . ", make sure it exists in the models directory.");
		}

		$this->model = new $model_name();
	}

	public function getFooter() {
		$tpl = new View('footer');
        $this->view->bind('footer', $tpl->get());
	}

	public function load_view($view) {
		$this->view = new View($view);
	}
}