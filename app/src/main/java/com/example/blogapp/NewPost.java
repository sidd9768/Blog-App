package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import id.zelory.compressor.Compressor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewPost extends AppCompatActivity {
    private androidx.appcompat.widget.Toolbar newPostToolbar;
    private ProgressBar newPostProgressBar;
    private ImageView newPostImage;
    private EditText newPostTitle;
    private EditText newPostDescription;
    private Button newPostButton;
    private Bitmap compressedImageFile;
    private Uri postImageUri=null;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        newPostToolbar = (Toolbar) findViewById(R.id.new_post_toolbar);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        newPostImage = (ImageView) findViewById(R.id.new_post_image);
        newPostTitle = (EditText) findViewById(R.id.new_post_title);
        newPostDescription = (EditText) findViewById(R.id.new_post_description);
        newPostButton = (Button) findViewById(R.id.post_button);
        newPostProgressBar = (ProgressBar) findViewById(R.id.new_post_progress);

        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add new post");


        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1, 1)
                        .start(NewPost.this);

            }
        });

        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String title = newPostTitle.getText().toString();
                final String description = newPostDescription.getText().toString();

            if(!TextUtils.isEmpty(title) || !TextUtils.isEmpty(description) || postImageUri != null){

                newPostProgressBar.setVisibility(View.VISIBLE);

                final String randomName = UUID.randomUUID().toString();

                StorageReference filePath = storageReference.child("post_images").child(randomName+".jpg");
                filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        final String downnloadUri = task.getResult().getDownloadUrl().toString();

                        if(task.isSuccessful()){

                            File newImageFile = new File(postImageUri.getPath());
                            try {

                                compressedImageFile =  new Compressor(NewPost.this)
                                        .setMaxHeight(150)
                                        .setMaxWidth(150)
                                        .setQuality(6)
                                        .compressToBitmap(newImageFile);

                            } catch (IOException e) {

                                e.printStackTrace();

                            }

                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG,100, byteArrayOutputStream);
                            byte[] thumbData = byteArrayOutputStream.toByteArray();

                            UploadTask uploadTask = storageReference.child("post_images/thumb").child(randomName+".jpg").putBytes(thumbData);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    String downloadThumbUri = taskSnapshot.getDownloadUrl().toString();

                                    Map<String, Object> postMap = new HashMap<>();
                                    postMap.put("image_url", downnloadUri);
                                    postMap.put("thumb_url", downloadThumbUri);
                                    postMap.put("title", title);
                                    postMap.put("description", description);
                                    postMap.put("timestamp", FieldValue.serverTimestamp());

                                    firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {

                                            if(task.isSuccessful()){

                                                newPostProgressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(NewPost.this, "Post added", Toast.LENGTH_SHORT).show();
                                                Intent mainIntent = new Intent(NewPost.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                            }else{

                                                Toast.makeText(NewPost.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    });

                                }
                            });


                        }

                    }
                });

            }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){

                postImageUri = activityResult.getUri();

                newPostImage.setImageURI(postImageUri);

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                Exception error = activityResult.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }

        }
    }
}
