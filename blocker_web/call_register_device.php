<?php
    header("Connection: close"); // connection need to be closed since by default is kept alive , it will interfere with the api call from the android app
    require_once  './api_class.php';

    $api = new api_class();
   
    if($_SERVER['REQUEST_METHOD'] == "POST"){
        $user_id = $_POST['User_Id'];
        $device = $_POST['device_id'];
        $contract = $_POST['contract'];

        if(isset($_POST['register_device']) == "OK")
            $api->add_user_device($user_id,$device,$contract);
    }
?>