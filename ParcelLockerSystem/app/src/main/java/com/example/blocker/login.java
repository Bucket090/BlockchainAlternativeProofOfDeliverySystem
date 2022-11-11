package com.example.blocker;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class login extends AppCompatActivity {
    private final static String SHARED_PREFS = "login_session";
    public TextView signupBTN,emailErrorText,passwordErrorText;
    public Button loginBTN;
    protected TextInputEditText emailText;
    protected TextInputEditText passwordText;
    String hashed_password;
    SharedPreferences sharedpreferences;
    Intent login_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        signupBTN = (TextView) findViewById(R.id.signupTXT);
        loginBTN = (Button) findViewById(R.id.loginBTN);
        passwordText = (TextInputEditText) findViewById(R.id.passwordINP);
        emailText = (TextInputEditText) findViewById(R.id.emailINP);
        emailErrorText = findViewById(R.id.erroremailTXT);
        passwordErrorText = findViewById(R.id.error_password_msgTXT);
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // checking if shared preferences is not empty
        boolean already_logged_In = sharedpreferences.contains("login");
            if(already_logged_In)
                if(sharedpreferences.getString("role","null").equals("Owner"))
                    startActivity(new Intent(getApplicationContext(),user_dashboard.class));
                else if(sharedpreferences.getString("role","null").equals("Courier"))
                    startActivity(new Intent(getApplicationContext(),courier_dashboard.class));

        signupBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), login_question.class));
            }
        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_user(emailText.getText().toString(),passwordText.getText().toString());
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Validates the input by checking if the email inputted by the user is valid and the password supplied is an acceptable password.
     * Still need to implement firebase feature ****
     * */
    protected boolean isInputValid(String email, String password){

        String email_regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        boolean isErrorFound = false;
        boolean isErrorFound_email = false;

        if(email.isEmpty() || !Pattern.matches(email_regex,email)) {
            emailErrorText.setVisibility(View.VISIBLE);
            emailErrorText.setText(getResources().getString(R.string.error_email_msg));
            isErrorFound_email = false;
         }else {
            emailErrorText.setVisibility(View.INVISIBLE);
            isErrorFound_email = true;
        }

        if(password.isEmpty()){
            passwordErrorText.setVisibility(View.VISIBLE);
            passwordErrorText.setText(getResources().getString(R.string.error_generic));
            isErrorFound = false;
        }else {
            passwordErrorText.setVisibility(View.GONE);
            isErrorFound = true;
        }

        if(isErrorFound_email == true && isErrorFound == true)
            return true;

        return false;
    }

    /**
     * checking if user credentials are valid, if so login will be granted
     * @param email
     * @param password
     */
    protected void check_user(String email , String password){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_check_user_credentials = Constants.api_url + Constants.get_user_login;
        final String[] result = {" "};
        final String[] response = {" "};

        HashingData hash_data = new HashingData();
        try{
            hashed_password = hash_data.SHA1(password);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

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
                    String data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")
                            +"&&"+ URLEncoder.encode("pass","UTF-8")+"="+URLEncoder.encode(hashed_password,"UTF-8")
                            +"&&"+ URLEncoder.encode("login_session","UTF-8")+"="+URLEncoder.encode("true","UTF-8");
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        if (isInputValid(email,password.toString())) {

                            if (response[0].equals("1"))
                                get_user_details(email);
                            else
                                Toast.makeText(getApplicationContext(), "Credentials are incorrect!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }

    /**
     * Getting user details where those details will be stored in a session and to be used at a later stage in the dashboard
     * @param email
     */
    private void get_user_details(String email){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_check_user_credentials = Constants.api_url + Constants.get_user_login;
        final String[] result = {" "};
        final String [] my_data = {null,null,null,null,null};

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
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
                            + "&&" + URLEncoder.encode("login_session_verified", "UTF-8") + "=" + URLEncoder.encode("true", "UTF-8");
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
                    my_data[0] = obj.getJSONObject(0).getString("First_Name");
                    my_data[1] = obj.getJSONObject(0).getString("Last_Name");
                    my_data[2] = obj.getJSONObject(0).getString("User_Id");
                    my_data[3] = obj.getJSONObject(0).getString("Role_Name");
                    my_data[4] = obj.getJSONObject(0).getString("Crypto_Address");
                } catch (MalformedURLException e) {
                    result[0] = e.getMessage();
                } catch (IOException e) {
                    result[0] = e.getMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Set login session
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("full_name", my_data[0] + " " + my_data[1]);
                        editor.putString("email", email);
                        editor.putString("user_id", my_data[2]);
                        editor.putString("role", my_data[3]);
                        editor.putString("address", my_data[4]);

                        editor.putBoolean("login", true);
                        editor.apply();

                        if(my_data[3].equals("Owner"))
                            login_intent = new Intent(getApplicationContext(),user_dashboard.class);
                        else if(my_data[3].equals("Courier"))
                            login_intent = new Intent(getApplicationContext(),courier_dashboard.class);

                        startActivity(login_intent);
                    }
                });
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}