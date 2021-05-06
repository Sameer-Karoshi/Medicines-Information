package com.sameer.Medicines_Information;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private DatabaseReference mUserDataBase;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Messages> mMessageList){
        this.mMessageList = mMessageList;
    }


    @Override
    public MessageViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout,parent,false);
        return new MessageViewHolder(view);
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public CircleImageView profileImage;
        public ImageView messageImage;

        public MessageViewHolder(View view){
            super(view);
            messageText = (TextView)view.findViewById(R.id.message_text_layout);
            //timeText = (TextView)view.findViewById(R.id.message_item_time);
            profileImage = (CircleImageView)view.findViewById(R.id.message_profile_layout);
            //messageImage = (ImageView)view.findViewById(R.id.message_image_layout);


        }
    }



    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i){
        mAuth = FirebaseAuth.getInstance();
        String CurrentUserId = mAuth.getCurrentUser().getUid();

        Messages c = mMessageList.get(i);
        String from_user = c.getFrom();

        String message_type = c.getType();
        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                Picasso.get().load(image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        if(from_user.equals(CurrentUserId)){
         viewHolder.messageText.setBackgroundResource(R.drawable.message_text_backround2);
         viewHolder.messageText.setTextColor(Color.BLACK);

          //viewHolder.messageText.setGravity(Gravity.RIGHT);



        }
        else{
             viewHolder.messageText.setBackgroundResource(R.drawable.message_text_backround);
             viewHolder.messageText.setTextColor(Color.WHITE);
             //viewHolder.messageText.setGravity(0);

        }


        viewHolder.messageText.setText(c.getMessage());
        //viewHolder.timeText.setText(c.getTime());

        /*if(message_type.equals("text")){
            viewHolder.messageText.setText(c.getMessage());
            viewHolder.messageImage.setVisibility(View.INVISIBLE);

        }
        else {
            viewHolder.messageText.setVisibility(View.INVISIBLE);
            //Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage()).placeholder(R.drawable.ic_action_its_me).into(viewHolder.messageImage);
        }

         */
    }
    @Override
    public int getItemCount(){
        return  mMessageList.size();

    }
}
