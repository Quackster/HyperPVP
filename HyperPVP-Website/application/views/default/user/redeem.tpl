
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>HyperPVP - Redeem</title>
	
	<?php $this->inc("base/style"); ?>
	
  </head>
  <body>
  
    <div class="container">

		<?php
			$this->inc("base/links");
		?>
	
		<hr>
	
		<h3>Success!</h3>

		<hr>
		

		<div class="row">
			<div class="span12">
			
			<h4>Please keep these details!</h4>
			<br>
			<p>You have now been a Paintball PVP account. Please note that your username upon logging in is your Minecraft account!</p>
			<p>Once you leave this page you won't be able to get these details back unless you save them!</p>
			
			<br>
			<table>
			<tr>
				<td><strong>Username</strong>{$buffer}</td>
				<td>{$redeem->username}</td>
			</tr>
			<tr>
				<td><strong>Password</strong>{$buffer}</td>
				<td>{$redeem->password}</td>
			</tr>
			</table>
			<br>
			<p>To log in, go <a href="{$site->url}/login">here</a>
			
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
