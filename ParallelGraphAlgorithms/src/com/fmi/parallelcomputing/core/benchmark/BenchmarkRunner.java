package com.fmi.parallelcomputing.core.benchmark;

import com.fmi.parallelcomputing.core.solutions.AbstractSolver;
import com.fmi.parallelcomputing.core.solutions.PartitionProblem.PartitionProblemSolver;
import com.fmi.parallelcomputing.core.solutions.TSP.DynamicProgrammingSolver;
import com.fmi.parallelcomputing.utils.FileReader;
import com.fmi.parallelcomputing.utils.exceptions.ExceptionsMessages;
import com.fmi.parallelcomputing.utils.exceptions.IllegalOperationException;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;


/**
 * Due to the big difference in execution time when running a solver multiple times on the same input data set,
 * this class introduces a general approach of finding the average time the program would take.
 *
 * The used method here is running the given problem solver multiple times with the same input data and
 * calculating the average time from all the runs.
 */
public class BenchmarkRunner {

    public double findAverageRunningTime(int threadCount, String filename, int numberOfExecutions) throws Exception {


        if (numberOfExecutions < 0) {
            throw new IllegalArgumentException(String.format(ExceptionsMessages.ILLEGAL_RUN_NUMBER,numberOfExecutions));
        }

        double totalExecutionTime = 0.0;

        for(int i=0; i<numberOfExecutions; i++) {
//            AbstractSolver solver = new PartitionProblemSolver(threadCount, filename);
            AbstractSolver solver = new DynamicProgrammingSolver(threadCount, filename);
            solver.run();
            totalExecutionTime += solver.getRunningTimeInSeconds();
        }

        return totalExecutionTime / numberOfExecutions;
    }

    public static void runPartitionProblem() throws Exception {
        BenchmarkRunner resultLogger = new BenchmarkRunner();
        List<Integer> threadNumberSet = Arrays.asList(1,2,3,4,6,8,10,12,16,24,32,48,64,92,128,256);
        int numberOfTestRuns = 3;
        String inputFilename = Constants.PARTITION_INPUT_FILENAME;

        Map<Integer, Double> threadCountToAverageTime = new HashMap<Integer, Double>();
        for(Integer threadCount : threadNumberSet) {
            double averageTime = resultLogger.findAverageRunningTime(threadCount, inputFilename, numberOfTestRuns);
            threadCountToAverageTime.put(threadCount, averageTime);
        }

        Calendar c = Calendar.getInstance();
        String logFilename = String.format(Constants.PARTITION_PROBLEM_LOG_FILE_NAME_FORMAT,c,c,c,c,c,c);
        File logFile = new File((logFilename));
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        else {
            throw new IllegalOperationException("Cannot create log file; wait 1 minute.");
        }
        PrintWriter out = new PrintWriter(new FileOutputStream(logFile));
        out.println(String.format("Number of runs for each test case: %d",numberOfTestRuns));
        out.println("-----------------------------------------");
        out.println(String.format("%-10s%-10s","Threads","Average execution time"));

        for(Integer threadCount : threadNumberSet) {
            out.println(String.format("%-10d%-10.5f",threadCount,threadCountToAverageTime.get(threadCount)));
        }
        out.flush();
        out.close();
    }

    public static void runTravelingSalesmanProblem() throws Exception {
        BenchmarkRunner resultLogger = new BenchmarkRunner();
        List<Integer> threadNumberSet = Arrays.asList(1,2,3,4,6,8,10,12,16,24,32,48,64,92,128,256);
        int numberOfTestRuns = 3;
        String inputFilename = Constants.TRAVELING_SALESMAN_INPUT_FILENAME;

        Map<Integer, Double> threadCountToAverageTime = new HashMap<Integer, Double>();
        for(Integer threadCount : threadNumberSet) {
            double averageTime = resultLogger.findAverageRunningTime(threadCount, inputFilename, numberOfTestRuns);
            threadCountToAverageTime.put(threadCount, averageTime);
        }

        Calendar c = Calendar.getInstance();
        String logFilename = String.format(Constants.TRAVELING_SALESMAN_LOG_FILE_NAME_FORMAT,c,c,c,c,c,c);
        File logFile = new File((logFilename));
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        else {
            throw new IllegalOperationException("Cannot create log file; wait 1 minute.");
        }
        PrintWriter out = new PrintWriter(new FileOutputStream(logFile));
        out.println(String.format("Number of runs for each test case: %d",numberOfTestRuns));
        out.println(String.format("Graph size: %d", (new FileReader(inputFilename)).getNextInt()));
        out.println("-----------------------------------------");
        out.println(String.format("%-10s%-10s","Threads","Average execution time"));

        for(Integer threadCount : threadNumberSet) {
            out.println(String.format("%-10d%-10.5f",threadCount,threadCountToAverageTime.get(threadCount)));
        }
        out.flush();
        out.close();
    }

    public static void main(String [] args) throws Exception {
//        runPartitionProblem();
        runTravelingSalesmanProblem();
    }

}
