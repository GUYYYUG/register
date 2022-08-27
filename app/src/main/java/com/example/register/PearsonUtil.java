package com.example.register;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class PearsonUtil {

    /**
     * 计算2个list的皮尔逊相关系数
     *
     * @param x
     * @param y
     * @return
     */
    public static double getPearsonCorrelationScore(List x, List y) {
        if (x.size() != y.size()) {
            return -2;
        }
        double[] xData = new double[x.size()];
        double[] yData = new double[x.size()];
        for (int i = 0; i < x.size(); i++) {
            xData[i] = Double.parseDouble(x.get(i).toString());
            yData[i] = Double.parseDouble(y.get(i).toString());
        }
        return getPearsonCorrelationScore(xData, yData);
    }

    /**
     * 计算2个数组的皮尔逊相关系数
     *
     * @param xData
     * @param yData
     * @return
     */
    public static double getPearsonCorrelationScore(double[] xData, double[] yData) {
        if (xData.length != yData.length) {
            return -2;
        }
        double xMeans;
        double yMeans;
        double numerator = 0; // 求解皮尔逊的分子
        double denominator = 0; // 求解皮尔逊系数的分母

        double result = 0;
        // 拿到两个数据的平均值
        xMeans = getMeans(xData);
        yMeans = getMeans(yData);
        // 计算皮尔逊系数的分子
        numerator = generateNumerator(xData, xMeans, yData, yMeans);
        // 计算皮尔逊系数的分母
        denominator = generateDenomiator(xData, xMeans, yData, yMeans);
        // 计算皮尔逊系数
        result = numerator / denominator;
        return result;
    }

    /**
     * 计算分子
     *
     * @param xData
     * @param xMeans
     * @param yData
     * @param yMeans
     * @return
     */
    private static double generateNumerator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double numerator = 0.0;
        for (int i = 0; i < xData.length; i++) {
            numerator += (xData[i] - xMeans) * (yData[i] - yMeans);
        }
        return numerator;
    }

    /**
     * 计算分母
     *
     * @param yMeans
     * @param yData
     * @param xMeans
     * @param xData
     * @return 分母
     */
    private static double generateDenomiator(double[] xData, double xMeans, double[] yData, double yMeans) {
        double xSum = 0.0;
        for (int i = 0; i < xData.length; i++) {
            xSum += (xData[i] - xMeans) * (xData[i] - xMeans);
        }
        double ySum = 0.0;
        for (int i = 0; i < yData.length; i++) {
            ySum += (yData[i] - yMeans) * (yData[i] - yMeans);
        }
        return Math.sqrt(xSum) * Math.sqrt(ySum);
    }

    /**
     * 计算数组平均值
     *
     * @param datas 数据集
     * @return 给定数据集的平均值
     */
    private static double getMeans(double[] datas) {
        double sum = 0.0;
        for (int i = 0; i < datas.length; i++) {
            sum += datas[i];
        }
        return sum / datas.length;
    }

//    public static void main(String[] args) {
//        double[] x = new double[]{0.98, 0.96, 0.96, 0.94, 0.925, 0.9025, 0.875};
//        double[] y = new double[]{1, 1, 1, 1, 0.961483893, 0.490591662, 0.837341784};
//        double score = getPearsonCorrelationScore(x, y);
//        System.out.println(score); // 0.6350393282549671
//
//        List lx = Arrays.asList(1,2,3);
//        List ly = Arrays.asList(1,2,3);
//        double score2 = getPearsonCorrelationScore(lx, ly);
//        System.out.println(score2); // 0.9999999999999998
//    }
}
