package com.carematix.twiliochatapp.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.carematix.twiliochatapp.architecture.table.UserAllList;
import com.carematix.twiliochatapp.architecture.table.UserChat;
import com.carematix.twiliochatapp.architecture.viewModel.UserChatViewModel;
import com.carematix.twiliochatapp.architecture.viewModel.UserListViewModel;
import com.carematix.twiliochatapp.bean.fetchChannel.LeaveChannel;
import com.carematix.twiliochatapp.bean.userList.UserDetails;
import com.carematix.twiliochatapp.databinding.ChatFragmentBinding;
import com.carematix.twiliochatapp.helper.Constants;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.listener.OnDialogInterfaceListener;
import com.carematix.twiliochatapp.preference.PrefConstants;
import com.carematix.twiliochatapp.preference.PrefManager;
import com.carematix.twiliochatapp.restapi.ApiClient;
import com.carematix.twiliochatapp.restapi.ApiInterface;
import com.carematix.twiliochatapp.twilio.MessageItem;
import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.Channels;
import com.twilio.chat.Member;
import com.twilio.chat.Members;
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
    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    private static Channel channel;

    String userName="",sID="",type="";
    View view;

    public String identity="";

    public ChatFragment(){}
    PrefManager prefManager=null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
      //  view = inflater.inflate(R.layout.chat_fragment, container, false);
        chatFragmentBinding =ChatFragmentBinding.inflate(getLayoutInflater());
        view = chatFragmentBinding.getRoot();

        try {

            prefManager=new PrefManager(getActivity());
            prefManager.setStringValue(PrefConstants.WHICH_SCREEN,"chat");

            Bundle bundle = getArguments();
            try {
                userName = bundle.getString(Constants.EXTRA_NAME, null);
                sID = bundle.getString(Constants.EXTRA_ID,null);
                type = bundle.getString(Constants.EXTRA_TYPE,null);

            } catch (Exception e) {
                e.printStackTrace();
            }
            // Set title bar
            setHasOptionsMenu(true);
            try {
                channel =getArguments().getParcelable(Constants.EXTRA_CHANNEL);

            } catch (Exception e) {
                e.printStackTrace();
            }

            Logs.d("chat fragment"," name : "+userName);
            Logs.d("chat fragment"," id : "+sID);
            Logs.d("chat fragment"," type : "+type);


            chatFragmentBinding.editText.setOnEditorActionListener(this);
            chatFragmentBinding.imageButton.setOnClickListener(this);
            chatFragmentBinding.imageBack.setOnClickListener(this);

            chatFragmentBinding.imageButton.setEnabled(true);
            chatFragmentBinding.editText.setEnabled(true);
            try {
                chatFragmentBinding.actionLeave.setVisibility(View.VISIBLE);
                chatFragmentBinding.actionLeave.setOnClickListener(this);
                /*String roleId = prefManager.getStringValue(PrefConstants.TWILIO_ROLE_ID);
                if(roleId.equals("1")){
                    chatFragmentBinding.actionLeave.setVisibility(View.GONE);
                }else{
                    chatFragmentBinding.actionLeave.setVisibility(View.VISIBLE);
                    chatFragmentBinding.actionLeave.setOnClickListener(this);
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }


            chatFragmentBinding.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    try {
                        chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size()-1);
                        chatFragmentBinding.recyclerView2.smoothScrollToPosition(chatFragmentBinding.recyclerView2.getBottom());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            }

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

            /*handler.postDelayed(runnable = new Runnable() {
                public void run() {
                    handler.postDelayed(runnable, delay);
                    updateBar();
                }
            }, delay);*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*Handler handler = new Handler();
    int delay = 5000;
    Runnable runnable;*/

    public void leaveChannelLevel(Channel channel){

        try {
            Logs.d("onSynchronizationChanged ","onSynchronizationStatus1 " + channel.getSynchronizationStatus());
            Logs.d("onSynchronizationChanged ","onSynchronizationStatus2 " + channel.getStatus());

            if((channel.getSynchronizationStatus() == Channel.SynchronizationStatus.NONE) && (channel.getStatus()==Channel.ChannelStatus.NOT_PARTICIPATING)){
                showAlertOnly(userName+" left the chat.You can't send the messages.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void loadSetListView(){

        RecyclerView mRecyclerView = chatFragmentBinding.recyclerView2;
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        // mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        chatAdapter=new LeftChatAdapter(getActivity(),messageItemList,linearLayoutManager,chatFragmentBinding.recyclerView2);
        mRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
        if(messageItemList.size() > 2){
            mRecyclerView.scrollToPosition(messageItemList.size()-1);
        }
        //mRecyclerView.setOnClickListener(getActivity());
        mRecyclerView.smoothScrollToPosition(mRecyclerView.getBottom());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(var == 0){
                        var++;
                        ++var;
                    }else{
                        Log.d("DERE", "onScrollStateChanged: "+getCurrentItem());
                        int position = getCurrentItem();
                        if(messageItemList.size() > 0){
                            Log.d("DERE", "onScrollStateChanged: position : "+position);
                            setDateLabel(messageItemList.get(position));
                            chatAdapter.notifyDataSetChanged();
                        }
                    }
                    var++;
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

    public static int var=0;
    String currentDate="";String date="";
    public void setDateLabel(MessageItem messageItem){

        Calendar calendar = Calendar.getInstance();
        String todayDate = Utils.getDateTime(calendar.getTimeInMillis());
        currentDate = Utils.setDateTime(messageItem.getMessage().getDateCreatedAsDate());

        chatFragmentBinding.dateLabel.setText(currentDate);
        chatFragmentBinding.dateLabel.setVisibility(View.VISIBLE);

        /*if(todayDate.compareTo(currentDate) == 0){
            chatFragmentBinding.dateLabel.setText("Today");
            chatFragmentBinding.dateLabel.setVisibility(View.VISIBLE);
        }else if(todayDate.compareTo(currentDate) > 0){
            int check = Integer.parseInt(Utils.getDateC1(messageItem.getMessage().getDateCreatedAsDate()));
            int today = Integer.parseInt(Utils.getDateC(calendar.getTimeInMillis()));
            if(check == (today-1)){
                chatFragmentBinding.dateLabel.setText("Yesterday");
                chatFragmentBinding.dateLabel.setVisibility(View.VISIBLE);
            }else{



                if(!date.equals(currentDate)){
                    date =currentDate;
                    chatFragmentBinding.dateLabel.setText(currentDate);
                    chatFragmentBinding.dateLabel.setVisibility(View.VISIBLE);
                }else{
                    date =currentDate;
                }
            }
        }else{
            chatFragmentBinding.dateLabel.setText(currentDate);
            chatFragmentBinding.dateLabel.setVisibility(View.INVISIBLE);
        }*/

    }


    private int getCurrentItem() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager)chatFragmentBinding.recyclerView2.getLayoutManager();
        return linearLayoutManager.findFirstVisibleItemPosition();
    }

    public ChannelListener channelListener =new ChannelListener() {
        @Override
        public void onMessageAdded(Message message) {
            try {
                if (message != null){
                    Members  members = channel.getMembers();
                    messageItemList.add(new MessageItem(message,members , identity));
                    chatAdapter.addItem(messageItemList);
                    chatAdapter.notifyDataSetChanged();
                    setupListView(channel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onMessageUpdated(Message message, Message.UpdateReason updateReason) {
            if (message != null) {
                TwilioApplication.get().showToast(message.getSid() + " changed");
                setupListView(channel);
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
                updateBar();
            }
        }

        @Override
        public void onMemberUpdated(Member member, Member.UpdateReason updateReason) {
            if (member != null) {
                TwilioApplication.get().showToast(member.getIdentity() + " onMemberUpdated");
                updateBar();
            }
        }

        @Override
        public void onMemberDeleted(Member member) {
            if (member != null) {
                TwilioApplication.get().showToast(member.getIdentity() + " onMemberDeleted");
                updateBar();
            }
        }

        @Override
        public void onTypingStarted(Channel channel, Member member) {
            if (channel != null) {
                //TextView typingIndc = (TextView)getActivity().findViewById(R.id.typingIndicator);
                String   text =  " is typing ...";
                // typingIndc.setText(text);
                chatFragmentBinding.typingIndicator.setText(""+userName+ " "+text);
                // typingIndc.setTextColor(Color.RED);
                Logs.d("onTypingStarted ", " start typing"+text);
            }
        }

        @Override
        public void onTypingEnded(Channel channel, Member member) {
            if (channel != null) {
                //  TextView typingIndc = (TextView)getActivity().findViewById(R.id.typingIndicator);
                //  typingIndc.setText(null);
                String   text =  " ended typing .....";
                chatFragmentBinding.typingIndicator.setText(""+userName);
                Logs.d("onTypingEnded ",member.getIdentity() + " ended typing");
            }
        }

        @Override
        public void onSynchronizationChanged(Channel channel) {
            Logs.d("onSynchronizationChanged ","Received onSynchronizationChanged callback " + channel.getFriendlyName());
            leaveChannelLevel(channel);
        }
    };

    private ChatClientManager chatClientManager;
    public void createUiData(){
        try {
            chatClientManager = TwilioApplication.get().getChatClientManager();
            identity = chatClientManager.getChatClient().getMyIdentity();
            Logs.d("chat fragmnet","identity : "+identity+" CHANNEL_ID : "+sID);

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
            if(!TwilioApplication.get().getChatClientManager().getChatClient().isReachabilityEnabled()){
                // chatFragmentBinding.isOnline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_offline_circle_24, 0);
                chatFragmentBinding.isOnline.setText("is offline");
            }else{
                // channel.getMembers().getMembersList()
                chatFragmentBinding.typingIndicator.setText(userName);

                Member member = null;
                try {
                    member = channel.getMembers().getMember(type);
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
                                chatFragmentBinding.isOnline.setText("is online");
                                //  chatFragmentBinding.isOnline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_online_circle_24, 0);
                            }else if(user.isNotifiable()){
                                chatFragmentBinding.isOnline.setText("is offline");
                                //  chatFragmentBinding.isOnline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_notifiable_circle_24, 0);
                            }else{
                                chatFragmentBinding.isOnline.setText("is offline");
                                //  chatFragmentBinding.isOnline.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_offline_circle_24, 0);
                            }
                        }
                    });
                }else{
                    chatFragmentBinding.isOnline.setText("is offline");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (!text.equals("")) {
            sendMessage(text);
        }
    }

    private void sendMessage(final String text){
        final Messages messagesObject = this.channel.getMessages();
        messagesObject.sendMessage(Message.options().withBody(text), new CallbackListener<Message>() {
            @Override
            public void onSuccess(Message message) {
                TwilioApplication.get().showToast("Successfully sent message");
              //  chatAdapter.notifyDataSetChanged();
                setupListView(channel);
                chatFragmentBinding.editText.setText("");
            }
        });
    }



    UserChatViewModel viewModel;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            viewModel =new ViewModelProvider(requireActivity()).get(UserChatViewModel.class);
            /*viewModel.getUserChatData(sID).observe(getViewLifecycleOwner(),userChats -> {
                //chatAdapter.addItem();
                try {
                    if(userChats.size() > 0){
                        for(UserChat userChat : userChats){
                            *//*hashMap=new HashMap<>();
                            hashMap.put(userChat.title,userChat.getChat_description());
                            i++;
                            arrayListHashMap.put(i,hashMap);*//*
                        }
                        chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size()-1);
                        chatFragmentBinding.recyclerView2.smoothScrollToPosition(chatFragmentBinding.recyclerView2.getBottom());
                    }else{
                        //chatFragmentBinding.recyclerView2.scrollToPosition(arrayListHashMap.size()-1);
                        // chatFragmentBinding.recyclerView2.smoothScrollToPosition(chatFragmentBinding.recyclerView2.getBottom());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });*/
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
                if(text.length() > 0){
                    userChat.setChat_description(text);
                    userChat.setUid(sID);
                    userChat.setTitle("RIGHT");
                    viewModel.insert(userChat);
                    //arrayListHashMap.put(i,hashMap);
                }else{
                    hideKeyboard(getActivity());
                }

                if(!text.equals("")){
                    sendMessage(text);
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
               // i++;
              //  hashMap=new HashMap<>();
               /* if(i%2 ==0){
                    userChat.setChat_description(text);
                    userChat.setUid(sId);
                    userChat.setTitle("RIGHT");
                //    hashMap.put("RIGHT",text);
                }else{
                    userChat.setChat_description(text);
                    userChat.setUid(sId);
                    userChat.setTitle("LEFT");
                 //   hashMap.put("LEFT",text);
                }*/

                userChat.setChat_description(text);
                userChat.setUid(sID);
                userChat.setTitle("RIGHT");
                viewModel.insert(userChat);
               // arrayListHashMap.put(i,hashMap);
                sendMessage();

            }
            chatFragmentBinding.editText.setText("");
          //  chatAdapter.addItem(arrayListHashMap);
           // chatAdapter.notifyDataSetChanged();

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
        try {
           // channel.removeListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    public void setupListView(Channel channel){

        try {
            final Messages messagesObject = channel.getMessages();
            loadMessageShowMessage(messagesObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size()-1);
            chatFragmentBinding.recyclerView2.smoothScrollToPosition(chatFragmentBinding.recyclerView2.getBottom());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    List<MessageItem> messageItemList = new ArrayList<>();
    public void loadMessageShowMessage(final Messages messagesObject){

        if (messagesObject != null) {
            messagesObject.getLastMessages(1000, new CallbackListener<List<Message>>() {
                @Override
                public void onSuccess(List<Message> messagesArray) {
                    messageItemList.clear();
                    Members  members = channel.getMembers();
                    if (messagesArray.size() > 0) {
                        for (int i = 0; i < messagesArray.size(); i++) {
                            messageItemList.add(new MessageItem(messagesArray.get(i), members, identity));
                        }
                    }
                    chatAdapter.addItem(messageItemList);
                    chatAdapter.notifyDataSetChanged();

                    try {
                        chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size()-1);
                        chatFragmentBinding.recyclerView2.smoothScrollToPosition(chatFragmentBinding.recyclerView2.getBottom());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });


            channel.getUnconsumedMessagesCount(new CallbackListener<Long>() {
                @Override
                public void onSuccess(Long aLong) {
                    try {
                        if(aLong != null)
                        if(aLong != 0){
                            chatAdapter.addItem(aLong);
                            chatAdapter.notifyDataSetChanged();
                        }

                        try {
                            chatFragmentBinding.recyclerView2.scrollToPosition(messageItemList.size()-1);
                            chatFragmentBinding.recyclerView2.smoothScrollToPosition(chatFragmentBinding.recyclerView2.getBottom());
                        } catch (Exception e) {
                            e.printStackTrace();
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
            //Utils.showToast(this,"Work in progress...!!!");
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

    public void showAlertOnly(String msg){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder((ChatActivity)getActivity());
            builder.setMessage(msg)
                    .setTitle(R.string.leave_msg);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                   // Intent it=new Intent(getActivity(),MainActivity.class);
                   // startActivity(it);
                    //getActivity().finish();
                    chatFragmentBinding.imageButton.setEnabled(false);
                    chatFragmentBinding.imageButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    chatFragmentBinding.editText.setEnabled(false);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showError(String msg){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(msg)
                    .setTitle(R.string.leave_msg);
            builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.cancel();
                }
            });
            AlertDialog dialog = builder.create();
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
            getActivity().finish();
            //prefManager.getBooleanValue()

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    ProgressDialog progressDialog1;
    @WorkerThread
    private void showProgressDialog1(){
        try {
            progressDialog1 = new ProgressDialog(getActivity(),R.style.MyDialog);
            progressDialog1.setMessage("Leave...");
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

}