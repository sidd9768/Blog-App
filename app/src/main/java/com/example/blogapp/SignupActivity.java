package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText lastnameEditText;
    private EditText firstnameEditText;
    private Button signupBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar signupProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        emailEditText = (EditText) findViewById(R.id.email_signup);
        passwordEditText = (EditText) findViewById(R.id.password_signup);
        lastnameEditText = (EditText) findViewById(R.id.last_name_editText);
        firstnameEditText = (EditText) findViewById(R.id.first_name_ediText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirm_password_signup);
        signupBtn = (Button) findViewById(R.id.signup_btn);
        signupProgress = (ProgressBar) findViewById(R.id.signup_progress_bar);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!emailEditText.getText().toString().matches("") && !passwordEditText.getText().toString().matches("") && !confirmPasswordEditText.getText().toString().matches("")){

                    if(passwordEditText.getText().toString().matches(confirmPasswordEditText.getText().toString())){

                        signupProgress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    Map<String, Object> users = new HashMap<>();
                                    users.put("fullname", firstnameEditText.getText().toString() + " " + lastnameEditText.getText().toString());
                                    users.put("firstName", firstnameEditText.getText().toString());
                                    users.put("lastName", lastnameEditText.getText().toString());
                                    users.put("email", emailEditText.getText().toString());
                                    users.put("timestamp", FieldValue.serverTimestamp());

                                    String fullname_user = firstnameEditText.getText().toString() + " " + lastnameEditText.getText().toString();

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(fullname_user).build();
                                    user.updateProfile(profileChangeRequest);


                                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.blogapp", Context.MODE_PRIVATE);

                                    sharedPreferences.edit().putString("fullname_user", fullname_user).apply();
                                    sharedPreferences.edit().putString("email_user", emailEditText.getText().toString()).apply();


                                    firebaseFirestore.collection("users").add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {

                                            Toast.makeText(SignupActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                            firebaseUser = mAuth.getCurrentUser();
                                            signupToMainActivity();
                                            signupProgress.setVisibility(View.INVISIBLE);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            signupProgress.setVisibility(View.INVISIBLE);

                                        }
                                    });


                                }else{

                                    Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    signupProgress.setVisibility(View.INVISIBLE);

                                }

                            }
                        });

                    }else{

                        Toast.makeText(SignupActivity.this, "Password and Confirm password should be same", Toast.LENGTH_SHORT).show();
                        signupProgress.setVisibility(View.INVISIBLE);

                    }


                }else{

                    Toast.makeText(SignupActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    signupProgress.setVisibility(View.INVISIBLE);

                }

            }
        });

    }

    public void signupToMainActivity(){

        Intent signupToMainActivity = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(signupToMainActivity);
        finish();

    }
}
