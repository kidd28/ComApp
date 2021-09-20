package com.kidd.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login_new extends AppCompatActivity {
    EditText N_email, N_name, N_phone, N_userName;
    Button next;
    String email,name,uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        N_name = findViewById(R.id.reg_name);
        N_phone = findViewById(R.id.reg_phone);
        N_userName = findViewById(R.id.userName);
        N_email = findViewById(R.id.reg_email);
        next = findViewById(R.id.next);

        email = getIntent().getStringExtra("email");
        name = getIntent().getStringExtra("name");
        uid = getIntent().getStringExtra("uid");
        N_email.setText(email);
        N_name.setText(name);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n_name = N_name.getText().toString().trim();
                String n_phone = N_phone.getText().toString().trim();
                String n_username = N_userName.getText().toString().trim();
                String n_email = N_email.getText().toString().trim();

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("email", n_email);
                hashMap.put("uid", uid);
                hashMap.put("name", n_name);
                hashMap.put("phone", n_phone);
                hashMap.put("image", "");
                hashMap.put("cover", "");
                hashMap.put("bio", "--");
                hashMap.put("Birthday", "--");
                hashMap.put("Address", "--");
                hashMap.put("UserName", n_username);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(uid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Login_new.this, "Welcome "+ name , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Login_new.this, Welcome.class);
                        intent.putExtra("name",n_name);
                        startActivity(intent);
                        Login_new.this.finish();
                    }
                });
            }
        });


    }
}