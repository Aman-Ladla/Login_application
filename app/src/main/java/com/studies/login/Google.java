package com.studies.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


public class Google extends AppCompatActivity {

    GoogleSignInClient client;
    TextView t1,t2;
    ImageView imageView;
    Button button;


    @SuppressLint("StaticFieldLeak")
    static
    class getPic extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {

            URL url = null;
            InputStream in = null;
            try {
                url = new URL(strings[0]);
                Log.i("URL",url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection;
            try {
                assert url != null;
                urlConnection = (HttpURLConnection)url.openConnection();
                Log.i("connection","done");
                assert urlConnection != null;
                urlConnection.connect();
                Log.i("connection","connected");
                in = urlConnection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return BitmapFactory.decodeStream(in);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0) {
            if (resultCode == -1){
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                Log.i("ResultCode", resultCode + "");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                t1.append(" "+account.getDisplayName());
                t2.append(" "+account.getEmail());
                t1.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                t2.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                getPic get = new getPic();
                Bitmap b = get.execute(Objects.requireNonNull(account.getPhotoUrl()).toString()).get();
                imageView.setImageBitmap(b);
            } catch (ApiException e) {
                Log.i("hello", "Google sign in failed");
                e.printStackTrace();
            } catch (InterruptedException | ExecutionException e) {
                Log.i("hello","problem");
                e.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
            if(resultCode==0){
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);

        t1 = findViewById(R.id.textView);
        t2 = findViewById(R.id.textView3);
        imageView= findViewById(R.id.imageView2);
        button = findViewById(R.id.button);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        client = GoogleSignIn.getClient(Google.this,gso);


        Log.i("client",client.toString());

        Intent intent = client.getSignInIntent();
        startActivityForResult(intent,0);

        button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               client.signOut();
               finish();
           }
       });
    }
}