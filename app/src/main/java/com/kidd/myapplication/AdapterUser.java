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

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder> {

    Context context;
    List<ModelUser> userList;


    public AdapterUser(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent, false);
    return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String userImage = userList.get(position).getImage();
        String userName = userList.get(position).getName();
        String userEmail = userList.get(position).getEmail();

        holder.P_name.setText(userName);
        holder.P_mail.setText(userEmail);


            Glide
                    .with(context)
                    .load(userImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_def_img)
                    .into(holder.P_avatar);



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, OtherProfile.class);
                intent.putExtra("email", userEmail);
                intent.putExtra("name", userName);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView P_avatar;
        TextView P_name,P_mail;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            P_avatar = itemView.findViewById(R.id.Pavatar);
            P_name=itemView.findViewById(R.id.Pname);
            P_mail=itemView.findViewById(R.id.Pemail);

        }
    }
}
