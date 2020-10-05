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

public class Login extends AppCompatActivity {

    private EditText input_email_login, input_password_login;
    private Button loginButton;
    FirebaseAuth auth;
    TextView controlText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        define();
    }


    public void define() {

        input_email_login = findViewById(R.id.input_email_login);
        input_password_login = findViewById(R.id.input_password_login);
        loginButton = findViewById(R.id.loginButton);
        controlText2 = findViewById(R.id.controlText_2);
        auth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = input_email_login.getText().toString();
                String password = input_password_login.getText().toString();
                if (!email.equals("") && !password.equals("")) {
                    login(email, password);
                } else {
                    Toast.makeText(Login.this, "Failed, enter email or password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        controlText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignIn.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void login(String email, String password) {

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Wrong email or password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}