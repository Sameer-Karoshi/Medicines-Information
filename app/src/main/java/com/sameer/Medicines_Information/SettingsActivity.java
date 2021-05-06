package com.sameer.Medicines_Information;

import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {


    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    private Button mStatusBtn;
    private Button mImageBtn;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private static final int GALLERY_PICK = 1;

    //Storage Firebase

     private StorageReference mImageStorage;

     private ProgressDialog mProgressDialog;

    //private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

       // mStorageRef = FirebaseStorage.getInstance().getReference();


        mDisplayImage = (CircleImageView)findViewById(R.id.circleImageView);
        mName = (TextView)findViewById(R.id.user_settings_display_name);
        mStatus = (TextView)findViewById(R.id.settings_status);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mImageBtn = (Button)findViewById(R.id.settings_change_image);
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);

                */
               Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);


            }
        });
        mStatusBtn = (Button)findViewById(R.id.settings_change_status);

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status_value = mStatus.getText().toString();
                Intent changeStatus_Intent = new Intent(SettingsActivity.this,StatusActivity.class);
                changeStatus_Intent.putExtra("status_value",status_value);
                startActivity(changeStatus_Intent);

            }
        });

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String name = dataSnapshot.child("name").getValue().toString();
               final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                if(!image.equals("ic_action_its_me")){
                    //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.ic_action_its_me).into(mDisplayImage);
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_action_its_me).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.ic_action_its_me).into(mDisplayImage);
                        }
                    });

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri =  data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
            //Toast.makeText(SettingsActivity.this,imageUri,Toast.LENGTH_LONG).show();
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading Image......");
                mProgressDialog.setMessage("Please wait while we upload and process the image.");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                final Uri resultUri = result.getUri();

                File thumb_Filepath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();

                Bitmap thumb_Bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_Filepath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_Bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] thumb_byte = baos.toByteArray();
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");

                StorageReference Filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");

                Filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){


                            final String download_url  = resultUri.toString();
                           // final String download_url = task.getResult().getUploadSessionUri().toString();
                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                   String thumb_download_Url = resultUri.toString();
                                    if(thumb_task.isSuccessful()){


                                        Map update_hashmap = new HashMap<>();
                                        update_hashmap.put("image",download_url);
                                        update_hashmap.put("thumb_image",thumb_download_Url);

                                        mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this,"Success Uploading",Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });

                                    }
                                    else {

                                        mProgressDialog.dismiss();

                                        Toast.makeText(SettingsActivity.this,"Error in uploading thumbnail",Toast.LENGTH_LONG).show();

                                    }

                                }
                            });
                            //String download_url = task.getResult().getDownloadUrl();


                        }
                        else{

                            mProgressDialog.dismiss();

                            Toast.makeText(SettingsActivity.this,"Error in uploading",Toast.LENGTH_LONG).show();

                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
