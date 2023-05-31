package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


public class MainActivity6 extends AppCompatActivity {

    private static final String TAG = "MainActivity6";
    private ImageButton back2;
    private ImageView task_img;
    private TextView errorMessage, task_msg, current_task, current_state;
    private View shape;
    private boolean hasInternet;
    private TextView cpu_temp, bat_temp, lat, longe, bat_perc, dir, cur_task;
    private ConstraintLayout constraintlayout, offlineLayout;

    private String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        //PARÂMETROS
        cpu_temp = findViewById(R.id.cpu);
        bat_temp = findViewById(R.id.batteries_temp);
        longe = findViewById(R.id.longitude);
        lat = findViewById(R.id.latitude);
        bat_perc = findViewById(R.id.batteries);
        dir = findViewById(R.id.direction);
        cur_task = findViewById(R.id.task);

        //EXCLUSIVO ESTADO MANEUVERING
        task_img = findViewById(R.id.task_img);
        task_msg = findViewById(R.id.task_text);
        current_task = findViewById(R.id.task);

        back2 = findViewById(R.id.backButton2);

        //EXCLUSIVO ESTADO ERROR
        errorMessage = findViewById(R.id.error_msg);

        shape = findViewById(R.id.shape);
        current_state = findViewById(R.id.status);
        constraintlayout = findViewById(R.id.constraintLayout_2);
        offlineLayout = findViewById(R.id.waiting_net_2);

        task_msg.setVisibility(TextView.GONE);
        task_img.setVisibility(ImageView.GONE);
        current_task.setVisibility(TextView.GONE);
        errorMessage.setVisibility(TextView.GONE);


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
        state = "idle";
        switch (state) {
            case "idle":
                task_msg.setVisibility(TextView.GONE);
                task_img.setVisibility(ImageView.GONE);
                current_task.setVisibility(TextView.GONE);
                errorMessage.setVisibility(TextView.GONE);
                shape.setBackgroundResource(R.drawable.telemetry_page);
                shape.getBackground().setColorFilter(getResources().getColor(R.color.telemetry_idle_color), PorterDuff.Mode.SRC_ATOP);
                current_state.setBackgroundColor(Color.parseColor("#2F5597"));
                current_state.setText("IDLE"); // Set the text for the current state
                break;
            case "maneuvering":
                task_msg.setVisibility(TextView.VISIBLE);
                task_img.setVisibility(ImageView.VISIBLE);
                current_task.setVisibility(TextView.VISIBLE);
                errorMessage.setVisibility(TextView.GONE);
                shape.setBackgroundResource(R.drawable.telemetry_page);
                shape.getBackground().setColorFilter(getResources().getColor(R.color.telemetry_maneuvering_color), PorterDuff.Mode.SRC_ATOP);
                current_state.setBackgroundColor(Color.parseColor("#00642D"));
                current_state.setText("MANEUVERING");
                break;
            case "error":
                task_msg.setVisibility(TextView.GONE);
                task_img.setVisibility(ImageView.GONE);
                current_task.setVisibility(TextView.GONE);
                errorMessage.setVisibility(TextView.VISIBLE);
                shape.setBackgroundResource(R.drawable.telemetry_page);
                shape.getBackground().setColorFilter(getResources().getColor(R.color.telemetry_error_color), PorterDuff.Mode.SRC_ATOP);
                current_state.setBackgroundColor(Color.parseColor("#D8625E"));
                current_state.setText("ERROR");
                break;
        }

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBackMenu2();
            }
            private void openBackMenu2() {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }
        });

        /*FuncaoLeitura() { //TODO
            state = parametros[0];
            cpu_temp.setText(parametros[1]);
            bat_temp.setText(parametros[2]);
            longe.setText(parametros[3]);
            lat.setText(parametros[4]);
            bat_perc.setText(parametros[5]);
            dir.setText(parametros[6]);
            cur_task.setText(parametros[7]);
        }*/
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
            constraintlayout.setVisibility(ConstraintLayout.VISIBLE);
            offlineLayout.setVisibility(ConstraintLayout.GONE);

        } else {
            constraintlayout.setVisibility(ConstraintLayout.GONE);
            offlineLayout.setVisibility(ConstraintLayout.VISIBLE);
        }
        Log.d(TAG, "updateSignalImage: Internet availability: " + hasInternet);
    }

    /*private String FuncaoLeitura(String parametros[]){ //TODO
        //receber valores dos parametros
    }*/
}