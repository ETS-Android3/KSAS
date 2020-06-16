package com.albertocasasortiz.ksas.auxfunctions;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Class for managing vibration.
 */
public class Vibration {

    /**
     * Vibrate an amount of ms.
     * @param activity Activity in which the vibration takes place.
     * @param ms Number of ms to vibrate.
     */
    public static void vibrate(AppCompatActivity activity, int ms){
        // Initialize vibrator.
        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        // If version older than O, execute old method.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (v != null) {
                v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } else {
            if (v != null) {
                v.vibrate(ms);
            }
        }
    }

}
