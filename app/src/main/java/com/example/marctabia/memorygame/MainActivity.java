package com.example.marctabia.memorygame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity implements View.OnClickListener {

    // for our high score
    SharedPreferences prefs;
    String dataName = "MyData";
    String intName = "MyString";
    int defaultInt = 0;

    public static int hiScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize our 2 SharedPreferences objects
        prefs = getSharedPreferences(dataName, MODE_PRIVATE);

        // load the hiScore or if not available, the default value
        hiScore = prefs.getInt(intName, defaultInt);
        // make a reference to the Hiscore textview in our layout
        TextView textHiScore = (TextView)findViewById(R.id.textHiScore);
        // display the hi score
        textHiScore.setText("Hi: " + hiScore);

        // make a button from the button in our layout
        Button button = (Button)findViewById(R.id.button1);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        i = new Intent(this, GameActivity.class);
        startActivity(i);
    }
}
