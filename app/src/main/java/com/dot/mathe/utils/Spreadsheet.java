package com.dot.mathe.utils;

import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.List;

public class Spreadsheet {

    public static final String SUM = "SUM";
    public static final String MINUS = "MINUS";
    public static final String AVG = "AVG";
    public static final String MAX = "MAX";
    public static final String MIN = "MIN";
    public static final String COUNT = "COUNT";
    public static final String MEDIAN = "MEDIAN";

    public static boolean isValidFormula(String string) {
        if (string.startsWith("=") && string.endsWith(")")) {
            String[] split = string.split("\\=");
            if (split.length > 1) {
                String[] firstBrace = split[1].split("\\(");
                if (firstBrace.length > 1) {
                    if (isValidFunction(firstBrace[0])) {
                        String[] afterFirstBrace = firstBrace[1].split("\\:");
                        if (afterFirstBrace.length > 1) {
                            String[] secondBrace = afterFirstBrace[1].split("\\)");
                            if (secondBrace.length > 0) {
                                int firstColumn = getRowIndex(afterFirstBrace[0]);
                                int secondColumn = getRowIndex(secondBrace[0]);
                                return firstColumn > 0 && secondColumn > 0;
                            } else {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static int[] getColumnIndex(String string) {
        if (string.startsWith("=") && string.endsWith(")")) {
            String[] split = string.split("\\=");
            if (split.length > 1) {
                String[] firstBrace = split[1].split("\\(");
                if (firstBrace.length > 1) {
                    if (isValidFunction(firstBrace[0])) {
                        String[] afterFirstBrace = firstBrace[1].split("\\:");
                        if (afterFirstBrace.length > 1) {
                            String[] secondBrace = afterFirstBrace[1].split("\\)");
                            if (secondBrace.length > 0) {
                                int firstColumn = getRowIndex(afterFirstBrace[0]);
                                int secondColumn = getRowIndex(secondBrace[0]);
                                if (firstColumn > 0 && secondColumn > 0) {
                                    return new int[]{firstColumn, secondColumn};
                                } else {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static double calculate(String string, List<Double> sheetValue) {
        String[] split = string.split("\\=");
        String[] firstBrace = split[1].split("\\(");
        String function = firstBrace[0];
        if (function.trim().equals(SUM)) {
            return Calculator.calculateSum(sheetValue);
        } else if (function.trim().equals(AVG)) {
            return Calculator.calculateAvg(sheetValue);
        } else if (function.trim().equals(MINUS)) {
            return Calculator.calculateMinus(sheetValue);
        } else if (function.trim().equals(MAX)) {
            return Calculator.calculateMax(sheetValue);
        } else if (function.trim().equals(MIN)) {
            return Calculator.calculateMin(sheetValue);
        } else if (function.trim().equals(COUNT)) {
            return Calculator.calculateCount(sheetValue);
        } else {
            return Calculator.calculateMedian(sheetValue);
        }
    }

    private static boolean isValidFunction(String value) {
        if (value.trim().equals(SUM)) {
            return true;
        } else if (value.trim().equals(AVG)) {
            return true;
        } else if (value.trim().equals(MINUS)) {
            return true;
        } else if (value.trim().equals(MAX)) {
            return true;
        } else if (value.trim().equals(MIN)) {
            return true;
        } else if (value.trim().equals(COUNT)) {
            return true;
        } else return value.trim().equals(MEDIAN);
    }

    private static int getRowIndex(String value) {
        if (value.equals("A1")) {
            return 1;
        } else if (value.equals("A2")) {
            return 3;
        } else if (value.equals("A3")) {
            return 5;
        } else if (value.equals("A4")) {
            return 7;
        } else if (value.equals("A5")) {
            return 9;
        } else if (value.equals("A6")) {
            return 11;
        } else if (value.equals("A7")) {
            return 13;
        } else if (value.equals("A8")) {
            return 1;
        } else if (value.equals("A9")) {
            return 17;
        } else if (value.equals("A10")) {
            return 19;
        } else if (value.equals("B1")) {
            return 2;
        } else if (value.equals("B2")) {
            return 4;
        } else if (value.equals("B3")) {
            return 6;
        } else if (value.equals("B4")) {
            return 8;
        } else if (value.equals("B5")) {
            return 10;
        } else if (value.equals("B6")) {
            return 12;
        } else if (value.equals("B7")) {
            return 14;
        } else if (value.equals("B8")) {
            return 16;
        } else if (value.equals("B9")) {
            return 18;
        } else if (value.equals("B10")) {
            return 20;
        } else {
            return 0;
        }
    }

    public static double getExpressionValue(String value) {
        Expression expression = new Expression(value);
        try {
            BigDecimal eval = expression.eval();
            String evalToString = String.valueOf(eval.doubleValue());
            if (evalToString.matches("[0-9.]*")) {
                return eval.doubleValue();
            } else return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
