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
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnclickListener {

    private ActivityMainBinding binding;
    public PrefManager prefManager;

    RecyclerView mRecyclerView;
    private ChatClientManager chatClientManager;

    Channels channelsObject;
    SharedPreferences sharedPreferences=null;

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

        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        prefManager =new PrefManager(this);
        prefManager.setBooleanValue(PrefConstants.PREFERENCE_LOGIN_CHECK,true);
        prefManager.setBooleanValue(PrefConstants.SCREEN,false);


    }

    public void setData(){
        try {
            setupListView();
            getAllUserListCall();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(UserAllList userAllList, final Channel channels,String UserName) {
        try {
            String name = UserName;
            String attendeeId= String.valueOf(userAllList.getDroProgramUserId());
            prefManager =new PrefManager(MainActivity.this);
            if(channels !=  null){
                Logs.d("onCLick ", " onclicjk :"+channels.getStatus());
                if(channels.getSid() != null){
                    if(channels.getStatus() == Channel.ChannelStatus.JOINED){
                        callActivity(channels,name,attendeeId);
                    }else{
                        channels.join(new ToastStatusListener("Successfully joined channel","Failed to join channel"){
                            @Override
                            public void onSuccess() {
                                //super.onSuccess();
                                callActivity(channels,name,attendeeId);
                            }
                            @Override
                            public void onError(ErrorInfo errorInfo) {
                                super.onError(errorInfo);
                                if(channels.getStatus() == Channel.ChannelStatus.NOT_PARTICIPATING){

                                    fetchChannel(userAllList.getDroProgramUserId(),prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID),UserName);
                                }
                            }
                        });
                    }

                }else{
                    fetchChannel(userAllList.getDroProgramUserId(),prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID),UserName);
                }


            }else{
                fetchChannel(userAllList.getDroProgramUserId(),prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID),UserName);
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
            if(!data.getChannelSid().equals("")){
                getChannelsDetails(data.getChannelSid(),attendProgramUserId,userName);
            }else{
                Utils.showToast("Channel not found.",MainActivity.this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getChannelsDetails(String sid,int attendProgramUserId,String userName){
        try {
            if (chatClientManager == null || chatClientManager.getChatClient() == null) return;

            if (channelsObject == null){
                channelsObject = chatClientManager.getChatClient().getChannels();
            }

            channelsObject.getChannel(sid, new CallbackListener<Channel>() {
                @Override
                public void onSuccess(Channel channel) {
                    getSingleChannelDetails(channel,attendProgramUserId,userName);
                }

                @Override
                public void onError(ErrorInfo errorInfo) {
                    super.onError(errorInfo);
                    stopActivityIndicator();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getSingleChannelDetails(Channel channel,int attendProgramUserId,String userName){

        stopActivityIndicator();

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
                    Logs.d("errorInfo "," errorInfo : "+errorInfo.getMessage());
                    super.onError(errorInfo);
                }
            });

        }

    }

    public void callActivity(Channel channels1,String userName,String attendeeId){
        Intent intent =new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra(Constants.EXTRA_ID,channels1.getSid());
        intent.putExtra(Constants.EXTRA_CHANNEL, (Parcelable)channels1);
        intent.putExtra(Constants.EXTRA_NAME,userName);
        intent.putExtra(Constants.EXTRA_TYPE,attendeeId);
        MainActivity.this.startActivity(intent);
        MainActivity.this.finish();
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
            checkTwilioClient();
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

    public void addListener(){
        if(chatClientManager != null)
        chatClientManager.getChatClient().addListener(chatClientListener);
    }

    UserListAdapter userListAdapter;
    ArrayList<UserAllList> arrayList=new ArrayList<>();
    public void setupListView(){

        if(chatClientManager.getChatClient() == null){
            chatClientManager =TwilioApplication.get().getChatClientManager();
        }
        mRecyclerView = binding.recyclerView;
        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        try {

            userListAdapter=new UserListAdapter(this,arrayList,this,chatClientManager);
            mRecyclerView.setAdapter(userListAdapter);
            mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 10);
            mRecyclerView.setItemViewCacheSize(10);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(chatClientManager.getChatClient() == null){
            chatClientManager =TwilioApplication.get().getChatClientManager();
            addListener();
        }else{
            addListener();
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
                channelViewModel.delete();

                Data data=  response.body().getData();
                List<User> userList=data.getUsers();
                for(User user : userList){
                    UserAllList userAllList=new UserAllList(user.getDroUserId(),user.getDroUserRoleId(),
                            user.getFirstName(),user.getLastName(),user.getDroProgramUserId());
                    allViewModel.insert(userAllList);
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
            stopActivityIndicator();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // get UserList in our db
        getUserListDetails();
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


    public String getStringResource(int id) {
        Resources resources = getResources();
        return resources.getString(id);
    }

    private void initializeClient() {
        chatClientManager.connectClient(new TaskCompletionListener<Void, String>() {
            @Override
            public void onSuccess(Void aVoid) {
                setData();
            }

            @Override
            public void onError(String errorMessage) {
                stopActivityIndicator();
            }
        });
    }

    public void setFCMToken(){
        try {
            String fcmToken = sharedPreferences.getString(FCMPreferences.TOKEN_NAME,null);
            TwilioApplication.get().getChatClientManager().setFCMToken(fcmToken);
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
        switch (item.getItemId()){
            case R.id.action_logout:
                alertLogout();
                return true;
            case R.id.action_refresh:
                refreshMenuItem = item;
                refreshAdapter();
                return true;
            default:
                refreshMenuItem = item;
                return super.onOptionsItemSelected(item);
        }

    }

    MenuItem refreshMenuItem;
    public void refreshAdapter(){

        if (Utils.onNetworkChange(this)) {

            try {
                if(refreshMenuItem != null)
                refreshMenuItem.setActionView(R.layout.actionbar_progress);
                refreshMenuItem.expandActionView();

                if(userListAdapter!= null){
                    userListAdapter.notifyDataSetChanged();
                }

                Timer timer=new Timer("timer");
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(refreshMenuItem != null)
                                    refreshMenuItem.collapseActionView();
                                refreshMenuItem.setActionView(null);
                            }
                        });
                    }
                },3000L,3000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Utils.showToast(getStringResource(R.string.internet),this);
        }


    }

    public void alertLogout(){

        try {
            String token = sharedPreferences.getString(FCMPreferences.TOKEN_NAME,null);
            TwilioApplication.get().getChatClientManager().unRegisterFCMToken(token);
            SessionManager.getInstance().logoutUser();
            chatClientManager.getChatClient().shutdown();
            deleteAllDb();
        } catch (Exception e) {
            e.printStackTrace();
        }

        prefManager.clearPref();

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        MainActivity.this.finish();

    }

    public void deleteAllDb(){
        try {
            allViewModel.delete();

            channelViewModel.delete();

            userChannelViewModel.delete();

            userChatViewModel.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //Q2FyZUAxMDE= Q2FyZUAxMTE= Q2FyZUAxMTA=
    public ChatClientListener chatClientListener=new ChatClientListener() {
        @Override
        public void onChannelJoined(Channel channel) {
            Logs.d("adapter onChannelJoined ","onChannelJoined"+channel.getSid());
            if(channel != null){
                userListAdapter.addItemWithChannel(channel,Constants.JOIN);
                userListAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChannelInvited(Channel channel) {
            Logs.d("adapter onChannelInvited ","onChannelInvited"+channel.getSid());
            if(channel != null){
                userListAdapter.addItemWithChannel(channel,Constants.INVITE);
                userListAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChannelAdded(Channel channel) {
            Logs.d("adapter onChannelAdded ","onChannelAdded"+channel.getSid());
        }

        @Override
        public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {
            Logs.d("adapter onChannelUpdated ","onChannelUpdated"+channel.getSid());
        }

        @Override
        public void onChannelDeleted(Channel channel) {
            if(channel != null){
                Logs.d("adapter onChannelDeleted ","onChannelDeleted"+channel.getSid());
                userListAdapter.addItemWithChannel(channel,Constants.DELETE);
            }
        }

        @Override
        public void onChannelSynchronizationChange(Channel channel) {
            Logs.d("adapter onChannelSynchronizationChange ","onChannelSynchronizationChange"+channel.getSid());
        }

        @Override
        public void onError(ErrorInfo errorInfo) {}

        @Override
        public void onUserUpdated(com.twilio.chat.User user, com.twilio.chat.User.UpdateReason updateReason) {}

        @Override
        public void onUserSubscribed(com.twilio.chat.User user) {}

        @Override
        public void onUserUnsubscribed(com.twilio.chat.User user) {}

        @Override
        public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {}

        @Override
        public void onNewMessageNotification(String s, String s1, long l) {}

        @Override
        public void onAddedToChannelNotification(String s) {}

        @Override
        public void onInvitedToChannelNotification(String s) {}

        @Override
        public void onRemovedFromChannelNotification(String s) {}

        @Override
        public void onNotificationSubscribed() {}

        @Override
        public void onNotificationFailed(ErrorInfo errorInfo) {}

        @Override
        public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {}

        @Override
        public void onTokenExpired() {}

        @Override
        public void onTokenAboutToExpire() {}
    };

}
