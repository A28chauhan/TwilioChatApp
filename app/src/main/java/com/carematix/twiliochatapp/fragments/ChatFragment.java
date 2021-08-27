package com.carematix.twiliochatapp.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carematix.twiliochatapp.ChatActivity;
import com.carematix.twiliochatapp.MainActivity;
import com.carematix.twiliochatapp.R;
import com.carematix.twiliochatapp.adapter.LeftChatAdapter;
import com.carematix.twiliochatapp.application.ChatClientManager;
import com.carematix.twiliochatapp.application.TwilioApplication;
import com.carematix.twiliochatapp.architecture.table.UserChat;
import com.carematix.twiliochatapp.architecture.viewModel.UserChatViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserListViewModel;
import com.carematix.twiliochatapp.bean.fetchChannel.LeaveChannel;
import com.carematix.twiliochatapp.databinding.ChatFragmentBinding;
import com.carematix.twiliochatapp.helper.Constants;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.restapi.ApiClient;
import com.carematix.twiliochatapp.restapi.ApiInterface;
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
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {

    ChatFragmentBinding chatFragmentBinding;
    LeftChatAdapter chatAdapter;
    public ChatFragment newInstance() {
        return new ChatFragment();
    }

    private Channel channel;

    String userName="",sID="",type="";
    View view;

    public String identity="";

    public ChatFragment(){}
    PrefManager prefManager=null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        chatFragmentBinding =ChatFragmentBinding.inflate(getLayoutInflater());
        view = chatFragmentBinding.getRoot();

        try {

            prefManager=new PrefManager(getActivity());
            prefManager.setStringValue(PrefConstants.WHICH_SCREEN,"chat");
            prefManager.setBooleanValue(PrefConstants.SCREEN,false);

            Bundle bundle = getArguments();
            userName = bundle.getString(Constants.EXTRA_NAME, null);
            sID = bundle.getString(Constants.EXTRA_ID,null);
            type = bundle.getString(Constants.EXTRA_TYPE,null);
            // Set title bar
            setHasOptionsMenu(true);
            try {
                channel =getArguments().getParcelable(Constants.EXTRA_CHANNEL);

            } catch (Exception e) {
                e.printStackTrace();
            }

            chatFragmentBinding.editText.setOnEditorActionListener(this);
            chatFragmentBinding.imageButton.setOnClickListener(this);
            chatFragmentBinding.imageBack.setOnClickListener(this);

            chatFragmentBinding.imageButton.setEnabled(true);
            chatFragmentBinding.editText.setEnabled(true);
            chatFragmentBinding.editText.setFocusableInTouchMode(true);

            chatFragmentBinding.actionLeave.setVisibility(View.VISIBLE);
            chatFragmentBinding.actionLeave.setOnClickListener(this);
                /*String roleId = prefManager.getStringValue(PrefConstants.TWILIO_ROLE_ID);
                if(roleId.equals("1")){
                    chatFragmentBinding.actionLeave.setVisibility(View.GONE);
                }else{
                    chatFragmentBinding.actionLeave.setVisibility(View.VISIBLE);
                    chatFragmentBinding.actionLeave.setOnClickListener(this);
                }*/

            String roleId = prefManager.getStringValue(PrefConstants.TWILIO_ROLE_ID);
            if(roleId.equals("1")){
                chatFragmentBinding.imageBack.setVisibility(View.GONE);
            }
            showKeyboard();

            chatFragmentBinding.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        if (hasFocus) {
                            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                        chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size()-1);
                        chatFragmentBinding.recyclerView2.smoothScrollToPosition(chatFragmentBinding.recyclerView2.getBottom());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            allViewModel = new ViewModelProvider(this).get(UserListViewModel.class);
            allViewModel.getUserByID(type).observe(requireActivity(),userAllLists -> {
                if(userAllLists.size() > 0){
                    try {
                        createUiData();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            try {
                loadSetListView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public UserListViewModel allViewModel;
    @Override
    public void onResume() {
        super.onResume();
        try {
            if(messageItemList.size() > 2){
                chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size()-1);
            }

            try {
                setupListView(channel);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leaveChannelLevel(Channel channel){

        try {
            if((channel.getSynchronizationStatus() == Channel.SynchronizationStatus.NONE) && (channel.getStatus()==Channel.ChannelStatus.NOT_PARTICIPATING)){
                showAlertOnly(userName+" left the chat.You can't send the messages.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    int y=0;
    boolean bb =false;
    public void loadSetListView(){
        bb =false;
        RecyclerView mRecyclerView = chatFragmentBinding.recyclerView2;
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        chatAdapter=new LeftChatAdapter(getActivity(),messageItemList,linearLayoutManager,chatFragmentBinding.recyclerView2,channel,type);
        mRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
        if(messageItemList.size() > 2){
            mRecyclerView.scrollToPosition(messageItemList.size()-1);
        }
        mRecyclerView.smoothScrollToPosition(mRecyclerView.getBottom());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
               // super.onScrolled(recyclerView, dx, dy);
                Logs.d("DERE", "onScrolled: position : "+dx+" : dy "+dy);
                y =dy;
                bb =true;
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //super.onScrollStateChanged(recyclerView, newState);
                if (newState == recyclerView.SCROLL_STATE_SETTLING) {
                   // bb= false;
                }
                if(mRecyclerView.SCROLL_STATE_DRAGGING==newState){
                    bb= true;
                    Logs.d("DERE", "onScrollStateChanged: "+getCurrentItem());
                    int position = getCurrentItem();
                    if(messageItemList.size() > 0){
                        Logs.d("DERE", "onScrollStateChanged: position : "+position);
                        setDateLabel(messageItemList.get(position));
                        chatAdapter.notifyDataSetChanged();
                    }
                }
                if (newState == recyclerView.SCROLL_STATE_IDLE) {
                    if( y <= 0){
                        Logs.d("DERE", "onScrollStateChanged: y : "+y);
                    }else{
                        y=0;
                        chatFragmentBinding.dateLabel.setVisibility(View.INVISIBLE);
                    }
                }
            }

        });

        setupListView(channel);

        chatFragmentBinding.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if(messageItemList.size() > 2) {
                        chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size() - 1);
                        chatFragmentBinding.recyclerView2.smoothScrollToPosition(chatFragmentBinding.recyclerView2.getBottom());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            handler1.removeCallbacks(runnable1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        try {
            if ( dialog!=null && dialog.isShowing() ){
                dialog.cancel();
                dialog.dismiss();
            }
            if ( progressDialog!=null && progressDialog.isShowing() ){
                progressDialog.cancel();
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    Handler handler1 = new Handler();
    Runnable runnable1;
    int delay1 = 5000;

    String currentDate="";
    public void setDateLabel(Message messageItem){

        Calendar calendar = Calendar.getInstance();
        String todayDate = Utils.getDateTime(calendar.getTimeInMillis());
        currentDate = Utils.setDateTime(messageItem.getDateCreatedAsDate());

        if(todayDate.compareTo(currentDate) == 0){
            chatFragmentBinding.dateLabel.setText("Today");
            chatFragmentBinding.dateLabel.setVisibility(View.VISIBLE);
        }

        handler1.postDelayed(runnable1 = new Runnable() {
            public void run() {
                handler1.postDelayed(runnable1, delay1);
                chatFragmentBinding.dateLabel.setVisibility(View.INVISIBLE);
            }
        }, delay1);

    }


    private int getCurrentItem() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager)chatFragmentBinding.recyclerView2.getLayoutManager();
        return linearLayoutManager.findFirstVisibleItemPosition();
    }



    public ChannelListener channelListener =new ChannelListener() {
        @Override
        public void onMessageAdded(Message message) {
            try {
                boolean isAppBackGround = Utils.isAppIsInBackground(getActivity());
                if (!isAppBackGround) {
                    if (message != null){
                        chatAdapter.addItem(message);
                        chatAdapter.notifyDataSetChanged();
                        setAllConsumedMessages(message.getMessageIndex());
                        scrollDown();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {
            if (message != null) {
                TwilioApplication.get().showToast(message.getSid() + " changed");
                setAllConsumedMessages(message.getMessageIndex());
                scrollDown();
                Logs.d("onMessageUpdated","Received onMessageUpdated for message sid|" + message.getSid() + "|");
            } else {
                Logs.d("onMessageUpdated","Received onMessageUpdated");
            }
        }

        @Override
        public void onMessageDeleted(Message message) {
            if (message != null) {
                TwilioApplication.get().showToast(message.getSid() + " deleted");
                Logs.d("onMessageDeleted","Received onMessageDeleted " + message.getSid() + "|");
            } else {
                Logs.d("onMessageDeleted","Received onMessageDelete.");
            }
        }

        @Override
        public void onMemberAdded(Member member) {
            if (member != null) {
                TwilioApplication.get().showToast(member.getIdentity() + " onMemberAdded");
            }
        }

        @Override
        public void onMemberUpdated(Member member, Member.UpdateReason updateReason) {
            if (member != null) {
                TwilioApplication.get().showToast(member.getIdentity() + " onMemberUpdated");
                //updateBar();
                if(updateReason == Member.UpdateReason.LAST_CONSUMPTION_TIMESTAMP || updateReason == Member.UpdateReason.LAST_CONSUMED_MESSAGE_INDEX){
                     chatAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onMemberDeleted(Member member) {
            if (member != null) {
                TwilioApplication.get().showToast(member.getIdentity() + " onMemberDeleted");
            }
        }

        @Override
        public void onTypingStarted(Channel channel, Member member) {
            if (channel != null) {
                String   text =  "is typing ...";
                String identity1 = member.getIdentity();
                if(!identity1.equals(prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID))){
                    chatFragmentBinding.typingIndicatorNote.setVisibility(View.VISIBLE);
                    chatFragmentBinding.typingIndicatorNote.setText(""+userName+ " "+text);
                }
                Logs.d("onTypingStarted ", " start typing"+text);
            }
        }

        @Override
        public void onTypingEnded(Channel channel, Member member) {
            if (channel != null) {
                String   text =  " ended typing .....";
                chatFragmentBinding.typingIndicatorNote.setText("");
                chatFragmentBinding.typingIndicatorNote.setVisibility(View.INVISIBLE);
                Logs.d("onTypingEnded ",member.getIdentity() + " ended typing");
            }
        }

        @Override
        public void onSynchronizationChanged(Channel channel) {
            Logs.d("onSynchronizationChanged ","Received onSynchronizationChanged callback " + channel.getFriendlyName());
            leaveChannelLevel(channel);
        }
    };


    public ChatClientListener chatClientListener=new ChatClientListener() {
        @Override
        public void onChannelJoined(Channel channel) {}

        @Override
        public void onChannelInvited(Channel channel) {}

        @Override
        public void onChannelAdded(Channel channel) {}

        @Override
        public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {}

        @Override
        public void onChannelDeleted(Channel channel) {}

        @Override
        public void onChannelSynchronizationChange(Channel channel) {}

        @Override
        public void onError(ErrorInfo errorInfo) {}

        @Override
        public void onUserUpdated(User user, User.UpdateReason updateReason) {
            if(updateReason == User.UpdateReason.REACHABILITY_ONLINE || updateReason == User.UpdateReason.REACHABILITY_NOTIFIABLE){
                Logs.e("onUserUpdated","createUiData :"+updateReason);
                updateBar();
            }
        }

        @Override
        public void onUserSubscribed(User user) {}

        @Override
        public void onUserUnsubscribed(User user) {}

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

    private ChatClientManager chatClientManager;
    public void createUiData(){
        try {
            chatClientManager = TwilioApplication.get().getChatClientManager();
            identity = chatClientManager.getChatClient().getMyIdentity();
            Logs.d("chat fragmnet","identity : "+identity+" CHANNEL_ID : "+sID);

            chatClientManager.getChatClient().addListener(chatClientListener);

            Channels channelsObject = chatClientManager.getChatClient().getChannels();
            channelsObject.getChannel(sID, new CallbackListener<Channel>() {
                @Override
                public void onSuccess(final Channel foundChannel)
                {
                    channel = foundChannel;
                    channel.addListener(channelListener);
                    setupListView(channel);
                    setupInput();
                }
            });

            updateBar();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void updateBar(){

        try {

            chatFragmentBinding.typingIndicator.setText(userName);


            if(type != null){
                chatClientManager.getChatClient().getUsers().getAndSubscribeUser(type, new CallbackListener<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if(user.isOnline()){
                            chatFragmentBinding.isOnline.setText("(online)");
                        }else if(user.isNotifiable()){
                            chatFragmentBinding.isOnline.setText("(offline)");
                        }else{
                            chatFragmentBinding.isOnline.setText("(offline)");
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


           /* if(!TwilioApplication.get().getChatClientManager().getChatClient().isReachabilityEnabled()){
                // chatFragmentBinding.isOnline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_offline_circle_24, 0);
                chatFragmentBinding.isOnline.setText("(offline)");
            }else{
                // channel.getMembers().getMembersList()
                chatFragmentBinding.typingIndicator.setText(userName);

                Member member = null;
                try {
                    if(channel != null){
                        List<Member> mem = channel.getMembers().getMembersList();
                        if(mem.size() > 0)
                            member = channel.getMembers().getMember(type);
                    }
                  //  String programId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
                  //  Member member1 = channel.getMembers().getMember(programId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(member != null){
                    member.getAndSubscribeUser(new CallbackListener<User>() {
                        @Override
                        public void onSuccess(User user) {
                            if(user.isOnline()){
                                chatFragmentBinding.isOnline.setText("(online)");
                                //  chatFragmentBinding.isOnline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_online_circle_24, 0);
                            }else if(user.isNotifiable()){
                                chatFragmentBinding.isOnline.setText("(offline)");
                                //  chatFragmentBinding.isOnline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notifiable_circle_24, 0);
                            }else{
                                chatFragmentBinding.isOnline.setText("(offline)");
                                //  chatFragmentBinding.isOnline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_offline_circle_24, 0);
                            }
                        }
                    });
                }else{
                    chatFragmentBinding.isOnline.setText("(offline)");
                }

            }*/
    }

    public void setupInput(){

        EditText inputText = chatFragmentBinding.editText;
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (channel != null && s.length() > 0) {
                    channel.typing();
                }
            }
            @Override
            public void afterTextChanged(Editable s){}
        });

        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                if (actionId == EditorInfo.IME_NULL
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });
    }

    public void sendMessage(){

        String text =chatFragmentBinding.editText.getText().toString();
        text =text.trim();
        if (!text.equals("")) {
            sendMessage(text);
        }else{
            Utils.showToast("Please enter message",getActivity());
        }

    }

    private void sendMessage(final String text){
        if(Utils.onNetworkChange(getActivity())){
        final Messages messagesObject = this.channel.getMessages();
        Logs.e("messages object","messages"+channel.getSid());
        messagesObject.sendMessage(Message.options().withBody(text), new CallbackListener<Message>() {
            @Override
            public void onSuccess(Message message) {
                TwilioApplication.get().showToast("Successfully sent message");
                setupListView(channel);
                chatFragmentBinding.editText.setText("");
            }
        });
        }else{
            Utils.showToast("The Internet connection appears to be offline.",getActivity());
        }

    }


    UserChatViewModel viewModel;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            viewModel =new ViewModelProvider(requireActivity()).get(UserChatViewModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callMain(){
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }


    @Override
    public void onClick(View v) {
        try {
            if(v.getId() == R.id.imageBack){
                callMain();
            }else if(v.getId() == R.id.action_leave){
                 preAlert();
            }else{
                UserChat userChat=new UserChat();
                String text =chatFragmentBinding.editText.getText().toString();
                text =text.trim();
                if(text.length() > 0){
                    userChat.setChat_description(text);
                    userChat.setUid(sID);
                    userChat.setTitle("RIGHT");
                    viewModel.insert(userChat);
                }else{
                    hideKeyboard(getActivity());
                }

                if(!text.equals("")){
                    sendMessage(text);
                }else{
                    Utils.showToast("Please enter message",getActivity());
                }
                try {
                    chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size()-1);
                    chatFragmentBinding.recyclerView2.smoothScrollToPosition(chatFragmentBinding.recyclerView2.getBottom());
                    chatAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                chatFragmentBinding.editText.setText("");
                chatFragmentBinding.editText.requestFocus();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onSendButton(View view){
        try {
            UserChat userChat=new UserChat();
            String text =chatFragmentBinding.editText.getText().toString();
            if(text.length() > 0){
                userChat.setChat_description(text);
                userChat.setUid(sID);
                userChat.setTitle("RIGHT");
                viewModel.insert(userChat);
                sendMessage();
            }
            chatFragmentBinding.editText.setText("");

            try {
                chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size()-1);
                chatFragmentBinding.recyclerView2.smoothScrollToPosition(chatFragmentBinding.recyclerView2.getBottom());
                chatAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }

            hideKeyboard(getActivity());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public boolean hideKeyboard(Context activity) {
        try {
           // InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            //imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            // hide virtual keyboard
          //  InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
          //  imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
           // onSendButton(null);
            return true;
        }else if(actionId == EditorInfo.IME_ACTION_DONE){
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.RESULT_UNCHANGED_SHOWN);
            onSendButton(null);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setAllConsumedMessages(long index){
        channel.getMessages().setLastConsumedMessageIndexWithResult(index, new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        try {
                            Logs.d("LastConsumedMessageIndex "," messages LastConsumedMessageIndex:"+aLong);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                channel.getMessages().advanceLastConsumedMessageIndexWithResult(index, new CallbackListener<Long>() {
                    @Override
                    public void onSuccess(Long aLong) {
                        try {
                            Logs.d("LastConsumedMessageIndex "," messages LastConsumedMessageIndex:"+aLong);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        /*channel.getMessages().setAllMessagesConsumedWithResult(new CallbackListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
            }
        });*/
    }


    public void scrollDown(){
        try {
            if(messageItemList.size() > 2){
                chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size()-1);
            }
            chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size()-1);
            chatFragmentBinding.recyclerView2.smoothScrollToPosition(chatFragmentBinding.recyclerView2.getBottom());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupListView(Channel channel){

        try {
            final Messages messagesObject = channel.getMessages();
            loadMessageShowMessage(messagesObject);
            scrollDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    List<Message> messageItemList = new ArrayList<>();
    public void loadMessageShowMessage(final Messages messagesObject){

        if (messagesObject != null) {

            messagesObject.getLastMessages(1000, new CallbackListener<List<Message>>() {
                @Override
                public void onSuccess(List<Message> messages) {
                    messageItemList.clear();
                    messageItemList=messages;
                    chatAdapter.addItem(messages);
                    chatAdapter.notifyDataSetChanged();
                    scrollDown();
                    try {
                        if(messageItemList.size() > 0){
                            setAllConsumedMessages((messages.get(messages.size()-1).getMessageIndex()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.chat_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_leave) {
            preAlert();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void preAlert(){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.leave_msg_details)
                    .setTitle(R.string.leave_msg);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.action_leave, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                    callLeaveApi();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ApiInterface apiServiceUser=null;
    public void callLeaveApi(){
        showProgressDialog1();
        apiServiceUser = null;
        String programUserId = prefManager.getStringValue(PrefConstants.PROGRAM_USER_ID);
        apiServiceUser = ApiClient.getClient1().create(ApiInterface.class);
        Call<LeaveChannel> call = apiServiceUser.leaveChannel(programUserId,channel.getSid(), Constants.X_DRO_SOURCE);
        call.enqueue(new Callback<LeaveChannel>() {
            @Override
            public void onResponse(Call<LeaveChannel> call, Response<LeaveChannel> response) {
                showDialog1(true);
                try {
                    int code = response.code();
                    if (code == 200) {
                        prefManager.setBooleanValue(PrefConstants.SCREEN,true);
                        showAlert("Channel Leave "+response.body().getMessage()+"fully.");
                    }else{
                        showError(response.message().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<LeaveChannel> call, Throwable t) {
                showDialog1(true);
                showError(t.getMessage().toString());
            }
        });
    }

    AlertDialog dialog;
    public void showAlertOnly(String msg){
        try {
            if(!prefManager.getBooleanValue(PrefConstants.SCREEN)){
                AlertDialog.Builder builder = new AlertDialog.Builder((ChatActivity)getActivity());
                builder.setMessage(msg)
                        .setTitle(R.string.leave_msg);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.cancel();
                        chatFragmentBinding.imageButton.setEnabled(false);
                        chatFragmentBinding.imageButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                        chatFragmentBinding.editText.setEnabled(false);
                    }
                });
                dialog = builder.create();
                dialog.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showError(String msg){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(msg)
                    .setTitle(R.string.leave_msg);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.cancel();
                }
            });
            dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String msg){
        try {
            try {
                viewModel.delete(channel.getSid());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent it=new Intent(getActivity(),MainActivity.class);
            startActivity(it);
            getActivity().finishAffinity();
            getActivity().finish();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ProgressDialog progressDialog;
    @WorkerThread
    private void showProgressDialog1(){
        try {
            progressDialog = new ProgressDialog(getActivity(),R.style.MyDialog);
            progressDialog.setMessage("Leave...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog1(final boolean show){
        try {
            if(show){
                if(progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    progressDialog.cancel();
                }
            }else{
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}