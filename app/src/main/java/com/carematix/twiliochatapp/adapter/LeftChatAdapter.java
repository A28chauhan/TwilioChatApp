package com.carematix.twiliochatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.carematix.twiliochatapp.ChatActivity;
import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.twilio.MessageItem;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.Member;
import com.twilio.chat.Members;
import com.twilio.chat.Message;
import com.twilio.chat.Messages;

import java.util.Calendar;
import java.util.List;

public class LeftChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_LEFT_ITEM = 0;
    private final int VIEW_TYPE_RIGHT_ITEM = 1;
    private final int VIEW_TYPE_CENTER_ITEM = 2;

    Context context;
    List<Message> messageItemList;

    LinearLayoutManager linearLayoutManager;
    Channel channel;

    String attendeeId="";
    public LeftChatAdapter(Context mContext, List<Message> arrayList, LinearLayoutManager linearLayoutManager, RecyclerView recyclerView,Channel mChannel,String attendeeId ){
        this.context=mContext;
        this.messageItemList = arrayList;
        this.linearLayoutManager = linearLayoutManager;
        this.channel =mChannel;
        this.attendeeId =attendeeId;
    }

    @Override
    public int getItemCount() {
        return messageItemList.size() == 0 ? 0 : messageItemList.size();
    }


    public void addItem(List<Message> message){
        try {
            this.messageItemList = message;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //public void addItem()

    public void addItem(Message message){
        try {
            this.messageItemList.add(message);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void clear(){
        messageItemList.clear();
        notifyDataSetChanged();
    }



    String currentDate="";
    @Override
    public int getItemViewType(int position) {
        Message messageItem =messageItemList.get(position);
        PrefManager prefManager=new PrefManager(context);
        String programUser = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
        if(!messageItem.getAuthor().contains(programUser)){
            return  VIEW_TYPE_LEFT_ITEM;
        }else {
            return VIEW_TYPE_RIGHT_ITEM;
        }

    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        Logs.d("onViewRecycled "," onViewRecycled index:"+holder.getAdapterPosition());
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        // holder.textView.setT
        Message messageItem =messageItemList.get(position);
        long index = messageItem.getMessageIndex();
        Logs.d("chat adpater 3"," messages index:"+index);

        if(viewHolder instanceof RightViewHolder){
            RightViewHolder userViewHolder = (RightViewHolder) viewHolder;

            userViewHolder.textView.setText(" " + messageItem.getMessageBody().toString());
            userViewHolder.textTime.setText(Utils.setTime(messageItem.getDateCreatedAsDate()));

            updateMemberMessageReadStatus(userViewHolder,messageItem);


        }else if(viewHolder instanceof LeftViewHolder){
            LeftViewHolder userViewHolder = (LeftViewHolder) viewHolder;

            userViewHolder.textView.setText(" " + messageItem.getMessageBody().toString());
            userViewHolder.textTime.setText(Utils.setTime(messageItem.getDateCreatedAsDate()));

        }else if(viewHolder instanceof CenterViewHolder){

            CenterViewHolder userViewHolder = (CenterViewHolder) viewHolder;
            try {
                //userViewHolder.linearLayoutLeft.setVisibility(View.VISIBLE);
                //userViewHolder.linearLayoutRight.setVisibility(View.VISIBLE);
                PrefManager prefManager=new PrefManager(context);
                String programUserID = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
                if(!messageItem.getAuthor().equals(programUserID)){
                    userViewHolder.linearLayoutLeft.setVisibility(View.VISIBLE);
                    userViewHolder.textLeftTime.setText(" " + messageItem.getMessageBody().toString());
                    userViewHolder.textLeftTime.setText(Utils.setTime(messageItem.getDateCreatedAsDate()));
                }else{
                    userViewHolder.linearLayoutRight.setVisibility(View.VISIBLE);
                    userViewHolder.textRightTime.setText(" " + messageItem.getMessageBody().toString());
                    userViewHolder.textRightTime.setText(Utils.setTime(messageItem.getDateCreatedAsDate()));
                    updateMemberMessageReadStatus(userViewHolder,messageItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Calendar calendar = Calendar.getInstance();
                String todayDate = Utils.getDateTime(calendar.getTimeInMillis());
                currentDate = Utils.setDateTime(messageItem.getDateCreatedAsDate());
                if(todayDate.compareTo(currentDate) == 0){
                    userViewHolder.textTimeFull.setText("Today");
                    userViewHolder.textTimeFull.setVisibility(View.VISIBLE);
                }else if(todayDate.compareTo(currentDate) > 0){
                    int check = Integer.parseInt(Utils.getDateC1(messageItem.getDateCreatedAsDate()));
                    int today = Integer.parseInt(Utils.getDateC(calendar.getTimeInMillis()));
                    if(check == (today-1)){
                        userViewHolder.textTimeFull.setText("Yesterday");
                        userViewHolder.textTimeFull.setVisibility(View.VISIBLE);
                    }else{
                        userViewHolder.textTimeFull.setText(currentDate);
                        userViewHolder.textTimeFull.setVisibility(View.VISIBLE);
                    }
                }else{
                    userViewHolder.textTimeFull.setText(currentDate);
                    userViewHolder.textTimeFull.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            throw new RuntimeException("Unknown view type in onBindViewHolder");
        }

    }




    public void updateMemberMessageReadStatus(RightViewHolder userViewHolder,Message messageItem){

        try {
            Member member =channel.getMembers().getMember(attendeeId);
            if(member != null)
            if(member.getLastConsumedMessageIndex() == null){
                userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_single_tick_24, 0);
            }else{
                if(member.getLastConsumedMessageIndex() >= messageItem.getMessageIndex()){
                    userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
                }else{
                    userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_single_tick_24, 0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateMemberMessageReadStatus(CenterViewHolder userViewHolder,Message messageItem){

        try {
            Member member =channel.getMembers().getMember(attendeeId);
            if(member.getLastConsumedMessageIndex() == null){
                userViewHolder.textRightTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_single_tick_24, 0);
            }else{
                if(member.getLastConsumedMessageIndex() >= messageItem.getMessageIndex()){
                    userViewHolder.textRightTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
                }else{
                    userViewHolder.textRightTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_single_tick_24, 0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        if(viewType == VIEW_TYPE_LEFT_ITEM){
            itemView  = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.left_chat_item, parent, false);
            return new LeftViewHolder(itemView);
        }else if(viewType == VIEW_TYPE_RIGHT_ITEM){
            itemView  = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.right_chat_item, parent, false);
            return new RightViewHolder(itemView);
        }else if(viewType == VIEW_TYPE_CENTER_ITEM){
            itemView  = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.center_chat_item, parent, false);
            return new CenterViewHolder(itemView);
        }
        return null;
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        public TextView textView,textTime,textTimeFull;
        public RightViewHolder(View view){
            super(view);
            textView=(TextView)view.findViewById(R.id.chat_text_right);
            textTime=(TextView)view.findViewById(R.id.text_time_right);
            textTimeFull=(TextView)view.findViewById(R.id.textfullDate);

        }

        public TextView getTextView() {
            return textView;
        }
    }

    public class LeftViewHolder extends RecyclerView.ViewHolder{
        public TextView textView,textTime,textTimeFull;
        public LeftViewHolder(View view){
            super(view);
            textView=(TextView)view.findViewById(R.id.chat_text_left);
            textTime=(TextView)view.findViewById(R.id.text_time_left);
            textTimeFull=(TextView)view.findViewById(R.id.textfullDate);
        }

        public TextView getTextView() {
            return textView;
        }
    }


    public class CenterViewHolder extends RecyclerView.ViewHolder{
        public TextView textTimeFull,textLeftView,textLeftTime,textRightView,textRightTime;
        LinearLayout linearLayoutLeft,linearLayoutRight;
        public CenterViewHolder(View view){
            super(view);
            textTimeFull=(TextView)view.findViewById(R.id.textfullDate);
            textLeftView=(TextView)view.findViewById(R.id.chat_text_left);
            textLeftTime=(TextView)view.findViewById(R.id.text_time_left);
            textRightView=(TextView)view.findViewById(R.id.chat_text_right);
            textRightTime=(TextView)view.findViewById(R.id.text_time_right);
            linearLayoutLeft=(LinearLayout)view.findViewById(R.id.left_layout);
            linearLayoutRight=(LinearLayout)view.findViewById(R.id.right_layout);
            linearLayoutLeft.setVisibility(View.VISIBLE);
            linearLayoutRight.setVisibility(View.VISIBLE);
        }

        public TextView getTextView() {
            return textTimeFull;
        }
    }

}
