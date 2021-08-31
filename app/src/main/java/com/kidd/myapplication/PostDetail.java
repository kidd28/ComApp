package com.kidd.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class PostDetail extends AppCompatActivity {


    String myUid, myEmail, myName, myDp, pId, hisDp, hisName, groupId, likes,pComment, grName, pUid, pImage, uEmail, groupIcon, groupTime, udp;
    String Shared, ShareTo, ShareName, ShareDp;
    ImageView pdp, pImg, arrow, sdp;
    TextView uName, pTime, pTitle, pDescription, pLike, likeBtn, commentBtn, shareBtn, groupName, moreBtn, shareName, grShareName, shareTime, CommentCount;
    View view;
    EditText comment;
    ImageButton sendbtn;
    ImageView mAvatar;

    ProgressDialog progress;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        pdp = findViewById(R.id.dp);
        pImg = findViewById(R.id.ImageV);
        uName = findViewById(R.id.name);
        pTime = findViewById(R.id.time);
        pTitle = findViewById(R.id.pTitle);
        pDescription = findViewById(R.id.pDescription);
        pLike = findViewById(R.id.pLike);
        moreBtn = findViewById(R.id.more);
        likeBtn = findViewById(R.id.likebtn);
        commentBtn = findViewById(R.id.commentbtn);
        shareBtn = findViewById(R.id.sharebtn);
        groupName = findViewById(R.id.group_Name);
        CommentCount = findViewById(R.id.commenCount);


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


        Shared = getIntent().getStringExtra("Shared");
        ShareTo = getIntent().getStringExtra("ShareTo");
        ShareName = getIntent().getStringExtra("ShareName");
        ShareDp = getIntent().getStringExtra("ShareDp");

        String uid = getIntent().getStringExtra("uid");
        String uEmail = getIntent().getStringExtra("uEmail");
        String pTime = getIntent().getStringExtra("pTime");
        String pTitle = getIntent().getStringExtra("pTitle");
        String pDesc = getIntent().getStringExtra("pDesc");
        String uDp = getIntent().getStringExtra("uDp");
        String pImage = getIntent().getStringExtra("pImage");
        String Name = getIntent().getStringExtra("uName");

        commentBtn.setVisibility(View.GONE);
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
                PostLike(likes, groupId, pId);
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
                shareWimage(likes, uid, uEmail, pId, groupId, groupIcon, grName, groupTime, pTime, pTitle, pDesc, uDp, pImage, Name);
            }
        });
    }

    private void shareWimage(String likes, String uid, String uEmail, String pId, String groupId, String groupIcon, String grName, String groupTime, String pTime, String pTitle, String pDesc, String uDp, String pImage, String name) {
        Intent intent = new Intent(this, SharePost.class);
        intent.putExtra("likes", likes);
        intent.putExtra("uid", uid);
        intent.putExtra("uEmail", uEmail);
        intent.putExtra("pId", pId);
        intent.putExtra("groupId", groupId);
        intent.putExtra("grIcon", groupIcon);
        intent.putExtra("groupTitle", grName);
        intent.putExtra("groupTime", groupTime);
        intent.putExtra("pTime", pTime);
        intent.putExtra("pTitle", pTitle);
        intent.putExtra("pDesc", pDesc);
        intent.putExtra("uDp", uDp);
        intent.putExtra("pImage", pImage);
        intent.putExtra("uName", name);
        startActivity(intent);
    }

    private void PostLike(String likes, String groupId, String postId) {
        postLike = true;
        ref = FirebaseDatabase.getInstance().getReference("Groups")
                .child(groupId).child("Posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (postLike) {
                    if (snapshot.child("Likes").hasChild(user.getUid())) {
                        ref.child("Likes").child(user.getUid()).removeValue();
                        ref.child("pLike").setValue("" + (Integer.parseInt(likes) - 1));
                        postLike = false;
                    } else {
                        ref.child("Likes").child(user.getUid()).setValue("liked");
                        ref.child("pLike").setValue("" + (Integer.parseInt(likes) + 1));
                        postLike = false;
                    }
                }
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
                }
                return false;
            }
        });
        menu.show();
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
                        if (postinfo.child("pId").getValue().equals(pId)) {

                            String p_Title = "" + postinfo.child("pTitle").getValue();
                            String pDesc = "" + postinfo.child("pDescription").getValue();
                            String pTimestamp = "" + postinfo.child("pTime").getValue();
                            String pImge = "" + postinfo.child("pImage").getValue();
                            hisDp = "" + postinfo.child("uDp").getValue();
                            String uid = "" + postinfo.child("uid").getValue();
                            uEmail = "" + postinfo.child("uEmail").getValue();
                            hisName = "" + postinfo.child("uName").getValue();
                            likes = "" + postinfo.child("pLike").getValue();
                            pComment = "" + postinfo.child("pComment").getValue();


                            Calendar calendar = Calendar.getInstance(Locale.getDefault());
                            try {
                                calendar.setTimeInMillis(Long.parseLong(pTimestamp));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            String p_Time = android.text.format.DateFormat.format("dd/MM/yyyy", calendar).toString();

                            pLike.setText(likes + " Likes");
                            CommentCount.setText(pComment + " Comments");
                            pTitle.setText(p_Title);
                            pDescription.setText(pDesc);
                            pTime.setText(p_Time);
                            uName.setText(hisName);
                            groupName.setText(grName);
                            likeBtn.setText("Like");
                            ref = FirebaseDatabase.getInstance().getReference("Groups")
                                    .child(groupId).child("Posts").child(pId).child("Likes");
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(user.getUid())) {
                                        likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_24, 0, 0, 0);
                                    } else {
                                        likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
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
                                sdp.setVisibility(View.GONE);
                                arrow.setVisibility(View.GONE);
                                view.setVisibility(View.GONE);
                            } else if (Shared.equals("true")) {
                                shareName.setText(ShareName);
                                grShareName.setText(ShareTo);
                                shareTime.setText(p_Time);

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