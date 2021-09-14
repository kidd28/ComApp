package com.kidd.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NF extends Fragment {
    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;
    ArrayList<ModelPost> modelPostList;
    AdapterNewsFeed adapterNewsFeed;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;

    TextView uname;
    EditText writePost;
    ImageView udp;
    
    SwipeRefreshLayout pullToRefresh;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NF() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NF.
     */
    // TODO: Rename and change types and number of parameters
    public static NF newInstance(String param1, String param2) {
        NF fragment = new NF();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_n, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = v.findViewById(R.id.postRv);
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        uname = v.findViewById(R.id.U_name);
        writePost = v.findViewById(R.id.writePost);
        udp = v.findViewById(R.id.U_dp);

        DatabaseReference ref1 = database.getReference("Users");
        Query query = ref1.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dname = "" + ds.child("name").getValue();
                    String dp = "" + ds.child("image").getValue();
                    uname.setText(dname);

                    try {
                        Glide
                                .with(getActivity())
                                .load(dp)
                                .centerCrop()
                                .placeholder(R.drawable.ic_def_img)
                                .into(udp);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        modelPostList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapterNewsFeed = new AdapterNewsFeed(getActivity(), modelPostList);
        recyclerView.setAdapter(adapterNewsFeed);
        pullToRefresh = v.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                user.reload();
                loadPost();
                pullToRefresh.setRefreshing(false);
            }
        });
        writePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WritePost.class));
            }
        });
        loadPost();
        return v;
    }
    private void loadPost() {
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelPostList.clear();
                for (DataSnapshot gr : snapshot.getChildren()) {
                    DataSnapshot post = gr.child("Posts");
                    for (DataSnapshot posts : post.getChildren()) {
                        if (gr.child("Members").child(user1.getUid()).exists()) {
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
                adapterNewsFeed = new AdapterNewsFeed(getActivity(), modelPostList);
                recyclerView.setAdapter(adapterNewsFeed);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}




