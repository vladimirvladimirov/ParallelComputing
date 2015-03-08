package com.fmi.parallelcomputing.core.benchmark;

/**
 * Due to the big difference in execution time when running a solver multiple times on the same input data set,
 * this class introduces a general approach of finding the average time the program would take.
 *
 * The used method here is running the given problem solver multiple times with the same input data and
 * calculating the average time from all the runs.
 */
public class AbstractBenchmarkRunner {
}
