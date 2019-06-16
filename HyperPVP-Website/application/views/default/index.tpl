<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>HyperPVP - Home</title>
	
	<?php $this->inc("base/style"); ?>
	

  <body>
  
    <div class="container">

		<?php $this->inc("base/links"); ?>
	
      <hr>

		<?php if (Session::isAuthed() && count(FriendDao::getFriendRequests(Session::auth()->id)) != 0) { ?>

		<div class="alert alert-info" style="text-decoration: none">
		<strong>You have friend requests!</strong>
		You have a total of <strong><?php echo count(FriendDao::getFriendRequests(Session::auth()->id)); ?></strong> friend requests. View your friend requests <a href="{$site->url}/friend/requests"><button class="btn btn-primary btn-mini" type="button">click here</button></a>
		</div>

		<?php } ?>
	  
      <?php
      
        $server = SiteDao::getServer("thor");
        
        $image = $server->current_name;
      
      
      ?>
      
      <div class="hero-unit" style="background: url('public/images/maps_lrge/<?php echo $image; ?>.png'); color: white">
        <h1>Welcome!</h1>
        <p class="lead">When you join the server, you have objectives and you kill players on the other teams.</p>
		<p class="lead">It's either kill or be killed.</p>
		<a class="btn btn-large btn-info" href="{$site->url}/servers">Do you have what it takes?</a>
      </div>
      <hr>

	  </div>
	  <div class="container">
	  
      <div class="row-fluid marketing">
        <div class="span6">
          <h4>Spectate Mode</h4>
          <p>You're in creative mode with the ability to fly around the place, watch others play before you want to play yourself.</p>
		  
          <h4>Objectives</h4>
          <p>These are the things that help you win. Connect to learn more about DTC, DTM, TDM and FFA.</p>

        </div>

        <div class="span6">
          <h4>Statistics</h4>
          <p>Helps keep track of your kills and deaths.</p>

		  <h4>Maps</h4>
          <p>Random maps are in rotation when playing Paintball PVP. All maps are custom made from the users who play Paintball PVP.</p>
		
		</div>
      </div>

      <hr>

		<?php
		$this->inc("base/footer");
		?>

    </div> <!-- /container -->
	
	<?php
	$this->inc("base/javascript");
	?>

  </body>
</html>