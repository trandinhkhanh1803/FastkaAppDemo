package com.example.fastkafoodappandroid.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastkafoodappandroid.MainActivity;
import com.example.fastkafoodappandroid.R;
import com.example.fastkafoodappandroid.UI.SendOTPActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    TextView txtRegister,forgotPassword;
    EditText edUser, edPass;
    ProgressBar bar;
    FirebaseAuth mAuth;
    Button btnLogin;


    public static final String TAG = "GoogleSignIn";
    public static final int RC_SIGN_IN = 321;
    private SignInButton btnSignInWithGoogle;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        txtRegister = findViewById(R.id.txt_register);
        edUser = findViewById(R.id.edEmailUser);
        edPass = findViewById(R.id.edPass);
        bar = (ProgressBar) findViewById(R.id.progressBar3);
        mAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnlogin);
        forgotPassword = findViewById(R.id.txt_forgotPassword);

        forgotPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        txtRegister.setOnClickListener(this);

//
        btnSignInWithGoogle = findViewById(R.id.btnGoogleSignIn);
        mAuth = FirebaseAuth.getInstance();
        requestGoogleSignIn();

        btnSignInWithGoogle.setOnClickListener(view -> {
            signIn();
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.btnlogin:
                userLogin();
                break;
            case R.id.txt_forgotPassword:
                startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
        }
    }

    private void userLogin() {
        String email = edUser.getText().toString().trim();
        String pass = edPass.getText().toString().trim();


        if(email.isEmpty()){
            edUser.setError("Please input email");
            edUser.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edUser.setError("Email not value ");
            edUser.requestFocus();
            return;
        }
        if(pass.isEmpty()){
            edPass.setError("Please input password");
            edPass.requestFocus();
            return;
        }
        if(pass.length() < 6){
            edPass.setError("Enter maximum 6 digits");
            edPass.requestFocus();
        }


        bar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    if (user.isEmailVerified()){
//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    }
//                   else {
//                       user.sendEmailVerification();
//                        Toast.makeText(LoginActivity.this,"Check your email to verify your account",Toast.LENGTH_SHORT).show();
//                    }
                }
                else {
                    Toast.makeText(LoginActivity.this,"Login Failed ! Try again",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestGoogleSignIn(){
        // Configure sign-in to request the user’s basic profile like name and email
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }



    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

private void firebaseAuthWithGoogle(String idToken) {

    //getting user credentials with the help of AuthCredential method and also passing user Token Id.
    AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

    //trying to sign in user using signInWithCredential and passing above credentials of user.
    mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        Log.d(TAG, "signInWithCredential:success");

                        // Sign in success, navigate user to Profile Activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(LoginActivity.this, "User authentication failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
}


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating user with firebase using received token id
                firebaseAuthWithGoogle(account.getIdToken());

                //assigning user information to variables
                String userName = account.getDisplayName();
                String userEmail = account.getEmail();
                String userPhoto = account.getPhotoUrl().toString();
                userPhoto = userPhoto+"?type=large";

                //create sharedPreference to store user data when user signs in successfully
                SharedPreferences.Editor editor = getApplicationContext()
                        .getSharedPreferences("MyPrefs",MODE_PRIVATE)
                        .edit();
                editor.putString("username", userName);
                editor.putString("useremail", userEmail);
                editor.putString("userPhoto", userPhoto);
                editor.apply();

                Log.i(TAG, "onActivityResult: Success");

            } catch (ApiException e) {
                Log.e(TAG, "onActivityResult: " + e.getMessage());
            }
        }
    }


}