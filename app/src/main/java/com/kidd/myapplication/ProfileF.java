
package com.kidd.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileF extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;

    RecyclerView recyclerView;
    ArrayList<ModelPost> modelPostList;
    AdapterNewsFeed adapterNewsFeed;

    StorageReference storageReference;

    String storagePath = "User_Profile_Cover_Imgs/";

    ImageView avatar, cover;
    TextView name, email, phone, address, bio, birthdate;
    FloatingActionButton fab;
    ProgressDialog progress;
    SwipeRefreshLayout pullToRefresh;


    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_GALLERY_PICK_CODE = 300;
    private static final int IMAGE_CAMERA_PICK_CODE = 400;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri = null;
    String CoverOrProfile;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileF() {

        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileF.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileF newInstance(String param1, String param2) {
        ProfileF fragment = new ProfileF();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        avatar = v.findViewById(R.id.avatar);
        cover = v.findViewById(R.id.cover);
        name = v.findViewById(R.id.name);
        email = v.findViewById(R.id.email);
        phone = v.findViewById(R.id.phone);
        address = v.findViewById(R.id.addresss);
        bio = v.findViewById(R.id.bio);
        birthdate = v.findViewById(R.id.birthday);

        ImagePopup avatarPop = new ImagePopup(getContext());
        ImagePopup coverPop = new ImagePopup(getContext());

        avatarPop.setWindowHeight(1000);
        avatarPop.setWindowWidth(1000);
        avatarPop.setBackgroundColor(Color.TRANSPARENT);
        avatarPop.setHideCloseIcon(true);
        avatarPop.setImageOnClickClose(true);

        coverPop.setWindowHeight(1000);
        coverPop.setWindowWidth(1000);
        coverPop.setBackgroundColor(Color.TRANSPARENT);
        coverPop.setHideCloseIcon(true);
        coverPop.setImageOnClickClose(true);

        recyclerView = v.findViewById(R.id.postRv);
        modelPostList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        pullToRefresh = v.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPost();
                user.reload();
                pullToRefresh.setRefreshing(false);
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avatarPop.viewPopup();
            }
        });
        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coverPop.viewPopup();
            }
        });

        progress = new ProgressDialog(getContext());
        Query query = reference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pullToRefresh.setRefreshing(true);
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dname = "" + ds.child("name").getValue();
                    String demail = "" + ds.child("email").getValue();
                    String dphone = "" + ds.child("phone").getValue();
                    String daddress = "" + ds.child("Address").getValue();
                    String dimg = "" + ds.child("image").getValue();
                    String dcover = "" + ds.child("cover").getValue();
                    String dbio = "" + ds.child("bio").getValue();
                    String dbday = "" + ds.child("Birthday").getValue();

                    name.setText(dname);
                    email.setText(demail);
                    phone.setText(dphone);
                    address.setText(daddress);
                    bio.setText(dbio);
                    birthdate.setText(dbday);

                    coverPop.initiatePopupWithGlide(dcover);
                    avatarPop.initiatePopupWithGlide(dimg);

                    if (getActivity() != null) {
                        Glide
                                .with(getActivity())
                                .load(dimg)
                                .centerCrop()
                                .placeholder(R.drawable.ic_def_img)
                                .into(avatar);

                        Glide
                                .with(getActivity())
                                .load(dcover)
                                .centerCrop()
                                .placeholder(R.drawable.ic_def_cover)
                                .into(cover);
                    }
                    pullToRefresh.setRefreshing(false);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        loadPost();
        return v;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pmenu, menu);
        menu.findItem(R.id.actionSearch).setVisible(false);
        menu.findItem(R.id.creategroup).setVisible(false);
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
                        if (posts.child("uid").getValue().equals(user1.getUid())) {
                            ModelPost modelPost = posts.getValue(ModelPost.class);
                            modelPostList.add(modelPost);
                            Collections.sort(modelPostList, new Comparator<ModelPost>() {
                                @Override
                                public int compare(ModelPost o2, ModelPost o1) {
                                    return Float.compare(Float.parseFloat(o1.getpId()), Float.parseFloat(o2.getpId()));
                                }
                            });
                        }
                    }
                    adapterNewsFeed = new AdapterNewsFeed(getActivity(), modelPostList);
                    recyclerView.setAdapter(adapterNewsFeed);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
}
