package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class delivery_notifications extends AppCompatActivity {
    public static final String SHARED_PREFS = "login_session";
    SharedPreferences logged_in_user;
    String user_id;
    ArrayList<Notification> notifications;
    DeliveryListAdapter adapter;
    RecyclerView rvNotifications;
    ProgressBar bar;
    Notification notify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_notifications);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // getting login session details
        logged_in_user = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        user_id = logged_in_user.getString("user_id", "-1");
        rvNotifications = findViewById(R.id.rvNotification);
        bar = findViewById(R.id.notification_loading);
        notifications = new ArrayList<>();
        // loading notifications
        get_notification();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        delete_notification();
    }

    /**
     * Getting notification into the database that this device has been accessed
     */
    public void get_notification(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.call_device_notification;
        final String [] result = {" "};
        final String [] my_data = {null,null,null,null};

        bar.setVisibility(View.VISIBLE);
        rvNotifications.setVisibility(View.GONE);

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
                            +"&&"+ URLEncoder.encode("get_delivery_notification", "UTF-8") + "=" + URLEncoder.encode("OK", "UTF-8");
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
                    my_data[0] = obj.getJSONObject(0).getString("delivery_id");
                    my_data[1] = obj.getJSONObject(0).getString("delivery_message");
                    my_data[2] = obj.getJSONObject(0).getString("Timestamp");
                    my_data[3] = obj.getJSONObject(0).getString("registered_device_id");

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
                        notify = new Notification(my_data[0],my_data[3],my_data[1],my_data[2]);
                        notifications.add(notify);
                        adapter = new DeliveryListAdapter(notifications);
                        rvNotifications.setAdapter(adapter);
                        rvNotifications.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                        bar.setVisibility(View.GONE);
                        rvNotifications.setVisibility(View.VISIBLE);
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

    /**
     * deleting a notification from the centralized database
     */
    public void delete_notification(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.call_device_notification;
        final String [] result = {" "};
        final String [] response = {" "};

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
                            +"&&"+ URLEncoder.encode("remove_delivery_notification", "UTF-8") + "=" + URLEncoder.encode("OK", "UTF-8");
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
                        if (response.equals("1")){
                            Toast.makeText(delivery_notifications.this, "Notifications removed", Toast.LENGTH_SHORT).show();
                        }

                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
}