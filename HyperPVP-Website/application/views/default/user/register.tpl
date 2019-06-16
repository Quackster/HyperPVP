
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>HyperPVP - Register</title>
	
	<?php $this->inc("base/style"); ?>
	
  </head>
  <body>
  
    <div class="container">

		<?php
			$this->inc("base/links");
		?>
	
		<hr>
	
		<h3>Register</h3>

		<hr>
		

		<div class="row">
			<div class="span12">
			
			<h4>Enter your secret code.</h4>
			<br>
			<p>When you go on a PVP server, type the command <strong>/register</strong> and you should get a secret 5 code pin. Enter that in the box below along with your Minecraft username and desired account password, and you will be able to make an account.</p>
			<br>
			<form action="register/redeem" method="post">
				<div class="control-group">
					<div class="controls">
						<p><strong>Minecraft username</strong> <i></i></p>
						<input type="text" name="username">
						<p><strong>Password</strong> <i>(Your password needs to be at least 8 characters long)</i></p>
						<input type="password" name="password">
						<p><strong>Email</strong> <i>(Required if you forget your password)</i></p>
						<input type="text" name="email">
						<p><strong>PIN code</strong></p>
						<input type="text" name="pin" placeholder="PIN">
					</div>
				</div>
				<div class="control-group">
					<div class="controls">
						<button type="submit" class="btn">Redeem</button>
					</div>
				</div>
			</form>

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
