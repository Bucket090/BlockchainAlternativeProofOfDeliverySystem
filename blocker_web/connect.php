<?php
// import database connection variables
require_once './db_config.php';

class db_connect{
    
    /**
     * Function to connect with database
     */
    function connect() {

    try{
        // connecting to the databse
		$sql_connection = "mysql:host=".DB_SERVER.";dbname=".DB_DATABASE.";charset=".DB_CHARSET;
		$conn = new PDO($sql_connection,DB_USER,DB_PASSWORD);
		$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        
		return $conn;
		}
		catch(Exception $e){
			echo "Connection Error: " . $e->getMessage();
		}
    }  
}

?>