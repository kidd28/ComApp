package com.kidd.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListGroup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListGroup extends Fragment {

    RecyclerView groupRv;
    ArrayList<ModelGroup> modelGroups;
    AdapterJoinGroup adapterJoinGroup;
    SwipeRefreshLayout pullToRefresh;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListGroup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListGroup.
     */
    // TODO: Rename and change types and number of parameters
    public static ListGroup newInstance(String param1, String param2) {
        ListGroup fragment = new ListGroup();
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
        View v= inflater.inflate(R.layout.fragment_list_group, container, false);
        groupRv=v.findViewById(R.id.groupRv);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        groupRv.setLayoutManager(layoutManager);
        modelGroups = new ArrayList<>();


        loadGroup();


        return v;
    }
    private void loadGroup() {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelGroups.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(!ds.child("Members").child(user.getUid()).exists()) {
                        ModelGroup model = ds.getValue(ModelGroup.class);
                        modelGroups.add(model);
                    }
                }
                adapterJoinGroup = new AdapterJoinGroup(getContext(), modelGroups);
                groupRv.setAdapter(adapterJoinGroup);
                adapterJoinGroup.notifyDataSetChanged();
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
                    if (!ds.child("Members").child(user.getUid()).exists()) {
                        if (ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase())) {
                            modelGroups.add(model);
                        }
                    }
                }
                adapterJoinGroup = new AdapterJoinGroup(getContext(), modelGroups);
                groupRv.setAdapter(adapterJoinGroup);
                adapterJoinGroup.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.pmenu, menu);
        menu.findItem(R.id.Settings).setVisible(false);
        MenuItem item = menu.findItem(R.id.actionSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!TextUtils.isEmpty(s.trim())){
                    searchGroup(s);
                }else{
                    loadGroup();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())){
                    searchGroup(s);
                }else{
                    loadGroup();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Settings) {

            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
        if (id == R.id.creategroup) {
            startActivity(new Intent(getActivity(), CreateGroupActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}