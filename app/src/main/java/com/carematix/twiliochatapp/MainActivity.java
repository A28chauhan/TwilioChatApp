package com.carematix.twiliochatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.carematix.twiliochatapp.adapter.UserListAdapter;
import com.carematix.twiliochatapp.application.ChatClientManager;
import com.carematix.twiliochatapp.application.SessionManager;
import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.architecture.table.ChannelList;
import com.carematix.twiliochatapp.architecture.table.UserAllList;
import com.carematix.twiliochatapp.architecture.table.UserChannelList;
import com.carematix.twiliochatapp.architecture.viewModel.UserChannelListViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserChannelViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserChatViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserListViewModel;
import com.carematix.twiliochatapp.bean.fetchChannel.ChannelDetails;
import com.carematix.twiliochatapp.bean.userList.Data;
import com.carematix.twiliochatapp.bean.userList.User;
import com.carematix.twiliochatapp.bean.userList.UserDetails;
import com.carematix.twiliochatapp.helper.Constants;
import com.carematix.twiliochatapp.helper.FCMPreferences;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.listener.OnDialogInterfaceListener;
import com.carematix.twiliochatapp.listener.OnclickListener;
import com.carematix.twiliochatapp.listener.TaskCompletionListener;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.restapi.ApiClient;
import com.carematix.twiliochatapp.restapi.ApiInterface;
import com.carematix.twiliochatapp.twilio.ChannelManager;
import com.carematix.twiliochatapp.twilio.ToastStatusListener;
import com.carematix.twiliochatapp.ui.login.LoginActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carematix.twiliochatapp.databinding.ActivityMainBinding;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.Channels;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ChatClientListener, OnclickListener {

    private ActivityMainBinding binding;
    public PrefManager prefManager;

    RecyclerView mRecyclerView;
    private ChatClientManager chatClientManager;

    Channels channelsObject;
    private ChannelManager channelManager;
    Channel channel=null;

    private static MainActivity mainActivity = null;
    SharedPreferences sharedPreferences=null;

    public MainActivity(){
        mainActivity = MainActivity.this;
    }

    public static OnDialogInterfaceListener onDialogInterfaceListener=new OnDialogInterfaceListener() {
        @Override
        public void onSuccess(String channelId) {
            mainActivity.onClickSyncListener.onSyncSuccess(channelId);
        }
    };

    OnClickSyncListener onClickSyncListener = new OnClickSyncListener() {
        @Override
        public void onSyncSuccess(String channelId) {
            loadLastMessagesIndex(channelId);
        }
    };
    public void loadLastMessagesIndex(String channelId){
           getChannels(channelId);
    }

    public interface OnClickSyncListener {
        void onSyncSuccess(String channelId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("mymessages");
            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


        prefManager =new PrefManager(this);
        prefManager.setStringValue(PrefConstants.WHICH_SCREEN,"main");
        prefManager.setBooleanValue(PrefConstants.PREFERENCE_LOGIN_CHECK,true);
        prefManager.setBooleanValue(PrefConstants.SCREEN,false);

        try {
            channelManager = ChannelManager.getInstance();
            checkTwilioClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setData(){
        try {
            boolean vv= prefManager.getBooleanValue(PrefConstants.SPLASH_ACTIVE_SERVICE);
            if(vv){
                getAllUserListCall();
                prefManager.setBooleanValue(PrefConstants.SPLASH_ACTIVE_SERVICE,false);
            }else{
                stopActivityIndicator();
                /*allViewModel.getAllList().observe(this,userAllLists -> {
                    if(userAllLists.size() > 0){
                        for(UserAllList userAllList : userAllLists){

                           // getActiveChannelList(userAllList.getDroProgramUserId());
                        }
                    }
                });*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(int attendeeProgramUserId, String programUserId, int pos, final Channel channels,String UserName) {
        try {
            String name = UserName;
            String type1= String.valueOf(attendeeProgramUserId);

            if(channels !=  null){
                if(channels.getSid() != null){
                    if(channels.getStatus() == Channel.ChannelStatus.JOINED){

                        callActivity(channels,name,type1);
                    }else{
                        channels.join(new ToastStatusListener("Successfully joined channel","Failed to join channel"){
                            @Override
                            public void onSuccess() {
                                //super.onSuccess();
                                callActivity(channels,name,type1);
                            }

                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                super.onError(errorInfo);
                            }
                        });

                    }
                }else{
                    fetchChannel(attendeeProgramUserId,programUserId,UserName);
                }


            }else{
                fetchChannel(attendeeProgramUserId,programUserId,UserName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showToast("Channel not found",this);
        }

    }

    public void fetchChannel(int attendeeProgramUserId,String programUserId,String userName){
        showActivityIndicator(null);
        apiServiceUser=null;
        apiServiceUser = ApiClient.getClient1().create(ApiInterface.class);
        Call<ChannelDetails> call = apiServiceUser.fetchChannel(programUserId,String.valueOf(attendeeProgramUserId), Constants.X_DRO_SOURCE);
        call.enqueue(new Callback<ChannelDetails>() {
            @Override
            public void onResponse(Call<ChannelDetails> call, Response<ChannelDetails> response) {
                try {
                    int code = response.code();
                    if (code == 200) {
                        getChannelDetails(response,attendeeProgramUserId,userName);
                    }else{
                        stopActivityIndicator();
                        Utils.showToast("Please try again",MainActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ChannelDetails> call, Throwable t) {
                stopActivityIndicator();
                Utils.showToast("Please try again",MainActivity.this);
            }
        });
    }

    public void getChannelDetails(Response<ChannelDetails> response,int attendProgramUserId,String userName){
        com.carematix.twiliochatapp.bean.fetchChannel.Data data=null;
        try {
            data=  response.body().getData();
            Logs.d("call channel ","call channel list");
            if(!data.getChannelSid().equals("")){
                getChannelsDetails(data.getChannelSid(),attendProgramUserId,userName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getChannelsDetails(String sid,int attendProgramUserId,String userName){
        try {
            //if (channelsObject == null) return;
            if (chatClientManager == null || chatClientManager.getChatClient() == null) return;

            Logs.d("get channel call","channel call details.");
            if (channelsObject == null){
                channelsObject = chatClientManager.getChatClient().getChannels();
            }

            channelsObject.getChannel(sid, new CallbackListener<Channel>() {
                @Override
                public void onSuccess(Channel channel) {
                    getSingleChannelDetails(channel,attendProgramUserId,userName);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getSingleChannelDetails(Channel channel,int attendProgramUserId,String userName){
        try {
            stopActivityIndicator();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(channel.getStatus() == Channel.ChannelStatus.JOINED){

            callActivity(channel,userName,String.valueOf(attendProgramUserId));
        }else{
            channel.join(new ToastStatusListener("Successfully joined channel","Failed to join channel"){
                @Override
                public void onSuccess() {
                    //super.onSuccess();
                    callActivity(channel,userName,String.valueOf(attendProgramUserId));
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    super.onError(errorInfo);
                }
            });

        }

    }

    public void callActivity(Channel channels1,String name,String type1){
        Intent intent =new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra(Constants.EXTRA_ID,channels1.getSid());
        intent.putExtra(Constants.EXTRA_CHANNEL, (Parcelable)channels1);
        intent.putExtra(Constants.EXTRA_NAME,name);
        intent.putExtra(Constants.EXTRA_TYPE,type1);
        MainActivity.this.startActivity(intent);
        MainActivity.this.finish();
    }

    @Override
    public void onLongClickListener(int attendeeProgramUserId, String programUserId, final Channel channels1,String UserName) {

    }


    public UserListViewModel allViewModel;
    public UserChannelViewModel channelViewModel;
    public UserChannelListViewModel userChannelViewModel;
    public UserChatViewModel userChatViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        allViewModel = new ViewModelProvider(MainActivity.this).get(UserListViewModel.class);
        channelViewModel = new ViewModelProvider(MainActivity.this).get(UserChannelViewModel.class);
        userChannelViewModel = new ViewModelProvider(MainActivity.this).get(UserChannelListViewModel.class);
        userChatViewModel = new ViewModelProvider(MainActivity.this).get(UserChatViewModel.class);

        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onResume() {
        try {
            setupListView();
            getUserListDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    public void getUserListDetails(){
        allViewModel.getAllList().observe(this,userAllLists -> {
            if(userAllLists.size() > 0){
                userListAdapter.addItem(userAllLists);
            }
        });
    }

    UserListAdapter userListAdapter;
    ArrayList<UserAllList> arrayList=new ArrayList<>();
    public void setupListView(){
        mRecyclerView = binding.recyclerView;
        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        try {

            userListAdapter=new UserListAdapter(this,arrayList,this,channel);
            mRecyclerView.setAdapter(userListAdapter);
            userListAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    ApiInterface apiServiceUser=null;
    public void getAllUserListCall(){

        apiServiceUser =null;
        String roleId = prefManager.getStringValue(PrefConstants.TWILIO_ROLE_ID);
        String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
        String droOrganizationProgramId =prefManager.getStringValue(PrefConstants.PROGRAM_ID);
        apiServiceUser = ApiClient.getClient1().create(ApiInterface.class);
        Call<UserDetails> call = apiServiceUser.getUserList(Integer.parseInt(programUserId),Integer.parseInt(roleId), Constants.X_DRO_SOURCE,droOrganizationProgramId);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                try {
                    int code = response.code();
                    if (code == 200) {
                        setAllData(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    stopActivityIndicator();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                stopActivityIndicator();
            }
        });

    }

    public void setAllData(Response<UserDetails> response){

        if(response.body().getCode() == 200){
            binding.noData.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            try {
                allViewModel.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                channelViewModel.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Data data=  response.body().getData();
                List<User> userList=data.getUsers();
                for(User user : userList){
                    UserAllList userAllList=new UserAllList(user.getDroUserId(),user.getDroUserRoleId(),
                            user.getFirstName(),user.getLastName(),user.getDroProgramUserId());
                    allViewModel.insert(userAllList);
                   // getActiveChannelList(user.getDroProgramUserId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
                binding.noData.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
        }

        try {
            setFCMToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            stopActivityIndicator();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setAllChannel(Response<ChannelDetails> response,int attendProgramUserId){
        String sid="";
        try {
            String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
            com.carematix.twiliochatapp.bean.fetchChannel.Data data=  response.body().getData();
            sid=response.body().getData().getChannelSid();

            if(sid != null){
                if(!(sid.equals(""))){
                    ChannelList channelList=new ChannelList(programUserId,String.valueOf(attendProgramUserId),sid);
                    channelViewModel.insert(channelList);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            sid="";
        }

        try {
            Logs.d("call channel ","call channel list");
            if(!sid.equals("")){
                getChannels(sid);
            }else{
                stopActivityIndicator();
            }

        } catch (Exception e) {
            e.printStackTrace();
            stopActivityIndicator();
        }

    }

    public void getActiveChannelList(final int attendeeProgramUserId){

        apiServiceUser=null;
        String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
        apiServiceUser = ApiClient.getClient1().create(ApiInterface.class);
        Call<ChannelDetails> call = apiServiceUser.activeChannel(programUserId,String.valueOf(attendeeProgramUserId), Constants.X_DRO_SOURCE);
        call.enqueue(new Callback<ChannelDetails>() {
            @Override
            public void onResponse(Call<ChannelDetails> call, Response<ChannelDetails> response) {
                try {
                    int code = response.code();
                    if (code == 200) {
                        setAllChannel(response,attendeeProgramUserId);
                    }else{
                        stopActivityIndicator();
                        showException(String.valueOf(code));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ChannelDetails> call, Throwable t) {
                stopActivityIndicator();
                showException(t.getMessage().toString());
            }
        });


    }

    public void showException(String msg){

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(msg)
                    .setTitle(R.string.Error);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        dialog.dismiss();
                        setData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void checkTwilioClient(){
        try {
            showActivityIndicator(getStringResource(R.string.loading_channels_message));
            chatClientManager = TwilioApplication.get().getChatClientManager();
            if (chatClientManager.getChatClient() == null) {
                initializeClient();
            } else {
                setData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getStringResource(int id) {
        Resources resources = getResources();
        return resources.getString(id);
    }

    private void initializeClient() {
        chatClientManager.connectClient(new TaskCompletionListener<Void, String>() {
            @Override
            public void onSuccess(Void aVoid) {
                //getChannels();
                setData();
                //stopActivityIndicator();
            }

            @Override
            public void onError(String errorMessage) {
                stopActivityIndicator();
            }
        });
    }


    public void getChannels(String sid){
        //  stopActivityIndicator();

        try {
            //if (channelsObject == null) return;
            if (chatClientManager == null || chatClientManager.getChatClient() == null) return;

            Logs.d("get channel call","channel call details.");
            if (channelsObject == null){
                channelsObject = chatClientManager.getChatClient().getChannels();
            }


            channelsObject.getChannel(sid, new CallbackListener<Channel>() {
                @Override
                public void onSuccess(Channel channel) {
                    refreshChannel(channel);
                }
            });

            channelManager.setChannelListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setFCMToken(){
        try {
            sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
            String fcmToken = sharedPreferences.getString(FCMPreferences.TOKEN_NAME,null);
            TwilioApplication.get().getChatClientManager().setFCMToken(fcmToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshChannel(Channel channel){

        try {
            userChannelViewModel.insert(new UserChannelList(channel.getSid(),channel.getFriendlyName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logs.d("adapter call","channels: "+channel.getSid()+" Name :"+channel.getFriendlyName());
                userListAdapter.addItem(channel);
                userListAdapter.notifyDataSetChanged();
            }
        });

        try {
            setFCMToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            stopActivityIndicator();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public ProgressDialog progressDialog;
    private void showActivityIndicator(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(MainActivity.this,R.style.MyDialog);
                progressDialog.setMessage(getStringResource(R.string.data_load));
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
            }
        });
    }
    private void stopActivityIndicator() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }


    AlertDialog incomingChannelInvite;
    private void showIncomingInvite(final Channel channel){
        new Handler().post(new Runnable() {
            @Override
            public void run()
            {
                if (incomingChannelInvite == null) {
                    incomingChannelInvite =
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(R.string.channel_invite)
                                    .setMessage(R.string.channel_invite_message)
                                    .setPositiveButton(
                                            R.string.join,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    channel.join(new ToastStatusListener(
                                                            "Successfully joined channel",
                                                            "Failed to join channel") {
                                                        @Override
                                                        public void onSuccess()
                                                        {
                                                            super.onSuccess();
                                                            //channels.put(channel.getSid(), new ChannelModel(channel));
                                                            refreshChannel(channel);
                                                        }
                                                    });
                                                    incomingChannelInvite = null;
                                                }
                                            })
                                    .setNegativeButton(
                                            R.string.decline,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which)
                                                {
                                                    channel.declineInvitation(new ToastStatusListener(
                                                            "Successfully declined channel invite",
                                                            "Failed to decline channel invite") {
                                                        @Override
                                                        public void onSuccess()
                                                        {
                                                            super.onSuccess();
                                                        }
                                                    });
                                                    incomingChannelInvite = null;
                                                }
                                            })
                                    .create();
                }
                incomingChannelInvite.show();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_logout){
            alertLogout();
            return true;
        }else if(id == R.id.action_create){
            return true;
        }else{

        }
        return super.onOptionsItemSelected(item);
    }

    public void alertLogout(){

        prefManager.setBooleanValue(PrefConstants.IS_FIRST_TIME_LOGIN,false);
        prefManager.setBooleanValue(PrefConstants.PREFERENCE_LOGIN_CHECK,false);

        try {
            SessionManager.getInstance().logoutUser();
        } catch (Exception e) {
            e.printStackTrace();
        }

        prefManager.clearPref();

        deleteAllDb();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        MainActivity.this.finish();

    }

    public void deleteAllDb(){
        try {
            allViewModel.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            channelViewModel.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            userChannelViewModel.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            userChatViewModel.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChannelJoined(Channel channel) {
        Logs.d("JoinChannel ","Received onChannelJoined callback for channel |" + channel.getFriendlyName() + "|");
        refreshChannel(channel);
    }
    @Override
    public void onChannelInvited(Channel channel) {
        Logs.d("JoinChannel ","Received onChannelInvited callback for channel |" + channel.getFriendlyName() + "|");
        refreshChannel(channel);
        showIncomingInvite(channel);
    }
    @Override
    public void onChannelAdded(Channel channel) {
        try {
            Logs.d("ChannelAdded ","Received onChannelJoined callback for channel |" + channel.getFriendlyName() + "|");
            getChannels(channel.getSid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {
        Logs.d("ChannelUpdated ","Received onChannelJoined callback for channel |" + channel.getFriendlyName() + "|");
        getChannels(channel.getSid());
    }
    @Override
    public void onChannelDeleted(Channel channel) {
        try {
            Logs.d("ChannelDeleted ","Received onChannelJoined callback for channel |" + channel.getFriendlyName() + "|");
            setData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onChannelSynchronizationChange(Channel channel) {
        Logs.d("ChannelSyncChange ","onChannelSynchronizationChange |" + channel.getFriendlyName() + "|");
        getChannels(channel.getSid());
    }
    @Override
    public void onError(ErrorInfo errorInfo) {
        TwilioApplication.get().showToast("Received onError : " , Toast.LENGTH_LONG);
    }
    @Override
    public void onUserUpdated(com.twilio.chat.User user, com.twilio.chat.User.UpdateReason updateReason) {
        Logs.d("UserUpdated ","onUserUpdated |" +updateReason.name() + "|");

    }
    @Override
    public void onUserSubscribed(com.twilio.chat.User user) {
        Logs.d("onUserSubscribed ","onUserSubscribed |" +user.getFriendlyName() + "|");
    }
    @Override
    public void onUserUnsubscribed(com.twilio.chat.User user) {
        Logs.d("onUserUnsubscribed ","onUserUnsubscribed |" +user.getFriendlyName() + "|");
    }
    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {
        Logs.d("ChannelSync","Received onChannelJoined callback for channel |" + synchronizationStatus.getValue() + "|");
    }
    @Override
    public void onNewMessageNotification(String s, String s1, long l) {
        TwilioApplication.get().showToast("Received onNewMessage push notification : " , Toast.LENGTH_LONG);
    }
    @Override
    public void onAddedToChannelNotification(String s) {
        TwilioApplication.get().showToast("Received onAddedToChannel push notification : " , Toast.LENGTH_LONG);
    }
    @Override
    public void onInvitedToChannelNotification(String s) {
        TwilioApplication.get().showToast("Received onNewMessage push notification : " , Toast.LENGTH_LONG);
    }
    @Override
    public void onRemovedFromChannelNotification(String s) {
        TwilioApplication.get().showToast("Received onRemovedFromChannel push notification : " , Toast.LENGTH_LONG);
    }
    @Override
    public void onNotificationSubscribed() {
        TwilioApplication.get().showToast("Received onNotificationSubscribed push notification : " , Toast.LENGTH_LONG);
    }
    @Override
    public void onNotificationFailed(ErrorInfo errorInfo) {
        TwilioApplication.get().showToast("Received onNotificationFailed push notification : " , Toast.LENGTH_LONG);
    }
    @Override
    public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {
        TwilioApplication.get().showToast("Received onConnectionStateChange push notification : " , Toast.LENGTH_LONG);
    }
    @Override
    public void onTokenExpired() {
    }
    @Override
    public void onTokenAboutToExpire() {
    }

    @Override
    public void onBackPressed() {
        MainActivity.this.finish();
    }

}
