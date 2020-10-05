package com.example.zekchat.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zekchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    EditText input_email, input_password;
    Button registerButton;
    FirebaseAuth auth;
    TextView controlText;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Define();
    }

    public void Define(){
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        registerButton = findViewById(R.id.registerButton);
        controlText = findViewById(R.id.controlText);
        auth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = input_email.getText().toString();
                String password = input_password.getText().toString();

                if(!email.equals("")&&!password.equals("")){
                    input_email.setText("");
                    input_password.setText("");
                    Register(email, password);
                }
                else {
                    Toast.makeText(getApplicationContext(), "You cannot empty email or password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        controlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void Register(String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference().child("Users").child(auth.getUid());

                    Map map = new HashMap();

                    map.put("profilePhoto", "null");
                    map.put("profileUsername", "null");
                    map.put("profileEducation", "null");
                    map.put("profileBirthday", "null");
                    map.put("profileAbout", "null");

                    databaseReference.setValue(map);

                    Intent intent = new Intent(SignIn.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Failed while registered.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}