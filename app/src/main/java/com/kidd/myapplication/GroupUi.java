package com.kidd.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GroupUi extends AppCompatActivity {

    ImageView avatar;
    TextView title;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    ArrayList<ModelPost> modelPostList;
    AdapterPost adapterPost;
    String grId, grtime;
    SwipeRefreshLayout pullToRefresh;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupfeed);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.setTitle("Group");

        FloatingActionButton fab = findViewById(R.id.fab);

        avatar = findViewById(R.id.avatar);
        title = findViewById(R.id.Title);
        String Avatar = getIntent().getStringExtra("grIcon");
        String Title = getIntent().getStringExtra("grName");
        grId = getIntent().getStringExtra("grId");
        grtime = getIntent().getStringExtra("grTime");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Group");

        title.setText(Title);

        Glide
                .with(GroupUi.this)
                .load(Avatar)
                .centerCrop()
                .placeholder(R.drawable.ic_group)
                .into(avatar);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.groupRv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        modelPostList = new ArrayList<>();
        FirebaseApp.initializeApp(this);

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPost();
                user.reload();
                pullToRefresh.setRefreshing(false);
            }
        });

        loadPost();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupUi.this, PublishPost.class);
                intent.putExtra("grId", grId);
                intent.putExtra("grName", Title);
                startActivity(intent);
            }
        });

    }

    private void loadPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups").child(grtime);
        DatabaseReference reference1 = reference.child("Posts");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelPostList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);
                    modelPostList.add(modelPost);
                    Collections.sort(modelPostList, new Comparator<ModelPost>() {
                        @Override
                        public int compare(ModelPost o1, ModelPost o2) {
                            return Float.compare(Float.parseFloat(o2.getpId()),Float.parseFloat(o1.getpId()));
                        }
                    });
                }
                adapterPost = new AdapterPost(GroupUi.this, modelPostList);
                recyclerView.setAdapter(adapterPost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Members) {
            Intent intent = new Intent(GroupUi.this, Members.class);
            intent.putExtra("grId", grId);
            startActivity(intent);

        }
        if (id == R.id.LeaveGroup) {
            leaveGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    private void leaveGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Group");
        builder.setMessage("Are you sure you want to leave?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups").child(grId);
                DatabaseReference ref1 = ref.child("Members");
                ref1.child(user.getUid())
                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GroupUi.this, "Leave Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(GroupUi.this, Home.class));
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();

    }


}


