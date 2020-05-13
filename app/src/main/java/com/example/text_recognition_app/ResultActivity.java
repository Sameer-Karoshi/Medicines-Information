package com.example.text_recognition_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Session2Command;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

public class ResultActivity extends AppCompatActivity {

    private TextView mName, mGenName, mPrice, mUse, mSideEff,mHow_to_use_this_medicine,mHow_medicine_works,mQuick_tips,mMissed_dosage,mAlternate_medicines;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        String Result = intent.getStringExtra("Result");
        Toast.makeText(this,Result, Toast.LENGTH_SHORT).show();
        mName = findViewById(R.id.result_activity_text_view_name);
        mGenName = findViewById(R.id.result_activity_text_view_gen_name);
        mPrice = findViewById(R.id.result_activity_text_view_price);
        mUse = findViewById(R.id.result_activity_text_view_use);
        mSideEff = findViewById(R.id.result_activity_text_view_side_effects);
        mHow_to_use_this_medicine = findViewById(R.id.result_activity_text_view_how_to_use);
        mHow_medicine_works = findViewById(R.id.result_activity_text_view_how_it_works);
        mQuick_tips = findViewById(R.id.result_activity_text_view_quick_tips);
        mMissed_dosage = findViewById(R.id.result_activity_text_view_missed_dosage);
        mAlternate_medicines = findViewById(R.id.result_activity_text_view_alternate_medicines);

        result(Result.trim());
    }
    private void result(String result){
        String text = "";
        mName.setText(result);
        try {
            InputStream is = getAssets().open(result+".txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            text = new String(buffer);
        }catch (Exception e){
            e.printStackTrace();
        }
        String[] medicine = text.split("@");
        if (medicine.length == 10){
            mGenName.setText(medicine[1]);
            mUse.setText(medicine[2]);
            mSideEff.setText(medicine[3]);
            mPrice.setText(medicine[4]);
            mHow_to_use_this_medicine.setText(medicine[5]);
            mHow_medicine_works.setText(medicine[6]);
            mQuick_tips.setText(medicine[7]);
            mMissed_dosage.setText(medicine[8]);
            mAlternate_medicines.setText(medicine[9]);
        }else {
            Toast.makeText(this, "Insufficient data", Toast.LENGTH_SHORT).show();
        }
        //mUse.setText(med[0]);
        //mGenName.setText(med[1]);
        //mPrice.setText(med[2]);
    }
}
