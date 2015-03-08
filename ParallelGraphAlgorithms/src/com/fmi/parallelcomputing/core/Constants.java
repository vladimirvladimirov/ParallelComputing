package com.fmi.parallelcomputing.core;

/**
 * Common constants for all files in the core subpackages.
 */
public class Constants {
    public static final int GRAPH_FILENAME_INDEX = 1;
    public static final String GRAPH_SOLUTION_FILENAME = String.format("graph%d.sol", GRAPH_FILENAME_INDEX);
    public static final String GRAPH_INPUT_FILENAME = String.format("graph%d.in", GRAPH_FILENAME_INDEX);
    public static final int PARTITION_FILENAME_INDEX = 3;
    public static final String PARTITION_INPUT_FILENAME = String.format("partition%d.in", PARTITION_FILENAME_INDEX);
}
