package com.albertocasasortiz.ksas.activity.parent;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.auxfunctions.ActivityFunctions;
import com.albertocasasortiz.ksas.auxfunctions.Msg;

import java.util.Locale;

/**
 * Full screen activity with a TextToSpeech that speaks an initial message.
 */
@SuppressLint("Registered")
public class ActivityFullScreenSpeecher extends AppCompatActivity {

    /** Allows to convert from text to voice.*/
    protected TextToSpeech tts;

    // Toast for showing messages to the user.
    private Toast toast;

    /**
     * OnCreate method that calls an static methods that set the activity to full screeen model.
     * @param savedInstanceState A saved instance of the activity to load when the activity is destroyed and opened again.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set full screen mode.
        ActivityFunctions.setFullScreen(this);

        this.toast = new Toast(this);
    }

    /**
     * Method for instantiate the TextToSpeech object.
     * @param initMessage Message to speak when the TTS object has been instantiated.
     */
    public void instantiateSpeech(final String initMessage){
        //Instantiate the speecher.
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.getDefault());
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Msg.showToast(getApplicationContext(), toast, getString(R.string.language_not_suported), Toast.LENGTH_LONG);
                }
                ActivityFunctions.speak(initMessage, tts, false);

            } else {
                Msg.showToast(getApplicationContext(), toast,getString(R.string.language_initialization_failed), Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * On destroy activity, execute this method.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop and shutdown the tts object.
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}
