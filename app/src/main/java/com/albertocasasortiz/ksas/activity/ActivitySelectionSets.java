package com.albertocasasortiz.ksas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.activity.parent.ActivityFullScreenSpeecher;
import com.albertocasasortiz.ksas.auxfunctions.Multimedia;

/**
 * Second activity of the app, ask the user to select a set to practice.
 */
public class ActivitySelectionSets extends ActivityFullScreenSpeecher {

    /**
     * OnCreate method. Instantiates the TTS with an initial message and the video.
     * @param savedInstanceState A saved instance of the activity to load when the activity is destroyed and opened again.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_selection);

        // Instantiate TTS with an initial message.
        this.instantiateSpeech(getString(R.string.set_selection_question) + ". ");

        // Initialize video.
        Multimedia.initializeVideo(this, R.raw.blocks, R.id.videoSelection);
    }

    /**
     * On click button blocking set I.
     * @param view View of the button.
     */
    public void onClickBlockingSetI(View view) {
        // If already speaking, stop.
        if (super.tts != null) {
            super.tts.stop();
        }
        // Intent to next activity.
        Intent myIntent = new Intent(this, ActivityBlockingSet1.class);
        this.startActivity(myIntent);
    }

}
