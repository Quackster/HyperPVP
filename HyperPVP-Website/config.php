<?php /*
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

Sierra::getConfig()->db->host = '127.0.0.1';
Sierra::getConfig()->db->username = 'root';
Sierra::getConfig()->db->password = '123456';
Sierra::getConfig()->db->database = 'hyperpvp';
Sierra::getConfig()->db->development_mode = false;

Sierra::getConfig()->site->template = 'default';
Sierra::getConfig()->site->url = 'http://127.0.0.1';
Sierra::getConfig()->site->name = 'Hyper PVP';