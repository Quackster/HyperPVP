<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>HyperPVP - Home</title>
    
		<?php $this->inc("base/style"); ?>
	
  </head>
  <body>
  
    <div class="container">

		<?php
		$this->inc("base/links");
		?>
	
	<hr>

    <div class="row">

		<div class="span12">
	
        <!-- <div class="alert alert-block">
            <h4 class="alert-heading">Warning!</h4>
            <p>	All servers are in beta, bugs may occur!</p>
        </div> -->

		  </div>
	
	<div class="span3">
		<table class="table table-bordered">
			<tr>
				<td>Servers</td>
			</tr>
			
			<?php

			foreach (SiteDao::getServers() as $server) {

				//$array = file_get_contents("http://api.iamphoenix.me/status/?server_ip=" . $server->bungee_name . ".hyperpvp.us");
				//$json = json_decode($array);
				//nline = $json->status == "true";
				//$online = @fsockopen($server->bungee_name . ".hyperpvp.us", "25565"); 
                
                $online = true;

				if ($online) { 
					echo "<tr><td>IP</td><td style='background-color:#D6FFD6'>" . $server->bungee_name . ".hyperpvp.us</td></tr>";
				} else {
				
					R::exec("DELETE FROM servers_users WHERE server_id = '" . $server->bungee_name . "'");
					echo "<tr><td>IP</td><td style='background-color:#FFB2B2'>" . $server->bungee_name . ".hyperpvp.us</td></tr>";
				}
			}
			
			?>
			
		</table>
	</div>
	
	<div class="span9">
		<table class="table table-bordered">
			<tr>
				<td>Users Online</td>
			</tr>
			
			<?php
 
				foreach (SiteDao::getServers() as $server) {
   
			?>
			
			<tr>
				<td>
				<p><span class="label label-<?php
				
				if ($server->status == 1) {
					echo "important";
				} 
				if ($server->status == 2) {
					echo "warning";
				}
				if ($server->status == 3) {
					echo "success";
				}
				?>"><?php echo $server->name; ?></span>  <span class="label label-info">[<?php echo $server->current_type; ?>] <?php echo $server->current_name; ?></span>  <span class="label"><?php echo $server->mins_left; ?> minutes left until next map!</span></p>
				
						<?php
						
						$i     = 0;
						$shown = 0;
						
						foreach (SiteDao::getUsers($server->bungee_name) as $user) {
							
							$i++;
							
							if ($i < 46) {
								
								$shown++;
								
						?>
				
							<a href='/profile/<?php echo $user->user; ?>' rel='tooltip' style='display: inline-block;' title='<?php echo $user->user; ?>'>
							<img class='avatar' src='https://minotar.net/helm/<?php echo $user->user; ?>/40' style='width: 40px; height: 40px; margin-bottom: 5px;'/>
							</a>
					
						<?php
							
							}
						}
							
						if ($i == 0) {
							echo "No users online.";
						}
								
						if ($i > 46) {
							
							?><span class="label label-warning">And <?php echo ($i - $shown); ?> More</span><?php
						}
				}

				?>
				
				</td>
			</tr>
		</table>
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