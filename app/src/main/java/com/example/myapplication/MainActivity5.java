package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity5 extends AppCompatActivity {

    private Button task1, task2, task3, task4;

    private ImageButton back2;

    private ImageView connect;

    private TextView errorMessage, rasp_text;

    private GifImageView noSignalGifImageView;

    private ConstraintLayout layout, layout2, sending;
    private boolean hasInternet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);

        task1 = findViewById(R.id.task1);
        task2 = findViewById(R.id.task2);
        task3 = findViewById(R.id.task3);
        task4 = findViewById(R.id.task4);
        back2 = findViewById(R.id.backButton2);
        errorMessage = findViewById(R.id.errorMessage2);
        rasp_text = findViewById(R.id.rasp_text);
        noSignalGifImageView = findViewById(R.id.no_signal_gif2);
        layout = findViewById(R.id.constraintLayout_2);
        layout2 = findViewById(R.id.waiting_net_2);
        sending = findViewById(R.id.sending_rasp);
        connect = findViewById(R.id.connected);
        errorMessage.setVisibility(TextView.GONE);
        layout2.setVisibility(ConstraintLayout.GONE);

        //CONNECT TO RASP
        connect.setVisibility(ImageView.GONE);
        //rasp_text.setText("....")

        updateImage2(false);

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


        task1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(taskSuccess()) {
                    updateImage2(true);
                }
                else{
                    errorMessage.setVisibility(TextView.VISIBLE);
                }
            }

            private boolean taskSuccess(){
                //TODO - Task sent confirmation
                return true;
            }
        });

        task2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(taskSuccess()) {
                    //TODO - Task1
                    // TODO - CHECK RASP RESPONSE
                }
                else{
                    errorMessage.setVisibility(TextView.VISIBLE);
                }
            }

            private boolean taskSuccess(){
                //TODO - Task sent confirmation
                return true;
            }
        });

        task3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(taskSuccess()) {
                    //TODO - Task1
                    // TODO - CHECK RASP RESPONSE
                }
                else{
                    errorMessage.setVisibility(TextView.VISIBLE);
                }
            }

            private boolean taskSuccess(){
                //TODO - Task sent confirmation
                return false;
            }
        });

        task4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(taskSuccess()) {
                    //TODO - Task1
                    // TODO - CHECK RASP RESPONSE
                }
                else{
                    errorMessage.setVisibility(TextView.VISIBLE);
                }
            }

            private boolean taskSuccess(){
                //TODO - Task sent confirmation
                return true;
            }
        });

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openBackMenu2();
                // TODO - CHECK RASP RESPONSE
            }

            private void openBackMenu2() {
                Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
                startActivity(intent);
            }
        });


        }

    private boolean isNetworkAvailable () {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        return false;
    }

    private void updateSignalImage(boolean animate) {
        if (hasInternet) {
            layout.setVisibility(ConstraintLayout.VISIBLE);
            layout2.setVisibility(ConstraintLayout.GONE);
            errorMessage.setVisibility(TextView.INVISIBLE);
            layout.setAlpha((float) 1);
            task1.setEnabled(true);
            task2.setEnabled(true);
            task3.setEnabled(true);
            task4.setEnabled(true);
            back2.setEnabled(true);
        } else {
            layout.setVisibility(ConstraintLayout.GONE);
            sending.setVisibility(ConstraintLayout.GONE);
            layout2.setVisibility(ConstraintLayout.VISIBLE);
        }
    }
    private void updateImage2(boolean exp) {
        if (exp) {
            layout.setAlpha((float) 0.2);
            layout2.setVisibility(ConstraintLayout.GONE);
            errorMessage.setVisibility(TextView.INVISIBLE);
            sending.setVisibility(ConstraintLayout.VISIBLE);
            task1.setEnabled(false);
            task2.setEnabled(false);
            task3.setEnabled(false);
            task4.setEnabled(false);
            back2.setEnabled(false);
        } else {
            layout.setAlpha((float) 1);
            layout2.setVisibility(ConstraintLayout.GONE);
            sending.setVisibility(ConstraintLayout.GONE);
            task1.setEnabled(true);
            task2.setEnabled(true);
            task3.setEnabled(true);
            task4.setEnabled(true);
            back2.setEnabled(true);
        }
    }
}