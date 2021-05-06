package com.sameer.Medicines_Information;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {


    //private Toolbar mToolbar;

    private TextView mStatus;
    private Button mSavebtn;

    private FirebaseAuth mAuth;

    private DatabaseReference mStatusDatabase;
    private FirebaseUser mcurrentuser;


    //Progress
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();


        mcurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mcurrentuser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        String status_value = getIntent().getStringExtra("status_value");

        //mStatus.setText(status_value);



        mStatus = (TextView)findViewById(R.id.status_change_status);
        mSavebtn = (Button)findViewById(R.id.status_save_btn);
        mStatus.setText(status_value);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //progress
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we saving the changes");
                mProgress.show();


                String status = mStatus.getText().toString();
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        }else {
                            Toast.makeText(getApplicationContext(),"There was some error in saving changes",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
