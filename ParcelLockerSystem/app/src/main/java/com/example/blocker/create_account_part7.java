package com.example.blocker;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class create_account_part7 extends AppCompatActivity {
    String hashed_password;
    TextView errorPasswordText, errorConfirmPasswordText;
    Button create_accountBTN;
    TextInputEditText password, passwordConfirm;
    ProfileAccount owner;
    ProfileAccount new_account;
    Intent part_7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_part7);
    }

    @Override
    protected void onStart() {
        super.onStart();
        errorConfirmPasswordText = findViewById(R.id.errorConfirmPasswordTxt);
        errorPasswordText = findViewById(R.id.errorPasswordTxt);
        create_accountBTN = findViewById(R.id.createAccountBTN);
        password = findViewById(R.id.newpasswordINP);
        passwordConfirm = findViewById(R.id.repeatpasswordINP);

        Intent get_intent_data = getIntent();
        part_7 = new Intent(create_account_part7.this, login.class);
        new_account = new ProfileAccount();
        owner = new ProfileAccount();

        create_accountBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Setting up owner details into the owner class
                owner.setFirst_Name(get_intent_data.getStringExtra("first_name").trim());
                owner.setLast_Name(get_intent_data.getStringExtra("last_name").trim());
                owner.setRole(get_intent_data.getStringExtra("role"));
                owner.setEmail(get_intent_data.getStringExtra("email").trim());
                owner.setMobile_No(get_intent_data.getStringExtra("mobile").trim());
                owner.setFlat_No(get_intent_data.getStringExtra("flat_number").trim());
                owner.setStreet_No(get_intent_data.getStringExtra("street_number").trim());
                owner.setStreet_Name(get_intent_data.getStringExtra("street_name").trim());
                owner.setLocality(get_intent_data.getStringExtra("locality").trim());
                owner.setCountry(get_intent_data.getStringExtra("country").trim());
                owner.setDOB(get_intent_data.getStringExtra("date").trim());
                char gender = get_intent_data.getStringExtra("gender").charAt(0);
                owner.setGender(gender);
                // Hashing password
                HashingData hash_data = new HashingData();
                try {
                    // hashes the password
                    hashed_password = hash_data.SHA1(password.getText().toString());
                    owner.setPassword(hashed_password);
                    Log.i("Hashed Password ", hashed_password);

                    // adding user to db
                    add_user();

                } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // validation on password for null and if they match
    private boolean isInputValid(String password_inp, String password_confirm_inp) {
        boolean isErrorFound_password = false;
        boolean isErrorFound_con_password = false;
        String password_regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";

        // check password inputted with regex
        if (password_inp.isEmpty() || !Pattern.matches(password_regex, password_inp)) {
            errorPasswordText.setVisibility(View.VISIBLE);
            errorPasswordText.setText(getResources().getString(R.string.error_password_msg));
            isErrorFound_password = false;
        } else {
            errorPasswordText.setVisibility(View.GONE);
            isErrorFound_password = true;
        }
        // check confirm password inputted with regex
        if (password_confirm_inp.isEmpty() || !Pattern.matches(password_regex, password_confirm_inp)) {
            errorConfirmPasswordText.setVisibility(View.VISIBLE);
            errorConfirmPasswordText.setText(getResources().getString(R.string.error_password_msg));
            isErrorFound_con_password = false;
        } else {
            errorConfirmPasswordText.setVisibility(View.GONE);
            isErrorFound_con_password = true;
        }
        // Confirming password and repeat password
        if (password_inp.compareTo(password_confirm_inp) != 0) {
            errorPasswordText.setVisibility(View.VISIBLE);
            errorPasswordText.setText(getResources().getString(R.string.error_password_confirm_msg));
            errorConfirmPasswordText.setVisibility(View.VISIBLE);
            errorConfirmPasswordText.setText(getResources().getString(R.string.error_password_confirm_msg));
            isErrorFound_con_password = false;
            isErrorFound_password = false;
        } else {
            errorPasswordText.setVisibility(View.GONE);
            errorConfirmPasswordText.setVisibility(View.GONE);
            isErrorFound_con_password = true;
            isErrorFound_password = true;
        }
        if (isErrorFound_con_password == true && isErrorFound_password == true)
            return true;

        return false;
    }
    /**
     * This Thread creates a connection between the android app and the db and passes the data inputted by the user.
     * Adding an Owner inside the centralized database
     * */
    public void add_user(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.call_user;
        final String[] result = {" "};
        final String[] response = {" "};

        executor.execute(new Runnable() {
            @Override
            public void run () {
                String email = owner.getEmail();
                String pass = owner.getPassword();
                String name = owner.getFirst_Name();
                String surname = owner.getLast_Name();
                String role = owner.getRole();
                char gender = owner.getGender();
                String dob = owner.getDOB();
                String mobile = owner.getMobile_No();
                String flat_no = owner.getFlat_No();
                String street_no = owner.getStreet_No();
                String street_name = owner.getStreet_Name();
                String locality = owner.getLocality();
                String country = owner.getCountry();

                try {
                    URL url = new URL(url_add_user);
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    http.setRequestMethod("POST");
                    http.setDoInput(true);
                    http.setDoOutput(true);

                    OutputStream ops = http.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops,"UTF-8"));
                    String data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")
                            +"&&"+ URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(pass,"UTF-8")
                            +"&&"+ URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")
                            +"&&"+ URLEncoder.encode("surname","UTF-8")+"="+URLEncoder.encode(surname,"UTF-8")
                            +"&&"+ URLEncoder.encode("role_name","UTF-8")+"="+URLEncoder.encode(role,"UTF-8")
                            +"&&"+ URLEncoder.encode("gender","UTF-8")+"="+URLEncoder.encode(String.valueOf(gender),"UTF-8")
                            +"&&"+ URLEncoder.encode("dob","UTF-8")+"="+URLEncoder.encode(dob,"UTF-8")
                            +"&&"+ URLEncoder.encode("mobile","UTF-8")+"="+URLEncoder.encode(mobile,"UTF-8")
                            +"&&"+ URLEncoder.encode("flat_no","UTF-8")+"="+URLEncoder.encode(flat_no,"UTF-8")
                            +"&&"+ URLEncoder.encode("street_no","UTF-8")+"="+URLEncoder.encode(street_no,"UTF-8")
                            +"&&"+ URLEncoder.encode("street_name","UTF-8")+"="+URLEncoder.encode(street_name,"UTF-8")
                            +"&&"+ URLEncoder.encode("locality","UTF-8")+"="+URLEncoder.encode(locality,"UTF-8")
                            +"&&"+ URLEncoder.encode("country","UTF-8")+"="+URLEncoder.encode(country,"UTF-8")
                            +"&&"+ URLEncoder.encode("crypto_address","UTF-8")+"="+URLEncoder.encode("null","UTF-8")
                            +"&&"+ URLEncoder.encode("add_new_user","UTF-8")+"="+URLEncoder.encode("OK","UTF-8");
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
                        if (isInputValid(password.getText().toString(), passwordConfirm.getText().toString())) {

                            if (response[0].equals("1")) {
                                if (role.equals("Courier"))
                                    add_user_courier();
                                else {
                                    Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                                    startActivity(part_7);
                                }
                            } else
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
    /**
     * Adding a courier to the centralized database
    */
    public void add_user_courier(){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.call_user_courier;
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
                    String data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(owner.getEmail(),"UTF-8")
                            +"&&"+ URLEncoder.encode("role_name","UTF-8")+"="+URLEncoder.encode(owner.getRole(),"UTF-8")
                            +"&&"+ URLEncoder.encode("add_new_user_courier","UTF-8")+"="+URLEncoder.encode("OK","UTF-8");
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
                        if (isInputValid(password.getText().toString(), passwordConfirm.getText().toString())) {

                            if (response[0].equals("1")) {
                                Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_SHORT).show();
                                startActivity(part_7);
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