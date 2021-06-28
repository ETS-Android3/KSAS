package com.albertocasasortiz.ksas.auxfunctions;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Class with auxiliary functions with different purposes.
 */
public class ActivityFunctions {

    /**
     * Set the activity to full screen mode.
     * @param activity Activity that will be in full screen mode.
     */
    public static void setFullScreen(AppCompatActivity activity){
        // Set full screen.
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Force portrait.
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide action bar.
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    /**
     * Transform text to speech.
     * @param text Text to be spoken.
     * @param tts TextToSpeech object that will process the text and speak it.
     * @param blockTillEnd Establish to true if you want the thread to be blocked till the speech
     *                     ends.
     */
    public static void speak(String text, TextToSpeech tts, boolean blockTillEnd){
        // If already speaking, stop.
        if(tts.isSpeaking())
            tts.stop();
        // Speak, use older method if the SDK version is older than LOLLIPOP.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
        //TODO Implement this properly
        // If block till end, block thread.
        if(blockTillEnd) {
            // Wait 200 ms for the tts to start speaking.
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // If the tts is speaking, wait.
            while (tts.isSpeaking()) {}
        }
    }

}
