<?php

header("Connection: close"); // connection need to be closed since by default is kept alive , it will interfere with the api call from the android app
    require_once  './api_class.php';

    $api = new api_class();
   
    if($_SERVER['REQUEST_METHOD'] == "POST"){

        if(isset($_POST['user_data_profile']))
        {
            if(isset($_POST['email'])) // getting all the user details for user profile layout
            {
                if($_POST['user_data_profile'] == true)
                    $api->getting_UserDetails($_POST['email']);
                return;
            }
        }

        if(isset($_POST['email']) && isset($_POST['courier_data_profile'])) // getting all the courier details for user courier profile layout
        {
            $api->getting_UserDetails_Courier($_POST['email']);
            return;
        }

        
        if(isset($_POST['user_id']) && isset($_POST['get_courier_details'])) // getting courier details 
        {
            $api->get_courier_employer_details($_POST['user_id']);
            return;
        }

        if(isset($_POST['email'])) // checks if email exists for create account part 6 layout (email layout)
        {
            $api->email_Exists($_POST['email']);
            return;
        }        
    }

?>