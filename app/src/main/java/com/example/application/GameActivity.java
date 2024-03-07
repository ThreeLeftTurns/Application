package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.GridLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class GameActivity extends AppCompatActivity {

    private Button firstSelectedButton = null;
    private int firstImageRes = 0;
    private boolean isChecking = false;
    int totalPairs;

    private TextView clickCounterTextView;
    private TextView timerTextView;
    private int clickCounter = 0;
    private long startTime;
    private final Handler timerHandler = new Handler();
    private boolean timerStarted = false;
    private int matchedPairs = 0;
    private Button resetButton;
    private final Runnable timerRunnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            timerTextView.setText(String.format("%02d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String difficulty = null;
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true); // If you have a custom view in your Toolbar

            // Difficulty setting
            difficulty = getIntent().getStringExtra("DIFFICULTY");
            getSupportActionBar().setTitle(difficulty);
        }

        clickCounterTextView = findViewById(R.id.ClickCounter);
        timerTextView = findViewById(R.id.textView3);

        assert difficulty != null; // Consider handling this more gracefully
        setupGameGrid(difficulty);
    }


    private void setupGameGrid(String difficulty) {
        GridLayout gridLayout = findViewById(R.id.gameGrid);
        int gridSize;

        // Set grid size based on difficulty
        switch (difficulty) {
            case "Easy":
                gridSize = 3;
                break;
            case "Medium":
                gridSize = 4;
                break;
            case "Hard":
                gridSize = 5;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + difficulty);
        }

        // Calculate the total number of pairs needed for the grid
        int totalPairsNeeded = (gridSize * gridSize) / 2;

        // Initialize your full list of image resources
        Integer[] imageResourcesArray = {
                R.drawable.file1, R.drawable.file2, R.drawable.file3,
                R.drawable.file4, R.drawable.file5, R.drawable.file6,
                R.drawable.file7, R.drawable.file8, R.drawable.file9,
                R.drawable.file10, R.drawable.file11, R.drawable.file12
        };

        // Prepare a list to hold the correct number of pairs
        List<Integer> selectedImages = new ArrayList<>();
        for (int i = 0; i < totalPairsNeeded; i++) {
            selectedImages.add(imageResourcesArray[i]);
            selectedImages.add(imageResourcesArray[i]); // Add each image twice to create pairs
        }

        Collections.shuffle(selectedImages);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        // Subtract the padding from the screen width to get the usable width
        int totalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics());
        screenWidth -= totalPadding;


        // Calculate the available space for each button, accounting for button margins
        int totalMargin = gridSize - 1;
        int marginSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int usableWidth = screenWidth - (totalMargin * marginSize);
        int buttonSize = usableWidth / gridSize;

        gridLayout.removeAllViews();
        gridLayout.setColumnCount(gridSize);
        gridLayout.setRowCount(gridSize);


        // Create buttons and add them to the grid
        for (int i = 0; i < gridSize * gridSize; i++) {
            Button button = new Button(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = buttonSize;
            params.height = buttonSize;
            params.setMargins(marginSize, marginSize, marginSize, marginSize);
            button.setBackgroundResource(R.drawable.flipped);

            button.setLayoutParams(params);
            gridLayout.addView(button);

            totalPairs = (gridSize * gridSize) / 2;


            if (i < selectedImages.size()) {
                int imageRes = selectedImages.get(i);
                button.setTag(imageRes);
                button.setOnClickListener(v -> {
                    if(!timerStarted)
                    {
                        startTimer();
                    }
                    incrementClickCounter();
                    if (isChecking) return; // Prevent interaction during check
                    Button b = (Button) v;
                    int res = (int) b.getTag();
                    b.setBackgroundResource(res);

                    if (firstSelectedButton == null) {
                        // First button selected
                        firstSelectedButton = b;
                        firstImageRes = res;
                    } else if (firstSelectedButton != b) {
                        // Second button selected, check for match
                        isChecking = true;
                        new Handler().postDelayed(() -> {
                            if (firstImageRes == res) {
                                // Match found
                                firstSelectedButton.setEnabled(false);
                                b.setEnabled(false);
                                matchedPairs++;

                                if(matchedPairs == totalPairs)
                                {
                                    onPlayerWon();
                                }

                            } else {
                                // No match
                                firstSelectedButton.setBackgroundResource(R.drawable.flipped);
                                b.setBackgroundResource(R.drawable.flipped);
                            }
                            resetSelection();
                        }, 300);
                    }
                });
            }

        }
    }

    private void incrementClickCounter() {
        clickCounter++;
        clickCounterTextView.setText(String.valueOf(clickCounter));
    }

    private void resetSelection() {
        firstSelectedButton = null;
        firstImageRes = 0;
        isChecking = false;
    }

    private void startTimer() {
        if (!timerStarted) {
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            timerStarted = true;
        }
    }

    private void onPlayerWon() {
        timerHandler.removeCallbacks(timerRunnable); // Stop the timer

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime; // Calculate total game time in milliseconds

        // Format totalTime to a readable format, e.g., "MM:SS"
        long totalSeconds = totalTime / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        String formattedTime = String.format("%02d:%02d", minutes, seconds);

        // Show a 'You win!' message
        runOnUiThread(() -> {
            //Toast.makeText(GameActivity.this, "You win!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(GameActivity.this, WinScreen.class);
            intent.putExtra("SCORE", clickCounter);
            intent.putExtra("TIME", formattedTime);
            startActivity(intent);
        });
    }


    // This method will reset the game to its initial state
    @SuppressLint("SetTextI18n")
    private void resetGame() {
        // Reset the game state variables
        matchedPairs = 0;
        clickCounter = 0;
        firstSelectedButton = null;
        firstImageRes = 0;
        isChecking = false;
        timerStarted = false;

        // Update the UI elements for the timer and click counter
        timerTextView.setText("00:00");
        clickCounterTextView.setText(String.valueOf(clickCounter));

        // Clear any existing handler callbacks
        timerHandler.removeCallbacks(timerRunnable);

        // Re-setup the game grid
        String difficulty = getIntent().getStringExtra("DIFFICULTY"); // Or store the difficulty level in a member variable
        if(difficulty == null)
        {
            finish();
            return;
        }

        setupGameGrid(difficulty);
    }


}