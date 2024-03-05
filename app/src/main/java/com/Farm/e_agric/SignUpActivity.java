package com.Farm.e_agric;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

//class to signup or register new user to the system
public class SignUpActivity extends AppCompatActivity {

    private EditText emailSignUp , passSignUp;
    private Button signUpBtn;
    private TextView signINText;

    //calling an instance of firebase authentication
    private FirebaseAuth auth;
    @Override
    //setup xml for signup activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //using firebase instance
        auth = FirebaseAuth.getInstance();
        //collecting xml id's
        emailSignUp = findViewById(R.id.sign_up_email);
        passSignUp = findViewById(R.id.sing_up_pass);
        signUpBtn = findViewById(R.id.sign_up_btn);
        signINText = findViewById(R.id.sign_in_text);
        //taking the user to the signin xml
        signINText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this , SignInActivity.class));
            }
        });
        //behaviour when the user clicks signup button
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //collect the all values from the edittexts
                String email = emailSignUp.getText().toString();
                String pass = passSignUp.getText().toString();
                //do this iff only both fields are present

                if (!email.isEmpty() && !pass.isEmpty()){
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //make a toast if storing is successful
                            if (task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "Registered Successfully !!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this , SetUpActivity.class));
                                finish();
                            }
                            //throw an exception if firebase authentication
                        else{
                                //delete here
                                //till here
                                Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                // either or none of edittext are filled
                else{
                    Toast.makeText(SignUpActivity.this, "Please Enter Email and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}