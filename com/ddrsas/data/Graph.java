package com.ddrsas.data;

import com.ddrsas.model.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Undirected weighted graph for disaster routing.
 */
public class Graph {
    private final Map<String, List<Edge>> adjacencyList = new HashMap<>();

    public void addNode(String node) {
        adjacencyList.computeIfAbsent(node, key -> new ArrayList<>());
    }

    public void addEdge(String source, String destination, int distance) {
        addNode(source);
        addNode(destination);
        adjacencyList.get(source).add(new Edge(destination, distance));
        adjacencyList.get(destination).add(new Edge(source, distance));
    }

    public List<Edge> getNeighbors(String node) {
        return adjacencyList.getOrDefault(node, List.of());
    }

    public Set<String> getNodes() {
        return adjacencyList.keySet();
    }
}
