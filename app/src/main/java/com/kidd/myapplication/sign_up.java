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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class sign_up extends AppCompatActivity {
        EditText email, pass,name,phone;
        Button reg;
        ProgressDialog progressDialog;
        FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference reference;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        FirebaseApp.initializeApp(this);


        email=findViewById(R.id.reg_email);
        pass=findViewById(R.id.reg_pass);
        reg=findViewById(R.id.register);
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Wait for a second..");
        mAuth = FirebaseAuth.getInstance();

         database = FirebaseDatabase.getInstance();
         reference=database.getReference("Users");


        FirebaseApp.initializeApp(this);



        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String V_email = email.getText().toString().trim();
               String V_pass = pass.getText().toString().trim();

               if(!Patterns.EMAIL_ADDRESS.matcher(V_email).matches()){
                   email.setError("Invalid Email");
                   email.setFocusable(true);
               }
               else if (pass.length()<6){
                   pass.setError("Password should contain atleast 6 characters");
                   pass.setFocusable(true);
               }
               else{
                   registerUser(V_email,V_pass);
               }
            }
        });
    }

    private void registerUser(String v_email, String v_pass) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(v_email,v_pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           user = FirebaseAuth.getInstance().getCurrentUser();
                           progressDialog.dismiss();
                           name=findViewById(R.id.reg_name);
                           phone=findViewById(R.id.reg_phone);

                           String email = user.getEmail();
                           String uid = user.getUid();
                           String rname= name.getText().toString().trim();
                           String rphone=phone.getText().toString().trim();

                           HashMap<String, String> hashMap = new HashMap<>();
                           hashMap.put("email",email);
                           hashMap.put("uid",uid);
                           hashMap.put("name",rname);
                           hashMap.put("phone",rphone);
                           hashMap.put("image","");
                           hashMap.put("cover","");
                           reference.child(user.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   user.reload();
                                   Toast.makeText(sign_up.this,"Registered.",Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(sign_up.this,Welcome.class));
                                   sign_up.this.finish();
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(sign_up.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(sign_up.this,"Authentication failed.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(sign_up.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        })
        ;
    }
    @Override
    public boolean onSupportNavigateUp(){
      onBackPressed();
      sign_up.this.finish();
      return super.onSupportNavigateUp();
    }

}