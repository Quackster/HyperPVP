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

Router::add('/home', 'index.base()');
Router::add('/index', 'index.base()');
Router::add('/servers', 'index.servers()');
Router::add('/maps', 'index.maps()');
Router::add('/command', 'command.handle()');
Router::add('/leaderboard', 'ranks.kills()');
Router::add('/leaderboard/kills', 'ranks.kills()');
Router::add('/leaderboard/deaths', 'ranks.deaths()');
Router::add('/leaderboard/core', 'ranks.core()');
Router::add('/leaderboard/monument', 'ranks.monument()');
Router::add('/rules', 'index.rules()');
Router::add('/shop', 'index.shop()');
Router::add('/avatar', 'misc.avatar()');
Router::add('/profile', 'user.profile()');
Router::add('/recent', 'user.recent()');
Router::add('/tos', 'index.tos()');

Router::add('/register', 'user.register()');
Router::add('/register/redeem', 'user.redeem()');
Router::add('/login', 'user.login()');
Router::add('/logout', 'user.logout()');

Router::add('/friend/requests', 'friend.requests()');
Router::add('/friend/request/accept', 'friend.accept()');
Router::add('/friend/request', 'friend.request()');
Router::add('/friend/request/deny', 'friend.deny()');
Router::add('/friend/remove', 'friend.remove()');

Router::add('/api/server_list', 'api.servers()');
Router::add('/api/server_data', 'api.serverData()');
Router::add('/api/player_data', 'api.playerData()');
Router::add('/api/player_stats', 'api.playerStats()');

//INSERT INTO `users_friendrequest` (`id`, `sender`, `receiver`) VALUES (NULL, '2', '1');