package com.albertocasasortiz.ksas.auxfunctions;

/**
 * Class with mathematical functions.
 */
public class Mathematics {

    /**
     * Execute EWMA over a tridimensional matrix and return the resulting sequences.
     * @param array Arrays to apply ewma.
     * @return Resulting sequences of applying ewma in the data.
     */
    public static float[][][] EWMA(float[][][] array){
        int pos = 0;
        int numExample = 0;
        float[][] example = new float[array[0].length][array[0][0].length];
        for(float[] attribute : array[0]){
            float[] newAttribute = new float[attribute.length];
            newAttribute[0] = attribute[0];
            for(float value : attribute){
                float newValue = (0.5f * value) + (1-0.5f) * newAttribute[pos];
                if(pos+1 < array[0][0].length)
                    newAttribute[pos+1] = newValue;
                pos++;
            }
            pos = 0;
            example[numExample] = newAttribute.clone();
            numExample++;
        }
        float[][][] resArray = new float[array.length][array[0].length][array[0][0].length];
        resArray[0] = example;
        return resArray;
    }

    /**
     * Get a portion of the array that contains a movement. The movement is detected over the
     * gyroscope data, obtaining the sum of the abs value of its three coordinates, and getting
     * the window of the sequence that maximizes it. The size is always the maximum allowed by the
     * ml model, and it is 56.
     * @param array Array from which we obtain the movement.
     * @return Sequence containing the movement.
     */
    public static float[][][] getMovementFromSequence(float[][][] array){
        int windowSize = 56;
        float max = 0;
        int begin = 0;
        int end = 55;
        int resBegin = 0;
        while(end < array[0][0].length){
            float value = 0;
            for(int i = begin; i < end; i++){
                if((Math.abs(array[0][6][begin]) + Math.abs(array[0][7][begin]) + Math.abs(array[0][8][begin])) > 2)
                    value += Math.abs(array[0][6][i]) + Math.abs(array[0][7][i]) + Math.abs(array[0][8][i]);
            }
            if(value > max) {
                max = value;
                resBegin = begin;
            }
            begin++;
            end++;
        }
        float[][][] res = new float[array.length][array[0].length][windowSize];
        for(int i = 0; i < array[0].length; i++){
            System.arraycopy(array[0][i], resBegin, res[0][i], 0, windowSize);
        }
        return res;
    }

    /**
     * Get second index of the maximum value in a float matrix.
     * @param numbers Float matrix.
     * @return Second index of the maximum value in a float matrix.
     */
    public static int getIndexOfMax(float[][] numbers){
        float maxValue = numbers[0][0];
        int maxIndex = 0;
        for(int i=0; i < numbers[0].length; i++){
            if(numbers[0][i] > maxValue){
                maxIndex = i;
                maxValue = numbers[0][i];
            }
        }
        return maxIndex;
    }

}
