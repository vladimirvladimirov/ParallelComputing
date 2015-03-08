package com.fmi.parallelcomputing.core.solutions.TSP;

import com.fmi.parallelcomputing.core.Constants;
import com.fmi.parallelcomputing.core.solutions.AbstractSolver;

import java.io.IOException;

/**
 * This class provides functionality to solve the TSP by using bruteforce.
 */
public class BruteforceSolver extends DynamicProgrammingSolver {

    private int answer = 0x7fffffff;

    public BruteforceSolver(int threadCount, String inputFilename) throws IOException {
        super(threadCount, inputFilename);
    }

    @Override
    public void solve() throws Exception {

        int [] used = new int[n];
        int [] perm = new int[n];
        for(int i=0; i<n; i++){
            perm[i] = i;
        }

        generateAllPerms(perm, used, 0);

        super.answer = this.answer;
    }

    @Override
    public void logResults() {
        out.println(String.format("%fs", (timestampAlgorithmEnd.getTime() - timestampAlgorithmBegin.getTime()) / 1000.0));
        out.println(String.format("Minimal path has length %d",answer));
        out.flush();
    }

    private void generateAllPerms(int [] perm, int [] used, int currIndex) {
        if (currIndex >= n) {
            int currSol = calculateDistance(perm);
            answer = Math.min(answer, currSol);
            return;
        }

        for(int i=0; i<n; i++) {
            if (used[i] == 0) {
                perm[currIndex] = i;
                used[i] = 1;
                generateAllPerms(perm,used,currIndex+1);
                used[i]=0;
            }
        }
    }

    private int calculateDistance(int[] perm) {
        int ans = 0;

        for(int i=1; i<=n; i++) {
            ans += graph.distance(perm[i%n], perm[i-1]);
        }

        return ans;
    }

    public static void main(String [] args) throws Exception {
        new BruteforceSolver(32, Constants.GRAPH_INPUT_FILENAME);
        AbstractSolver.main(args);
    }
}
