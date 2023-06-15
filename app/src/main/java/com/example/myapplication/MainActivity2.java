package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity2 extends AppCompatActivity {

    private ImageButton camera, remote, telemtry, setting;

    private Button stop;

    private boolean hasInternet;

    private ConstraintLayout layout, layout2,sendingLayout;

    private GifImageView searching, no_signal_gif2;
    private static final String TAG = "MainActivity2";

    private static final String raspberryPiIpAddress = "192.168.57.32"; // Replace with Raspberry Pi's IP address
    private static final int raspberryPiPort = 49162; // Replace with the port of the Raspberry Pi

    FrameLayout frameLayout;
    ImageView blurredImageView, sucess_image, unssucess_image;
    private TextView rasp_text, rasp_available, rasp_sucess, rasp_unsucess;
    private String stopMessage = "Stop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        camera = findViewById(R.id.cameraButton);
        remote = findViewById(R.id.freeControl);
        setting = findViewById(R.id.settingsButton);
        telemtry = findViewById(R.id.telemetryButton);
        stop = findViewById(R.id.stopButton);

        layout = findViewById(R.id.constraintLayout_2);
        layout2 = findViewById(R.id.waiting_net_2);
        sendingLayout = findViewById(R.id.sending_rasp);

        rasp_text = findViewById(R.id.rasp_text);
        rasp_sucess = findViewById(R.id.message_sucess_1);
        rasp_unsucess = findViewById(R.id.error_message);

        frameLayout = findViewById(R.id.frameLayout);
        blurredImageView = findViewById(R.id.defocus);

        sucess_image = findViewById(R.id.connected);
        unssucess_image = findViewById(R.id.not_connected);

        searching = findViewById(R.id.searching);
        no_signal_gif2 = findViewById(R.id.no_signal_gif2);

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
            }

            private void openCameraMenu() {
                Intent intent = new Intent(getApplicationContext(), MainActivity7.class);
                startActivity(intent);
            }
        });

        remote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRemoteMenu();
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
            }

            private void openTelemetryMenu() {
                Intent intent = new Intent(getApplicationContext(), MainActivity6.class);
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
                Intent intent = new Intent(getApplicationContext(), MainActivity8.class);
                startActivity(intent);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendingImageLayout(true);
                new MainActivity2.SocketTask(raspberryPiIpAddress, raspberryPiPort, stopMessage).execute();
            }
        });

    }

    private void sendingImageLayout(boolean check){
        if(check == true){
            layout.setDrawingCacheEnabled(true);
            layout.buildDrawingCache();
            Bitmap blurredBitmap = MainActivity2.BlurBuilder.blur(this, layout.getDrawingCache());
            blurredImageView.setImageBitmap(blurredBitmap);
            blurredImageView.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
            layout2.setVisibility(ConstraintLayout.GONE);
            camera.setEnabled(false);
            telemtry.setEnabled(false);
            remote.setEnabled(false);
            setting.setEnabled(false);
            stop.setEnabled(false);
            sendingLayout.setVisibility(View.VISIBLE);
            sucess_image.setVisibility(View.GONE);
            unssucess_image.setVisibility(View.GONE);
            rasp_sucess.setVisibility(View.GONE);
            rasp_unsucess.setVisibility(View.GONE);
        }
        else{
            blurredImageView.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            layout2.setVisibility(ConstraintLayout.GONE);
            camera.setEnabled(true);
            telemtry.setEnabled(true);
            remote.setEnabled(true);
            setting.setEnabled(true);
            stop.setEnabled(true);
            sendingLayout.setVisibility(View.GONE);
        }

    }

    public void goodMessageLayout(){
        rasp_text.setVisibility(View.GONE);
        searching.setVisibility(View.GONE);
        rasp_sucess.setVisibility(View.VISIBLE);
        sucess_image.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendingImageLayout(false);
                rasp_text.setVisibility(View.VISIBLE);
                searching.setVisibility(View.VISIBLE);
            }
        }, 3000); // delay for 3 seconds
    }
    public void badMessageLayout(){
        rasp_text.setVisibility(View.GONE);
        searching.setVisibility(View.GONE);
        rasp_unsucess.setVisibility(View.VISIBLE);
        unssucess_image.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendingImageLayout(false);
                rasp_text.setVisibility(View.VISIBLE);
                searching.setVisibility(View.VISIBLE);

            }
        }, 3000); // delay for 3 seconds


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
                    String expectedResponse = "Stopped";
                    if (!expectedResponse.equals(result)) {
                        goodMessageLayout();
                    } else {
                        badMessageLayout();
                    }
                }
            });
        }
    }


    public class BlurBuilder {
        private static final float BITMAP_SCALE = 0.2f;
        private static final float BLUR_RADIUS = 25f;

        public static Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }


}