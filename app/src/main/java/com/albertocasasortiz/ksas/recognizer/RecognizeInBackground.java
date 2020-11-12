package com.albertocasasortiz.ksas.recognizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.auxfunctions.Multimedia;
import com.albertocasasortiz.ksas.auxfunctions.SensorsInfo;
import com.albertocasasortiz.ksas.auxfunctions.ActivityFunctions;
import com.albertocasasortiz.ksas.auxfunctions.Mathematics;
import com.albertocasasortiz.ksas.auxfunctions.Msg;
import com.albertocasasortiz.ksas.auxfunctions.Vibration;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Activity that recognizes and gives feedback in background thread.
 */
public class RecognizeInBackground extends AsyncTask<Void, Void, Boolean> implements SensorEventListener {
    // Manager for sensor reading.
    private SensorManager sensorManager;
    // Class to store sensor info.
    private SensorsInfo sensorsInfo;
    // Delay of sensor readings.
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;
    // Boolean that indicates if the sensors are reading data.
    private boolean recordingData;

    // Indicates which is the current movement.
    private Movements current_movement;
    // Indicates which is the recognized movement.
    private Movements recognized_movement;
    // Stores number of errors.
    private int errors;

    // Interpreter to load the model.
    private Interpreter tflite;

    //TODO fix posible memory leak.
    // Activity in which the task is being executed.
    @SuppressLint("StaticFieldLeak")
    private AppCompatActivity activity;

    /** Allows to convert from text to voice.*/
    private TextToSpeech tts;

    // Toast for showing messages to the user.
    private Toast toast;

    /**
     * Constructor function.
     * @param activity Activity in which the task is being executed.
     * @param tts Allows to convert from text to voice.
     */
    public RecognizeInBackground(AppCompatActivity activity, TextToSpeech tts){
        // Instantiate activity.
        this.activity = activity;
        // Instantiate tts.
        this.tts = tts;

        // Initialize toast.
        this.toast = new Toast(this.activity);

        // Load tensorflow model.
        try{
            //TODO This method using MappedByteBuffer is deprecated, update.
            tflite = new Interpreter(loadModelFile());
        } catch (Exception ex){
            Msg.showToast(this.activity, this.toast, activity.getString(R.string.error_loading_model), Toast.LENGTH_LONG);
        }

        // Initialize sensors info and sensor manager.
        this.sensorsInfo = new SensorsInfo();
        // Initialize sensor manager.
        this.sensorManager = (SensorManager) this.activity.getSystemService(Context.SENSOR_SERVICE);

        // Initial values of movements.
        current_movement = Movements.NO_MOVEMENT;
        recognized_movement = Movements.NO_MOVEMENT;
        this.errors = 0;

        // When activity starts, it is not recording data.
        this.recordingData = false;

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            Log.i("SENSOR", "TYPE_ACCELEROMETER available.");
        } else {
            Log.e("SENSOR", "TYPE_ACCELEROMETER not available.");
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
            Log.i("SENSOR", "TYPE_GRAVITY available.");
        } else {
            Log.e("SENSOR", "TYPE_GRAVITY not available.");
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            Log.i("SENSOR", "TYPE_GYROSCOPE available.");
        } else {
            Log.e("SENSOR", "TYPE_GYROSCOPE not available.");
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null){
            Log.i("SENSOR", "TYPE_LINEAR_ACCELERATION available.");
        } else {
            Log.e("SENSOR", "TYPE_LINEAR_ACCELERATION not available.");
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null){
            Log.i("SENSOR", "TYPE_GAME_ROTATION_VECTOR available.");
        } else {
            Log.e("SENSOR", "TYPE_GAME_ROTATION_VECTOR not available.");
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
            Log.i("SENSOR", "TYPE_MAGNETIC_FIELD available.");
        } else {
            Log.e("SENSOR", "TYPE_MAGNETIC_FIELD not available.");
        }
    }

    /**
     * Ask the user for a movement, record it, and give feedback.
     * @param text Texto to say to user.
     */
    private void askForMovement(String text){
        // Give indications to user.
        ActivityFunctions.speak(text, tts, true);
        // Start recording.
        startRecording();
        // Wait 3 seconds for the user to execute the movement.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Stop recording.
        stopRecording();
        // Infer movement using the model previously trained.
        recognized_movement = Movements.values()[doInference(sensorsInfo)];
        // Give feedback to the user.
        giveFeedback();
    }

    /**
     * Make inference and return inferred class.
     * @param sensorsInfo Input to the model.
     * @return Output of the model, e.g., inferred class.
     */
    private int doInference(SensorsInfo sensorsInfo){
        // Input shape is [1]
        float [][][] rawInput = sensorsInfo.asArray();
        float[][][] inputVal = Mathematics.EWMA(rawInput);

        // Output shape is [1][1]
        float[][] outputVal = new float[1][6];

        // Run inference passing the input shape and getting the output shape.
        tflite.run(inputVal, outputVal);

        // Return it
        return Mathematics.getIndexOfMax(outputVal);
    }

    /**
     * Return if the recognized movement is the correct one, if no movement has been recognized, or
     * if there was an error.
     * @param recognized_movement Last recognized movement.
     * @param current_movement Last movement correctly executed.
     * @return 1-5 if the movement is the correct one, 0 if no movement has been recognized, or -1
     * if there was an error.
     */
    private Movements isCorrectMovement(Movements recognized_movement, Movements current_movement){
        if(current_movement == Movements.NO_MOVEMENT && recognized_movement == Movements.UPWARD_BLOCK)
            return Movements.UPWARD_BLOCK;
        else if(current_movement == Movements.UPWARD_BLOCK && recognized_movement == Movements.INWARD_BLOCK)
            return Movements.INWARD_BLOCK;
        else if(current_movement == Movements.INWARD_BLOCK && recognized_movement == Movements.OUTWARD_EXTENDED_BLOCK)
            return Movements.OUTWARD_EXTENDED_BLOCK;
        else if(current_movement == Movements.OUTWARD_EXTENDED_BLOCK && recognized_movement == Movements.DOWNWARD_OUTWARD_BLOCK)
            return Movements.DOWNWARD_OUTWARD_BLOCK;
        else if(current_movement == Movements.DOWNWARD_OUTWARD_BLOCK && recognized_movement == Movements.REAR_ELBOW_BLOCK)
            return Movements.REAR_ELBOW_BLOCK;
        else if(recognized_movement == Movements.NO_RECOGNIZED)
            return Movements.NO_RECOGNIZED;
        else return Movements.WRONG_MOVEMENT;
    }

    /**
     * Give verbal feedback if the executed movement is the correct one.
     */
    private void giveFeedback(){
        if(isCorrectMovement(recognized_movement, current_movement) == Movements.UPWARD_BLOCK){
            Multimedia.playSound(this.activity, R.raw.correct_movement);
            ActivityFunctions.speak(activity.getString(R.string.great), tts, true);
            current_movement = Movements.UPWARD_BLOCK;
        } else if(isCorrectMovement(recognized_movement, current_movement) == Movements.INWARD_BLOCK) {
            Multimedia.playSound(this.activity, R.raw.correct_movement);
            ActivityFunctions.speak(activity.getString(R.string.thats_it), tts, true);
            current_movement = Movements.INWARD_BLOCK;
        } else if(isCorrectMovement(recognized_movement, current_movement) == Movements.OUTWARD_EXTENDED_BLOCK) {
            Multimedia.playSound(this.activity, R.raw.correct_movement);
            ActivityFunctions.speak(activity.getString(R.string.perfect), tts, true);
            current_movement = Movements.OUTWARD_EXTENDED_BLOCK;
        } else if(isCorrectMovement(recognized_movement, current_movement) == Movements.DOWNWARD_OUTWARD_BLOCK) {
            Multimedia.playSound(this.activity, R.raw.correct_movement);
            ActivityFunctions.speak(activity.getString(R.string.good_movement), tts, true);
            current_movement = Movements.DOWNWARD_OUTWARD_BLOCK;
        } else if(isCorrectMovement(recognized_movement, current_movement) == Movements.REAR_ELBOW_BLOCK) {
            Multimedia.playSound(this.activity, R.raw.correct_movement);
            ActivityFunctions.speak(activity.getString(R.string.finish_set), tts, true);
            current_movement = Movements.REAR_ELBOW_BLOCK;
        } else if(isCorrectMovement(recognized_movement, current_movement) == Movements.NO_RECOGNIZED) {
            ActivityFunctions.speak(activity.getString(R.string.no_recognized), tts, true);
        } else if(isCorrectMovement(recognized_movement, current_movement) == Movements.WRONG_MOVEMENT) {
            Vibration.vibrate(this.activity, 500);
            Multimedia.playSound(this.activity, R.raw.wrong_movement);
            ActivityFunctions.speak(activity.getString(R.string.wrong_movement), tts, true);
            errors++;
        } else {
            ActivityFunctions.speak(activity.getString(R.string.error), tts, true);
        }
    }

    /**
     * Load the model file and return as mappedbytebuffer.
     * @return Model as mappedbytebuffer.
     * @throws IOException If the file cannot be read, return exception.
     */
    private MappedByteBuffer loadModelFile() throws IOException {
        //TODO The use of MappedByteBuffer for loading the tfmodel is deprecated, update.
        AssetFileDescriptor fileDescriptor = this.activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /**
     * Execute main loop in background.
     * @param Voids Nothing.
     * @return Nothing.
     */
    @Override
    protected Boolean doInBackground(Void... Voids) {
        // Establish that we have not finished.
        boolean finished = false;

        // Main loop, give indications about the movements to the user and then analyze the
        // movements and gives feedback.
        while(!finished){
            if(current_movement == Movements.NO_MOVEMENT) {
                askForMovement(activity.getString(R.string.upward_block));
            }
            if(current_movement == Movements.UPWARD_BLOCK) {
                askForMovement(activity.getString(R.string.inward_block));
            }
            if(current_movement == Movements.INWARD_BLOCK) {
                askForMovement(activity.getString(R.string.outward_extended_block));
            }
            if(current_movement == Movements.OUTWARD_EXTENDED_BLOCK) {
                askForMovement(activity.getString(R.string.downward_outward_block));
            }
            if(current_movement == Movements.DOWNWARD_OUTWARD_BLOCK) {
                askForMovement(activity.getString(R.string.rear_elbow_block));
            }
            if(current_movement == Movements.REAR_ELBOW_BLOCK) {
                finished = true;
                ActivityFunctions.speak(activity.getString(R.string.errors_commited) + " " + errors + " " + activity.getString(R.string.errors), tts, true);
            }
        }
        // Restore values for next execution.
        current_movement = Movements.NO_MOVEMENT;
        recognized_movement = Movements.NO_MOVEMENT;
        this.errors = 0;

        return true;
    }

    /**
     * Not used.
     * @param sensor Not used.
     * @param accuracy Not used.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Save values read by the sensors.
     * @param event Event containing information about the sensor and the captured data.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            sensorsInfo.addAccelerometerReading(event.values.clone());
        } else if (event.sensor.getType()==Sensor.TYPE_GRAVITY){
            sensorsInfo.addGravityReading(event.values.clone());
        } else if (event.sensor.getType()==Sensor.TYPE_GYROSCOPE){
            sensorsInfo.addGyroscopeReading(event.values.clone());
        } else if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION) {
            sensorsInfo.addLinearAccelerationReading(event.values.clone());
        } else if(event.sensor.getType()==Sensor.TYPE_GAME_ROTATION_VECTOR){
            sensorsInfo.addGameRotationVectorReading(event.values.clone());
        } else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            sensorsInfo.addMagneticFieldReading(event.values.clone());
        }
    }

    /**
     * Register listener for every sensor we want.
     */
    private void registerSensorListeners(){
        if(!this.recordingData){
            this.recordingData = true;
            if ( this.sensorManager != null) {
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SENSOR_DELAY);
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SENSOR_DELAY);
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SENSOR_DELAY);
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SENSOR_DELAY);
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR), SENSOR_DELAY);
                this.sensorManager.registerListener(this, this.sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SENSOR_DELAY);
            } else {
                Msg.showToast(this.activity, toast, activity.getString(R.string.sensormanager_not_instantiated), Toast.LENGTH_SHORT);
            }
        } else {
            Msg.showToast(this.activity, toast, activity.getString(R.string.already_recording), Toast.LENGTH_SHORT);
        }
    }

    /**
     * Unregister sensors that are recording data.
     */
    private void unregisterSensorListeners(){
        if(this.recordingData) {
            this.recordingData = false;
            // Don't receive any more updates from either sensor.
            sensorManager.unregisterListener(this);
            // Fill empty due to lack of sensor.
            this.sensorsInfo.fillEmptyArrays();
            // List of string to store the data.
            List<String> dataFromSensors = new LinkedList<>();
            // Add each row to file.
            for(int i = sensorsInfo.getLowestArraySize() - 1; i >= 0; i--){
                dataFromSensors.add(this.sensorsInfo.getLine(i));
            }
            // Set header of the file at the end since the array will be reversed.
            dataFromSensors.add(this.sensorsInfo.getHeader());
            // Since each row was added in reverse order, reverse array.
            Collections.reverse(dataFromSensors);
        }
    }

    /**
     * Start recording data.
     */
    private void startRecording(){
        this.sensorsInfo.clear();
        registerSensorListeners();
        recordingData = true;
        Multimedia.playSound(this.activity, R.raw.start_recording);
    }

    /**
     * Stop recording data.
     */
    private void stopRecording(){
        unregisterSensorListeners();
        recordingData = false;
    }

}
