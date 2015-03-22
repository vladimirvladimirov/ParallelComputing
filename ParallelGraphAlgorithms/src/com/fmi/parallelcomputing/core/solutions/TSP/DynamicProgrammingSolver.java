package com.fmi.parallelcomputing.core.solutions.TSP;

import com.fmi.parallelcomputing.core.solutions.AbstractSolver;
import com.fmi.parallelcomputing.utils.Interval;
import com.fmi.parallelcomputing.utils.Utils;
import com.fmi.parallelcomputing.utils.graph.Graph;
import com.fmi.parallelcomputing.utils.graph.NeighbourMatrixIndirectedGraph;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Solves the Traveling Salesman Problem using dynamic programming. Complexity of algorithm: O(2^N * N^2).
 * It can use multiple threads in order to reduce the running time.
 */
public class DynamicProgrammingSolver extends AbstractSolver {

    // input container
    protected Graph graph;
    // number of nodes in graph
    int n;
    // state: [first][last][bitmask]
    private int[][][] dp;
    // set with thread-workers
    private CopyOnWriteArrayList<Worker> workers;

    public DynamicProgrammingSolver(int threadCount, String inputFilename) throws IOException {
        super(threadCount, inputFilename);
        solver = this;
    }

    public static void main(String[] args) throws Exception {
        new DynamicProgrammingSolver(8, com.fmi.parallelcomputing.core.Constants.GRAPH_INPUT_FILENAME);
        AbstractSolver.main(args);
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
        dp = new int[n][n][(1 << n)];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < 1 << n; k++)
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

    @Override
    public void logResults() {
        out.println(String.format("%fs", (timestampAlgorithmEnd.getTime() - timestampAlgorithmBegin.getTime()) / 1000.0));
        out.println(String.format("Minimal path has length %d", answer));
        out.flush();
    }

    /**
     * Used to make computations in another thread.
     */
    class Worker extends Thread {

        DynamicProgrammingSolver solver;

        int fromState;

        int toState;

        int bits;

        public Worker(DynamicProgrammingSolver solver, Interval indexesToCheck, int clusterSize) {
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
