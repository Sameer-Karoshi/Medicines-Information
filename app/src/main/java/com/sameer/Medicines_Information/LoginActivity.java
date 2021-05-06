package com.sameer.Medicines_Information;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView mLoginEmail;
    private TextView mLoginPassword;
    private Button mLogin_btn;


    private DatabaseReference mUSERDATABASE;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUSERDATABASE = FirebaseDatabase.getInstance().getReference().child("Users");

        mLoginEmail = (TextView)findViewById(R.id.login_email);
        mLoginPassword = (TextView) findViewById(R.id.reg_password);



        mLogin_btn = (Button) findViewById(R.id.login_btn);
        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getText().toString();
                String password = mLoginPassword.getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){
                    loginUser(email,password);
                }
            }

            private void loginUser(String email, String password) {

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {



                        if (task.isSuccessful()){

                            String current_user_id = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            mProgressDialog = new ProgressDialog(LoginActivity.this);
                            mProgressDialog.setTitle("Login......");
                            mProgressDialog.setMessage("Please wait,Login is in progress");
                            //mProgressDialog.setCanceledOnTouchOutside(false);
                            mProgressDialog.show();

                            mUSERDATABASE.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                }
                            });

                        }

                        else {
                            mProgressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Cannot Sign in.Please check the form and try again",Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
    }
}
