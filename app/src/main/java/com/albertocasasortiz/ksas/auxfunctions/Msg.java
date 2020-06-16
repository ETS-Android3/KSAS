package com.albertocasasortiz.ksas.auxfunctions;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Class to ease the use of toast messages.
 */
public class Msg {

    /**
     * Show toast in screed.
     * @param context Context of the toast.
     * @param toast Object of toast to avoid multiple toast queues.
     * @param msg Message of the toast.
     * @param duration Duration of the toast.
     */
    public static void showToast(Context context, Toast toast, String msg, int duration){
        if(toast != null){
            // If toast is not null...
            if(toast.getView() != null && toast.getView().isShown())
                // And is showing in screen, cancel toast.
                toast.cancel();
        }
        // Create and show toast.
        toast = Toast.makeText(context, msg, duration);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}