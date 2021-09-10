package com.kidd.myapplication;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class OtherProfile extends AppCompatActivity {

    ProgressDialog progress;
    DatabaseReference reference;
    FirebaseAuth mAuth;

    ImageView avatar, cover;
    TextView  email, phone, address, bio, birthdate;


    String umail, uid;
    SwipeRefreshLayout pullToRefresh;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        email = findViewById(R.id.email);
        umail = getIntent().getStringExtra("email");
        String uname = getIntent().getStringExtra("name");

        avatar = findViewById(R.id.avatar);
        cover = findViewById(R.id.cover);

        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.addresss);
        bio = findViewById(R.id.bio);
        birthdate = findViewById(R.id.birthday);
        mAuth = FirebaseAuth.getInstance();
        viewPager = findViewById(R.id.otherPage);

        CollapsingToolbarLayout Ctoolbar = findViewById(R.id.collapse);
        Ctoolbar.setTitle(uname);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        ImagePopup avatarPop = new ImagePopup(this);
        ImagePopup coverPop = new ImagePopup(this);

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

        reference = FirebaseDatabase.getInstance().getReference("Users");
        progress = new ProgressDialog(this);

        Query query = reference.orderByChild("email").equalTo(umail);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dname = "" + ds.child("name").getValue();
                    String demail = "" + ds.child("email").getValue();
                    String dphone = "" + ds.child("phone").getValue();
                    String daddress = "" + ds.child("Address").getValue();
                    String dimg = "" + ds.child("image").getValue();
                    String dbio = "" + ds.child("bio").getValue();
                    String dbday = "" + ds.child("Birthday").getValue();
                    String dcover = "" + ds.child("cover").getValue();
                    uid = "" + ds.child("uid").getValue();


                    email.setText(demail);
                    phone.setText(dphone);
                    address.setText(daddress);
                    bio.setText(dbio);
                    birthdate.setText(dbday);
                    coverPop.initiatePopupWithGlide(dcover);
                    avatarPop.initiatePopupWithGlide(dimg);

                    try {


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
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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


        final BubbleNavigationLinearView bubbleNavigationLinearView = findViewById(R.id.Other_bottom_navigation_view_linear);

        AdapterOtherProfile adapter = new AdapterOtherProfile(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bubbleNavigationLinearView.setCurrentActiveItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                viewPager.setCurrentItem(position, true);
            }
        });
    }

}