package com.fmi.parallelcomputing.utils.graph;

import com.fmi.parallelcomputing.utils.exceptions.IllegalOperationException;
import com.fmi.parallelcomputing.utils.ExceptionsMessages;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is implementation of graph representation by adjacency matrix.
 */
public class NeighbourMatrixIndirectedGraph implements Graph {

    public static final int NO_EDGE_WEIGHT = 0;
    private static final int NO_EDGE = 0x7fffffff;
    /**
     * If true, then indexing of our nodes starts from 0, otherwise starts from 1. BY default the node indexes are from 0
     */
    private boolean isZeroBasedIndex;
    /**
     * Number of nodes in graph
     */
    private int n;
    /**
     * Matrix of distances, representing our graph.
     */
    private int[][] graph;

    /**
     * Defines the first number, usable as index of a node.
     */
    private int firstIndex;

    /**
     * If true, than the graph is indirected (the edges are 'two-way'). By default is true.
     */
    private boolean isDirected;

    /**
     * Creates an empty indirected graph.
     *
     * @param nodes number of nodes in graph
     */
    public NeighbourMatrixIndirectedGraph(int nodes) {
        n = nodes;
        isZeroBasedIndex = false;
        isDirected = false;
        firstIndex = isZeroBasedIndex ? 0 : 1;

        graph = new int[n + firstIndex][];
        for (int i = 0; i < n + firstIndex; i++) {
            graph[i] = new int[n + firstIndex];
        }
    }

    /**
     * Creates an empty indirected graph.
     *
     * @param nodes number of nodes in graph
     * @param isZeroBasedIndex true if the nodes are 0-indexed, or false if their numeration starts from 1
     */
    public NeighbourMatrixIndirectedGraph(int nodes, boolean isZeroBasedIndex) {
        n = nodes;
        this.isZeroBasedIndex = isZeroBasedIndex;
        isDirected = false;
        firstIndex = isZeroBasedIndex ? 0 : 1;

        graph = new int[n + firstIndex][];
        for (int i = 0; i < n + firstIndex; i++) {
            graph[i] = new int[n + firstIndex];
            for(int j=0; j<n+firstIndex; j++) {
                graph[i][j] = NO_EDGE;
            }
        }
    }

    @Override
    public void addEdge(int fromNode, int toNode, int cost) {
        validateNodes(fromNode, toNode);
        graph[fromNode][toNode] = cost;
        if (!isDirected) {
            graph[toNode][fromNode] = cost;
        }
    }

    @Override
    public void removeEdge(int fromNode, int toNode) {
        validateNodes(fromNode, toNode);
        graph[fromNode][toNode] = NO_EDGE;
        if (!isDirected) {
            graph[toNode][fromNode] = NO_EDGE;
        }
    }

    @Override
    public boolean hasEdge(int fromNode, int toNode) {
        return (graph[fromNode][toNode] != NO_EDGE);
    }

    @Override
    public int distance(int fromNode, int toNode) {
        if (!hasEdge(fromNode,toNode)) {
            return NO_EDGE_WEIGHT;
        }
        else {
            return graph[fromNode][toNode];
        }
    }

    @Override
    public List<Integer> getNeighbours(int forNode) {
        List<Integer> neighbours = new LinkedList<Integer>();
        for(int i=firstIndex; i<firstIndex + n; i++) {
            if (hasEdge(forNode,i)) {
                neighbours.add(i);
            }
        }
        return neighbours;
    }

    @Override
    public int getFirstNode() {
        return isZeroBasedIndex ? 0 : 1;
    }

    private void validateNodes(int fromNode, int toNode) {
        if (fromNode < firstIndex || fromNode >= n + firstIndex) {
            throw new IllegalOperationException(String.format(ExceptionsMessages.ILLEGAL_NODE_INDEX_FORMAT, fromNode));
        }
        if (toNode < firstIndex || toNode >= n + firstIndex) {
            throw new IllegalOperationException(String.format(ExceptionsMessages.ILLEGAL_NODE_INDEX_FORMAT, toNode));
        }
    }
}
