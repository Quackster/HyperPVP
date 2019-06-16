
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>HyperPVP - Friend Requests</title>
	
	<?php $this->inc("base/style"); ?>
	
  </head>
  <body>
  
    <div class="container">

		<?php
			$this->inc("base/links");
		?>
	
		<hr>
	
		<h3>Your friend requests!</h3>

		<hr>
		

		<div class="row">
			<div class="span12">
			
							
			<table class="table table-striped">
				<tr>
					<td><strong>Username</strong></td>
					<td><strong></strong></td>
					<td><strong></strong></td>
					<td><strong></strong></td>
				</tr>
				
				<?php 
				foreach (FriendDao::getFriendRequests(Session::auth()->id) as $friend) {		
					$row = UserDao::getId($friend->sender);
					?> 
				
					<tr>
						<td><a href='{$site->url}/profile/<?php echo $row->username; ?>'><?php echo $row->username; ?></a></td>
						<td><a href='{$site->url}/profile/<?php echo $row->username; ?>' rel='tooltip' style='display: inline-block;' title='<?php echo $row->username; ?>'><img class='avatar' src='https://minotar.net/helm/<?php echo $row->username; ?>/32.png' player='<?php echo $row->username; ?>' style='width: 32px; height: 32px; margin-bottom: 2px; margin-left: 2px; margin-right: 2px;'/></a>	</td>
						<td><a href="{$site->url}/friend/request/accept?id=<?php echo $friend->id; ?>"><button class="btn btn-success" type="button">Accept</button></a></td>
						<td><a href="{$site->url}/friend/request/deny?id=<?php echo $friend->id; ?>"><button class="btn btn-warning" type="button">Deny</button></a></td>
					</tr>
				
				<?php } ?>
			</table>


			</div>
		</div>
	
		<hr>
	  
		<?php
		$this->inc("base/footer");
		?>
	  
    </div>
	
	<?php
	$this->inc("base/javascript");
	?>

  </body>
</html>
