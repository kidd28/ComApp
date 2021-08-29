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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        String groupTitle = model.getGroupTitle();
        String groupTime = model.getTimestamp();
        String likes = intent.getStringExtra("likes");
        String uid = intent.getStringExtra("uid");
        String uEmail = intent.getStringExtra("uEmail");
        String pId = intent.getStringExtra("pId");
        String pTime = intent.getStringExtra("pTime");
        String pTitle = intent.getStringExtra("pTitle");
        String pDesc = intent.getStringExtra("pDesc");
        String uDp = intent.getStringExtra("uDp");
        String pImage = intent.getStringExtra("pImage");
        String uName = intent.getStringExtra("uName");

        holder.groupName.setText(groupTitle);
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
                    sharePost(groupId, groupIcon, groupTitle, groupTime, likes, uid, uEmail, pId, pTime, pTitle, pDesc, uDp, uName);
                } else {
                    sharePostwImage(groupId, groupIcon, groupTitle, groupTime, likes, uid, uEmail, pId, pTime, pTitle, pDesc, uDp, pImage, uName);
                }
            }
        });
    }
    private void sharePostwImage(String groupId, String groupIcon, String groupTitle, String groupTime, String likes, String uid, String uEmail, String pId, String pTime, String pTitle, String pDesc, String uDp, String pImage, String uName) {
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("uName", uName);
        hashMap.put("uEmail", uEmail);
        hashMap.put("uDp", uDp);
        hashMap.put("pId", pId);
        hashMap.put("pTitle", pTitle);
        hashMap.put("pLike", "0");
        hashMap.put("pDescription", pDesc);
        hashMap.put("pImage", pImage);
        hashMap.put("pTime", pTime);
        hashMap.put("groupId", groupId);
        hashMap.put("groupTitle", groupTitle);
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
        reference1.child(groupId).child("Posts").child(pId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Shared Post Successfully", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, Home.class));
                    }
                });
    }
    private void sharePost(String groupId, String groupIcon, String groupTitle, String groupTime, String likes, String uid, String uEmail, String pId, String pTime, String pTitle, String pDesc, String uDp, String uName) {
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("uName", uName);
        hashMap.put("uEmail", uEmail);
        hashMap.put("uDp", uDp);
        hashMap.put("pId", pId);
        hashMap.put("pTitle", pTitle);
        hashMap.put("pLike", "0");
        hashMap.put("pDescription", pDesc);
        hashMap.put("pImage", "noImage");
        hashMap.put("pTime", pTime);
        hashMap.put("groupId", groupId);
        hashMap.put("groupTitle", groupTitle);
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
        reference1.child(groupId).child("Posts").child(pId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Shared Post Successfully", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, Home.class));
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
