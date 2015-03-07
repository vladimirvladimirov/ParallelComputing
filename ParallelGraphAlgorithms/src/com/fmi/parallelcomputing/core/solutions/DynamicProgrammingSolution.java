package com.fmi.parallelcomputing.core.solutions;

import com.fmi.parallelcomputing.graph.NeighbourMatrixIndirectedGraph;
import com.fmi.parallelcomputing.utils.Interval;
import com.fmi.parallelcomputing.utils.Utils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Dev on 3/7/2015.
 */
public class DynamicProgrammingSolution extends AbstractSolver {


    private static final class Lock { }
    private final Object lock = new Lock();
    Boolean isWakeupNeeded;

    // number of nodes in graph
    int n;
    // state: [first][last][bitmask]
    private int[][][] dp;
    // map with thread-workers
    List<Worker> workers;

    public DynamicProgrammingSolution(int threadCount, String inputFilename) throws IOException {
        super(threadCount, inputFilename);
//        workers = new Worker[threadCount];
    }

    public void readGraph() throws IOException {
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
                for(int k=0; k<(1<<n); k++)
                    dp[i][j][k] = 0x0fffffff;
            }
        }
    }

    @Override
    public void solve() throws Exception {
        workers = new LinkedList<Worker>();
        // handle states with only 1 element
        for(int i=0; i<n; i++)
                dp[i][i][1<<i] = 0;

        for(int firstNode = 0; firstNode < n; firstNode++) {
            for(int secondNode = 0; secondNode < n; secondNode++) {
                if (firstNode != secondNode && graph.hasEdge(firstNode,secondNode)) {
                    dp[firstNode][secondNode][(1<<firstNode) + (1<<secondNode)] = graph.distance(firstNode,secondNode);
                }
            }
        }

        for(int numberOfNodes = 3; numberOfNodes <= n; numberOfNodes++) {
            List<Interval> subindexes = Utils.divideLoad(1 << n, threadCount);
            for(Interval partition : subindexes) {
                Worker workerThread = new Worker(this, partition, numberOfNodes);
                workers.add(workerThread);
            }

            for(Worker aWorker : workers) {
                aWorker.start();
            }

            isWakeupNeeded = false;
            synchronized (lock) {
                while (!isWakeupNeeded) {
                    lock.wait();
                }
            }
//            synchronized (this) {
//                this.wait();
//            }
        }

        int bestAns = 0x7fffffff;
        for(int firstNode = 0; firstNode < n; firstNode++) {
            for(int secondNode = 0; secondNode < n; secondNode++) {
                if (firstNode != secondNode) {
                    int currAns = dp[firstNode][secondNode][(1<<n)-1] + graph.distance(secondNode,firstNode);
                    bestAns = Math.min(bestAns, currAns);
                }
            }
        }

        answer = bestAns;
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
            // super.start(); // TODO remove this?
            int newBitmask;
            for(int currentState=fromState; currentState<=toState; currentState++) {
                if (bitcount(currentState)==bits - 1) {
                    for(int firstNode = 0; firstNode < n; firstNode++) {
                        if (((currentState>>firstNode) & 1)==1) {
                            for(int lastNode = 0; lastNode < n; lastNode++) {
                                if (lastNode != firstNode && ((currentState>>lastNode) & 1)==1) {
                                    // we have a chain [firstnode][lastnode][cluster] and we must make it bigger
                                    for(int nextNode = 0; nextNode < n; nextNode++) {
                                        if (((currentState >> nextNode)&1)==0) {
                                            newBitmask = currentState | (1 << nextNode);
                                            dp[firstNode][nextNode][newBitmask] = dp[firstNode][lastNode][currentState] + graph.distance(lastNode, nextNode);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

//            synchronized (solver) {
//                    solver.workers.remove(this);
//                    if (solver.workers.isEmpty()) {
//                        solver.notify();
//                    }
//            }
            synchronized (solver.lock) {
                synchronized (solver.workers) {
                    solver.workers.remove(this);
                    if (solver.workers.isEmpty()) {
                        synchronized (solver.isWakeupNeeded) {
                            solver.isWakeupNeeded = true;
                            lock.notifyAll();
                        }
                    }
                }
            }
        }

        int bitcount(int number) {
            int ans = 0;
            for(int i=0; i<=n; i++) {
                ans += (number >> i)&1;
            }
            return ans;
        }
    }

}
