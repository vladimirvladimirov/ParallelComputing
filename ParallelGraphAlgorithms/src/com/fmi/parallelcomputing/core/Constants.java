package com.fmi.parallelcomputing.core;

/**
 */
public class Constants {

    public static final int MINIMAL_EDGE_COST = -100;

    public static final int MAXIMAL_EDGE_COST = 100;

    public static final int DEFAULT_NODES_NUMBER = 10;

    public static final int DEFAULT_MAXIMAL_NODE_DEGREE = DEFAULT_NODES_NUMBER - 1; // for full graphs

    public static final int DEFAULT_THREAD_COUNT = 1;

    public static final int GRAPH_FILENAME_INDEX = 1;
    public static final String GRAPH_INPUT_FILENAME = String.format("graph%d.in", GRAPH_FILENAME_INDEX);
    public static final String GRAPH_SOLUTION_FILENAME = String.format("graph%d.sol", GRAPH_FILENAME_INDEX);

    public static final int PARTITION_FILENAME_INDEX = 3;
    public static final String PARTITION_INPUT_FILENAME = String.format("partition%d.in", PARTITION_FILENAME_INDEX);
    public static final String PARTITION_SOLUTION_FILENAME = String.format("partition%d.sol", PARTITION_FILENAME_INDEX);
    public static final int MAX_INT = 0x7fffffff;

    /**
     * Constants, used for generating graphs.
     */
    public enum GraphConstant {
        GRAPH_MAX_EDGE, GRAPH_MIN_EDGE, GRAPH_MAX_NODE_DEGREE, GRAPH_NODE_NUMBER
    }

    public enum PartitionProblemConstants {
        NUMBERS_COUNT, MIN_NUMBER, MAX_NUMBER
    }
}
