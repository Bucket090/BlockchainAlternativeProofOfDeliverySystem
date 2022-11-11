<?php

    header("Connection: close"); // connection need to be closed since by default is kept alive , it will interfere with the api call from the android app
    require_once  './api_class.php';

    $api = new api_class();
   
    if($_SERVER['REQUEST_METHOD'] == "POST"){

        if(isset($_POST['user_id']) && isset($_POST['message'])  && isset($_POST['smart_contract']) && isset($_POST['add_delivery_notification']) == "OK")
            $api->add_notification($_POST['user_id'],$_POST['message'],$_POST['smart_contract']);
        
        if(isset($_POST['user_id']) && isset($_POST['remove_delivery_notification']) == "OK")
            $api->delete_notification($_POST['user_id']);
        
        if(isset($_POST['user_id']) && isset($_POST['get_delivery_notification']) == "OK")
            $api->get_delivery_notifications($_POST['user_id']);
    }
    

?>