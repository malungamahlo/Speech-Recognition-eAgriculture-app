package com.Farm.e_agric;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.FirebaseFirestore;
//class to login the existing user to the system
public class SignInActivity extends AppCompatActivity {

    private EditText signInEmail , signInPass;
    private Button signInBtn;
    private TextView sign_up_text;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    @Override
    //setup xml for signin activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //use the firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
        //collecting views by id's
        signInEmail = findViewById(R.id.sign_in_email);
        signInPass = findViewById(R.id.sing_in_pass);
        signInBtn = findViewById(R.id.sign_in_btn);
        sign_up_text = findViewById(R.id.sign_up_text);
        //setting up firestore
        firestore = FirebaseFirestore.getInstance();
        //function that takes us to signup
        sign_up_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this , SignUpActivity.class));
            }
        });
        //behavior when the user clicks the signin button
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //collecting strings from the edit_texts
                String email = signInEmail.getText().toString();
                String pass = signInPass.getText().toString();
                //when email and password are provided
                if (!email.isEmpty() && !pass.isEmpty()){
                    //authenticate the email and password that are entered
                    mAuth.signInWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //if authentication is successful
                           if (task.isSuccessful()){
                               Toast.makeText(SignInActivity.this, "Login Successfull !!", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(SignInActivity.this , MainActivity.class));
                               finish();
                           }
                           //if authentication is not successful
                           else{
                               Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                }
                else{
                    Toast.makeText(SignInActivity.this, "Please Enter Email and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}