package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity8 extends AppCompatActivity {

    private Button task, atp_but, tm_but, sec_but;
    private ImageButton back2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);

        task = findViewById(R.id.task_set);
        atp_but = findViewById(R.id.atp_set);
        tm_but = findViewById(R.id.tm_set);
        back2 = findViewById(R.id.backButton2);

        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTaskMenu();
            }

            private void openTaskMenu() {
                Intent intent = new Intent(getApplicationContext(), MainActivity9.class);
                startActivity(intent);
            }
        });
        atp_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTaskMenu();
            }

            private void openTaskMenu() {
                Intent intent = new Intent(getApplicationContext(), MainActivity10.class);
                startActivity(intent);
            }
        });
        tm_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTaskMenu();
            }

            private void openTaskMenu() {
                Intent intent = new Intent(getApplicationContext(), MainActivity11.class);
                startActivity(intent);
            }
        });
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
    }
}