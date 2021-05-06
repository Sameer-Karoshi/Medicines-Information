package com.sameer.Medicines_Information;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMainActivity extends AppCompatActivity {

    private String mChatUser;
    private DatabaseReference mRootRef;
    //private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;
    private String mcurrentUserId;
    private ImageButton mChataddBtn;
    private ImageButton mChatSendBtn;
    private TextView mChatMessageView;

    private RecyclerView mMessageslist;
    private SwipeRefreshLayout mRefreshLayout;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
   // private DatabaseReference mMessageDatabase;

    private static final  int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private static final int GALLERY_PICK = 1;

    private int Item_position = 0;

    private String mLastKey = "";
    private String mPrevKey = "";

    private StorageReference mImageStorage;

    //private Toolbar mChatToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

       // mChatToolBar =  findViewById(R.id.chat_app_bar);
       // setSupportActionBar(mChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mChataddBtn = (ImageButton)findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton)findViewById(R.id.chat_send_btn);
        mChatMessageView = (TextView)findViewById(R.id.chat_message_view);
        mAdapter = new MessageAdapter(messagesList);


        mMessageslist = (RecyclerView)findViewById(R.id.messages_list);

        mRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_message_swipe_layout);


        mLinearLayout = new LinearLayoutManager(this);

        mMessageslist.setHasFixedSize(true);
        mMessageslist.setLayoutManager(mLinearLayout);

        mMessageslist.setAdapter(mAdapter);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        //mMessageDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mcurrentUserId = mAuth.getCurrentUser().getUid();
        mChatUser = getIntent().getStringExtra("user_id");

        mImageStorage = FirebaseStorage.getInstance().getReference();
        loadMessages();
        final String userName = getIntent().getStringExtra("user_name");
       // getSupportActionBar().setTitle(userName);





       mRootRef.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               String online = dataSnapshot.child("online").getValue().toString();

               String currentimage = FirebaseDatabase.getInstance().getReference().child("Users").child(mcurrentUserId).child("thumb_image").toString();
               String userimage = FirebaseDatabase.getInstance().getReference().child("Users").child(mChatUser).child("thumb_image").toString();
               //setimageforcurrent(currentimage,getBaseContext());
               //setimageforchat(userimage,getBaseContext());

               if(online.equals("true")){
                   //Toast.makeText(ChatMainActivity.this,"Online",Toast.LENGTH_LONG).show();

                   getSupportActionBar().setTitle(userName +" " + "(" +"Online"+")");

               }
               else {
                   GetTimeAgo getTimeAgo = new GetTimeAgo();
                   long lastTime = Long.parseLong(online);
                   String LastSeenTime = getTimeAgo.getTimeAgo(lastTime,getApplicationContext());

                   getSupportActionBar().setTitle(userName + " "+ "("+ LastSeenTime +")");

                   //Toast.makeText(ChatMainActivity.this,LastSeenTime,Toast.LENGTH_LONG).show();

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });


        mRootRef.child("Chat").child(mcurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(mChatUser)){
                    Map ChatAddMap = new HashMap();
                    ChatAddMap.put("seen",false);
                    ChatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map ChatUserMap = new HashMap();
                    ChatUserMap.put("Chat/" + mcurrentUserId + "/" + mChatUser,ChatAddMap);
                    ChatUserMap.put("Chat/" + mChatUser + "/" + mcurrentUserId,ChatAddMap);


                    mRootRef.updateChildren(ChatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                          if(databaseError != null){
                              Log.d("CHAT_LOG",databaseError.getMessage().toString());
                          }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        mChataddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"), GALLERY_PICK);
            }
        });
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                //messagesList.clear();
                Item_position = 0;
                loadMoreMessages();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            final String current_user_ref = "messages/" + mcurrentUserId + "/" + mChatUser;
            final String chat_user_ref = "messages/" + mChatUser + "/" + mcurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages").child(mcurrentUserId).child(mChatUser).push();
            final String push_id = user_message_push.getKey();


            StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful()){


                        //final Uri resultUri = task.getUri();
                        //final String download_url  = resultUri.toString();
                        Map MessageMap = new HashMap();
                       // MessageMap.put("message",downlo);
                        MessageMap.put( "seen",false);
                        MessageMap.put("type","image");
                        MessageMap.put("time",ServerValue.TIMESTAMP);
                        MessageMap.put("from",mcurrentUserId);

                        Map MessageUserMap = new HashMap();
                        MessageUserMap.put(current_user_ref + "/" + push_id,MessageMap);
                        MessageUserMap.put(chat_user_ref + "/" + push_id,MessageMap);

                        mChatMessageView.setText("");

                        mRootRef.updateChildren(MessageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if(databaseError != null){
                                    Log.d("CHAT_LOG",databaseError.getMessage().toString());
                                }

                            }
                        });

                    }
                }
            });
        }
    }

    private void loadMoreMessages() {
        DatabaseReference messageRef = mRootRef.child("messages").child(mcurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Messages message = dataSnapshot.getValue(Messages.class);
                String messagekey = dataSnapshot.getKey();
                //String messagekey = dataSnapshot.getKey();
               // messagesList.add(Item_position++,message);
               // messagesList.add(Item_position++,message);
                if(!mPrevKey.equals(messagekey)){
                    messagesList.add(Item_position++,message);

                }
                else {
                    mPrevKey = mLastKey;
                }
                if(Item_position == 1){


                    mLastKey = messagekey;

                }
                /*else {
                    mPrevKey = mLastKey;

                }
                if(Item_position == 1){

                    mLastKey = messagekey;
                }

                 */



                Log.d("TOTALKEYS","Last Key : " + mLastKey + "| Prev Key : " + mPrevKey + " | message key : " + messagekey);

                mAdapter.notifyDataSetChanged();

                mRefreshLayout.setRefreshing(false);
                mLinearLayout.scrollToPositionWithOffset(10,0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void setimageforchat(String thumb_image,Context ctx){
        CircleImageView userImageView = (CircleImageView)findViewById(R.id.message_profile_layout);
        Picasso.get().load(thumb_image).placeholder(R.drawable.ic_action_its_me).into(userImageView);
    }
    public void setimageforcurrent(String thumb_image,Context ctx){
        CircleImageView userImageView = (CircleImageView)findViewById(R.id.message_profile_layout);
        Picasso.get().load(thumb_image).placeholder(R.drawable.ic_action_its_me).into(userImageView);
    }


    private void loadMessages() {


        DatabaseReference messageRef = mRootRef.child("messages").child(mcurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage*TOTAL_ITEMS_TO_LOAD);

       messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                Item_position++;
                if(Item_position == 1){
                    String messagekey = dataSnapshot.getKey();
                    mLastKey = messagekey;
                    mPrevKey = messagekey;
                }
                messagesList.add(message);
                mAdapter.notifyDataSetChanged();


                mMessageslist.scrollToPosition(messagesList.size()-1);
                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {

        String message = mChatMessageView.getText().toString();
        if(!TextUtils.isEmpty(message)){

            String current_user_ref = "messages/" + mcurrentUserId + "/" + mChatUser;
            String Chat_user_ref = "messages/" + mChatUser + "/" + mcurrentUserId;

            DatabaseReference user_message_push = mRootRef.child("messages").child(mcurrentUserId).child(mChatUser).push();
            String push_id = user_message_push.getKey();

            Map MessageMap = new HashMap();
            MessageMap.put("message",message);
            MessageMap.put( "seen",false);
            MessageMap.put("type","text");
            MessageMap.put("time",ServerValue.TIMESTAMP);
            MessageMap.put("from",mcurrentUserId);

            Map MessageUserMap = new HashMap();
            MessageUserMap.put(current_user_ref + "/" + push_id,MessageMap);
            MessageUserMap.put(Chat_user_ref + "/" + push_id,MessageMap);
            mChatMessageView.setText("");

            mRootRef.updateChildren(MessageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if(databaseError != null){
                        Log.d("CHAT_LOG",databaseError.getMessage().toString());
                    }

                }
            });


        }
    }
}
