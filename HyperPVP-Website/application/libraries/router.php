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

class Router
{
	/*
	 *	The array of all routes within the system.
	 */
	static $routes = array();

	/*
	 *	The error events (such as 404, etc)
	 */
	static $events = array(
			"404" => "error.notFound()",
			"403" => "error.notAllowed()",
			"ROUTE" => "error.unexpected()",
			"CONTROLLER" => "error.unexpected()"
		);

	/*
	 *	Post routes (The routes that are for POST requests only)
	 */
	static $posts = array();

	/*
	 *	The HTTP constants
	 */
	const HTTP_POST = 'post';

	/*
	 *	Adds a request to the routed list.
	 *		-- Default method is get.
	 */
	public static function add($caller, $listener, $method = 'get')
	{
		if($method === self::HTTP_POST)
		{
			self::$posts[] = $caller;
            self::$posts[] = $caller . "/";
		}

		self::$routes[$caller] = $listener;
		self::$routes[$caller . "/"] = $listener;
	}

	/*
	 *	 Checks to see if a request has been routed.
	 */
	public function routed($caller)
	{
		return (isset(self::$routes[$caller]));
	}

	/*
	 *	Returns the class.method name for a route (if it's been routed..)
	 */
	public function route($caller)
	{
		if (startsWith($caller, "/profile"))
		{
			$splitted = explode('.', "user.profile()");
		}
	
		if($this->routed($caller) || startsWith($caller, "/profile"))
		{
			if(in_array($caller, self::$posts))
			{
				if($_SERVER['REQUEST_METHOD'] != 'POST')
				{
					self::sendTo();
				}
			}

			if (!startsWith($caller, "/profile"))
			{
				$splitted = explode('.', self::$routes[$caller]);
			}
			
			$controller = $splitted[0];
			$method = substr($splitted[1], 0, -2);

		
			if(file_exists(APP_PATH . '/controllers/' . $controller . '_controller.php')) {
				
				require_once APP_PATH . '/controllers/' . $controller . '_controller.php';
				
				if(startsWith($controller, "housekeeping/")) {
					$controller = str_replace("housekeeping/", "", $controller);
				}
				
				$ctrl = new $controller;

				if(method_exists($controller, $method))
				{
					$ctrl->{$method}();
					die();
				}
				else
				{
					return self::$events["ROUTE"];
				}
			} else {
				return self::$events["CONTROLLER"];
			}
		}
		else
		{
			return self::$events["404"];
		}

		return self::$events["404"];
	}

	/*
	 *	Routes you to the chosen error.
	 */
	public function routeError($caller)
	{
		$splitted = explode('.', $caller);
		$controller = $splitted[0];
		$method = substr($splitted[1], 0, -2);

		require_once APP_PATH . '/controllers/' . $controller . '_controller.php';
		$ctrl = new $controller;

		if(method_exists($controller, $method))
		{
			$ctrl->{$method}();
		}
		else
		{
			die("An unexpected error, related to: '<i>" . $caller . "</i>' has occured. Please contact the site administrator.");
		}
	}

	/*
	 *	Sends the client to a specific route.
	 */
	public static function sendTo($route = null)
	{
		if($route == null) {
			header('Location: ' . URL);
		} else {
			header('Location: ' . URL . '/' . $route);
		}

		die();
	}
}