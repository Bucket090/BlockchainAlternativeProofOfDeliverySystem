package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class view_registered_devices extends AppCompatActivity {

    public static final String SHARED_PREFS = "login_session";
    SharedPreferences logged_in_user;
    ProgressBar bar;
    private ListView listOfDevices;
    private ArrayAdapter<String> adapterRegisteredDevices;
    String smart_contract_default,user_id = null;
    boolean device_state = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_registered_devices);
    }

    @Override
    protected void onStart() {
        super.onStart();

        listOfDevices = findViewById(R.id.list_registered_devices);
        // loading session data
        logged_in_user = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        user_id = logged_in_user.getString("user_id", "-1");
        device_state = logged_in_user.getBoolean("device_state_access", true);
        smart_contract_default = logged_in_user.getString("smart_contract_default_device", "null");

        adapterRegisteredDevices = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextSize(14);
                return textView;
            }
        };
        retrieve_user_devices(); // retrieving devices
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void retrieve_user_devices(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.get_user_device;
        final String [] result = {" "};
        final String [] my_devices = {null,null};

        bar = findViewById(R.id.device_loading);
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
                            +"&&"+ URLEncoder.encode("show_devices", "UTF-8") + "=" + URLEncoder.encode("OK", "UTF-8");
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

                    JSONArray obj = null;
                    obj = new JSONArray(result[0]);
                    for (int i = 0 ; i < obj.length();i++) {

                        my_devices[0] = obj.getJSONObject(i).getString("registered_device_id");
                        my_devices[1] = obj.getJSONObject(i).getString("smart_contract");
                        adapterRegisteredDevices.add(my_devices[0] + "\n" + my_devices[1]); // storing result in a list
                    }

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
                        bar.setVisibility(View.GONE);
                        listOfDevices.setVisibility(View.VISIBLE);
                        listOfDevices.setAdapter(adapterRegisteredDevices);
                        listOfDevices.setOnItemClickListener(myListClickListener);
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String device_name = info.split("\n")[0];
            String device_smart_contract = info.split("\n")[1];

            Intent intent = new Intent(view_registered_devices.this,Device_Details.class);
            intent.putExtra("device_name",device_name);
            intent.putExtra("device_smart_contract",device_smart_contract);
            startActivity(intent);
        }
    };
}