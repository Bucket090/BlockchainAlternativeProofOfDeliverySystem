<?php

header("Connection: close"); // connection need to be closed since by default is kept alive , it will interfere with the api call from the android app
    require_once  './api_class.php';

    $api = new api_class();
   
    if($_SERVER['REQUEST_METHOD'] == "POST"){

        if(isset($_POST['email']) && isset($_POST['device_id'])) // checking if device id is already registered
        {
            $api->device_exists($_POST['email'],$_POST['device_id']);
            return;
        }
        if(isset($_POST['user_id']) && isset($_POST['show_devices'])) // return list of devices of the user
        {
            $api->get_list_devices($_POST['user_id']);
            return;
        }

        if(isset($_POST['smart_contract'])  && isset($_POST['device_id']) && isset($_POST['delete_device'])) // remove device 
        {
            $api->delete_device($_POST['smart_contract'],$_POST['device_id']);
            return;
        }
    }
?>