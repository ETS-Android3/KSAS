package com.albertocasasortiz.ksas.auxfunctions;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Store information recorded by sensors.
 */
public class SensorsInfo {
    /** Total acceleration recorded by accelerometer. */
    private ArrayList<float[]> accelerometerSequence;
    /** Gravity recorded by accelerometer. */
    private ArrayList<float[]> gravitySequence;
    /** Amount of turn recorded by gyroscope. */
    private ArrayList<float[]> gyroscopeSequence;
    /** Linear acceleration (acc - grav) recorded by accelerometer. */
    private ArrayList<float[]> linearAccelerationSequence;
    /** Game rotation vector. */
    private ArrayList<float[]> gameRotationVectorSequence;
    /** Magnetic field recorded by magnetometer. */
    private ArrayList<float[]> magneticFieldSequence;

    /**
     * Constructor of class SensorsInfo.
     */
    public SensorsInfo(){
        this.accelerometerSequence = new ArrayList<>();
        this.gravitySequence = new ArrayList<>();
        this.gyroscopeSequence = new ArrayList<>();
        this.linearAccelerationSequence = new ArrayList<>();
        this.gameRotationVectorSequence = new ArrayList<>();
        this.magneticFieldSequence = new ArrayList<>();
    }

    /**
     * Add a reading to accelerometer array.
     * @param accelerometterSequence Reading of accelerometer.
     */
    public void addAccelerometerReading(float[] accelerometterSequence){
        this.accelerometerSequence.add(accelerometterSequence);
    }

    /**
     * Add a reading to gravity array.
     * @param gravitySequence Reading of gravity.
     */
    public void addGravityReading(float[] gravitySequence) {
        this.gravitySequence.add(gravitySequence);
    }

    /**
     * Add a reading to gyroscope array.
     * @param gyroscopeSequence Reading of gyroscope.
     */
    public void addGyroscopeReading(float[] gyroscopeSequence) {
        this.gyroscopeSequence.add(gyroscopeSequence);
    }

    /**
     * Add a reading to linear accelerometer array.
     * @param linearAccelerationSequence Reading of linear accelerometer.
     */
    public void addLinearAccelerationReading(float[] linearAccelerationSequence) {
        this.linearAccelerationSequence.add(linearAccelerationSequence);
    }

    /**
     * Add a reading to game rotation array.
     * @param gameRotationVectorSequence Reading of game rotation.
     */
    public void addGameRotationVectorReading(float[] gameRotationVectorSequence) {
        this.gameRotationVectorSequence.add(gameRotationVectorSequence);
    }

    /**
     * Add a reading to magnetic field array.
     * @param magneticFieldSequence Reading of magnetic field.
     */
    public void addMagneticFieldReading(float[] magneticFieldSequence) {
        this.magneticFieldSequence.add(magneticFieldSequence);
    }

    /**
     * Get a row of the captured data at a determined movement.
     * @param i Row of data.
     * @return Row of captured data.
     */
    public String getLine(int i){
        String result = this.format(this.accelerometerSequence.get(i)[0]) + ";" + this.format(this.accelerometerSequence.get(i)[1]) + ";" + this.format(this.accelerometerSequence.get(i)[2]) + ";";
        result += this.format(this.gravitySequence.get(i)[0]) + ";" + this.format(this.gravitySequence.get(i)[1]) + ";" + this.format(this.gravitySequence.get(i)[2]) + ";";
        result += this.format(this.gyroscopeSequence.get(i)[0]) + ";" + this.format(this.gyroscopeSequence.get(i)[1]) + ";" + this.format(this.gyroscopeSequence.get(i)[2]) + ";";
        result += this.format(this.linearAccelerationSequence.get(i)[0]) + ";" + this.format(this.linearAccelerationSequence.get(i)[1]) + ";" + this.format(this.linearAccelerationSequence.get(i)[2]) + ";";
        result += this.format(this.gameRotationVectorSequence.get(i)[0]) + ";" + this.format(this.gameRotationVectorSequence.get(i)[1]) + ";" + this.format(this.gameRotationVectorSequence.get(i)[2]) + ";";
        result += this.format(this.magneticFieldSequence.get(i)[0]) + ";" + this.format(this.magneticFieldSequence.get(i)[1]) + ";" + this.format(this.magneticFieldSequence.get(i)[2]);
        return result;
    }

    /**
     * Decimal format of the data.
     * @param i Number to format
     * @return Formatted number.
     */
    private String format(float i){
        DecimalFormat df = new DecimalFormat();
        return df.format(i);
    }

    /**
     * Generate header of the dataset.
     * @return Header of the dataset.
     */
    public String getHeader(){
        String result = "accelerometer_x;accelerometer_y;accelerometer_z;";
        result += "gravity_x;gravity_y;gravity_z;";
        result += "gyros_x;gyros_y;gyros_z;";
        result += "lin_accel_x;lin_accel_y;lin_accel_z;";
        result += "game_rot_vec_x;game_rot_vec_y;game_rot_vec_z;";
        result += "magn_field_x;magn_field_y;magn_field_z";
        return result;
    }

    /**
     * Get lowest array size to discard extra readings.
     * @return Lowest size of the arrays.
     */
    public int getLowestArraySize(){
        int minSize = accelerometerSequence.size();
        if(gravitySequence.size() != 0 && gyroscopeSequence.size() < minSize)
            minSize = gravitySequence.size();
        if(gyroscopeSequence.size() != 0 && gyroscopeSequence.size() < minSize)
            minSize = gyroscopeSequence.size();
        if(linearAccelerationSequence.size() != 0 && linearAccelerationSequence.size() < minSize)
            minSize = linearAccelerationSequence.size();
        if(gameRotationVectorSequence.size() != 0 && gameRotationVectorSequence.size() < minSize)
            minSize = gameRotationVectorSequence.size();
        if(magneticFieldSequence.size() != 0 && magneticFieldSequence.size() < minSize)
            minSize = magneticFieldSequence.size();
        return minSize;
    }

    /**
     * Clear all data.
     */
    public void clear(){
        this.accelerometerSequence.clear();
        this.gravitySequence.clear();
        this.gyroscopeSequence.clear();
        this.linearAccelerationSequence.clear();
        this.gameRotationVectorSequence.clear();
        this.magneticFieldSequence.clear();
    }

    /**
     * Get the sensor data into a matrix of dimensions:
     * · Dimension 1: Always 1.
     * · Dimension 2: Number of attributes.
     * · Dimension 3: Number of examples per attribute.
     * @return Data structured as array.
     */
    public float[][][] asArray(){
        int lowestArrSize = this.getLowestArraySize();
        float[][][] array= new float[1][18][lowestArrSize];
        for(int i = 0; i < lowestArrSize; i++){
            array[0][0][i] = accelerometerSequence.get(i)[0];
            array[0][1][i] = accelerometerSequence.get(i)[1];
            array[0][2][i] = accelerometerSequence.get(i)[2];
            array[0][3][i] = gravitySequence.get(i)[0];
            array[0][4][i] = gravitySequence.get(i)[1];
            array[0][5][i] = gravitySequence.get(i)[2];
            array[0][6][i] = gyroscopeSequence.get(i)[0];
            array[0][7][i] = gyroscopeSequence.get(i)[1];
            array[0][8][i] = gyroscopeSequence.get(i)[2];
            array[0][9][i] = linearAccelerationSequence.get(i)[0];
            array[0][10][i] = linearAccelerationSequence.get(i)[1];
            array[0][11][i] = linearAccelerationSequence.get(i)[2];
            array[0][12][i] = gameRotationVectorSequence.get(i)[0];
            array[0][13][i] = gameRotationVectorSequence.get(i)[1];
            array[0][14][i] = gameRotationVectorSequence.get(i)[2];
            array[0][15][i] = magneticFieldSequence.get(i)[0];
            array[0][16][i] = magneticFieldSequence.get(i)[1];
            array[0][17][i] = magneticFieldSequence.get(i)[2];
        }
        array = Mathematics.getMovementFromSequence(array);
        return array;
    }

    public void fillEmptyArrays() {
        int lowestArrSize = this.getLowestArraySize();
        for(int i = 0; i < lowestArrSize; i++) {
            if (accelerometerSequence.size() < lowestArrSize) {
                accelerometerSequence.add(new float[3]);
                accelerometerSequence.get(i)[0] = 0;
                accelerometerSequence.get(i)[1] = 0;
                accelerometerSequence.get(i)[2] = 0;
            }
            if (gravitySequence.size() < lowestArrSize) {
                gravitySequence.add(new float[3]);
                gravitySequence.get(i)[0] = 0;
                gravitySequence.get(i)[1] = 0;
                gravitySequence.get(i)[2] = 0;
            }
            if (gyroscopeSequence.size() < lowestArrSize) {
                gyroscopeSequence.add(new float[3]);
                gyroscopeSequence.get(i)[0] = 0;
                gyroscopeSequence.get(i)[1] = 0;
                gyroscopeSequence.get(i)[2] = 0;
            }
            if (linearAccelerationSequence.size() < lowestArrSize) {
                linearAccelerationSequence.add(new float[3]);
                linearAccelerationSequence.get(i)[0] = 0;
                linearAccelerationSequence.get(i)[1] = 0;
                linearAccelerationSequence.get(i)[2] = 0;
            }
            if (gameRotationVectorSequence.size() < lowestArrSize) {
                gameRotationVectorSequence.add(new float[3]);
                gameRotationVectorSequence.get(i)[0] = 0;
                gameRotationVectorSequence.get(i)[1] = 0;
                gameRotationVectorSequence.get(i)[2] = 0;
            }
            if (magneticFieldSequence.size() < lowestArrSize) {
                magneticFieldSequence.add(new float[3]);
                magneticFieldSequence.get(i)[0] = 0;
                magneticFieldSequence.get(i)[1] = 0;
                magneticFieldSequence.get(i)[2] = 0;
            }
        }
    }
}
