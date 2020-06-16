package com.albertocasasortiz.ksas.recognizer;

/**
 * Enumeration of movements and status.
 * · No recognized: No movements have been recognized
 * · No movement: Initial state where no movement has been executed.
 * · Wrong movement: A wrong movement has been executed.
 * The rest of enumerations correspond with the executed movements.
 */
public enum Movements {
    NO_RECOGNIZED, UPWARD_BLOCK, INWARD_BLOCK, OUTWARD_EXTENDED_BLOCK, DOWNWARD_OUTWARD_BLOCK, REAR_ELBOW_BLOCK, NO_MOVEMENT, WRONG_MOVEMENT
}
