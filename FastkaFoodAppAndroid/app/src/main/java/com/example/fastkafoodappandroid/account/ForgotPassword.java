package com.example.fastkafoodappandroid.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fastkafoodappandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private Button btnReset;
    private EditText edEmail;
    private ProgressBar bar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edEmail = findViewById(R.id.edEmailRp);
        btnReset = findViewById(R.id.btnResetPass);
        bar = findViewById(R.id.progressBar1);
        auth = FirebaseAuth.getInstance();


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }

        });
    }

    private void resetPassword() {
        String email = edEmail.getText().toString().trim();

        if (email.isEmpty()){
            edEmail.setError("Input Email");
            edEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError("Email not value ");
            edEmail.requestFocus();
            return;
        }

        bar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPassword.this,"Check your mail to reset your password!",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(ForgotPassword.this,"Reset Password failed ! Try again",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}