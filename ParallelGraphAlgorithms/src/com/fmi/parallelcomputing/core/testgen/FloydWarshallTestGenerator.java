package com.fmi.parallelcomputing.core.testgen;

import com.fmi.parallelcomputing.core.Constants;
import com.fmi.parallelcomputing.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a generator for test cases for the Floyd-Warshal distributed algorithm. Its output consists of
 * number of nodes in the graph and its representation as a matrix of the edges weights. Node indexing starts from 0.
 */
public class FloydWarshallTestGenerator {

    private PrintWriter graphOut;

    private Map<Constants.GraphConstant, Integer> graphProperties;

    private int[][] graphMatrix;

    private int nodesNumber;

    public FloydWarshallTestGenerator(Map<Constants.GraphConstant, Integer> graphProperties, String inputFilename) throws FileNotFoundException {
        graphOut = new PrintWriter(new FileOutputStream(inputFilename));
        this.graphProperties = graphProperties;
    }

    public static void main(String[] args) throws FileNotFoundException {

        Map<Constants.GraphConstant, Integer> inputGraphProperties = new HashMap<Constants.GraphConstant, Integer>();
        inputGraphProperties.put(Constants.GraphConstant.GRAPH_MIN_EDGE, -1000);
        inputGraphProperties.put(Constants.GraphConstant.GRAPH_MAX_EDGE, 10000);
        inputGraphProperties.put(Constants.GraphConstant.GRAPH_NODE_NUMBER, 2*5120);

        FloydWarshallTestGenerator generator = new FloydWarshallTestGenerator(inputGraphProperties, Constants.INPUT_FILENAME);
        generator.createGraphMatrix();
        generator.printGraph();
    }

    private void printGraph() {

        StringBuilder output = new StringBuilder();
        output.append(nodesNumber);
        output.append('\n');
        for(int i=0; i<nodesNumber; i++) {
            for(int j=0; j<nodesNumber; j++) {
                output.append(graphMatrix[i][j]);
                output.append(' ');
            }
            output.append('\n');
        }

        graphOut.print(output.toString());
        graphOut.flush();
        graphOut.close();
    }

    private void createGraphMatrix() {
        int minWeight = graphProperties.get(Constants.GraphConstant.GRAPH_MIN_EDGE);
        int maxWeight = graphProperties.get(Constants.GraphConstant.GRAPH_MAX_EDGE);

        nodesNumber = graphProperties.get(Constants.GraphConstant.GRAPH_NODE_NUMBER);
        graphMatrix = new int[nodesNumber][];

        for(int i=0; i<nodesNumber; i++) {
            graphMatrix[i] = new int[nodesNumber];
            for(int j=0; j<nodesNumber; j++) {
                graphMatrix[i][j] = Utils.getIntInRange(minWeight, maxWeight);
            }
            graphMatrix[i][i] = 0;
        }
    }
}
