package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity9 extends AppCompatActivity {

    private ImageButton back2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main9);

        back2 = findViewById(R.id.backButton2);
        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBackMenu2();
            }
            private void openBackMenu2() {
                Intent intent = new Intent(getApplicationContext(), MainActivity8.class);
                startActivity(intent);
            }
        });
    }
}