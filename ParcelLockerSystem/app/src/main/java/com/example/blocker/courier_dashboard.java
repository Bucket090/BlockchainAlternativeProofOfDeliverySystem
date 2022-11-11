package com.example.blocker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple3;
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
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class courier_dashboard extends AppCompatActivity{
    public static final String SHARED_PREFS = "login_session";
    SharedPreferences logged_in_user;
    LinearLayout accessDeviceBTN, QrcodeScanBTN,settingsBTN,logoutBTN;
    ImageView profileBtn;
    String owner_address,crypto_address,device_smart_contract_address,user_id,system_id,company;
    GridLayout  action_layout;
    Intent access_device;
    TextView fullname;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_dashboard);
    }

    @Override
    protected void onStart() {
        super.onStart();
        accessDeviceBTN = (LinearLayout) findViewById(R.id.accessDeviceBTN);
        QrcodeScanBTN = (LinearLayout) findViewById(R.id.CodeScanBTN);
        profileBtn = findViewById(R.id.ProfileBTN);
        settingsBTN = findViewById(R.id.settingsBTN);
        logoutBTN = findViewById(R.id.logoutBTN);
        fullname = (TextView) findViewById(R.id.nameTXT);
        action_layout = findViewById(R.id.ActionButtons_Layout);
        bar = findViewById(R.id.courier_dashboard_loading);

        logged_in_user = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String full_name = logged_in_user.getString("full_name", "Full Name");
        crypto_address = logged_in_user.getString("address","null");
        user_id = logged_in_user.getString("user_id","-1");
        fullname.setText(full_name);

        owner_address = null;
        access_device = new Intent(this, access_device.class);

        // if we already got the data, we won't need to call this method again.
       if(!logged_in_user.getBoolean("employer_data_retrieved",false)) {
           get_employer_details();
       }
        system_id = logged_in_user.getString("courier_system_id","null");
        company = logged_in_user.getString("courier_employer","null");
        // storing the gathered information in an intent
        access_device.putExtra("SYSTEM_ID",system_id);
        access_device.putExtra("COMPANY_NAME",company);

        accessDeviceBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!crypto_address.equals(null)) {
                    if (owner_address != null) // If QR CODE is not scanned we cannot access the access device activity
                        startActivity(access_device);
                    else
                        Toast.makeText(getApplicationContext(), "Please Scan the QR code first", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Set a crypto address (private key) from your profile", Toast.LENGTH_SHORT).show();
            }
        });

        QrcodeScanBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!crypto_address.equals(null)) {
                    RequestCameraAccess();
                }
                else
                    Toast.makeText(getApplicationContext(), "Set a crypto address (private key) from your profile", Toast.LENGTH_SHORT).show();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // redirecting to the courier profile
                Intent i = new Intent(getApplicationContext(),settings.class);
                i.putExtra("ACTIVITY","courier_profile");
                startActivity(i);
            }
        });
        settingsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // redirecting to the courier profile
                Intent i = new Intent(getApplicationContext(),settings.class);
                i.putExtra("ACTIVITY","courier_profile");
                startActivity(i);
            }
        });
        logoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(courier_dashboard.this,R.style.Base_Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Logging out");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Clear login Sessions
                        SharedPreferences.Editor clear_session = logged_in_user.edit();
                        clear_session.clear();
                        clear_session.apply();

                        startActivity(new Intent(getApplicationContext(),login.class));
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    /**
     * This function is requesting permission for the Camera Access
     * Calling a function called IntentIntegrator to initiate the scan once the qr code button is clicked it will check for the camera permission if given by the user
     * whilst checking for any faulty camera hardware
     */
    public void RequestCameraAccess() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Camera Permission was not granted.", Toast.LENGTH_LONG).show();
        }
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        if (!checkCameraHardware(this))
            Toast.makeText(getApplicationContext(), "Camera Hardware fault, Try again Later!", Toast.LENGTH_SHORT).show();
        else
            intentIntegrator.initiateScan();
    }

    /**
     * On QR Code scan this method will be triggered retrieving the data from the QR Code.
     * The QR Code contains a combination of public address of the owner and the smart contract of the device
     * To allow the courier to interact with the device.
     * These are saved in an intent so it will be passed to the access device activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (intentResult != null) {
                if (intentResult.getContents() != null) {
                    owner_address = intentResult.getContents();
                    String[] info = intentResult.getContents().split("\n");
                    owner_address = info[0];
                    device_smart_contract_address = info[1];
                    String device_name = info[2];
                    access_device.putExtra("OWNER_ADDRESS",owner_address);
                    access_device.putExtra("SMART_CONTRACT",device_smart_contract_address);
                    access_device.putExtra("DEVICE_NAME",device_name);
                    Toast.makeText(this,"GOT IT !! ",Toast.LENGTH_SHORT).show();
                    logged_in_user.getString("courier_system_id","null");
                    Get_Device_State();
                }
            }
        }
    }

    /**
     * Adding the courier to the smart contract (done once per smart contract)
     */
    public void Add_Courier_to_Blockchain(String employer, String system_id){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        
        final String[] tx_receipt = {null};

        bar.setVisibility(View.VISIBLE);
        action_layout.setVisibility(View.GONE);

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
                    Device smart_contract = Device.load(device_smart_contract_address,web3,rawTransactionManager,Constants.GAS_PRICE,Constants.GAS_LIMIT);
                    TransactionReceipt transactionReceipt = smart_contract.AddCourier(public_address,employer,BigInteger.valueOf(Integer.valueOf(system_id))).send();
                    tx_receipt[0] = transactionReceipt.getStatus();
                    System.out.println("Receipt: " + tx_receipt[0]);

                    web3.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        bar.setVisibility(View.GONE);
                        action_layout.setVisibility(View.VISIBLE);

                        // transaction has been completed
                        if (tx_receipt[0] != null) {
                            Toast.makeText(courier_dashboard.this, "Courier Added on the blockchain", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(courier_dashboard.this, "Make sure that you have entered a valid crypto address (private key)", Toast.LENGTH_SHORT).show();
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

    /**
    * Retrieving employer details System id , Company name
    */
    private void get_employer_details() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        bar.setVisibility(View.VISIBLE);
        action_layout.setVisibility(View.GONE);

        String url_check_user_credentials = Constants.api_url + Constants.get_user;
        final String[] result = {" "};
        final String [] my_data = {null,null};
        executor.execute(new Runnable() {
            @Override
            public void run () {
                try {
                    URL url = new URL(url_check_user_credentials);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoInput(true);
                    http.setDoOutput(true);

                    OutputStream ops = http.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("user_id","UTF-8")+"="+URLEncoder.encode(user_id,"UTF-8")
                            +"&&"+ URLEncoder.encode("get_courier_details","UTF-8")+"="+URLEncoder.encode("OK","UTF-8");
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

                    JSONArray obj = null;
                    obj = new JSONArray(result[0]);
                    my_data[0] = obj.getJSONObject(0).getString("System_Id");
                    my_data[1] = obj.getJSONObject(0).getString("Company_Name");

                } catch (IOException | JSONException e) {
                    result[0] = e.getMessage();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        bar.setVisibility(View.GONE);
                        action_layout.setVisibility(View.VISIBLE);

                        // adding session to retrieve this information once
                        SharedPreferences.Editor editor = logged_in_user.edit();
                        editor.putString("courier_system_id",my_data[0]);
                        editor.putString("courier_employer",my_data[1]);
                        editor.putBoolean("employer_data_retrieved",true);
                        editor.apply();
                    }
                });
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

    public void Get_Device_State(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        final String[] courier_data = {null,null};
        bar.setVisibility(View.VISIBLE);
        action_layout.setVisibility(View.GONE);
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
                    Device smart_contract = Device.load(device_smart_contract_address,web3,rawTransactionManager,Constants.GAS_PRICE, Constants.GAS_LIMIT);

                    Tuple3<String, String, BigInteger> transactionReceipt = smart_contract.couriers(public_address).send();
                    courier_data[0] = transactionReceipt.component2();
                    courier_data[1] = transactionReceipt.component3().toString();

                    System.out.println("Receipt: " + transactionReceipt);

                    web3.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        bar.setVisibility(View.GONE);
                        action_layout.setVisibility(View.VISIBLE);

                        // transaction has been completed
                        if(courier_data[0].isEmpty() && courier_data[1].equals("0"))
                            Add_Courier_to_Blockchain(company,system_id);
                        else
                            Toast.makeText(getApplicationContext(),"Courier already registered with this device",Toast.LENGTH_SHORT).show();
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }


    /*** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}