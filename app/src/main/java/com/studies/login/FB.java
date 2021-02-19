package com.studies.login;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FB extends AppCompatActivity {

    FirebaseAuth mAuth;
    ImageView imageView;
    TextView t1, t2;
    String t;
    Button button;

    @SuppressLint("StaticFieldLeak")
    static
    class getPic extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {

            URL url = null;
            InputStream in = null;
            try {
                url = new URL(strings[0]);
                Log.i("URL", url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection;
            try {
                assert url != null;
                urlConnection = (HttpURLConnection) url.openConnection();
                Log.i("connection", "done");
                assert urlConnection != null;
                urlConnection.connect();
                Log.i("connection", "connected");
                in = urlConnection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return BitmapFactory.decodeStream(in);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_b);
        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.imageView2);
        t1 = findViewById(R.id.textView);
        t2 = findViewById(R.id.textView3);
        button = findViewById(R.id.button1);

        t1.setVisibility(View.VISIBLE);
        t2.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                finish();
            }
        });

        t = getIntent().getStringExtra("url");

        t1.setText("Name: "+getIntent().getStringExtra("name"));
        t2.setText("Email: "+getIntent().getStringExtra("email"));

        getPic get = new getPic();
        Bitmap b = null;
        try {
            b = get.execute(Objects.requireNonNull(t)).get();
            Log.i("url",t);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        assert b != null;
        imageView.setImageBitmap(Bitmap.createScaledBitmap(b,500,500,false));
    }
}