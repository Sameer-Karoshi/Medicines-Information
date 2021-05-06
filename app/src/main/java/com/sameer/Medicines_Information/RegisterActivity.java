package com.sameer.Medicines_Information;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private TextView mDisplayName;
    private TextView mEmail;
    private TextView mPassword;
    private Button mCreateBtn;

    private FirebaseAuth mAuth;

    private Toolbar mToolBar;
    private DatabaseReference mDatabase;
    private DatabaseReference mUSERDATABASE;
    private DatabaseReference mDATABASE;

    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mToolBar = (Toolbar) findViewById(R.id.chat_app_bar);
        //getSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUSERDATABASE = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mDisplayName = (TextView) findViewById(R.id.login_your_email);
        mEmail = (TextView) findViewById(R.id.login_email);
        mPassword = (TextView) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.login_btn);


        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name = mDisplayName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();




                //Intent startMainActivity = new Intent(RegisterActivity.this,MainActivity.class);
                //startActivity(startMainActivity);

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(display_name)){
                    register_user(display_name,email,password);
                }





            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }



    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){



                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String UID = current_user.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
                    HashMap<String,String> userMap = new HashMap<>();
                    userMap.put("name",display_name);
                    userMap.put("status","Hi there I'm using pocket pharmacist app");
                    userMap.put("image","default");
                    userMap.put("thumb_image","default");

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){




                                String current_user_id = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                mUSERDATABASE.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mProgressDialog = new ProgressDialog(RegisterActivity.this);
                                        mProgressDialog.setTitle("Sign in......");
                                        mProgressDialog.setMessage("Please wait,Sign in is in progress");
                                        //mProgressDialog.setCanceledOnTouchOutside(false);
                                        mProgressDialog.show();

                                        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();



                                    }
                                });

                            }




                        }
                    });









                }
                else {
                    mProgressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,"Cannot Sign in.Please check the form and try again",Toast.LENGTH_LONG).show();
                }

            }
        });

    }


}
