package com.albertocasasortiz.ksas.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;

import com.albertocasasortiz.ksas.BuildConfig;
import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.activity.parent.ActivityFullScreenSpeecher;
import com.albertocasasortiz.ksas.auxfunctions.Multimedia;

/**
 * Third activity of the app, show a video with an introduction of the set..
 */
public class ActivityBlockingSet1 extends ActivityFullScreenSpeecher {

    /**
     * OnCreate method. Instantiates the TTS with an initial message and the video.
     * @param savedInstanceState A saved instance of the activity to load when the activity is destroyed and opened again.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocking_set1);

        // Instantiate TTS with an initial message.
        super.instantiateSpeech(getString(R.string.blocking_set_I_introduction));

        // Initialize video.
        Multimedia.initializeVideo(this, R.raw.fullset, R.id.videoSet);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
        }
    }

    /**
     * On click button start.
     * @param view View of the button.
     */
    public void onClickStart(View view) {
        // If already speaking, stop.
        if (super.tts != null) {
            super.tts.stop();
        }
        // Intent to next activity.
        Intent myIntent = new Intent(this, ActivityLearningBlockingSetI.class);
        this.startActivity(myIntent);
    }
}
