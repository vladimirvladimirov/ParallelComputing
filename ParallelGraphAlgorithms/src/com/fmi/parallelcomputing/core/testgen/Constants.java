package com.fmi.parallelcomputing.core.testgen;

/**
 */
public class Constants {

    public static final int MINIMAL_EDGE_COST = -100;

    public static final int MAXIMAL_EDGE_COST = 100;

    public static final int DEFAULT_NODES_NUMBER = 10;

    public static final int DEFAULT_MAXIMAL_NODE_DEGREE = DEFAULT_NODES_NUMBER - 1; // for full graphs

    /**
     * Constants, used for generating graphs.
     */
    public enum GraphConstant {
        GRAPH_MAX_EDGE, GRAPH_MIN_EDGE, GRAPH_MAX_NODE_DEGREE, GRAPH_NODE_NUMBER
    }

    /**
     * Constants, used for generating lists of integers as input data for the Partition problem.
     */
    public enum PartitionProblemConstants {
        INPUT_SET_SIZE, MIN_NUMBER, MAX_NUMBER
    }
}
