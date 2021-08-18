package com.carematix.twiliochatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Calendar;
import java.util.List;

public class LeftChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_LEFT_ITEM = 0;
    private final int VIEW_TYPE_RIGHT_ITEM = 1;
    private final int VIEW_TYPE_CENTER_ITEM = 2;

    Context context;
    List<MessageItem> hashMapHashMap;

    long unConsumedCount=0;
    public static int unConsumed =0;

    LinearLayoutManager linearLayoutManager;

    public LeftChatAdapter(Context mContext, List<MessageItem> arrayList, LinearLayoutManager linearLayoutManager, RecyclerView recyclerView ){
        this.context=mContext;
        this.hashMapHashMap = arrayList;
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public int getItemCount() {
        return hashMapHashMap.size() == 0 ? 0 : hashMapHashMap.size();
    }


    public void addItem(List<MessageItem>  hashMapHashMap){
        try {
            this.hashMapHashMap = hashMapHashMap;
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
        hashMapHashMap.clear();
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        MessageItem messageItem =hashMapHashMap.get(position);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sharedPreferences.getString("userName", null);
        PrefManager prefManager=new PrefManager(context);
        String programUser = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);

       // Calendar calendar = Calendar.getInstance();
       // String todayDate = Utils.getDateTime(calendar.getTimeInMillis());
        currentDate = Utils.setDateTime(messageItem.getMessage().getDateCreatedAsDate());

        /*try {
            if(!date.equals("")){
                if(!date.equals(currentDate)){
                    date =currentDate;
                    return VIEW_TYPE_CENTER_ITEM;
                }else{

                }
            }else{
                date =currentDate;
            }
        } catch (Exception e) {
            e.printStackTrace();
            date =currentDate;
        }*/

        if(!messageItem.getMessage().getAuthor().contains(programUser)){
            return  VIEW_TYPE_LEFT_ITEM;
        }else if(messageItem.getMessage().getAuthor().contains(programUser)){
            return VIEW_TYPE_RIGHT_ITEM;
        }

        return VIEW_TYPE_CENTER_ITEM;
    }


    String date="",currentDate="";
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        // holder.textView.setT
        MessageItem hashMap =hashMapHashMap.get(position);
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //String userName = sharedPreferences.getString("userName", null);

        if(viewHolder instanceof RightViewHolder){
            RightViewHolder userViewHolder = (RightViewHolder) viewHolder;

            userViewHolder.textView.setText(" " + hashMap.getMessage().getMessageBody().toString());
            userViewHolder.textTime.setText(Utils.setTime(hashMap.getMessage().getDateCreatedAsDate()));


            /*Calendar calendar = Calendar.getInstance();
            String todayDate = Utils.getDateTime(calendar.getTimeInMillis());
            currentDate = Utils.setDateTime(hashMap.getMessage().getDateCreatedAsDate());

            if(todayDate.compareTo(currentDate) == 0){
                userViewHolder.textTimeFull.setText("Today");
                userViewHolder.textTimeFull.setVisibility(View.VISIBLE);
            }else if(todayDate.compareTo(currentDate) > 0){

                int check = Integer.parseInt(Utils.getDateC1(hashMap.getMessage().getDateCreatedAsDate()));
                int today = Integer.parseInt(Utils.getDateC(calendar.getTimeInMillis()));
                if(check == (today-1)){
                    userViewHolder.textTimeFull.setText("Yesterday");
                    userViewHolder.textTimeFull.setVisibility(View.VISIBLE);
                }else{
                    userViewHolder.textTimeFull.setText(currentDate);
                    userViewHolder.textTimeFull.setVisibility(View.VISIBLE);

                }
            }*/


            try {
             //   Member member = hashMap.getMembers().getMember(userName);
             //   updateMemberMessageReadStatus(member.getIdentity(),member.getLastConsumedMessageIndex(),member.getLastConsumptionTimestamp());
             //   Message message =hashMap.getMessage();
              //  message.getMessageIndex();
                if(unConsumed <= unConsumedCount){
                    userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_nsend_indicator, 0);
                    unConsumed++;
                }else{
                    userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // }
        }else if(viewHolder instanceof LeftViewHolder){
            LeftViewHolder userViewHolder = (LeftViewHolder) viewHolder;

            userViewHolder.textView.setText(" " + hashMap.getMessage().getMessageBody().toString());
            userViewHolder.textTime.setText(Utils.setTime(hashMap.getMessage().getDateCreatedAsDate()));

        }else if(viewHolder instanceof CenterViewHolder){
            CenterViewHolder userViewHolder = (CenterViewHolder) viewHolder;
            Calendar calendar = Calendar.getInstance();
            String todayDate = Utils.getDateTime(calendar.getTimeInMillis());

            currentDate = Utils.setDateTime(hashMap.getMessage().getDateCreatedAsDate());
            if(todayDate.compareTo(currentDate) == 0){
                userViewHolder.textTimeFull.setText("Today");
                userViewHolder.textTimeFull.setVisibility(View.VISIBLE);
            }else if(todayDate.compareTo(currentDate) > 0){

                int check = Integer.parseInt(Utils.getDateC1(hashMap.getMessage().getDateCreatedAsDate()));
                int today = Integer.parseInt(Utils.getDateC(calendar.getTimeInMillis()));
                if(check == (today-1)){
                    userViewHolder.textTimeFull.setText("Yesterday");
                    userViewHolder.textTimeFull.setVisibility(View.VISIBLE);
                }else{
                    userViewHolder.textTimeFull.setText(currentDate);
                    userViewHolder.textTimeFull.setVisibility(View.VISIBLE);
                    /*if(!date.equals(currentDate)){
                        date =currentDate;
                        userViewHolder.textTimeFull.setText(currentDate);
                        userViewHolder.textTimeFull.setVisibility(View.VISIBLE);
                    }else{
                        date =currentDate;
                    }*/
                }
            }else{
                userViewHolder.textTimeFull.setText(currentDate);
                userViewHolder.textTimeFull.setVisibility(View.GONE);
            }

        }else{
            throw new RuntimeException("Unknown view type in onBindViewHolder");
        }

    }




    public void updateMemberMessageReadStatus(String identity,Long lastmessage,String date ){

        Logs.d("member ",identity+""+lastmessage+" "+date);
        /*if(member.getLastConsumedMessageIndex() != null && member.getLastConsumedMessageIndex() == hashMap.getMessage().getMessageIndex()){
            userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_nsend_indicator, 0);
        }else{
            userViewHolder.textTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick_indicator, 0);
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
            textView=(TextView)view.findViewById(R.id.chat_text);
            textTime=(TextView)view.findViewById(R.id.text_time);
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
            textView=(TextView)view.findViewById(R.id.chat_text);
            textTime=(TextView)view.findViewById(R.id.text_time);
            textTimeFull=(TextView)view.findViewById(R.id.textfullDate);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();

                    Logs.d("left chat adapter","position :"+pos);
                  //  Intent intent =new Intent(context, ChatActivity.class);
                  //  context.startActivity(intent);
                    //context.finish();
                }
            });
        }

        public TextView getTextView() {
            return textView;
        }
    }


    public class CenterViewHolder extends RecyclerView.ViewHolder{
        public TextView textTimeFull;
        public CenterViewHolder(View view){
            super(view);
            textTimeFull=(TextView)view.findViewById(R.id.textfullDate);
        }

        public TextView getTextView() {
            return textTimeFull;
        }
    }


   /* private final int VIEW_TYPE_LEFT_ITEM = 0;
    private final int VIEW_TYPE_RIGHT_ITEM = 1;

    Context context;
    HashMap<Integer, HashMap<String,String>> hashMapHashMap;
    *//*ArrayList<String> arrayList;
    public LeftChatAdapter(Context mContext, ArrayList<String> arrayList){
        this.context=mContext;
        this.arrayList = arrayList;
    }*//*

    public LeftChatAdapter(Context mContext, HashMap<Integer, HashMap<String,String>> arrayList){
        this.context=mContext;
        this.hashMapHashMap = arrayList;
    }

    @Override
    public int getItemCount() {
        return hashMapHashMap.size();
    }


    public void addItem(HashMap<Integer, HashMap<String,String>>  hashMapHashMap){
        this.hashMapHashMap= hashMapHashMap;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        HashMap<String,String> hashMap =hashMapHashMap.get(position);
        //Map.Entry<String,String> e = hashMap.entrySet();
        Log.e("tag",""+hashMap.keySet());
        if(hashMap.keySet().contains("LEFT")){
            return  VIEW_TYPE_LEFT_ITEM;
        }else if(hashMap.keySet().contains("RIGHT")){
            return VIEW_TYPE_RIGHT_ITEM;
        }
        return VIEW_TYPE_LEFT_ITEM;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
       // holder.textView.setT
        HashMap<String,String> hashMap =hashMapHashMap.get(position);
        if(viewHolder instanceof RightViewHolder){
            RightViewHolder userViewHolder = (RightViewHolder) viewHolder;
            for(Map.Entry m : hashMap.entrySet()) {
                userViewHolder.textView.setText(" " + m.getValue());
                userViewHolder.textTime.setText(Utils.setTime(System.currentTimeMillis()));
            }
        }else if(viewHolder instanceof LeftViewHolder){
            LeftViewHolder userViewHolder = (LeftViewHolder) viewHolder;
            for(Map.Entry m : hashMap.entrySet()) {
                userViewHolder.textView.setText(" " + m.getValue());
                userViewHolder.textTime.setText(Utils.setTime(System.currentTimeMillis()));
            }
        }else{
            throw new RuntimeException("Unknown view type in onBindViewHolder");
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
        }
        return null;
    }

    public class RightViewHolder extends RecyclerView.ViewHolder{
        public TextView textView,textTime;
        public RightViewHolder(View view){
            super(view);
            textView=(TextView)view.findViewById(R.id.chat_text);
            textTime=(TextView)view.findViewById(R.id.text_time);

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
        public TextView textView,textTime;
        public LeftViewHolder(View view){
            super(view);
            textView=(TextView)view.findViewById(R.id.chat_text);
            textTime=(TextView)view.findViewById(R.id.text_time);

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
    }*/


}
