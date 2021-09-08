package com.carematix.twiliochatapp.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carematix.twiliochatapp.MainActivity;
import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.application.ChatClientManager;
import com.carematix.twiliochatapp.architecture.table.UserAllList;
import com.carematix.twiliochatapp.bean.fetchChannel.ChannelDetails;
import com.carematix.twiliochatapp.databinding.UserListItemBinding;
import com.carematix.twiliochatapp.helper.Constants;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.listener.OnclickListener;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.restapi.ApiClient;
import com.carematix.twiliochatapp.restapi.ApiInterface;
import com.carematix.twiliochatapp.twilio.ToastStatusListener;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Member;
import com.twilio.chat.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public
class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>{

    Context context;
    List<UserAllList> arrayList;
    OnclickListener onclickListener;
    PrefManager prefManager;

    private ChatClientManager chatClientManager;

    public HashMap<String,Integer> channelList =new HashMap<>();


    public ChatClientManager getChatClientManager() {
        return chatClientManager;
    }

    public void setChatClientManager(ChatClientManager chatClientManager) {
        this.chatClientManager = chatClientManager;
    }

    public UserListAdapter(Context mContext, ArrayList<UserAllList> arrayList, OnclickListener onclickListener, ChatClientManager chatClientManager){
        this.context=mContext;
        this.arrayList = arrayList;
        this.onclickListener =onclickListener;
        prefManager=new PrefManager(context);
        this.chatClientManager = chatClientManager;
        setChatClientManager(chatClientManager);
        // add chat client listener
    }


    public void addItem(List<UserAllList> arrayList1){
        try {
            arrayList = arrayList1;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addItemWithChannel(Channel channel,String tag){
        try {
            if(channel != null){
                if(tag.equals(Constants.INVITE) || tag.equals(Constants.JOIN)){
                    notifyDataSetChanged();
                } else{
                    int position = channelList.get(channel.getSid());
                    channelList.remove(channel.getSid());
                    notifyItemChanged(position);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 //onBindViewHolder onCreateViewHolder getItemViewType
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        try {
            viewHolder.setPositions(position);
            viewHolder.setUserAllList(arrayList.get(position));
            viewHolder.setUserDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //onBindViewHolder onCreateViewHolder getItemViewType
    View itemView;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);
        Logs.d(" onCreateViewHolder ","onCreateViewHolder call "+viewType);
        return new ViewHolder(itemView);
    }


    @Override
    public int getItemCount() {
        if(arrayList.size()>0){
            return arrayList.size();
        }
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        UserListItemBinding userListItemBinding;
        Channel channel;
        UserAllList userAllList;
        int positions;

        public int getPositions() {
            return positions;
        }

        public void setPositions(int positions) {
            this.positions = positions;
        }

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        public UserListItemBinding getUserListItemBinding() {
            return userListItemBinding;
        }

        public void setUserListItemBinding(UserListItemBinding userListItemBinding) {
            this.userListItemBinding = userListItemBinding;
        }

        public UserAllList getUserAllList() {
            return userAllList;
        }

        public void setUserAllList(UserAllList userAllList) {
            this.userAllList = userAllList;
        }

        public ViewHolder(View view){
            super(view);
            setUserListItemBinding(UserListItemBinding.bind(view));
            getUserListItemBinding().textName.setOnClickListener(this::onClick);
            getUserListItemBinding().msgDetails.setOnClickListener(this::onClick);
            getUserListItemBinding().constraintLayout.setOnClickListener(this::onClick);

        }

        public void setUserDetails() {

            try {
                String userName= getUserAllList().getFirstName() + " " + getUserAllList().getLastName();
                getUserListItemBinding().textName.setText( ""+userName);
                getUserListItemBinding().msgDetails.setText(Utils.getStringResource(R.string.tap_to_start,context));
                getUserListItemBinding().msgDetails.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(getChannel() == null){
                callChannelAPI();
            }else if(channelList.get( getChannel().getSid()) == null){
                clearChannelDetails();
            }else{
                callChannelAPI();
            }

        }

        public void callChannelAPI(){
            Thread t =new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        fetchUserDetails();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            t.start();
        }

        public void fetchUserDetails(){
            try {
                if(arrayList.size() > 0){
                    int attendeeProgramUserID =getUserAllList().getDroProgramUserId();
                    String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);

                    ApiInterface apiService = ApiClient.getClient1().create(ApiInterface.class);
                    Call<ChannelDetails> call = apiService.activeChannel(programUserId,String.valueOf(attendeeProgramUserID), Constants.X_DRO_SOURCE);
                    call.enqueue(new Callback<ChannelDetails>() {
                        @Override
                        public void onResponse(Call<ChannelDetails> call, Response<ChannelDetails> response) {
                            try {
                                int code = response.code();
                                if (code == 200) {
                                    if(response.body().getMessage().contains("No active")){}else{
                                        updateUiWithUser(response.body().getData().getChannelSid());
                                    }
                                }else{}
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ChannelDetails> call, Throwable t) {}
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void updateUiWithUser(String channelId){
            try {

                if(channelId == null){
                    clearChannelDetails();
                }else if(channelId.equals("")){
                    clearChannelDetails();
                }else{
                    if (getChatClientManager() == null || getChatClientManager().getChatClient() == null) return;

                    getChatClientManager().getChatClient().getChannels().getChannel(channelId, new CallbackListener<Channel>() {
                        @Override
                        public void onSuccess(Channel channels) {

                            try {
                                channels.removeListener(channelListener);
                                channels.addListener(channelListener);

                                setChannel(channels);
                                channelList.put(channels.getSid(),getAdapterPosition());

                                joinChannel(getChannel());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            super.onError(errorInfo);
                            Logs.d("error","getChannel is getting null."+errorInfo.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClick(View view) {
            click();
        }

        public void joinChannel(Channel channels){

            if(channels.getStatus() == Channel.ChannelStatus.JOINED){
                updateView();
            }else{
                channels.join(new ToastStatusListener("Successfully joined channel","Failed to join channel"){
                    @Override
                    public void onSuccess() {
                        updateViewFirstTime(getChannel());
                    }
                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        super.onError(errorInfo);
                    }
                });
            }
        }

        public void updateViewFirstTime(Channel channel){
            try {
                if (channel.getMessages() != null) {
                    channel.getMessages().getLastMessages(1, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            if(messages.size()>0){
                                setMessagesText(messages.get(0).getMessageBody(),messages.get(0).getDateCreatedAsDate(),View.VISIBLE,View.VISIBLE);
                                setVisibleText(String.valueOf((messages.get(0).getMessageIndex()+1)),View.VISIBLE);
                            }else{
                                setVisibleText("0",View.INVISIBLE);
                            }
                        }
                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            super.onError(errorInfo);
                            Logs.d("ErrorInfo"," getLastMessages count"+errorInfo.getMessage()+" "+channel.getSid());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void click(){
            channel = getChannel();
            onclickListener.onClick(arrayList.get(getAdapterPosition()),channel,arrayList.get(getAdapterPosition()).getFirstName()+" "+arrayList.get(getAdapterPosition()).getLastName());
        }

        public void getUpdateView(Message message){
            Logs.d("getUpdateView","messages added"+message.getChannel().getSid());

            try {
                String UserMessages = message.getMessageBody().toString();
                setMessagesText(UserMessages,message.getDateCreatedAsDate(),View.VISIBLE,View.VISIBLE);

                message.getChannel().getUnconsumedMessagesCount(new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        try {
                            Logs.d("errorInfo onSuccess","messages added"+aLong);
                           if(aLong != null){
                                if(aLong == 0){
                                    setVisibleText("0",View.INVISIBLE);
                                }else{
                                    setVisibleText(String.valueOf(aLong),View.VISIBLE);
                                }
                            }else{
                                updateViewFirstTime(message.getChannel());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        super.onError(errorInfo);
                        Logs.d("errorInfo","messages getUnconsumedMessagesCount added"+errorInfo.getMessage());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void updateView(){
            try {
                if (getChannel() != null)
                if (getChannel().getMessages() != null) {
                    getChannel().getMessages().getLastMessages(1, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            try {
                                if (messages.size() > 0) {
                                    String UserMessages = messages.get(0).getMessageBody().toString();
                                    setMessagesText(UserMessages,messages.get(0).getDateCreatedAsDate(),View.VISIBLE,View.VISIBLE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            super.onError(errorInfo);
                            Logs.d("ErrorInfo"," getLastMessages count"+errorInfo.getMessage()+" "+channel.getSid());
                        }
                    });
                }

                getChannel().getUnconsumedMessagesCount(new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        try {
                            if(aLong != null){
                                if(aLong == 0){
                                    setVisibleText("0",View.INVISIBLE);
                                  }else{
                                    setVisibleText(String.valueOf(aLong),View.VISIBLE);
                                }
                            }else{
                                updateViewFirstTime(channel);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        super.onError(errorInfo);
                        Logs.d("ErrorInfo"," getUnconsumed count"+errorInfo.getMessage() +" "+channel.getSid());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setMessagesText(String  msgDetails, Date msgTime, int msgVisibility , int msgTimeVisibility){
            ((MainActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getUserListItemBinding().msgDetails.setText(msgDetails);
                    getUserListItemBinding().msgDetails.setVisibility(msgVisibility);
                    if(msgTime != null)
                    getUserListItemBinding().msgTime.setText(Utils.setTime(msgTime));
                    getUserListItemBinding().msgTime.setVisibility(msgTimeVisibility);
                }
            });
        }

        public void setVisibleText(String count,int countVisibility){
            ((MainActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getUserListItemBinding().textUnconsumedMessageCount.setText(""+count);
                    getUserListItemBinding().textUnconsumedMessageCount.setVisibility(countVisibility);
                }
            });
        }

        public void clearChannelDetails(){
            ((MainActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setChannel(null);
                    setMessagesText(Utils.getStringResource(R.string.tap_to_start,context),null,View.VISIBLE,View.VISIBLE);
                    setVisibleText("0",View.INVISIBLE);
                }
            });
        }


        public ChannelListener channelListener =new ChannelListener() {
            @Override
            public void onMessageAdded(Message message) {
                if (message != null){
                    Logs.e("adapter onMessageAdded userListAdapter"," msg : "+message.getMessageBody());
                    getUpdateView(message);
                }
            }

            @Override
            public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {

            }

            @Override
            public void onMessageDeleted(Message message) {

            }

            @Override
            public void onMemberAdded(Member member) {

            }

            @Override
            public void onMemberUpdated(Member member, Member.UpdateReason updateReason) {
            }

            @Override
            public void onMemberDeleted(Member member) {

            }

            @Override
            public void onTypingStarted(Channel channel, Member member) {

            }

            @Override
            public void onTypingEnded(Channel channel, Member member) {

            }

            @Override
            public void onSynchronizationChanged(Channel channel) {

            }
        };

    }





}
