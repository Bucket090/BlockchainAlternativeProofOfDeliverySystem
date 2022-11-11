<?php

    header("Connection: close"); // connection need to be closed since by default is kept alive , it will interfere with the api call from the android app
    require_once  './api_class.php';

    $api = new api_class();
   
    if($_SERVER['REQUEST_METHOD'] == "POST"){

        $email = $_POST['email'];
        $pass = $_POST['password'];
        $role = $_POST['role_name'];
        $name = $_POST['name'];
        $surname = $_POST['surname'];
        $gender = $_POST['gender'];
        $dob = $_POST['dob'];
        $mobile = $_POST['mobile'];
        $flat_no = $_POST['flat_no'];
        $street_no = $_POST['street_no'];
        $street_name = $_POST['street_name'];
        $locality = $_POST['locality'];
        $country = $_POST['country'];
        $crypto_addr = $_POST['crypto_address'];

        if(isset($_POST['add_new_user']) == "OK")
            $api->add_User($name,$surname,$email,$pass,$gender,$dob,$mobile,$flat_no,$street_no,$street_name,$locality,$country,$crypto_addr,$role);
        if(isset($_POST['update_user']) == "OK"){
            $user_id = $_POST['user_Id'];
            $api->update_Owner($name,$surname,$email,$gender,$dob,$mobile,$flat_no,$street_no,$street_name,$locality,$country,$crypto_addr,$user_id);
        }
    }
    

?>