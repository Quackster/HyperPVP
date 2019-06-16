
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Hyper PVP - Recent Connected</title>
	
	<?php $this->inc("base/style"); ?>
	
  </head>
  <body>
  
    <div class="container">

		<?php
			$this->inc("base/links");
		?>
	
		<hr>
	
		<h3>Recently connected.</h3>

		<hr>
		

		<div class="row">
			<div class="span12">
			
			<?php
		
			$rows = UserDao::loadStdClass(R::getAll('SELECT * FROM `users` WHERE `last_online` <> 0 ORDER BY last_online DESC LIMIT 50'));

			foreach ($rows as $row) {
			
				echo "<p><img class='avatar' src='https://minotar.net/helm/$row->username/16' player='$row->username' size='16' width='16' height='16' style='width: 16px; height: 16px; '/> <a href='{$site->url}/profile/$row->username'>$row->username</a> connected to the server " . humanTiming($row->last_online) . " ago";
			}
			
			?>

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
