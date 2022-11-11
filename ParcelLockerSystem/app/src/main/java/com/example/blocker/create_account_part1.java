package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class create_account_part1 extends AppCompatActivity {
    TextView errorFirstName,errorLastName;
    TextInputEditText first_name,last_name;
    Button continueBTN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_part1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        continueBTN = (Button) findViewById(R.id.continueBTN);
        errorFirstName = (TextView) findViewById(R.id.errorFirstNameTXT);
        errorLastName = (TextView) findViewById(R.id.errorLastNameTXT);
        first_name = (TextInputEditText) findViewById(R.id.firstNameINP);
        last_name = (TextInputEditText) findViewById(R.id.lastNameINP);

        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checkInput = isInputValid(first_name.getText().toString(),last_name.getText().toString());
                if(checkInput) {
                    Intent get_intent_data = getIntent(); // retrieving data from the previous activity
                    Intent part_1 = new Intent(create_account_part1.this,create_account_part2.class);
                    part_1.putExtra("role",get_intent_data.getStringExtra("role"));
                    part_1.putExtra("first_name",first_name.getText().toString());
                    part_1.putExtra("last_name",last_name.getText().toString());
                    startActivity(part_1);
                }
            }
        });

    }

    // validation for null values
    private boolean isInputValid(String name, String surname){

        boolean isErrorFound_name = false;
        boolean isErrorFound_surname = false;

        if(name.isEmpty()) {
            errorFirstName.setVisibility(View.VISIBLE);
            errorFirstName.setText(getResources().getString(R.string.error_generic));
            isErrorFound_name = false;
        }else {
            errorFirstName.setVisibility(View.INVISIBLE);
            isErrorFound_name = true;
        }
        if(surname.isEmpty()){
            errorLastName.setVisibility(View.VISIBLE);
            errorLastName.setText(getResources().getString(R.string.error_generic));
            isErrorFound_surname = false;
        }else {
            errorLastName.setVisibility(View.GONE);
            isErrorFound_surname = true;
        }

        if (isErrorFound_name == true && isErrorFound_surname == true)
            return true;

        return false;
    }
}