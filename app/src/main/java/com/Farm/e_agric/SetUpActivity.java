package com.Farm.e_agric;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
//class to set up profile with name and profile pic
public class SetUpActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private EditText mProfileName;
    private Button mSaveBtn;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private String Uid;
    private Uri mImageUri = null;
    private ProgressBar progressBar;
    private boolean isPhotoSelected = false;

    //Delete here
    private CheckBox bsc;
    private CheckBox msc;
    //Till here
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        Toolbar setUpToolbar = findViewById(R.id.setup_toolbar);
        setSupportActionBar(setUpToolbar);
        getSupportActionBar().setTitle("Profile");

        //setup firebase storage database to store profile_pic
        storageReference = FirebaseStorage.getInstance().getReference();
        //setup firestore to store image uri and name
        firestore = FirebaseFirestore.getInstance();

        //get the current user in the system
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        //collecting all the id's of the views
        circleImageView = findViewById(R.id.circleImageView);
        mProfileName = findViewById(R.id.profile_name);
        mSaveBtn = findViewById(R.id.save_btn);
        //delete here
        bsc = findViewById(R.id.Bsc);
        msc = findViewById(R.id.Msc);

        //till

        //delete here
        //till here


        auth = FirebaseAuth.getInstance();
        // storing to firebase
        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String name = task.getResult().getString("name");
                        String imageUrl = task.getResult().getString("image");

                        //setup profile name in the xml edittext
                        mProfileName.setText(name);
                        //this links firestore with cloudstorage
                        mImageUri = Uri.parse(imageUrl);
                        //loading the retrieved image into the cycle image view

                        Glide.with(SetUpActivity.this).load(imageUrl).into(circleImageView);
                    }
                }
            }
        });
        //this function stores setup information:name,profile_pic,imageuri
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String name = mProfileName.getText().toString();
                //creates a collection with document terminating with .jpg
                StorageReference imageRef = storageReference.child("Profile_pics").child(Uid + ".jpg");
                if (isPhotoSelected) {
                    if (!name.isEmpty() && mImageUri != null && (bsc.isChecked() == true || msc.isChecked() == true)) {
                        imageRef.putFile(mImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            saveToFireStore(task, name, uri,bsc,msc);
                                        }
                                    });

                                }
                                //if firestorage fails for unknown
                                else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(SetUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(SetUpActivity.this, "Please Select picture and write your name", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    saveToFireStore(null , name , mImageUri,bsc, msc);
                }
            }
        });
        //an action that happens when one sets up the profile picture
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking that the android version that we target holds
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                   //permission to allow for phone storage access
                   if (ContextCompat.checkSelfPermission(SetUpActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                       ActivityCompat.requestPermissions(SetUpActivity.this , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} , 1);
                   }else{
                       //a library to help us crop the image to fit circle image view
                       CropImage.activity()
                               .setGuidelines(CropImageView.Guidelines.ON)
                               .setAspectRatio(1,1)
                               .start(SetUpActivity.this);
                   }
               }
            }
        });
    }
    //store name and image in fire store
    private void saveToFireStore(Task<UploadTask.TaskSnapshot> task, String name, Uri downloadUri,CheckBox bsc, CheckBox msc) {

        String Bsc = null;
        if (bsc.isChecked() == true) {
            Bsc = "bsc";

        }
        String Msc = null;
        if (msc.isChecked() == true) {
            Msc = "msc";
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("Bsc", Bsc);
        map.put("Msc", Msc);
        map.put("image", downloadUri.toString());
        firestore.collection("Users").document(Uid).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SetUpActivity.this, "Profile Settings Saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SetUpActivity.this, MainActivity.class));
                    finish();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SetUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                mImageUri = result.getUri();
                circleImageView.setImageURI(mImageUri);

                isPhotoSelected = true;
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}