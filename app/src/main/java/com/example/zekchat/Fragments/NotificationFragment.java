package com.example.zekchat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;

import com.example.zekchat.Adapters.FriendRequestAdapter;
import com.example.zekchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {

    View view;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String userId;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<String> friendRequestKeyList;
    RecyclerView requestRecyclerView;
    FriendRequestAdapter friendRequestAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notification, container, false);
        define();
        friendRequests();
        return view;
    }

    public void define() {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        userId = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("FriendRequest");

        friendRequestKeyList = new ArrayList<>();

        requestRecyclerView = view.findViewById(R.id.request_RecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        requestRecyclerView.setLayoutManager(layoutManager);

        friendRequestAdapter = new FriendRequestAdapter(friendRequestKeyList, getActivity(), getContext());
        requestRecyclerView.setAdapter(friendRequestAdapter);
    }

    public void friendRequests() {
        databaseReference.child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String control = snapshot.child("type").getValue().toString();
                if (control.equals("getRequest")) {
                    friendRequestKeyList.add(snapshot.getKey());
                    friendRequestAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(snapshot.getKey())) {
                    String control = snapshot.child(userId).child("type").getValue().toString();
                    if (control.equals("getRequest")) {
                        friendRequestKeyList.add(snapshot.getKey());
                        friendRequestAdapter.notifyDataSetChanged();
                    }
                }
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