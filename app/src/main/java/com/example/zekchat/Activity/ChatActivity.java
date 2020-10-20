package com.example.zekchat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.zekchat.Adapters.MessageAdapter;
import com.example.zekchat.Models.MessageModel;
import com.example.zekchat.R;
import com.example.zekchat.Utils.GetDate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    TextView messageText, chatUserName;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    Button sendMessageButton;
    List<MessageModel> messageModelList;
    RecyclerView recyclerViewChat;
    MessageAdapter messageAdapter;
    List<String> keyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        define();
        action();
        loadMessage();
    }

    public String getUserName() {
        //Bundle bundle = getIntent().getExtras();
        //String otherUserName = bundle.getString("otherUserName");
        String otherUserName = getIntent().getExtras().getString("otherUserName");
        return otherUserName;
    }

    public String getOtherUserId() {
        String otherId = getIntent().getExtras().getString("otherId");
        return otherId;
    }

    public void define() {
        sendMessageButton = findViewById(R.id.send_message);
        messageText = findViewById(R.id.message_text);
        chatUserName = findViewById(R.id.chat_UserName);
        chatUserName.setText(getUserName());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        messageModelList = new ArrayList<>();
        keyList = new ArrayList<>();
        messageAdapter = new MessageAdapter(keyList, ChatActivity.this, ChatActivity.this, messageModelList);

        recyclerViewChat = findViewById(R.id.chat_recy_view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ChatActivity.this, 1);
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(messageAdapter);
    }

    public void action() {
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Message = messageText.getText().toString();
                messageText.setText("");
                sendMessage(firebaseUser.getUid(), getOtherUserId(), "text", GetDate.getDate(), false, Message);
            }
        });
    }

    public void sendMessage(final String userId, final String otherId, String textType, String date, Boolean seen, String messageText) {

        final String messageId = databaseReference.child("Messages").child(userId).child(otherId).push().getKey();
        final Map messageMap = new HashMap();
        messageMap.put("type", textType);
        messageMap.put("seen", seen);
        messageMap.put("time", date);
        messageMap.put("text", messageText);
        messageMap.put("from", userId);

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

    public void loadMessage() {
        databaseReference.child("Messages").child(firebaseUser.getUid()).child(getOtherUserId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageModel messageModel = snapshot.getValue(MessageModel.class);
                messageModelList.add(messageModel);
                messageAdapter.notifyDataSetChanged();
                keyList.add(snapshot.getKey());
                recyclerViewChat.scrollToPosition(messageModelList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}