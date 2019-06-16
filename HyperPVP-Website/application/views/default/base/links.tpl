    <script type="text/javascript" src="{$site->url}/public/js/enhance.js"></script>
    
    <script type="text/javascript">
    function d() {
        var myTarget = document.getElementById("message");
        myTarget.innerHTML = ("<div class='alert alert-error'><strong>Oh dear!</strong> Please turn off AdBlock, we don't run ads on our site, but we appreciate if you didn't use it on our website.</div>");
    }
    function e() {

    }
    var _abdDetectedFnc = 'd';
    var _abdNotDetectedFnc = 'e';
    </script>
    
        <div id="message"></div>
    
    <div class="header">
    <ul class="nav nav-pills pull-right">

    <?php if (!isset($this->data->tab)) {
    $this->data->tab = "null";
    } ?>

    <li <?php if ($this->data->tab == "index") { echo 'class="active"'; } ?>><a href="{$site->url}/home">Home</a></li>
    <li <?php if ($this->data->tab == "servers") { echo 'class="active"'; } ?>><a href="{$site->url}/servers">Server List</a></li>		  
    <li <?php if ($this->data->tab == "ranks") { echo 'class="active"'; } ?>><a href="{$site->url}/leaderboard">Leaderboard</a></li>
    <li <?php if ($this->data->tab == "shop") { echo 'class="active"'; } ?>><a href="{$site->url}/shop">Shop</a></li>
    <li <?php if ($this->data->tab == "kits") { echo 'class="active"'; } ?>><a href="{$site->url}/maps">Maps</a></li>

	
	<?php
	if (Session::isAuthed()) {
	?>
	
	<li <?php if ($this->data->tab == "logout") { echo 'class="active"'; } ?>><a href="{$site->url}/logout">Logout</a></li>
	
	<?php 
	} else {
	?>
	
	<li <?php if ($this->data->tab == "login") { echo 'class="active"'; } ?>><a href="{$site->url}/login">Login</a></li>
	<li <?php if ($this->data->tab == "register") { echo 'class="active"'; } ?>><a href="{$site->url}/register">Register</a></li>
	
	<?php } ?>
	

    </ul>
    <h3 class="muted">Hyper PVP</h3>

    </div>     
