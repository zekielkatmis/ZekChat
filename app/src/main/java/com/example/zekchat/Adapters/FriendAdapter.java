package com.example.zekchat.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zekchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    List<String> userKeyList;
    Activity activity;
    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    String userId;

    public FriendAdapter(List<String> userKeyList, Activity activity, Context context) {
        this.userKeyList = userKeyList;
        this.activity = activity;
        this.context = context;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        userId = firebaseUser.getUid();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.friend_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        reference.child("Users").child(userKeyList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userName = snapshot.child("profileUsername").getValue().toString();
                String userImage = snapshot.child("profilePhoto").getValue().toString();
                Boolean stateUser = Boolean.parseBoolean(snapshot.child("state").getValue().toString());
                if (stateUser == true){
                    holder.friendState.setImageResource(R.drawable.online_icon);
                }
                else {
                    holder.friendState.setImageResource(R.drawable.offline_icon);
                }

                Picasso.get().load(userImage).into(holder.friendsImage);
                holder.friendsText.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userKeyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView friendsText;
        CircleImageView friendsImage,friendState;

        ViewHolder(View itemView) {
            super(itemView);
            friendsText = itemView.findViewById(R.id.friends_text);
            friendsImage = itemView.findViewById(R.id.friends_image);
            friendState = itemView.findViewById(R.id.friends_state_img);
        }
    }
}
