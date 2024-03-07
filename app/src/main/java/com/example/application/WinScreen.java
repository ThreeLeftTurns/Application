package com.example.application;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WinScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win_screen);

        // Retrieve and display the score and time
        int score = getIntent().getIntExtra("SCORE", 0);
        String time = getIntent().getStringExtra("TIME");

        TextView scoreTextView = findViewById(R.id.totalscore);
        TextView timeTextView = findViewById(R.id.grabtime);
        scoreTextView.setText(String.valueOf(score));
        timeTextView.setText(time);

        // Find the button by its ID and set an OnClickListener
        Button playAgainButton = findViewById(R.id.restartButton);
        playAgainButton.setVisibility(View.INVISIBLE);

        // Handler to show the button after 1 second
        new Handler().postDelayed(() -> {
            playAgainButton.setVisibility(View.VISIBLE); // Make the button visible after 1 second
        }, 2000);


        playAgainButton.setOnClickListener(v -> {

            // Intent to start MainActivity
            Intent intent = new Intent(WinScreen.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
