package com.carematix.twiliochatapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.carematix.twiliochatapp.MainActivity;
import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.application.ChatClientManager;
import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.architecture.table.ChannelList;
import com.carematix.twiliochatapp.architecture.table.UserAllList;
import com.carematix.twiliochatapp.architecture.table.UserChannelList;
import com.carematix.twiliochatapp.architecture.viewModel.UserChannelListViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserChannelViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserListViewModel;
import com.carematix.twiliochatapp.fetchchannel.bean.FetchChannelResult;
import com.carematix.twiliochatapp.fetchchannel.bean.FetchChannelView;
import com.carematix.twiliochatapp.fetchchannel.viewmodel.FetchChannelViewModel;
import com.carematix.twiliochatapp.fetchchannel.viewmodel.FetchChannelViewModelFactory;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.listener.OnclickListener;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.twilio.ChannelManager;
import com.carematix.twiliochatapp.twilio.ChannelModel;
import com.carematix.twiliochatapp.twilio.MessageItem;
import com.carematix.twiliochatapp.ui.login.LoginViewModelFactory;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.Channels;
import com.twilio.chat.Members;
import com.twilio.chat.Message;
import com.twilio.chat.Messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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

    public UserListAdapter(Context mContext, ArrayList<UserAllList> arrayList, OnclickListener onclickListener,Channel channels){
        this.context=mContext;
        this.arrayList = arrayList;
        this.onclickListener =onclickListener;
        this.channel=channels;
        prefManager=new PrefManager(context);

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

    }

    public void addItem(List<UserAllList> arrayList1){
        try {
            arrayList = arrayList1;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addItem(Channel channels1){
        try {
            this.channel = channels1;
            //notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(arrayList.size()>0){
            return arrayList.size();
        }
        return 0;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String name = arrayList.get(position).getFirstName();
        String lastName = arrayList.get(position).getLastName();
        holder.textView.setText(name+" "+lastName);
        //int attendeeProgramId = arrayList.get(position).getDroProgramUserId();
        holder.textView1.setText("Tap to start chat");
        holder.textView1.setVisibility(View.VISIBLE);
        try {
            fetchChannelDetails(holder);
           // getConsumedDataSet(holder,position);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void fetchChannelDetails(ViewHolder holder){

        Logs.d(" fetchChannelDetails ","fetchChannelDetails call"+holder.getAdapterPosition());
        int attendeeProgramUserID =arrayList.get(holder.getAdapterPosition()).getDroProgramUserId();
        String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);

        fetchChannelViewModel.fetchChannel(String.valueOf(attendeeProgramUserID),programUserId);

        fetchChannelViewModel.getFetchChannelResult().observe(((MainActivity)context), new Observer<FetchChannelResult>() {
            @Override
            public void onChanged(FetchChannelResult fetchChannelResult) {
                if (fetchChannelResult == null) {
                    return;
                }

                /*if (fetchChannelResult.getError() == null || fetchChannelResult.getErrorMsg() == null) {
                    holder.textView1.setText("Tap to start chat");
                    holder.textView1.setVisibility(View.VISIBLE);
                }*/

                if (fetchChannelResult.getSuccess() != null) {
                    //getConsumedDataSet(holder,loginResult.getSuccess());
                    updateUiWithUser(holder,fetchChannelResult.getSuccess(),attendeeProgramUserID);
                }
            }
        });



    }

    public void updateUiWithUser(ViewHolder holder, FetchChannelView fetchChannelView,int attendeeProgramUserID){

        String channelId = fetchChannelView.getUserResultResponse().body().getData().getChannelSid();
        try {
            if (chatClientManager == null || chatClientManager.getChatClient() == null) return;

            Logs.d("get channel call","channel call details.");
            if (channelsObject == null){
                channelsObject = chatClientManager.getChatClient().getChannels();
            }


            channelsObject.getChannel(channelId, new CallbackListener<Channel>() {
                @Override
                public void onSuccess(Channel channels) {
                    channelList.put(attendeeProgramUserID,channels);
                    setChannel(channels,holder);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setChannel(Channel channels,ViewHolder holder){
        channel =channels;
        channel.getUnconsumedMessagesCount(new CallbackListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                try {
                    if(aLong != null){
                        if(aLong == 0){
                            holder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                            holder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                        }else{
                            holder.textUnconsumedMessageCount.setText(""+String.valueOf(aLong));
                            holder.textUnconsumedMessageCount.setVisibility(View.VISIBLE);
                            getMessages(channels,holder);
                        }
                    }else{
                        holder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                        holder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    holder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                    holder.textUnconsumedMessageCount.setVisibility(View.GONE);
                }

            }
        });

        getMessages(channels,holder);



    }

    public void getMessages(Channel channel,ViewHolder holder){
        try {
            final Messages messagesObject = channel.getMessages();
            if (messagesObject != null) {
                messagesObject.getLastMessages(1, new CallbackListener<List<Message>>() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        try {
                            if (messages.size() > 0) {
                                holder.textView1.setText(messages.get(0).getMessageBody().toString());
                                holder.textTime.setText(Utils.setTime(messages.get(0).getDateCreatedAsDate()));
                                holder.textView1.setVisibility(View.VISIBLE);

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
    }
    String sid="",uSid="",ss="";
    Channel cc;
    String programUserID="",attendeeProgramUserID="";
    //String name="";

   // public void getConsumedDataSet(final int attendeeProgramId,final ViewHolder holder,int position){
   /* public void getConsumedDataSet(final ViewHolder holder,int position){
        //
        int programUserId = arrayList.get(position).getDroProgramUserId();

        if(channel != null){
            if(channel.getSid() != null)
                if(channel.getStatus() == Channel.ChannelStatus.JOINED){
                    try {
                        cc =channel;
                        cc.getUnconsumedMessagesCount(new CallbackListener<Long>() {
                            @Override
                            public void onSuccess(Long aLong) {
                                try {
                                    if(aLong != null){
                                        if(aLong == 0){
                                            holder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                            holder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                                        }else{
                                            holder.textUnconsumedMessageCount.setText(""+String.valueOf(aLong));
                                            holder.textUnconsumedMessageCount.setVisibility(View.VISIBLE);
                                        }
                                    }else{
                                        holder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                        holder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    holder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                    holder.textUnconsumedMessageCount.setVisibility(View.GONE);
                                }

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        final Messages messagesObject = cc.getMessages();
                        if (messagesObject != null) {
                            messagesObject.getLastMessages(1, new CallbackListener<List<Message>>() {
                                @Override
                                public void onSuccess(List<Message> messages) {
                                    try {
                                        if (messages.size() > 0) {
                                            holder.textView1.setText(messages.get(0).getMessageBody().toString());
                                            holder.textTime.setText(Utils.setTime(messages.get(0).getDateCreatedAsDate()));
                                            holder.textView1.setVisibility(View.VISIBLE);

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
                }else{
                    cc= channel;
                    try {
                        cc.getUnconsumedMessagesCount(new CallbackListener<Long>() {
                            @Override
                            public void onSuccess(Long aLong) {
                                try {
                                    if(aLong != null){
                                        if(aLong == 0){
                                            holder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                            holder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                                        }else{
                                            holder.textUnconsumedMessageCount.setText(""+String.valueOf(aLong));
                                            holder.textUnconsumedMessageCount.setVisibility(View.VISIBLE);
                                        }
                                    }else{
                                        holder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                        holder.textUnconsumedMessageCount.setVisibility(View.INVISIBLE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    holder.textUnconsumedMessageCount.setText(""+String.valueOf("0"));
                                    holder.textUnconsumedMessageCount.setVisibility(View.GONE);
                                }

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        final Messages messagesObject = cc.getMessages();
                        if (messagesObject != null) {
                            messagesObject.getLastMessages(1, new CallbackListener<List<Message>>() {
                                @Override
                                public void onSuccess(List<Message> messages) {
                                    try {
                                        if (messages.size() > 0) {
                                            holder.textView1.setText(messages.get(0).getMessageBody().toString());
                                            holder.textTime.setText(Utils.setTime(messages.get(0).getDateCreatedAsDate()));
                                            holder.textView1.setVisibility(View.VISIBLE);

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

                }
        }
    }*/

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(itemView);
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

                    String name = arrayList.get(pos).getFirstName();
                    String lastName = arrayList.get(pos).getLastName();
                    int attendeeProgramUserId = arrayList.get(pos).getDroProgramUserId();
                    String programUserId= prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
                    if(channelList.size()>0){
                        channel = channelList.get(pos);
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
                            int id=entry.getKey();
                            if(id == attendeeProgramUserId){
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

}
