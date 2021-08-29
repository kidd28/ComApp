package com.kidd.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SharePost extends AppCompatActivity {
    RecyclerView groupRv;
    ArrayList<ModelGroup> modelGroups;
    Toolbar toolbar;
    AdapterShare adapterShare;
    SwipeRefreshLayout pullToRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_post);
        groupRv=findViewById(R.id.groupRv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        groupRv.setLayoutManager(layoutManager);
        modelGroups = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Share Post");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        loadGroup();
        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadGroup();
                user.reload();
                pullToRefresh.setRefreshing(false);
            }
        });
    }
    private void loadGroup() {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelGroups.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("Members").child(user.getUid()).exists()) {
                        ModelGroup model = ds.getValue(ModelGroup.class);
                        modelGroups.add(model);
                    }
                }
                adapterShare = new AdapterShare(SharePost.this, modelGroups );
                groupRv.setAdapter(adapterShare);
                adapterShare.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void searchGroup(String query) {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        modelGroups = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelGroups.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelGroup model = ds.getValue(ModelGroup.class);
                    if (ds.child("Members").child(user.getUid()).exists()) {
                        if (ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase())) {
                            modelGroups.add(model);
                        }
                    }
                }
                adapterShare = new AdapterShare(SharePost.this, modelGroups);
                groupRv.setAdapter(adapterShare);
                adapterShare.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}