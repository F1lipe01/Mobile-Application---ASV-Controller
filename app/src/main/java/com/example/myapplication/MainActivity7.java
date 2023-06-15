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
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
public class MainActivity7 extends AppCompatActivity {
    private static final String TAG = "MainActivity7";
    private static final long FRAME_INTERVAL = 1000 / 50; // For 30 FPS

    private static final long PACKET_RECEIVED_TIMEOUT = 1000; // 1 second

    private TextureView textureView;

    private static final long NO_FRAME_RECEIVED_TIMEOUT = 2000; // 5 seconds

    // Used to schedule a task that will be executed after a certain delay
    private final Handler frameReceivedHandler = new Handler();
    private Runnable frameReceivedRunnable;

    private Button backButton;
    private DatagramSocket videoSocket;
    private DatagramSocket commandSocket;
    private Thread videoThread;
    private Thread commandThread;
    private Thread networkQualityThread;
    private Handler uiHandler;
    private InetAddress serverAddress;

    private ConstraintLayout constraintLayout;
    private static final int COMMAND_PORT = 8889;
    private pl.droidsonroids.gif.GifImageView gifImageView;

    private Bitmap currentFrame;

    private long lastFrameTime = 0;
    private boolean isNetworkPoor = false;  // Flag for network quality

    private final Handler packetReceivedHandler = new Handler();
    private Runnable packetReceivedRunnable;

    private void startVideoStream() {
        videoThread = new Thread(() -> {
            try {
                byte[] buffer = new byte[65000];
                videoSocket = new DatagramSocket(8888);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Handler to handle the display of the GIF after half a second
                Handler gifDisplayHandler = new Handler(Looper.getMainLooper());
                Runnable gifDisplayRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // Make the GIF visible
                        uiHandler.post(() -> gifImageView.setVisibility(View.VISIBLE));
                    }
                };

                // Post the Runnable with half a second delay
                gifDisplayHandler.postDelayed(gifDisplayRunnable, 500);

                while (!Thread.interrupted()) {
                    videoSocket.receive(packet);

                    // Remove the packet received Runnable, if any, and prepare for a new one
                    if (packetReceivedRunnable != null) {
                        packetReceivedHandler.removeCallbacks(packetReceivedRunnable);
                    }

                    packetReceivedRunnable = new Runnable() {
                        @Override
                        public void run() {
                            // If no packet received for PACKET_RECEIVED_TIMEOUT, show the gifImageView and hide the textureView
                            uiHandler.post(() -> {
                                gifImageView.setVisibility(View.VISIBLE);
                                textureView.setVisibility(View.INVISIBLE);
                            });
                        }
                    };

                    packetReceivedHandler.postDelayed(packetReceivedRunnable, PACKET_RECEIVED_TIMEOUT);

                    byte[] data = packet.getData();
                    int length = packet.getLength();

                    // Packet received, hide the GIF
                    // Packet received, hide the GIF and show the textureView
                    uiHandler.post(() -> {
                        gifImageView.setVisibility(View.GONE);
                        textureView.setVisibility(View.VISIBLE);
                    });
                    Bitmap frame = decodeBitmapFromData(data, length);
                    if (frame != null) {
                        currentFrame = frame;

                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastFrameTime >= FRAME_INTERVAL) {
                            lastFrameTime = currentTime;

                            // Post to UI thread to render the frame
                            uiHandler.post(() -> {
                                if (!frame.isRecycled() && textureView.isAvailable()) {
                                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textureView.getLayoutParams();
                                    params.width = frame.getWidth();
                                    params.height = frame.getHeight();
                                    textureView.setLayoutParams(params);

                                    Canvas canvas = textureView.lockCanvas();
                                    if (canvas != null) {
                                        canvas.drawBitmap(frame, 0, 0, null);
                                        textureView.unlockCanvasAndPost(canvas);
                                    }
                                }

                                // Frame received, hide the constraint layout
                                constraintLayout.setVisibility(View.GONE);

                                // Cancel the previous Runnable, if any
                                if (frameReceivedRunnable != null) {
                                    frameReceivedHandler.removeCallbacks(frameReceivedRunnable);
                                }

                                // Schedule a new Runnable
                                frameReceivedRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        // 5 seconds passed without receiving a new frame, show the constraint layout
                                        constraintLayout.setVisibility(View.VISIBLE);
                                        constraintLayout.animate().alpha(1).setDuration(300);
                                    }
                                };
                                frameReceivedHandler.postDelayed(frameReceivedRunnable, NO_FRAME_RECEIVED_TIMEOUT);
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

    private void sendCommand(String command) {
        new Thread(() -> {
            try {
                byte[] buffer = command.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, COMMAND_PORT);
                commandSocket.send(packet);
                Log.d(TAG, "Sent command: " + command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startCommandListener() {
        commandThread = new Thread(() -> {
            try {
                commandSocket = new DatagramSocket(8889);
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

    private void checkNetworkQuality() {
        networkQualityThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                if (isNetworkPoor) {
                    sendCommand("gray");
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        networkQualityThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Getting WiFi IP address
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ip = android.text.format.Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.d(TAG, "WiFi IP: " + ip);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        textureView = findViewById(R.id.textureView);
        constraintLayout = findViewById(R.id.layout);
        constraintLayout.setVisibility(View.VISIBLE);  // Set to visible here
        textureView.setVisibility(View.INVISIBLE); // Set textureView to invisible initially

        backButton = findViewById(R.id.backButton);
        gifImageView = findViewById(R.id.search);
        gifImageView.setVisibility(View.VISIBLE);  // To show the GIF initially

        backButton.setOnClickListener(v -> {
            if (videoThread != null) videoThread.interrupt();
            if (commandThread != null) commandThread.interrupt();
            if (networkQualityThread != null) networkQualityThread.interrupt();
            if (videoSocket != null) videoSocket.close();
            if (commandSocket != null) commandSocket.close();
            finish();
        });

        uiHandler = new Handler(Looper.getMainLooper());

        try {
            serverAddress = InetAddress.getByName("192.168.1.66");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        checkNetworkQuality();

        startVideoStream();

        startCommandListener();
    }
}
