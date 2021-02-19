package com.studies.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fbAuth;
    CallbackManager mCallbackManager;
    Button FbLoginButton;
    Button GoogleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fbAuth = FirebaseAuth.getInstance();
        GoogleButton = findViewById(R.id.textG);

        mCallbackManager = CallbackManager.Factory.create();

        FbLoginButton = findViewById(R.id.textFB);

        GoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Google.class);
                startActivity(intent);
            }
        });

        FbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,Arrays.asList("email", "public_profile"));
            }
        });

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("here", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i("here", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                Log.i("here",error.toString());
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            // The activity result pass back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(final AccessToken token) {
        Log.i("here", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i("here", "signInWithCredential:success");
                            FirebaseUser user = fbAuth.getCurrentUser();

                            assert user != null;
                            String s;
                            String t;
                            String name,email;
                            for (UserInfo profile : user.getProviderData()) {
                                // check if the provider id matches "facebook.com"
                                if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                                    s = profile.getUid();
                                    name = profile.getDisplayName();
                                    email=profile.getEmail();
                                    t = "https://graph.facebook.com/" + s+"/picture?type=large";
                                    Intent intent = new Intent(getApplicationContext(),FB.class);
                                    intent.putExtra("name",name);
                                    intent.putExtra("email",email);
                                    intent.putExtra("url",t);
                                    startActivityForResult(intent,1);
                                }
                            }
                        }
                    }
                });
    }

}