package com.ddrsas.util;

import com.ddrsas.data.Graph;
import com.ddrsas.model.ReliefCenter;
import com.ddrsas.model.Request;

import java.util.List;

/**
 * Handles request-to-center assignment using stock availability and shortest distance.
 */
public class Allocator {
    private final Graph graph;
    private final List<ReliefCenter> reliefCenters;
    private final Logger logger;

    public Allocator(Graph graph, List<ReliefCenter> reliefCenters, Logger logger) {
        this.graph = graph;
        this.reliefCenters = reliefCenters;
        this.logger = logger;
    }

    public AllocationResult allocate(Request request) {
        ReliefCenter bestCenter = null;
        int bestDistance = Integer.MAX_VALUE;

        for (ReliefCenter center : reliefCenters) {
            if (!center.canFulfill(request)) {
                continue;
            }
            int distance = Dijkstra.shortestDistance(graph, center.getLocation(), request.getAreaName());
            if (distance < bestDistance) {
                bestDistance = distance;
                bestCenter = center;
            }
        }

        if (bestCenter == null || bestDistance == Integer.MAX_VALUE) {
            String message = "Unable to fulfill Request#" + request.getId() + " for " + request.getAreaName();
            logger.log(message);
            return new AllocationResult(false, message, null, Integer.MAX_VALUE);
        }

        bestCenter.allocate(request);
        String message = "Request#" + request.getId() + " allocated to " + bestCenter.getName()
                + " | Area=" + request.getAreaName() + " | Distance=" + bestDistance + " km";
        logger.log(message);
        return new AllocationResult(true, message, bestCenter, bestDistance);
    }

    public record AllocationResult(boolean success, String message, ReliefCenter center, int distance) {
    }
}
