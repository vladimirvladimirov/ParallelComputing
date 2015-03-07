package com.fmi.parallelcomputing.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dev on 3/7/2015.
 */
public class Utils {
    public static boolean isPowerOf2(int threadCount) {
        if (threadCount < 0) return false;

        while (threadCount % 2 == 0) {
            threadCount /= 2;
        }

        return (threadCount == 1);
    }

    public static List<Interval> divideLoad(int indexesToCheck, int threadCount) {
        List<Interval> partitions = new LinkedList<Interval>();
        int divisionSize = indexesToCheck / threadCount;

        for(int i=0; i<indexesToCheck; i+= divisionSize) {
            partitions.add(new Interval(i,i+divisionSize-1));
        }

        return partitions;
    }
}
