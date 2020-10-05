package com.example.zekchat.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zekchat.Fragments.OtherUserFragment;
import com.example.zekchat.Models.Users;
import com.example.zekchat.R;
import com.example.zekchat.Utils.ChangeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    List<String> userKeyList;
    Activity activity;
    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    String userId;

    public FriendRequestAdapter(List<String> userKeyList, Activity activity, Context context) {
        this.userKeyList = userKeyList;
        this.activity = activity;
        this.context = context;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        userId = firebaseUser.getUid();
    }

    // Define layout
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.friend_request_layout, parent, false);
        return new ViewHolder(view);
    }

    // Setting to views
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        //holder.user_name.setText(userKeyList.get(position).toString());

        //We can get user information from here.
        reference.child("Users").child(userKeyList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                Picasso.get().load(users.getProfilePhoto()).into(holder.requestProfileImage);
                holder.requestProfileName.setText(users.getProfileUsername());

                holder.requestAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptRequest(userId, userKeyList.get(position));
                    }
                });

                holder.requestDecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        declineRequest(userId, userKeyList.get(position));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // List size of Adapter
    @Override
    public int getItemCount() {
        return userKeyList.size();
    }

    // Define of views.
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView requestProfileName;
        CircleImageView requestProfileImage;
        Button requestAccept, requestDecline;

        ViewHolder(View itemView) {
            super(itemView);
            requestProfileName = itemView.findViewById(R.id.request_profile_name);
            requestProfileImage = itemView.findViewById(R.id.request_profile_image);
            requestAccept = itemView.findViewById(R.id.request_accept);
            requestDecline = itemView.findViewById(R.id.request_decline);
        }
    }

    private void acceptRequest(final String userId, final String otherId) {

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Date today = Calendar.getInstance().getTime();

        final String reportDate = df.format(today);

        reference.child("Users").child(userId).child(otherId).child("date").setValue(reportDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                reference.child("Users").child(otherId).child(userId).child("date").setValue(reportDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(context, "Friend request accepted.", Toast.LENGTH_SHORT).show();

                            reference.child("FriendRequest").child(userId).child(otherId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    reference.child("FriendRequest").child(otherId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "Friend request denied.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void declineRequest(final String userId, final String otherId) {
        reference.child("FriendRequest").child(userId).child(otherId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reference.child("FriendRequest").child(otherId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Friend request denied.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

}
