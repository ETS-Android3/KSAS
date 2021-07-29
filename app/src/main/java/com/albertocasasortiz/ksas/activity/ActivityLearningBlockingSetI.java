package com.albertocasasortiz.ksas.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;

import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.activity.parent.ActivityFullScreenSpeecher;
import com.albertocasasortiz.ksas.auxfunctions.Multimedia;
import com.albertocasasortiz.ksas.recognizer.KSAS;


/**
 * Fourth activity of the app, captures the data of the execution and gives feedback.
 */
public class ActivityLearningBlockingSetI extends ActivityFullScreenSpeecher {

    /**
     * KSAS object, containing all classes and element to deal with learning.
     */
    KSAS ksas;

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs1_assistant);
        //Instantiate the speecher and give initial indications.
        instantiateSpeech(getString(R.string.follow_instructions));
        // Instantiae ksas, so it can execute the capture, model,
        // analysis and feedback in a background thread.
        ksas = new KSAS(this, super.tts);

        // Initialize video.
        videoView = Multimedia.initializeVideo(videoView, this, R.raw.a_start, R.id.videoAssistant);
    }

    /**
     * On click start, start lifecycle of KSAS.
     * @param view View of the gui.
     */
    public void onClickStart(View view) {
        ksas.start(this.videoView);
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
