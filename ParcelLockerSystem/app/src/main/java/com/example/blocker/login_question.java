package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class login_question extends AppCompatActivity {

    Button continueBTN;
    LinearLayout OwnerBTN, CourierBTN;
    boolean isOccupationSelected = false;
    String chosen_occupation = "Owner"; // owner by default
    TextView error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_question);
    }

    @Override
    protected void onStart() {
        super.onStart();
        OwnerBTN = findViewById(R.id.OwnerBTN);
        CourierBTN = findViewById(R.id.courierBTN);
        continueBTN = findViewById(R.id.continueBTN);
        error = findViewById(R.id.errorOccupationTXT);

        OwnerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OwnerBTN.setBackgroundColor(Color.parseColor("#a4bfef"));
                CourierBTN.setBackgroundColor(Color.parseColor("#FFFFFF"));
                isOccupationSelected = true;
                chosen_occupation = "Owner";
            }
        });

        CourierBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CourierBTN.setBackgroundColor(Color.parseColor("#a4bfef"));
                OwnerBTN.setBackgroundColor(Color.parseColor("#FFFFFF"));
                isOccupationSelected = true;
                chosen_occupation = "Courier"; // courier
            }
        });

        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid(isOccupationSelected)){
                    Intent login_question = new Intent(login_question.this,create_account_part1.class);
                    login_question.putExtra("role",chosen_occupation);
                    startActivity(login_question);
                }
            }
        });
    }

    // validation on selection
    private boolean isInputValid(boolean isSelected) {
        boolean isErrorFound = false;

        if (!isSelected) {
            error.setVisibility(View.VISIBLE);
            error.setText(getResources().getString(R.string.error_generic));
            isErrorFound = false;
            chosen_occupation = null;
        } else {
            error.setVisibility(View.GONE);
            isErrorFound = true;
        }

        return isErrorFound;
    }
}
