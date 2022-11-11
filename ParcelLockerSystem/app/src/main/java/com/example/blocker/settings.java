package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class settings extends AppCompatActivity {
    private final static String SHARED_PREFS = "login_session";
    Button profileBTN;
    ImageView ImageProfileBTN;
    CardView LogoutBTN;
    TextView fullname;
    private Intent intent;
    public static final String Activity_User_Profile = "user_profile";
    public static final String Activity_Courier_Profile = "courier_profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onStart() {
        super.onStart();
        profileBTN = findViewById(R.id.editProfileBTN);
        ImageProfileBTN = findViewById(R.id.ProfileBTN);
        LogoutBTN = findViewById(R.id.logoutBTN);
        fullname = findViewById(R.id.fullnameTXT);

        // getting login session details
        SharedPreferences logged_in_user = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String full_name = logged_in_user.getString("full_name","Full Name");
        fullname.setText(full_name);

        ImageProfileBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),profile_user.class));
            }
        });

        profileBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent get_intent_data = getIntent();
                if (get_intent_data.getStringExtra("ACTIVITY").equals(Activity_User_Profile))
                    intent = new Intent(settings.this, profile_user.class);
                else if (get_intent_data.getStringExtra("ACTIVITY").equals(Activity_Courier_Profile))
                    intent = new Intent(settings.this, profile_courier.class);

                startActivity(intent);
            }
        });

        LogoutBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear login Sessions
                SharedPreferences.Editor clear_session = logged_in_user.edit();
                clear_session.clear();
                clear_session.apply();

                startActivity(new Intent(getApplicationContext(),login.class));
            }
        });
    }
}