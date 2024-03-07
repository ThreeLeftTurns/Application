package com.example.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonEasy = findViewById(R.id.button1);
        Button buttonMedium = findViewById(R.id.button2);
        Button buttonHard = findViewById(R.id.button3);

        buttonEasy.setOnClickListener(v -> {
            //code goes in here
            startGame("Easy");
        });

        buttonMedium.setOnClickListener(v -> {
            //medium code goes here
            startGame("Medium");
        });

        buttonHard.setOnClickListener(v -> {
            //hard code goes here
            startGame("Hard");
        });

    }
private void startGame (String difficulty)
    {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra("DIFFICULTY", difficulty);
        startActivity(intent);
    }
}