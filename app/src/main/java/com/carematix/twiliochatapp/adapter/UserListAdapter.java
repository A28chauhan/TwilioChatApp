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
        viewHolder.textView.setText(userName);
        viewHolder.textView1.setText(Utils.getStringResource(R.string.tap_to_start,context));
        viewHolder.textView1.setVisibility(View.VISIBLE);

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
           if(arrayList.size() > 0){
               int attendeeProgramUserID =arrayList.get(holder.getAdapterPosition()).getDroProgramUserId();
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
                      // if(count <= arrayList.size()+1){
                           Logs.d("updateUiWithUser","updateUiWithUser count: "+count);
                           updateUiWithUser(fetchChannelResult);
                     //  }
                       count++;
                   }
               }
           });
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    public void updateUiWithUser(FetchChannelResult fetchChannelResult){

        FetchChannelView fetchChannelView=fetchChannelResult.getSuccess();
        HashMap<String, FetchInDetails> resultHashMap = fetchChannelView.getFetchInDetails();

        try {
            if (chatClientManager == null || chatClientManager.getChatClient() == null) return;

            if (channelsObject == null){
                channelsObject = chatClientManager.getChatClient().getChannels();
            }
            Logs.d("updateUiWithUser","updateUiWithUser : "+resultHashMap.size());
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
            }


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
        try {
            viewHolder = null;
            Logs.d("setChannel ","setChannel : "+channels.getSid());

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(channels != null){
                channels.getMessages().getLastMessages(1, new CallbackListener<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        try {
                            if (messages.size() > 0) {
                                viewHolder = getViewHolder(channelList,messages.get(0).getChannel());
                                String UserMessages = messages.get(0).getMessageBody().toString();
                                viewHolder.textView1.setText(UserMessages);
                                viewHolder.textTime.setText(Utils.setTime(messages.get(0).getDateCreatedAsDate()));
                                viewHolder.textView1.setVisibility(View.VISIBLE);
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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            channels.getUnconsumedMessagesCount(new CallbackListener<Long>() {
                @Override
                public void onSuccess(Long aLong) {
                    try {
                        if(aLong != null){
                            viewHolder = getViewHolder(channelList,channels);
                            if(aLong == 0){
                                viewHolder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                viewHolder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                            }else{
                                viewHolder.textUnconsumedMessageCount.setText(""+String.valueOf(aLong));
                                viewHolder.textUnconsumedMessageCount.setVisibility(View.VISIBLE);
                            }
                        }else{
                            viewHolder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                            viewHolder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
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
    }

    public ViewHolder getViewHolder(HashMap<Integer,Channel> channelList,Channel channel){
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
    }

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


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView,textView1,textTime,textUnconsumedMessageCount;
        ConstraintLayout constraintLayout;
        ImageView imageView;

        public ViewHolder(View view){
            super(view);

            textUnconsumedMessageCount=(TextView)view.findViewById(R.id.textUnconsumedMessageCount);
            textView=(TextView)view.findViewById(R.id.text_user);
            textView1=(TextView)view.findViewById(R.id.text_user_1);
            textTime=(TextView)view.findViewById(R.id.textView2);
            imageView=(ImageView)view.findViewById(R.id.imageView);
            constraintLayout=(ConstraintLayout)view.findViewById(R.id.constraintLayout);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    channel =null;
                    String name = arrayList.get(pos).getFirstName();
                    String lastName = arrayList.get(pos).getLastName();
                    int attendeeProgramUserId = arrayList.get(pos).getDroProgramUserId();
                    String programUserId= prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
                    if(channelList.size()>0){
                        for(Map.Entry<Integer,Channel> entry: channelList.entrySet()){
                            if(entry.getKey() == attendeeProgramUserId){
                                channel = entry.getValue();
                            }
                        }
                    }
                    onclickListener.onClick(attendeeProgramUserId,programUserId,pos,channel,name+" "+lastName);

                }
            });

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    channel =null;
                    int pos=getAdapterPosition();
                    int attendeeProgramUserId = arrayList.get(pos).getDroProgramUserId();
                    String programUserId= prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
                    String name = arrayList.get(pos).getFirstName();
                    String lastName = arrayList.get(pos).getLastName();
                    if(channelList.size()>0){
                        for(Map.Entry<Integer,Channel> entry: channelList.entrySet()){
                            if(entry.getKey() == attendeeProgramUserId){
                                channel = entry.getValue();
                            }
                        }
                    }
                    onclickListener.onClick(attendeeProgramUserId,programUserId,pos,channel,name+" "+lastName);

                }
            });

        }


        public TextView getTextView() {
            return textView;
        }

        public TextView getTextView1() {
            return textView1;
        }

    }

//Q2FyZUAxMDE= Q2FyZUAxMTE= Q2FyZUAxMTA=
    ViewHolder viewHolder;
    public void getUpdateView(Message message){
        viewHolder = null;
        try {
            String sid = message.getChannel().getSid();
            for(Map.Entry<Integer,Channel> entry: channelList.entrySet()){
                Channel channel=entry.getValue();
                if(channel != null){
                    if(channel.getSid().equals(sid)){
                        viewHolder = holderViewList.get(entry.getKey());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String UserMessages = message.getMessageBody().toString();
            viewHolder.textView1.setText(UserMessages);
            viewHolder.textTime.setText(Utils.setTime(message.getDateCreatedAsDate()));
            viewHolder.textView1.setVisibility(View.VISIBLE);
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
                                viewHolder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                viewHolder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                            }else{
                                viewHolder.textUnconsumedMessageCount.setText(""+String.valueOf(aLong));
                                viewHolder.textUnconsumedMessageCount.setVisibility(View.VISIBLE);
                            }
                        }else{
                            viewHolder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                            viewHolder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
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
        viewHolder = null;
        try {
            String sid = channel.getSid();
            for(Map.Entry<Integer,Channel> entry: channelList.entrySet()){
                Channel channel1=entry.getValue();
                if(channel1 != null){
                    if(channel1.getSid().equals(sid)){
                        viewHolder = holderViewList.get(entry.getKey());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            final Messages messagesObject = channel.getMessages();
            if (messagesObject != null) {
                messagesObject.getLastMessages(1, new CallbackListener<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        try {
                            if (messages.size() > 0) {
                                String UserMessages = messages.get(0).getMessageBody().toString();
                                viewHolder.textView1.setText(UserMessages);
                                viewHolder.textTime.setText(Utils.setTime(messages.get(0).getDateCreatedAsDate()));
                                viewHolder.textView1.setVisibility(View.VISIBLE);
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
                                viewHolder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                viewHolder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                            }else{
                                viewHolder.textUnconsumedMessageCount.setText(""+String.valueOf(aLong));
                                viewHolder.textUnconsumedMessageCount.setVisibility(View.VISIBLE);
                            }
                        }else{
                            viewHolder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                            viewHolder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
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
        viewHolder = null;
        try {
            String channelId = channel.getSid();
            for(Map.Entry<Integer,Channel> entry: channelList.entrySet()){
                Channel channel1=entry.getValue();
                if(channel1.getSid().equals(channelId)){
                    channelList.put(entry.getKey(),null);
                    viewHolder = holderViewList.get(entry.getKey());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            viewHolder.textView1.setText(Utils.getStringResource(R.string.tap_to_start,context));
            viewHolder.textTime.setText("");
            viewHolder.textView1.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            viewHolder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
            viewHolder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
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
