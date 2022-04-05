package com.albertocasasortiz.ksas.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.activity.parent.ActivityFullScreenSpeecher;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * First activity of the app. Welcome the user and ask her/him to use the device as wearable.
 */
public class ActivityWelcome extends ActivityFullScreenSpeecher {

    // List of required sensors.
    private static final ArrayList<Integer> requiredSensors = new ArrayList<>(Arrays.asList(Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GRAVITY, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_LINEAR_ACCELERATION,
            Sensor.TYPE_GAME_ROTATION_VECTOR, Sensor.TYPE_MAGNETIC_FIELD));

    // Show a warning to the user here.
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
        // Manager for sensor reading.
        SensorManager sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        warningText = (TextView)findViewById(R.id.warningText);
        String warningMessage = "";

        // Check if sensors are available.
        for(int sensor: requiredSensors) {
            if(sensorManager.getDefaultSensor(sensor) == null) {
                warningMessage = getSensorWarning(sensor);
            }
        }
        // If any sensor is not available, tell the user that it may entail some problems.
        if(!warningMessage.isEmpty()) {
            warningMessage += getString(R.string.application_may_not_recognize);
        } else {
            // If all sensors are available and working, tell the user that everything is working.
            warningMessage = getString(R.string.all_sensors_ok);
            warningText.setTextColor(Color.parseColor("#009900"));
        }
        warningText.setText(warningMessage);

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

    public String getSensorWarning(int sensor) {
        String warningMessage = "";
        switch (sensor) {
            case Sensor.TYPE_ACCELEROMETER:
                warningMessage += getString(R.string.acceleration_not_available) + "\n";
                break;
            case Sensor.TYPE_GRAVITY:
                warningMessage += getString(R.string.gravity_not_available) + "\n";
                break;
            case Sensor.TYPE_GYROSCOPE:
                warningMessage += getString(R.string.gyroscope_not_available) + "\n";
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                warningMessage += getString(R.string.linear_acceleration_not_available) + "\n";
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                warningMessage += getString(R.string.game_rotation_vector_not_available) + "\n";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                warningMessage += getString(R.string.magnetic_field_not_available) + "\n";
                break;
            default:
                warningMessage += getString(R.string.default_sensor_not_available) + sensor + "\n";
                break;
        }
        return warningMessage;
    }

}
