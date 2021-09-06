package com.kidd.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.Myholder>  {

    Context context;
    List<ModelComment> commentList;
    public AdapterComment(Context context, List<ModelComment>commentList){
        this.context = context;
        this.commentList = commentList;
    }


    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.comment_list, parent, false);

        return new AdapterComment.Myholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {

        String cName = commentList.get(position).getuName();
        String cTime = commentList.get(position).getTimestamp();
        String cDp = commentList.get(position).getuDp();
        String uComment = commentList.get(position).getComment();

        Calendar calendar =Calendar.getInstance(Locale.getDefault());
        try {
            calendar.setTimeInMillis(Long.parseLong(cTime));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        String pTime = android.text.format.DateFormat.format("dd/MM/yyyy", calendar).toString();
        holder.name.setText(cName);
        holder.time.setText(pTime);
        holder.comment.setText(uComment);

        Glide
                .with(context)
                .load(cDp)
                .centerCrop()
                .placeholder(R.drawable.ic_def_img)
                .into(holder.dp);

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class Myholder extends RecyclerView.ViewHolder {
        ImageView dp;
        TextView name, time,comment;

        public Myholder(@NonNull View itemView) {
            super(itemView);

            dp = itemView.findViewById(R.id.cdp);
            name = itemView.findViewById(R.id.cname);
            time = itemView.findViewById(R.id.ctime);
            comment = itemView.findViewById(R.id.ucomment);

        }
    }
}
