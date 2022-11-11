package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.content.SharedPreferences;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainId;
import org.web3j.tx.RawTransactionManager;

import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Device_control extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String SHARED_PREFS = "login_session";
    SharedPreferences logged_in_user;
    private ListView listAvailableDevices;
    private ArrayAdapter<String> adapterAvailableDevices;
    private ProgressBar progressScanDevices;
    private android.widget.Button scan;
    private BluetoothAdapter myBluetooth = null;

    private Intent intent, turnBTon,get_intent_data;

    String address,deviceName,device_mac_address,owner_address,smart_contract,system_id = null;

    BigInteger one_time_code,parcel;

    private static final int REQUEST_LOCATION_PERMISSION = 1;

    public static final String Activity_Register_Device = "owner_register_device";
    public static final String Activity_Owner_Access_Device = "owner_access_device";
    public static final String Activity_Access_Device = "accessing_device";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        listAvailableDevices = findViewById(R.id.list_available_devices);
        progressScanDevices = findViewById(R.id.progress_scan_devices);
        scan = findViewById(R.id.scanBTN);

        //if the device has bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        // Ask to the user turn the bluetooth on
        turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (myBluetooth == null) {
            Toast.makeText(
                    getApplicationContext(),
                    "Bluetooth Device Not Available",
                    Toast.LENGTH_LONG).show();

            //finish apk
            this.finish();
            System.exit(0);

        } else if (!myBluetooth.isEnabled()) {
            startActivity(turnBTon);
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        get_intent_data = getIntent();
        if (get_intent_data.getStringExtra("ACTIVITY").equals(Activity_Access_Device)) {
            owner_address = get_intent_data.getStringExtra("owner_address");
            smart_contract = get_intent_data.getStringExtra("smart_contract");
            system_id = get_intent_data.getStringExtra("system_id");
            device_mac_address = get_intent_data.getStringExtra("device_mac_address");

            String generated_code = get_intent_data.getStringExtra("code");
            one_time_code = BigInteger.valueOf(Integer.valueOf(generated_code));

            String total_parcel = get_intent_data.getStringExtra("total_parcels");
            parcel = BigInteger.valueOf(Integer.valueOf(total_parcel));


        }

        adapterAvailableDevices = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(getResources().getColor(R.color.black));
                return textView;
            }
        };

        listAvailableDevices.setAdapter(adapterAvailableDevices);
        listAvailableDevices.setOnItemClickListener(myListClickListener);

        // checking if the request is coming from a courier , if so then we will register a specific broadcast listener
        // else those requests are coming from the owner itself
        if (get_intent_data.getStringExtra("ACTIVITY").equals(Activity_Access_Device)) {
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(bluetoothDeviceListenerCourier, intentFilter);
            IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(bluetoothDeviceListenerCourier, intentFilter1);
        }else{
            IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(bluetoothDeviceListener, intentFilter);
            IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(bluetoothDeviceListener, intentFilter1);
        }

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan_for_Available_Devices();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setConnectMsg();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onStop() {
        super.onStop();
        myBluetooth.cancelDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // unregistering broadcast receiver on destroy
        if (bluetoothDeviceListenerCourier != null) {
            unregisterReceiver(bluetoothDeviceListenerCourier);
        }
        else if (bluetoothDeviceListener != null) {
            unregisterReceiver(bluetoothDeviceListener);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("port")) {
            setConnectMsg(sharedPreferences);
        }
    }

    private void setConnectMsg() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        setConnectMsg(sharedPreferences);
    }

    private void setConnectMsg(SharedPreferences sharedPreferences) {
        String message = "Connect to Device";
        Boolean default_port = sharedPreferences.getBoolean("default_port", true);
        String port_value = sharedPreferences.getString("port", "1");

        if (!default_port) {
            message = message + " on port " + port_value;
        }

        TextView connectView = findViewById(R.id.connect);
        connectView.setText(message);
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            // removing the broadcast intent so it won't keep popping up toast messages on the next activity
            // if a courier requests access
            if (get_intent_data.getStringExtra("ACTIVITY").equals(Activity_Access_Device)) {
                if (bluetoothDeviceListenerCourier != null) {
                    unregisterReceiver(bluetoothDeviceListenerCourier);
                }
            }

            // if an owner requests access
            if (get_intent_data.getStringExtra("ACTIVITY").equals(Activity_Register_Device) || get_intent_data.getStringExtra("ACTIVITY").equals(Activity_Owner_Access_Device)) {
                if (bluetoothDeviceListener != null) {
                    unregisterReceiver(bluetoothDeviceListener);
                }
            }
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            deviceName = info.split("\n")[0];
            address = info.split("\n")[1];

            // checking if the activity that came from was either from the register device page or else from the access device page
            // Make an intent to start next activity.

            if (get_intent_data.getStringExtra("ACTIVITY").equals(Activity_Access_Device)) {
                logged_in_user = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                String crypto_address = logged_in_user.getString("address","null");
                Courier_Access_Device(one_time_code,parcel,crypto_address,smart_contract,owner_address,system_id);

            }
            else if (get_intent_data.getStringExtra("ACTIVITY").equals(Activity_Owner_Access_Device)) {
                intent = new Intent(Device_control.this, Button.class);
                intent.putExtra("ACTIVITY", Activity_Owner_Access_Device);
                intent.putExtra("device_name", deviceName);
                intent.putExtra("device_address", address);
                startActivity(intent);
            }
            else if (get_intent_data.getStringExtra("ACTIVITY").equals(Activity_Register_Device)) {
                intent = new Intent(Device_control.this, register_device.class);
                intent.putExtra("ACTIVITY", Activity_Register_Device);
                intent.putExtra("device_name", deviceName);
                intent.putExtra("device_address", address);
                startActivity(intent);
            }
        }
    };

    /**
     * This method is discovering for new devices
     */
    private void scan_for_Available_Devices() {
        if (myBluetooth.getState() == BluetoothAdapter.STATE_OFF) {
            startActivity(turnBTon);
            Toast.makeText(this, "Bluetooth must be turned on to discover new devices", Toast.LENGTH_SHORT).show();
        } else if (myBluetooth.getState() == BluetoothAdapter.STATE_ON) {
            progressScanDevices.setVisibility(View.VISIBLE);
            adapterAvailableDevices.clear();
            Toast.makeText(this, "Searching for Available Device", Toast.LENGTH_SHORT).show();

            // checking if we have permission for location especially for api version 30 and up, this allows bluetooth devices nearby to be seen
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Location Permission was not granted.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            } else {
                myBluetooth.startDiscovery();
            }
        }
    }

    /**
     * This Broadcast Receiver listens to upcoming bluetooth devices which are discoverable in a short time window that the mobile is on discovery mode
     * The same broadcast receiver is being called twice , once when a new bluetooth device is discovered and the second time when discovery mode has ended
     * and no new devices were found and won't be found unless the process starts again. (Clicking on the scan for devices button)
     * */
    // This is user ONLY by the courier
    private BroadcastReceiver bluetoothDeviceListenerCourier = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if(device_mac_address.equals(device.getAddress())) {
                        adapterAvailableDevices.add(device.getName() + "\n" + device.getAddress());
                        progressScanDevices.setVisibility(View.GONE);
                        Toast.makeText(context, "Device found, Click to connect", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (listAvailableDevices.getCount() == 0) {
                    progressScanDevices.setVisibility(View.GONE);
                    Toast.makeText(context, "Device not found, Try again", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "No more Devices found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    // This is user ONLY by the owner of the device
    private BroadcastReceiver bluetoothDeviceListener = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        adapterAvailableDevices.add(device.getName() + "\n" + device.getAddress());
                        progressScanDevices.setVisibility(View.GONE);
                        Toast.makeText(context, "New Device found, Click to connect", Toast.LENGTH_SHORT).show();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (listAvailableDevices.getCount() == 0) {
                    progressScanDevices.setVisibility(View.GONE);
                    Toast.makeText(context, "Device not found, Try again", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "No more Devices found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /**
     * Storing the generated one time code inside the smart contract which is retrieved by the courier from the QR Code
     */
    public void Courier_Access_Device(BigInteger generated_number, BigInteger total_parcels, String crypto_address,String device_smart_contract_address,String owner_address,String system_id){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        progressScanDevices.setVisibility(View.VISIBLE);
        listAvailableDevices.setVisibility(View.GONE);

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

                    Device smart_contract = Device.load(device_smart_contract_address,web3,rawTransactionManager,Constants.GAS_PRICE,Constants.GAS_LIMIT);
                    TransactionReceipt transactionReceipt = smart_contract.CourierAccessDevice(owner_address,BigInteger.valueOf(Integer.valueOf(system_id)),generated_number,total_parcels).send();
                    tx_receipt[0] = transactionReceipt.getStatus();
                    System.out.println("Receipt: " + tx_receipt[0]);

                    web3.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (tx_receipt[0] != null) {
                            Toast.makeText(getApplicationContext(),"Device access allowed",Toast.LENGTH_SHORT).show();
                            intent = new Intent(Device_control.this, Button.class);
                            intent.putExtra("ACTIVITY", Activity_Access_Device);
                            //Change the activity.
                            intent.putExtra("device_name", deviceName);
                            intent.putExtra("device_address", address);
                            intent.putExtra("smart_contract", smart_contract);
                            intent.putExtra("system_id", system_id);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),"Device might be not available at the moment",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Device_control.this,courier_dashboard.class));
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

}