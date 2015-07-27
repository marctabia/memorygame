package com.example.marctabia.memorygame;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;


public class GameActivity extends Activity implements View.OnClickListener {

    Animation wobble;

    // for our hi score
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String dataName = "MyData";
    String intName = "MyString";
    int defaultInt = 0;
    int hiScore;

    // Prepare objects and sound references
    private SoundPool soundPool;
    int sample1 = -1;
    int sample2 = -1;
    int sample3 = -1;
    int sample4 = -1;

    // for the UI
    TextView textScore;
    TextView textDifficulty;
    TextView textWatchGo;

    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button buttonReplay;

    // some variables for our thread
    int difficultyLevel = 3;
    // An array to hold the randomly generated sequence
    int[] sequenceToCopy = new int[100];

    private Handler myHandler;
    // are we playing a sequence at the moment?
    boolean playSequence = false;
    // and which element of the sequence are we on?
    int elementToPlay = 0;

    // For checking the player's answer
    int playerResponses;
    int playerScore;
    boolean isResponding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        wobble = AnimationUtils.loadAnimation(this, R.anim.wobble);

        // initialize our 2 SharedPreferences objects
        prefs = getSharedPreferences(dataName, MODE_PRIVATE);
        editor = prefs.edit();
        hiScore = prefs.getInt(intName, defaultInt);

        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            // create objects of the 2 required classes
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor;

            // create our 4 fx in memory ready for use
            descriptor = assetManager.openFd("sample1.ogg");
            sample1 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample2.ogg");
            sample2 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample3.ogg");
            sample3 = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("sample4.ogg");
            sample4 = soundPool.load(descriptor, 0);

        } catch (IOException e) {

        }

        // reference all elements in our UI
        // the 3 views
        textScore = (TextView) findViewById(R.id.textScore);
        textScore.setText("Score: " + playerScore);
        textDifficulty = (TextView) findViewById(R.id.textDifficulty);
        textDifficulty.setText("Level: " + difficultyLevel);
        textWatchGo = (TextView) findViewById(R.id.textWatchGo);

        // Now the buttons
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        buttonReplay = (Button) findViewById(R.id.buttonReplay);

        // set all buttons to listen for clicks
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        buttonReplay.setOnClickListener(this);

        // this will be the code where we define our thread
        myHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (playSequence) {
                    // thread action goes here
                    // make sure all the buttons are visible
                    //button1.setVisibility(View.VISIBLE);
                    //button2.setVisibility(View.VISIBLE);
                    //button3.setVisibility(View.VISIBLE);
                    //button4.setVisibility(View.VISIBLE);

                    switch (sequenceToCopy[elementToPlay]) {
                        case 1:
                            // hide a button
                            //button1.setVisibility(View.INVISIBLE);
                            button1.startAnimation(wobble);
                            //play a sound
                            soundPool.play(sample1, 1, 1, 0, 0, 1);
                            break;
                        case 2:
                            // hide a button
                            //button2.setVisibility(View.INVISIBLE);
                            button2.startAnimation(wobble);
                            //play a sound
                            soundPool.play(sample2, 1, 1, 0, 0, 1);
                            break;
                        case 3:
                            // hide a button
                            //button3.setVisibility(View.INVISIBLE);
                            button3.startAnimation(wobble);
                            //play a sound
                            soundPool.play(sample3, 1, 1, 0, 0, 1);
                            break;
                        case 4:
                            // hide a button
                            //button4.setVisibility(View.INVISIBLE);
                            button4.startAnimation(wobble);
                            //play a sound
                            soundPool.play(sample4, 1, 1, 0, 0, 1);
                            break;
                    }

                    elementToPlay++;
                    if (elementToPlay == difficultyLevel) {
                        sequenceFinished();
                    }

                }
                myHandler.sendEmptyMessageDelayed(0, 1000);
            }
        };

        myHandler.sendEmptyMessage(0);
        playASequence();
    }

    @Override
    public void onClick(View view) {
        if (!playSequence) {
            switch (view.getId()) {
                case R.id.button1:
                    // play a sound
                    soundPool.play(sample1, 1, 1, 0, 0, 1);
                    checkElement(1);
                    break;
                case R.id.button2:
                    // play a sound
                    soundPool.play(sample2, 1, 1, 0, 0, 1);
                    checkElement(2);
                    break;
                case R.id.button3:
                    // play a sound
                    soundPool.play(sample3, 1, 1, 0, 0, 1);
                    checkElement(3);
                    break;
                case R.id.button4:
                    // play a sound
                    soundPool.play(sample4, 1, 1, 0, 0, 1);
                    checkElement(4);
                    break;
                case R.id.buttonReplay:
                    difficultyLevel = 3;
                    playerScore = 0;
                    textScore.setText("Score: " + playerScore);
                    playASequence();
                    break;
            }
        }
    }

    public void createSequence() {
        // for choosing a random button
        Random randInt = new Random();
        int ourRandom;
        for (int i = 0; i < difficultyLevel; i++) {
            // get a random number between 1 and 4
            ourRandom = randInt.nextInt(4);
            ourRandom++; // make sure it is not zero
            // save that number in an array
            sequenceToCopy[i] = ourRandom;
        }
    }

    public void playASequence() {
        createSequence();
        isResponding = false;
        elementToPlay = 0;
        playerResponses = 0;
        textWatchGo.setText("WATCH!");
        playSequence = true;
    }

    public void sequenceFinished() {
        playSequence = false;
        // make sure all buttons are visible
        //button1.setVisibility(View.VISIBLE);
        //button2.setVisibility(View.VISIBLE);
        //button3.setVisibility(View.VISIBLE);
        //button4.setVisibility(View.VISIBLE);
        textWatchGo.setText("GO!");
        isResponding = true;
    }

    public void checkElement(int thisElement) {
        if (isResponding) {
            playerResponses++;
            if (sequenceToCopy[playerResponses-1] == thisElement) {
                playerScore = playerScore + ((thisElement + 1) * 2);
                textScore.setText("Score: " + playerScore);
                if (playerResponses == difficultyLevel) {
                    isResponding = false;
                    difficultyLevel++;
                    playASequence();
                }
            } else {
                textWatchGo.setText("FAILED!");
                isResponding = false;

                if (playerScore > hiScore) {
                    hiScore = playerScore;
                    editor.putInt(intName, hiScore);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "New Hi-score",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
