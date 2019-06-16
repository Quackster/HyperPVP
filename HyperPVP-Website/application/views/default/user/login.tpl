
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>HyperPVP - Login</title>
	
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
			
			<h4>Login</h4>
			<br>
			<p>Haven't registered? Go on any server and type <strong>/register</strong> and you'll get a pin to register with <a href="{$site->url}/register">here</a>.</p>
			<br>
			
			<form method="post">
			<table style="padding: 20px">
				<tr>
					<td style="padding: 10px">Username</td>
					<td><input type="text" name="username" placeholder="Username"></td>
				</tr>
				<tr>
					<td style="padding: 10px">Password</td>
					<td><input type="password" name="password" placeholder="Password"></td>
				</tr>
				<tr>
					<td></td>
					<td><input type="submit" class="btn" value="Sign in"></td>
				</tr>
			</table>
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
