package com.kidd.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OtherProfile extends AppCompatActivity {

   ProgressDialog progress;
   DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    ImageView avatar, cover;
    TextView name, email,phone, address,bio, birthdate;

    RecyclerView recyclerView;
    ArrayList<ModelPost> modelPostList;
    AdapterNewsFeed adapterNewsFeed;
    String umail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        email=findViewById(R.id.email);
        umail = getIntent().getStringExtra("email");

        String uname = getIntent().getStringExtra("name");

        avatar = findViewById(R.id.avatar);
        cover =findViewById(R.id.cover);
        name=findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address=findViewById(R.id.addresss);
        bio = findViewById(R.id.bio);
        birthdate=findViewById(R.id.birthday);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle(uname);

        recyclerView = findViewById(R.id.postRv);
        modelPostList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapterNewsFeed = new AdapterNewsFeed(this,modelPostList);
        recyclerView.setAdapter(adapterNewsFeed);
        loadPost();
        database= FirebaseDatabase.getInstance();
        reference=database.getReference("Users");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress=new ProgressDialog(this);


        Query query = reference.orderByChild("email").equalTo(umail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    String dname=""+ds.child("name").getValue();
                    String demail=""+ds.child("email").getValue();
                    String dphone=""+ds.child("phone").getValue();
                    String daddress=""+ds.child("Address").getValue();
                    String dimg=""+ds.child("image").getValue();
                    String dbio=""+ds.child("bio").getValue();
                    String dbday=""+ds.child("Birthday").getValue();
                    String dcover=""+ds.child("cover").getValue();

                    name.setText(dname);
                    email.setText(demail);
                    phone.setText(dphone);
                    address.setText(daddress);
                    bio.setText(dbio);
                    birthdate.setText(dbday);


                    Glide
                            .with(OtherProfile.this)
                            .load(dimg)
                            .centerCrop()
                            .placeholder(R.drawable.ic_def_img)
                            .into(avatar);
                    Glide
                            .with(OtherProfile.this)
                            .load(dcover)
                            .centerCrop()
                            .placeholder(R.drawable.ic_def_cover)
                            .into(cover);
                }}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void loadPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelPostList.clear();
                for (DataSnapshot gr : snapshot.getChildren()) {
                    DataSnapshot post = gr.child("Posts");
                    for (DataSnapshot posts : post.getChildren()) {
                        if (posts.child("uEmail").getValue().equals(umail)) {
                            ModelPost modelPost = posts.getValue(ModelPost.class);
                            modelPostList.add(modelPost);
                            Collections.sort(modelPostList, new Comparator<ModelPost>() {
                                @Override
                                public int compare(ModelPost o1, ModelPost o2) {
                                    return Float.compare(Float.parseFloat(o2.getpId()),Float.parseFloat(o1.getpId()));
                                }
                            });
                        }
                    }
                    adapterNewsFeed.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}