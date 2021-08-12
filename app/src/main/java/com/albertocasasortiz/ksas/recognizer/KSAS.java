package com.albertocasasortiz.ksas.recognizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.albertocasasortiz.ksas.R;
import com.albertocasasortiz.ksas.activity.ActivityReport;
import com.albertocasasortiz.ksas.auxfunctions.ActivityFunctions;
import com.albertocasasortiz.ksas.auxfunctions.Mathematics;
import com.albertocasasortiz.ksas.auxfunctions.Msg;
import com.albertocasasortiz.ksas.auxfunctions.Multimedia;
import com.albertocasasortiz.ksas.auxfunctions.SensorsInfo;
import com.albertocasasortiz.ksas.auxfunctions.Vibration;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class KSAS extends AsyncTask<Void, Movements, Boolean> implements SensorEventListener, MCMARR {
    // TODO fix possible memory leak. Only solution I see is to propagate
    // the variable everywhere, so I keep this here.
    // Activity in which the task is being executed.
    @SuppressLint("StaticFieldLeak")
    private final AppCompatActivity activity;

    // Manager for sensor reading.
    private final SensorManager sensorManager;
    // Class to store sensor info.
    private final SensorsInfo sensorsInfo;
    // Delay of sensor readings.
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;
    // Boolean that indicates if the sensors are reading data.
    private boolean recordingData;

    /** Allows to convert from text to voice.*/
    protected TextToSpeech tts;
    // Interpreter to load the model.
    private Interpreter tflite;

    // Indicates which is the current movement.
    private Movements current_movement;

    // Toast for showing messages to the user.
    private final Toast toast;

    // Determines if an execution has finished.
    private boolean finish;

    // VideoView Video view for showing the video.
    @SuppressLint("StaticFieldLeak")
    private VideoView videoView;

    // Register any error committed by the user.
    private final ErrorsCommitted errorsCommitted;

    // TODO This default constructor is deprecated now.
    public KSAS(AppCompatActivity activity, TextToSpeech tts) {
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

        // Initialize errors committed
        errorsCommitted = new ErrorsCommitted();

        // When activity starts, it is not recording data.
        this.recordingData = false;
    }

    @Override
    public boolean finish() {
        return this.finish;
    }

    @Override
    public void giveIndications() {
        if(current_movement == Movements.NO_MOVEMENT) {
            publishProgress(current_movement);
            ActivityFunctions.speak(activity.getString(R.string.upward_block), tts, true);
        }
        if(current_movement == Movements.UPWARD_BLOCK) {
            publishProgress(current_movement);
            ActivityFunctions.speak(activity.getString(R.string.inward_block), tts, true);
        }
        if(current_movement == Movements.INWARD_BLOCK) {
            publishProgress(current_movement);
            ActivityFunctions.speak(activity.getString(R.string.outward_extended_block), tts, true);
        }
        if(current_movement == Movements.OUTWARD_EXTENDED_BLOCK) {
            publishProgress(current_movement);
            ActivityFunctions.speak(activity.getString(R.string.downward_outward_block), tts, true);
        }
        if(current_movement == Movements.DOWNWARD_OUTWARD_BLOCK) {
            publishProgress(current_movement);
            ActivityFunctions.speak(activity.getString(R.string.rear_elbow_block), tts, true);
        }
        if(current_movement == Movements.REAR_ELBOW_BLOCK) {
            publishProgress(current_movement);
            ActivityFunctions.speak(activity.getString(R.string.errors_commited) + " " + errorsCommitted.getTotalNumberOfErrors() + " " + activity.getString(R.string.errors), tts, true);
        }
    }

    @Override
    public void giveFeedback(int recognizedMovement){
        if(isCorrectMovement(Movements.values()[recognizedMovement], current_movement) == Movements.UPWARD_BLOCK){
            Multimedia.playSound(this.activity, R.raw.correct_movement);
            ActivityFunctions.speak(activity.getString(R.string.great), tts, true);
            current_movement = Movements.UPWARD_BLOCK;
        } else if(isCorrectMovement(Movements.values()[recognizedMovement], current_movement) == Movements.INWARD_BLOCK) {
            Multimedia.playSound(this.activity, R.raw.correct_movement);
            ActivityFunctions.speak(activity.getString(R.string.thats_it), tts, true);
            current_movement = Movements.INWARD_BLOCK;
        } else if(isCorrectMovement(Movements.values()[recognizedMovement], current_movement) == Movements.OUTWARD_EXTENDED_BLOCK) {
            Multimedia.playSound(this.activity, R.raw.correct_movement);
            ActivityFunctions.speak(activity.getString(R.string.perfect), tts, true);
            current_movement = Movements.OUTWARD_EXTENDED_BLOCK;
        } else if(isCorrectMovement(Movements.values()[recognizedMovement], current_movement) == Movements.DOWNWARD_OUTWARD_BLOCK) {
            Multimedia.playSound(this.activity, R.raw.correct_movement);
            ActivityFunctions.speak(activity.getString(R.string.good_movement), tts, true);
            current_movement = Movements.DOWNWARD_OUTWARD_BLOCK;
        } else if(isCorrectMovement(Movements.values()[recognizedMovement], current_movement) == Movements.REAR_ELBOW_BLOCK) {
            Multimedia.playSound(this.activity, R.raw.correct_movement);
            ActivityFunctions.speak(activity.getString(R.string.finish_set), tts, true);
            current_movement = Movements.REAR_ELBOW_BLOCK;
            finish = true;
        } else if(isCorrectMovement(Movements.values()[recognizedMovement], current_movement) == Movements.NO_RECOGNIZED) {
            ActivityFunctions.speak(activity.getString(R.string.no_recognized), tts, true);
        } else if(isCorrectMovement(Movements.values()[recognizedMovement], current_movement) == Movements.WRONG_MOVEMENT) {
            Vibration.vibrate(this.activity, 500);
            Multimedia.playSound(this.activity, R.raw.wrong_movement);
            ActivityFunctions.speak(activity.getString(R.string.wrong_movement), tts, true);
            errorsCommitted.addError(current_movement);
        } else {
            ActivityFunctions.speak(activity.getString(R.string.error), tts, true);
        }
    }

    @Override
    public void captureMovements() {
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
    }

    @Override
    public float[][][] modelMovements() {
        // Input shape is [1]
        float [][][] rawInput = sensorsInfo.asArray();
        return Mathematics.EWMA(rawInput);
    }

    @Override
    public int analyzeMovements(float [][][] movements) {
        // Output shape is [1][1]
        float[][] outputVal = new float[1][6];

        // Run inference passing the input shape and getting the output shape.
        tflite.run(movements, outputVal);

        // Return it
        return Mathematics.getIndexOfMax(outputVal);
    }





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

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        // Follow mCMARR framework inside the loop.
        while(!finish()) {
            giveIndications();
            captureMovements();
            float[][][] model = modelMovements();
            int recognizedMovement = analyzeMovements(model);
            giveFeedback(recognizedMovement);
        }
        // Give final indications. Reports would be here.
        ActivityFunctions.speak(activity.getString(R.string.errors_commited) + " " + errorsCommitted.getTotalNumberOfErrors() + " " + activity.getString(R.string.errors), tts, true);

        // Save errors in file.
        if (isExternalStorageWritable()) {
            // Get date and time to set the name.
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CANADA);
            Date date = new Date();

            // First, lest save training session.
                // Construct fileName.
                String fileName = "trainingSession" + " - " + dateFormat.format(date);

                // List of string to store the data.
                List<String> dataToStore = errorsCommitted.getAsListOfStrings();

                // Add header at beggining
                dataToStore.add(0, "Movements, Errors");

                // String with the format of the saved file.
                String fileFormat = ".csv";

                // Save data
                saveDataInExternalStorage(activity, fileName + fileFormat, dataToStore, false);

            // Now, lest save errors per day.
                // Construct fileName.
                fileName = "trainingErrors";

                // Generate content
                dataToStore.clear();
                dataToStore.add(dateFormat.format(date) + ", " + errorsCommitted.getTotalNumberOfErrors());

                // Save data
                saveDataInExternalStorage(activity, fileName + fileFormat, dataToStore, true);
        }

        return true;
    }

    /**
     * Check if external storage is available for write data.
     * @return True if external storage is available.
     */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Save data contained in a String in the external storage.
     * @param context  Context of the activity.
     * @param fileName Name of the file.
     * @param body     Body of message (As array of strings).
     * @param append   Append to file or not.
     */
    private void saveDataInExternalStorage(Context context, String fileName, List<String> body, boolean append) {
        try {
            // Set folder where save data.
            // String with the path of the files.
            String pathFolder = "KSAS training sessions/";
            File root = new File(context.getExternalFilesDir(null), pathFolder);
            // Create directory if not exists.
            boolean created = true;
            if (!root.exists()) {
                created = root.mkdir();
            }
            if(root.exists() && created) {
                // If directory created...
                // Create file object.
                File gpxfile = new File(root, fileName);
                // Create file writer.
                FileWriter writer = new FileWriter(gpxfile, append);
                // Write data.
                for (int i = 0; i < body.size(); i++) {
                    writer.append(body.get(i));
                    writer.append("\n");
                }
                writer.flush();
                writer.close();
                //Msg.showToast(context, toast, "Data saved in file: " + fileName, Toast.LENGTH_SHORT);
                this.sensorsInfo.clear();
            } else {
                //Msg.showToast(context, toast, "Could not create directory..", Toast.LENGTH_SHORT);
            }
        } catch (IOException e) {
            //Msg.showToast(context, toast, "Error saving data in file.", Toast.LENGTH_SHORT);
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
     * Stops execution of the background task.
     */
    public void stop() {
        this.finish = true;
        this.cancel(true);
    }

    /**
     * Starts execution of the background task.
     */
    public void start(VideoView videoView) {
        this.videoView = videoView;
        this.execute();
    }

    @Override
    protected void onProgressUpdate(Movements... values) {
        if(values[0] == Movements.NO_MOVEMENT) {
            Multimedia.changeVideo(videoView, this.activity, R.raw.b_up, R.id.videoAssistant);
        }
        if(values[0] == Movements.UPWARD_BLOCK) {
            Multimedia.changeVideo(videoView, this.activity, R.raw.c_inner, R.id.videoAssistant);
        }
        if(values[0] == Movements.INWARD_BLOCK) {
            Multimedia.changeVideo(videoView, this.activity, R.raw.d_outer, R.id.videoAssistant);
        }
        if(values[0] == Movements.OUTWARD_EXTENDED_BLOCK) {
            Multimedia.changeVideo(videoView, this.activity, R.raw.e_down, R.id.videoAssistant);
        }
        if(values[0] == Movements.DOWNWARD_OUTWARD_BLOCK) {
            Multimedia.changeVideo(videoView, this.activity, R.raw.f_elbow, R.id.videoAssistant);
        }
        if(values[0] == Movements.REAR_ELBOW_BLOCK) {
            Multimedia.changeVideo(videoView, this.activity, R.raw.a_start, R.id.videoAssistant);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        // If the tts is speaking, wait.
        //TODO Implement this properly
        while (tts.isSpeaking()) {}

        // Intent to next activity.
        Intent myIntent = new Intent(activity, ActivityReport.class);
        activity.startActivity(myIntent);
    }
}
