package com.example.firstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseUser curUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef, UsersRef;

    private EditText Email, Password;
    private Button RegButton;
    private TextView LoginLink;

    private ProgressDialog loadingBar;

    private String currentUserID, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UsersRef = RootRef.child("Users");

        InitializeElements();

        RegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });

        LoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
    }

    private void CreateNewAccount() {
        email = Email.getText().toString();
        password = Password.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, "?????????????? ?????????? ????. ??????????", Toast.LENGTH_SHORT).show();
        }
        else if (password.isEmpty()) {
            Toast.makeText(this, "?????????????? ????????????", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("??????????????????????");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUserID = mAuth.getCurrentUser().getUid();
                                UsersRef.child(currentUserID).child("email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                            // ???????????? ?????????????? ???????????????? => ?????????????? ???? ??????????????????????
                                        if (task.isSuccessful()) {
                                            mAuth.signOut();    // ???????????? ?????????? ?????????????????????? ?????????? ??????????
                                            SendUserToLoginActivity();
                                            Toast.makeText(RegisterActivity.this, "???????????????? ??????????????????????!", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            String message = task.getException().toString();
                                            Toast.makeText(RegisterActivity.this, "????????????: " + message, Toast.LENGTH_SHORT).show();
                                        }
                                        loadingBar.dismiss();
                                    }
                                });
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "????????????: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    // ?????????????????????????? ?????????????????? ????????????????????
    private void InitializeElements() {
        Email = findViewById(R.id.email_et);
        Password = findViewById(R.id.password_et);
        RegButton = findViewById(R.id.reg_button);
        LoginLink = findViewById(R.id.login_tv_link);
        loadingBar = new ProgressDialog(this);
    }

    // ???????????????? ???? ???????????? ????????????????????
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        this.finish();
    }
}