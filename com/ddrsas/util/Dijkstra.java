package com.ddrsas.util;

import com.ddrsas.data.Graph;
import com.ddrsas.model.Edge;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Utility class to compute shortest route distance.
 */
public final class Dijkstra {
    private Dijkstra() {
    }

    public static int shortestDistance(Graph graph, String source, String destination) {
        if (source.equals(destination)) {
            return 0;
        }

        Map<String, Integer> distanceMap = new HashMap<>();
        for (String node : graph.getNodes()) {
            distanceMap.put(node, Integer.MAX_VALUE);
        }
        distanceMap.put(source, 0);

        PriorityQueue<NodeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(NodeDistance::distance));
        queue.offer(new NodeDistance(source, 0));

        while (!queue.isEmpty()) {
            NodeDistance current = queue.poll();
            if (current.distance() > distanceMap.getOrDefault(current.node(), Integer.MAX_VALUE)) {
                continue;
            }
            if (current.node().equals(destination)) {
                return current.distance();
            }

            for (Edge edge : graph.getNeighbors(current.node())) {
                int nextDistance = current.distance() + edge.getDistance();
                int recorded = distanceMap.getOrDefault(edge.getDestination(), Integer.MAX_VALUE);
                if (nextDistance < recorded) {
                    distanceMap.put(edge.getDestination(), nextDistance);
                    queue.offer(new NodeDistance(edge.getDestination(), nextDistance));
                }
            }
        }

        return Integer.MAX_VALUE;
    }

    private record NodeDistance(String node, int distance) {
    }
}
