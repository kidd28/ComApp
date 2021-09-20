package com.kidd.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {
    Button create,join;
    TextView u_name;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        create=findViewById(R.id.create_group);
        join=findViewById(R.id.join_group);
        u_name = findViewById(R.id.Name);
        name= getIntent().getStringExtra("name");

        u_name.setText(name);


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this,CreateGroupActivity.class));
                finish();
            }
        });
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Welcome.this,JoinGroup.class));
                finish();
            }
        });
    }
    public void onBackPressed(){
        finish();
        System.exit(0);
    }
}