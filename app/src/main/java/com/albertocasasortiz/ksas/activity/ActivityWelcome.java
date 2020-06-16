package com.albertocasasortiz.ksas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.activity.parent.ActivityFullScreenSpeecher;

/**
 * First activity of the app. Welcome the user and ask him to use the device as wearable.
 */
public class ActivityWelcome extends ActivityFullScreenSpeecher {

    /**
     * OnCreate method. Instantiates the TTS with an initial message.
     * @param savedInstanceState A saved instance of the activity to load when the activity is destroyed and opened again.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate TTS with an initial message.
        this.instantiateSpeech(getString(R.string.welcome) + ". "
                + getString(R.string.use_as_wearable) + " "
                + getString(R.string.attach_phone));
    }

    /**
     * On click button continue.
     * @param view View of the button.
     */
    public void onClickContinue(View view) {
        // If already speaking, stop.
        if (super.tts != null) {
            tts.stop();
        }
        // Intent to next activity.
        Intent myIntent = new Intent(this, ActivitySelectionSets.class);
        this.startActivity(myIntent);
}

}
