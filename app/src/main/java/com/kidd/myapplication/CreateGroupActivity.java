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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class CreateGroupActivity extends AppCompatActivity {
    private ImageView grPhoto;
    private EditText grName, grDesc;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_GALLERY_PICK_CODE = 300;
    private static final int IMAGE_CAMERA_PICK_CODE = 400;
    String[] cameraPermission;
    String[] storagePermission;
    Uri image_uri;
    DatabaseReference reference;
    String email;

    String name,image;
    TextView textView;


    private FirebaseAuth firebaseAuth;
    private FloatingActionButton create;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        grPhoto=findViewById(R.id.GroupPhoto);
        grName=findViewById(R.id.groupname);
        grDesc=findViewById(R.id.groupdesc);
        create=findViewById(R.id.fab);
        firebaseAuth =FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        email = user.getEmail();
        textView = findViewById(R.id.grphoto);
        
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Create Group");




        reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query =   reference.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()){
                    name = ""+ds.child("name").getValue();
                    email = ""+ds.child("email").getValue();
                    image = ""+ds.child("image").getValue();
                }
            }
            @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
        checkUser();
        grPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImgDialog();
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImgDialog();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateGroup();
            }
        });
    }
    private void checkUser() {
    }
    private void startCreateGroup() {
        progress = new ProgressDialog(this);
        progress.setMessage("Creating Group");

        String groupTitle = grName.getText().toString().trim();
        String groupdesc = grDesc.getText().toString().trim();


        if(TextUtils.isEmpty(groupTitle)){
            Toast.makeText(this, "Please enter group Name", Toast.LENGTH_SHORT).show();
            return;
        }
        progress.show();

        String gr_timeStamp =""+ System.currentTimeMillis();

        if(image_uri == null){
            createGroup(""+gr_timeStamp,""+groupTitle, ""+groupdesc,"");
        }else{
            String  filePathAndName = "Group_Imgs/"+ "image" +gr_timeStamp;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();

                            while (!p_uriTask.isSuccessful());
                            Uri p_downloadUri =p_uriTask.getResult();
                            if (p_uriTask.isSuccessful()){
                                createGroup(""+gr_timeStamp,""+groupTitle, ""+groupdesc, ""+p_downloadUri);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CreateGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    private void createGroup(String gr_timeStamp,String groupTitle, String groupDesc, String groupIcon ) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId",""+gr_timeStamp);
        hashMap.put("groupTitle",""+groupTitle);
        hashMap.put("groupDesc",""+groupDesc);
        hashMap.put("groupIcon", groupIcon);
        hashMap.put("timestamp",""+gr_timeStamp);
        hashMap.put("owner",""+ firebaseAuth.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(gr_timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        FirebaseUser user =firebaseAuth.getCurrentUser();
                        progress.dismiss();
                        Toast.makeText(CreateGroupActivity.this, "Group created successfully", Toast.LENGTH_SHORT).show();
                        HashMap<String, String> hashMap1 = new HashMap<>();
                        hashMap1.put("uid", firebaseAuth.getUid());
                        hashMap1.put("email",user.getEmail());
                        hashMap1.put("role","creator");
                        hashMap1.put("timestamp",gr_timeStamp);
                        hashMap1.put("name",name);
                        hashMap1.put("image",image);
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                        reference1.child(gr_timeStamp).child("Members").child(firebaseAuth.getUid()).setValue(hashMap1)
                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                 progress.dismiss();
                                 Toast.makeText(CreateGroupActivity.this, "Group Created..", Toast.LENGTH_SHORT).show();
                                 startActivity(new Intent(CreateGroupActivity.this,Home.class));
                             }
                         }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress.dismiss();
                                Toast.makeText(CreateGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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

        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                grPhoto.setImageURI(image_uri);
            }
            if (requestCode == IMAGE_CAMERA_PICK_CODE){
                grPhoto.setImageURI(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}