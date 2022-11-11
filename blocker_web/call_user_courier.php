<?php

    header("Connection: close"); // connection need to be closed since by default is kept alive , it will interfere with the api call from the android app
    require_once  './api_class.php';

    $api = new api_class();
   
    if($_SERVER['REQUEST_METHOD'] == "POST"){

        $email = $_POST['email'];
        $role = $_POST['role_name'];

        if(isset($_POST['add_new_user_courier']) == "OK")
            $api->add_Courier($email,"Maltapost",$role);
    }
    

?>