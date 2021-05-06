package com.sameer.Medicines_Information;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private RecyclerView mConvList;

    private DatabaseReference mConvDatabse;
    private DatabaseReference mMessageDatabase;
    private DatabaseReference mUserDatabase;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private View mMainView;



    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_chats, container, false);
        mConvList = (RecyclerView)mMainView.findViewById(R.id.conversion_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mConvDatabse = FirebaseDatabase.getInstance().getReference().child("Chat").child(mCurrentUserId);
        mConvDatabse.keepSynced(true);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mMessageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUserId);
        mUserDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mConvList.setHasFixedSize(true);
        mConvList.setLayoutManager(linearLayoutManager);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query conversionQuery = mConvDatabse.orderByChild("timestamp");

        FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(
                Conv.class,
                R.layout.users_single_layout,
                ConvViewHolder.class,
                conversionQuery
        ) {
            @Override
            protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {

                final String list_user_id = getRef(i).getKey();
                Query lastmessageQuery = mMessageDatabase.child(list_user_id).limitToLast(1);
                lastmessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        //String data = dataSnapshot.child("message").getValue().toString();
                       // convViewHolder.setMessage(data, conv.isSeen());
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

                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     final String username = dataSnapshot.child("name").getValue().toString();
                     String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                     String userstatus = dataSnapshot.child("status").getValue().toString();

                     if (dataSnapshot.hasChild("online")){
                         String userOnline = dataSnapshot.child("online").getValue().toString();
                         convViewHolder.setUserOnline(userOnline);

                     }

                     convViewHolder.setUserStatus(userstatus);
                     convViewHolder.setName(username);
                     convViewHolder.setUserImage(userThumb,getContext());

                     convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Intent chatIntent = new Intent(getContext(),ChatMainActivity.class);
                             chatIntent.putExtra("user_id",list_user_id);
                             chatIntent.putExtra("user_name",username);
                             startActivity(chatIntent);
                         }
                     });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
        mConvList.setAdapter(firebaseConvAdapter);
    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ConvViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setMessage(String message,boolean isSeen){
            TextView userStatusView = (TextView)mView.findViewById(R.id.user_single_status);
            userStatusView.setText(message);
            if(!isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            }
            else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }

        }

        public void setUserStatus(String status) {

            TextView userStatusView = (TextView)mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }
        public void setName(String name){
            TextView userNameView = (TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public void setUserImage(String thumb_image, Context ctx){
            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.ic_action_its_me).into(userImageView);
        }

        public void setUserOnline(String online_status){
            ImageView userOnlineView = (ImageView)mView.findViewById(R.id.user_single_online_icon);
            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            }
            else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
