<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>HyperPVP - Maps</title>
	
	<?php $this->inc("base/style"); ?>
	
  </head>
  <body>
  
    <div class="container">

		<?php
			$this->inc("base/links");
		?>
	
		<hr>
	
		<h3>Maps <small>Below are the maps on the server that users play on!</small></h3>

		<hr>
		

		<div class="row">
			<div class='span4'>
				<div class='map thumbnail'>
					<div class='img'>
						<img alt='' src='{$site->url}/public/images/maps/sandsofegypt.png'>
					</div>
					<div class='caption' style='text-align: center;'>
						<h1 class='lead'>Sands of Egypt</h1>
						<small>
						by
						<a href='{$site->url}/profile/_AlexM'>_AlexM</a>
						</small>
					</div>
				</div>
			</div>
			<div class='span4'>
				<div class='map thumbnail'>
					<div class='img'>
						<img alt='' src='{$site->url}/public/images/maps/suburbruins.png'>
					</div>
					<div class='caption' style='text-align: center;'>
						<h1 class='lead'>Suburb Ruins</h1>
						<small>
						by
						<a href='{$site->url}/profile/_AlexM'>_AlexM</a>
						</small>
					</div>
				</div>
			</div>
			<div class='span4'>
				<div class='map thumbnail'>
					<div class='img'>
						<img alt='' src='{$site->url}/public/images/maps/wasteland.png'>
					</div>
					<div class='caption' style='text-align: center;'>
						<h1 class='lead'>Wasteland</h1>
						<small>
						by
						<a href='{$site->url}/profile/_AlexM'>_AlexM</a>
						</small>
					</div>
				</div>
			</div>
		</div>
		<br>
		<div class="row">
			<div class='span4'>
				<div class='map thumbnail'>
					<div class='img'>
						<img alt='' src='{$site->url}/public/images/maps/thecliffs.png'>
					</div>
					<div class='caption' style='text-align: center;'>
						<h1 class='lead'>The Cliffs</h1>
						<small>
						by
						<a href='{$site->url}/profile/_AlexM'>_AlexM</a>
						</small>
					</div>
				</div>
			</div>
			<div class='span4'>
				<div class='map thumbnail'>
					<div class='img'>
						<img alt='' src='{$site->url}/public/images/maps/bloodforest.png'>
					</div>
					<div class='caption' style='text-align: center;'>
						<h1 class='lead'>Blood Forest</h1>
						<small>
						by
						<a href='{$site->url}/profile/_AlexM'>_AlexM</a>
						</small>
					</div>
				</div>
			</div>
			<div class='span4'>
				<div class='map thumbnail'>
					<div class='img'>
						<img alt='' src='{$site->url}/public/images/maps/lonelyforest.png'>
					</div>
					<div class='caption' style='text-align: center;'>
						<h1 class='lead'>Lonely Forest</h1>
						<small>
						by
						<a href='{$site->url}/profile/_AlexM'>_AlexM</a>
						</small>
					</div>
				</div>
			</div>
		</div>
        <br>
		<div class="row">
			<div class='span4'>
				<div class='map thumbnail'>
					<div class='img'>
						<img alt='' src='{$site->url}/public/images/maps/shroomville.png'>
					</div>
					<div class='caption' style='text-align: center;'>
						<h1 class='lead'>Shroom Ville</h1>
						<small>
						by
						<a href='{$site->url}/profile/_AlexM'>_AlexM</a>
						</small>
					</div>
				</div>
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