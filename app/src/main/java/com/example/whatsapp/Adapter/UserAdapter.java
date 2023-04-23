package com.example.whatsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp.MessageActivity;
import com.example.whatsapp.R;
import com.example.whatsapp.model.Users;

import java.util.List;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<Users> mUsers;

    private boolean isChat;


    public UserAdapter(Context context, List<Users> mUsers,boolean isChat) {
        this.context = context;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
       return  new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // gan gia tri cho recyclerview
        Users  users = mUsers.get(position);
        holder.username.setText(users.getUsername());
       if (users.getImageURL().equals("default")){
           holder.imageView.setImageResource(R.mipmap.ic_launcher);

       }
       else {
           Glide.with(context).load(users.getImageURL()).into(holder.imageView);
       }
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

//                put du lieu sang trang MessageActivity
               Intent  i = new Intent(context, MessageActivity.class);
               i.putExtra("userid",users.getId());
               context.startActivity(i);
           }
       });

        Log.d("TAG",users.getStatus());
        // Status check
        if (isChat){

            if(users.getStatus().equals("online")){
                holder.imageViewON.setVisibility(View.VISIBLE);
                holder.imageViewOFF.setVisibility(View.GONE);
            }else{

                holder.imageViewON.setVisibility(View.GONE);
                holder.imageViewOFF.setVisibility(View.VISIBLE);
            }
        }else{

            holder.imageViewON.setVisibility(View.GONE);
            holder.imageViewOFF.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView imageView;
        public ImageView imageViewON;
        private ImageView imageViewOFF;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.username = itemView.findViewById(R.id.textView30);
            this.imageView = itemView.findViewById(R.id.imageView);
            this.imageViewON = itemView.findViewById(R.id.statusimageON);
            this.imageViewOFF = itemView.findViewById(R.id.statusimageOFF);

        }
    }
}
