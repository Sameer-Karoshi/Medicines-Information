package com.example.text_recognition_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Session2Command;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private TextView mName, mGenName, mPrice, mUse, mSideEff, mHow_to_use_this_medicine, mHow_medicine_works, mQuick_tips, mMissed_dosage, mAlternate_medicines;
    private String med;
   public String Lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();

        setContentView(R.layout.activity_result);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle(getResources().getString(R.string.app_name));
        Intent intent = getIntent();
        String Result = intent.getStringExtra("Result");
        Toast.makeText(this, Result, Toast.LENGTH_SHORT).show();
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
        med = Result.trim();
        switch (Lang){
            case "fr":
                result(med,"French");
                break;
            case "hi":
                result(med,"Hindi");
                break;
            case "mr":
                result(med,"Marathi");
                break;
            case "en":
                result(med,"English");
                break;
            case "de":
                result(med,"German");
                break;
                default:
                    result(med,"English");
                    break;

        }


         /*   if (Lang == "mr"){
                result(med,"Marathi");

            }
            else if(Lang == "en"){
                result(med,"English");

            }
            else if(Lang == "hi"){
                result(med,"Hindi");

            }
            else if(Lang == "fr"){
                result(med,"French");

            }*/
        //result(med,"English");








        Button changeLang = findViewById(R.id.changeMyLang);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show AlterDialog to display list of languages ,one can be selected
                showChangeLanguageDialog();
            }
        });


    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"French", "हिन्दी", "मराठी", "English","Deutsche"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ResultActivity.this);
        mBuilder.setTitle("Choose Language...");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {
                    //french
                    Toast.makeText(ResultActivity.this, "French Language", Toast.LENGTH_SHORT).show();
                    //result(med,"French");
                    //answer = i;
                    setLocale("fr");
                    recreate();

                    //finish();


                } else if (i == 1) {
                    //Hindi
                    Toast.makeText(ResultActivity.this,"Hindi Language", Toast.LENGTH_SHORT).show();
                    //result(med,"Hindi");
                    //answer = i;
                    setLocale("hi");
                    recreate();

                    //finish();

                } else if (i == 2) {
                    //Marathi
                    Toast.makeText(ResultActivity.this, "Marathi Language", Toast.LENGTH_SHORT).show();
                    //result(med,"Marathi");
                    //answer = i;
                    setLocale("mr");
                    recreate();

                    //finish();

                } else if (i == 3) {
                    //English
                    Toast.makeText(ResultActivity.this, "English Language", Toast.LENGTH_SHORT).show();
                    //result(med,"English");
                    //answer = i;
                    setLocale("en");
                    recreate();

                    //finish();

                }
                else if (i == 4) {
                    //English
                    Toast.makeText(ResultActivity.this, "German Language", Toast.LENGTH_SHORT).show();
                    //result(med,"English");
                    //answer = i;
                    setLocale("de");
                    recreate();

                    //finish();

                }

                dialogInterface.dismiss();

            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();


    }


    private void setLocale(String lang) {




        Lang = lang;


        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();


    }

    public void loadLocale() {

        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String Language = prefs.getString("My_Lang", "");
        setLocale(Language);


    }


    private void result(String med, String language) {
        String text = "";
        mName.setText(med);
        try {
            InputStream is = getAssets().open(med + "(" + language + ")" + ".txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            text = new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] medicine = text.split("@");
        if (medicine.length == 10) {
            mName.setText(medicine[0]);
            mGenName.setText(medicine[1]);
            mUse.setText(medicine[2]);
            mSideEff.setText(medicine[3]);
            mPrice.setText(medicine[4]);
            mHow_to_use_this_medicine.setText(medicine[5]);
            mHow_medicine_works.setText(medicine[6]);
            mQuick_tips.setText(medicine[7]);
            mMissed_dosage.setText(medicine[8]);
            mAlternate_medicines.setText(medicine[9]);
        } else {
            Toast.makeText(this, "Insufficient data", Toast.LENGTH_SHORT).show();
        }


    }
}