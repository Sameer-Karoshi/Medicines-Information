package com.sameer.Medicines_Information;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private RecyclerView mFriendsList;
    private DatabaseReference mFriendsDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_Id;
    private  View mMainView;

    private DatabaseReference mUsersDatabase;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mMainView = inflater.inflate(R.layout.fragment_friends,container,false);
        mFriendsList = (RecyclerView)mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_Id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_Id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder>friendsViewHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends,FriendsViewHolder>(
                Friends.class,
                R.layout.users_single_layout,
                FriendsViewHolder.class,
                mFriendsDatabase
        ){
            @Override
            protected void populateViewHolder(final FriendsViewHolder friendsViewHolder, Friends friends, int i) {
                friendsViewHolder.setDate(friends.getDate());

                final String list_user_id = getRef(i).getKey();
                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        if (dataSnapshot.hasChild("online")) {
                            String userOnline = (String) dataSnapshot.child("online").getValue().toString();
                            friendsViewHolder.setUseronline(userOnline);
                        }


                        friendsViewHolder.setName(userName);
                        friendsViewHolder.setUserImage(userThumb,getContext());
                        friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{"Open Profile","Send Message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {

                                        //Click Event For Each Item
                                        if(i == 0){
                                            Intent profile_Intent = new Intent(getContext(),ProfileActivity.class);
                                            profile_Intent.putExtra("user_id",list_user_id);
                                            startActivity(profile_Intent);
                                        }
                                        if(i == 1){
                                            Intent ChatMain_Intent = new Intent(getContext(),ChatMainActivity.class);
                                            ChatMain_Intent.putExtra("user_id",list_user_id);
                                            ChatMain_Intent.putExtra("user_name",userName);
                                            startActivity(ChatMain_Intent);

                                        }

                                    }
                                });
                                builder.show();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        mFriendsList.setAdapter(friendsViewHolderFirebaseRecyclerAdapter);
    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FriendsViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }


        public void setDate(String date) {
            TextView userNameView = (TextView)mView.findViewById(R.id.user_single_status);
            userNameView.setText(date);
        }

        public void setName(String name){
            TextView userNameView = (TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.ic_action_its_me).into(userImageView);

        }
        public void setUseronline(String online_status){
            ImageView userOnlineView = (ImageView)mView.findViewById(R.id.user_single_online_icon);
            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            }
            else{
                userOnlineView.setVisibility(View.INVISIBLE);

            }


        }
    }
}
