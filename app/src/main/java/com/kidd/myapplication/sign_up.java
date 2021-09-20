package com.kidd.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class sign_up extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    EditText email, pass, name, phone, userName;
    Button reg;
    TextView login;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    ImageView facebook, google;
    FirebaseUser user;
    DatabaseReference reference;
    FirebaseDatabase database;
    GoogleSignInAccount account;

    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        FirebaseApp.initializeApp(this);


        email = findViewById(R.id.reg_email);
        pass = findViewById(R.id.reg_pass);
        reg = findViewById(R.id.register);
        login = findViewById(R.id.login);
        google = findViewById(R.id.google);
        facebook = findViewById(R.id.facebook);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Wait for a second..");
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(sign_up.this, MainActivity.class));
                finish();
            }
        });

        FirebaseApp.initializeApp(this);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String V_email = email.getText().toString().trim();
                String V_pass = pass.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(V_email).matches()) {
                    email.setError("Invalid Email");
                    email.setFocusable(true);
                } else if (pass.length() < 6) {
                    pass.setError("Password should contain atleast 6 characters");
                    pass.setFocusable(true);
                } else {
                    registerUser(V_email, V_pass);
                }
            }
        });
    }

    private void registerUser(String v_email, String v_pass) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(v_email, v_pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            progressDialog.dismiss();
                            name = findViewById(R.id.reg_name);
                            phone = findViewById(R.id.reg_phone);
                            userName = findViewById(R.id.userName);

                            String email = user.getEmail();
                            String uid = user.getUid();
                            String rname = name.getText().toString().trim();
                            String rphone = phone.getText().toString().trim();
                            String username = userName.getText().toString().trim();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", rname);
                            hashMap.put("phone", rphone);
                            hashMap.put("image", "");
                            hashMap.put("cover", "");
                            hashMap.put("bio", "--");
                            hashMap.put("Birthday", "--");
                            hashMap.put("Address", "--");
                            hashMap.put("UserName", username);

                            reference.child(user.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    user.reload();
                                    Toast.makeText(sign_up.this, "Registered.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(sign_up.this, Welcome.class);
                                    intent.putExtra("name",rname);
                                    startActivity(intent);
                                    sign_up.this.finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(sign_up.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(sign_up.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(sign_up.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        ;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                account = task.getResult(ApiException.class);
                Toast.makeText(this, "Google sign in successfully", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign in failed:" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                            // Sign in success, update UI with the signed-in user's information
                            if (isNew) {
                                Intent intent = new Intent(sign_up.this, Login_new.class);
                                intent.putExtra("email", account.getEmail());
                                intent.putExtra("uid", user1.getUid());
                                intent.putExtra("name", account.getDisplayName().substring(0, 1).toUpperCase() + account.getDisplayName().substring(1).toLowerCase());
                                startActivity(intent);
                            } else {
                                Toast.makeText(sign_up.this, "Account already exists."+ account.getDisplayName(), Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(sign_up.this);
                                builder.setTitle("Account already exists.");
                                builder.setMessage("Continue to Login page?");
                                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(sign_up.this, MainActivity.class));
                                        finish();
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create().show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(sign_up.this, "Sign-in Failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        sign_up.this.finish();
        return super.onSupportNavigateUp();
    }

}