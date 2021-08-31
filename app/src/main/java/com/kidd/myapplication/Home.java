package com.kidd.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Home extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseDatabase database;
    StorageReference storageReference;
    ViewPager viewPager;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseApp.initializeApp(Home.this);
        viewPager = findViewById(R.id.vp);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Newsfeed");

        final BubbleNavigationLinearView bubbleNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                    bubbleNavigationLinearView.setCurrentActiveItem(position);
                switch (position) {
                    case 0:
                        toolbar.setTitle("Newsfeed");
                        break;
                    case 1:
                        toolbar.setTitle("Groups");
                        break;
                    case 2:
                        toolbar.setTitle("Profile");
                        break;
                }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Settings) {
            startActivity(new Intent(Home.this, Settings.class));
            finish();
        }
        if (id == R.id.creategroup) {
            startActivity(new Intent(Home.this, CreateGroupActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Home.super.onBackPressed();
                       finish();
                    }
                }).create().show();
    }
}








