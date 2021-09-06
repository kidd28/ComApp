package com.kidd.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AdapterNewsFeed extends RecyclerView.Adapter<AdapterNewsFeed.MyHolder> {

    List<ModelPost> postList;
    Context context;
    Boolean likeProcess = false;
    ImagePopup imagePopup;


    public AdapterNewsFeed(Context context, List<ModelPost> postList) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.newsfeed, parent, false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        String likes = postList.get(i).getpLike();
        String uid = postList.get(i).getUid();
        String uEmail = postList.get(i).getuEmail();
        String pId = postList.get(i).getpId();
        String groupId = postList.get(i).getGroupId();
        String grIcon = postList.get(i).getGroupIcon();
        String pImage = postList.get(i).getpImage();
        String groupTitle = postList.get(i).getGroupTitle();
        String groupTime = postList.get(i).getGroupId();
        String pCaption = postList.get(i).getpCaption();
        String uDp = postList.get(i).getuDp();
        String uName = postList.get(i).getuName();
        String Shared = postList.get(i).getShared();
        String ShareTo = postList.get(i).getShareTo();
        String ShareName = postList.get(i).getShareName();
        String ShareDp = postList.get(i).getShareDp();
        String ShareTime = postList.get(i).getShareTime();
        String ShareEmail = postList.get(i).getShareEmail();
        String pComment = postList.get(i).getpComment();
        String pTime = postList.get(i).getpTime();
        String ShareUid = postList.get(i).getShareUid();

        holder.uName.setText(postList.get(i).getuName());
        try {
            Glide
                    .with(context)
                    .load(postList.get(i).getuDp())
                    .centerCrop()
                    .placeholder(R.drawable.ic_def_img)
                    .into(holder.pdp);
        } catch (Exception e) {
        }


        holder.pCaption.setText(postList.get(i).getpCaption());
        holder.pTime.setText(postList.get(i).getpTime());
        holder.groupName.setText(postList.get(i).getGroupTitle());
        holder.pLike.setText(likes + " Likes");
        holder.likeBtn.setText("Like");
        setLikes(holder, pId, groupId);
        holder.CommentCount.setText(pComment + " Comments");


        if (pImage.equals("noImage")) {
            holder.pImg.setVisibility(View.GONE);
        } else {

            Glide
                    .with(context)
                    .load(pImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_def_cover)
                    .into(holder.pImg);
        }

        if (Shared.equals("false")) {
            holder.shareName.setVisibility(View.GONE);
            holder.grShareName.setVisibility(View.GONE);
            holder.shareTime.setVisibility(View.GONE);
            holder.sdp.setVisibility(View.GONE);
            holder.arrow.setVisibility(View.GONE);
            holder.view.setVisibility(View.GONE);
            holder.ShareMore.setVisibility(View.GONE);
        } else if (Shared.equals("true")) {
            holder.grShareName.setText(ShareTo);
            holder.shareTime.setText(ShareTime);
            holder.shareTime.setText(ShareTime);
            holder.moreBtn.setVisibility(View.GONE);
            holder.shareName.setText(ShareName);
            Glide
                    .with(context)
                    .load(ShareDp)
                    .centerCrop()
                    .placeholder(R.drawable.ic_def_img)
                    .into(holder.sdp);

        }
        holder.ShareMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                SharemoreOption(ShareUid,holder.ShareMore, uid, user1.getUid(), groupId, pId, pImage);
            }
        });
        holder.pImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePopup = new ImagePopup(context);
                imagePopup.setImageOnClickClose(true);
                imagePopup.setWindowHeight(1000);
                imagePopup.setWindowWidth(1000);
                imagePopup.setBackgroundColor(Color.TRANSPARENT);
                imagePopup.setHideCloseIcon(true);
                imagePopup.initiatePopupWithGlide(postList.get(i).getpImage());
                imagePopup.viewPopup();
            }
        });
        holder.pCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetail.class);
                intent.putExtra("pId", pId);
                intent.putExtra("groupID", groupId);
                intent.putExtra("likes", likes);
                intent.putExtra("pComment", pComment);
                intent.putExtra("grName", postList.get(i).getGroupTitle());
                intent.putExtra("uid", uid);
                intent.putExtra("grIcon", grIcon);
                intent.putExtra("pImage", pImage);
                intent.putExtra("uEmail", uEmail);
                intent.putExtra("grIcon", grIcon);
                intent.putExtra("groupTitle", groupTitle);
                intent.putExtra("groupTime", groupTime);
                intent.putExtra("pTime", pTime);
                intent.putExtra("pCaption", pCaption);
                intent.putExtra("pComment", pComment);
                intent.putExtra("uDp", uDp);
                intent.putExtra("pImage", pImage);
                intent.putExtra("uName", uName);
                intent.putExtra("Shared", Shared);
                intent.putExtra("ShareTo", ShareTo);
                intent.putExtra("ShareName", ShareName);
                intent.putExtra("ShareDp", ShareDp);
                context.startActivity(intent);
            }
        });
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();
                moreOption(ShareUid, Shared, holder.moreBtn, uid, user2.getUid(), groupId, pId, pImage);
            }
        });
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostLike(likes, groupId, pId);
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetail.class);
                intent.putExtra("pId", pId);
                intent.putExtra("groupID", groupId);
                intent.putExtra("likes", likes);
                intent.putExtra("pComment", pComment);
                intent.putExtra("grName", postList.get(i).getGroupTitle());
                intent.putExtra("uid", uid);
                intent.putExtra("pImage", pImage);
                intent.putExtra("Shared", Shared);
                intent.putExtra("ShareTo", ShareTo);
                intent.putExtra("ShareName", ShareName);
                intent.putExtra("ShareDp", ShareDp);
                context.startActivity(intent);
            }
        });
        holder.uName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OtherProfile.class);
                intent.putExtra("email", uEmail);
                intent.putExtra("name", postList.get(i).getuName());
                intent.putExtra("uid", postList.get(i).getUid());
                context.startActivity(intent);
            }
        });
        holder.groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupUi.class);
                intent.putExtra("grName", groupTitle);
                intent.putExtra("grIcon", grIcon);
                intent.putExtra("grId", groupId);
                intent.putExtra("grTime", groupTime);
                context.startActivity(intent);
            }
        });
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWimage(likes, uid, uEmail, pId, groupId, grIcon, groupTitle, groupTime, pTime, pCaption, uDp, pImage, uName);
            }
        });
    }
    private void SharemoreOption(String ShareUid,TextView moreBtn, String uid, String uid1, String groupId, String pId, String pImage) {
        PopupMenu menu = new PopupMenu(context, moreBtn, Gravity.END);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Posts");
        builder.setMessage("Are you sure you want to Delete this Post?");
        FirebaseUser user3 = FirebaseAuth.getInstance().getCurrentUser();
        if (ShareUid.equals(user3.getUid())) {
            menu.getMenu().add(Menu.NONE, 0, 0, "Delete");
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

    private void shareWimage(String likes, String uid, String uEmail, String pId, String groupId, String grIcon, String groupTitle, String groupTime, String pTime, String pCaption, String uDp, String pImage, String uName) {
        Intent intent = new Intent(context, SharePost.class);
        intent.putExtra("likes", likes);
        intent.putExtra("uid", uid);
        intent.putExtra("uEmail", uEmail);
        intent.putExtra("pId", pId);
        intent.putExtra("groupId", groupId);
        intent.putExtra("grIcon", grIcon);
        intent.putExtra("groupTitle", groupTitle);
        intent.putExtra("groupTime", groupTime);
        intent.putExtra("pTime", pTime);
        intent.putExtra("pCaption", pCaption);
        intent.putExtra("uDp", uDp);
        intent.putExtra("pImage", pImage);
        intent.putExtra("uName", uName);
        context.startActivity(intent);
    }

    private void PostLike(String likes, String groupId, String pId) {
        likeProcess = true;
        FirebaseUser user4 = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Posts").child(pId);
        ref3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (likeProcess) {
                    if (snapshot.child("Likes").hasChild(user4.getUid())) {
                        ref3.child("Likes").child(user4.getUid()).removeValue();
                        ref3.child("pLike").setValue("" + (Integer.parseInt(likes) - 1));
                        likeProcess = false;
                    } else {
                        ref3.child("Likes").child(user4.getUid()).setValue("liked");
                        ref3.child("pLike").setValue("" + (Integer.parseInt(likes) + 1));
                        likeProcess = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void moreOption(String ShareUid, String Shared, TextView morebtn, String uid, String myUid, String grId, String pId, String pImage) {
        PopupMenu menu = new PopupMenu(context, morebtn, Gravity.END);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Posts");
        builder.setMessage("Are you sure you want to Delete this Post?");
        FirebaseUser user6 = FirebaseAuth.getInstance().getCurrentUser();

            if (uid.equals(user6.getUid())) {
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
                            deletePosts(pId, grId, pImage);
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

    private void deletePosts(String pId, String grId, @NotNull String pImg) {
        if (pImg.equals("noImage")) {
            deletePost(pId, grId);
        } else {
            deletePostWithImage(pId, grId, pImg);
        }
    }

    private void deletePost(String pId, String grId) {
        DatabaseReference ref4 = FirebaseDatabase.getInstance().getReference("Groups")
                .child(grId).child("Posts");
        ref4.child(pId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deletePostWithImage(String pId, String grId, String pImg) {
        StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImg);
        imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DatabaseReference ref5 = FirebaseDatabase.getInstance().getReference("Groups")
                        .child(grId).child("Posts");
                ref5.child(pId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setLikes(MyHolder holder, String pId, String grId) {
        FirebaseUser user5 = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref6 = FirebaseDatabase.getInstance().getReference("Groups").child(grId).child("Posts").child(pId);
        ref6.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Likes").hasChild(user5.getUid())) {
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_thumb_up_24, 0, 0, 0);
                } else {
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0, 0, 0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView pdp, pImg, sdp, arrow;
        TextView ShareMore, CommentCount, shareName, grShareName, uName, pTime, pCaption, pLike, likeBtn, commentBtn, shareBtn, groupName, moreBtn, shareTime;
        View view;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pdp = itemView.findViewById(R.id.dp);
            pImg = itemView.findViewById(R.id.ImageV);
            uName = itemView.findViewById(R.id.name);
            pTime = itemView.findViewById(R.id.time);
            pCaption = itemView.findViewById(R.id.pCaption);
            pLike = itemView.findViewById(R.id.pLike);
            moreBtn = itemView.findViewById(R.id.more);
            likeBtn = itemView.findViewById(R.id.likebtn);
            commentBtn = itemView.findViewById(R.id.commentbtn);
            shareBtn = itemView.findViewById(R.id.sharebtn);
            groupName = itemView.findViewById(R.id.group_Name);
            shareName = itemView.findViewById(R.id.shareName);
            grShareName = itemView.findViewById(R.id.grShareName);
            shareTime = itemView.findViewById(R.id.shareTime);
            sdp = itemView.findViewById(R.id.sdp);
            arrow = itemView.findViewById(R.id.arrow);
            view = itemView.findViewById(R.id.view);
            CommentCount = itemView.findViewById(R.id.commenCount);
            ShareMore = itemView.findViewById(R.id.ShareMore);
        }
    }
}
