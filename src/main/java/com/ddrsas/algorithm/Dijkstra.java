package com.ddrsas.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra {
    private final Map<String, List<Edge>> graph = new HashMap<>();

    public void addEdge(String from, String to, int distance) {
        graph.computeIfAbsent(from, ignored -> new ArrayList<>()).add(new Edge(to, distance));
        graph.computeIfAbsent(to, ignored -> new ArrayList<>()).add(new Edge(from, distance));
    }

    public int shortestDistance(String source, String destination) {
        if (source == null || destination == null || !graph.containsKey(source) || !graph.containsKey(destination)) {
            return Integer.MAX_VALUE;
        }

        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(NodeDistance::distance));
        Map<String, Integer> distances = new HashMap<>();
        Set<String> visited = new HashSet<>();

        distances.put(source, 0);
        queue.offer(new NodeDistance(source, 0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
            if (!visited.add(current.node())) {
                continue;
            }
            if (current.node().equals(destination)) {
                return current.distance();
            }

            for (Edge edge : graph.getOrDefault(current.node(), List.of())) {
                if (visited.contains(edge.to())) {
                    continue;
                }
                int newDistance = current.distance() + edge.distance();
                if (newDistance < distances.getOrDefault(edge.to(), Integer.MAX_VALUE)) {
                    distances.put(edge.to(), newDistance);
                    queue.offer(new NodeDistance(edge.to(), newDistance));
                }
            }
        }

        return Integer.MAX_VALUE;
    }

    private record Edge(String to, int distance) {
    }

    private record NodeDistance(String node, int distance) {
    }
}
