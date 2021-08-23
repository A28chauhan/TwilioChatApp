package com.carematix.twiliochatapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
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

import com.carematix.twiliochatapp.adapter.LeftChatAdapter;
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
import com.carematix.twiliochatapp.bean.accesstoken.TokenResponse;
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
import com.carematix.twiliochatapp.twilio.ChannelModel;
import com.carematix.twiliochatapp.twilio.CustomChannelComparator;
import com.carematix.twiliochatapp.twilio.ToastStatusListener;
import com.carematix.twiliochatapp.ui.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carematix.twiliochatapp.databinding.ActivityMainBinding;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelDescriptor;
import com.twilio.chat.Channels;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Paginator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ChatClientListener, OnclickListener {

    private ActivityMainBinding binding;
    public PrefManager prefManager;

    public static int val=0;
    RecyclerView mRecyclerView;
    private ChatClientManager chatClientManager;

    private Map<String, ChannelModel> channels = new HashMap<String,ChannelModel>();
    Channels channelsObject;
    private ChannelManager channelManager;

    private static MainActivity mainActivity = null;
    SharedPreferences sharedPreferences=null;

    public MainActivity(){
        mainActivity = MainActivity.this;
    }

    public static OnDialogInterfaceListener onDialogInterfaceListener=new OnDialogInterfaceListener() {
        @Override
        public void onSuccess() {
            mainActivity.onClickSyncListener.onSyncSuccess();
        }
    };

    OnClickSyncListener onClickSyncListener = new OnClickSyncListener() {
        @Override
        public void onSyncSuccess() {
            loadLastMessagesIndex();
        }
    };
    public void loadLastMessagesIndex(){
            getChannels();
    }

    public interface OnClickSyncListener {
        void onSyncSuccess();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("mymessages");

        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            prefManager =new PrefManager(this);
            prefManager.setStringValue(PrefConstants.WHICH_SCREEN,"main");
            prefManager.setBooleanValue(PrefConstants.PREFERENCE_LOGIN_CHECK,true);
            prefManager.setBooleanValue(PrefConstants.SCREEN,false);
            //prefManager.setBooleanValue(PrefConstants.SPLASH_ACTIVE_SERVICE,false);

            FirebaseMessaging.getInstance().setAutoInitEnabled(true);
            //setAnalyticsCollectionEnabled(true);
            LeftChatAdapter.unConsumed = 0;
            val =0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            channelManager = ChannelManager.getInstance();

            String roleId = prefManager.getStringValue(PrefConstants.TWILIO_ROLE_ID);
            ActionBar actionBar =getSupportActionBar();
            if(roleId.equals("1")){
                actionBar.setTitle("Nurse");
            }else{
                actionBar.setTitle("Patient");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
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
                allViewModel.getAllList().observe(this,userAllLists -> {
                    if(userAllLists.size() > 0){
                        //showActivityIndicator(getStringResource(R.string.loading_channels_message));
                        showProgressDialog1();
                        for(UserAllList userAllList : userAllLists){
                            getChannelList(userAllList.getDroProgramUserId());
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            getChannels();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // allViewModel,channelViewModel,userChannelViewModel
    @Override
    public void onClick(int attendeeProgramUserId, String programUserId, int pos, final Map<String, ChannelModel> channels1,String UserName) {

        try {
            String name = UserName;
            String type1= String.valueOf(attendeeProgramUserId);

            channelViewModel.getChannelDetails(programUserId,String.valueOf(attendeeProgramUserId)).observe(this,channelLists -> {
            if(channelLists.size() > 0){
                ChannelList channelList=channelLists.get(channelLists.size()-1);

                Map<String, ChannelModel> result = new HashMap<String, ChannelModel>();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    result = channels1.entrySet()
                            .stream().filter(channelModel -> channelModel.getValue().getSid().equals(channelList.getChannelSid()))
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toMap(ChannelModel::getSid, channelModel -> channelModel));
                }else{
                    // android version is less or less then <= 23
                    for( ChannelModel channelModel: channels1.values()){
                        if(channelModel.getSid().equals(channelList.getChannelSid())){
                            result.putAll(channels1);
                            break;
                        }
                    }
                }
                if(result.values().size() > 0){
                    for(ChannelModel  channelModel: result.values()){
                        if(channelModel.getSid() != null)
                            if(channelModel.getStatus() == Channel.ChannelStatus.JOINED){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        channelModel.getChannel(new CallbackListener<Channel>() {
                                            @Override
                                            public void onSuccess(Channel channel) {
                                                Intent intent =new Intent(MainActivity.this, ChatActivity.class);
                                                intent.putExtra(Constants.EXTRA_ID,channelModel.getSid());
                                                intent.putExtra(Constants.EXTRA_CHANNEL, (Parcelable)channel);
                                                intent.putExtra(Constants.EXTRA_NAME,name);
                                                intent.putExtra(Constants.EXTRA_TYPE,type1);
                                                MainActivity.this.startActivity(intent);
                                                MainActivity.this.finish();

                                            }
                                        });
                                    }
                                },0);
                            }else{
                                channelModel.join(new ToastStatusListener("Successfully joined channel","Failed to join channel"){
                                });
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        channelModel.getChannel(new CallbackListener<Channel>() {
                                            @Override
                                            public void onSuccess(Channel channel) {
                                                Intent intent =new Intent(MainActivity.this, ChatActivity.class);
                                                intent.putExtra(Constants.EXTRA_ID,channelModel.getSid());
                                                intent.putExtra(Constants.EXTRA_CHANNEL, (Parcelable)channel);
                                                intent.putExtra(Constants.EXTRA_NAME,name);
                                                intent.putExtra(Constants.EXTRA_TYPE,type1);
                                                MainActivity.this.startActivity(intent);
                                                MainActivity.this.finish();

                                            }
                                        });
                                    }
                                },100);
                            }
                    }
                }
                else{
                    Utils.showToast("Channel not found",this);
                }

            }else{
                Utils.showToast("Channel not found",this);
            }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showToast("Channel not found",this);
        }

    }

    @Override
    public void onLongClickListener(int attendeeProgramUserId, String programUserId, final Map<String, ChannelModel> channels1,String UserName) {

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
            setData();
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
                //arrayList.addAll(userAllLists);
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

            userListAdapter=new UserListAdapter(this,arrayList,this,channels);
            mRecyclerView.setAdapter(userListAdapter);
            userListAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    ApiInterface apiServiceUser=null,apiServiceChannel=null;
    public void getAllUserListCall(){
        showProgressDialog1();
        String roleId = prefManager.getStringValue(PrefConstants.TWILIO_ROLE_ID);
        String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
        apiServiceUser = ApiClient.getClient1().create(ApiInterface.class);
        Call<UserDetails> call = apiServiceUser.getUserList(Integer.parseInt(programUserId),Integer.parseInt(roleId), Constants.X_DRO_SOURCE);
        call.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                showDialog1(true);
                try {
                    int code = response.code();
                    if (code == 200) {
                        setAllData(response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                showDialog1(true);
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

                    getChannelList(user.getDroProgramUserId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
                binding.noData.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
        }


    }

    public void setAllChannel(Response<ChannelDetails> response,int attendProgramUserId){

        try {
            String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
            com.carematix.twiliochatapp.bean.fetchChannel.Data data=  response.body().getData();
            String sid=data.getChannelSid();

            ChannelList channelList=new ChannelList(programUserId,String.valueOf(attendProgramUserId),sid);
            channelViewModel.insert(channelList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Logs.d("call channel ","call channel list");
            getChannels();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getChannelList(final int attendeeProgramUserId){

        String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
        apiServiceChannel = ApiClient.getClient1().create(ApiInterface.class);
        Call<ChannelDetails> call = apiServiceChannel.fetchChannel(programUserId,String.valueOf(attendeeProgramUserId), Constants.X_DRO_SOURCE);
        call.enqueue(new Callback<ChannelDetails>() {
            @Override
            public void onResponse(Call<ChannelDetails> call, Response<ChannelDetails> response) {
                try {
                    int code = response.code();
                    if (code == 200) {
                        setAllChannel(response,attendeeProgramUserId);
                    }else{
                        showException(String.valueOf(code));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ChannelDetails> call, Throwable t) {
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
                getChannels();
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
                getChannels();
            }

            @Override
            public void onError(String errorMessage) {
                stopActivityIndicator();
            }
        });
    }

    public void getChannels(){
      //  stopActivityIndicator();

        try {
            if (channels == null) return;
            if (chatClientManager == null || chatClientManager.getChatClient() == null) return;

            Logs.d("get channel call","channel call details.");
            channelsObject = chatClientManager.getChatClient().getChannels();

            channels.clear();

            channelsObject.getPublicChannelsList(new CallbackListener<Paginator<ChannelDescriptor>>() {
                @Override
                public void onSuccess(Paginator<ChannelDescriptor> channelDescriptorPaginator) {
                    getChannelsPage(channelDescriptorPaginator);
                }
            });

            channelsObject.getUserChannelsList(new CallbackListener<Paginator<ChannelDescriptor>>() {
                @Override
                public void onSuccess(Paginator<ChannelDescriptor> channelDescriptorPaginator) {
                    getChannelsPage(channelDescriptorPaginator);
                }
            });
            channelManager.setChannelListener(MainActivity.this);


           /* try {
                ChatClient chatClient=chatClientManager.getChatClient();
                chatClient.getChannels().getPublicChannelsList(new CallbackListener<Paginator<ChannelDescriptor>>() {
                    @Override
                    public void onSuccess(Paginator<ChannelDescriptor> channelDescriptorPaginator) {
                        getChannelsPage(channelDescriptorPaginator);
                        for (ChannelDescriptor channel : channelDescriptorPaginator.getItems()) {
                            Logs.d("MainActivity", "Channel named: " + channel.getFriendlyName());
                        }

                    }
                });
                chatClient.getChannels().getUserChannelsList(new CallbackListener<Paginator<ChannelDescriptor>>() {
                    @Override
                    public void onSuccess(Paginator<ChannelDescriptor> channelDescriptorPaginator) {
                        getChannelsPage(channelDescriptorPaginator);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getChannelsPage(Paginator<ChannelDescriptor> paginator) {
        try {
            for (ChannelDescriptor cd : paginator.getItems()) {
                Logs.e("HASNEXTPAGE","Adding channel descriptor for sid|"+cd.getSid()+"| friendlyName "+cd.getFriendlyName());
                channels.put(cd.getSid(), new ChannelModel(cd));
            }

            if (paginator.hasNextPage()) {

                Logs.e("HASNEXTPAGE","Pagginator call..");

                paginator.requestNextPage(new CallbackListener<Paginator<ChannelDescriptor>>() {
                    @Override
                    public void onSuccess(Paginator<ChannelDescriptor> channelDescriptorPaginator) {
                        getChannelsPage(channelDescriptorPaginator);
                    }
                });
            } else {
                // Get subscribed channels last - so their status will overwrite whatever we received
                // from public list. Ugly workaround for now.
                if(channelsObject == null){
                    chatClientManager = TwilioApplication.get().getChatClientManager();
                    channelsObject = chatClientManager.getChatClient().getChannels();//basicClient.getChatClient().getChannels();
                }

                List<Channel> ch = channelsObject.getSubscribedChannels();
                for (Channel channel : ch) {
                    Logs.e("HASNEXTPAGE","Adding descriptor for sid|"+channel.getSid()+"| friendlyName "+channel);
                    channels.put(channel.getSid(), new ChannelModel(channel));
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshChannelList();
        try {
            setFCMToken();
            stopActivityIndicator();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            showDialog1(true);
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

    public void refreshChannelList(){
        try {

            List list = new LinkedList(channels.values());
            Collections.sort(list, new CustomChannelComparator());
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    userListAdapter.addItem(channels);
                    userListAdapter.notifyDataSetChanged();
                }
            });

            try {
                if(channels.size() > 0){
                    userChannelViewModel.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(ChannelModel channelModel :channels.values()){
                Logs.d("reftreshChannelList","channels: "+channelModel.getSid()+" Name :"+channelModel.getFriendlyName());
                channelModel.getSid();
                UserChannelList userChannelList=new UserChannelList();
                userChannelList.setSid(channelModel.getSid());
                userChannelList.setFriendlyName(channelModel.getFriendlyName());
                userChannelViewModel.insert(userChannelList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WorkerThread
    private void showProgressDialog1(){
        try {
            progressDialog1 = new ProgressDialog(MainActivity.this,R.style.MyDialog);
            progressDialog1.setMessage(getStringResource(R.string.data_load));
            progressDialog1.setCancelable(false);
            progressDialog1.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showDialog1(final boolean show){
        try {
            if(show){
                if(progressDialog1 != null && progressDialog1.isShowing()) {
                    progressDialog1.dismiss();
                    progressDialog1.cancel();
                }
            }else{
                progressDialog1.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProgressDialog progressDialog,progressDialog1,progressDialogMain;
    private void showActivityIndicator(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialogMain = new ProgressDialog(MainActivity.this,R.style.MyDialog);
                progressDialogMain.setMessage(message);
                progressDialogMain.show();
                progressDialogMain.setCanceledOnTouchOutside(false);
                progressDialogMain.setCancelable(false);
            }
        });
    }
    private void stopActivityIndicator() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialogMain.isShowing()) {
                    progressDialogMain.dismiss();
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
                                                            channels.put(channel.getSid(), new ChannelModel(channel));
                                                            refreshChannelList();
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
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_logout){
            alertLogout();
            return true;
        }else if(id == R.id.action_create){
            // channelCreate();
            return true;
        }else{

        }
        return super.onOptionsItemSelected(item);
    }

    public void alertLogout(){

        prefManager.setBooleanValue(PrefConstants.IS_FIRST_TIME_LOGIN,false);
        prefManager.setBooleanValue(PrefConstants.PREFERENCE_LOGIN_CHECK,false);
        //prefManager.setBooleanValue(PrefConstants.LOGIN_ACTIVE,false);
        //prefManager.setBooleanValue(PrefConstants.LOGIN_ACTIVE_SERVICE,false);

        try {
            SessionManager.getInstance().logoutUser();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        MainActivity.this.finish();

    }

    @Override
    public void onChannelJoined(Channel channel) {
        Log.d("JoinChannel ","Received onChannelJoined callback for channel |" + channel.getFriendlyName() + "|");
        channels.put(channel.getSid(), new ChannelModel(channel));
        refreshChannelList();
        //refreshChannels();
    }
    @Override
    public void onChannelInvited(Channel channel) {
        Log.d("JoinChannel ","Received onChannelInvited callback for channel |" + channel.getFriendlyName() + "|");
        channels.put(channel.getSid(), new ChannelModel(channel));
        refreshChannelList();
        //refreshChannels();
        showIncomingInvite(channel);
    }
    @Override
    public void onChannelAdded(Channel channel) {
        Log.d("ChannelAdded ","Received onChannelJoined callback for channel |" + channel.getFriendlyName() + "|");
        channels.put(channel.getSid(),new ChannelModel(channel));
        refreshChannelList();
    }
    @Override
    public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {
        Log.d("ChannelUpdated ","Received onChannelJoined callback for channel |" + channel.getFriendlyName() + "|");
        channels.put(channel.getSid(),new ChannelModel(channel));
        refreshChannelList();
    }
    @Override
    public void onChannelDeleted(Channel channel) {
        Log.d("ChannelDeleted ","Received onChannelJoined callback for channel |" + channel.getFriendlyName() + "|");
        channels.remove(channel.getSid());
        refreshChannelList();
    }
    @Override
    public void onChannelSynchronizationChange(Channel channel) {
        Log.d("ChannelSyncChange ","onChannelSynchronizationChange |" + channel.getFriendlyName() + "|");
        refreshChannelList();
    }
    @Override
    public void onError(ErrorInfo errorInfo) {
        TwilioApplication.get().showToast("Received onError : " , Toast.LENGTH_LONG);
    }
    @Override
    public void onUserUpdated(com.twilio.chat.User user, com.twilio.chat.User.UpdateReason updateReason) {
        Log.d("UserUpdated ","onUserUpdated |" +updateReason.name() + "|");

    }
    @Override
    public void onUserSubscribed(com.twilio.chat.User user) {
        Log.d("onUserSubscribed ","onUserSubscribed |" +user.getFriendlyName() + "|");
    }
    @Override
    public void onUserUnsubscribed(com.twilio.chat.User user) {
        Log.d("onUserUnsubscribed ","onUserUnsubscribed |" +user.getFriendlyName() + "|");
    }
    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {
        Log.d("ChannelSync","Received onChannelJoined callback for channel |" + synchronizationStatus.getValue() + "|");
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
        //TwilioApplication.getInstance().getBasicClient().onTokenExpired();
    }
    @Override
    public void onTokenAboutToExpire() {
        //TwilioApplication.get().getChatClientManager().getChatClient().onTokenAboutToExpire();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        MainActivity.this.finish();
    }



















}



/*    BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController); */