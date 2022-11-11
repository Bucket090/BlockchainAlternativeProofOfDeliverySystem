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
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.tx.RawTransactionManager;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class access_device extends AppCompatActivity {
    public static final String SHARED_PREFS = "login_session";
    SharedPreferences logged_in_user;
    Button requestDevice;
    TextInputEditText systemId,company,owner_address,total_parcel,device_inp;
    String owner_address_result,system_id_result,company_result,device_smart_contract_address,crypto_address,device_name;
    LinearLayout accessLayout;
    ProgressBar bar;
    BigInteger number_generated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_device);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestDevice = findViewById(R.id.requestAccessBTN);
        systemId = findViewById(R.id.systemIdINP);
        company = findViewById(R.id.companyINP);
        device_inp = findViewById(R.id.deviceINP);
        owner_address = findViewById(R.id.OwnerAddressINP);
        total_parcel = findViewById(R.id.ParcelsINP);
        accessLayout = findViewById(R.id.AccessDeviceLayout);
        bar = findViewById(R.id.access_device_loading);

        owner_address_result = null;
        Intent generated_results = getIntent();

        // getting data from intent
       system_id_result = (String) generated_results.getStringExtra("SYSTEM_ID");
       company_result = (String) generated_results.getStringExtra("COMPANY_NAME");
       owner_address_result = (String) generated_results.getStringExtra("OWNER_ADDRESS");
       device_smart_contract_address = generated_results.getStringExtra("SMART_CONTRACT");
       device_name = generated_results.getStringExtra("DEVICE_NAME");

       logged_in_user = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
       crypto_address = logged_in_user.getString("address","null");

        device_inp.setText(device_name);
        systemId.setText(system_id_result);
        company.setText(company_result);
        owner_address.setText(owner_address_result);

        requestDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!total_parcel.getText().toString().isEmpty()) {
                    store_generated_one_time_code();

                }
                else
                    Toast.makeText(access_device.this, "Specify total number of parcels", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    /**
     * Storing the generated one time code inside the smart contract which is retrieved by the courier from the QR Code
     */
    public void store_generated_one_time_code(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        number_generated = BigInteger.valueOf(Generate_Random_Number());
        final String[] tx_receipt = {null};

        bar.setVisibility(View.VISIBLE);
        accessLayout.setVisibility(View.GONE);

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

                    Device smart_contract = Device.load(device_smart_contract_address,web3,rawTransactionManager,Constants.GAS_PRICE,Constants.GAS_LIMIT);
                    TransactionReceipt transactionReceipt = smart_contract.GetOneTimeCode(owner_address_result,number_generated).send();
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
                        accessLayout.setVisibility(View.VISIBLE);

                        // transaction has been completed
                        if (tx_receipt[0] != null) {
                            Toast.makeText(getApplicationContext(), "One Time Code Generated", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), Device_control.class);
                            i.putExtra("ACTIVITY", "accessing_device");
                            i.putExtra("code", number_generated.toString());
                            i.putExtra("owner_address", owner_address_result);
                            i.putExtra("smart_contract", device_smart_contract_address);
                            i.putExtra("system_id", system_id_result);
                            i.putExtra("total_parcels", total_parcel.getText().toString());
                            i.putExtra("device_mac_address", device_name);
                            startActivity(i);
                        }else{
                            Toast.makeText(access_device.this, "Device might be not available at the moment", Toast.LENGTH_SHORT).show();
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

    /**
     * Generating a random number of 4 digits which will be stored in the blockchain and valid only once
     */
    private int Generate_Random_Number(){
        Random rand = new Random();
        int code = rand.nextInt(10000);
        return code;
    }
}