package com.example.fastkafoodappandroid.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastkafoodappandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    TextView txtLogin;
    EditText edUsername,edPassword,edEmail,edConfPass;
    ProgressBar bar;
    FirebaseAuth mAuth;
    Button btnRegister;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        //   AnhXa();
        edUsername = findViewById(R.id.edUsername);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        edConfPass = findViewById(R.id.edConfigPass);
        bar=(ProgressBar)findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        txtLogin = findViewById(R.id.txt_login);
        txtLogin.setOnClickListener(this);
//        txtLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
//                startActivity(intent);
//
//            }
//        });



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.txt_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btnRegister:
                RegisterUser();
                break;
        }
    }

    private void RegisterUser() {
        String name = edUsername.getText().toString().trim();
        String email=edEmail.getText().toString().trim();
        String password=edPassword.getText().toString().trim();
        String configPass=edConfPass.getText().toString().trim();

        if(name.isEmpty()){
            edUsername.setError("Please input username");
            edUsername.requestFocus();
            return;
        }
        if(email.isEmpty()){
            edEmail.setError("Please input email");
            edEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edEmail.setError("Email not value ");
            edEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            edPassword.setError("Please input password");
            edPassword.requestFocus();
            return;
        }
        if(password.length() < 6){
            edPassword.setError("Enter maximum 6 digits");
            edPassword.requestFocus();
        }


        if(configPass.isEmpty()) {
            edConfPass.setError("Please re-enter your password");
            edConfPass.requestFocus();
            return;
        }



        if(password.equals(configPass)){
            bar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                User user = new User(name,email,password);


                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(RegisterActivity.this,"Register Successfully",Toast.LENGTH_SHORT).show();
                                            bar.setVisibility(View.VISIBLE);
                                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                        }
                                        else {
                                            Toast.makeText(RegisterActivity.this,"Failed to register ! Try again ",Toast.LENGTH_SHORT).show();
                                            bar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(RegisterActivity.this,"Failed to register",Toast.LENGTH_SHORT).show();
                                bar.setVisibility(View.GONE);
                            }
                        }
                    });
        }else {
            Toast.makeText(RegisterActivity.this, "Password incorrect", Toast.LENGTH_SHORT).show();
            edPassword.setText("");
            edConfPass.setText("");
        }





    }



}