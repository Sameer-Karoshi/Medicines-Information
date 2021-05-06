package com.sameer.Medicines_Information;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class UsersActivity extends AppCompatActivity {

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    private DatabaseReference userimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersList = (RecyclerView)findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(Users.class,R.layout.users_single_layout,UsersViewHolder.class,mUsersDatabase) {
            @Override
            protected void populateViewHolder(final UsersViewHolder usersViewHolder, final Users users, int position) {

                final  String user_id = getRef(position).getKey();
                usersViewHolder.setName(users.getName());
                usersViewHolder.setUserStatus(users.getStatus());
                /*mUsersDatabase.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        usersViewHolder.setUserImage(userThumb,getApplicationContext());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                 */


               usersViewHolder.setUserImage(users.getThumb_image(),getApplicationContext());



                usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profile_Intent = new Intent(UsersActivity.this,ProfileActivity.class);
                        profile_Intent.putExtra("user_id",user_id);
                        startActivity(profile_Intent);

                    }
                });

            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setName(String name) {

            TextView userNameView =(TextView) mView.findViewById(R.id.user_single_name);

            userNameView.setText(name);



        }

        public void setUserStatus(String status) {

            TextView userStatusView = (TextView)mView.findViewById(R.id.user_single_status);
            userStatusView.setText(status);
        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.ic_action_its_me).into(userImageView);

        }
    }
}

