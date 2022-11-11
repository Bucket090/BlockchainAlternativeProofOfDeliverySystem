<?php

require "./connect.php";
class api_class{

    public $response;
    public $email;
    public $pass;

    // getting owner profile details
    function get_Profile($email){
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("SELECT uc.User_Id, First_Name, Last_Name, Gender,DOB, Mobile, Flat_No, Street_No, Street_Name, Locality, Country, Crypto_Address  FROM user_profile up INNER JOIN user_credential uc ON up.User_Id = uc.User_Id WHERE uc.Email =:email");
            $stmt->bindParam(":email",$email);
            $stmt->execute();
            $data = array();
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                $data[] = $row;
            }
        echo json_encode($data);
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }
    
    // This function returns the user id once the user's credentials are created. 
    function get_Profile_User_Id($email){
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("SELECT User_Id FROM user_credential WHERE Email =:email");
            $stmt->bindParam(":email",$email);
            $stmt->execute();
            $result = $stmt->fetchColumn();
            
            return $result;        
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }
    // getting roles 
    function get_role($role){
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("SELECT Role_ID FROM role WHERE Role_Name = :role");
            $stmt->bindParam(":role",$role);
            $stmt->execute();
            $result = $stmt->fetchColumn();
            
            return $result;        
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }
        // getting owner profile details
        function get_list_devices($user_id){
            try {
                $connect_db = new db_connect();
                $stmt = $connect_db->connect()->prepare("SELECT registered_device_id, smart_contract FROM registered_devices WHERE User_Id = :user");
                $stmt->bindParam(":user",$user_id);
                $stmt->execute();
                $data = array();
                while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                    $data[] = $row;
                }
            echo json_encode($data);
            }
            catch(PDOException $e){
                echo "Error: " . $e->getMessage();
            }
            $connect_db = null;
        }
    //adding a new user to the db
    function add_User($first_name,$last_name,$email,$password,$gender,$dob,$mobile,$flat_No,$street_No,$street_name,$locality,$country,$crypto_address,$role_name){
        $this->response = array();
        try {
            $role = $this->get_role($role_name);

            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("INSERT INTO user_credential(Email,Password, Role_Id) VALUES (:email,:pass,:user_role)");
            $stmt->bindParam(":email",$email);
            $stmt->bindParam(":pass",$password);
            $stmt->bindParam(":user_role",$role);
            $stmt->execute();
            
            //getting the user id based upon email
            $user_id = $this->get_Profile_User_Id($email);

            // Inserting data into User Profile after credentials are created
            $stmt = $connect_db->connect()->prepare("INSERT INTO user_profile(First_Name, Last_Name, Gender, DOB, Mobile, Flat_No, Street_No, Street_Name, Locality, Country, Crypto_Address, User_Id) VALUES (:name,:surname,:gender,:dob,:mob,:flat_no,:street_no,:street_name,:locality,:country,:cryto_addr,:user_id)");
            $stmt->bindParam(":name",$first_name);
            $stmt->bindParam(":surname",$last_name);
            $stmt->bindParam(":gender",$gender);
            $stmt->bindParam(":dob",$dob);
            $stmt->bindParam(":mob",$mobile);
            $stmt->bindParam(":flat_no",$flat_No);
            $stmt->bindParam(":street_no",$street_No);
            $stmt->bindParam(":street_name",$street_name);
            $stmt->bindParam(":locality",$locality);
            $stmt->bindParam(":country",$country);
            $stmt->bindParam(":cryto_addr",$crypto_address);
            $stmt->bindParam(":user_id",$user_id);
            
            $stmt->execute();
            $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
            if($result){
                $response["success"] = 1;
                $response["message"] = "New User Added!!";
            
            }else{
                $response["success"] = 0;
                $response["message"] = "Required Field(s) is missing";
            }
            echo json_encode($response);
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }
        //Register new user Device
        function add_user_device($user_id,$device_id,$smart_contract){
            $this->response = array();
            try {
                $connect_db = new db_connect();
                $stmt = $connect_db->connect()->prepare("INSERT INTO registered_devices(User_Id,registered_device_id, smart_contract) VALUES (:user,:device,:contract)");
                $stmt->bindParam(":user",$user_id);
                $stmt->bindParam(":device",$device_id);
                $stmt->bindParam(":contract",$smart_contract);
                $stmt->execute();
                $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
                if($result){
                    $response["success"] = 1;
                    $response["message"] = "User Registered a New device!!";
                
                }else{
                    $response["success"] = 0;
                    $response["message"] = "Required Field(s) is missing";
                }
                echo json_encode($response);
            }
            catch(PDOException $e){
                echo "Error: " . $e->getMessage();
            }
            $connect_db = null;
        }
    // update owner 
    function update_Owner($first_name,$last_name,$email,$gender,$dob,$mobile,$flat_No,$street_No,$street_name,$locality,$country,$crypto_address,$user_id){
        $this->response = array();
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("UPDATE user_credential SET Email = :email WHERE User_Id = :user_Id");
            $stmt->bindParam(":email",$email);
            $stmt->bindParam(":user_Id",$user_id);
            $stmt->execute();

            // Inserting data into User Profile after 
            $stmt = $connect_db->connect()->prepare("UPDATE user_profile SET First_Name = :name , Last_Name = :surname, Gender = :gender, DOB = :dob, Mobile = :mob, Flat_No = :flat_no, Street_No = :street_no, Street_Name = :street_name, Locality = :locality, Country = :country, Crypto_Address = :crypto_addr WHERE User_Id = :user_Id");
            $stmt->bindParam(":name",$first_name);
            $stmt->bindParam(":surname",$last_name);
            $stmt->bindParam(":gender",$gender);
            $stmt->bindParam(":dob",$dob);
            $stmt->bindParam(":mob",$mobile);
            $stmt->bindParam(":flat_no",$flat_No);
            $stmt->bindParam(":street_no",$street_No);
            $stmt->bindParam(":street_name",$street_name);
            $stmt->bindParam(":locality",$locality);
            $stmt->bindParam(":country",$country);
            $stmt->bindParam(":crypto_addr",$crypto_address);
            $stmt->bindParam(":user_Id",$user_id);
            $stmt->execute();
            $data = array();
                while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                        $data[] = $row;
                }
                echo json_encode($data);
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }
    // delete owner 
    function delete_Owner($email){
        $user_id = $this->get_Profile_User_Id($email);
    try {
        $connect_db = new db_connect();
        $stmt = $connect_db->connect()->prepare("DELETE FROM user_credential WHERE User_Id = :user");
        $stmt->bindParam(":user",$user_id);
        $stmt->execute();
        $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
        if($result){
            $response["success"] = 1;
            $response["message"] = "Owner Deleted!!";
        
        }else{
            $response["success"] = 0;
            $response["message"] = "User was not found!";
        }
        echo json_encode($response);
    }
    catch(PDOException $e){
        echo "Error: " . $e->getMessage();
    }
    $connect_db = null;
    }
     // checking user credentials before logging in
    function check_credentials($email,$pass){
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("SELECT COUNT(*) FROM user_credential WHERE Email = :email AND Password = :pass");
            $stmt->bindParam(":email",$email);
            $stmt->bindParam(":pass",$pass);
            $stmt->execute();
            $result = $stmt->fetchColumn();
            if($result == 1)
                $response["success"] = 1;
            else
                $response["success"] = 0;
    
            echo json_encode($response);
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }

    // getting user full name
    function getting_UserinSession($email){
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("SELECT First_Name,Last_Name,uc.User_Id,r.Role_Name,up.Crypto_Address FROM user_profile up INNER JOIN user_credential uc ON up.User_Id = uc.User_Id INNER JOIN role r ON uc.Role_Id = r.Role_ID WHERE Email = :email");
            $stmt->bindParam(":email",$email);
            $stmt->execute();
            $data = array();
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                    $data[] = $row;
            }
            echo json_encode($data);          
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }

        // getting user details
    function getting_UserDetails($email){
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("SELECT First_Name,Last_Name,uc.Email,Gender,DOB,Mobile,Flat_No,Street_No,Street_Name,Locality,Country,Crypto_Address FROM user_profile up INNER JOIN user_credential uc ON up.User_Id = uc.User_Id WHERE Email = :email");
            $stmt->bindParam(":email",$email);
            $stmt->execute();
            $data = array();
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                    $data[] = $row;
            }
            echo json_encode($data);          
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }

            // getting user details
    function getting_UserDetails_Courier($email){
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("SELECT First_Name,Last_Name,uc.Email,Gender,DOB,Mobile,Flat_No,Street_No,Street_Name,Locality,Country,Crypto_Address, ur.System_Id, c.Company_Name FROM user_profile up INNER JOIN user_credential uc ON up.User_Id = uc.User_Id INNER JOIN user_roles ur ON uc.User_Id = ur.User_Id INNER JOIN company c ON ur.Company_Id = c.Company_Id WHERE uc.Email = :email");
            $stmt->bindParam(":email",$email);
            $stmt->execute();
            $data = array();
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                    $data[] = $row;
            }
            echo json_encode($data);          
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }

    // checking if email exists
    function email_Exists($email){
    $this->response = array();
    try {
    $connect_db = new db_connect();
    $stmt = $connect_db->connect()->prepare("SELECT COUNT(*) FROM user_credential WHERE Email = :email");
    $stmt->bindParam(":email",$email);
    $stmt->execute();
    $result = $stmt->fetchColumn();
    if($result == 1)
        $response["success"] = 1;
    else
        $response["success"] = 0;

    echo json_encode($response);
    }
    catch(PDOException $e){
        echo "Error: " . $e->getMessage();
    }
        $connect_db = null;
    }

      // checking if device exists
      function device_exists($email, $device_id){
        $this->response = array();
        try {
        $connect_db = new db_connect();
        $stmt = $connect_db->connect()->prepare("SELECT COUNT(*) FROM user_credential uc INNER JOIN registered_devices r ON uc.User_Id = r.User_Id WHERE Email = :email AND r.registered_device_id = :device");
        $stmt->bindParam(":email",$email);
        $stmt->bindParam(":device",$device_id);
        $stmt->execute();
        $result = $stmt->fetchColumn();
        if($result == 1)
            $response["success"] = 1;
        else
            $response["success"] = 0;
    
        echo json_encode($response);
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
            $connect_db = null;
        }

    // This function returns the company_Id once the user's credentials are created. 
    function get_Company_Id($company){
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("SELECT Company_Id FROM company WHERE Company_Name = :company");
            $stmt->bindParam(":company",$company);
            $stmt->execute();
            $result = $stmt->fetchColumn();
            return $result;        
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }

    // create employee
    function add_Courier($email,$company_name,$role_name){
            $this->response = array();
            $role = $this->get_role($role_name);
            //getting the user id based upon email
            $user_id = $this->get_Profile_User_Id($email);
            // getting the company id by company name which is set as unique
            $company = $this->get_Company_Id($company_name);
            $system = mt_rand(100,100000); // generating a 5 digit random number to be set as the system id
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("INSERT INTO user_roles(User_Id, Role_Id, System_Id, Company_Id) VALUES (:user_Id,:role,:system_Id,:company_Id)");
            $stmt->bindParam(":user_Id",$user_id);
            $stmt->bindParam(":role",$role);
            $stmt->bindParam(":system_Id",$system);
            $stmt->bindParam(":company_Id",$company);
            $stmt->execute();
        
            $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
            if($result){
                $response["success"] = 1;
                $response["message"] = "New Courier Added!!";
            
            }else{
                $response["success"] = 0;
                $response["message"] = "Required Field(s) is missing";
            }
            echo json_encode($response);
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }

    // update employee (user_roles table only)
    function update_Courier($email,$company_name){
        $this->response = array();
        try {
            //getting the user id based upon email
            $user_id = $this->get_Profile_User_Id($email);
            // getting the company id by company name which is set as unique
            $company = $this->get_Company_Id($company_name);
            
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("UPDATE user_roles SET Company_Id = :company WHERE User_Id = :user_Id");
            $stmt->bindParam(":company",$company);
            $stmt->bindParam(":user_Id",$user_id);
            $stmt->execute();

            $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
            if($result){
                $response["success"] = 1;
                $response["message"] = "Courier Updated!";
            
            }else{
                $response["success"] = 0;
                $response["message"] = "Required Field(s) is missing";
            }
            echo json_encode($response);
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }

    // delete employee
    function delete_Courier($email,$company_name){
        $this->response = array();
        $user_id = $this->get_Profile_User_Id($email);
        $company = $this->get_Company_Id($company_name);
    try {
        $connect_db = new db_connect(); 
        $stmt = $connect_db->connect()->prepare("DELETE FROM user_roles WHERE Company_Id = :company AND User_Id = :user");
        $stmt->bindParam(":company",$company);
        $stmt->bindParam(":user",$user_id);
        $stmt->execute();
        $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
        if($result){
            $response["success"] = 1;
            $response["message"] = "Courier Deleted!!";
        
        }else{
            $response["success"] = 0;
            $response["message"] = "User was not found!";
        }
        echo json_encode($response);
    }
    catch(PDOException $e){
        echo "Error: " . $e->getMessage();
    }
    $connect_db = null;
    }

    
    // getting the default device for the user
    function get_default_device($user_id){
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("SELECT smart_contract FROM registered_devices WHERE set_as_default = '1' AND User_Id = :user");
            $stmt->bindParam(":user",$user_id);
            $stmt->execute();
            $result = $stmt->fetchColumn();
            echo $result;
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }

        // getting the default device for the user
    function get_courier_employer_details($user_id){
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("SELECT System_Id, c.Company_Name FROM user_roles ur INNER JOIN company c ON ur.Company_Id = c.Company_Id WHERE ur.User_Id = :user");
            $stmt->bindParam(":user",$user_id);
            $stmt->execute();
            $data = array();
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                    $data[] = $row;
            }
            echo json_encode($data);      
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }


      // removed device as the default device
    function remove_device_as_default($user_id){
        try {

            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("UPDATE registered_devices SET set_as_default = '0' WHERE User_Id = :user AND set_as_default ='1' ");
            $stmt->bindParam(":user",$user_id);
            $stmt->execute();
            $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
            if($result){
                $response["success"] = 1;
                $response["message"] = "Device Set as Default!";
            
            }else{
                $response["success"] = 0;
            }
            echo json_encode($response);
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }


    // updated device as default
    function update_device_default($user_id,$smart_contract){
        try {
            //removing the device which is set as default
            
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("UPDATE registered_devices SET set_as_default = '1' WHERE User_Id = :user AND smart_contract = :contract");
            $stmt->bindParam(":user",$user_id);
            $stmt->bindParam(":contract",$smart_contract);
            $stmt->execute();
            $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
            if($result){
                $response["success"] = 1;
                $response["message"] = "Device Set as Default!";
            
            }else{
                $response["success"] = 0;
            }
            echo json_encode($response);
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }

    // delete device
    function delete_device($smart_contract,$device){
        try {
            $connect_db = new db_connect(); 
            $stmt = $connect_db->connect()->prepare("DELETE FROM registered_devices WHERE smart_contract = :contract AND registered_device_id = :device");
            $stmt->bindParam(":contract",$smart_contract);
            $stmt->bindParam(":device",$device);
            $stmt->execute();
            $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
            if($result){
                $response["success"] = 1;
                $response["message"] = "Device Removed!!";
            
            }else{
                $response["success"] = 0;
                $response["message"] = "Device was not found!";
            }
            echo json_encode($response);
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }

    // getting the notifications the device
    function get_delivery_notifications($user_id){
        try {
            $connect_db = new db_connect();
            $stmt = $connect_db->connect()->prepare("SELECT d.delivery_id, d.delivery_message, d.Timestamp, r.registered_device_id  FROM delivery_notifications d INNER JOIN registered_devices r ON d.smart_contract = r.smart_contract WHERE r.User_Id  = :user");
            $stmt->bindParam(":user",$user_id);
            $stmt->execute();
            $data = array();
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
                    $data[] = $row;
            }
            echo json_encode($data);      
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }
    
    // delete delivery notification
    function delete_notification($user_id){
        try {
            $connect_db = new db_connect(); 
            $stmt = $connect_db->connect()->prepare("DELETE d FROM delivery_notifications d INNER JOIN registered_devices r ON d.smart_contract = r.smart_contract  WHERE r.User_Id = :user");
            $stmt->bindParam(":user",$user_id);
            $stmt->execute();
            $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
            if($result){
                $response["success"] = 1;
                $response["message"] = "Notification Removed!!";
            
            }else{
                $response["success"] = 0;
            }
            echo json_encode($response);
        }
        catch(PDOException $e){
            echo "Error: " . $e->getMessage();
        }
        $connect_db = null;
    }

    // adding delivery notification
    function add_notification($user_id,$message,$smart_contract){
    try {
        $connect_db = new db_connect();
        $stmt = $connect_db->connect()->prepare("INSERT INTO delivery_notifications(User_id, delivery_message,smart_contract) VALUES (:user,:message,:contract)");
        $stmt->bindParam(":user",$user_id);
        $stmt->bindParam(":message",$message);
        $stmt->bindParam(":contract",$smart_contract);
        $stmt->execute();
    
        $result = $stmt->setFetchMode(PDO::FETCH_ASSOC);
        if($result){
            $response["success"] = 1;
            $response["message"] = "Notification added Added!!";
        
        }else{
            $response["success"] = 0;
        }
        echo json_encode($response);
    }
    catch(PDOException $e){
        echo "Error: " . $e->getMessage();
    }
    $connect_db = null;
}

}

//$api = new api_class();
//echo $api->add_User("test","Test","test@gmail.com","12344","M","14/12/1934","79345634",null,"105","adsa","asd","asdas",null,"Courier"); // Adding courier
//echo $api->add_Courier("test@gmail.com","DHL",'Courier');
//echo $api->delete_Courier("test@gmail.com","DHL");
//echo $api->delete_Owner("test@gmail.com");
//echo $api->update_Courier("test@gmail.com","Maltapost");
//echo $api->email_Exists('jake@gmail.com');
//echo $api->check_credentials("jake@gmail.com","123");
//$api->add_user_device(85,"123:123:1as","asxa12e290as");
//$api->device_exists("jake@gmail.com","123:123:1as");
?>