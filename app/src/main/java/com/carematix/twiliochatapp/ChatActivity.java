package com.carematix.twiliochatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.carematix.twiliochatapp.fragments.ChatFragment;
import com.carematix.twiliochatapp.helper.Constants;
import com.carematix.twiliochatapp.helper.Logs;
import com.carematix.twiliochatapp.helper.Utils;
import com.carematix.twiliochatapp.restapi.ApiInterface;
import com.twilio.chat.Channel;

public
class ChatActivity extends AppCompatActivity {

    ChatFragment chatFragment;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

            Channel channel = getIntent().getParcelableExtra(Constants.EXTRA_CHANNEL);
            bundle = getIntent().getExtras();
            String position = bundle.getString(Constants.EXTRA_ID, null);
            String name = bundle.getString(Constants.EXTRA_NAME,null);
            String type = bundle.getString(Constants.EXTRA_TYPE,null);
            channel =bundle.getParcelable(Constants.EXTRA_CHANNEL);

            Logs.d("chat Activity"," name : "+name);
            Logs.d("chat Activity"," id : "+position);
            Logs.d("chat Activity"," type : "+type);
            Logs.d("chat Activity"," name : "+channel.getSid());

            chatFragment=new ChatFragment();
            chatFragment.setArguments(bundle);

            try {
                if(chatFragment != null){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.container, ChatFragment.class, bundle).addToBackStack(null)
                            .commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if(count >= 0){
            Intent intent =new Intent(this,MainActivity.class);
            startActivity(intent);
            this.finish();
        }else{
            getSupportFragmentManager().popBackStack();
        }
        // super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }


}
