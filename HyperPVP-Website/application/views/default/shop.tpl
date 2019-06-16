<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>HyperPVP - Top Ranks</title>
	
	<?php $this->inc("base/centered_style"); ?>
	
  </head>
  <body>
  
    <div class="container">

		<?php
			$this->inc("base/links");
		?>
	
		<hr>
	
        <div class='page-header'>
            <h1><small>Support Hyper PVP with purchasing a subscription.</small></h1>
        </div>

        <div class='row'>
            <div class='span8'>
                <h4>Welcome to our store</h4>
                <p>Welcome to the Hyper PVP Donator store! This is where you can buy donator ranks which will give you perks in-game. We do not have any packages that will give you an advantage over other players but this is more a sign of appreciation from you towards the server.</p>
                <p>We want Hyper PVP players to have the best experience possible but all this comes at a cost. Our donators are the ones who help us run this great server by providing us with funds to pay for everything. Without our amazing donators we couldn't continue Hyper PVP.</p>
                <p>Donating is not necessarily to gain these perks but more to help out the server. All payments are in USD and are non refundable.</p>
                <p>If you would like an upgrade, email hypermcpvp@gmail.com with your following Minecraft name, your proof of purchasing the Wolf package and then you'll be required to pay another $5 only once.</p>
            </div>
            
            <div class='center span4'>
                <h4>Where does the money go?</h4>
                <p>So where is your money actually going?</p>
                <ul>
                    <li>Cost of our servers</li>
                    <li>Paying for advertising and promotion</li>
                    <li>Paying the developers</li>
                    <li>Licensing of software etc</li>
                    <li>Giveaways and events</li>
                </ul>
                <h4>Legend</h4>
                <p>What do you get in your packages?</p>
                <i class='icon-ok'></i> You will receive items with a tick</br>
                <i class='icon-remove'></i> You will not receive items with a cross</br>
            </div>
            <div class="clearfix"></div>
            <br />
            <p></p>
            
            <form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_blank">
            <div id="modalWolf" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                    <h3 id="myModalLabel">Wolf</h3>
                </div>
               <div class="modal-body">
                <input type="hidden" name="cmd" value="_xclick">
                <input type="hidden" name="business" value="alex.daniel.97@gmail.com">
                <input type="hidden" name="lc" value="US">
                <input type="hidden" name="item_name" value="Wolf">
                <input type="hidden" name="amount" value="5.00">
                <input type="hidden" name="currency_code" value="USD">
                <input type="hidden" name="button_subtype" value="services">
                <input type="hidden" name="no_note" value="0">
                <input type="hidden" name="bn" value="PP-BuyNowBF:btn_buynowCC_LG.gif:NonHostedGuest">
                <p>Enter your Minecraft username</p>
                <table>
                    <tr>
                        <td>
                        <input type="hidden" class="username" name="on0" value="Username">
                        </td>
                    </tr>
                    <tr>
                        <td>
                        <input type="text" class="username" style="width: 270px" name="os0" maxlength="200">&nbsp;&nbsp;&nbsp;&nbsp;
                        </td>
                        <td><img src="http://i.imgur.com/60t4EFa.png" alt="The wolf" style="width: 60%; height: 60%"></td>
                    </tr>
                </table>
                <small><p class="muted">(Be aware by clicking submit you are declaring that you agree to the Terms of Service)</p></small>
                <p class="text-warning">Please double check your name before purchase.</p>
                </div>
                <div class="modal-footer">
                    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
                    <input class="btn btn-primary" type="submit" value="Purchase">
                </div>
            </div>
            </form>
            
            <form action="https://www.paypal.com/cgi-bin/webscr" method="post" target="_top">
            <div id="modalSpider" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">

                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                    <h3 id="myModalLabel">Spider</h3>
                </div>
               <div class="modal-body">
                <input type="hidden" name="cmd" value="_xclick">
                <input type="hidden" name="business" value="alex.daniel.97@gmail.com">
                <input type="hidden" name="lc" value="US">
                <input type="hidden" name="item_name" value="Spider">
                <input type="hidden" name="amount" value="10.00">
                <input type="hidden" name="currency_code" value="USD">
                <input type="hidden" name="button_subtype" value="services">
                <input type="hidden" name="no_note" value="0">
                <input type="hidden" name="bn" value="PP-BuyNowBF:btn_buynowCC_LG.gif:NonHostedGuest">
                <p>Enter your Minecraft username</p>
                <table>
                    <tr>
                        <td>
                        <input type="hidden" class="username" name="on0" value="Username">
                        </td>
                    </tr>
                    <tr>
                        <td>
                        <input type="text" class="username" style="width: 270px" name="os0" maxlength="200">&nbsp;&nbsp;&nbsp;&nbsp;
                        </td>
                        <td>&nbsp;&nbsp;&nbsp;&nbsp;<img src="http://i.imgur.com/heSOuZj.png" alt="The spider" style="width: 50%"></td>
                    </tr>
                </table>
                <small><p class="muted">(Be aware by clicking submit you are declaring that you agree to the Terms of Service)</p></small>
                <p class="text-warning">Please double check your name before purchase.</p>
                </div>
                <div class="modal-footer">
                    <button class="btn" data-dismiss="modal" aria-hidden="true">Close</button>
                    <input class="btn btn-primary" type="submit" value="Purchase">
                </div>
            </div>
            </form>
            
            <div class="span4">
                <table class="table table-bordered">
                <tr><td style='background-color:#FFFF94'><h3>Wolf <small>$5 one-time payment</small></h3></td></tr>
                    <tr>
                        <td>
                        <p><i class='icon-ok'></i> Join full teams</p>
                        <p><i class='icon-ok'></i> VIP prefix in pvp chat</p>
                        <p><i class='icon-ok'></i> VIP prefix in lobby chat</p>
                        <p><i class='icon-ok'></i> Rank on website</p>
                        <p><i class='icon-remove'></i> Join any team</p>
                        <p><i class='icon-remove'></i> Portable workbench</p>
                        </td>
                    </tr>
                    <tr><td><center><button href="#modalWolf" role="button" class="btn btn-info" data-toggle="modal" type="button">Buy Wolf</button></center></td></tr>
                    </table>
            </div>

            <div class="span4">
                <table class="table table-bordered">
                <tr><td style='background-color:#B8DBFF'><h3>Spider <small>$10 one-time payment</small></h3></td></tr>
                    <tr>
                        <td>
                        <p><i class='icon-ok'></i> Join full teams</p>
                        <p><i class='icon-ok'></i> VIP prefix in pvp chat</p>
                        <p><i class='icon-ok'></i> VIP prefix in lobby chat</p>
                        <p><i class='icon-ok'></i> Rank on website</p>
                        <p><i class='icon-ok'></i> Join any team</p>
                        <p><i class='icon-ok'></i> Portable workbench (/bench)</p>
                        </td>
                    </tr>
                    <tr><td><center><button href="#modalSpider" role="button" class="btn btn-info" data-toggle="modal" type="button">Buy Spider</button></center></td></tr>
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
    
    <script>
        $(document).ready(function () {
            var hash = window.location.hash;
                if (hash == "#wolf") {
                 $('#modalWolf').modal();
                }
                
                if (hash == "#spider") {
                 $('#modalSpider').modal();
                }
            }
        );
    </script>

  </body>
</html>