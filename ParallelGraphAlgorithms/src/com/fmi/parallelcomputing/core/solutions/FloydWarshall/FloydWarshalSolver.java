package com.fmi.parallelcomputing.core.solutions.FloydWarshall;

import com.fmi.parallelcomputing.core.Constants;
import com.fmi.parallelcomputing.core.solutions.AbstractSolver;
import com.fmi.parallelcomputing.utils.Interval;
import com.fmi.parallelcomputing.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is implementation of parallel computing of the Floyd-Warshall algorithm for finding all shortest
 * paths between all pairs in graph, represented by its edge weight matrix. I don't use explicit {@link com.fmi.parallelcomputing.graph.Graph}
 * structure here for optimization purposes.
 */
public class FloydWarshalSolver extends AbstractSolver {

    private static final int INF = 0x7ffffff0 / 2;

    /**
     * Number of nodes in graph
     */
    private int n;
    /**
     * Matrix with edge weights
     */
    private int [][] graph;

    private CopyOnWriteArrayList<ColumnWorker> workers;

    private final Lock lock = new Lock();

    public FloydWarshalSolver(int threadCount, String inputFilename) throws IOException {
        super(threadCount, inputFilename);
        solver = this;
    }

    @Override
    public void readInput() throws IOException {
        n = in.getNextInt();
        graph = new int[n][n];

        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                graph[i][j] = in.getNextInt();
                if (graph[i][j]==0) graph[i][j] = INF;
            }
        }
    }

    @Override
    public void solve() throws Exception {
        workers = new CopyOnWriteArrayList<ColumnWorker>();

        for(int k=0; k<n; k++) {
            List<Interval> workDivision = Utils.divideLoad(n, threadCount);
            for(Interval interval : workDivision) {
                ColumnWorker worker = new ColumnWorker(this, interval, k);
                workers.add(worker);
                worker.start();
            }
        }

        synchronized (lock) {
            while (!workers.isEmpty()) {
                lock.wait(0);
            }
        }
    }

    @Override
    public void logResults() {

//        StringBuilder output = new StringBuilder();
//        for(int i=0; i<n; i++) {
//            graph[i][i] = 0;
//            for(int j=0; j<n; j++) {
//                if (graph[i][j] == INF) graph[i][j] = 0;
//                output.append(graph[i][j]);
//                output.append(' ');
//            }
//            output.append('\n');
//        }
//
//        out.print(output);

        out.println(String.format("%fs", (timestampAlgorithmEnd.getTime() - timestampAlgorithmBegin.getTime()) / 1000.0));
        out.flush();

        out.flush();
        out.close();
    }

    public static void main(String [] args) throws Exception {
        new FloydWarshalSolver(4, Constants.INPUT_FILENAME);
        AbstractSolver.main(args);
    }

    private static final class Lock {}

    /**
     * This class should be used when parallelizing only on one dimention - in this case it's columns, but it might as well be a row parallelization
     */
    class ColumnWorker extends Thread {

        int startColumn;

        int endColumn;

        FloydWarshalSolver solver;

        int middlePoint;

        public ColumnWorker(FloydWarshalSolver solver, Interval load, int currentMiddleNode) {
            this.solver = solver;
            startColumn = load.getA();
            endColumn = load.getB();
            middlePoint = currentMiddleNode;
        }


        @Override
        public void run() {
            for(int currNode = startColumn; currNode <= endColumn; currNode++) {
                for(int nextNode=0; nextNode<n; nextNode++) {
                    if (graph[currNode][middlePoint] + graph[middlePoint][nextNode] < graph[currNode][nextNode]) {
                        graph[currNode][nextNode] = graph[currNode][middlePoint] + graph[middlePoint][nextNode];
                    }
                }
            }

            solver.workers.remove(this);
            if (solver.workers.isEmpty()) {
                synchronized (solver.lock) {
                    lock.notifyAll();
                }
            }
        }
    }
}
