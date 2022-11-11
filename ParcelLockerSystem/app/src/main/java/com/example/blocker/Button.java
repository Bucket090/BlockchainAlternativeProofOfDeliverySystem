package com.example.blocker;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.google.android.material.switchmaterial.SwitchMaterial;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Button extends AppCompatActivity {
    public static final String SHARED_PREFS = "login_session";
    SharedPreferences logged_in_user;
    private String mConnectedDeviceName = null;
    private StringBuffer mOutStringBuffer;
    private StringBuffer mInStringBuffer;
    private CardView lock_state_CardView;
    private ProgressBar pBar;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice b_device;

    String address,deviceName,smart_contract,system_id = null;

    private LinearLayout deviceStateButton;
    private SwitchMaterial button_Switch;

    Boolean isButtonChecked = false;
    TextView device_Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_button);

        Intent get_Connected_Device_Values = getIntent();
        deviceName = get_Connected_Device_Values.getStringExtra("device_name");
        address = get_Connected_Device_Values.getStringExtra("device_address");
        smart_contract = get_Connected_Device_Values.getStringExtra("smart_contract");
        system_id = get_Connected_Device_Values.getStringExtra("system_id");

        logged_in_user = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        // Get the bluetooth port number from preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int port_number = 1;
        // if the default port is not used, get the port
        if (!sharedPreferences.getBoolean("default_port", true)) {
            String port_value = sharedPreferences.getString("port", "0");
            port_number = Integer.parseInt(port_value);
        }

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
        // Initialize the buffer for incoming messages
        mInStringBuffer = new StringBuffer("");

        // Get the BluetoothDevice object
        b_device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(b_device, port_number, true);

        deviceStateButton = findViewById(R.id.devicestateBTN);
        pBar = findViewById(R.id.progressBar);
        lock_state_CardView = findViewById(R.id.cardView_State);
        button_Switch = findViewById(R.id.devicestateSwtich);
        device_Status = findViewById(R.id.deviceStatus);

        button_Switch.setChecked(isButtonChecked);

        deviceStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isButtonChecked = !isButtonChecked;
                if(isButtonChecked) {
                    device_Status.setText("Close Device");
                    send("1");
                }
                else {
                    device_Status.setText("Open Device");
                    send("0");
                }
                button_Switch.setChecked(isButtonChecked);

            }
        });

        button_Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isButtonChecked = !isButtonChecked;
                if(isButtonChecked) {
                    device_Status.setText("Close Device");
                    send("1");
                }
                else {
                    device_Status.setText("Open Device");
                    send("0");
                }
                button_Switch.setChecked(isButtonChecked);
            }
        });

    }
    public void send(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "cant send message - not connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

    private void disconnect() {
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    private void msg(String message) {
        TextView statusView = (TextView) findViewById(R.id.status);
        statusView.setText(message);
    }

    private void parseData(String data) {
        // add the message to the buffer
        mInStringBuffer.append(data);

        // debug - log data and buffer
//        Log.d("data", data);
//        Log.d("mInStringBuffer", mInStringBuffer.toString());
//        msg(data.toString());

        // find any complete messages
        String[] messages = mInStringBuffer.toString().split("\\n");
        int noOfMessages = messages.length;
        // does the last message end in a \n, if not its incomplete and should be ignored
        if (!mInStringBuffer.toString().endsWith("\n")) {
            noOfMessages = noOfMessages - 1;
        }

        // clean the data buffer of any processed messages
        if (mInStringBuffer.lastIndexOf("\n") > -1)
            mInStringBuffer.delete(0, mInStringBuffer.lastIndexOf("\n") + 1);
    }

    @SuppressLint("MissingPermission")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            Log.d("status", "connected");
                            msg("Connected to " + deviceName);
                            lock_state_CardView.setVisibility(View.VISIBLE);
                            pBar.setVisibility(View.GONE);
                            // send the protocol version to the server
                            send("Client Connected " + mBluetoothAdapter.getName());
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            Log.d("status", "connecting");
                            msg("Connecting to " + deviceName);
                            lock_state_CardView.setVisibility(View.INVISIBLE);
                            pBar.setVisibility(View.VISIBLE);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            Log.d("status", "not connected");
                            msg("Not connected");
                            disconnect();
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readData = new String(readBuf, 0, msg.arg1);
                    // message received
                    parseData(readData);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != this) {
                        Toast.makeText(getApplicationContext(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != this) {
                        Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        }
    };

    @SuppressLint("MissingPermission")
    private void unpairDevices(BluetoothDevice device) {
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            try {
                Method m = device.getClass().getMethod("removeBond", (Class[]) null);
                m.invoke(device, (Object[]) null);
            } catch (Exception e) {
                Log.e("Removing has been failed.", e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        disconnect();
        unpairDevices(b_device);
        if(logged_in_user.getString("role","null").equals("Owner"))
            startActivity(new Intent(getApplicationContext(),user_dashboard.class));
        else if(logged_in_user.getString("role","null").equals("Courier"))
            add_notification();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnect();
        unpairDevices(b_device);
        finish();
    }

    /**
     * Adding a notification into the database that this device has been accessed
     */
    public void add_notification(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.call_device_notification;
        final String [] result = {" "};
        final String[] response = {" "};

        String message = "Courier " + system_id + " accessed device " + address;
        String user_id = logged_in_user.getString("user_id","-1");

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
                            +"&&"+ URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8")
                            +"&&"+ URLEncoder.encode("smart_contract", "UTF-8") + "=" + URLEncoder.encode(smart_contract, "UTF-8")
                            +"&&"+ URLEncoder.encode("add_delivery_notification", "UTF-8") + "=" + URLEncoder.encode("OK", "UTF-8");
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
                            startActivity(new Intent(getApplicationContext(),courier_dashboard.class));
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
}