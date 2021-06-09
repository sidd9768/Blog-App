package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private EditText usernameText;
    private EditText passwordText;
    private Button loginBtn;
    private TextView signupOnLogin;
    private ProgressBar loginProgress;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        usernameText = (EditText) findViewById(R.id.username_login);
        passwordText = (EditText) findViewById(R.id.password_login);
        signupOnLogin = (TextView) findViewById(R.id.signup_on_login);
        loginBtn = (Button) findViewById(R.id.login_button);
        loginProgress = (ProgressBar) findViewById(R.id.login_progress_bar);
        forgotPassword = (TextView) findViewById(R.id.forget_password_text);


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!usernameText.getText().toString().matches("") ) {
                    mAuth.sendPasswordResetEmail(usernameText.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Check your e-mail", Toast.LENGTH_SHORT).show();
                                        Log.d("Forgot password: ", "Email sent.");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(LoginActivity.this, "Account does not exist!", Toast.LENGTH_SHORT).show();

                        }
                    });

                } else {

                    Toast.makeText(LoginActivity.this, "Please enter e-mail address", Toast.LENGTH_SHORT).show();

                }
            }
        });

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    loginProgress.setVisibility(View.VISIBLE);

                    if(!usernameText.getText().toString().matches("") && !passwordText.getText().toString().matches("")) {

                        mAuth.signInWithEmailAndPassword(usernameText.getText().toString(), passwordText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    firebaseUser = mAuth.getCurrentUser();
                                    mainActivityIntent();

                                    loginProgress.setVisibility(View.INVISIBLE);

                                } else {

                                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    loginProgress.setVisibility(View.INVISIBLE);

                                }

                            }
                        });
                    }else{

                        Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                        loginProgress.setVisibility(View.INVISIBLE);

                    }

                }
            });


        signupOnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginToSignupIntent();

            }
        });

    }

    public void mainActivityIntent(){

        Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();

    }

    public void loginToSignupIntent(){

        Intent loginToSignup = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(loginToSignup);
        finish();
    }
}
