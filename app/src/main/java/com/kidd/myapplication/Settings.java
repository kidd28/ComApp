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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

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

import java.util.HashMap;

public class Settings extends AppCompatActivity {

    ImageView dp;
    TextView name, email, profile, help, about, Logout;
    FirebaseAuth auth;

    FirebaseUser user;
    Toolbar toolbar;

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    StorageReference storageReference;
    ProgressDialog progress;

    String storagePath = "User_Profile_Cover_Imgs/";

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_GALLERY_PICK_CODE = 300;
    private static final int IMAGE_CAMERA_PICK_CODE = 400;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri = null;
    String CoverOrProfile;
    Boolean change = false;
    Boolean changeComdp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dp = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        profile = findViewById(R.id.Profile);
        help = findViewById(R.id.Help);
        about = findViewById(R.id.About);
        Logout = findViewById(R.id.Logout);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        toolbar = findViewById(R.id.toolbar);
        progress = new ProgressDialog(this);

        setSupportActionBar(toolbar);
        this.setTitle("Settings");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dname = "" + ds.child("name").getValue();
                    String demail = "" + ds.child("email").getValue();
                    String dimg = "" + ds.child("image").getValue();
                    name.setText(dname);
                    email.setText(demail);
                    try {
                        Glide
                                .with(Settings.this)
                                .load(dimg)
                                .centerCrop()
                                .placeholder(R.drawable.ic_def_img)
                                .into(dp);
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Settings.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to Logout?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                auth.signOut();
                                startActivity(new Intent(Settings.this, MainActivity.class));
                                finish();
                            }
                        }).create().show();

            }
        });
    }
    private boolean checkStoragePermission() {
        boolean result = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
        checkStoragePermission();
        if (checkCameraPermission()) {
            pickFromGallery();
        }
    }
    private boolean checkCameraPermission() {
        boolean result = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
        checkCameraPermission();
        if (!checkCameraPermission()) {
            requestCameraPermission();
        } else {
            pickFromCamera();
        }
    }


    private void showEditProfileDialog() {
        String option[] = {"Edit Profile Picture", "Edit Cover", "Edit Name", "Edit Phone", "Edit Address", "Edit Bio", "Edit Birthday"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    progress.setMessage("Updating Profile Picture");
                    showImgDialog();
                    CoverOrProfile = "image";
                } else if (which == 1) {
                    progress.setMessage("Updating Cover Photo");
                    CoverOrProfile = "cover";
                    showImgDialog();
                } else if (which == 2) {
                    progress.setMessage("Updating Name");
                    showNamePhoneUpdateDialog("name");
                } else if (which == 3) {
                    progress.setMessage("Updating Phone");
                    showNamePhoneUpdateDialog("phone");
                } else if (which == 4) {
                    progress.setMessage("Updating Address");
                    showNamePhoneUpdateDialog("Address");
                } else if (which == 5) {
                    progress.setMessage("Updating bio");
                    showNamePhoneUpdateDialog("bio");
                } else if (which == 6) {
                    progress.setMessage("Updating Birthday");
                    showNamePhoneUpdateDialog("Birthday");
                }
            }
        });
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update " + key);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);

        EditText editText = new EditText(this);

        if (key == "Birthday") {
            editText.setHint("Month/Day/Year ");
            linearLayout.addView(editText);
        } else {
            editText.setHint("Enter " + key);
            linearLayout.addView(editText);
        }

        builder.setView(linearLayout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {
                    progress.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progress.dismiss();
                                    Toast.makeText(Settings.this, "Updated..", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progress.dismiss();
                                    Toast.makeText(Settings.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(Settings.this, "Please Enter " + key, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    private void showImgDialog() {
        String option[] = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }

                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Please Enable Camera & Storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        Toast.makeText(Settings.this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                        pickFromGallery();
                    } else {
                        Toast.makeText(Settings.this, "Please Enable Storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_PICK_CODE) {
                image_uri = data.getData();
                uploadProfileCoverPhoto(image_uri);
            }
            if (requestCode == IMAGE_CAMERA_PICK_CODE) {
                uploadProfileCoverPhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        progress.setMessage("Updating..");
        progress.show();

        String filePathAndName = storagePath + "" + CoverOrProfile + "_" + user.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String downloadUri = uriTask.getResult().toString();
                        if (uriTask.isSuccessful()) {
                            HashMap<String, Object> results = new HashMap<>();
                            results.put(CoverOrProfile, downloadUri);
                            reference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            changedp();
                                            updateCommentDp();
                                            progress.dismiss();
                                            Toast.makeText(Settings.this, "Image Updated..", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progress.dismiss();
                                            Toast.makeText(Settings.this, "Error Updating image..", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progress.dismiss();
                            Toast.makeText(Settings.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.dismiss();
                        Toast.makeText(Settings.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void changedp() {
        change = true;
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users");
        Query query = reference1.orderByChild("email").equalTo(user1.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dimg = "" + ds.child("image").getValue();
                    DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                    ref1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (change) {
                                for (DataSnapshot gr : snapshot.getChildren()) {
                                    DataSnapshot post = gr.child("Posts");
                                    for (DataSnapshot posts : post.getChildren()) {
                                        DatabaseReference ref2 = posts.getRef();
                                        if (posts.child("Shared").getValue().equals("false")) {
                                            if (posts.child("uid").getValue().equals(user1.getUid())) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("uDp", dimg);
                                                change = false;
                                                ref2.updateChildren(hashMap);
                                            }
                                        } else if (posts.child("Shared").getValue().equals("true")) {
                                            if (posts.child("ShareUid").getValue().equals(user1.getUid())) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("ShareDp", dimg);
                                                change = false;
                                                ref2.updateChildren(hashMap);
                                            }
                                            if (posts.child("uid").getValue().equals(user1.getUid())) {
                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("uDp", dimg);
                                                change = false;
                                                ref2.updateChildren(hashMap);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateCommentDp() {
        changeComdp = true;

        FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users");
        Query query1 = reference2.orderByChild("email").equalTo(user2.getEmail());
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String dimg = "" + ds.child("image").getValue();
                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Groups");
                    ref2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot gr : snapshot.getChildren()) {
                                DataSnapshot post = gr.child("Posts");
                                for (DataSnapshot posts : post.getChildren()) {
                                    DatabaseReference ref3 = posts.getRef().child("Comment");
                                    ref3.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (changeComdp) {
                                                for (DataSnapshot com : snapshot.getChildren()) {
                                                    if (com.child("uid").getValue().equals(user2.getUid())) {
                                                        HashMap<String, Object> hashMap1 = new HashMap<>();
                                                        hashMap1.put("uDp", dimg);
                                                        DatabaseReference comm = com.getRef();
                                                        comm.updateChildren(hashMap1);
                                                        changeComdp = false;
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");

        image_uri = Settings.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

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
    public void onBackPressed() {
        Settings.super.onBackPressed();
        startActivity(new Intent(Settings.this, Home.class));
        finish();

    }
}