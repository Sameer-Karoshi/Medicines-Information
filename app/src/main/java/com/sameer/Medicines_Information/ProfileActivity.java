package com.sameer.Medicines_Information;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

   // private TextView mDisplayId;

    private ImageView mProfileImage;
    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private Button mProfileSendRequestsButton;
    private Button mDeclinebtn;


    private DatabaseReference mUsersDataBase;
    private ProgressDialog mProgressDialog;

    private FirebaseUser mCurrent_user;
    private String mCurrent_state;

    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mFriendRequestreceived;
    private DatabaseReference mFriendDataBase;
    private DatabaseReference mRootRef;

    private DatabaseReference mNotificationDatabase;

    private FirebaseAuth mAuth;
    private String current_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");
        mRootRef = FirebaseDatabase.getInstance().getReference();
       // mDisplayId = (TextView)findViewById(R.id.profile_display_name);
        //mDisplayId.setText(user_id);


        mUsersDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");

        mFriendDataBase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();



        mProfileImage = (ImageView)findViewById(R.id.profile_image);
        mProfileName = (TextView)findViewById(R.id.profile_display_name);
        mProfileStatus = (TextView)findViewById(R.id.profile_status);
        mProfileFriendsCount = (TextView)findViewById(R.id.profile_total_friends);
        mProfileSendRequestsButton = (Button)findViewById(R.id.profile_send_req_btn);
        mDeclinebtn = (Button)findViewById(R.id.profile_decline_btn);


        mCurrent_state = "not_friend";

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading Users Data..");
        mProgressDialog.setMessage("Please wait while we load the users data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();



        mUsersDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                Picasso.get().load(image).placeholder(R.drawable.ic_action_its_me).into(mProfileImage);

                //----------------FRIENDS LIST/REQUESTS FEATURE

                mFriendRequestDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("received")){
                                //mProfileSendRequestsButton.setEnabled(true);


                                mCurrent_state = "req_received";
                                mProfileSendRequestsButton.setText("Accept Friend Request");
                                mDeclinebtn.setVisibility(View.VISIBLE);
                                mDeclinebtn.setEnabled(true);
                            }
                            else if(req_type.equals("sent")) {
                                mCurrent_state = "req_sent";
                                mProfileSendRequestsButton.setText("Cancel Friend Request");

                                mDeclinebtn.setVisibility(View.INVISIBLE);
                                mDeclinebtn.setEnabled(false);


                            }

                        }
                        else {
                            mFriendDataBase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id)){
                                        mCurrent_state = "Friends";
                                        mProfileSendRequestsButton.setText("Unfriend this person");

                                        mDeclinebtn.setVisibility(View.INVISIBLE);
                                        mDeclinebtn.setEnabled(false);

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }


                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled( DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
        mProfileSendRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendRequestsButton.setEnabled(false);


                //----------------------NOT FRIENDS STATE--------------------
                if (mCurrent_state.equals("not_friend")){
                    mDeclinebtn.setVisibility(View.INVISIBLE);
                    mDeclinebtn.setEnabled(false);
                    DatabaseReference newNotificationRef = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationID = newNotificationRef.getKey();
                    HashMap<String,String> notificationsData = new HashMap<>();
                    notificationsData.put("from",mCurrent_user.getUid());
                    notificationsData.put("type","request");
                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + user_id + "/" + newNotificationID,notificationsData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                          if(databaseError != null){
                              Toast.makeText(ProfileActivity.this,"There was some error in sending request",Toast.LENGTH_SHORT).show();
                          }
                          mProfileSendRequestsButton.setEnabled(true);

                            mCurrent_state = "req_sent";
                            mProfileSendRequestsButton.setText("Cancel Friend Request");


                        }
                    });



                }

                //-------CANCEL REQUEST STATE----------------------

                if(mCurrent_state.equals("req_sent")){
                    mFriendRequestDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendRequestDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendRequestsButton.setEnabled(true);
                                    mCurrent_state = "not_friend";
                                    mProfileSendRequestsButton.setText("Send Friend Request");
                                    mDeclinebtn.setVisibility(View.INVISIBLE);
                                    mDeclinebtn.setEnabled(false);



                                }
                            });
                        }
                    });
                }

                //----------------Request Received State

                if(mCurrent_state.equals("req_received")){




                    //mFriendRequestreceived.child("heyyy");


                    final String current_date = DateFormat.getDateTimeInstance().format(new Date());
                    Map friendsMAp = new HashMap();
                    friendsMAp.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date" ,current_date);
                    friendsMAp.put("Friends/" + user_id + "/" + mCurrent_user.getUid() + "/date",current_date);


                    friendsMAp.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id  ,null);
                    friendsMAp.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() ,null);

                    mRootRef.updateChildren(friendsMAp, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null){
                                mProfileSendRequestsButton.setEnabled(true);
                                mCurrent_state = "Friends";
                                mProfileSendRequestsButton.setText("Unfriend this person");

                                mDeclinebtn.setVisibility(View.INVISIBLE);
                                mDeclinebtn.setEnabled(false);
                            }
                            else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



                }

                // ------------------UNFRIEND STATE ----------------------

                if(mCurrent_state.equals("Friends")){
               Map UnfriendMap = new HashMap();
               UnfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id ,null);
               UnfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(),null);

                    mRootRef.updateChildren(UnfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null){

                                mCurrent_state = "not_friend";
                                mProfileSendRequestsButton.setText("Send Friend request");

                                mDeclinebtn.setVisibility(View.INVISIBLE);
                                mDeclinebtn.setEnabled(false);
                            }
                            else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendRequestsButton.setEnabled(true);
                        }
                    });

                }

            }
        });

    }
}
