package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class create_account_part6 extends AppCompatActivity {
    TextInputEditText flat_number, street_number,street_name,locality,country;
    TextView error_streetName,error_locality,error_country;
    Button continueBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_part6);
    }

    @Override
    protected void onStart() {
        super.onStart();
        flat_number = findViewById(R.id.flatNumberINP);
        street_number = findViewById(R.id.streetNumberINP);
        street_name = findViewById(R.id.streetNameINP);
        locality = findViewById(R.id.localityINP);
        country = findViewById(R.id.countryINP);
        error_streetName = findViewById(R.id.errorStreetNameTXT);
        error_locality = findViewById(R.id.errorLocalityTXT);
        error_country = findViewById(R.id.errorCountryTXT);
        continueBTN = findViewById(R.id.continueBTN);


        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInputValid(street_name.getText().toString(),locality.getText().toString(),country.getText().toString())) {
                    Intent get_intent_data = getIntent();
                    Intent part_6 = new Intent(create_account_part6.this, create_account_part7.class);
                    part_6.putExtra("first_name",get_intent_data.getStringExtra("first_name"));
                    part_6.putExtra("last_name",get_intent_data.getStringExtra("last_name"));
                    part_6.putExtra("role",get_intent_data.getStringExtra("role"));
                    part_6.putExtra("date",get_intent_data.getStringExtra("date"));
                    part_6.putExtra("gender",get_intent_data.getStringExtra("gender"));
                    part_6.putExtra("mobile",get_intent_data.getStringExtra("mobile"));
                    part_6.putExtra("email", get_intent_data.getStringExtra("email"));
                    part_6.putExtra("flat_number",flat_number.getText().toString());
                    part_6.putExtra("street_number",street_number.getText().toString());
                    part_6.putExtra("street_name",street_name.getText().toString());
                    part_6.putExtra("locality",locality.getText().toString());
                    part_6.putExtra("country",country.getText().toString());
                    startActivity(part_6);
                }

            }
        });
    }

    // validation on null values
    private boolean isInputValid(String street_name,String locality,String country) {
        boolean isErrorFound_street_name = false;
        boolean isErrorFound_locality = false;
        boolean isErrorFound_country = false;

        if (street_name.isEmpty()) {
            error_streetName.setVisibility(View.VISIBLE);
            error_streetName.setText(getResources().getString(R.string.error_generic));
            isErrorFound_street_name = false;
        } else {
            error_streetName.setVisibility(View.INVISIBLE);
            isErrorFound_street_name = true;
        }

        if (locality.isEmpty()) {
            error_locality.setVisibility(View.VISIBLE);
            error_locality.setText(getResources().getString(R.string.error_generic));
            isErrorFound_locality = false;
        } else {
            error_locality.setVisibility(View.INVISIBLE);
            isErrorFound_locality = true;
        }

        if (country.isEmpty()) {
            error_country.setVisibility(View.VISIBLE);
            error_country.setText(getResources().getString(R.string.error_generic));
            isErrorFound_country = false;
        } else {
            error_country.setVisibility(View.INVISIBLE);
            isErrorFound_country = true;
        }

        if(isErrorFound_street_name == true && isErrorFound_locality == true && isErrorFound_country == true)
            return true;

        return false;
    }
}