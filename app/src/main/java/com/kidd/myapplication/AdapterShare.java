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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AdapterShare extends RecyclerView.Adapter<AdapterShare.HolderGroupList> {
    private Context context;
    private ArrayList<ModelGroup> groupList;
    String myName, myDp;

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
        String groupIcon = model.getGroupIcon();
        String shareTogroupTitle = model.getGroupTitle();
        String groupTime = model.getTimestamp();
        String ShareGroupId = model.getGroupId();
        String ShareGroupIcon = model.getGroupIcon();
        String grIcon= intent.getStringExtra("grIcon");
        String groupId =intent.getStringExtra("groupId");
        String uid = intent.getStringExtra("uid");
        String uEmail = intent.getStringExtra("uEmail");
        String pId = intent.getStringExtra("pId");
        String pTime = intent.getStringExtra("pTime");
        String pCaption = intent.getStringExtra("pCaption");
        String grTitle = intent.getStringExtra("groupTitle");
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Query query = ref.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    myName = "" + ds.child("name").getValue();
                    myDp = "" + ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pImage.equals("noImage")) {
                    sharePost(ShareGroupIcon,ShareGroupId,myName, myDp, groupId, grIcon, grTitle, shareTogroupTitle, groupTime, uid, uEmail, pId, pTime, pCaption, uDp, uName);
                } else {
                    sharePostwImage(ShareGroupIcon,ShareGroupId,myName, myDp, groupId, grIcon, grTitle, shareTogroupTitle, groupTime, uid, uEmail, pId, pTime, pCaption, uDp, pImage, uName);
                }
            }
        });
    }

    private void sharePostwImage(String ShareGroupIcon,String ShareGroupId,String name, String Dp, String groupId, String grIcon, String groupTitle, String shareTogroupTitle, String groupTime, String uid, String uEmail, String pId, String pTime, String pCaption, String uDp, String pImage, String uName) {
        String postId = String.valueOf(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        try {
            calendar.setTimeInMillis(Long.parseLong(postId));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String ShareTime = android.text.format.DateFormat.format("dd/MM/yyyy", calendar).toString();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("uName", uName);
        hashMap.put("uEmail", uEmail);
        hashMap.put("uDp", uDp);
        hashMap.put("pId", postId);
        hashMap.put("OrigPid", pId);
        hashMap.put("pCaption", pCaption);
        hashMap.put("pComment", "0");
        hashMap.put("pImage", pImage);
        hashMap.put("pTime", pTime);
        hashMap.put("groupId", groupId);
        hashMap.put("groupTitle", groupTitle);
        hashMap.put("groupIcon", grIcon);
        hashMap.put("ShareTo", shareTogroupTitle);
        hashMap.put("Shared", "true");
        hashMap.put("ShareName", name);
        hashMap.put("ShareDp", Dp);
        hashMap.put("ShareTime", ShareTime);
        hashMap.put("ShareEmail", user1.getEmail());
        hashMap.put("ShareUid", user1.getUid());
        hashMap.put("ShareGroupId", ShareGroupId);
        hashMap.put("ShareGroupIcon", ShareGroupIcon);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.child(postId).child("Likes").setValue("0");
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
        reference1.child(ShareGroupId).child("Posts").child(postId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(context, "Share Post Successfully", Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context, Home.class));
                    }
                });


    }

    private void sharePost(String ShareGroupIcon, String ShareGroupId,String name, String Dp, String groupId, String grIcon, String groupTitle, String shareTogroupTitle, String groupTime, String uid, String uEmail, String pId, String pTime, String pCaption, String uDp, String uName) {
        String postId = String.valueOf(System.currentTimeMillis());

        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        try {
            calendar.setTimeInMillis(Long.parseLong(postId));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String ShareTime = android.text.format.DateFormat.format("dd/MM/yyyy", calendar).toString();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("uName", uName);
        hashMap.put("uEmail", uEmail);
        hashMap.put("uDp", uDp);
        hashMap.put("pId", postId);
        hashMap.put("OrigPid", pId);
        hashMap.put("pComment", "0");
        hashMap.put("pImage", "noImage");
        hashMap.put("pTime", pTime);
        hashMap.put("pCaption", pCaption);
        hashMap.put("groupId", groupId);
        hashMap.put("groupTitle", groupTitle);
        hashMap.put("groupIcon", grIcon);
        hashMap.put("ShareTo", shareTogroupTitle);
        hashMap.put("Shared", "true");
        hashMap.put("ShareName", name);
        hashMap.put("ShareDp", Dp);
        hashMap.put("ShareTime", ShareTime);
        hashMap.put("ShareEmail", user1.getEmail());
        hashMap.put("ShareUid", user1.getUid());
        hashMap.put("ShareGroupId", ShareGroupId);
        hashMap.put("ShareGroupIcon", ShareGroupIcon);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.child(postId).child("Likes").setValue("0");
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Groups");
        reference1.child(ShareGroupId).child("Posts").child(postId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Share Post Successfully", Toast.LENGTH_SHORT).show();
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
