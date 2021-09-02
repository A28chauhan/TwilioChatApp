package com.carematix.twiliochatapp.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.carematix.twiliochatapp.MainActivity;
import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.application.ChatClientManager;
import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.architecture.table.UserAllList;
import com.carematix.twiliochatapp.architecture.viewModel.FetchChannelDetailsViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserChannelListViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserChannelViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserListViewModel;
import com.carematix.twiliochatapp.databinding.UserListItemBinding;
import com.carematix.twiliochatapp.fetchchannel.bean.FetchChannelResult;
import com.carematix.twiliochatapp.fetchchannel.bean.FetchChannelView;
import com.carematix.twiliochatapp.fetchchannel.data.FetchInDetails;
import com.carematix.twiliochatapp.fetchchannel.viewmodel.FetchChannelViewModel;
import com.carematix.twiliochatapp.fetchchannel.viewmodel.FetchChannelViewModelFactory;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.listener.OnclickListener;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.twilio.ChannelManager;
import com.carematix.twiliochatapp.twilio.ToastStatusListener;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.Channels;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Member;
import com.twilio.chat.Message;
import com.twilio.chat.Messages;
import com.twilio.chat.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public
class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>{

    Context context;
    List<UserAllList> arrayList;
    OnclickListener onclickListener;

    PrefManager prefManager;

    Channel channel;
    Channels channelsObject;
    private ChatClientManager chatClientManager;
    public FetchChannelDetailsViewModel fetchChannelDetailsViewModel;

    public HashMap<Integer,Channel> channelList =new HashMap<>();
    public HashMap<Integer,ViewHolder> holderViewList =new HashMap<>();

    public UserListAdapter(Context mContext, ArrayList<UserAllList> arrayList, OnclickListener onclickListener,Channel channels){
        this.context=mContext;
        this.arrayList = arrayList;
        this.onclickListener =onclickListener;
        this.channel=channels;
        prefManager=new PrefManager(context);
        fetchChannelDetailsViewModel = new ViewModelProvider((MainActivity)mContext).get(FetchChannelDetailsViewModel.class);
        chatClientManager = TwilioApplication.get().getChatClientManager();
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

 //onBindViewHolder onCreateViewHolder getItemViewType
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        try {
            holderViewList.put(arrayList.get(position).getDroProgramUserId(),viewHolder);
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

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        public UserAllList getUserAllList() {
            return userAllList;
        }

        public void setUserAllList(UserAllList userAllList) {
            this.userAllList = userAllList;
        }

        public ViewHolder(View view){
            super(view);
            userListItemBinding = UserListItemBinding.bind(view);
            //setContentView(binding.getRoot());

            userListItemBinding.textName.setOnClickListener(this::onClick);
            userListItemBinding.msgDetails.setOnClickListener(this::onClick);
            userListItemBinding.constraintLayout.setOnClickListener(this::onClick);

            try {
                chatClientManager.getChatClient().addListener(chatClientListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setUserDetails() {

            String userName= getUserAllList().getFirstName() + " " + getUserAllList().getLastName();
            userListItemBinding.textName.setText( ""+userName);
            userListItemBinding.msgDetails.setText(Utils.getStringResource(R.string.tap_to_start,context));
            userListItemBinding.msgDetails.setVisibility(View.VISIBLE);
            fetchUserDetails();
        }

        public void fetchUserDetails(){
            try {

                if(arrayList.size() > 0){
                    int attendeeProgramUserID =getUserAllList().getDroProgramUserId();
                    channelList.put(attendeeProgramUserID,null);
                    String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
                    fetchChannelDetailsViewModel.setViewHolder(this);
                    fetchChannelDetailsViewModel.setProgramUserId(programUserId);
                    fetchChannelDetailsViewModel.setAttendeeProgramUserId(String.valueOf(attendeeProgramUserID));
                    fetchChannelDetailsViewModel.callApi(programUserId,String.valueOf(attendeeProgramUserID),this);

                    fetchChannelDetailsViewModel.getMutableLiveData().observe(((MainActivity) context), new Observer<FetchInDetails>() {
                        @Override
                        public void onChanged(FetchInDetails fetchInDetails) {
                            if (fetchInDetails == null) {
                                return;
                            }
                            if (fetchInDetails.getAttendeUserID() != null) {
                                Logs.d("fetchInDetails" ,"fetchInDetails : "+fetchInDetails.getAttendeUserID());
                                updateUiWithUser(fetchInDetails);

                                //fetchChannelDetailsViewModel.getMutableLiveData().removeObserver();
                            }

                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void updateUiWithUser(FetchInDetails fetchInDetails){
            try {

                if (chatClientManager == null || chatClientManager.getChatClient() == null) return;

                int attendId =Integer.parseInt(fetchInDetails.getAttendeUserID());
                String channelId = fetchInDetails.getChannelDetailsResponse().body().getData().getChannelSid();//fetchChannelView.getUserResultResponse().body().getData().getChannelSid();

                chatClientManager.getChatClient().getChannels().getChannel(channelId, new CallbackListener<Channel>() {
                    @Override
                    public void onSuccess(Channel channels) {
                        try {
                            channels.removeListener(channelListener);
                            channels.addListener(channelListener);

                            setChannel(channels);
                            channelList.put(attendId,channels);

                            joinChannel(channels);
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
                Logs.d("Channel","Channel already joined : "+channels.getStatus());
                updateView(channels);
            }else{

                Logs.d("Channel","Channel not joined :"+channels.getStatus());
                channels.join(new ToastStatusListener("Successfully joined channel","Failed to join channel"){
                    @Override
                    public void onSuccess() {
                        //super.onSuccess();
                        Logs.d("Channel","Channel joined Successfully");
                        setAllConsume(channels);
                       // updateView(channels);
                    }
                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        super.onError(errorInfo);
                    }
                });
            }

        }

        public void setAllConsume(Channel channels){
            updateViewFirstTime(channels);
        }

        public void updateViewFirstTime(Channel channel){
            try {
                Logs.d("channel"," updateView count "+channel.getSid());
                if (channel.getMessages() != null) {

                    channel.getMessages().getLastMessages(50, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            if(messages.size()>0){
                                viewHolder = getViewHolder(channelList,channel);
                                String UserMessages = messages.get((messages.size()-1)).getMessageBody().toString();
                                Date date=messages.get((messages.size()-1)).getDateCreatedAsDate();
                                int count=messages.size();
                                Logs.d("updateViewFirstTime onSuccess"," getLastMessages count"+UserMessages +" "+date.getTime());
                                viewHolder.userListItemBinding.msgDetails.setText(UserMessages);
                                viewHolder.userListItemBinding.msgTime.setText(Utils.setTime(date));
                                viewHolder.userListItemBinding.msgDetails.setVisibility(View.VISIBLE);

                                viewHolder.userListItemBinding.textUnconsumedMessageCount.setText(""+count);
                                viewHolder.userListItemBinding.textUnconsumedMessageCount.setVisibility(View.VISIBLE);

                            }else{
                                viewHolder.userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                viewHolder.userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
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
            channel =null;
            int attendeeProgramUserId = arrayList.get(getAdapterPosition()).getDroProgramUserId();
            String programUserId= prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
            String name = arrayList.get(getAdapterPosition()).getFirstName();
            String lastName = arrayList.get(getAdapterPosition()).getLastName();
            if(channelList.size()>0){
                for(Map.Entry<Integer,Channel> entry: channelList.entrySet()){
                    if(entry.getKey() == attendeeProgramUserId){
                        channel = entry.getValue();
                    }
                }
            }
            onclickListener.onClick(attendeeProgramUserId,programUserId,getAdapterPosition(),channel,name+" "+lastName);

        }

        public void getUpdateView(Message message){
            Logs.d("getUpdateView","messages added"+message.getChannel().getSid());
            viewHolder = getViewHolder(channelList,message.getChannel());
            try {
                String UserMessages = message.getMessageBody().toString();
                viewHolder.userListItemBinding.msgDetails.setText(UserMessages);
                viewHolder.userListItemBinding.msgTime.setText(Utils.setTime(message.getDateCreatedAsDate()));
                viewHolder.userListItemBinding.msgDetails.setVisibility(View.VISIBLE);

                message.getChannel().getUnconsumedMessagesCount(new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        try {
                            Logs.d("errorInfo onSuccess","messages added"+aLong);
                            viewHolder = getViewHolder(channelList,message.getChannel());
                            if(aLong != null){
                                if(aLong == 0){
                                    viewHolder.userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                    viewHolder.userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                                }else{
                                    viewHolder.userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf(aLong));
                                    viewHolder.userListItemBinding.textUnconsumedMessageCount.setVisibility(View.VISIBLE);
                                }
                            }else{
                                updateViewFirstTime(message.getChannel());
                               // viewHolder.userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                               // viewHolder.userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
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

        public void updateView(Channel channel){
            try {
                Logs.d("channel"," updateView count "+channel.getSid());
                if (channel.getMessages() != null) {
                    channel.getMessages().getLastMessages(1, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            try {
                                Logs.d("channel"," onSuccess messages "+messages.size()+" "+channel.getSid());

                                if (messages.size() > 0) {
                                    viewHolder = getViewHolder(channelList,channel);
                                    //viewHolder= (ViewHolder) itemView.getTag();
                                    String UserMessages = messages.get(0).getMessageBody().toString();
                                    viewHolder.userListItemBinding.msgDetails.setText(UserMessages);
                                    viewHolder.userListItemBinding.msgTime.setText(Utils.setTime(messages.get(0).getDateCreatedAsDate()));
                                    viewHolder.userListItemBinding.msgDetails.setVisibility(View.VISIBLE);
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Logs.d("channel"," getUnconsumed count "+channel.getSid());
                channel.getUnconsumedMessagesCount(new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        try {
                            Logs.d("onSuccess"," getUnconsumed count"+channel.getSid()+" aLong = "+aLong);
                            viewHolder = getViewHolder(channelList,channel);
                            //viewHolder= (ViewHolder) itemView.getTag();
                            if(aLong != null){
                                if(aLong == 0){
                                    viewHolder.userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                    viewHolder.userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                                }else{
                                    viewHolder.userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf(aLong));
                                    viewHolder.userListItemBinding.textUnconsumedMessageCount.setVisibility(View.VISIBLE);
                                }
                            }else{
                                updateViewFirstTime(channel);
                               // viewHolder.userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                               // viewHolder.userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
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

        ViewHolder viewHolder=null;
        public ViewHolder getViewHolder(HashMap<Integer,Channel> channelList,Channel channel){
            try {
                for(Map.Entry<Integer,Channel> entry: channelList.entrySet()){
                    Channel channel1 = entry.getValue();
                    if(channel1 != null){
                        if(channel1.getSid().equals(channel.getSid())){
                            viewHolder = holderViewList.get(entry.getKey());
                            return viewHolder;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return viewHolder;
        }

        public void ClearChannelDetails(Channel channel){
            try {
                viewHolder = getViewHolder(channelList,channel);

                viewHolder.userListItemBinding.msgDetails.setText(Utils.getStringResource(R.string.tap_to_start,context));
                viewHolder.userListItemBinding.msgTime.setText("");
                viewHolder.userListItemBinding.msgDetails.setVisibility(View.VISIBLE);

                viewHolder.userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                viewHolder.userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                try {
                    //Logs.e("adapter onMemberUpdated userListAdapter"," msg : "+member.getChannel().getSid());
                    //updateView(member.getChannel());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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


        public ChatClientListener chatClientListener=new ChatClientListener() {
            @Override
            public void onChannelJoined(Channel channel) {

            }

            @Override
            public void onChannelInvited(Channel channel) {

            }

            @Override
            public void onChannelAdded(Channel channel) {

            }

            @Override
            public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {

            }

            @Override
            public void onChannelDeleted(Channel channel) {
                if(channel != null){
                    Logs.d("adapter onChannelDeleted ","onChannelDeleted"+channel.getSid());
                    ClearChannelDetails(channel);
                }
            }

            @Override
            public void onChannelSynchronizationChange(Channel channel) {

            }

            @Override
            public void onError(ErrorInfo errorInfo) {

            }

            @Override
            public void onUserUpdated(User user, User.UpdateReason updateReason) {

            }

            @Override
            public void onUserSubscribed(User user) {

            }

            @Override
            public void onUserUnsubscribed(User user) {

            }

            @Override
            public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {

            }

            @Override
            public void onNewMessageNotification(String s, String s1, long l) {

            }

            @Override
            public void onAddedToChannelNotification(String s) {

            }

            @Override
            public void onInvitedToChannelNotification(String s) {

            }

            @Override
            public void onRemovedFromChannelNotification(String s) {

            }

            @Override
            public void onNotificationSubscribed() {

            }

            @Override
            public void onNotificationFailed(ErrorInfo errorInfo) {

            }

            @Override
            public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {

            }

            @Override
            public void onTokenExpired() {

            }

            @Override
            public void onTokenAboutToExpire() {

            }
        };

    }

//Q2FyZUAxMDE= Q2FyZUAxMTE= Q2FyZUAxMTA=



}
