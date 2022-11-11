package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.ChainId;
import org.web3j.tx.RawTransactionManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class user_dashboard extends AppCompatActivity {
    public static final String SHARED_PREFS = "login_session";
    LinearLayout registerDeviceBTN,notificationsBTN,device_stateBTN,view_registered_devicesBTN,profileBTN,settingsBTN;
    SwitchMaterial device_State_Switch;
    ImageView Profile_imageBTN;
    TextView fullname,status_device,error_password;
    String crypto_address,smart_contract_address,user_id = null;
    SharedPreferences logged_in_user;
    EditText alert_password;
    Button enter,cancel;
    boolean isButtonChecked = true;
    Dialog dialog;
    ProgressBar bar;
    GridLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerDeviceBTN = findViewById(R.id.registerDeviceBTN);
        notificationsBTN = findViewById(R.id.deliveryNotificationBTN);
        device_stateBTN = findViewById(R.id.devicestateBTN);
        view_registered_devicesBTN = findViewById(R.id.viewRegisteredDeviceBTN);
        profileBTN = findViewById(R.id.userprofileBTN);
        Profile_imageBTN = findViewById(R.id.ProfileBTN);
        settingsBTN = findViewById(R.id.settingsBTN);
        fullname = findViewById(R.id.fullnameTXT);
        device_State_Switch = findViewById(R.id.devicestateSwtich);
        status_device = findViewById(R.id.device_statusTXT);
        bar = findViewById(R.id.user_dashboard_loading);
        layout = findViewById(R.id.action_layout);

        // getting login session details
        logged_in_user = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        if(!logged_in_user.getBoolean("retrieved_device_state",false))
            retrieving_default_device();

        String full_name = logged_in_user.getString("full_name", "Full Name");
        fullname.setText(full_name);

        crypto_address = logged_in_user.getString("address", "null");
        user_id = logged_in_user.getString("user_id", "-1");
        smart_contract_address = logged_in_user.getString("smart_contract_default_device", "null");
        isButtonChecked = logged_in_user.getBoolean("device_state_access", true);
        device_State_Switch.setChecked(isButtonChecked);

        registerDeviceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),register_device.class));
            }
        });

        notificationsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),delivery_notifications.class));
            }
        });

        view_registered_devicesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),view_registered_devices.class));
            }
        });

        profileBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),profile_user.class));
            }
        });

        Profile_imageBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),settings.class);
                i.putExtra("ACTIVITY","user_profile");
                startActivity(i);
            }
        });

        settingsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),settings.class);
                i.putExtra("ACTIVITY","user_profile");
                startActivity(i);
            }
        });

        device_stateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!smart_contract_address.equals("null"))
                    openDialog();
                else
                    Toast.makeText(getApplicationContext(), "Device not found", Toast.LENGTH_SHORT).show();

            }
        });
        device_State_Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!smart_contract_address.equals("null"))
                    openDialog();
                else {
                    Toast.makeText(getApplicationContext(), "Device not found", Toast.LENGTH_SHORT).show();
                    device_State_Switch.setChecked(false);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void openDialog() {
        dialog = new Dialog(user_dashboard.this); // Context, this, etc.
        dialog.setContentView(R.layout.alert_enter_password);
        dialog.setTitle("Enter Password");
        dialog.show();

        alert_password = dialog.findViewById(R.id.editTextTextPassword);
        enter = dialog.findViewById(R.id.btn_enter);
        cancel = dialog.findViewById(R.id.btn_cancel);
        error_password = dialog.findViewById(R.id.errorPasswordTxt);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                error_password.setVisibility(View.INVISIBLE);
                dialog.dismiss();
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = alert_password.getText().toString();
                if(!password.isEmpty()){
                    isButtonChecked = !isButtonChecked;
                    Set_Device_Access(password);
                    device_State_Switch.setChecked(isButtonChecked);
                    dialog.dismiss();
                }else{
                    error_password.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Setting Device access by removing or allowing access of the device
     * @param device_password
     */
    public void Set_Device_Access(String device_password){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        status_device.setText("Waiting..");

        final String[] tx_receipt = {null};
        executor.execute(new Runnable() {
            @Override
            public void run () {
                // Connecting to the network
                System.out.println("Connecting to Ethereum ...");
                HttpService httpService = new HttpService(Constants.http_connection);
                Web3j web3 = Web3j.build(httpService);
                System.out.println("Successfully connected to Ethereum");
                String private_key = "0x" + crypto_address;
                try {
                    // Load an account
                    Credentials credentials = Credentials.create(private_key); // retrieving the crypto address [PRIVATE] from the shared preference
                    RawTransactionManager rawTransactionManager = new RawTransactionManager(web3,credentials);

                    Device smart_contract = Device.load(smart_contract_address,web3,rawTransactionManager,Constants.GAS_PRICE,Constants.GAS_LIMIT);
                    TransactionReceipt transactionReceipt = smart_contract.SetDeviceAccess(device_password,isButtonChecked).send();
                    tx_receipt[0] = transactionReceipt.getStatus();
                    System.out.println("Receipt: " + tx_receipt[0]);

                    web3.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        status_device.setText("Device State");

                        // transaction has been completed
                        if (tx_receipt[0] != null && !isButtonChecked)
                            Toast.makeText(getApplicationContext(), "Device Temporary out of service", Toast.LENGTH_SHORT).show();
                        else if (tx_receipt[0] != null && isButtonChecked)
                            Toast.makeText(getApplicationContext(), "Device service is restored", Toast.LENGTH_SHORT).show();
                        else {
                            Toast.makeText(getApplicationContext(), "You might have entered an incorrect password", Toast.LENGTH_SHORT).show();
                            isButtonChecked = !isButtonChecked;
                            device_State_Switch.setChecked(isButtonChecked);
                        }
                        SharedPreferences.Editor editor = logged_in_user.edit();
                        editor.putBoolean("device_state_access", isButtonChecked);
                        editor.putBoolean("retrieved_device_state", false);
                        editor.apply();
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

    /**
     * Getting device state from the blockchain
     */
    public void Get_Device_State(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        final Boolean[] current_state = {false};

        executor.execute(new Runnable() {
            @Override
            public void run () {
                // Connecting to the network
                System.out.println("Connecting to Ethereum ...");
                HttpService httpService = new HttpService(Constants.http_connection);
                Web3j web3 = Web3j.build(httpService);
                System.out.println("Successfully connected to Ethereum");
                String private_key = "0x" + crypto_address;
                try {
                    // Load an account
                    Credentials credentials = Credentials.create(private_key); // retrieving the crypto address [PRIVATE] from the shared preference
                    RawTransactionManager rawTransactionManager = new RawTransactionManager(web3,credentials);
                    String public_address = credentials.getAddress();
                    // load smart contract and call update password method to change the current password on the device
                    Device smart_contract = Device.load(smart_contract_address,web3,rawTransactionManager,Constants.GAS_PRICE,Constants.GAS_LIMIT);

                    Tuple8<String, String, Boolean, byte[], BigInteger, Boolean, Boolean, BigInteger> transactionReceipt = smart_contract.owners(public_address).send();
                    current_state[0] = transactionReceipt.component7();

                    System.out.println("Receipt: " + transactionReceipt);

                    web3.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // transaction has been completed
                        SharedPreferences.Editor editor = logged_in_user.edit();
                        editor.putBoolean("device_state_access", current_state[0]);
                        editor.putBoolean("retrieved_device_state", true);
                        editor.apply();

                        device_State_Switch.setChecked(current_state[0]);

                        bar.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

    /**
     * Retrieving the default device from the centralized database
     */
    public void retrieving_default_device(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.get_user_device_default;
        final String [] result = {" "};

        bar.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);

        executor.execute(new Runnable() {
            @Override
            public void run () {

                try {
                    URL url = new URL(url_add_user);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoInput(true);
                    http.setDoOutput(true);

                    OutputStream ops = http.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                    String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")
                            +"&&"+ URLEncoder.encode("retrieve_device_as_default", "UTF-8") + "=" + URLEncoder.encode("OK", "UTF-8");
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    ops.close();

                    InputStream ips = http.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ips, "ISO-8859-1"));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        result[0] += line;
                    }
                    reader.close();
                    ips.close();
                    http.disconnect();

                } catch (MalformedURLException e) {
                    result[0] = e.getMessage();
                } catch (IOException e) {
                    result[0] = e.getMessage();
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //UI Thread work here
                        if(!logged_in_user.getBoolean("retrieved_device_state",false)) {
                            if (smart_contract_address.equals("null")) {
                                SharedPreferences.Editor editor = logged_in_user.edit();
                                if (result[0].trim().length() == 0)
                                    editor.putString("smart_contract_default_device", "null");
                                else
                                    editor.putString("smart_contract_default_device", result[0].trim());
                                editor.apply();
                                smart_contract_address = logged_in_user.getString("smart_contract_default_device", "null");
                            }
                            Get_Device_State();
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
}