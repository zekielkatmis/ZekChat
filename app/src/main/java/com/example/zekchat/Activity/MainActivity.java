package com.example.zekchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.zekchat.Fragments.MainUserProfileFragment;
import com.example.zekchat.Fragments.NotificationFragment;
import com.example.zekchat.Utils.ChangeFragment;
import com.example.zekchat.Fragments.HomeFragment;
import com.example.zekchat.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ChangeFragment changeFragment;
    private FirebaseAuth  auth;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        define();
        control();

        changeFragment = new ChangeFragment(MainActivity.this);
        changeFragment.change(new HomeFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_home:
                    changeFragment.change(new HomeFragment());
                    return true;
                case R.id.nav_notification:
                        changeFragment.change(new NotificationFragment());

                    return true;
                case R.id.nav_user_profile:
                    changeFragment.change(new MainUserProfileFragment());
                    return true;

                case R.id.nav_exit:
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Are you sure want to exit?");
                    builder.setCancelable(true);
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            exit();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return true;
            }
            return false;
        }
    };

    public void define(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    public void control(){
        if (user == null){
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
        else {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference reference = firebaseDatabase.getReference().child("Users");
            reference.child(user.getUid()).child("state").setValue(true);
        }
    }

    public void exit(){
        auth.signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference().child("Users");
        reference.child(user.getUid()).child("state").setValue(false);
    }

    //ACTIVITY LIFE CYCLE
    @Override
    //if the application is put into the background
    protected void onStop() {
        super.onStop();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference().child("Users");
        reference.child(user.getUid()).child("state").setValue(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference().child("Users");
        reference.child(user.getUid()).child("state").setValue(true);

    }
}