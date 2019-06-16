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

class Sierra {

	public static $config;
	public static $session;
	public $router;

	public function __construct() {
		$this->init();
		$this->fetch_files();

		define("URL", Sierra::getConfig()->site->url);

		$this->router = new Router();
		$this->doRoute();
	}

	public function init() {
		Sierra::$config = new StdClass();
		Sierra::$config->db = new StdClass();
		Sierra::$config->site = new StdClass();
		Sierra::$config->user = new StdClass();
		Sierra::$config->security = new StdClass();
		Sierra::$session = new StdClass();
		
		foreach($_SESSION as $key => $value) {
			if(is_array($value)) {
				Sierra::getSession()->$key = new StdClass();

				foreach($value as $skey => $svalue) {
					Sierra::getSession()->$key->$skey = $svalue;
				}
			} else {
				Sierra::getSession()->$key = $value;
			}
		}
	}

	public function doRoute() {

		if(isset($_GET['do'])) {
			if(empty($_GET['do'])) {
				$route = 'index';
			} else {
				$route = $_GET['do'];
			}
		} else {
			$route = 'index';
		}

		$process = $this->router->route('/' . $route);

		if($process) {
			$this->router->routeError($process);
		}
	}

	public function fetch_files() {

		// Config
		require_once "config.php";

		// Libraries
		require_once "application/libraries/router.php";
		require_once "application/libraries/session.php";
		require_once "application/libraries/functions.php";
		require_once "application/libraries/form.php";

		// MVC Libraries
		require_once "application/base/model.php";
		require_once "application/base/view.php";
		require_once "application/base/controller.php";

		// External Libraries
		require_once "application/external/redbean.php";
		require_once "application/external/recaptchalib.php";

		// Router Config
		require_once "routes.php";

		// Data Access Objects (dao)
		require_once "application/dao/user.php";
		require_once "application/dao/site.php";
		require_once "application/dao/friend.php";
	}

	public static function getConfig() {
		return Sierra::$config;
	}

	public static function getSession() {
		return Sierra::$session;
	}
}