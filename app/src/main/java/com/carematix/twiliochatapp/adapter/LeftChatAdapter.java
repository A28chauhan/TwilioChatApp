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
    List<MessageItem> messageItemList;

    long unConsumedCount=0;
    public static int unConsumed =0;

    LinearLayoutManager linearLayoutManager;
    Channel channel;

    public LeftChatAdapter(Context mContext, List<MessageItem> arrayList, LinearLayoutManager linearLayoutManager, RecyclerView recyclerView,Channel mChannel ){
        this.context=mContext;
        this.messageItemList = arrayList;
        this.linearLayoutManager = linearLayoutManager;
        this.channel =mChannel;
    }

    @Override
    public int getItemCount() {
        return messageItemList.size() == 0 ? 0 : messageItemList.size();
    }


    public void addItem(List<MessageItem>  hashMapHashMap){
        try {
            this.messageItemList = hashMapHashMap;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addItem(long msg){
        this.unConsumedCount = msg;
        notifyDataSetChanged();
    }


    public void clear(){
        messageItemList.clear();
        notifyDataSetChanged();
    }


    public static String dateType="";
    String currentDate="";
    @Override
    public int getItemViewType(int position) {
        MessageItem messageItem =messageItemList.get(position);
        PrefManager prefManager=new PrefManager(context);
        String programUser = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);

        currentDate = Utils.setDateTime(messageItem.getMessage().getDateCreatedAsDate());
        try {
            if(dateType.equals("")){
                dateType =currentDate;
                return VIEW_TYPE_CENTER_ITEM;
            }else if(currentDate.equals(dateType)){
                if(!messageItem.getMessage().getAuthor().contains(programUser)){
                    return  VIEW_TYPE_LEFT_ITEM;
                }else if(messageItem.getMessage().getAuthor().contains(programUser)){
                    return VIEW_TYPE_RIGHT_ITEM;
                }
            }else{
                return VIEW_TYPE_CENTER_ITEM;
            }

        } catch (Exception e) {
            e.printStackTrace();
            dateType = currentDate;
        }
        return VIEW_TYPE_CENTER_ITEM;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        // holder.textView.setT
        MessageItem messageItem =messageItemList.get(position);
        long index = messageItem.getMessage().getMessageIndex();
        Logs.d("chat adpater 3"," messages index:"+index);


        channel.getMessages().setLastConsumedMessageIndexWithResult(index, new CallbackListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                try {
                    Logs.d("chat adpater 4"," messages index:"+aLong);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if(viewHolder instanceof RightViewHolder){
            RightViewHolder userViewHolder = (RightViewHolder) viewHolder;

            userViewHolder.textView.setText(" " + messageItem.getMessage().getMessageBody().toString());
            userViewHolder.textTime.setText(Utils.setTime(messageItem.getMessage().getDateCreatedAsDate()));

            updateMemberMessageReadStatus(userViewHolder,messageItem,position);


            /*try {
                int count =userViewHolder.getAdapterPosition();
                if(unConsumedCount != 0){
                    if(unConsumed <= unConsumedCount){
                        userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
                    }else{
                         }
                    unConsumed++;
                }else{
                    userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_nsend_indicator, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        }else if(viewHolder instanceof LeftViewHolder){
            LeftViewHolder userViewHolder = (LeftViewHolder) viewHolder;

            userViewHolder.textView.setText(" " + messageItem.getMessage().getMessageBody().toString());
            userViewHolder.textTime.setText(Utils.setTime(messageItem.getMessage().getDateCreatedAsDate()));

           // channel.getCreatedBy()

        }else if(viewHolder instanceof CenterViewHolder){

            CenterViewHolder userViewHolder = (CenterViewHolder) viewHolder;
            try {
                userViewHolder.linearLayoutLeft.setVisibility(View.VISIBLE);
                userViewHolder.linearLayoutRight.setVisibility(View.VISIBLE);
                PrefManager prefManager=new PrefManager(context);
                String programUser = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
                if(!messageItem.getMessage().getAuthor().contains(programUser)){
                    userViewHolder.linearLayoutLeft.setVisibility(View.VISIBLE);
                    userViewHolder.textLeftTime.setText(" " + messageItem.getMessage().getMessageBody().toString());
                    userViewHolder.textLeftTime.setText(Utils.setTime(messageItem.getMessage().getDateCreatedAsDate()));
                }else if(messageItem.getMessage().getAuthor().contains(programUser)){
                    userViewHolder.linearLayoutRight.setVisibility(View.VISIBLE);
                    userViewHolder.textRightTime.setText(" " + messageItem.getMessage().getMessageBody().toString());
                    userViewHolder.textRightTime.setText(Utils.setTime(messageItem.getMessage().getDateCreatedAsDate()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Calendar calendar = Calendar.getInstance();
                String todayDate = Utils.getDateTime(calendar.getTimeInMillis());
                currentDate = Utils.setDateTime(messageItem.getMessage().getDateCreatedAsDate());
                if(todayDate.compareTo(currentDate) == 0){
                    userViewHolder.textTimeFull.setText("Today");
                    userViewHolder.textTimeFull.setVisibility(View.VISIBLE);
                }else if(todayDate.compareTo(currentDate) > 0){

                    int check = Integer.parseInt(Utils.getDateC1(messageItem.getMessage().getDateCreatedAsDate()));
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
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }else{
            throw new RuntimeException("Unknown view type in onBindViewHolder");
        }

    }




    public void updateMemberMessageReadStatus(RightViewHolder userViewHolder,MessageItem messageItem,int post){

        /*if(member.getLastConsumedMessageIndex() != null && member.getLastConsumedMessageIndex() == hashMap.getMessage().getMessageIndex()){
            userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_nsend_indicator, 0);
        }else{
            userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
        }*/

        long index = 0;
        try {
            index = messageItem.getMessage().getMessageIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
        channel.getMessages().advanceLastConsumedMessageIndexWithResult(index, new CallbackListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                try {
                    if(aLong != null){
                        if(aLong == 0){
                            userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
                        }else{
                            userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_nsend_indicator, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        channel.getMessages().setLastConsumedMessageIndexWithResult(index, new CallbackListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                try {
                    if(aLong != null){
                        if(aLong == 0){
                            userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
                        }else{
                            userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_nsend_indicator, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        channel.getMessages().setAllMessagesConsumedWithResult(new CallbackListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                try {
                    if(aLong != null){
                        if(aLong == 0){
                            userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
                        }else{
                            userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_nsend_indicator, 0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        long inn = 0;
        long ind = 0;
        try {
            inn = messageItem.getMessage().getMember().getLastConsumedMessageIndex();
            ind = messageItem.getMessage().getMessageIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logs.d("chat adpater 0"," memeber index :"+post+" :"+inn);
        Logs.d("chat adpater 0"," memeber index :"+post+" :"+ind);
        /*for(Member member: channel.getMembers().getMembersList()){
            Logs.d("chat adpater 0"," memeber :"+member.getIdentity());
            Logs.d("chat adpater 1"," memeber :"+member.getLastConsumedMessageIndex());
            Logs.d("chat adpater 2"," memeber :"+channel.getMessages().getLastConsumedMessageIndex());
            if(member.getLastConsumedMessageIndex() != null && member.getLastConsumedMessageIndex() == messageItem.getMessage().getMessageIndex()){
                userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
                //userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_nsend_indicator, 0);
            }else{
                userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_nsend_indicator, 0);
            }

        }*/
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

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();

                    Intent intent =new Intent(context, ChatActivity.class);
                    context.startActivity(intent);
                    //context.finish();
                }
            });
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

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    Logs.d("left chat adapter","position :"+pos);

                }
            });
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
