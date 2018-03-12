package com.example.pccs_0007.androidfirebasechatapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by PCCS-0007 on 12-Mar-18.
 */

public class ChatUserListAdapter extends RecyclerView.Adapter<ChatUserListAdapter.MyViewHolder> {

    Context context;
    List<Users> usersList;

    public ChatUserListAdapter(Context context,List<Users> usersList){
      this.context = context;
      this.usersList = usersList;
    }
    @Override
    public ChatUserListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_user_list_layout, parent, false);

        return new ChatUserListAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ChatUserListAdapter.MyViewHolder viewHolder, int position) {

       final Users model = usersList.get(position);

        viewHolder.name.setText(model.getName());
        viewHolder.email.setText(model.getPhone());


        Log.i("tag", "online status" + model.getOnline());
        Log.i("tag", "name" + model.getName());
        Log.i("tag", "phone" + model.getPhone());
        Log.i("tag", "userid" + model.getUserid());
        Log.i("tag", "url" + model.getImgurl());


        if (model.getImgurl() != null) {
            if (model.getImgurl() != null) {
                if (model.getImgurl() != null && model.getImgurl().length() > 0) {
                    Picasso.with(context).load(model.getImgurl())
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .error(R.drawable.default_user_men_icon)
                            .into(viewHolder.imageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(context).load(model.getImgurl()).into(viewHolder.imageView);
                                }
                            });
                }
            }

        }
        viewHolder.mview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", model.getName());
                intent.putExtra("userid", model.getUserid());
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList!=null?usersList.size():0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{


        View mview;
        ImageView imageView,online;
        LinearLayout itemLayout;
        TextView name,email;
        public MyViewHolder(View itemView) {
            super(itemView);
            mview       =   itemView;
            imageView   =   mview.findViewById(R.id.pic);
            itemLayout  =   mview.findViewById(R.id.item_layout);
            name =  mview.findViewById(R.id.user_list_name);
            email =   mview.findViewById(R.id.user_list_email);
           online = mview.findViewById(R.id.online_status);

        }

    }
}
