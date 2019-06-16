<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>HyperPVP - Top Ranks</title>
	
	<?php $this->inc("base/style"); ?>
	
  </head>
  <body>
  
    <div class="container">

		<?php
			$this->inc("base/links");
		?>
	
		<hr>
	
		<h3>Top Ranks <small>Below are the list of the top 20 users sorted by most kills.</small></h3>

		<hr>
		

		<div class="row">
			<div class="span12">
				
			<table class="table table-hover">
				<tr>
					<?php
						$this->inc("leaderboard/links");
					?>			
				</tr>
				
				<?php

                $count = 0;
                
				foreach (SiteDao::getTopRanks() as $rank) {
				
					$kills = 0;
					foreach (UserDao::getStatsNoLimit($rank->username, "kill") as $kill) {
						$kills++;
					}

					$deaths = 0;
					foreach (UserDao::getStatsNoLimit($rank->username, "death") as $death) {
						$deaths++;
					}
					
                    $count++;
                    
					//R::exec("UPDATE users SET kills = '" . $kills . "' WHERE username = '" . $rank->username . "'");
					//R::exec("UPDATE users SET deaths = '" . $deaths . "' WHERE username = '" . $rank->username . "'");
				
					echo "<tr>
                        <td>" . $count . "</td>
						<td><a href='{$site->url}/profile/" . $rank->username . "'>" . $rank->username . "</a></td>
						<td>" . $rank->kills . "</td>
						<td>" . $rank->deaths . "</td>
                        <td>" . $rank->leaked_core . "</td>
                        <td>" . $rank->broke_monument . "</td>
					</tr>";
				
				}
				
				?>
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