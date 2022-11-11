<?php
    header("Connection: close"); // connection need to be closed since by default is kept alive , it will interfere with the api call from the android app
    require_once  './api_class.php';

    $api = new api_class();

    if($_SERVER['REQUEST_METHOD'] == "POST"){
        if(isset($_POST['email']) && isset($_POST['pass']) && isset($_POST["login_session"])) // checks if login credentials are valid (login layout)
        {
            if($_POST['login_session'] == true)
            $email = $_POST['email'];
            $pass = $_POST['pass'];
            $api->check_credentials($email,$pass);
        }

        if(isset($_POST['email']) &&  isset($_POST['login_session_verified'])) // getting user details once confirmed login
        {
            if($_POST['login_session_verified'] == true){
                $api->getting_UserinSession($_POST['email']);
                return;
            }
        }
    }

?>