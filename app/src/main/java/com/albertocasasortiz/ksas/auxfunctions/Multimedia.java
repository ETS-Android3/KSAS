package com.albertocasasortiz.ksas.auxfunctions;

import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Class for managing multimedia files (sound, video...)
 */
public class Multimedia {

    /**
     * Play a sound from files.
     * @param activity Activity where the sound is played.
     * @param soundFile Sound file from R.raw.
     */
    public static void playSound(AppCompatActivity activity, int soundFile){
        //Initialize media player.
        MediaPlayer mp = MediaPlayer.create(activity.getApplicationContext(), soundFile);
        //Start reproduction.
        mp.start();
        //TODO Implement this properly
        //Wait till sound ends.
        while(mp.isPlaying()){}

    }

    /**
     * Initialize the video and the media controller.
     * @param activity Activity where the video is played.
     * @param videoName Reference to the video in R.raw.
     * @param viewId Reference to the view id in R.id.
     */
    public static void initializeVideo(AppCompatActivity activity, int videoName, int viewId){
        // Get videoView from gui.
        VideoView videoView = activity.findViewById(viewId);
        // Prepare the video, set looping true, and automatic start.
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
        // Attach video URI to videoView.
        String videoPath = "android.resource://" + activity.getPackageName() + "/" + videoName;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        // Create mediaController and set size.
        MediaController mediaController = new MediaController(activity);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
    }

}
