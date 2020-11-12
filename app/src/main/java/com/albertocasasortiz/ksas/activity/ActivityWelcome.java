package com.albertocasasortiz.ksas.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.activity.parent.ActivityFullScreenSpeecher;

/**
 * First activity of the app. Welcome the user and ask him to use the device as wearable.
 */
public class ActivityWelcome extends ActivityFullScreenSpeecher {
    // Manager for sensor reading.
    private SensorManager sensorManager;
    // Show a warning tothe user here.
    TextView warningText;

    /**
     * OnCreate method. Instantiates the TTS with an initial message.
     * @param savedInstanceState A saved instance of the activity to load when the activity is destroyed and opened again.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate TTS with an initial message.
        this.instantiateSpeech(getString(R.string.welcome) + ". "
                + getString(R.string.use_as_wearable) + " "
                + getString(R.string.attach_phone));

        // Initialize sensor manager.
        this.sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        warningText = (TextView)findViewById(R.id.warningText);
        String warningMessage = "";
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null){
            warningMessage += getString(R.string.acceleration_not_available) + "\n";
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) == null){
            warningMessage += getString(R.string.gravity_not_available) + "\n";
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) == null){
            warningMessage += getString(R.string.gyroscope_not_available) + "\n";
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) == null){
            warningMessage += getString(R.string.linear_acceleration_not_available) + "\n";
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) == null){
            warningMessage += getString(R.string.game_rotation_vector_not_available) + "\n";
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null){
            warningMessage += getString(R.string.magnetic_field_not_available) + "\n";
        }

        if(!warningMessage.isEmpty()) {
            warningMessage += getString(R.string.application_may_not_recognize);
            warningText.setText(warningMessage);
        } else {
            warningMessage = getString(R.string.all_sensors_ok);
            warningText.setTextColor(Color.parseColor("#009900"));
            warningText.setText(warningMessage);
        }

    }

    /**
     * On click button continue.
     * @param view View of the button.
     */
    public void onClickContinue(View view) {
        // If already speaking, stop.
        if (super.tts != null) {
            tts.stop();
        }
        // Intent to next activity.
        Intent myIntent = new Intent(this, ActivitySelectionSets.class);
        this.startActivity(myIntent);
}

}
