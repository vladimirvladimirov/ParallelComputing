package com.fmi.parallelcomputing.graph;

import java.util.List;

/**
 * Created by Dev on 3/6/2015.
 */
public interface Graph {

    public abstract void addEdge(int fromNode, int toNode, int cost);

    public abstract void removeEdge(int fromNode, int toNode);

    public abstract boolean hasEdge(int fromNode, int toNode);

    public abstract int distance(int fromNode, int toNode);

    public abstract List<Integer> getNeighbours(int forNode);

    public abstract int getFirstNode();

}
