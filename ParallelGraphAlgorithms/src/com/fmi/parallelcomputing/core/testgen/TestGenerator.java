package com.fmi.parallelcomputing.core.testgen;

import com.fmi.parallelcomputing.core.Constants;
import com.fmi.parallelcomputing.core.Constants.GraphConstant;
import com.fmi.parallelcomputing.graph.Graph;
import com.fmi.parallelcomputing.graph.NeighbourMatrixIndirectedGraph;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 *
 */
public class TestGenerator {

    private PrintWriter graphOut;

    private PrintWriter solutionOut;

    private Map<GraphConstant, Integer> graphProperties;

    private Graph graph;

    private List<Integer> solution;

    public TestGenerator(Map<GraphConstant, Integer> graphProperties, String inputFilename, String solutionFilename) throws IOException {
        graphOut = new PrintWriter(new FileOutputStream(inputFilename));
        solutionOut = new PrintWriter(new FileOutputStream(solutionFilename));
        this.graphProperties = graphProperties;
    }

    public static void main(String[] args) throws Exception {
        Map<GraphConstant, Integer> graphProperties = generateGraphProperties(args);
        TestGenerator generator = new TestGenerator(graphProperties, Constants.INPUT_FILENAME, Constants.SOLUTION_FILENAME);
        generator.createGraph();
        generator.printGraph();
        generator.printSolution();
    }

    private void printSolution() {
        int totalWeight = 0;
        for(int secondEndIndex = 1; secondEndIndex < solution.size(); secondEndIndex++) {
            int firstEnd = solution.get(secondEndIndex-1);
            int secondEnd = solution.get(secondEndIndex);
            totalWeight += graph.distance(firstEnd,secondEnd);
            solutionOut.print(String.format("%d ",graph.distance(firstEnd,secondEnd)));
        }
        solutionOut.println();
        solutionOut.println(String.format("%d\n",totalWeight));
        solutionOut.close();
    }

    private void printGraph() {
        int nodeNumber = graphProperties.get(GraphConstant.GRAPH_NODE_NUMBER);
        graphOut.println(String.format("%d %d\n", nodeNumber, (nodeNumber * (nodeNumber - 1)) / 2));
        int firstIndex = graph.getFirstNode();
        for(int firstNode=firstIndex; firstNode < firstIndex + nodeNumber; firstNode++) {
            for(int secondNode=firstNode+1; secondNode < firstIndex + nodeNumber; secondNode++) {
                graphOut.println(String.format("%d %d %d\n", firstNode, secondNode, graph.distance(firstNode, secondNode)));
            }
        }
        graphOut.close();
    }

    private static Map<GraphConstant, Integer> generateGraphProperties(String[] args) {
        Map<GraphConstant, Integer> config = loadDefaultConfiguration();
        if (args == null || args.length == 0) {
            config = loadConfiguration();
        } else {
            GraphConstant[] allGraphConstants = GraphConstant.values();
            for (int i = 0; i < Math.min(allGraphConstants.length, args.length); i++) {
                config.put(allGraphConstants[i], Integer.parseInt(args[i]));
            }
        }
        return config;
    }

    private static Map<GraphConstant, Integer> loadConfiguration() {
        Map<GraphConstant, Integer> graphConfig = new HashMap<GraphConstant, Integer>();
        graphConfig.put(GraphConstant.GRAPH_MIN_EDGE, -1000);
        graphConfig.put(GraphConstant.GRAPH_MAX_EDGE, 1000);
        int nodeNumber = 21;
        graphConfig.put(GraphConstant.GRAPH_NODE_NUMBER, nodeNumber);
        graphConfig.put(GraphConstant.GRAPH_MAX_NODE_DEGREE, nodeNumber - 1);
        return graphConfig;
    }

    private static Map<GraphConstant, Integer> loadDefaultConfiguration() {
        Map<GraphConstant, Integer> graphConfig = new HashMap<GraphConstant, Integer>();
        graphConfig.put(GraphConstant.GRAPH_MIN_EDGE, Constants.MINIMAL_EDGE_COST);
        graphConfig.put(GraphConstant.GRAPH_MAX_EDGE, Constants.MAXIMAL_EDGE_COST);
        graphConfig.put(GraphConstant.GRAPH_NODE_NUMBER, Constants.DEFAULT_NODES_NUMBER);
        graphConfig.put(GraphConstant.GRAPH_MAX_NODE_DEGREE, Constants.DEFAULT_MAXIMAL_NODE_DEGREE);
        return graphConfig;
    }

    public void createGraph() throws Exception {
        int nodeNumber = graphProperties.get(GraphConstant.GRAPH_NODE_NUMBER);
        int maxEdgeWeight = graphProperties.get(GraphConstant.GRAPH_MAX_EDGE);
        int minEdgeWeight = graphProperties.get(GraphConstant.GRAPH_MIN_EDGE);
        // indexing of nodes in graph starts from 1
        boolean isZeroIndexedGraph = true;
        int firstIndex = isZeroIndexedGraph ? 0 : 1;

        graph = new NeighbourMatrixIndirectedGraph(nodeNumber, isZeroIndexedGraph);
        generateSolution(nodeNumber, isZeroIndexedGraph);
        // now every two adjacent numbers in solution define us an edge from the optimal solution

        Random rand = new Random();
        // ends of edges from optimal cycle
        int firstEnd, secondEnd;
        int maxEdge = graphProperties.get(GraphConstant.GRAPH_MIN_EDGE) - 1; // keep which edge will be the heavies so we don't allow to add anymore edges, lighter than it in order to keep the optimality of the solution
        for (int secondEndIndex = 1; secondEndIndex < solution.size(); secondEndIndex++) {
            firstEnd = solution.get(secondEndIndex - 1);
            secondEnd = solution.get(secondEndIndex);
            int edgeWeight = rand.nextInt((maxEdgeWeight - minEdgeWeight) + 1) + minEdgeWeight;
            graph.addEdge(firstEnd, secondEnd, edgeWeight);
            maxEdge = Math.max(maxEdge, edgeWeight);
        }

        // now add all of the rest edges, which should weight more than the maximal edge, used until now
        maxEdge = Math.max(1,maxEdge+1);
        for(firstEnd = firstIndex; firstEnd < firstIndex + nodeNumber; firstEnd++) {
            for(secondEnd = firstEnd + 1; secondEnd <firstIndex + nodeNumber; secondEnd++) {
                if (!graph.hasEdge(firstEnd,secondEnd)) {
                    int weight = rand.nextInt((maxEdgeWeight+1 - maxEdge) + 1) + maxEdge;
                    if (weight == 0) {
                        System.out.print(String.format("(%d,%d)",firstEnd,secondEnd));
                    }
                    graph.addEdge(firstEnd,secondEnd,weight);
                }
            }
        }
    }

    private void generateSolution(int nodeNumber, boolean isZeroBasedIndex) {
        // generate a permutation, which will be our optimal Hamiltonian cycle
        solution = new LinkedList<Integer>();
        int firstIndex = isZeroBasedIndex ? 0 : 1;
        for (int i = 1; i <= nodeNumber; i++) {
            solution.add(i - (1-firstIndex));
        }
        Collections.shuffle(solution);
        // add the first element to the end of the list, so we close the cycle
        solution.add(solution.get(0));
    }
}
