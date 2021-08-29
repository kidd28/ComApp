package com.kidd.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Members extends AppCompatActivity {
    RecyclerView recyclerView;
    AdapterUser adapterUser;
    List<ModelUser> userList;
    String grId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        recyclerView=findViewById(R.id.userRecyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        adapterUser = new AdapterUser(this, userList);
        recyclerView.setAdapter(adapterUser);
        this.setTitle("Members");

         grId = getIntent().getStringExtra("grId");

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Members");

        FirebaseApp.initializeApp(this);


        getAllUser();

    }

    private void getAllUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups").child(grId);
        DatabaseReference ref1 = ref.child("Members");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot gr : snapshot.getChildren()) {
                            ModelUser modelUser = gr.getValue(ModelUser.class);
                            userList.add(modelUser);
                    }
                    adapterUser.notifyDataSetChanged();
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void searchUser(String query) {

        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot gr : snapshot.getChildren()) {
                    DataSnapshot post = gr.child("Members");
                    if (gr.child("Members").child(user.getUid()).exists()) {
                        for (DataSnapshot posts : post.getChildren()) {
                            ModelUser modelUser = posts.getValue(ModelUser.class);
                            userList.add(modelUser);
                        }
                }
                    adapterUser.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    }

