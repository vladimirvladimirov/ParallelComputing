package com.fmi.parallelcomputing.core.solutions.TSP;

import com.fmi.parallelcomputing.core.Constants;
import com.fmi.parallelcomputing.core.solutions.AbstractSolver;
import com.fmi.parallelcomputing.graph.NeighbourMatrixIndirectedGraph;
import com.fmi.parallelcomputing.utils.Interval;
import com.fmi.parallelcomputing.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Solves the Traveling Salesman Problem using dynamic programming. Complexity of algorithm: O(2^N * N^2).
 * It can use multiple threads in order to reduce the running time.
 */
public class DynamicProgrammingSolution extends AbstractSolver {


    private final Object lock = new Lock();
    // number of nodes in graph
    int n;
    // state: [first][last][bitmask]
    private int[][][] dp;
    // set with thread-workers
    private CopyOnWriteArrayList<Worker> workers;

    public DynamicProgrammingSolution(int threadCount, String inputFilename) throws IOException {
        super(threadCount, inputFilename);
        solver = this;
    }

    public void readInput() throws IOException {
        boolean isZeroBased = true;
        n = in.getNextInt();
        int numberOfEdges = in.getNextInt();

        graph = new NeighbourMatrixIndirectedGraph(n, isZeroBased);
        for (int i = 1; i <= numberOfEdges; i++) {
            int from = in.getNextInt();
            int to = in.getNextInt();
            int cost = in.getNextInt();

            graph.addEdge(from, to, cost);
        }

        initializeDp();
    }

    private void initializeDp() {
        dp = new int[n][][];
        for (int i = 0; i < n; i++) {
            dp[i] = new int[n][];
            for (int j = 0; j < n; j++) {
                dp[i][j] = new int[1 << n];
                for (int k = 0; k < (1 << n); k++)
                    dp[i][j][k] = 0x0fffffff;
            }
        }
    }

    @Override
    public void solve() throws Exception {
        workers = new CopyOnWriteArrayList<Worker>();
        // handle states with only 1 element
        for (int i = 0; i < n; i++)
            dp[i][i][1 << i] = 0;

        for (int firstNode = 0; firstNode < n; firstNode++) {
            for (int secondNode = 0; secondNode < n; secondNode++) {
                if (firstNode != secondNode && graph.hasEdge(firstNode, secondNode)) {
                    dp[firstNode][secondNode][(1 << firstNode) + (1 << secondNode)] = graph.distance(firstNode, secondNode);
                }
            }
        }

        for (int numberOfNodes = 3; numberOfNodes <= n; numberOfNodes++) {
            List<Interval> subindexes = Utils.divideLoad(1 << n, threadCount);
            for (Interval partition : subindexes) {
                Worker workerThread = new Worker(this, partition, numberOfNodes);
                workers.add(workerThread);
            }

            for (Worker aWorker : workers) {
                aWorker.start();
            }

            synchronized (lock) {
                while (!workers.isEmpty()) {
                    lock.wait();
                }
            }
        }

        int bestAns = 0x7fffffff;
        for (int firstNode = 0; firstNode < n; firstNode++) {
            for (int secondNode = 0; secondNode < n; secondNode++) {
                if (firstNode != secondNode) {
                    int currAns = dp[firstNode][secondNode][(1 << n) - 1] + graph.distance(secondNode, firstNode);
                    bestAns = Math.min(bestAns, currAns);
                }
            }
        }

        answer = bestAns;
    }

    public static void main(String [] args) throws Exception {
        new DynamicProgrammingSolution(32, Constants.INPUT_FILENAME);
        AbstractSolver.main(args);
    }

    private static final class Lock {
    }

    /**
     * Used to make computations in another thread.
     */
    class Worker extends Thread {

        DynamicProgrammingSolution solver;

        int fromState;

        int toState;

        int bits;

        public Worker(DynamicProgrammingSolution solver, Interval indexesToCheck, int clusterSize) {
            fromState = indexesToCheck.getA();
            toState = indexesToCheck.getB();
            this.solver = solver;
            bits = clusterSize;
        }

        @Override
        public synchronized void run() {
            int newBitmask;
            for (int currentState = fromState; currentState <= toState; currentState++) {
                if (bitcount(currentState) == bits - 1) {
                    for (int firstNode = 0; firstNode < n; firstNode++) {
                        if (((currentState >> firstNode) & 1) == 1) {
                            for (int lastNode = 0; lastNode < n; lastNode++) {
                                if (lastNode != firstNode && ((currentState >> lastNode) & 1) == 1) {
                                    // we have a chain [firstnode][lastnode][cluster] and we must make it bigger
                                    for (int nextNode = 0; nextNode < n; nextNode++) {
                                        if (((currentState >> nextNode) & 1) == 0) {
                                            newBitmask = currentState | (1 << nextNode);
                                            dp[firstNode][nextNode][newBitmask] = Math.min(dp[firstNode][nextNode][newBitmask],
                                                    dp[firstNode][lastNode][currentState] + graph.distance(lastNode, nextNode));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            synchronized (solver.lock) {
                solver.workers.remove(this);
                if (solver.workers.isEmpty()) {
                    lock.notifyAll();
                }
            }
        }

        int bitcount(int number) {
            int ans = 0;
            for (int i = 0; i <= n; i++) {
                ans += (number >> i) & 1;
            }
            return ans;
        }
    }

}
