package com.albertocasasortiz.ksas.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.activity.parent.ActivityFullScreenSpeecher;
import com.albertocasasortiz.ksas.auxfunctions.Multimedia;
import com.albertocasasortiz.ksas.recognizer.KSAS;


/**
 * Fifth activity of the app, captures the data of the execution and gives feedback.
 */
public class ActivityLearningBlockingSetI extends ActivityFullScreenSpeecher {

    // KSAS object, containing all classes and element to deal with learning.
    KSAS ksas;

    // Video showing visual fedback.
    VideoView videoView;

    // Left hand selected.
    boolean left;
    // Right hand selected.
    boolean right;

    // Showing visual feedback.
    boolean visual;
    // Showing haptix feedback.
    boolean haptic;
    // Showing auditory feedback.
    boolean auditory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs1_assistant);

        //Instantiate the speecher and give initial indications.
        instantiateSpeech(getString(R.string.follow_instructions));

        // Get preferences from previous activity
        Bundle bundle = getIntent().getExtras();
        left = bundle.getBoolean("left");
        right = bundle.getBoolean("right");
        visual = bundle.getBoolean("visual");
        haptic = bundle.getBoolean("haptic");
        auditory = bundle.getBoolean("auditive");


        // Instantiate KSAS, so it can execute the mCMAR-R framework.
        ksas = new KSAS(this, super.tts, left, right, visual, haptic, auditory);

        // Initialize video.
        videoView = Multimedia.initializeVideo(videoView, this, R.raw.a_start, R.id.videoAssistant);
        if(visual) {
            videoView.setVisibility(View.VISIBLE);
        } else {
            videoView.setVisibility(View.GONE);
        }

    }

    /**
     * On click start, start lifecycle of KSAS.
     * @param view View of the gui.
     */
    public void onClickStart(View view) {
        if(visual)
            ksas.start(this.videoView);
        else
            ksas.start(null);
    }

    /**
     * On click stop, stop lifecycle of KSAS.
     * @param view View of the gui.
     */
    public void onClickStop(View view) {
        ksas.stop();
        finish();
    }
}
