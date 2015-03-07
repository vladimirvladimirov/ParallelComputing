package com.fmi.parallelcomputing.core;

/**
 * Created by Dev on 3/6/2015.
 */
public class Constants {

    public static final int MINIMAL_EDGE_COST = -100;

    public static final int MAXIMAL_EDGE_COST = 100;

    public static final int DEFAULT_NODES_NUMBER = 10;

    public static final int DEFAULT_MAXIMAL_NODE_DEGREE = DEFAULT_NODES_NUMBER - 1; // for full graphs

    public static final int DEFAULT_THREAD_COUNT = 1;

    public static final int FILENAME_INDEX = 5;
    public static final String INPUT_FILENAME = String.format("test%d.txt", FILENAME_INDEX);
    public static final String SOLUTION_FILENAME = String.format("test%d.sol", FILENAME_INDEX);

    public enum GraphConstant {
        GRAPH_MAX_EDGE, GRAPH_MIN_EDGE, GRAPH_MAX_NODE_DEGREE, GRAPH_NODE_NUMBER;
    }
}
