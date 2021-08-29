package com.kidd.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    Button login, Signup;
    EditText login_email, login_pass;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    FirebaseDatabase database;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        login = findViewById(R.id.login);
        Signup = findViewById(R.id.signup);
        login_email = findViewById(R.id.log_email);
        login_pass = findViewById(R.id.log_pass);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait for a second..");

        FirebaseApp.initializeApp(this);



        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, sign_up.class));

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String V_email = login_email.getText().toString().trim();
                String V_pass = login_pass.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(V_email).matches()) {
                    login_email.setError("Invalid Email");
                    login_email.setFocusable(true);
                } else if (login_pass.length() < 6) {
                    login_pass.setError("Password should contain atleast 6 characters");
                    login_pass.setFocusable(true);
                } else {
                    LoginUser(V_email, V_pass);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.reload();
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    private void LoginUser(String v_email, String v_pass) {
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(v_email, v_pass)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Log in Successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, Home.class));
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            MainActivity.this.finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

    }

}



