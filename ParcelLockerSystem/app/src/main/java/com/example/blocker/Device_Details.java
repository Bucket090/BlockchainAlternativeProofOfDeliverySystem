package com.example.blocker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.ChainId;
import org.web3j.tx.RawTransactionManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
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

public class Device_Details extends AppCompatActivity {
    public static final String SHARED_PREFS = "login_session";
    SharedPreferences logged_in_user;
    EditText alert_password;
    Button enter,cancel;
    Dialog dialog;
    TextView error_password,device_name,device_state,DefaultTxt,password_txt;
    CardView change_password , copy,default_layout,connect_layout,generate_layout,delete_device;
    String smart_contract_device,smart_contract_default, crypto_address,_logged_in_user_id, device_status = null;
    ImageButton default_device_imagebutton;
    boolean currently_set_as_default = false;
    Intent intent;
    ProgressBar bar;
    ScrollView scrollView;
    LinearLayout header_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);
    }

    @Override
    protected void onStart() {
        super.onStart();
        device_name = findViewById(R.id.deviceNameTXT);
        device_state = findViewById(R.id.deviceStateTXT);
        change_password = findViewById(R.id.changePasswordLayout);
        password_txt = findViewById(R.id.updatePasswordTXT);
        copy = findViewById(R.id.copySmartContractLayout);
        default_layout = findViewById(R.id.DefaultLayout);
        connect_layout = findViewById(R.id.ConnectDevicelayout);
        generate_layout = findViewById(R.id.generateLayout);
        header_layout = findViewById(R.id.deviceheader);
        delete_device = findViewById(R.id.deleteDevice);
        default_device_imagebutton = findViewById(R.id.default_device);
        DefaultTxt = findViewById(R.id.DefaultTxt);
        bar = findViewById(R.id.user_device_details_progress);
        scrollView = findViewById(R.id.scrollview_Action);

        logged_in_user = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        smart_contract_default = logged_in_user.getString("smart_contract_default_device", "null"); // default smart contract
        crypto_address = logged_in_user.getString("address","null");
        _logged_in_user_id = logged_in_user.getString("user_id","-1");


        intent = getIntent();
        device_name.setText(intent.getStringExtra("device_name"));
        smart_contract_device = intent.getStringExtra("device_smart_contract");

        // getting the current state of the device from the blockchain
        Get_Device_State();

        default_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currently_set_as_default)
                    remove_device_as_default(_logged_in_user_id);
                else
                    set_device_as_default(_logged_in_user_id,smart_contract_device);
            }
        });

        connect_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirecting to bluetooth layout to search for nearby devices and return with the bluetooth mac address
                Intent i = new Intent(getApplicationContext(), Device_control.class);
                i.putExtra("ACTIVITY","owner_access_device");
                startActivity(i);
            }
        });

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currently_set_as_default)
                    openDialog();
                else
                    Toast.makeText(view.getContext(),"Set this device as default",Toast.LENGTH_SHORT).show();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(view.getContext(),smart_contract_device);
            }
        });

        generate_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // retrieving the public key
                String private_key = "0x" + crypto_address;
                Credentials credentials = Credentials.create(private_key);
                String public_address = credentials.getAddress();
                String device_mac_address = intent.getStringExtra("device_name");
                String combine_address = public_address + "\n" + smart_contract_device + "\n" + device_mac_address;
                GenerateQRCode(combine_address); // generating qr code
            }
        });

        delete_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Device_Details.this,R.style.Base_Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Confirm");
                builder.setMessage("Are you sure?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        remove_device(smart_contract_device,intent.getStringExtra("device_name"));
                        dialog.dismiss();
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
    * copying the provided content in this case the smart contract to be displayed or printed for the courier
     */
    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context,"Smart Contract Copied",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opening a custom dialog layout to enter the new password for the device
     * Once the password has been entered , the enter button must be clicked to trigger the blockchain event and add another block to the ledger
     */
    public void openDialog() {
        dialog = new Dialog(this); // Context, this, etc.
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
                    Update_Device_Password(password);
                    dialog.dismiss();
                }else{
                    error_password.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * Updating device's password on the blockchain
     * @param device_password
     */
    public void Update_Device_Password(String device_password){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        // updating UI
        password_txt.setText("Updating Password..");

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
                    String public_address = credentials.getAddress();
                    // load smart contract and call update password method to change the current password on the device
                    Device smart_contract = Device.load(smart_contract_device,web3,rawTransactionManager,Constants.GAS_PRICE,Constants.GAS_LIMIT);
                    TransactionReceipt transactionReceipt = smart_contract.UpdatePassword(public_address,device_password).send();
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
                        if (tx_receipt[0] != null)
                            Toast.makeText(getApplicationContext(), "Device password updated", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "You might have entered an incorrect password", Toast.LENGTH_SHORT).show();

                        password_txt.setText("Change Password");
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

    /**
     * Generating a Bitmap image with the provided content in this case the public crypto address from the user.
     */
    public void GenerateQRCode(String content) {

        // requesting storage permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        // if permission is granted then we will generated a qr code
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Storage Permission was not granted.", Toast.LENGTH_LONG).show();
        }else {
            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                saveToInternalStorage(bmp);
                Toast.makeText(getApplicationContext(), "QR Code saved", Toast.LENGTH_SHORT).show();

            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Storing the QR Code generated inside the downloads folder inside the android phone.
     * The QR Code contains the public crypto address of the owner of the device.
     * This is meant to be either printed and placed on the Locker or other device to make sure that the Courier scans it for access
     */
    private String saveToInternalStorage(Bitmap bitmapImage){

        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); // path to downloads
        File path = new File(directory,"qrcode.jpg"); // name image
        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    /**
    * Removing device from the centralized database
    */
    public void remove_device(String smart_contract,String device_name){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.get_user_device;
        final String [] result = {" "};
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
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                    String data = URLEncoder.encode("smart_contract", "UTF-8") + "=" + URLEncoder.encode(smart_contract, "UTF-8")
                            +"&&"+ URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(device_name, "UTF-8")
                            +"&&"+ URLEncoder.encode("delete_device", "UTF-8") + "=" + URLEncoder.encode("OK", "UTF-8");
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
                            // updating sessions
                            currently_set_as_default = false;
                            SharedPreferences.Editor editor = logged_in_user.edit();
                            editor.putString("smart_contract_default_device", null);
                            editor.apply();

                            startActivity(new Intent(getApplicationContext(),user_dashboard.class));
                            Toast.makeText(getApplicationContext(), "Device Removed", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Device was not removed", Toast.LENGTH_SHORT).show();
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
    /**
     * Retrieving the smart contract of this device from the centralized database
     */
    public void retrieving_default_device(String user_id){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.get_user_device_default;
        final String [] result = {" "};

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
                        bar.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                        header_layout.setVisibility(View.VISIBLE);

                        //UI Thread work here
                         if (result[0].trim().equals(smart_contract_device)) {
                             SharedPreferences.Editor editor = logged_in_user.edit();
                             editor.putBoolean("default_device_retrieved",true);
                             editor.putString("smart_contract_default_device", smart_contract_device);
                             editor.apply();
                             // checking if the current select smart contract is equal to the select device, if so we are going to change the icon of the image button
                            default_device_imagebutton.setBackground(getResources().getDrawable(R.drawable.star_device));
                            DefaultTxt.setText("Remove as Default");
                            currently_set_as_default = true;
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

    /**
     * Setting device as default from the centralized database
     */
    public void set_device_as_default(String user_id,String smart_contract){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        DefaultTxt.setText("Processing..");

        String url_add_user = Constants.api_url + Constants.get_user_device_default;
        final String [] result = {" "};
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
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                    String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")
                            +"&&"+ URLEncoder.encode("smart_contract", "UTF-8") + "=" + URLEncoder.encode(smart_contract, "UTF-8")
                            +"&&"+ URLEncoder.encode("set_default_device", "UTF-8") + "=" + URLEncoder.encode("OK", "UTF-8");
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
                            currently_set_as_default = !currently_set_as_default;
                            SharedPreferences.Editor editor = logged_in_user.edit();

                            if(currently_set_as_default) {
                                default_device_imagebutton.setBackground(getResources().getDrawable(R.drawable.star_device));
                                editor.putString("smart_contract_default_device", smart_contract_device);
                                DefaultTxt.setText("Remove as Default");
                                Toast.makeText(Device_Details.this, "Device selected as default", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                editor.putString("smart_contract_default_device", null);
                                default_device_imagebutton.setBackground(getResources().getDrawable(R.drawable.star_device_outline));
                                DefaultTxt.setText("Set as Default");
                                Toast.makeText(Device_Details.this, "Device Removed as default", Toast.LENGTH_SHORT).show();
                            }
                            editor.apply();
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
    /**
     * Removing device as default from the centralized database
     */
    public void remove_device_as_default(String user_id){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        DefaultTxt.setText("Processing..");

        String url_add_user = Constants.api_url + Constants.get_user_device_default;
        final String [] result = {" "};
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
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                    String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")
                            +"&&"+ URLEncoder.encode("remove_default_device", "UTF-8") + "=" + URLEncoder.encode("OK", "UTF-8");
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
                            currently_set_as_default = !currently_set_as_default;
                            SharedPreferences.Editor editor = logged_in_user.edit();
                            editor.putString("smart_contract_default_device", "null");
                            default_device_imagebutton.setBackground(getResources().getDrawable(R.drawable.star_device_outline));
                            DefaultTxt.setText("Set as Default");
                            Toast.makeText(Device_Details.this, "Device Removed as default", Toast.LENGTH_SHORT).show();
                            editor.apply();
                        }
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
        bar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        header_layout.setVisibility(View.GONE);

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
                    Device smart_contract = Device.load(smart_contract_device,web3,rawTransactionManager,Constants.GAS_PRICE,Constants.GAS_LIMIT);

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
                        if (current_state[0])
                            device_status = "Device Available";
                        else if (!current_state[0])
                            device_status = "Device Not Available";

                        device_state.setText(device_status);

                        if(!logged_in_user.getBoolean("default_device_retrieved",false)) {
                            // retrieving default device from the centralized database (once)
                            retrieving_default_device(_logged_in_user_id);
                        }
                        else{
                            bar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                            header_layout.setVisibility(View.VISIBLE);

                            if (smart_contract_default.equals(smart_contract_device)) {
                                default_device_imagebutton.setBackground(getResources().getDrawable(R.drawable.star_device));
                                DefaultTxt.setText("Remove as Default");
                                currently_set_as_default = true;
                            }
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
}