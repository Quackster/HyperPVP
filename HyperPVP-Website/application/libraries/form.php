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

class Form
{
    public $fields = array();
    public $type;
    public $field;

    public function __construct($type, $fields)
    {
        $this->type = $type;
        $this->fields = $fields;
        $this->field = new StdClass();
    }
    public function produce()
    {
        foreach($this->fields as $field)
        {
            if($this->type == 'post') 
            {
                $this->field->$field = $_POST[$field];
            }
            elseif($this->type == 'get')
            {
                $this->field->$key = $_GET[$field];
            }
        }
    }
    public function clean()
    {
        foreach($this->fields as $field)
        {
            if($this->type == 'post')
            {
                $_POST[$field] = mysql_real_escape_string(htmlspecialchars($_POST[$field]));
            }
            elseif($this->type == 'get')
            {
                $_GET[$field] = mysql_real_escape_string(htmlspecialchars($_GET[$field]));   
            }
        }
    }
    public function check()
    {
        foreach($this->fields as $field)
        {
            if($this->type == 'post')
            {
                if(!isset($_POST[$field]))
                {
                    return false;
                }
                elseif(empty($_POST[$field]))
                {
                    return false;
                }
            }
            if($this->type == 'get')
            {
                if(!isset($_GET[$field]))
                {
                    return false;
                }
                elseif(empty($_GET[$field]))
                {
                    return false;
                }
            }
        }
        return true;
    }
}