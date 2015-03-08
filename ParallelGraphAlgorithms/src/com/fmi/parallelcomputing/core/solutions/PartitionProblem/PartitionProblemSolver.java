package com.fmi.parallelcomputing.core.solutions.PartitionProblem;

import com.fmi.parallelcomputing.core.solutions.AbstractSolver;
import com.fmi.parallelcomputing.utils.Interval;
import com.fmi.parallelcomputing.utils.Utils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class finds a solution ( if such exists ) of the partition problem where one must divide a set of integers
 * into two subsets with equals sums or to show this is not possible.
 */
public class PartitionProblemSolver extends AbstractSolver {

    // size of input set
    protected int n;

    // set with integers for division into subsets
    protected List<Integer> set;

    protected CopyOnWriteArrayList<Worker> workers;

    private ConcurrentSkipListSet<Integer> allSolutions;

    protected PartitionProblemSolver(int threadCount, String inputFilename) throws IOException {
        super(threadCount, inputFilename);
        workers = new CopyOnWriteArrayList<Worker>();
        allSolutions = new ConcurrentSkipListSet<Integer>();
        super.solver = this;
    }

    @Override
    public void readInput() throws IOException {
        n = in.getNextInt();
        set = new LinkedList<Integer>();

        for(int i=0; i<n; i++) {
            set.add(in.getNextInt());
        }
    }

    @Override
    public void solve() throws Exception {
        List<Interval> dividedLoad = Utils.divideLoad((1<<n)-1, threadCount, 1);

        for(int i=0; i<dividedLoad.size(); i++) {
            Worker worker = new Worker(this, dividedLoad.get(i));
            workers.add(worker);
            worker.start();
        }

        synchronized (lock) {
            while (!workers.isEmpty()) {
                lock.wait(0);
            }
        }
    }

    @Override
    public void logResults() {
        out.println(String.format("%fs", (timestampAlgorithmEnd.getTime() - timestampAlgorithmBegin.getTime()) / 1000.0));
        out.println(String.format("Found %d solutions", allSolutions.size()));
        out.flush();
//        for(Integer solution : allSolutions) {
//            for(int i=0; i<n; i++) {
//                if (((solution >> i) & 1) == 1) {
//                    System.out.print(String.format("%d ",set.get(i)));
//                }
//            }
//            System.out.println();
//        }
    }

    public static void main(String[] args) throws Exception {
        new PartitionProblemSolver(4, com.fmi.parallelcomputing.core.Constants.PARTITION_INPUT_FILENAME);
        AbstractSolver.main(args);
    }

    class Worker extends Thread {

        PartitionProblemSolver solver;

        Interval load;

        public Worker(PartitionProblemSolver partitionProblemSolver, Interval interval) {
            solver = partitionProblemSolver;
            load = interval;
        }

        @Override
        public void run() {
            for(int bitmask=load.getA(); bitmask <= load.getB(); bitmask++) {
                int sums[] = {0,0};
                for(int bitPosition=0; bitPosition < n; bitPosition++) {
                    int bitValue = (bitmask >> bitPosition) & 1;
                    sums[bitValue] += solver.set.get(bitPosition);
                }
                if (sums[0] == sums[1]) {
                    solver.allSolutions.add(bitmask);
                }
            }

            synchronized (solver.lock) {
                solver.workers.remove(this);
                if (solver.workers.isEmpty()) {
                    lock.notifyAll();
                }
            }
        }
    }
}
