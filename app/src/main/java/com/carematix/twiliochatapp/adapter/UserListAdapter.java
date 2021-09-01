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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public
class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>{

    Context context;
    List<UserAllList> arrayList;
    OnclickListener onclickListener;

    public UserListViewModel userListviewModel;
    public UserChannelViewModel userChannelViewModel;
    public UserChannelListViewModel userChannelListViewModel;
    public FetchChannelViewModel fetchChannelViewModel;

    PrefManager prefManager;

    Channel channel;
    Channels channelsObject;

    private ChannelManager channelManager;
    private ChatClientManager chatClientManager;

    public HashMap<Integer,Channel> channelList =new HashMap<>();
    public HashMap<Integer,ViewHolder> holderViewList =new HashMap<>();

    public int count =0;

    public UserListAdapter(Context mContext, ArrayList<UserAllList> arrayList, OnclickListener onclickListener,Channel channels){
        this.context=mContext;
        this.arrayList = arrayList;
        this.onclickListener =onclickListener;
        this.channel=channels;
        prefManager=new PrefManager(context);
        count =0;
        try {
            userListviewModel =new ViewModelProvider((MainActivity)mContext).get(UserListViewModel.class);
            userChannelViewModel = new ViewModelProvider((MainActivity)mContext).get(UserChannelViewModel.class);
            userChannelListViewModel = new ViewModelProvider((MainActivity)mContext).get(UserChannelListViewModel.class);
            fetchChannelViewModel = new ViewModelProvider((MainActivity)mContext, new FetchChannelViewModelFactory()).get(FetchChannelViewModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        channelManager = ChannelManager.getInstance();
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

        String name = arrayList.get(position).getFirstName();
        String lastName = arrayList.get(position).getLastName();
        String userName= name+" "+lastName;
        viewHolder.userListItemBinding.textName.setText(userName);
        viewHolder.userListItemBinding.msgDetails.setText(Utils.getStringResource(R.string.tap_to_start,context));
        viewHolder.userListItemBinding.msgDetails.setVisibility(View.VISIBLE);

        try {
            int attendeeProgramUserID =arrayList.get(position).getDroProgramUserId();
            holderViewList.put(attendeeProgramUserID,viewHolder);

            fetchChannelDetails(viewHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


   public void fetchChannelDetails(ViewHolder holder){
       try {
           holder.fetchUserDetails(holder.getAdapterPosition());
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    /*public ViewHolder getViewHolder(HashMap<Integer,Channel> channelList,Channel channel){
        for(Map.Entry<Integer,Channel> entry: channelList.entrySet()){
            Channel channel1 = entry.getValue();
            if(channel1 != null){
                if(channel1.getSid().equals(channel.getSid())){
                    viewHolder = holderViewList.get(entry.getKey());
                    return viewHolder;
                }
            }
        }

        return viewHolder;
    }*/

    //onBindViewHolder onCreateViewHolder getItemViewType
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
        if(arrayList.size()>0){
            return arrayList.size();
        }
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        UserListItemBinding userListItemBinding;
        public ViewHolder(View view){
            super(view);
            userListItemBinding = UserListItemBinding.bind(view);
            userListItemBinding.textName.setOnClickListener(this::onClick);
            userListItemBinding.msgDetails.setOnClickListener(this::onClick);
            userListItemBinding.constraintLayout.setOnClickListener(this::onClick);


        }

        public void fetchUserDetails(int position){
            try {
                if(arrayList.size() > 0){
                    int attendeeProgramUserID =arrayList.get(position).getDroProgramUserId();
                    String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
                    channelList.put(attendeeProgramUserID,null);
                    fetchChannelViewModel.fetchChannel(String.valueOf(attendeeProgramUserID),programUserId);
                }

                fetchChannelViewModel.getFetchChannelResult().observe(((MainActivity)context), new Observer<FetchChannelResult>() {
                    @Override
                    public void onChanged(FetchChannelResult fetchChannelResult) {
                        if (fetchChannelResult == null) {
                            return;
                        }
                        if (fetchChannelResult.getSuccess() != null) {
                            updateUiWithUser(fetchChannelResult);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void updateUiWithUser(FetchChannelResult fetchChannelResult){

            FetchChannelView fetchChannelView=fetchChannelResult.getSuccess();
            //HashMap<String, FetchInDetails> resultHashMap = fetchChannelView.getFetchInDetails();

            try {
                if (chatClientManager == null || chatClientManager.getChatClient() == null) return;

                if (channelsObject == null){
                    channelsObject = chatClientManager.getChatClient().getChannels();
                }

                //FetchInDetails fetchChannel = entry.getValue();
                int attendId =Integer.parseInt(fetchChannelView.getChannelSId());
                String channelId = fetchChannelView.getUserResultResponse().body().getData().getChannelSid();//fetchChannelView.getUserResultResponse().body().getData().getChannelSid();

                channelsObject.getChannel(channelId, new CallbackListener<Channel>() {
                    @Override
                    public void onSuccess(Channel channels) {
                        try {
                            channels.removeListener(channelListener);
                            channels.addListener(channelListener);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            channelList.put(attendId,channels);
                            setChannel(channels);
                            Logs.d("onSuccess","get chaneel : "+channels.getSid());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

               /* Logs.d("updateUiWithUser","updateUiWithUser : "+resultHashMap.size());
                if(resultHashMap.size() > 0)
                    for(Map.Entry<String,FetchInDetails> entry: resultHashMap.entrySet()){
                        FetchInDetails fetchChannel = entry.getValue();
                        int attendId =Integer.parseInt(entry.getKey());
                        String channelId = fetchChannel.getChannelDetailsResponse().body().getData().getChannelSid();//fetchChannelView.getUserResultResponse().body().getData().getChannelSid();

                        channelsObject.getChannel(channelId, new CallbackListener<Channel>() {
                            @Override
                            public void onSuccess(Channel channels) {
                                try {
                                    channels.removeListener(channelListener);
                                    channels.addListener(channelListener);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    channelList.put(attendId,channels);
                                    setChannel(channels);
                                    Logs.d("onSuccess","get chaneel : "+channels.getSid());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }*/


                try {
                    chatClientManager.getChatClient().addListener(chatClientListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MutableLiveData<Channel> mutableLiveData =new MutableLiveData<>();

        public MutableLiveData<Channel> getMutableLiveData() {
            return mutableLiveData;
        }

        public void setChannel(Channel channels){

            try {
                mutableLiveData.setValue(channels);

                mutableLiveData.observe(((MainActivity) context), new Observer<Channel>() {
                    @Override
                    public void onChanged(Channel channel) {
                        updateValue(channel);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void updateValue(Channel channels){
            if(channels != null){
            try {
                    channels.getMessages().getLastMessages(1, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            try {
                                if (messages.size() > 0) {
                                    String UserMessages = messages.get(0).getMessageBody().toString();
                                    userListItemBinding.msgDetails.setText(UserMessages);
                                    userListItemBinding.msgTime.setText(Utils.setTime(messages.get(0).getDateCreatedAsDate()));
                                    userListItemBinding.msgDetails.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ErrorInfo errorInfo) {
                            super.onError(errorInfo);
                            Logs.d("onError ","getUnconsumedMessagesCount : "+errorInfo.getMessage());
                        }
                    });


            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                channels.getUnconsumedMessagesCount(new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        try {
                            if(aLong != null){
                                if(aLong == 0){

                                    userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                    userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                                }else{
                                    userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf(aLong));
                                    userListItemBinding.textUnconsumedMessageCount.setVisibility(View.VISIBLE);
                                }
                            }else{
                                userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ErrorInfo errorInfo) {
                        super.onError(errorInfo);
                        Logs.d("onError ","getUnconsumedMessagesCount : "+errorInfo.getMessage());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            }else{
                Utils.showToast("Channel not found",context);
            }
        }

        @Override
        public void onClick(View view) {
            click();
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

            try {
                String UserMessages = message.getMessageBody().toString();
                userListItemBinding.msgDetails.setText(UserMessages);
                userListItemBinding.msgTime.setText(Utils.setTime(message.getDateCreatedAsDate()));
                userListItemBinding.msgDetails.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                channel =message.getChannel();
                channel.getUnconsumedMessagesCount(new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        try {
                            if(aLong != null){
                                if(aLong == 0){
                                    userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                    userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                                }else{
                                    userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf(aLong));
                                    userListItemBinding.textUnconsumedMessageCount.setVisibility(View.VISIBLE);
                                }
                            }else{
                                userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void updateView1(Channel channel){

            try {
                final Messages messagesObject = channel.getMessages();
                if (messagesObject != null) {
                    messagesObject.getLastMessages(1, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {
                            try {
                                if (messages.size() > 0) {
                                    String UserMessages = messages.get(0).getMessageBody().toString();
                                    userListItemBinding.msgDetails.setText(UserMessages);
                                    userListItemBinding.msgTime.setText(Utils.setTime(messages.get(0).getDateCreatedAsDate()));
                                    userListItemBinding.msgDetails.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                channel.getUnconsumedMessagesCount(new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        try {
                            if(aLong != null){
                                if(aLong == 0){
                                    userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                    userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                                }else{
                                    userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf(aLong));
                                    userListItemBinding.textUnconsumedMessageCount.setVisibility(View.VISIBLE);
                                }
                            }else{
                                userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void updateView(Channel channel){
            try {
                userListItemBinding.msgDetails.setText(Utils.getStringResource(R.string.tap_to_start,context));
                userListItemBinding.msgTime.setText("");
                userListItemBinding.msgDetails.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                userListItemBinding.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                userListItemBinding.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
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
                    Logs.e("adapter onMessageAdded userListAdapter"," msg : "+member.getChannel().getSid());
                    updateView1(member.getChannel());
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
                    updateView(channel);
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
