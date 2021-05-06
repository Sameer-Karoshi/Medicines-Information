package com.sameer.Medicines_Information;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class ChatActivity extends AppCompatActivity {




    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        //setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Pocket Pharmacist Chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Tabs
        mViewPager = (ViewPager) findViewById(R.id.chat_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.chat_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mAuth = FirebaseAuth.getInstance();
        
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_chat, menu);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.chat_account_settings && item.getItemId() == R.id.chat_account_settings)
        {
            Intent accsettingsIntent = new Intent(ChatActivity.this,SettingsActivity.class);
            startActivity(accsettingsIntent);

        }

        if(item.getItemId() == R.id.chat_all_users){
            Intent all_users_Intent = new Intent(ChatActivity.this,UsersActivity.class);
            startActivity(all_users_Intent);
        }


        //if(item.getItemId() == R.id.log_out){

        //  FirebaseAuth.getInstance().signOut();

//
        //      }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser != null){

            mUserRef.child("online").setValue("true");
            //mUserRef.child("LastSeen").setValue(ServerValue.TIMESTAMP);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser CurrentUser = mAuth.getCurrentUser();
        if(CurrentUser != null){
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }
}
