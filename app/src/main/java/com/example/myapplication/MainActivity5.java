package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity5 extends AppCompatActivity {

    private static final String TAG = "MainActivity5";

    private String task1Message = "Task1";
    private String task2Message = "Task2";
    private String task3Message = "Task3";
    private String task4Message = "Task4";
    private Button task1, task2, task3, task4;

    private ImageButton back2;

    private ImageView connect;

    private TextView errorMessage, rasp_text, rasp_available, rasp_sucess;

    private GifImageView noSignalGifImageView, sendingMessage;

    private ConstraintLayout layout, layout2, sending;
    private boolean hasInternet;

    FrameLayout frameLayout;
    ImageView blurredImageView;

    private static final String raspberryPiIpAddress = "192.168.1.66"; // Replace with Raspberry Pi's IP address
    private static final int raspberryPiPort = 49162; // Replace with the port of the Raspberry Pi

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
        rasp_available = findViewById(R.id.message_sucess_2);
        rasp_sucess = findViewById(R.id.message_sucess_1);

        frameLayout = findViewById(R.id.frameLayout);
        blurredImageView = findViewById(R.id.defocus);

        noSignalGifImageView = findViewById(R.id.no_signal_gif2);
        sendingMessage = findViewById(R.id.no_signal_gif3);
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
                updateImage2(true);

                // Execute the SocketTask
                if (taskSuccess()) {
                    new SocketTask(raspberryPiIpAddress, raspberryPiPort, task1Message).execute();
                } else {
                    errorMessage.setVisibility(TextView.VISIBLE);
                }
            }

            private boolean taskSuccess() {
                //TODO - Task sent confirmation
                return true;
            }
        });

        task2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage2(true);

                if (taskSuccess()) {
                    new SocketTask(raspberryPiIpAddress, raspberryPiPort, task2Message).execute();
                } else {
                    errorMessage.setVisibility(TextView.VISIBLE);
                }
            }

            private boolean taskSuccess() {
                //TODO - Task sent confirmation
                return true;
            }
        });

        task3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage2(true);

                if (taskSuccess()) {
                    new SocketTask(raspberryPiIpAddress, raspberryPiPort, task3Message).execute();
                } else {
                    errorMessage.setVisibility(TextView.VISIBLE);
                }
            }

            private boolean taskSuccess() {
                //TODO - Task sent confirmation
                return true;
            }
        });

        task4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage2(true);

                if (taskSuccess()) {
                    new SocketTask(raspberryPiIpAddress, raspberryPiPort, task4Message).execute();
                } else {
                    errorMessage.setVisibility(TextView.VISIBLE);
                }
            }

            private boolean taskSuccess() {
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

        Log.d(TAG, "onCreate: MainActivity5 created");

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
                    String expectedResponse = message.equals("Ping") ? "Pong" : "ACK " + message;
                    if (expectedResponse.equals(result)) {
                        Log.d(TAG, "onPostExecute: Expected Message Received " + result);

                        Toast.makeText(MainActivity5.this, "Expected Message Received: " + result, Toast.LENGTH_LONG).show();
                        updateImage3();
                    } else {
                        String toastMessage;
                        updateImage2(false);
                        if (result == null) {
                            toastMessage = "No message received. \n Please, try again later.";
                        } else if (result.equals("Error")) {
                            toastMessage = "An error occurred while connecting. \n Please, try again later.";
                        } else {
                            toastMessage = "Unexpected message received: " + result;
                        }

                        Toast.makeText(MainActivity5.this, toastMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
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
        Log.d(TAG, "updateSignalImage: Internet availability: " + hasInternet);
    }

    private void updateImage2(boolean exp) {
        if (exp) {
            // PUT LAYOUT BLUR
            layout.setDrawingCacheEnabled(true);
            layout.buildDrawingCache();
            Bitmap blurredBitmap = BlurBuilder.blur(this, layout.getDrawingCache());
            blurredImageView.setImageBitmap(blurredBitmap);
            blurredImageView.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);

            layout2.setVisibility(ConstraintLayout.GONE);
            errorMessage.setVisibility(TextView.INVISIBLE);
            sending.setVisibility(ConstraintLayout.VISIBLE);
            sendingMessage.setVisibility(ConstraintLayout.VISIBLE);
            rasp_text.setVisibility(ConstraintLayout.VISIBLE);
            rasp_available.setVisibility(ConstraintLayout.GONE);
            rasp_sucess.setVisibility(ConstraintLayout.GONE);

            task1.setEnabled(false);
            task2.setEnabled(false);
            task3.setEnabled(false);
            task4.setEnabled(false);
            back2.setEnabled(false);
        } else {
            blurredImageView.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
            layout2.setVisibility(ConstraintLayout.GONE);
            sending.setVisibility(ConstraintLayout.GONE);
            connect.setVisibility(ConstraintLayout.GONE);

            task1.setEnabled(true);
            task2.setEnabled(true);
            task3.setEnabled(true);
            task4.setEnabled(true);
            back2.setEnabled(true);
        }
        Log.d(TAG, "updateImage2: UI state change: " + exp);

    }

    private void updateImage3() {
        sendingMessage.setVisibility(ConstraintLayout.GONE);
        connect.setVisibility(ConstraintLayout.VISIBLE);
        rasp_text.setVisibility(ConstraintLayout.GONE);
        rasp_available.setVisibility(ConstraintLayout.VISIBLE);
        rasp_sucess.setVisibility(ConstraintLayout.VISIBLE);

        Log.d(TAG, "updateImage3: Message sent successfully");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateImage2(false);
            }
        }, 2000); // 2 seconds delay
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
