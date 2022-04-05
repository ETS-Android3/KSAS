package com.albertocasasortiz.ksas.recognizer;


import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class for managing the number of errors commited per movement.
 */
public class ErrorsCommitted {
    // Array containing pairs of movements, and number of errors for that movement.
    ArrayList<Pair<Movements, Integer>> errors;

    /**
     * Constructor for class ErrorsCommited.
     */
    public ErrorsCommitted() {
        errors = new ArrayList<>();
    }

    /**
     * Add an error to a specific movement.
     * @param movement Movement of which an error has been commited.
     */
    public void addError(Movements movement) {
        if(errors.isEmpty()) {
            errors.add(new Pair<>(movement, 1));
        } else {
            for (int i = 0; i < errors.size(); i++) {
                if (errors.get(i).first == movement) {
                    errors.set(i, new Pair<>(errors.get(i).first, errors.get(i).second + 1));
                    break;
                } else {
                    errors.add(new Pair<>(movement, 1));
                }
            }
        }
    }

    /**
     * Get number of errors commited for a specific movement.
     * @param movement Movement we are retrieving the number of errors.
     * @return Number of errors commited for a movement.
     */
    public int getNumErrorsOfMovement(Movements movement) {
        for(int i = 0; i < errors.size(); i++) {
            if(errors.get(i).first.equals(movement)) {
                return errors.get(i).second;
            }
        }
        return 0;
    }

    /**
     * Get total number of errors commited in a session, independently of the movement.
     * @return Total number of errors.
     */
    public int getTotalNumberOfErrors() {
        int res = 0;
        for(int i = 0; i < errors.size(); i++) {
            res += errors.get(i).second;
        }
        return res;
    }

    /**
     * Get errors commited as a list of strings.
     * @return List of string values containing movement names and number of errors.
     */
    public LinkedList<String> getAsListOfStrings() {
        LinkedList<String> list = new LinkedList<>();
        for (Pair<Movements, Integer> pair : errors) {
            list.add(pair.first.name() + ", " + pair.second.toString());
        }
        return list;
    }
}
