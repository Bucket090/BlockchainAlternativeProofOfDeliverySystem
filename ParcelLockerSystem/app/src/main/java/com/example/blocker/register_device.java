package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.tx.Contract;
import org.web3j.tx.RawTransactionManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class register_device extends AppCompatActivity {

    public static final String SHARED_PREFS = "login_session";
    TextView error_lockerID, error_password,error_confirm_password,status;
    TextInputEditText locker_Id, password,confirm_password;
    LinearLayout layout;
    Button nearby_Device,linkDevice;
    String address, user_id,email,crypto_address = null;
    ProgressBar progressBar;
    SharedPreferences logged_in_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);
    }

    @Override
    protected void onStart() {
        super.onStart();
        error_lockerID = findViewById(R.id.error_lockerIDTXT);
        error_password = findViewById(R.id.errorPasswordTxt);
        error_confirm_password = findViewById(R.id.errorConfirmPasswordTxt);
        locker_Id = findViewById(R.id.lockerIdINP);
        password = findViewById(R.id.passwordINP);
        confirm_password = findViewById(R.id.confirmPasswordINP);
        nearby_Device = findViewById(R.id.searchNearbyDeviceBTN);
        linkDevice = findViewById(R.id.linkdeviceBTN);
        status = findViewById(R.id.register_statustxt);
        progressBar = findViewById(R.id.waiting_blockchain);
        layout = findViewById(R.id.registerdevice_layout);
        MyLocker new_device = new MyLocker();

        logged_in_user = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        user_id = logged_in_user.getString("user_id","user_id");
        email = logged_in_user.getString("email","email");
        crypto_address = logged_in_user.getString("address","null");

        nearby_Device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirecting to bluetooth layout to search for nearby devices and return with the bluetooth mac address
                Intent i = new Intent(getApplicationContext(), Device_control.class);
                i.putExtra("ACTIVITY","owner_register_device");
                startActivity(i);
            }
        });

        linkDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checkInput = isInputValid(locker_Id.getText().toString(),password.getText().toString(),confirm_password.getText().toString());
                if (checkInput) {
                    new_device.setLocker_Id(address); // storing the mac address of the device
                    new_device.setLocker_Password(password.getText().toString());
                    // checking if device already added
                    if(!crypto_address.equals("null"))
                        checkDevice(email, new_device.getLocker_Id(),new_device.getLocker_Password());
                    else
                        Toast.makeText(getApplicationContext(), "Add a crypto address (private key)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent get_intent_data = getIntent();
        if(get_intent_data.hasExtra("ACTIVITY")) {
                address = get_intent_data.getStringExtra("device_address");
                locker_Id.setText(address);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    private boolean isInputValid(String locker_Id,String password_inp, String password_confirm_inp){
        boolean isErrorFoundinPassword = false;
        boolean isErrorFoundinLocker = false;
        boolean isErrorFoundinPassword_Confirm = false;

        // checking password inputted is equal to 4 digits
        if(password_inp.length() < 4 || password_inp.length() > 4){
            error_password.setVisibility(View.VISIBLE);
            error_password.setText(getResources().getString(R.string.error_reg_password_len));
            isErrorFoundinPassword = false;
        }else {
            error_password.setVisibility(View.GONE);
            isErrorFoundinPassword = true;
        }

        if(password_confirm_inp.length() != 4 || password_confirm_inp.length() > 4){
            error_confirm_password.setVisibility(View.VISIBLE);
            error_confirm_password.setText(getResources().getString(R.string.error_reg_password_len));
            isErrorFoundinPassword_Confirm = false;
        }else {
            error_confirm_password.setVisibility(View.GONE);
            isErrorFoundinPassword_Confirm = true;
        }

        // Confirming password and repeat password
        if(password_inp.compareTo(password_confirm_inp) == 0 && password_confirm_inp.length() ==  4 && password_inp.length() == 4){
            error_password.setVisibility(View.GONE);
            error_confirm_password.setVisibility(View.GONE);
            isErrorFoundinPassword = true;
            isErrorFoundinPassword_Confirm = true;
        }else {
            error_password.setVisibility(View.VISIBLE);
            error_password.setText(getResources().getString(R.string.error_password_confirm_msg));
            error_confirm_password.setVisibility(View.VISIBLE);
            error_confirm_password.setText(getResources().getString(R.string.error_password_confirm_msg));
            isErrorFoundinPassword = false;
            isErrorFoundinPassword_Confirm = false;
        }

        if(locker_Id.isEmpty()){
            error_lockerID.setVisibility(View.VISIBLE);
            error_lockerID.setText(getResources().getString(R.string.error_locker_ID));
            isErrorFoundinLocker = false;
        }else {
            error_lockerID.setVisibility(View.INVISIBLE);
            isErrorFoundinLocker = true;
        }

        if (isErrorFoundinLocker == true && isErrorFoundinPassword == true && isErrorFoundinPassword_Confirm == true)
            return true;

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),user_dashboard.class));
    }

    public void checkDevice(String email,String device,String device_password){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.get_user_device;
        final String [] result = {" "};
        final String[] response = {" "};

        // Hiding everything inside the layout except the progress bar
        progressBar.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        status.setVisibility(View.GONE);

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
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
                            +"&&"+ URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(device, "UTF-8");
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

                    JSONObject obj = null;
                    obj = new JSONObject(result[0]);
                    response[0] = obj.getString("success");

                } catch (MalformedURLException e) {
                    result[0] = e.getMessage();
                } catch (IOException e) {
                    result[0] = e.getMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        if (response[0].equals("1")) {
                            progressBar.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            status.setVisibility(View.VISIBLE);
                            status.setText("Error: You might have already registered this locker");
                        }
                        else {
                            Toast.makeText(register_device.this, "Check Device Complete, Accessing the Network", Toast.LENGTH_SHORT).show();
                            Deploy_Smart_Contract_Device(device, device_password); // Trigger Blockchain function
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

    public void Deploy_Smart_Contract_Device(String user_device_id, String user_device_password){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        final String[] smart_contract_address = {" "};
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

                    // Deploy smart contract for every device
                    Device contract_deploy = Device.deploy(web3,rawTransactionManager,Contract.GAS_PRICE,Contract.GAS_LIMIT).send();
                    smart_contract_address[0] = contract_deploy.getContractAddress();
                    System.out.println("Smart Contaract address: " + smart_contract_address[0]);

                    // generating receipt
                    Optional<TransactionReceipt> transactionReceipt = contract_deploy.getTransactionReceipt();
                    tx_receipt[0] = transactionReceipt.toString();
                    System.out.println("Receipt: " + tx_receipt[0]);

                    web3.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // transaction has been completed
                        if (tx_receipt[0] != null) {
                            Toast.makeText(register_device.this, "Device Link Created", Toast.LENGTH_SHORT).show();
                            Register_Device_Blockchain(user_device_id,user_device_password,smart_contract_address[0]);
                        }else{
                            // once an output has been returned from the blockchain, the layout will be displayed again
                            progressBar.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            status.setVisibility(View.VISIBLE);
                            status.setText("Error: Device Link Failed");
                        }
                        if (crypto_address.isEmpty() || crypto_address == null){
                            progressBar.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            status.setVisibility(View.VISIBLE);
                            status.setText("Error: Set a crypto address from your profile.");
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }


    public void Register_Device_Blockchain(String user_device_id, String user_device_password,String smart_contract_address){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

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
                    TransactionReceipt transactionReceipt = smart_contract.RegisterDevice(user_device_id,user_device_password).send();
                    tx_receipt[0] = transactionReceipt.getStatus();
                    System.out.println("Receipt: " + tx_receipt[0]);

                    web3.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // transaction has been completed
                        if (tx_receipt[0] != null) {
                            SharedPreferences.Editor editor = logged_in_user.edit();
                            editor.putString("smart_contract_default_device", smart_contract_address);
                            editor.putBoolean("device_state_access", true); // default
                            editor.apply();
                            register_user_device(user_id,user_device_id,smart_contract_address);
                        }else{
                            // once an output has been returned from the blockchain, the layout will be displayed again
                            progressBar.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            status.setVisibility(View.VISIBLE);
                            status.setText("Error: You might have already registered this locker");
                        }
                        if (crypto_address.isEmpty() || crypto_address == null){
                            progressBar.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            status.setVisibility(View.VISIBLE);
                            status.setText("Error: Set a crypto address from your profile.");
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }


    public void register_user_device(String user,String device,String contract_address){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.call_register_device;
        final String[] result = {" "};
        final String[] response = {" "};

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
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("User_Id","UTF-8")+"="+URLEncoder.encode(user,"UTF-8")
                            +"&&"+ URLEncoder.encode("device_id","UTF-8")+"="+URLEncoder.encode(device,"UTF-8")
                            +"&&"+ URLEncoder.encode("contract","UTF-8")+"="+URLEncoder.encode(contract_address,"UTF-8")
                            +"&&"+ URLEncoder.encode("register_device","UTF-8")+"="+URLEncoder.encode("OK","UTF-8");
                    writer.write(data);
                    writer.flush();
                    writer.close();
                    ops.close();

                    InputStream ips = http.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));
                    String line ="";
                    while ((line = reader.readLine()) != null) {
                        result[0] += line;
                    }
                    reader.close();
                    ips.close();
                    http.disconnect();

                    JSONObject obj = null;
                    obj = new JSONObject(result[0]);
                    response[0] = obj.getString("success");

                } catch (MalformedURLException e) {
                    result[0] = e.getMessage();
                } catch (IOException e) {
                    result[0] = e.getMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        if (response[0].equals("1")) {
                            startActivity(new Intent(getApplicationContext(), user_dashboard.class));
                            Toast.makeText(getApplicationContext(), "Device Registered on the network", Toast.LENGTH_SHORT).show();
                        }else{
                            progressBar.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            status.setVisibility(View.VISIBLE);
                            status.setText("Error: You might have already registered this locker");
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
}