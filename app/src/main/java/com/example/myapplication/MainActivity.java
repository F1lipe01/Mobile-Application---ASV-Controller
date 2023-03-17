package com.example.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    private boolean hasInternet;

    private GifImageView noSignalGifImageView;
    private ImageView wifiSignalImageView;

    private TextView sendingMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noSignalGifImageView = findViewById(R.id.no_signal_gif);
        wifiSignalImageView = findViewById(R.id.wifi);

        sendingMessage = findViewById(R.id.message);

        // Check if device has internet
        hasInternet = isNetworkAvailable();
        updateSignalImage(false);

        // Periodically check for network connectivity
        Handler handler = new Handler();
        int delay = 3000; // delay for 5 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isConnected = isNetworkAvailable();
                if (hasInternet != isConnected) {
                    hasInternet = isConnected;
                    updateSignalImage(true);
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void updateSignalImage(boolean animate) {
        if (hasInternet) {
            sendingMessage.setText("Connected");
            noSignalGifImageView.setVisibility(GifImageView.GONE);
            wifiSignalImageView.setVisibility(ImageView.VISIBLE);
        } else {
            sendingMessage.setText("Waiting for connection");
            noSignalGifImageView.setVisibility(GifImageView.VISIBLE);
            wifiSignalImageView.setVisibility(ImageView.GONE);
        }

        // Apply animation if necessary
        /*
        if (animate) {
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(500);
            fadeOut.setFillAfter(true);
            noSignalGifImageView.startAnimation(fadeOut);

            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(500);
            fadeIn.setFillAfter(true);
            wifiSignalImageView.startAnimation(fadeIn);
        }
        */
    }
}
