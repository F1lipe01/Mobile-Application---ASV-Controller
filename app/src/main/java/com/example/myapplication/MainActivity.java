package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    private boolean hasInternet;

    private GifImageView noSignalGifImageView;
    private ImageView wifiSignalImageView;

    private TextView sendingMessage;
    private RelativeLayout form;

    private TextView errorMessage;

    private Button confirmButton;

    private EditText inputPin;

    private ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noSignalGifImageView = findViewById(R.id.no_signal_gif);
        wifiSignalImageView = findViewById(R.id.wifi);
        sendingMessage = findViewById(R.id.message);
        errorMessage = findViewById(R.id.errorMessage);
        form = findViewById(R.id.form);
        confirmButton = findViewById(R.id.button);
        inputPin = findViewById(R.id.inputPin);
        loadingBar = findViewById(R.id.progressBar);

        errorMessage.setVisibility(TextView.GONE);
        confirmButton.setEnabled(false);
        loadingBar.setProgress(0);
        loadingBar.setVisibility(View.INVISIBLE);

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

        inputPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called during the text change
                String input = s.toString().trim();
                if (input.length() == 4) {
                    confirmButton.setEnabled(true);
                } else {
                    confirmButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text has been changed
                String newPin = s.toString();
                // Do something with the newPin value
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()){
                    errorMessage.setVisibility(TextView.GONE);
                    handleLoadingState();
                    openActivity2();
                    // TODO - CHECK RASP RESPONSE
                }
                else errorMessage.setVisibility(TextView.VISIBLE);
            }

            private void openActivity2(){
                Intent intent =new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }
            private boolean validateInput(){
                String pin = inputPin.getText().toString();
                if(pin.equals("1234")) return true;
                else return false;
            }

            private boolean handleLoadingState(){
                confirmButton.setText("");
                confirmButton.setEnabled(false);
                loadingBar.setVisibility(View.VISIBLE);
                inputPin.setEnabled(false);
                return false;
            }

            private void stopLoadingState(boolean somethingWentWrong){

                if(somethingWentWrong){
                    confirmButton.setText("Confirm");
                    confirmButton.setEnabled(true);
                    loadingBar.setProgress(0);
                    loadingBar.setVisibility(View.INVISIBLE);
                    inputPin.setEnabled(true);
                }
                else{
                    // TODO - LOAD NEXT ACTIVITY
                }

            }

        });
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
            form.setVisibility(RelativeLayout.VISIBLE);
        } else {
            sendingMessage.setText("Waiting for connection");
            noSignalGifImageView.setVisibility(GifImageView.VISIBLE);
            wifiSignalImageView.setVisibility(ImageView.GONE);
            form.setVisibility(RelativeLayout.GONE);
        }

    }
}
