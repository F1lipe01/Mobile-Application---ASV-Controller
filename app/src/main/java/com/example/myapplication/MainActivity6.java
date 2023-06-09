package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;


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

    private static final String raspberryPiIpAddress = "192.168.92.32"; // Replace with Raspberry Pi's IP address
    private static final int raspberryPiPort = 49162; // Replace with the port of the Raspberry Pi
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

        Handler handler_message = new Handler();
        int delay_message = 3000; // delay for 3 seconds
        handler.postDelayed(new Runnable() {
            public void run() {
                new MainActivity6.SocketTask(raspberryPiIpAddress, raspberryPiPort, "SendData").execute();
                handler_message.postDelayed(this, delay_message);
            }
        }, delay);
    }

    class SocketTask extends AsyncTask<Void, Void, String> {
        private String ipAddress;
        private int port;
        private String message;
        private static final int SOCKET_TIMEOUT = 5000; // 5 seconds

        public SocketTask(String ipAddress, int port, String message) {
            this.ipAddress = ipAddress;
            this.port = port;
            this.message = message;
        }

        protected String doInBackground(Void... voids) {
            Socket socket = new Socket();

            PrintWriter writer = null;
            OutputStream outputStream = null;
            BufferedReader reader = null;

            try {
                socket.connect(new InetSocketAddress(ipAddress, port), SOCKET_TIMEOUT);
                socket.setSoTimeout(SOCKET_TIMEOUT);
                Log.d(TAG, "doInBackground: Connected to Raspberry Pi");


                outputStream = socket.getOutputStream();
                writer = new PrintWriter(outputStream, true);
                writer.println(message);

                Log.d(TAG, "doInBackground: Message Sent to Raspberry Pi " + message);

                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                char[] buffer = new char[256];
                int bytesRead = reader.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    String response = new String(buffer, 0, bytesRead);
                    Log.d(TAG, "Received response from Raspberry Pi: " + response);
                    return response;
                }
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: IOException while connecting to Raspberry Pi", e);
                return "Error";
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                if (writer != null) {
                    writer.close();
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute: Result from doInBackground: " + result);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (result != null && !result.equals("Error")) {
                        Log.d(TAG, "onPostExecute: Message Received " + result);

                        // Read from messages and update the variables
                        // "cpu_temp=25,bat_temp=25,lat=2,longe=1,bat_perc=70,dir=N,cur_task=3;"
                        String[] parameters = result.split(",");
                        for (String parameter : parameters) {
                            String[] nameValue = parameter.split("=");

                            switch (nameValue[0]) {
                                case "cpu_temp":
                                    cpu_temp.setText(nameValue[1].split("\\.")[0] + "º"); // Use split to remove the decimal cases
                                    break;
                                case "bat_temp":
                                    bat_temp.setText(nameValue[1].split("\\.")[0] + "º"); // Use split to remove the decimal cases
                                    break;
                                case "lat":
                                    if (nameValue[1].length() > 1) { // Ensure the string has more than one character
                                        lat.setText(nameValue[1].substring(0, nameValue[1].length() - 2) + "º"); // Remove the last character
                                    } else {
                                        lat.setText(nameValue[1]); // If the string has only one character, display it as it is
                                    }
                                    break;
                                case "long":
                                    if (nameValue[1].length() > 1) { // Ensure the string has more than one character
                                        longe.setText(nameValue[1].substring(0, nameValue[1].length() - 2) + "º"); // Remove the last character
                                    } else {
                                        longe.setText(nameValue[1]); // If the string has only one character, display it as it is
                                    }
                                    break;
                                case "bat_perc":
                                    bat_perc.setText(nameValue[1].split("\\.")[0]+ "%"); // Use split to remove the decimal cases
                                    break;
                                case "dir":
                                    dir.setText(nameValue[1].split("\\.")[0] +"º"); // Use split to remove the decimal cases
                                    break;
                                case "cur_task":
                                    cur_task.setText(nameValue[1]);
                                    break;
                                case "state":
                                    Log.d(TAG, "STATE: " + nameValue[1]);

                                    if(nameValue[1].contains("0") || nameValue[1].contains("1")) {
                                        task_msg.setVisibility(TextView.GONE);
                                        task_img.setVisibility(ImageView.GONE);
                                        current_task.setVisibility(TextView.GONE);
                                        errorMessage.setVisibility(TextView.GONE);
                                        shape.setBackgroundResource(R.drawable.telemetry_page);
                                        shape.getBackground().setColorFilter(getResources().getColor(R.color.telemetry_idle_color), PorterDuff.Mode.SRC_ATOP);
                                        current_state.setBackgroundColor(Color.parseColor("#2F5597"));
                                        current_state.setText("IDLE"); // Set the text for the current state
                                        break;
                                    }
                                    else if(nameValue[1].contains("3")) {
                                        task_msg.setVisibility(TextView.VISIBLE);
                                        task_img.setVisibility(ImageView.VISIBLE);
                                        current_task.setVisibility(TextView.VISIBLE);
                                        errorMessage.setVisibility(TextView.GONE);
                                        shape.setBackgroundResource(R.drawable.telemetry_page);
                                        shape.getBackground().setColorFilter(getResources().getColor(R.color.telemetry_maneuvering_color), PorterDuff.Mode.SRC_ATOP);
                                        current_state.setBackgroundColor(Color.parseColor("#00642D"));
                                        current_state.setText("MANEUVERING");
                                        break;
                                    }
                                    else if(nameValue[1].contains("2")){
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
                            }
                        }
                    } else {
                        String toastMessage;
                        if (result == null) {
                            toastMessage = "No message received. Couldn't Update";
                        } else if (result.equals("Error")) {
                            toastMessage = "An error occurred while connecting. Couldn't Update";
                        } else {
                            toastMessage = result;
                            Log.d(TAG, result);
                        }
                        Toast.makeText(MainActivity6.this, toastMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
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