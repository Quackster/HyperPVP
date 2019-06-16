	  <div class="footer">

		

		<div class="masthead">

		<ul class="nav nav-pills pull-left inverse">

		HyperPVP - 2014

		</ul>

        <ul class="nav nav-pills pull-right inverse">

		

			<?php if (!isset($this->data->tab)) {

				$this->data->tab = "null";

			} ?>

		

		  <li <?php if ($this->data->tab == "rules") { echo 'class="active"'; } ?>><a href="{$site->url}/rules">Rules</a></li>
           <li <?php if ($this->data->tab == "tos") { echo 'class="active"'; } ?>><a href="{$site->url}/tos">TOS</a></li>

        </ul>

      </div>     		

      </div>
      