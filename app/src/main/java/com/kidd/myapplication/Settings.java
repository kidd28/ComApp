package com.kidd.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {

    ImageView dp;
    TextView name, email, profile, help, about, account, addAcc, switchAcc, Logout;
    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseUser user;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dp = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        profile = findViewById(R.id.Profile);
        help = findViewById(R.id.Help);
        about = findViewById(R.id.About);
        account = findViewById(R.id.Account);
        addAcc = findViewById(R.id.AddAccount);
        switchAcc = findViewById(R.id.SwitchAccount);
        Logout = findViewById(R.id.Logout);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Settings");

        reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dname = "" + ds.child("name").getValue();
                    String demail = "" + ds.child("email").getValue();
                    String dimg = "" + ds.child("image").getValue();

                    name.setText(dname);
                    email.setText(demail);
                    Glide
                            .with(Settings.this)
                            .load(dimg)
                            .centerCrop()
                            .placeholder(R.drawable.ic_def_img)
                            .into(dp);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(Settings.this, MainActivity.class));
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Settings.super.onBackPressed();
        startActivity(new Intent(Settings.this,Home.class));
        finish();

    }
}