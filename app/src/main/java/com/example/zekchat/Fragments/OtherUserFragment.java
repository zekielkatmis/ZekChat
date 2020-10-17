package com.example.zekchat.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zekchat.Activity.ChatActivity;
import com.example.zekchat.Models.Users;
import com.example.zekchat.R;
import com.example.zekchat.Utils.ShowToastMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserFragment extends Fragment {

    View view;
    String otherUserId, userId;
    CircleImageView otherUserProfileImage;
    ImageView favoriteProfile, otherUserMessage, otherUserAddFriend;
    TextView otherUserProfileName, otherUserName, otherUserEducation, otherUserBirthday, otherUserAbout, otherUserFav, otherUserFriend;

    FirebaseDatabase database;
    DatabaseReference reference, friendRequestReference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    String control = "";
    String favControl = "";

    ShowToastMessage showToastMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_other_user, container, false);
        define();
        action();
        setFavoriteText();
        setFriendText();
        return view;
    }

    public void define() {

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        friendRequestReference = database.getReference().child("FriendRequest");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        userId = firebaseUser.getUid();

        otherUserId = getArguments().getString("userId");

        otherUserName = view.findViewById(R.id.other_user_name);
        otherUserEducation = view.findViewById(R.id.other_user_education);
        otherUserBirthday = view.findViewById(R.id.other_user_birthday);
        otherUserAbout = view.findViewById(R.id.other_user_about);
        otherUserFav = view.findViewById(R.id.other_user_fav);
        otherUserFriend = view.findViewById(R.id.other_user_friend);
        otherUserProfileName = view.findViewById(R.id.other_user_profileName);

        favoriteProfile = view.findViewById(R.id.favorite_profile);
        otherUserAddFriend = view.findViewById(R.id.other_user_addFriend);
        otherUserMessage = view.findViewById(R.id.other_user_message);
        otherUserProfileImage = view.findViewById(R.id.other_user_profile_image);

        showToastMessage = new ShowToastMessage(getContext());

        friendRequestReference.child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userId)) {
                    control = "request";
                    otherUserAddFriend.setImageResource(R.drawable.ic_baseline_friend_add_disabled_24);
                } else {
                    otherUserAddFriend.setImageResource(R.drawable.ic_baseline_add_friend_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        friendRequestReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(otherUserId)) {
                    control = "friend";
                    otherUserAddFriend.setImageResource(R.drawable.ic_baseline_remove_circle_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("Favorites").child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userId)){
                    favControl = "liked";
                    favoriteProfile.setImageResource(R.drawable.ic_baseline_favorite_24);
                }
                else {

                    favoriteProfile.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void action() {

        reference.child("Users").child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Users u1 = snapshot.getValue(Users.class);
                otherUserName.setText("Name: " + u1.getProfileUsername());
                otherUserEducation.setText("Education: " + u1.getProfileEducation());
                otherUserBirthday.setText("Birthday: " + u1.getProfileBirthday());
                otherUserAbout.setText("About: " + u1.getProfileAbout());
                otherUserProfileName.setText(u1.getProfileUsername());

                if (!u1.getProfilePhoto().equals("null")) {
                    Picasso.get().load(u1.getProfilePhoto()).into(otherUserProfileImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        otherUserAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (control.equals("request")) {
                    cancelAddFriend(otherUserId, userId);
                } else if (control.equals("friend")) {
                    removeFromTable(otherUserId, userId);

                } else {
                    addFriend(otherUserId, userId);
                }
            }
        });

        favoriteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favControl.equals("liked")){
                    cancelFavProfile(userId, otherUserId);
                }
                else {
                    favProfile(userId, otherUserId);
                }
            }
        });

        otherUserMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("otherUserName", otherUserProfileName.getText().toString());
                intent.putExtra("otherId", otherUserId);
                startActivity(intent);
            }
        });
    }

    private void removeFromTable(final String otherUserId, final String userId) {

        friendRequestReference.child(otherUserId).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                friendRequestReference.child(userId).child(otherUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        control = "";

                        otherUserAddFriend.setImageResource(R.drawable.ic_baseline_add_friend_24);
                        showToastMessage.showToast("Removed from friendship.");
                        setFriendText();
                    }
                });
            }
        });
    }

    public void addFriend(final String otherId, final String userId1) {
        friendRequestReference.child(userId1).child(otherId).child("type").setValue("sendRequest").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    friendRequestReference.child(otherId).child(userId1).child("type").setValue("getRequest")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        control = "request";
                                        showToastMessage.showToast("Friend request successfully sent.");
                                        otherUserAddFriend.setImageResource(R.drawable.ic_baseline_friend_add_disabled_24);
                                    } else {
                                        showToastMessage.showToast("Failed..");
                                    }
                                }
                            });
                } else {
                    showToastMessage.showToast("Failed..");
                }
            }
        });
    }

    private void cancelAddFriend(final String otherUserId, final String userId2) {
        friendRequestReference.child(otherUserId).child(userId2).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                friendRequestReference.child(userId2).child(otherUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        control = "";

                        otherUserAddFriend.setImageResource(R.drawable.ic_baseline_add_friend_24);
                        showToastMessage.showToast("Friend request cancelled");
                    }
                });
            }
        });
    }

    public void favProfile(String userId, String otherUserId){

        reference.child("Favorites").child(otherUserId).child(userId).child("type").setValue("Like").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showToastMessage.showToast("Like Profile");
                    favoriteProfile.setImageResource(R.drawable.ic_baseline_favorite_24);
                    favControl = "liked";
                    setFavoriteText();
                }
            }
        });
    }

    private void cancelFavProfile(String userId, String otherUserId) {
        reference.child("Favorites").child(otherUserId).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                favoriteProfile.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                favControl = "";
                showToastMessage.showToast("Unlike Profile.");
                setFavoriteText();
            }
        });
    }

    public void setFavoriteText(){

        //addChildEventListener can not return 0 amount so we used addListenerForSingleValueEvent for
        //showing friend and liked number

        //if we used addChildEventListener we have to create new arraylist for showing numbers of friend and liked

        reference.child("Favorites").child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                otherUserFav.setText(snapshot.getChildrenCount() + " Like");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setFriendText(){

        friendRequestReference.child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                otherUserFriend.setText(snapshot.getChildrenCount() + " Friend");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}