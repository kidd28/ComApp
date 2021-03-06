package com.kidd.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterJoinGroup extends RecyclerView.Adapter<AdapterJoinGroup.HolderGroupList> {
    private Context context;
    private ArrayList<ModelGroup> groupList;

    public AdapterJoinGroup(Context context , ArrayList<ModelGroup> groupList){
        this.context= context;
        this.groupList=groupList;
    }
    @NonNull
    @Override
    public HolderGroupList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list, parent, false);
        return new HolderGroupList(v);
    }
    @Override
    public void onBindViewHolder(@NonNull HolderGroupList holder, int position) {

        ModelGroup model = groupList.get(position);
        String groupId = model.getGroupId();
        String groupIcon = model.getGroupIcon();
        String groupTitle = model.getGroupTitle();
        String groupTime= model.getTimestamp();

        holder.groupName.setText(groupTitle);

            Glide
                    .with(context)
                    .load(groupIcon)
                    .centerCrop()
                    .placeholder(R.drawable.ic_def_img)
                    .into(holder.groupIcon);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, GroupPrev.class);
                intent.putExtra("grName", groupTitle);
                intent.putExtra("grIcon", groupIcon);
                intent.putExtra("grId", groupId);
                intent.putExtra("grTime", groupTime);
                context.startActivity(intent);
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

            groupIcon=itemView.findViewById(R.id.groupIcon);
            groupName = itemView.findViewById(R.id.mgroupName);

        }
    }
}
