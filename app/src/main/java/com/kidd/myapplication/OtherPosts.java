package com.kidd.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherPosts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherPosts extends Fragment {
    RecyclerView recyclerView;
    ArrayList<ModelPost> modelPostList;
    AdapterNewsFeed adapterNewsFeed;
    String umail, uid;
    SwipeRefreshLayout pullToRefresh;

    ProgressDialog progress;
    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseAuth mAuth;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OtherPosts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtherPosts.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherPosts newInstance(String param1, String param2) {
        OtherPosts fragment = new OtherPosts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_other_posts, container, false);

        Intent intent = ((Activity) getContext()).getIntent();
        umail = intent.getStringExtra("email");
        uid = intent.getStringExtra("uid");

        recyclerView = v.findViewById(R.id.postRv);
        modelPostList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapterNewsFeed = new AdapterNewsFeed(getContext(), modelPostList);
        recyclerView.setAdapter(adapterNewsFeed);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadPost();
        return v;
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
                        if (posts.child("Shared").getValue().equals("false")) {
                            if (posts.child("uid").getValue().equals(uid)) {
                                ModelPost modelPost = posts.getValue(ModelPost.class);
                                modelPostList.add(modelPost);
                                Collections.sort(modelPostList, new Comparator<ModelPost>() {
                                    @Override
                                    public int compare(ModelPost o1, ModelPost o2) {
                                        return Float.compare(Float.parseFloat(o2.getpId()), Float.parseFloat(o1.getpId()));
                                    }
                                });
                            }
                        } else if (posts.child("Shared").getValue().equals("true")) {
                            if (posts.child("ShareUid").getValue().equals(uid)) {
                                ModelPost modelPost = posts.getValue(ModelPost.class);
                                modelPostList.add(modelPost);
                                Collections.sort(modelPostList, new Comparator<ModelPost>() {
                                    @Override
                                    public int compare(ModelPost o1, ModelPost o2) {
                                        return Float.compare(Float.parseFloat(o2.getpId()), Float.parseFloat(o1.getpId()));
                                    }
                                });
                            }
                        }
                    }
                    adapterNewsFeed = new AdapterNewsFeed(getContext(), modelPostList);
                    recyclerView.setAdapter(adapterNewsFeed);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}