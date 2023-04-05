package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity5 extends AppCompatActivity {

    private Button task1, task2, task3, task4;

    private ImageButton back2;


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
                //TODO - Task1
                // TODO - CHECK RASP RESPONSE
            }
        });
        task2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - Task1
                // TODO - CHECK RASP RESPONSE
            }
        });
        task3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - Task1
                // TODO - CHECK RASP RESPONSE
            }
        });
        task4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO - Task1
                // TODO - CHECK RASP RESPONSE
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

    private void updateSignalImage ( boolean animate){
        if (hasInternet) {
            //TODO LAYOUT SEM NET
        } else {

        }
    }
}