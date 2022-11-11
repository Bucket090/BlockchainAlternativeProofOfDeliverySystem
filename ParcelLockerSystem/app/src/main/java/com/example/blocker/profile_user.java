package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

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
import java.util.regex.Pattern;

public class profile_user extends AppCompatActivity {
    TextView gender,mobile,dob,full_name,full_addr,role;
    TextInputEditText name,surname,email_inp,mobile_inp,gender_inp,dob_inp,flat_no_inp,street_no_inp,street_name_inp,locality_inp,country_inp,crypto_address;
    Button update_user;
    ProgressBar bar;
    ScrollView scrollView;
    GridLayout gridLayout;
    RelativeLayout relativeLayout;
    private final static String SHARED_PREFS = "login_session";
    private ProfileAccount owner;
    SharedPreferences sharedpreferences;
    private String _logged_in_user_email;
    private String _logged_in_user_id;
    private String _logged_in_role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gender = findViewById(R.id.genderTXT);
        mobile = findViewById(R.id.mobileTXT);
        dob = findViewById(R.id.dobTXT);
        full_name = findViewById(R.id.fullnameTXT);
        full_addr = findViewById(R.id.fulladdressTXT);
        role = findViewById(R.id.roleTXT);
        name = findViewById(R.id.firstNameINP);
        surname = findViewById(R.id.lastNameINP);
        email_inp = findViewById(R.id.emailINP);
        mobile_inp = findViewById(R.id.MobileINP);
        gender_inp = findViewById(R.id.genderINP);
        dob_inp = findViewById(R.id.DOBINP);
        flat_no_inp = findViewById(R.id.flatNumberINP);
        street_no_inp = findViewById(R.id.streetNumberINP);
        street_name_inp = findViewById(R.id.streetNameINP);
        locality_inp = findViewById(R.id.localityINP);
        country_inp = findViewById(R.id.countryINP);
        crypto_address = findViewById(R.id.cryptoaddrINP);
        update_user = findViewById(R.id.update_UserBTN);
        bar = findViewById(R.id.profile_loading);
        scrollView = findViewById(R.id.profile_detailsView);
        gridLayout = findViewById(R.id.columnLayout);
        relativeLayout = findViewById(R.id.Header_layout);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        _logged_in_user_email = sharedpreferences.getString("email","none");
        _logged_in_user_id = sharedpreferences.getString("user_id","-1");
        _logged_in_role = sharedpreferences.getString("role","role");
        // call executor service
        get_user_data();
        update_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid())
                    update_profile();
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
    * Retrieving data from the centralized database
    */
    private void get_user_data()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_check_user_credentials = Constants.api_url + Constants.get_user;
        final String[] result = {" "};
        final JSONArray[] json_array = {null};

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
                    String data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(_logged_in_user_email,"UTF-8")
                            +"&&"+ URLEncoder.encode("user_data_profile","UTF-8")+"="+URLEncoder.encode("true","UTF-8");
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

                    json_array[0] = new JSONArray(result[0]);


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
                        try {
                            // Headers
                            full_name.setText((json_array[0].getJSONObject(0).get("First_Name")) + " " +(json_array[0].getJSONObject(0).get("Last_Name")));
                            full_addr.setText(Format_Address(json_array[0].getJSONObject(0).get("Flat_No").toString(),
                                    json_array[0].getJSONObject(0).getString("Street_No"),
                                    json_array[0].getJSONObject(0).getString("Street_Name"),
                                    json_array[0].getJSONObject(0).getString("Locality"),
                                    json_array[0].getJSONObject(0).getString("Country")));
                            //columns data
                            gender.setText(Format_Gender(json_array[0].getJSONObject(0).getString("Gender").charAt(0)));
                            mobile.setText(json_array[0].getJSONObject(0).getString("Mobile"));
                            dob.setText(json_array[0].getJSONObject(0).getString("DOB"));
                            role.setText(_logged_in_role);
                            // input fields
                            name.setText(json_array[0].getJSONObject(0).getString("First_Name"));
                            surname.setText(json_array[0].getJSONObject(0).getString("Last_Name"));
                            email_inp.setText(json_array[0].getJSONObject(0).getString("Email"));
                            mobile_inp.setText(json_array[0].getJSONObject(0).getString("Mobile"));
                            gender_inp.setText(Format_Gender(json_array[0].getJSONObject(0).getString("Gender").charAt(0)));
                            dob_inp.setText(json_array[0].getJSONObject(0).getString("DOB"));
                            flat_no_inp.setText(json_array[0].getJSONObject(0).getString("Flat_No"));
                            street_no_inp.setText(json_array[0].getJSONObject(0).getString("Street_No"));
                            street_name_inp.setText(json_array[0].getJSONObject(0).getString("Street_Name"));
                            locality_inp.setText(json_array[0].getJSONObject(0).getString("Locality"));
                            country_inp.setText(json_array[0].getJSONObject(0).getString("Country"));
                            crypto_address.setText(json_array[0].getJSONObject(0).getString("Crypto_Address"));

                            bar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                            gridLayout.setVisibility(View.VISIBLE);
                            relativeLayout.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
    /**
     * Updating user on the centralized database
     */
    private void update_profile()
    {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        String url_check_user_credentials = Constants.api_url + Constants.call_user;
        final String[] result = {" "};

        bar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);

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
                    String data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email_inp.getText().toString(),"UTF-8")
                            +"&&"+ URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name.getText().toString(),"UTF-8")
                            +"&&"+ URLEncoder.encode("surname","UTF-8")+"="+URLEncoder.encode(surname.getText().toString(),"UTF-8")
                            +"&&"+ URLEncoder.encode("gender","UTF-8")+"="+URLEncoder.encode(Convert_Formatted_Gender(gender_inp.getText().toString()),"UTF-8")
                            +"&&"+ URLEncoder.encode("dob","UTF-8")+"="+URLEncoder.encode(dob_inp.getText().toString(),"UTF-8")
                            +"&&"+ URLEncoder.encode("mobile","UTF-8")+"="+URLEncoder.encode(mobile_inp.getText().toString(),"UTF-8")
                            +"&&"+ URLEncoder.encode("flat_no","UTF-8")+"="+URLEncoder.encode(flat_no_inp.getText().toString(),"UTF-8")
                            +"&&"+ URLEncoder.encode("street_no","UTF-8")+"="+URLEncoder.encode(street_no_inp.getText().toString(),"UTF-8")
                            +"&&"+ URLEncoder.encode("street_name","UTF-8")+"="+URLEncoder.encode(street_name_inp.getText().toString(),"UTF-8")
                            +"&&"+ URLEncoder.encode("locality","UTF-8")+"="+URLEncoder.encode(locality_inp.getText().toString(),"UTF-8")
                            +"&&"+ URLEncoder.encode("country","UTF-8")+"="+URLEncoder.encode(country_inp.getText().toString(),"UTF-8")
                            +"&&"+ URLEncoder.encode("crypto_address","UTF-8")+"="+URLEncoder.encode(crypto_address.getText().toString(),"UTF-8")
                            +"&&"+ URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode("null","UTF-8") // password won't be actually updated
                            +"&&"+ URLEncoder.encode("user_Id","UTF-8")+"="+URLEncoder.encode(_logged_in_user_id,"UTF-8")
                            +"&&"+ URLEncoder.encode("update_user","UTF-8")+"="+URLEncoder.encode("OK","UTF-8");
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

                    owner = new ProfileAccount();
                    owner.setFirst_Name(name.getText().toString());
                    owner.setLast_Name(surname.getText().toString());
                    owner.setMobile_No(mobile_inp.getText().toString());
                    owner.setEmail(email_inp.getText().toString());
                    owner.setGender(gender_inp.getText().toString().charAt(0));
                    owner.setDOB(dob_inp.getText().toString());
                    owner.setCrypto_Address(crypto_address.getText().toString());
                    owner.setFlat_No(flat_no_inp.getText().toString());
                    owner.setStreet_No(street_no_inp.getText().toString());
                    owner.setStreet_Name(street_name_inp.getText().toString());
                    owner.setLocality(locality_inp.getText().toString());
                    owner.setCountry(country_inp.getText().toString());
                    owner.setFlat_No(flat_no_inp.getText().toString());
                    owner.setStreet_No(street_no_inp.getText().toString());
                    owner.setStreet_Name(street_name_inp.getText().toString());

                } catch (IOException e) {
                    result[0] = e.getMessage();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        // Headers
                        full_name.setText(owner.getFirst_Name() + " " + owner.getLast_Name());
                        full_addr.setText(Format_Address(owner.getFlat_No(),owner.getStreet_No(),owner.getStreet_Name(),owner.getLocality(),owner.getCountry()));
                        //columns data
                        gender.setText(Format_Gender(owner.getGender()));
                        mobile.setText(owner.getMobile_No());
                        dob.setText(owner.getDOB());
                        role.setText(_logged_in_role);
                        // input fields
                        name.setText(owner.getFirst_Name());
                        surname.setText(owner.getLast_Name());
                        email_inp.setText(owner.getEmail());
                        mobile_inp.setText(owner.getMobile_No());
                        gender_inp.setText(Format_Gender(owner.getGender()));
                        dob_inp.setText(owner.getDOB());
                        flat_no_inp.setText(owner.getFlat_No());
                        street_no_inp.setText(owner.getStreet_No());
                        street_name_inp.setText(owner.getStreet_Name());
                        locality_inp.setText(owner.getLocality());
                        country_inp.setText(owner.getCountry());
                        crypto_address.setText(owner.getCrypto_Address());

                        // updating UI
                        bar.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);

                        // if name or crypto address is changed shared prefs will be updated as well
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("full_name", full_name.getText().toString());
                        editor.putString("address", owner.getCrypto_Address());
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        // shutting down the thread to free up that space which we occupied
        executor.shutdown();
    }
    // Formatting address in a specific way returning the outcome based upon the given values
    private String Format_Address(String flat_no, String street_no,String street_name,String locality, String country)  {
        String format = null;

        if(flat_no.length() == 0)
            format = street_no + "," + street_name + "," + locality + "," + country;
        else if (street_no.length() == 0)
            format = flat_no + "," + street_name + "," + locality + "," + country;
        else if (flat_no.length() == 0 && street_no.length() == 0)
            format = street_name + "," + locality + "," + country;
        else if (flat_no.length() > 0 &&  street_no.length() > 0)
            format = flat_no + "," +  street_name + "," + locality + "," + country;

        return format;
    }
    // converting the input to the word describing the letter of the given gender
    private String Format_Gender(char gender) {
        String format = null;

        if(gender == 'm')
            format = "male";
        else if(gender == 'f')
            format = "female";
        else if(gender == 'o')
            format = "other";

        return format;
    }
    // converting the input of the word to the letter of the given gender
    private String Convert_Formatted_Gender(String gender)
    {
        String format = null;

        if(gender.toLowerCase().equals("male"))
            format = "m";
        else if(gender.toLowerCase().equals("female"))
            format = "f";
        else if(gender.toLowerCase().equals("other"))
            format = "o";

        return format;
    }
    // validation for null values , mobile format and date format and also for a specific gender format
    private boolean isInputValid(){
        String mobile_regex = "^[+]?[0-9]{8,15}$";
        String date_regex = "^(1[0-9]|0[1-9]|3[0-1]|2[1-9])/(0[1-9]|1[0-2])/[0-9]{4}$";

        // mobile format validation
        if (!Pattern.matches(mobile_regex,mobile_inp.getText().toString())){
            Toast.makeText(this, getResources().getString(R.string.error_mobile), Toast.LENGTH_SHORT).show();
            return false;
        }

        // date format validation
        if(!Pattern.matches(date_regex,dob_inp.getText().toString())){
            Toast.makeText(this, getResources().getString(R.string.error_dob), Toast.LENGTH_SHORT).show();
            return false;
        }

        // general validation
        if (name.getText().length() <= 1 || surname.getText().length() <= 1 || email_inp.getText().length() <= 1
            || street_name_inp.getText().length() <= 1 || locality_inp.getText().length() <= 1 || country_inp.getText().length() <= 1){

            Toast.makeText(this, "Something is missing, check input !", Toast.LENGTH_SHORT).show();
            return false;
        }

        // gender format validation
        String gender_check = gender_inp.getText().toString().toLowerCase();
        boolean isGenderIncorrect = false;
        if (!gender_check.equals("male"))
            isGenderIncorrect = true;
        else {
            return true;
        }
        if (!gender_check.equals("female"))
            isGenderIncorrect = true;
        else {
            return true;
        }
        if (!gender_check.equals("other"))
            isGenderIncorrect = true;
        else {
            return true;
        }

        if(isGenderIncorrect) {
            Toast.makeText(this, "Fill out gender properly!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}