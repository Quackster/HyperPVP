<?php
/*
     /'\_/`\                                    
    /\      \     __      ___      __     ___   
    \ \ \__\ \  /'__`\  /' _ `\  /'_ `\  / __`\ 
     \ \ \_/\ \/\ \L\.\_/\ \/\ \/\ \L\ \/\ \L\ \
      \ \_\\ \_\ \__/.\_\ \_\ \_\ \____ \ \____/
       \/_/ \/_/\/__/\/_/\/_/\/_/\/___L\ \/___/ 
                                   /\____/      
                                   \_/__/       
    @author Leon Hartley
*/

class View
{    
    public $contents;
    public $template_name;

    public $params = array();
    public $template;
    
    public $data;

    public function __construct($template) {
        $this->data = new StdClass();
        $this->template = $template;
        
        $this->grabTemplate();
    }

    public function bind($key, $value) {
        $this->params[$key] = $value;
        return true;
    }

    public function setDefaults() {

        //$this->bind('users-online', UserDao::usersOnline());

        foreach(get_object_vars(Sierra::getConfig()->site) as $key => $value) {
            $this->bind("site->" . $key, $value);
        }

			//$this->bind("adverts", "Powered by novaCMS 3.0-Sierra 'Sierra' edition");

        if(Session::isAuthed()) {
        
            $this->bind('user->username', Session::auth()->username);
            /*$this->bind('user->credits', number_format(Session::auth()->credits));
            $this->bind('user->pixels', number_format(Session::auth()->pixels));
            $this->bind('user->figure', Session::auth()->figure);
            $this->bind('user->mission', Session::auth()->mission);
            $this->bind('user->email', Session::auth()->email);
            $this->bind('user->last_online', Session::auth()->	last_online);
            $this->bind('user->points', number_format(Session::auth()->pixels));*/
        }
    }
    
    public function render() {
        $this->setDefaults();
        
        foreach($this->params as $key => $value) {
            if(is_array($value)) {
                // do nothing.
            } else {
                $this->contents = str_ireplace('{$' . $key . '}', $value, $this->contents);
            }
        }
		
        return $this->contents;
    }
    
    public function grabTemplate() {
        $path = APP_PATH . "/views/" . Sierra::getConfig()->site->template . '/' . $this->template.".tpl";
        
        if(file_exists($path)) {
            $this->contents = file_get_contents($path);
        }
        else {
            die("<h2>Fatal Error</h2> Failed to grab the template: " . $this->template);
        }
    }

    public function get() {
        $this->render();
        return $this->contents;
    }
    
    public function publish() {
        $this->render();
        eval("?> " . $this->contents . "<?php ");
    }

    public function inc($tpl) {
        $v = new View($tpl);
        $v->data = $this->data;
        
        $v->publish();
    }
}