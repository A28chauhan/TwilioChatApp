package com.carematix.twiliochatapp.adapter;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.carematix.twiliochatapp.MainActivity;
import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.architecture.table.ChannelList;
import com.carematix.twiliochatapp.architecture.table.UserAllList;
import com.carematix.twiliochatapp.architecture.table.UserChannelList;
import com.carematix.twiliochatapp.architecture.viewModel.UserChannelListViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserChannelViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserListViewModel;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.listener.OnclickListener;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.twilio.ChannelModel;
import com.carematix.twiliochatapp.twilio.MessageItem;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
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

    PrefManager prefManager;

    Channel channel;
    private Map<String, ChannelModel> channels = new HashMap<String,ChannelModel>();

    public UserListAdapter(Context mContext, ArrayList<UserAllList> arrayList, OnclickListener onclickListener,Map<String, ChannelModel> channelList){
        this.context=mContext;
        this.arrayList = arrayList;
        this.onclickListener =onclickListener;
        this.channels=channelList;
        prefManager=new PrefManager(context);
        try {
            userListviewModel =new ViewModelProvider((MainActivity)mContext).get(UserListViewModel.class);
            userChannelViewModel = new ViewModelProvider((MainActivity)mContext).get(UserChannelViewModel.class);
            userChannelListViewModel = new ViewModelProvider((MainActivity)mContext).get(UserChannelListViewModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addItem(List<UserAllList> arrayList1){
        try {
            arrayList = arrayList1;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addItem(Map<String, ChannelModel> channels1){
        try {
            //channels.clear();
            this.channels = channels1;
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(arrayList.size() > 0){
            return arrayList.size();
        }
        return 0;
    }

    int attendeeProgramId=0;
    ViewHolder mHolder=null;
    List<MessageItem> messageItemList = new ArrayList<>();
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // holder.textView.setT
        this.mHolder = holder;
        String name = arrayList.get(position).getFirstName();
        String lastName = arrayList.get(position).getLastName();
        holder.textView.setText(name+" "+lastName);
        attendeeProgramId = arrayList.get(position).getDroProgramUserId();
        try {
            getConsumedDataSet(attendeeProgramId,holder,position);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    String sid="",uSid="",ss="";
    Channel cc;
    String programUserID="",attendeeProgramUserID="";
    String name="";
    public void getConsumedDataSet(final int attendeeProgramId,final ViewHolder holder,int position){
        name ="";
        if(arrayList.size() > 0){
            name = arrayList.get(position).getFirstName();
        }

        programUserID = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
        attendeeProgramUserID= String.valueOf(attendeeProgramId);
        userChannelViewModel.getChannelDetails(programUserID,attendeeProgramUserID).observe((MainActivity)context,channelLists -> {
            if(channelLists.size() > 0){
                ChannelList channelList=channelLists.get(channelLists.size()-1);
                if(channelList != null)
                if(channels.size() > 0){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){

                        Map<String, ChannelModel> result = new HashMap<String,ChannelModel>();
                        result=  channels.entrySet()
                                .stream().filter(  channelModel -> channelModel.getValue().getSid().equals(channelList.getChannelSid()))
                                .map(Map.Entry::getValue)
                                .collect(Collectors.toMap(ChannelModel::getSid, channelModel -> channelModel));

                        for(final ChannelModel channelModel: result.values()){
                            Logs.d("onclick1","channel name : "+channelModel.getSid() +" : channel "+channelModel.getSid()+" "+channelModel.getFriendlyName());
                            // Logs.d("onclick2","channel name : "+channelModel.getSid() +" : channel "+channelList.getChannelSid()+" "+channelList.getOrganizerProgramUserId())+" "+channelList.getAttendeeProgramUserId());
                            if(channelModel != null){
                                if(channelModel.getSid() != null)
                                    if(channelModel.getStatus() == Channel.ChannelStatus.JOINED){
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                channelModel.getChannel(new CallbackListener<Channel>() {
                                                    @Override
                                                    public void onSuccess(Channel channel1) {
                                                        cc= channel1;
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
                                                                            messageItemList.clear();
                                                                            Members members = cc.getMembers();
                                                                            if (messages.size() > 0) {
                                                                                messageItemList.add(new MessageItem(messages.get(0), members, name));
                                                                                MessageItem messageItem=messageItemList.get(0);
                                                                                holder.textView1.setText(messageItem.getMessage().getMessageBody().toString());
                                                                                holder.textTime.setText(Utils.setTime(messageItem.getMessage().getDateCreatedAsDate()));
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
                                                });
                                            }
                                        },0);
                                    }else{
                                        // channelModel.join(new ToastStatusListener("Successfully joined channel","Failed to join channel"){});
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                channelModel.getChannel(new CallbackListener<Channel>() {
                                                    @Override
                                                    public void onSuccess(Channel channel1) {

                                                        cc= channel1;
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
                                                                            messageItemList.clear();
                                                                            Members members = cc.getMembers();
                                                                            if (messages.size() > 0) {
                                                                                messageItemList.add(new MessageItem(messages.get(0), members, name));
                                                                                MessageItem messageItem=messageItemList.get(0);
                                                                                holder.textView1.setText(messageItem.getMessage().getMessageBody().toString());
                                                                                holder.textTime.setText(Utils.setTime(messageItem.getMessage().getDateCreatedAsDate()));
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
                                                });
                                            }
                                        },100);
                                    }
                            }
                        }
                    }else{

                        for(final ChannelModel channelModel: channels.values()){
                            Logs.d("onclick","channel name : "+channelModel +" : channel"+channelModel.getSid()+" "+channelModel.getFriendlyName() +" "+channelList.getChannelSid());

                            if(channelModel != null){
                                if(channelModel.getSid() != null)
                                    if(channelModel.getSid().equals(channelList.getChannelSid())){
                                        if(channelModel.getStatus() == Channel.ChannelStatus.JOINED){
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    channelModel.getChannel(new CallbackListener<Channel>() {
                                                        @Override
                                                        public void onSuccess(Channel channel1) {
                                                            cc= channel1;
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
                                                                                messageItemList.clear();
                                                                                Members members = cc.getMembers();
                                                                                if (messages.size() > 0) {
                                                                                    messageItemList.add(new MessageItem(messages.get(0), members, name));
                                                                                    MessageItem messageItem=messageItemList.get(0);
                                                                                    holder.textView1.setText(messageItem.getMessage().getMessageBody().toString());
                                                                                    holder.textTime.setText(Utils.setTime(messageItem.getMessage().getDateCreatedAsDate()));
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
                                                    });
                                                }
                                            },0);
                                        }
                                        break;
                                    }
                            }

                        }
                    }
                }

            }
        });


    }

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
                    int attendeeProgramUserId = arrayList.get(pos).getDroProgramUserId();
                    String programUserId= prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
                   // String type = arrayList.get(pos).getUserType();
                    onclickListener.onClick(attendeeProgramUserId,programUserId,pos,channels,name);
                    //context.finish();
                }
            });

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    int attendeeProgramUserId = arrayList.get(pos).getDroProgramUserId();
                    String programUserId= prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
                    String name = arrayList.get(pos).getFirstName();

                   // String name = arrayList.get(pos).getFirstName();
                   // String type = arrayList.get(pos).getUserType();
                    onclickListener.onClick(attendeeProgramUserId,programUserId,pos,channels,name);
                    //context.finish();
                }
            });

            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos=getAdapterPosition();
                    String name = arrayList.get(pos).getFirstName();

                    int attendeeProgramUserId = arrayList.get(pos).getDroProgramUserId();
                    String programUserId= prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);

                    // String name = arrayList.get(pos).getFirstName();
                    onclickListener.onLongClickListener(attendeeProgramUserId,programUserId,channels,name);
                    return false;
                }
            });

            constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos=getAdapterPosition();
                    String name = arrayList.get(pos).getFirstName();

                    int attendeeProgramUserId = arrayList.get(pos).getDroProgramUserId();
                    String programUserId= prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);

                    onclickListener.onLongClickListener(attendeeProgramUserId,programUserId,channels,name);
                    return false;
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
