package com.fmi.parallelcomputing.utils.graph;

import java.util.List;

/**
 * Interface for all data structure that are used to represent graphs.
 */
public interface Graph {

    public abstract void addEdge(int fromNode, int toNode, int cost);

    public abstract void removeEdge(int fromNode, int toNode);

    public abstract boolean hasEdge(int fromNode, int toNode);

    public abstract int distance(int fromNode, int toNode);

    public abstract List<Integer> getNeighbours(int forNode);

    public abstract int getFirstNode();

}
