package com.carematix.twiliochatapp.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.carematix.twiliochatapp.MainActivity;
import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.application.ChatClientManager;
import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.architecture.table.UserAllList;
import com.carematix.twiliochatapp.architecture.viewModel.FetchChannelDetailsViewModel;
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
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Member;
import com.twilio.chat.Message;
import com.twilio.chat.User;

import java.util.ArrayList;
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
    ChatClientManager chatClientManager;
    HashMap<String ,Integer> channelHashMap = new HashMap<String ,Integer> ();

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
    }

    public void addChatListener(){
        getChatClientManager().getChatClient().addListener(chatClientListener);
    }


    public void addItem(List<UserAllList> arrayList1){
        try {
            arrayList = arrayList1;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Logs.d(" onBindViewHolder ","onBindViewHolder call "+position);
        viewHolder.setPositions(position);
        viewHolder.setAttandee(arrayList.get(position));
        viewHolder.setUserDetails();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);
        Logs.d(" onCreateViewHolder ","onCreateViewHolder call "+viewType);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        UserListItemBinding userListItemBinding;
        String name;
        UserAllList attandee;
        Channel channel;
        int positions ;

        public int getPositions() {
            return positions;
        }
        public void setPositions(int positions) {
            this.positions = positions;
        }




        public UserListItemBinding getUserListItemBinding() {
            return userListItemBinding;
        }
        public void setUserListItemBinding(UserListItemBinding userListItemBinding) {
            this.userListItemBinding = userListItemBinding;
        }

        public Channel getChannel() {
            return channel;
        }
        public void setChannel(Channel channel) {
            this.channel = channel;
        }

        public UserAllList getAttandee() {
            return attandee;
        }
        public void setAttandee(UserAllList attandee) {
            this.attandee = attandee;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public ViewHolder(View view){
            super(view);
            setUserListItemBinding(UserListItemBinding.bind(view));
            getUserListItemBinding().textName.setOnClickListener(this::onClick);
            getUserListItemBinding().msgDetails.setOnClickListener(this::onClick);
            getUserListItemBinding().constraintLayout.setOnClickListener(this::onClick);
        }

        public void setUserDetails() {
            setMessageText(Utils.getStringResource(R.string.tap_to_start,context), "" ,View.VISIBLE ,View.VISIBLE );
            getUserListItemBinding().textName.setText(getAttandee().getFirstName() + "" + getAttandee().getLastName() );

            if ( getChannel() == null) {
                callChannelAPI();
            }else if  ( channelHashMap.get( getChannel().getSid()) == null){
                // updateUiWithUser(getChannel().getSid());
                clearChannelData();
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
                int attendeeProgramUserID = getAttandee().getDroProgramUserId();
                String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
                ApiInterface apiService = ApiClient.getClient1().create(ApiInterface.class);
                Call<ChannelDetails> call = apiService.activeChannel(programUserId,String.valueOf(attendeeProgramUserID), Constants.X_DRO_SOURCE);
                call.enqueue(new Callback<ChannelDetails>() {
                    @Override
                    public void onResponse(Call<ChannelDetails> call, Response<ChannelDetails> response) {
                        try {
                            int code = response.code();
                            if (code == 200) {
                                if(response.body().getMessage().contains("No active")){
                                    //fetchInDetailsResult = fetchFailureDetails(response.body().getMessage());
                                }else{
                                    String channelId = response.body().getData().getChannelSid();//fetchChannelView.getUserResultResponse().body().getData().getChannelSid();//fetchChannelView.getUserResultResponse().body().getData().getChannelSid();

                                    updateUiWithUser(channelId);
                                }
                            }else{

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ChannelDetails> call, Throwable t) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void updateUiWithUser(String channelId){
            if  ( channelId == null){
                clearChannelData();
            }else if  ( channelId.equals("") ){
                clearChannelData();
            }else {

                try {

                    if (chatClientManager == null || chatClientManager.getChatClient() == null)
                        return;

                    getChatClientManager().getChatClient().getChannels().getChannel(channelId, new CallbackListener<Channel>() {

                        @Override
                        public void onSuccess(Channel channels) {
                            try {
                                if (channels.getStatus() == Channel.ChannelStatus.JOINED) {
                                    setChannel(channels);
                                    getChannel().removeListener(channelListener);
                                    getChannel().addListener(channelListener);
                                    getUpdateView();
                                    channelHashMap.put(getChannel().getSid(), getPositions());

                                } else {
                                    channels.join(new ToastStatusListener("Successfully joined channel", "Failed to join channel") {
                                        @Override
                                        public void onSuccess() {
                                            setChannel(channels);
                                            getChannel().removeListener(channelListener);
                                            getChannel().addListener(channelListener);
                                            getUpdateView();
                                            channelHashMap.put(getChannel().getSid(), getPositions());
                                        }

                                        @Override
                                        public void onError(ErrorInfo errorInfo) {
                                            super.onError(errorInfo);
                                            if (channels.getStatus() == Channel.ChannelStatus.NOT_PARTICIPATING) {
                                                // fetchChannel(attendeeProgramUserId,programUserId,UserName);
                                            }
                                        }
                                    });
                                }
                                Logs.d("onSuccess", "get chatClientManagerchaneel : " + channels.getSid());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onClick(View view) {
            click();
        }

        public void click(){
            int attendeeProgramUserId = arrayList.get(getAdapterPosition()).getDroProgramUserId();
            String programUserId= prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
            String name = arrayList.get(getAdapterPosition()).getFirstName();
            String lastName = arrayList.get(getAdapterPosition()).getLastName();
            channel = getChannel();
            onclickListener.onClick(attendeeProgramUserId,programUserId,getAdapterPosition(),channel,name+" "+lastName);
        }

        public void getUpdateView(){
            try {
                if (getChannel() != null) {
                    getChannel().getUnconsumedMessagesCount(new CallbackListener<Long>() {
                        @Override
                        public void onSuccess(Long aLong) {

                            if (aLong != null) {
                                if (aLong == 0) {
                                    setCountText( String.valueOf("0"),View.INVISIBLE);
                                } else {
                                    setCountText( String.valueOf(aLong),View.VISIBLE);
                                }
                                setLastMessage(4);
                            }else{
                                setCountText( String.valueOf("0"),View.INVISIBLE);
                                setLastMessage(-1);

                            }

                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            super.onError(errorInfo);
                            setCountText( String.valueOf("0"),View.INVISIBLE);
                        }
                    });
                }else{
                    Logs.d("onError ", "getUnconsumedMessagesCount : " + "Channel not found");
                }
            } catch(Exception e){
                e.printStackTrace();
            }

        }

        public void setLastMessage(int count){
            try {
                if ( getChannel().getMessages() != null) {
                    getChannel().getMessages().getLastMessages(1, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            try {
                                if (messages.size() > 0) {
                                    setMessageText(messages.get(0).getMessageBody().toString(), Utils.setTime(messages.get(0).getDateCreatedAsDate()) ,View.VISIBLE ,View.VISIBLE );
                                    if (count == -1) {
                                        setCountText( String.valueOf(messages.get(0).getMessageIndex() + 1),View.VISIBLE );
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            super.onError(errorInfo);
                            Logs.d("onError ", "getUnconsumedMessagesCount : " + errorInfo.getMessage());
                        }
                    });
                }else{
                    Logs.d("onError ", "getChannel().getMessages() == null : ");
                }
            } catch (Exception e) {
                e.printStackTrace(  );
            }
        }
        public void setMessageText( String  msgDetails, String  msgTime, int msgVisibility , int msgTimeVisibility ){
            ((MainActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getUserListItemBinding().msgDetails.setText(msgDetails);
                    getUserListItemBinding().msgTime.setText(msgTime);
                    getUserListItemBinding().msgDetails.setVisibility(msgVisibility);
                    getUserListItemBinding().msgTime.setVisibility(msgTimeVisibility);
                }
            });


        }
        public void setCountText( String  count,  int countVisibility ){
            ((MainActivity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    userListItemBinding.textUnconsumedMessageCount.setText( count);
                    userListItemBinding.textUnconsumedMessageCount.setVisibility(countVisibility);
                }
            });

        }

        public void clearChannelData(){
            try {
                setMessageText(Utils.getStringResource(R.string.tap_to_start,context), "" ,View.VISIBLE ,View.VISIBLE );
                setChannel(null);
                setCountText(String.valueOf("0") ,View.INVISIBLE );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public ChannelListener channelListener =new ChannelListener() {
            @Override
            public void onMessageAdded(Message message) {
                if (message != null){
                    Logs.e("adapter onMessageAdded userListAdapter"," msg : "+message.getMessageBody());
                    getUpdateView();
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
    public ChatClientListener chatClientListener=new ChatClientListener() {
        @Override
        public void onChannelJoined(Channel channel) {
            if (channel != null) notifyDataSetChanged();
            Logs.d("adapter onChannelJoined ", "onChannelJoined" + channel.getSid());

        }

        @Override
        public void onChannelInvited(Channel channel) {
            Logs.d("adapter onChannelJoined ", "onChannelInvited" + channel.getSid());

        }

        @Override
        public void onChannelAdded(Channel channel) {
            Logs.d("adapter onChannelDeleted ", "onChannelAdded" + channel.getSid());

        }

        @Override
        public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {

        }

        @Override
        public void onChannelDeleted(Channel channel) {
            Logs.d("adapter onChannelDeleted ", "onChannelDeleted" + channel.getSid());
            try {
                if (channel != null){
                    int position = channelHashMap.get(channel.getSid());
                    channelHashMap.remove(channel.getSid());
                    notifyItemChanged( position);

                }
            }catch (Exception e){
                e.printStackTrace();
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
            Logs.d("adapter onChannelDeleted ", "onAddedToChannelNotification" + s);

        }

        @Override
        public void onInvitedToChannelNotification(String s) {
            Logs.d("adapter onChannelDeleted ", "onInvitedToChannelNotification" + s);

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
