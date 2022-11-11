package com.example.blocker;

import java.math.BigInteger;

/**
 * Defines several constants used between {@link BluetoothChatService in Device Layout} and the UI.
 */
public interface Constants {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // API String call
    //public static final String api_url = "http://192.168.0.12:8080/blocker_web/"; // localhost
    public static final String api_url = "https://parceldeliverylocker.000webhostapp.com/"; // webhost
    public static final String call_user = "call_user.php";
    public static final String call_user_courier = "call_user_courier.php";
    public static final String call_register_device = "call_register_device.php";
    public static final String call_device_notification = "call_device_notification.php";
    public static final String get_user = "get_user.php";
    public static final String get_user_login = "get_user_login.php";
    public static final String get_user_device = "get_user_device.php";
    public static final String get_user_device_default = "get_user_device_default.php";

    // Blockchain Variables
    public final static String infura_project_id = "475e58536ac8491fbb6e0d30403d6ac1";
    //public String http_connection = "https://ropsten.infura.io/v3/" + infura_project_id;
    public String http_connection = "https://goerli.infura.io/v3/" + infura_project_id;

    // define gas price
    public final BigInteger GAS_LIMIT = BigInteger.valueOf(150_000);
    public final BigInteger GAS_PRICE = BigInteger.valueOf(2_600_000_000L);
}