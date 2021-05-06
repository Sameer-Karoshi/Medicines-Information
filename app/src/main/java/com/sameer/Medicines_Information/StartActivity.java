package com.sameer.Medicines_Information;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button mAlreadyAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        mRegBtn = (Button) findViewById(R.id.start_reg_btn);
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_Intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(reg_Intent);


            }
        });


        mAlreadyAccount = (Button) findViewById(R.id.already_account);
        mAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent alreadyaccount_Intent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(alreadyaccount_Intent);
            }
        });
    }
}
