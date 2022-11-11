package com.example.blocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class create_account_part2 extends AppCompatActivity {
    TextView errorDOB;
    TextInputEditText date;
    Button continueBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_part2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        continueBTN = (Button) findViewById(R.id.continueBTN);
        errorDOB = (TextView) findViewById(R.id.errorDOBTXT);
        date = (TextInputEditText) findViewById(R.id.DOBINP);


        continueBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checkInput = isInputValid(date.getText().toString());
                if (checkInput){
                    Intent get_intent_data = getIntent();
                    Intent part_2 = new Intent(create_account_part2.this,create_account_part3.class);
                    part_2.putExtra("first_name",get_intent_data.getStringExtra("first_name"));
                    part_2.putExtra("last_name",get_intent_data.getStringExtra("last_name"));
                    part_2.putExtra("role",get_intent_data.getStringExtra("role"));
                    part_2.putExtra("date",date.getText().toString());
                    startActivity(part_2);
                }
            }
        });
    }

    // validation on null values and date format
    private boolean isInputValid(String user_dob) {
        boolean isErrorFound = false;
        String date_regex = "^(1[0-9]|0[1-9]|3[0-1]|2[1-9])/(0[1-9]|1[0-2])/[0-9]{4}$";

        if (user_dob.isEmpty()) {
            errorDOB.setVisibility(View.VISIBLE);
            errorDOB.setText(getResources().getString(R.string.error_generic));
            isErrorFound = false;
        } else {
            errorDOB.setVisibility(View.INVISIBLE);
            isErrorFound = true;
        }
        try {
            if(!Pattern.matches(date_regex,user_dob)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.parse(user_dob);

                errorDOB.setVisibility(View.VISIBLE);
                errorDOB.setText(getResources().getString(R.string.error_dob));
                isErrorFound = false;

            }else{
                errorDOB.setVisibility(View.INVISIBLE);
                isErrorFound = true;
            }

        }catch (ParseException e){
            e.printStackTrace();
        }
        return isErrorFound;
    }
}