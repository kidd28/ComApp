package com.kidd.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherGroups#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherGroups extends Fragment {

    RecyclerView groupRv;
    ArrayList<ModelGroup> modelGroups;
    AdapterGroupList adapterGroupList;
    String umail,uid;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OtherGroups() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtherGroups.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherGroups newInstance(String param1, String param2) {
        OtherGroups fragment = new OtherGroups();
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
        View v =  inflater.inflate(R.layout.fragment_other_groups, container, false);
        groupRv=v.findViewById(R.id.groupRv);

        Intent intent = ((Activity) getContext()).getIntent();
        umail = intent.getStringExtra("email");
        uid = intent.getStringExtra("uid");

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        groupRv.setLayoutManager(layoutManager);
        modelGroups = new ArrayList<>();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        loadGroup();

        return  v;
    }
    private void loadGroup() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelGroups.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("Members").child(uid).exists()) {
                        ModelGroup model = ds.getValue(ModelGroup.class);
                        modelGroups.add(model);
                    }
                }
                adapterGroupList = new AdapterGroupList(getContext(), modelGroups);
                groupRv.setAdapter(adapterGroupList);
                adapterGroupList.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}