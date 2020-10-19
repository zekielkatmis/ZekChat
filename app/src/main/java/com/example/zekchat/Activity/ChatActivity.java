package com.example.zekchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.zekchat.R;
import com.example.zekchat.Utils.GetDate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    TextView messageText,chatUserName;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    Button sendMessageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        define();
        action();
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
        sendMessageButton = findViewById(R.id.send_message);
        messageText = findViewById(R.id.message_text);
        chatUserName = findViewById(R.id.chat_UserName);
        chatUserName.setText(getUserName());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void action(){
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Message = messageText.getText().toString();
                messageText.setText("");
                sendMessage(firebaseUser.getUid(), getOtherUserId(), "text", GetDate.getDate(), false, Message);
            }
        });
    }

    public void sendMessage(final String userId, final String otherId, String textType, String date, Boolean seen, String messageText){

        final String messageId = databaseReference.child("Messages").child(userId).child(otherId).push().getKey();
        final Map messageMap = new HashMap();
        messageMap.put("type", textType);
        messageMap.put("seen", seen);
        messageMap.put("time", date);
        messageMap.put("text", messageText);

        databaseReference.child("Messages").child(userId).child(otherId).child(messageId).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                databaseReference.child("Messages").child(otherId).child(userId).child(messageId).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        });
    }
}