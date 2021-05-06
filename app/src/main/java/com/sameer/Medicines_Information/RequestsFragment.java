package com.sameer.Medicines_Information;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static com.sameer.Medicines_Information.R.layout.users_single_layout;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    public int pos;
    private View mMainView;
    //For displaying request list
    private RecyclerView mRequestsList;

    private DatabaseReference mUsersDatabase;
   // private DatabaseReference mFriend_req;
    private DatabaseReference mFriend_requests_database;
    private DatabaseReference mFriend_requests_received;
    private DatabaseReference name;
    private FirebaseAuth mAuth;
    private String mCurrent_User_Id;

    public RequestsFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);
        mRequestsList = (RecyclerView) mMainView.findViewById(R.id.request_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_User_Id = mAuth.getCurrentUser().getUid();
        mFriend_requests_database = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_User_Id);
        mFriend_requests_database.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersDatabase.keepSynced(true);





        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());


        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRequestsList.setHasFixedSize(true);
        mRequestsList.setLayoutManager(linearLayoutManager);




        return mMainView;

    }
    @Override
    public void onStart() {
        super.onStart();



        FirebaseRecyclerAdapter<Users, RequestsViewHolder> requestsViewHolderFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, RequestsViewHolder>(
                Users.class, users_single_layout, RequestsViewHolder.class,mFriend_requests_database) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder requestsViewHolder, final Users requests,  int position) {


                pos = position;

                final  String list_user_id = getRef(position).getKey();
                //Log.d(list_user_id,"THIS IS UID");


                //mFriend_requests_database.child(user_id).child("request_type").child("received");











                   mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           final String username = dataSnapshot.child("name").getValue().toString();
                           final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                           final String status = dataSnapshot.child("status").getValue().toString();


                           mFriend_requests_database.addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                   String req_type = dataSnapshot.child(list_user_id).child("request_type").getValue().toString();
                                   if(req_type.equals("received")){

                                       requestsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {

                                               Intent profile_Intent = new Intent(getActivity().getApplication(), ProfileActivity.class);
                                               profile_Intent.putExtra("user_id", list_user_id);
                                               //Toast.makeText(getActivity(),list_user_id,Toast.LENGTH_SHORT).show();
                                               startActivity(profile_Intent);

                                           }
                                       });


                                       requestsViewHolder.setName(username);
                                       requestsViewHolder.setUserStatus(status);
                                       requestsViewHolder.setUserImage(userThumb, getContext());
                                   }
                                   else {





                                      /* requestsViewHolder.setName(null);
                                      requestsViewHolder.setUserStatus(null);
                                      requestsViewHolder.SetImgNull(userThumb,getContext());

                                       */



                                      requestsViewHolder.remove();









                                      /* CircleImageView img  = (CircleImageView)mMainView.findViewById(R.id.user_single_image);
                                       img.setVisibility(View.GONE);
                                       TextView name = (TextView)mMainView.findViewById(R.id.user_single_name);
                                       name.setVisibility(View.GONE);
                                       TextView status = (TextView)mMainView.findViewById(R.id.user_single_status);
                                       status.setVisibility(View.GONE);

                                       */




                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });



















                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
               }





        };
        mRequestsList.setAdapter(requestsViewHolderFirebaseRecyclerAdapter);

    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public RequestsViewHolder(View itemView) {
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
        public void SetImgNull(String thumb_image,Context ctx){


            CircleImageView userImageView = (CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.message_text_backround2).into(userImageView);
        }

        public void remove(){

            View image = mView.findViewById(R.id.user_single_image);
            ((ViewGroup) image.getParent()).removeView(image);
            View status = mView.findViewById(R.id.user_single_status);
            ((ViewGroup) status.getParent()).removeView(status);
            View name = mView.findViewById(R.id.user_single_name);
            ((ViewGroup)name.getParent()).removeView(name);

        }

    }
}
