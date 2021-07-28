package com.albertocasasortiz.ksas.activity;

import android.os.Bundle;
import android.view.View;

import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.activity.parent.ActivityFullScreenSpeecher;
import com.albertocasasortiz.ksas.recognizer.KSAS;


/**
 * Fourth activity of the app, captures the data of the execution and gives feedback.
 */
public class ActivityLearningBlockingSetI extends ActivityFullScreenSpeecher {

    /**
     * KSAS object, containing all classes and element to deal with learning.
     */
    KSAS ksas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bs1_assistant);
        //Instantiate the speecher and give initial indications.
        instantiateSpeech(getString(R.string.follow_instructions));
        // Instantiae ksas, so it can execute the capture, model,
        // analysis and feedback in a background thread.
        ksas = new KSAS(this, super.tts);
    }

    /**
     * On click start, start lifecycle of KSAS.
     * @param view View of the gui.
     */
    public void onClickStart(View view) {
        ksas.start();
    }

    /**
     * On click stop, stop lifecycle of KSAS.
     * @param view
     */
    public void onClickStop(View view) {
        ksas.stop();
        finish();
    }
}
