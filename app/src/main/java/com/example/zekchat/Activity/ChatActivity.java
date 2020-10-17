package com.example.zekchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.zekchat.R;

public class ChatActivity extends AppCompatActivity {

    TextView messageText,chatUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        define();
    }

    public String getUserName(){
        //Bundle bundle = getIntent().getExtras();
        //String otherUserName = bundle.getString("otherUserName");
        String otherUserName = getIntent().getExtras().getString("otherUserName");
        return otherUserName;
    }

    public String getOtherUserId(){
        String otherId = getIntent().getExtras().getString("otherId");
        return otherId;
    }

    public void define(){
        chatUserName = findViewById(R.id.chat_UserName);
        messageText = findViewById(R.id.message_text);
        chatUserName.setText(getUserName());
    }
}