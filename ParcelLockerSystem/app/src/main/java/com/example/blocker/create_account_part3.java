package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class create_account_part3 extends AppCompatActivity {
    Button continueBTN;
    LinearLayout MaleBTN,FemaleBTN,OtherBTN;
    TextView error_gender;
    boolean isGenderSelected = false;
    String chosen_gender = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_part3);
    }

    @Override
    protected void onStart() {
        super.onStart();
        continueBTN = findViewById(R.id.continueBTN);
        MaleBTN = findViewById(R.id.MaleBTN);
        FemaleBTN = findViewById(R.id.FemaleBTN);
        OtherBTN = findViewById(R.id.OtherBTN);
        error_gender = findViewById(R.id.errorGenderTXT);

        MaleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaleBTN.setBackgroundColor(Color.parseColor("#a4bfef"));
                FemaleBTN.setBackgroundColor(Color.parseColor("#FFFFFF"));
                OtherBTN.setBackgroundColor(Color.parseColor("#FFFFFF"));
                isGenderSelected = true;
                chosen_gender = "m";
            }
        });
        FemaleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaleBTN.setBackgroundColor(Color.parseColor("#FFFFFF"));
                FemaleBTN.setBackgroundColor(Color.parseColor("#a4bfef"));
                OtherBTN.setBackgroundColor(Color.parseColor("#FFFFFF"));
                isGenderSelected = true;
                chosen_gender = "f";
            }
        });
        OtherBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaleBTN.setBackgroundColor(Color.parseColor("#FFFFFF"));
                FemaleBTN.setBackgroundColor(Color.parseColor("#FFFFFF"));
                OtherBTN.setBackgroundColor(Color.parseColor("#a4bfef"));
                isGenderSelected = true;
                chosen_gender = "o";
            }
        });

        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid(isGenderSelected)) {
                    Intent get_intent_data = getIntent();
                    Intent part_3 = new Intent(create_account_part3.this,create_account_part4.class);
                    part_3.putExtra("first_name",get_intent_data.getStringExtra("first_name"));
                    part_3.putExtra("last_name",get_intent_data.getStringExtra("last_name"));
                    part_3.putExtra("role",get_intent_data.getStringExtra("role"));
                    part_3.putExtra("date",get_intent_data.getStringExtra("date"));
                    part_3.putExtra("gender",chosen_gender);
                    startActivity(part_3);
                }
            }
        });
    }
    // validation on the selected option
    private boolean isInputValid(boolean isGenderSelected) {
        boolean isErrorFound = false;

        if (!isGenderSelected) {
            error_gender.setVisibility(View.VISIBLE);
            error_gender.setText(getResources().getString(R.string.error_generic));
            isErrorFound = false;
            chosen_gender = null;
        } else {
            error_gender.setVisibility(View.INVISIBLE);
            isErrorFound = true;
        }

        return isErrorFound;
    }
}