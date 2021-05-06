package com.sameer.Medicines_Information;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;



public class MainActivity extends AppCompatActivity {

    //private Button button;




    //private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private Button button;
    EditText mResultEt;
    ImageView mPreviewIv;


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    private String[] Result;
    private String[] Medicines = {"Amoxil","Avomine","Azipro250","Biaxin","Bismatrol","Bumex","Ciproxin","Cliford","Crocin650","Ecpirin","Floraster","Gutron","Imodium","Lozol","Maxalt","Omzid","Rantac","Restoril","Sectral","Sinarest","Zaroxolyn","Zestril"};

    String cameraPermission[];
    String storagePermission[];


    Uri image_uri;


    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       // Toast.makeText(this, "initialise", Toast.LENGTH_SHORT).show();


        button = (Button)findViewById(R.id.button);

        button.setVisibility(View.INVISIBLE);
        button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResult();
               // openResultActivity();

            }
        });



        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("Click on Image Icon to insert image");

        mResultEt = findViewById(R.id.resultEt);
        mPreviewIv = findViewById(R.id.imageIv);

        //Camera Permission
        cameraPermission = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //Storage Permission
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null)
        {
            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }


    }


    public void openResultActivity(){
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //handle actionbar items

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.addImage)
        {
            showImageImportDialog();

        }
        if(id == R.id.log_out && item.getItemId() == R.id.log_out)
        {
            Toast.makeText(this,"Logged Out",Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        if (item.getItemId() == R.id.menu_chat){
            Intent chatIntent = new Intent(MainActivity.this,ChatActivity.class);
            startActivity(chatIntent);

        }

        //if(item.getItemId() == R.id.log_out){

          //  FirebaseAuth.getInstance().signOut();

//
  //      }

        return super.onOptionsItemSelected(item);
    }

    private void showImageImportDialog() {
        //items to display in dialog
        String[] items = {" Camera"," Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        //set title
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0)
                {
                    //camera option clicked
                    if(!checkCameraPermission()){
                        //camera permission not allowed, request it
                        requestCameraPermission();
                    }
                    else{
                        //permission allowed,take picture
                        pickCamera();
                    }


                }
                if(which == 1)
                {
                    //gallery option clicked
                    if(!checkStoragePermission()){
                        //Storage permission not allowed, request it
                        requestStoragePermission();
                    }
                    else{
                        //permission allowed,take picture
                        pickGallery();
                    }
                }
            }

        });
        dialog.create().show(); //show dialog
    }

    private void pickGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //Set Intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
        button.setVisibility(View.VISIBLE);
        button.setEnabled(true);
    }

    private void pickCamera() {
        //intent to take image from camera,it will be also be saved to storage to get high quality image
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");  //Title of the picture
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image To Text");//Description
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI , values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
        button.setVisibility(View.VISIBLE);
        button.setEnabled(true);



    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;


    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
         return result && result1;
    }

    //handle permission result


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length >0){
                    boolean cameraAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickCamera();
                    }
                    else{
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length >0){
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickGallery();
                    }
                    else{
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                //got image from gallery now crop it
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);

            }
            if(requestCode == IMAGE_PICK_CAMERA_CODE)
            {

                CropImage.activity(image_uri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);



            }
        }
        //get cropped image
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();//get image Uri
                //set image to image view
                mPreviewIv.setImageURI(resultUri);

                //get drawable bitmap for text recognition
                BitmapDrawable bitmapDrawable = (BitmapDrawable)mPreviewIv.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();


                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();


                if(!recognizer.isOperational()){
                    Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();

                }
                else{
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();

                    for(int i = 0;i<items.size();i++){
                        TextBlock myItems = items.valueAt(i);
                        sb.append(myItems.getValue());

                    }

                    mResultEt.setText(sb.toString());
                    Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
                    Result = sb.toString().trim().toLowerCase().split("");

                }
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Toast.makeText(this,""+error,Toast.LENGTH_SHORT).show();



            }
        }
    }
    private void getResult(){
        if (Result.length > 0){
            for (int i=0; i<Result.length; i++){
                for (int j=0; j<Medicines.length; j++){
                    //button.setVisibility(View.VISIBLE);
                   // button.setEnabled(true);
                    String[] med = Medicines[j].toLowerCase().split("");
                    boolean matched = true;
                    for (int k=0; k<med.length; k++){
                        System.out.println(i+"\t"+j+"\t"+k);
                        if (i+k < Result.length && Result[i+k].equals(med[k])){
                            matched = matched && true;
                        }else {
                            matched = false;
                            break;
                        }
                    }
                    if (matched){
                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                        intent.putExtra("Result", Medicines[j]);
                        startActivity(intent);
                        return;
                    }
                }
            }
            Toast.makeText(this, "Result not found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            sendToStart();
        }
        else{
           // mUserRef.child("online").setValue(true);
        }
        //updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mUserRef.child("online").setValue(false);

    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


}


/*
private void getResult(){
        if (Result.length != 0){
            boolean Found = false;
            for (int i=0; i<Result.length; i++){
                for (int j=0; j<Medicines.length; j++){
                    if (Result[i].trim().toLowerCase().equals(Medicines[j].trim().toLowerCase())){
                       Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                        intent.putExtra("Result",Medicines[j]);
                        startActivity(intent);

                        Found = true;
                        break;
                    }
                }
            }
            if (!Found){
                Toast.makeText(this, "Result not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

 */
