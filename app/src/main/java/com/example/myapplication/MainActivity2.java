package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity2 extends AppCompatActivity {

    private ImageButton camera, remote, telemtry, setting;

    private Button locate, stop;

    private boolean hasInternet;

    private ConstraintLayout layout, layout2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        camera = findViewById(R.id.cameraButton);
        remote = findViewById(R.id.freeControl);
        setting = findViewById(R.id.settingsButton);
        telemtry = findViewById(R.id.telemetryButton);
        stop = findViewById(R.id.stopButton);
        locate = findViewById(R.id.locateButton);
        layout = findViewById(R.id.constraintLayout_2);
        layout2 = findViewById(R.id.waiting_net_2);

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


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openCameraMenu();
                // TODO - CHECK RASP RESPONSE
            }

            private void openCameraMenu() {
                //TODO Criar as activity
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }
        });

        remote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openRemoteMenu();
                // TODO - CHECK RASP RESPONSE
            }

            private void openRemoteMenu() {
                Intent intent = new Intent(getApplicationContext(), MainActivity3.class);
                startActivity(intent);
            }
        });

        telemtry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openTelemetryMenu();
                // TODO - CHECK RASP RESPONSE
            }

            private void openTelemetryMenu() {
                //TODO Criar as activity
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingMenu();
                // TODO - CHECK RASP RESPONSE
            }

            private void openSettingMenu() {
                //TODO Criar as activity
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openStopMenu();
                // TODO - CHECK RASP RESPONSE
            }

            private void openStopMenu() {
                //TODO Criar as activity
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }
        });
        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openLocateMenu();
                // TODO - CHECK RASP RESPONSE
            }

            private void openLocateMenu() {
                //TODO Criar as activity
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