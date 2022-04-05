package com.albertocasasortiz.ksas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.activity.parent.ActivityFullScreenSpeecher;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Fourth activity of the app. Allows to select which arm will execute the movements and the
 * feedback methods provided.
 */
public class ActivitySelectionFeedback extends ActivityFullScreenSpeecher {

    private RadioButton left;
    private RadioButton right;
    private SwitchMaterial visual;
    private SwitchMaterial haptic;
    private SwitchMaterial auditive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_feedback);

        // Instantiate TTS with an initial message.
        super.instantiateSpeech(getString(R.string.select_feedback));

        left = (RadioButton) findViewById(R.id.radioButtonLeft);
        right = (RadioButton) findViewById(R.id.radioButtonRight);
        visual = (SwitchMaterial) findViewById(R.id.switch1);
        haptic = (SwitchMaterial) findViewById(R.id.switch2);
        auditive = (SwitchMaterial) findViewById(R.id.switch3);
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

        // Send status of switches to next activity
        Intent myIntent = new Intent(this, ActivityLearningBlockingSetI.class);
        myIntent.putExtra("left", left.isChecked());
        myIntent.putExtra("right", right.isChecked());
        myIntent.putExtra("visual", visual.isChecked());
        myIntent.putExtra("haptic", haptic.isChecked());
        myIntent.putExtra("auditive", auditive.isChecked());

        // Intent to next activity.
        this.startActivity(myIntent);
    }
}