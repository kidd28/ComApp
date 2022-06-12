package com.kidd.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class PostDetail extends AppCompatActivity {


    String myUid, myEmail, myName, myDp, pId, hisDp, hisName, groupId, likes, pComment, grName, pUid, pImage, uEmail, groupIcon, groupTime, udp, UserName, ShareUserName, OrigPid;
    String Shared, ShareTo, ShareName, ShareDp;
    ImageView pdp, pImg, arrow, sdp;
    TextView ShareMore, uName, pTime, pTitle, pLike, groupName, moreBtn, shareName, grShareName, shareTime, CommentCount, username, shareusername;
    View view;
    EditText comment;
    ImageButton sendbtn;
    ImageView mAvatar;
    ImagePopup imagePopup;
    ProgressDialog progress;
    ImageView likeBtn, commentBtn, shareBtn;

    RecyclerView recyclerView;
    ArrayList<ModelComment> modelComments;
    AdapterComment adapterComment;

    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;

    DatabaseReference ref;
    DatabaseReference ref1;

    Toolbar toolbar;

    Boolean postLike = false;
    String pCaption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        pdp = findViewById(R.id.dp);
        pImg = findViewById(R.id.ImageV);
        uName = findViewById(R.id.name);
        pTime = findViewById(R.id.time);
        pTitle = findViewById(R.id.pTitle);
        pLike = findViewById(R.id.pLike);
        moreBtn = findViewById(R.id.more);
        likeBtn = findViewById(R.id.likebtn);
        commentBtn = findViewById(R.id.commentbtn);
        shareBtn = findViewById(R.id.sharebtn);
        groupName = findViewById(R.id.group_Name);
        CommentCount = findViewById(R.id.commenCount);
        ShareMore = findViewById(R.id.ShareMore);
        username = findViewById(R.id.username);
        shareusername = findViewById(R.id.ShareUserName);


        shareName = findViewById(R.id.shareName);
        grShareName = findViewById(R.id.grShareName);
        shareTime = findViewById(R.id.shareTime);
        sdp = findViewById(R.id.sdp);
        arrow = findViewById(R.id.arrow);
        view = findViewById(R.id.view);

        comment = findViewById(R.id.comment);
        sendbtn = findViewById(R.id.send);
        mAvatar = findViewById(R.id.my_dp);

        recyclerView = findViewById(R.id.cRV);
        modelComments = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        pId = getIntent().getStringExtra("pId");
        groupId = getIntent().getStringExtra("groupID");
        grName = getIntent().getStringExtra("grName");
        pUid = getIntent().getStringExtra("uid");
        pImage = getIntent().getStringExtra("pImage");
        groupTime = getIntent().getStringExtra("grTime");
        groupIcon = getIntent().getStringExtra("grIcon");
        udp = getIntent().getStringExtra("uDp");
        pComment = getIntent().getStringExtra("pComment");
        UserName = getIntent().getStringExtra("UserName");
        ShareUserName = getIntent().getStringExtra("ShareUserName");
        OrigPid = getIntent().getStringExtra("OrigPid");


        imagePopup = new ImagePopup(this);
        imagePopup.setWindowHeight(1000);
        imagePopup.setWindowWidth(1000);
        imagePopup.setBackgroundColor(Color.TRANSPARENT);
        imagePopup.setHideCloseIcon(true);
        imagePopup.setImageOnClickClose(true);

        Shared = getIntent().getStringExtra("Shared");
        ShareTo = getIntent().getStringExtra("ShareTo");
        ShareName = getIntent().getStringExtra("ShareName");
        ShareDp = getIntent().getStringExtra("ShareDp");

        String uid = getIntent().getStringExtra("uid");
        String uEmail = getIntent().getStringExtra("uEmail");
        String pTime = getIntent().getStringExtra("pTime");
        pCaption = getIntent().getStringExtra("pCaption");
        String uDp = getIntent().getStringExtra("uDp");
        String pImage = getIntent().getStringExtra("pImage");
        String Name = getIntent().getStringExtra("uName");

        PostInfo();
        LoaduserInfo();
        LoadComment();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("Post");
        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostLike(groupId, pId);
            }
        });
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOption(moreBtn, pUid, user.getUid(), groupId, pId, pImage);
            }
        });
        uName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetail.this, OtherProfile.class);
                intent.putExtra("email", uEmail);
                intent.putExtra("name", hisName);
                startActivity(intent);
            }
        });
        groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetail.this, GroupUi.class);
                intent.putExtra("grName", grName);
                intent.putExtra("grIcon", groupIcon);
                intent.putExtra("grId", groupId);
                intent.putExtra("grTime", groupTime);
                startActivity(intent);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Shared.equals("false")) {
                    shareOrig(uid, uEmail, pId, groupId, groupIcon, grName, groupTime, pTime, pCaption, uDp, pImage, Name, OrigPid, UserName);
                } else {
                    shareagain(uid, uEmail, pId, groupId, groupIcon, grName, groupTime, pTime, pCaption, uDp, pImage, Name, OrigPid, UserName);
                }
            }
        });
        pImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePopup.viewPopup();
            }
        });
        ShareMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharemoreOption(ShareMore, uid, user.getUid(), groupId, pId, pImage);
            }
        });
    }


    private void SharemoreOption(TextView moreBtn, String uid, String uid1, String groupId, String pId, String pImage) {
        PopupMenu menu = new PopupMenu(this, moreBtn, Gravity.END);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Posts");
        builder.setMessage("Are you sure you want to Delete this Post?");

        if (uid.equals(user.getUid())) {
            menu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            menu.getMenu().add(Menu.NONE, 1, 2, "Edit");
        }
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0) {
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletePost(pId, groupId);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create().show();
                }
                return false;
            }
        });
        menu.show();
    }

    private void shareagain(String uid, String uEmail, String pId, String groupId, String grIcon, String groupTitle, String groupTime,
                            String pTime, String pCaption, String uDp, String pImage, String uName, String OrigPid, String UserName) {
        Intent intent = new Intent(PostDetail.this, SharePost.class);
        intent.putExtra("uid", uid);
        intent.putExtra("uEmail", uEmail);
        intent.putExtra("pId", pId);
        intent.putExtra("OrigPid", OrigPid);
        intent.putExtra("groupId", groupId);
        intent.putExtra("grIcon", grIcon);
        intent.putExtra("groupTitle", groupTitle);
        intent.putExtra("groupTime", groupTime);
        intent.putExtra("pTime", pTime);
        intent.putExtra("pCaption", pCaption);
        intent.putExtra("uDp", uDp);
        intent.putExtra("pImage", pImage);
        intent.putExtra("uName", uName);
        intent.putExtra("UserName", UserName);
        startActivity(intent);
    }

    private void shareOrig(String uid, String uEmail, String pId, String groupId, String grIcon, String groupTitle, String groupTime, String pTime, String pCaption, String uDp, String pImage, String uName, String OrigPid, String UserName) {
        Intent intent = new Intent(PostDetail.this, SharePost.class);
        intent.putExtra("uid", uid);
        intent.putExtra("uEmail", uEmail);
        intent.putExtra("pId", pId);
        intent.putExtra("OrigPid", pId);
        intent.putExtra("groupId", groupId);
        intent.putExtra("grIcon", grIcon);
        intent.putExtra("groupTitle", groupTitle);
        intent.putExtra("groupTime", groupTime);
        intent.putExtra("pTime", pTime);
        intent.putExtra("pCaption", pCaption);
        intent.putExtra("uDp", uDp);
        intent.putExtra("pImage", pImage);
        intent.putExtra("uName", uName);
        intent.putExtra("UserName", UserName);
        startActivity(intent);
    }

    private void PostLike(String groupId, String pId) {
        postLike = true;
        FirebaseUser user4 = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference UserLike = FirebaseDatabase.getInstance().getReference("Users").child(user4.getUid());
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Posts");
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String counts = "" + snapshot.child(pId).child("Likes").getValue();
                pLike.setText(counts);
                UserLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (postLike) {
                            if (snapshot.child("Liked").hasChild(pId)) {
                                ref3.child(pId).child("Likes").setValue("" + (Integer.parseInt(counts) - 1));
                                Glide
                                        .with(PostDetail.this)
                                        .load(R.drawable.heart__red)
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_def_img)
                                        .into(likeBtn);
                                UserLike.child("Liked").child(pId).removeValue();
                                postLike = false;
                            } else {
                                ref3.child(pId).child("Likes").setValue("" + (Integer.parseInt(counts) + 1));
                                Glide
                                        .with(PostDetail.this)
                                        .load(R.drawable.heart)
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_def_img)
                                        .into(likeBtn);
                                UserLike.child("Liked").child(pId).setValue("Liked");
                                postLike = false;
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void moreOption(TextView moreBtn, String pUid, String uid, String groupId, String postId, String pImage) {
        PopupMenu menu = new PopupMenu(this, moreBtn, Gravity.END);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Posts");
        builder.setMessage("Are you sure you want to Delete this Post?");

        if (pUid.equals(uid)) {
            menu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            menu.getMenu().add(Menu.NONE, 1, 2, "Edit");
        }
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0) {
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletePosts(postId, groupId, pImage);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.create().show();
                } else if (id == 1) {
                    editPost(pId, groupId, pImage, pCaption, grName, groupIcon);
                }
                return false;
            }
        });
        menu.show();
    }

    private void editPost(String pId, String grId, String pImage, String pCaption, String grName, String grIcon) {
        if (pImage.equals("noImage")) {
            EditPostText(pId, grId, pCaption, grName, grIcon);
        } else {
            EditPostWithImage(pId, grId, pImage, pCaption, grName, grIcon);
        }
    }

    private void EditPostText(String pId, String grId, String pCaption, String grName, String grIcon) {
        Intent intent = new Intent(PostDetail.this, EditPost.class);
        intent.putExtra("pId", pId);
        intent.putExtra("grId", grId);
        intent.putExtra("pImage", "noImage");
        intent.putExtra("pCaption", pCaption);
        intent.putExtra("grName", grName);
        intent.putExtra("grIcon", grIcon);
        startActivity(intent);
    }

    private void EditPostWithImage(String pId, String grId, String pImage, String pCaption, String grName, String grIcon) {
        Intent intent = new Intent(PostDetail.this, EditPost.class);
        intent.putExtra("pId", pId);
        intent.putExtra("grId", grId);
        intent.putExtra("pImage", pImage);
        intent.putExtra("pCaption", pCaption);
        intent.putExtra("grName", grName);
        intent.putExtra("grIcon", grIcon);
        startActivity(intent);

    }

    private void deletePosts(String postId, String groupId, String pImage) {
        if (pImage.equals("noImage")) {
            deletePost(postId, groupId);
        } else {
            deletePostWithImage(postId, groupId, pImage);
        }
    }

    private void deletePostWithImage(String postId, String groupId, String pImg) {
        StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImg);
        imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups")
                        .child(groupId).child("Posts");
                ref.child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(PostDetail.this, Home.class));
                        Toast.makeText(PostDetail.this, "Post Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void deletePost(String postId, String groupId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups")
                .child(groupId).child("Posts");
        ref.child(postId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(PostDetail.this, Home.class));
                Toast.makeText(PostDetail.this, "Post Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void LoadComment() {
        reference = database.getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelComments.clear();
                for (DataSnapshot gr : snapshot.getChildren()) {
                    DataSnapshot post = gr.child("Posts").child(pId);
                    DataSnapshot comment = post.child("Comment");
                    for (DataSnapshot comments : comment.getChildren()) {
                        ModelComment modelComment = comments.getValue(ModelComment.class);
                        modelComments.add(modelComment);
                        Collections.sort(modelComments, new Comparator<ModelComment>() {
                            @Override
                            public int compare(ModelComment o1, ModelComment o2) {
                                return Float.compare(Float.parseFloat(o1.getTimestamp()), Float.parseFloat(o2.getTimestamp()));
                            }
                        });
                    }
                    adapterComment = new AdapterComment(PostDetail.this, modelComments);
                    recyclerView.setAdapter(adapterComment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void postComment() {
        progress = new ProgressDialog(this);
        progress.setMessage("Uploading comment..");
        progress.show();
        String up_comment = comment.getText().toString().trim();
        if (TextUtils.isEmpty(up_comment)) {
            Toast.makeText(this, "Please add comment", Toast.LENGTH_SHORT).show();
            progress.dismiss();
            return;
        } else {
            String timestamp = String.valueOf(System.currentTimeMillis());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups")
                    .child(groupId).child("Posts").child(pId).child("Comment");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cId", timestamp);
            hashMap.put("comment", up_comment);
            hashMap.put("timestamp", timestamp);
            hashMap.put("uid", myUid);
            hashMap.put("uEmail", myEmail);
            hashMap.put("uDp", myDp);
            hashMap.put("uName", myName);
            hashMap.put("pId", pId);
            ref.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups")
                                    .child(groupId).child("Posts").child(pId);
                            ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ref1.child("pComment").setValue("" + (Integer.parseInt(pComment) + 1));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            progress.dismiss();
                            comment.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostDetail.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void LoaduserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = ref.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    myName = "" + ds.child("name").getValue();
                    myEmail = "" + ds.child("email").getValue();
                    myDp = "" + ds.child("image").getValue();
                    myUid = "" + ds.child("uid").getValue();
                    try {
                        Glide
                                .with(PostDetail.this)
                                .load(myDp)
                                .centerCrop()
                                .placeholder(R.drawable.ic_def_cover)
                                .into(mAvatar);
                    } catch (Exception e) {

                    }
                    if (ds.child("Liked").hasChild(pId)) {
                        try {
                            Glide
                                    .with(PostDetail.this)
                                    .load(R.drawable.heart)
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_def_img)
                                    .into(likeBtn);
                        } catch (Exception e) {
                        }

                    } else {
                        try {
                            Glide
                                    .with(PostDetail.this)
                                    .load(R.drawable.heart__red)
                                    .centerCrop()
                                    .placeholder(R.drawable.ic_def_img)
                                    .into(likeBtn);
                        } catch (Exception e) {
                        }


                    }

                    DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Posts");
                    ref3.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String counts = "" + snapshot.child(pId).child("Likes").getValue();
                            pLike.setText(counts);
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

    private void PostInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot gr : snapshot.getChildren()) {
                    DataSnapshot post = gr.child("Posts");
                    for (DataSnapshot postinfo : post.getChildren()) {
                        if (Objects.equals(postinfo.child("pId").getValue(), pId)) {
                            String p_Title = "" + postinfo.child("pCaption").getValue();
                            String pTimestamp = "" + postinfo.child("pTime").getValue();
                            String pImge = "" + postinfo.child("pImage").getValue();
                            hisDp = "" + postinfo.child("uDp").getValue();
                            String uid = "" + postinfo.child("uid").getValue();
                            uEmail = "" + postinfo.child("uEmail").getValue();
                            hisName = "" + postinfo.child("uName").getValue();
                            pComment = "" + postinfo.child("pComment").getValue();
                            imagePopup.initiatePopupWithGlide(pImge);
                            Calendar calendar = Calendar.getInstance(Locale.getDefault());
                            try {
                                calendar.setTimeInMillis(Long.parseLong(pTimestamp));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            String p_Time = android.text.format.DateFormat.format("dd/MM/yyyy", calendar).toString();


                            CommentCount.setText(pComment);
                            pTitle.setText(p_Title);
                            pTime.setText(pTimestamp);
                            uName.setText(hisName);
                            groupName.setText(grName);
                            username.setText("@"+UserName);
                            shareusername.setText("@"+ShareUserName);


                            try {
                                Glide
                                        .with(PostDetail.this)
                                        .load(hisDp)
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_def_img)
                                        .into(pdp);

                                if (pImge.equals("noImage")) {
                                    pImg.setVisibility(View.GONE);
                                } else {
                                    Glide
                                            .with(PostDetail.this)
                                            .load(pImge)
                                            .centerCrop()
                                            .placeholder(R.drawable.ic_def_cover)
                                            .into(pImg);
                                }
                            } catch (Exception e) {
                            }

                            if (Shared.equals("false")) {
                                shareName.setVisibility(View.GONE);
                                grShareName.setVisibility(View.GONE);
                                shareTime.setVisibility(View.GONE);
                                shareusername.setVisibility(View.GONE);
                                sdp.setVisibility(View.GONE);
                                arrow.setVisibility(View.GONE);
                                view.setVisibility(View.GONE);
                                ShareMore.setVisibility(View.GONE);
                            } else if (Shared.equals("true")) {
                                shareName.setText(ShareName);
                                grShareName.setText(ShareTo);
                                shareTime.setText(p_Time);
                                moreBtn.setVisibility(View.GONE);

                                try {
                                    Glide
                                            .with(PostDetail.this)
                                            .load(ShareDp)
                                            .centerCrop()
                                            .placeholder(R.drawable.ic_def_img)
                                            .into(sdp);

                                } catch (Exception e) {

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