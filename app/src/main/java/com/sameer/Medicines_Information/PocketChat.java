package com.sameer.Medicines_Information;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class PocketChat extends MultiDexApplication {


    private DatabaseReference mUserDatabase;
    private FirebaseAuth mauth;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        mauth = FirebaseAuth.getInstance();

        if(mauth.getCurrentUser() != null){

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());
            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot != null){
                        mUserDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);
                        //mUserDatabase.child("LastSeen").setValue(ServerValue.TIMESTAMP);
                        // mUserDatabase.child("online").setValue(true);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }
}
