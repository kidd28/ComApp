package com.kidd.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupPrev extends AppCompatActivity {

    ImageView avatar;
    TextView title;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ArrayList<ModelPost> modelPostList;
    AdapterPost adapterPost;
    Button join;
    String grId, grtime,email,name, image;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupprev);

        avatar = findViewById(R.id.avatar);
        title = findViewById(R.id.Title);
        join = findViewById(R.id.joinbtn);
        String userTime = ""+System.currentTimeMillis();

        String Avatar = getIntent().getStringExtra("grIcon");
        String Title = getIntent().getStringExtra("grName");
        grId = getIntent().getStringExtra("grId");
        grtime = getIntent().getStringExtra("grTime");

        firebaseAuth =FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        email = user.getEmail();

        this.setTitle("Join Group");

        title.setText(Title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Glide
                .with(GroupPrev.this)
                .load(Avatar)
                .centerCrop()
                .placeholder(R.drawable.ic_group)
                .into(avatar);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        recyclerView = findViewById(R.id.groupRv);
        fab = findViewById(R.id.fab);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //show newest post
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        modelPostList = new ArrayList<>();

        FirebaseApp.initializeApp(this);



        loadPost();

        ProgressDialog progress = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query =   reference.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    name = ""+ds.child("name").getValue();
                    email = ""+ds.child("email").getValue();
                    image = ""+ds.child("image").getValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                HashMap<String, String> hashMap1 = new HashMap<>();
                                hashMap1.put("uid", firebaseAuth.getUid());
                                hashMap1.put("email",firebaseAuth.getCurrentUser().getEmail());
                                hashMap1.put("role","member");
                                hashMap1.put("timestamp",userTime);
                                hashMap1.put("name",name);
                                hashMap1.put("image",image);
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                                reference1.child(grId).child("Members").child(firebaseAuth.getUid()).setValue(hashMap1)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progress.dismiss();
                                                Toast.makeText(GroupPrev.this, "Joined..", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(GroupPrev.this,Home.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progress.dismiss();
                                        Toast.makeText(GroupPrev.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
    }
        private void loadPost () {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups").child(grtime);
            DatabaseReference reference1 = reference.child("Posts");
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    modelPostList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ModelPost modelPost = ds.getValue(ModelPost.class);
                        modelPostList.add(modelPost);
                    }
                    adapterPost = new AdapterPost(GroupPrev.this, modelPostList);
                    recyclerView.setAdapter(adapterPost);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }


}