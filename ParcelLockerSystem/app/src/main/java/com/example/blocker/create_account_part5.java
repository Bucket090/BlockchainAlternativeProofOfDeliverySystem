package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class create_account_part5 extends AppCompatActivity {
    TextView emailErrorText;
    TextInputEditText email;
    Button continueBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_part5);
    }

    @Override
    protected void onStart() {
        super.onStart();
        emailErrorText = findViewById(R.id.errorEmailTXT2);
        email = findViewById(R.id.emailINP);
        continueBTN = findViewById(R.id.continueBTN);

        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEmail(email.getText().toString());
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

    // validation on null values and on email
    private boolean isInputValid(String email_inp,String response){
        boolean isErrorFound = false;
        boolean isErrorFound2 = false;
        String email_regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        if(email_inp.isEmpty() || !Pattern.matches(email_regex,email_inp)) {
            emailErrorText.setVisibility(View.VISIBLE);
            emailErrorText.setText(getResources().getString(R.string.error_email_msg));
            isErrorFound =  false;
        }else {
            emailErrorText.setVisibility(View.INVISIBLE);
            isErrorFound = true;
        }
         // response = 1 , email exists , response = 0 , email does not exist
        if(response.equals("1")) {
            emailErrorText.setVisibility(View.VISIBLE);
            emailErrorText.setText(getResources().getString(R.string.error_email_msg2));
            isErrorFound2 =  false;
        }else {
            emailErrorText.setVisibility(View.INVISIBLE);
            isErrorFound2 = true;
        }

        if(isErrorFound == true && isErrorFound2 == true)
            return true;

        return false;
    }
    /**
     * This method creates a thread (executor service) to handle background work which sends the data and gets back the response from the db
     * whilst the handler handles the ui part which is the validation in this case
     * Note that AsyncTask are deprecated in API 30 hence it was replaced by the method checkEmail which consist of a single thread and a handler
     * A POST Request was used because the response given by the server wasn't refreshed if we use a GET Request to check if email exists
     * hence a post request kept getting the actual response from the server.
     */
    public void checkEmail(String email){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_add_user = Constants.api_url + Constants.get_user;
        final String [] result = {" "};
        final String[] query_response = {" "};

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
                    String data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
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
                    query_response[0] = obj.getString("success");


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
                        if (isInputValid(email, query_response[0])) {
                            Intent get_intent_data = getIntent();
                            Intent part_5 = new Intent(create_account_part5.this, create_account_part6.class);
                            part_5.putExtra("first_name", get_intent_data.getStringExtra("first_name"));
                            part_5.putExtra("last_name", get_intent_data.getStringExtra("last_name"));
                            part_5.putExtra("role",get_intent_data.getStringExtra("role"));
                            part_5.putExtra("date", get_intent_data.getStringExtra("date"));
                            part_5.putExtra("gender", get_intent_data.getStringExtra("gender"));
                            part_5.putExtra("mobile", get_intent_data.getStringExtra("mobile"));
                            part_5.putExtra("email", email);

                            startActivity(part_5); // launch next activity
                        }
                    }
                },500);
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
}