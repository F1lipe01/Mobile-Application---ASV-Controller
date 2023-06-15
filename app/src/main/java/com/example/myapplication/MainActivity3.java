package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
public class MainActivity3 extends AppCompatActivity {

    private ImageButton free, task, back;

    private ConstraintLayout layout, layout2;

    private boolean hasInternet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        free = findViewById(R.id.freeControl);
        task = findViewById(R.id.taskControl);
        back = findViewById(R.id.backButton);
        layout = findViewById(R.id.constraintLayout);
        layout2 = findViewById(R.id.waiting_net_3);

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

        free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFreeMenu();
            }

            private void openFreeMenu() {
                //Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
                //startActivity(intent);
            }
        });

        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTaskMenu();
            }

            private void openTaskMenu() {
                Intent intent = new Intent(getApplicationContext(), MainActivity5.class);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBackMenu();
            }

            private void openBackMenu() {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
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
            layout.setVisibility(ConstraintLayout.VISIBLE);
            layout2.setVisibility(ConstraintLayout.GONE);
        } else {
            layout.setVisibility(ConstraintLayout.GONE);
            layout2.setVisibility(ConstraintLayout.VISIBLE);
        }
    }
}