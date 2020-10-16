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

import com.example.zekchat.Adapters.UserAdapter;
import com.example.zekchat.Models.Users;
import com.example.zekchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    List<String> userKeyList;
    RecyclerView user_RecyclerView;
    View view;
    UserAdapter userAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);
        define();
        getUsers();
        // Inflate the layout for this fragment
        return view;
    }

    public void define(){
        userKeyList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference();
        user_RecyclerView = view.findViewById(R.id.user_RecyclerView);

        userAdapter = new UserAdapter(userKeyList, getActivity(), getContext());
        user_RecyclerView.setAdapter(userAdapter);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        //We can put 2 cells in 1 row.
        RecyclerView.LayoutManager mng = new GridLayoutManager(getContext(), 2);
        user_RecyclerView.setLayoutManager(mng);
    }

    public void getUsers(){
        reference.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                //We can get non-null user information from here.
                //The logged in user's profile will not appear on the main page.
                reference.child("Users").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);

                        if (!users.getProfileUsername().equals("null") && !snapshot.getKey().equals(firebaseUser.getUid())){
                            if (userKeyList.indexOf(snapshot.getKey()) == -1){
                                userKeyList.add(snapshot.getKey());
                            }
                            userAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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