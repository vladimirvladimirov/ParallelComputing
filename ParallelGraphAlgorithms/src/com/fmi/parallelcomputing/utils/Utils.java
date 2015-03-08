package com.fmi.parallelcomputing.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Utility methods for the whole project.
 */
public class Utils {

    private static final Random rand;

    static {
        rand = new Random();
    }

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

        if (indexesToCheck % threadCount > 0) {
            divisionSize++;
        }

        int currBegin = 0;
        for(int i=0; i<indexesToCheck % threadCount; i++) {
            partitions.add(new Interval(currBegin, currBegin + divisionSize - 1));
            currBegin += divisionSize;
        }

        if (indexesToCheck % threadCount > 0) {
            divisionSize--;
        }
        for(int i=partitions.size(); i<threadCount; i++) {
            partitions.add(new Interval(currBegin, currBegin + divisionSize - 1));
            currBegin += divisionSize;
        }

        return partitions;
    }

    public static List<Interval> divideLoad(int indexesToCheck, int threadCount, int startingIndex) {
        List<Interval> partitions = divideLoad(indexesToCheck,threadCount);
        for(Interval interval : partitions) {
            interval.setA(interval.getA() + startingIndex);
            interval.setB(interval.getB() + startingIndex);
        }
        return partitions;
    }

    public static int getIntInRange(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException(String.format(ExceptionsMessages.ILLEGAL_INTERVAL_FORMAT,from,to));
        }
        int gen = rand.nextInt(to - from + 1) + from;
        return gen;
    }
}
