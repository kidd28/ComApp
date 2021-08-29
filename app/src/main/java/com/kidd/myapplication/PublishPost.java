package com.kidd.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class PublishPost extends AppCompatActivity {
    TextView uName, time;
    ImageView Dp;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String groupId;

    DatabaseReference reference;

    EditText title, desc;
    ImageView postImg;
    Button upBtn;
    Uri image_uri = null;

    String groupTitle;

    ProgressDialog progressDialog;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_GALLERY_PICK_CODE = 300;
    private static final int IMAGE_CAMERA_PICK_CODE = 400;
    String[] cameraPermission;
    String[] storagePermission;
    String name, email, uid,dp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_post);


        title = findViewById(R.id.Title);
        desc = findViewById(R.id.Description);
        upBtn= findViewById(R.id.uploadbtn);
        postImg = findViewById(R.id.ImageV);

        uName = findViewById(R.id.name);
        time = findViewById(R.id.time);
        Dp = findViewById(R.id.dp);
        loadUserInfo();

        groupId = getIntent().getStringExtra("grId");
        groupTitle = getIntent().getStringExtra("grName");


        firebaseAuth = FirebaseAuth.getInstance();
        user= firebaseAuth.getCurrentUser();
        checkUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Publish Post");


        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);

        reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query =   reference.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    name = ""+ds.child("name").getValue();
                    email = ""+ds.child("email").getValue();
                    dp = ""+ds.child("image").getValue();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        postImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImgDialog();
            }
        });

        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mtitle= title.getText().toString().trim();
                String mdesc= desc.getText().toString().trim();

                if(TextUtils.isEmpty(mtitle)){
                    Toast.makeText(PublishPost.this, "Plase Enter Title..", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(mdesc)){
                    Toast.makeText(PublishPost.this, "Plase Enter description..", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (image_uri == null){
                    uploadData(mtitle, mdesc , "noImage");
                }else{
                    uploadData(mtitle, mdesc , String.valueOf(image_uri));
                }
            }
        });
    }

    private void loadUserInfo() {
        String ptime = String.valueOf(System.currentTimeMillis());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = ref.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                   String myName = "" + ds.child("name").getValue();
                    String myDp = "" + ds.child("image").getValue();

                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    try {
                        calendar.setTimeInMillis(Long.parseLong(ptime));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    String pTime = android.text.format.DateFormat.format("dd/MM/yyyy", calendar).toString();
                    time.setText(pTime);
                    uName.setText(myName);
                    uName.setText(myName);
                    try {
                        Glide
                                .with(PublishPost.this)
                                .load(myDp)
                                .centerCrop()
                                .placeholder(R.drawable.ic_def_cover)
                                .into(Dp);
                    } catch (Exception e) {

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void uploadData(String mtitle, String mdesc, String uri) {
        progressDialog.setMessage("Publishing Post...");
        progressDialog.show();

        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "Post/" + "post_" +timeStamp;

        if (!uri.equals("noImage")){
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            String downloadUri = uriTask.getResult().toString();

                            if(uriTask.isSuccessful()){
                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("uid",uid);
                                hashMap.put("uName",name);
                                hashMap.put("uEmail",email);
                                hashMap.put("uDp",dp);
                                hashMap.put("pId",timeStamp);
                                hashMap.put("pTitle",mtitle);
                                hashMap.put("pLike","0");
                                hashMap.put("pDescription",mdesc);
                                hashMap.put("pImage",downloadUri);
                                hashMap.put("pTime",timeStamp);
                                hashMap.put("groupId",groupId);
                                hashMap.put("groupTitle",groupTitle);
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                                reference1.child(groupId).child("Posts").child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.dismiss();
                                                Toast.makeText(PublishPost.this, "Post published", Toast.LENGTH_SHORT).show();
                                                 title.setText("");
                                                 desc.setText("");
                                                 postImg.setImageURI(null);
                                                 image_uri = null;
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(PublishPost.this, "Publish Failed.."+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(PublishPost.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("uid",uid);
            hashMap.put("uName",name);
            hashMap.put("uEmail",email);
            hashMap.put("uDp",dp);
            hashMap.put("pId",timeStamp);
            hashMap.put("pTitle",mtitle);
            hashMap.put("pLike","0");
            hashMap.put("pDescription",mdesc);
            hashMap.put("pImage","noImage");
            hashMap.put("pTime",timeStamp);
            hashMap.put("groupId",groupId);
            hashMap.put("groupTitle",groupTitle);
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
            reference1.child(groupId).child("Posts").child(timeStamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(PublishPost.this, "Post published", Toast.LENGTH_SHORT).show();
                            title.setText("");
                            desc.setText("");
                            postImg.setImageURI(null);
                            image_uri = null;
                            startActivity(new Intent(PublishPost.this, Home.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(PublishPost.this, "Publish Failed.."+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void checkUser()
    {
        if (user!=null){
            email = user.getEmail();
            uid = user.getUid();
        }else{
            startActivity(new Intent(this , MainActivity.class));
            finish();

        }
    }
    private void showImgDialog() {
        String option[] = {"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickFromCamera();
                    }

                }else if(which==1){
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }else{
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }
    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermission, STORAGE_REQUEST_CODE);
    }
    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1 ;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermission, CAMERA_REQUEST_CODE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    private void pickFromCamera() {
        ContentValues values =new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri =this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAMERA_PICK_CODE);
    }
    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_GALLERY_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }else{
                        Toast.makeText(this, "Please Enable Camera & Storage Permission",Toast.LENGTH_SHORT).show();
                    }

                }
            }break;
            case STORAGE_REQUEST_CODE:{
                boolean writeStorageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted){
                    pickFromGallery();
                }else{
                    Toast.makeText(this, "Please Enable Storage Permission",Toast.LENGTH_SHORT).show();
                }

            }break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_GALLERY_PICK_CODE){
                image_uri = data.getData();
                postImg.setImageURI(image_uri);
            }
            else if (requestCode == IMAGE_CAMERA_PICK_CODE){
                postImg.setImageURI(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}