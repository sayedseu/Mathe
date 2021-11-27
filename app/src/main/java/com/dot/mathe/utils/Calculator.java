package com.dot.mathe.utils;

import java.util.Collections;
import java.util.List;

public class Calculator {

    public static double calculateSum(List<Double> doubleList) {
        double count = 0;
        for (Double value : doubleList) {
            count += value;
        }
        return count;
    }

    public static double calculateMinus(List<Double> doubleList) {
        Collections.sort(doubleList, Collections.<Double>reverseOrder());
        double count = doubleList.get(0);
        for (int i = 1; i < doubleList.size(); i++) {
            count = count - doubleList.get(i);
        }
        return count;
    }

    public static double calculateAvg(List<Double> doubleList) {
        double sum = calculateSum(doubleList);
        return sum / doubleList.size();
    }

    public static double calculateMax(List<Double> doubleList) {
        double max = doubleList.get(0);
        for (Double value : doubleList) {
            if (max < value) {
                max = value;
            }
        }
        return max;
    }

    public static double calculateMin(List<Double> doubleList) {
        double min = doubleList.get(0);
        for (Double value : doubleList) {
            if (min > value) {
                min = value;
            }
        }
        return min;
    }

    public static double calculateCount(List<Double> doubleList) {
        return doubleList.size();
    }

    public static double calculateMedian(List<Double> doubleList) {
        Collections.sort(doubleList);
        int length = doubleList.size();
        if (length % 2 != 0) {
            return doubleList.get(length / 2);
        } else {
            return (doubleList.get((length - 1) / 2) + doubleList.get(length / 2)) / 2.0;
        }
    }

}
