package com.example.myapplication;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class MainActivity7 extends AppCompatActivity {
    private static final String TAG = "MainActivity7";
    private static final long FRAME_INTERVAL = 1000 / 50; // For 30 FPS

    private TextureView textureView;
    private Button backButton;
    private DatagramSocket videoSocket;
    private Thread videoThread;
    private Thread commandThread;
    private Handler uiHandler;

    private Bitmap currentFrame;

    private long lastFrameTime = 0;

    private void startVideoStream() {
        videoThread = new Thread(() -> {
            try {
                byte[] buffer = new byte[65000];
                videoSocket = new DatagramSocket(8888);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                while (!Thread.interrupted()) {
                    videoSocket.receive(packet);
                    byte[] data = packet.getData();
                    int length = packet.getLength();

                    Bitmap frame = decodeBitmapFromData(data, length);
                    if (frame != null) {
                        currentFrame = frame;
                        Log.d(TAG, "Received video packet: " + length + " bytes");

                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastFrameTime >= FRAME_INTERVAL) {
                            lastFrameTime = currentTime;

                            // Post to UI thread to render the frame
                            uiHandler.post(() -> {
                                if (!frame.isRecycled() && textureView.isAvailable()) {
                                    int width = textureView.getWidth();
                                    int height = textureView.getHeight();

                                    // Scale bitmap to fit the TextureView
                                    Bitmap scaledFrame = Bitmap.createScaledBitmap(frame, width, height, false);

                                    Canvas canvas = textureView.lockCanvas();
                                    if (canvas != null) {
                                        canvas.drawBitmap(scaledFrame, 0, 0, null);
                                        textureView.unlockCanvasAndPost(canvas);
                                    }

                                    // Recycle the scaled bitmap to free up memory
                                    scaledFrame.recycle();
                                }
                            });
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        videoThread.start();
    }

    private Bitmap decodeBitmapFromData(byte[] data, int length) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        return BitmapFactory.decodeByteArray(data, 0, length, options);
    }

    private void startCommandListener() {
        commandThread = new Thread(() -> {
            try {
                DatagramSocket commandSocket = new DatagramSocket(8889);
                byte[] buffer = new byte[16];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (!Thread.interrupted()) {
                    commandSocket.receive(packet);
                    String cmd = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    Log.d(TAG, "Received command: " + cmd);
                    // Handle command
                    // At the moment, we're not doing anything with the command
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        commandThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Getting WiFi IP address
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ip = android.text.format.Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.d(TAG, "WiFi IP: " + ip);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        textureView = findViewById(R.id.textureView);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            if (videoThread != null) videoThread.interrupt();
            if (commandThread != null) commandThread.interrupt();
            finish();
        });

        uiHandler = new Handler(Looper.getMainLooper());

        startVideoStream();

        startCommandListener();
    }
}
