<?php

header("Connection: close"); // connection need to be closed since by default is kept alive , it will interfere with the api call from the android app
    require_once  './api_class.php';

    $api = new api_class();
   
    if($_SERVER['REQUEST_METHOD'] == "POST"){

        
        if(isset($_POST['user_id']) && isset($_POST['smart_contract']) && isset($_POST['set_default_device'])) // settings device as default
        {
            $api->remove_device_as_default($_POST['user_id']);
            $api->update_device_default($_POST['user_id'],$_POST['smart_contract']);
            return;
        }

        if(isset($_POST['user_id']) && isset($_POST['remove_default_device'])) // removing device as default
        {
            $api->remove_device_as_default($_POST['user_id']);
            return;
        }

        if(isset($_POST['user_id']) && isset($_POST['retrieve_device_as_default'])) // getting the default device
        {
            $api->get_default_device($_POST['user_id']);
            return;
        }


    }
?>