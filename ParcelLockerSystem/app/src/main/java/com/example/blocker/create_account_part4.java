package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

public class create_account_part4 extends AppCompatActivity {

    Button continueBTN;
    TextInputEditText mobile;
    TextView mobile_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_part4);
    }

    @Override
    protected void onStart() {
        super.onStart();
        continueBTN = findViewById(R.id.continueBTN);
        mobile = findViewById(R.id.MobileINP);
        mobile_error = findViewById(R.id.errorMobileTXT);

        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid(mobile.getText().toString())) {
                    Intent get_intent_data = getIntent();
                    Intent part_4 = new Intent(create_account_part4.this, create_account_part5.class);
                    part_4.putExtra("first_name", get_intent_data.getStringExtra("first_name"));
                    part_4.putExtra("last_name", get_intent_data.getStringExtra("last_name"));
                    part_4.putExtra("role", get_intent_data.getStringExtra("role"));
                    part_4.putExtra("date", get_intent_data.getStringExtra("date"));
                    part_4.putExtra("gender", get_intent_data.getStringExtra("gender"));
                    part_4.putExtra("mobile", mobile.getText().toString());
                    startActivity(part_4);

                }

            }
        });

    }
    // validation on null values and mobile format
    private boolean isInputValid(String user_mobile) {
        boolean isErrorFound = false;
        String mobile_regex = "^[+]?[0-9]{8,15}$";

        if (user_mobile.isEmpty()) {
            mobile_error.setVisibility(View.VISIBLE);
            mobile_error.setText(getResources().getString(R.string.error_generic));
            isErrorFound = false;
        } else {
            mobile_error.setVisibility(View.INVISIBLE);
            isErrorFound = true;
        }

        if(!Pattern.matches(mobile_regex,user_mobile)){
            mobile_error.setVisibility(View.VISIBLE);
            mobile_error.setText(getResources().getString(R.string.error_mobile));
            isErrorFound = false;
        } else {
            mobile_error.setVisibility(View.INVISIBLE);
            isErrorFound = true;
        }
        return isErrorFound;
    }
}