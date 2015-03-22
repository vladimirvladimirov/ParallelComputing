package com.fmi.parallelcomputing.core.benchmark;

/**
 * Created by vivanovv on 3/10/2015.
 */
public class Constants {

    public static final int PARTITION_FILENAME_INDEX = 3;
    public static final int TRAVELING_SALESMAN_FILENAME_INDEX = 1;

    public static final String PARTITION_INPUT_FILENAME = String.format("partition%d.in", PARTITION_FILENAME_INDEX);
    public static final String TRAVELING_SALESMAN_INPUT_FILENAME = String.format("salesman%d.in", TRAVELING_SALESMAN_FILENAME_INDEX);

    public static final String PARTITION_PROBLEM_LOG_FILE_NAME_FORMAT = "PartitionProblem_%tB_%te_%tl_%tM_%tp.log";
    public static final String TRAVELING_SALESMAN_LOG_FILE_NAME_FORMAT = "TravelingSalesman_%tB_%te_%tl_%tM_%tp.log";
}
