package com.fmi.parallelcomputing.core.solutions;

import com.fmi.parallelcomputing.utils.FileReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Model for solutions for various number of tasks, related with graphs, such as the traveling salesman problem
 * or algorithms for path searching
 */
public abstract class AbstractSolver {

    /**
     * Actual instance of the class that finds the solution.
     */
    protected static AbstractSolver solver;

    // Fields in the solver

    protected PrintWriter out;

    protected FileReader in;

    protected int threadCount;

    protected Date timestampAlgorithmBegin;

    protected Date timestampAlgorithmEnd;

    protected int answer;

    protected final Object lock = new Lock();

    protected AbstractSolver(int threadCount, String inputFilename) throws IOException {
//        if (!Utils.isPowerOf2(threadCount)) {
//            throw new IllegalArgumentException(String.format(ExceptionsMessages.ILLEGAL_THREAD_COUNT_FORMAT,threadCount));
//        }
        this.threadCount = threadCount;
        in = new FileReader(inputFilename);
        out = new PrintWriter(System.out);
    }

    public abstract void readInput() throws IOException;

    public abstract void solve() throws Exception;

    public abstract void logResults();

    protected void endTimer() {
        timestampAlgorithmEnd = new Date();
    }

    protected void startTimer() {
        timestampAlgorithmBegin = new Date();
    }


    public static void main(String [] args) throws Exception {
        solver.readInput();
        solver.startTimer();
        solver.solve();
        solver.endTimer();
        solver.logResults();
    }

    private static final class Lock {
    }
}
