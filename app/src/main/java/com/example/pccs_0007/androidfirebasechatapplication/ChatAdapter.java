package com.example.pccs_0007.androidfirebasechatapplication;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


/**
 * Created by user on 07-05-2017.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<ChatMessage> chatMessageList;
    String senderId;
    private SparseBooleanArray selectedItems;
    public static int itemPositionInAdapter=0;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView messageLeft,messageRight,messageLeftTime,messageRightTime;
        LinearLayout leftLayout,rightLayout;
        LinearLayout dateAndTimeLayout;
        TextView dateAndTimeTextview;


        public MyViewHolder(View view) {
            super(view);
            leftLayout      =   view.findViewById(R.id.message_layout_left);
            rightLayout     =   view.findViewById(R.id.message_layout_right);
            messageLeft     =   view.findViewById(R.id.message_left);
            messageLeftTime =   view.findViewById(R.id.send_time_left);
            messageRight    =   view.findViewById(R.id.message_right);
            messageRightTime =  view.findViewById(R.id.send_time_right);


            dateAndTimeLayout   =  itemView.findViewById(R.id.date_and_time_chat_layout);
            dateAndTimeTextview = itemView.findViewById(R.id.date_and_time_bubble);
            dateAndTimeLayout.setEnabled(false);
            dateAndTimeLayout.setClickable(false);

        }
    }


    public ChatAdapter(List<ChatMessage> chatMessageList, String senderId) {
        this.chatMessageList = chatMessageList;
        this.senderId = senderId;
        selectedItems       =   new SparseBooleanArray();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        itemPositionInAdapter = position;
        ChatMessage chatMessage =chatMessageList.get(position);
        if(chatMessage.getSenderId().toString().equalsIgnoreCase(senderId))
        {
            // visible right
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.messageRight.setText(chatMessage.getMessage());
            holder.messageRightTime.setText(chatMessage.getSent_time().split(" ")[1] + chatMessage.getSent_time().split(" ")[2]);
        }
         else
        {
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.messageLeft.setText(chatMessage.getMessage());
            holder.messageLeftTime.setText(chatMessage.getSent_time().split(" ")[1] + chatMessage.getSent_time().split(" ")[2]);
        }

        holder.itemView.setActivated(selectedItems.get(position, false));

        showDateAndTimeLayout(holder,chatMessageList,position);



    }

    @Override
    public int getItemCount() {
        return (chatMessageList!=null)?chatMessageList.size():0;
    }





    private void showDateAndTimeLayout(MyViewHolder holder, List<ChatMessage> chatMessageEntityList, int position)
    {
        if(chatMessageEntityList!=null && chatMessageEntityList.size()>0)
        {
            if(position==0)
            {


                String prevTime = chatMessageEntityList.get(position).getSent_time();
                String prevDate = prevTime.split(" ")[0];
                holder.dateAndTimeLayout.setVisibility(View.VISIBLE);
                holder.dateAndTimeLayout.setActivated(false);
                if(prevDate.equalsIgnoreCase(DateTimeUtilities.getCurrentOnlyDate()))
                {
                    holder.dateAndTimeTextview.setText("TODAY");
                }

                else {
                    holder.dateAndTimeTextview.setText(DateTimeUtilities.getConvertedTime(prevDate));
                }
            }
            if(position>0)
            {
                String prevTime = chatMessageEntityList.get(position-1).getSent_time();
                String prevDate =prevTime.split(" ")[0];

                String currentTime = chatMessageEntityList.get(position).getSent_time();
                String currentDate  = currentTime.split(" ")[0];
                if(!prevDate.toString().equalsIgnoreCase(currentDate))
                {
                    holder.dateAndTimeLayout.setVisibility(View.VISIBLE);
                    if(currentDate.equalsIgnoreCase(DateTimeUtilities.getCurrentOnlyDate()))
                    {
                        holder.dateAndTimeTextview.setText("TODAY");
                    }

                    else {
                        holder.dateAndTimeTextview.setText(DateTimeUtilities.getConvertedTime(currentDate));
                    }
                }
                else
                {
                    holder.dateAndTimeLayout.setVisibility(View.GONE);
                }
            }

        }
    }



}
