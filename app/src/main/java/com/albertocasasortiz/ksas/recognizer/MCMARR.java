package com.albertocasasortiz.ksas.recognizer;

/**
 * MCMARR interface defining the phases of the framework.
 */
public interface MCMARR {
    /**
     * Terminate condition. Finish when the execution of the movements finish.
     * @return Boolean indicating if the execution of the movements has finished.
     */
    boolean finish();
    /**
     * Give indications to the learner. This is actually part of te Motion Response phase, but I
     * have separated it here for clarity.
     */
    void giveIndications();
    /**
     * Capture information of the movements from the sensor.
     */
    void captureMovements();
    /**
     * Model the captured movements. The captured information is stored in
     * a SensorsInfo object, so it is not necessary to pass it here.
     * @return Movements modeled as synchronized data series with EWMA applied.
     */
    float[][][] modelMovements();
    /**
     * Analyze the captured and modeled movements.
     * @param movements Movements modeled as data series.
     * @return Integer indicating the class of the data series (the movement).
     */
    int analyzeMovements(float [][][] movements);
    /**
     * Given a recognized movement, give feedback to the user.
     * @param recognizedMovement Movement recognized by the analysis phase.
     */
    void giveFeedback(int recognizedMovement);
}

