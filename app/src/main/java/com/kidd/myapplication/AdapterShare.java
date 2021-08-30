package com.kidd.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterShare extends RecyclerView.Adapter<AdapterShare.HolderGroupList> {
    private Context context;
    private ArrayList<ModelGroup> groupList;


    public AdapterShare(Context context, ArrayList<ModelGroup> groupList) {
        this.context = context;
        this.groupList = groupList;

    }

    @NonNull
    @Override
    public HolderGroupList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list, parent, false);


        return new AdapterShare.HolderGroupList(v);
    }
    @Override
    public void onBindViewHolder(@NonNull HolderGroupList holder, int position) {
        Intent intent = ((Activity) context).getIntent();
        ModelGroup model = groupList.get(position);
        String groupId = model.getGroupId();
        String groupIcon = model.getGroupIcon();
        String shareTogroupTitle = model.getGroupTitle();
        String groupTime = model.getTimestamp();


        String likes = intent.getStringExtra("likes");
        String uid = intent.getStringExtra("uid");
        String uEmail = intent.getStringExtra("uEmail");
        String pId = intent.getStringExtra("pId");
        String pTime = intent.getStringExtra("pTime");
        String pTitle = intent.getStringExtra("pTitle");
        String grTitle = intent.getStringExtra("groupTitle");
        String pDesc = intent.getStringExtra("pDesc");
        String uDp = intent.getStringExtra("uDp");
        String pImage = intent.getStringExtra("pImage");
        String uName = intent.getStringExtra("uName");

        holder.groupName.setText(shareTogroupTitle);
        if (context != null) {
            Glide
                    .with(context)
                    .load(groupIcon)
                    .centerCrop()
                    .placeholder(R.drawable.ic_def_img)
                    .into(holder.groupIcon);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pImage.equals("noImage")) {
                    sharePost(groupId, groupIcon, grTitle,shareTogroupTitle, groupTime, likes, uid, uEmail, pId, pTime, pTitle, pDesc, uDp, uName);
                } else {
                    sharePostwImage(groupId, groupIcon, grTitle,shareTogroupTitle, groupTime, likes, uid, uEmail, pId, pTime, pTitle, pDesc, uDp, pImage, uName);
                }
            }
        });
    }
    private void sharePostwImage(String groupId, String groupIcon, String groupTitle,String shareTogroupTitle, String groupTime, String likes, String uid, String uEmail, String pId, String pTime, String pTitle, String pDesc, String uDp, String pImage, String uName) {
        String postId = String.valueOf(System.currentTimeMillis());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = ref.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String myName = "" + ds.child("name").getValue();
                    String myDp = "" + ds.child("image").getValue();

                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("uid", user.getUid());
                    hashMap.put("uName", uName);
                    hashMap.put("uEmail", uEmail);
                    hashMap.put("uDp", uDp);
                    hashMap.put("pId", postId);
                    hashMap.put("pTitle", pTitle);
                    hashMap.put("pLike", "0");
                    hashMap.put("pDescription", pDesc);
                    hashMap.put("pImage", pImage);
                    hashMap.put("pTime", pTime);
                    hashMap.put("groupId", groupId);
                    hashMap.put("groupTitle", groupTitle);
                    hashMap.put("ShareTo", shareTogroupTitle);
                    hashMap.put("Shared","true");
                    hashMap.put("ShareName",myName);
                    hashMap.put("ShareDp",myDp);

                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                    reference1.child(groupId).child("Posts").child(postId).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Shared Post Successfully", Toast.LENGTH_SHORT).show();
                                    context.startActivity(new Intent(context, Home.class));
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



    }
    private void sharePost(String groupId, String groupIcon, String groupTitle,String shareTogroupTitle, String groupTime, String likes, String uid, String uEmail, String pId, String pTime, String pTitle, String pDesc, String uDp, String uName) {
        String postId = String.valueOf(System.currentTimeMillis());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = ref.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String myName = "" + ds.child("name").getValue();
                    String myDp = "" + ds.child("image").getValue();

                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("uid", user.getUid());
                    hashMap.put("uName", uName);
                    hashMap.put("uEmail", uEmail);
                    hashMap.put("uDp", uDp);
                    hashMap.put("pId", postId);
                    hashMap.put("pTitle", pTitle);
                    hashMap.put("pLike", "0");
                    hashMap.put("pDescription", pDesc);
                    hashMap.put("pImage", "noImage");
                    hashMap.put("pTime", pTime);
                    hashMap.put("groupId", groupId);
                    hashMap.put("groupTitle", groupTitle);
                    hashMap.put("ShareTo", shareTogroupTitle);
                    hashMap.put("Shared","true");
                    hashMap.put("ShareName",myName);
                    hashMap.put("ShareDp",myDp);

                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
                    reference1.child(groupId).child("Posts").child(postId).setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context, "Shared Post Successfully", Toast.LENGTH_SHORT).show();
                                    context.startActivity(new Intent(context, Home.class));
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }


    class HolderGroupList extends RecyclerView.ViewHolder {
        private ImageView groupIcon;
        private TextView groupName;

        public HolderGroupList(@NonNull View itemView) {
            super(itemView);
            groupIcon = itemView.findViewById(R.id.groupIcon);
            groupName = itemView.findViewById(R.id.mgroupName);
        }
    }
}
