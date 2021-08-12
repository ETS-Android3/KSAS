package com.albertocasasortiz.ksas.recognizer;


import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ErrorsCommitted {
    ArrayList<Pair<Movements, Integer>> errors;

    public ErrorsCommitted() {
        errors = new ArrayList<>();
    }

    public void addError(Movements movement) {
        if(errors.isEmpty()) {
            errors.add(new Pair<Movements, Integer>(movement, 1));
        } else {
            for (int i = 0; i < errors.size(); i++) {
                if (errors.get(i).first == movement) {
                    errors.set(i, new Pair<Movements, Integer>(errors.get(i).first, errors.get(i).second + 1));
                    break;
                } else {
                    errors.add(new Pair<Movements, Integer>(movement, 1));
                }
            }
        }
    }

    public int getNumErrorsOfMovement(Movements movement) {
        for(int i = 0; i < errors.size(); i++) {
            if(errors.get(i).first.equals(movement)) {
                return errors.get(i).second;
            }
        }
        return 0;
    }

    public int getTotalNumberOfErrors() {
        int res = 0;
        for(int i = 0; i < errors.size(); i++) {
            res += errors.get(i).second;
        }
        return res;
    }

    public LinkedList<String> getAsListOfStrings() {
        LinkedList<String> list = new LinkedList<>();
        for (Pair<Movements, Integer> pair : errors) {
            list.add(pair.first.name() + ", " + pair.second.toString());
        }
        return list;
    };
}
