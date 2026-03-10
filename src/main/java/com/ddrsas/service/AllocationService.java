package com.ddrsas.service;

import com.ddrsas.algorithm.Dijkstra;
import com.ddrsas.model.AllocationResult;
import com.ddrsas.model.ReliefCenter;
import com.ddrsas.model.Request;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AllocationService {
    private final List<ReliefCenter> centers = new ArrayList<>();
    private final RequestService requestService;
    private final Dijkstra dijkstra = new Dijkstra();

    public AllocationService(RequestService requestService) {
        this.requestService = requestService;
        seedCenters();
        seedRoutes();
    }

    public List<ReliefCenter> getCenters() {
        return centers;
    }

    public AllocationResult allocateNextRequest() {
        Request request = requestService.getNextPendingRequest();

        if (request == null) {
            AllocationResult result = new AllocationResult();
            result.setStatus("NO_PENDING_REQUESTS");
            result.setMessage("No pending requests to allocate.");
            requestService.addLog("WARN", "Allocation triggered but no pending requests found.");
            return result;
        }

        ReliefCenter selected = centers.stream()
                .filter(center -> hasEnoughStock(center, request))
                .min(Comparator.comparingInt(center -> dijkstra.shortestDistance(center.getLocation(), request.getLocation())))
                .orElse(null);

        if (selected == null) {
            AllocationResult result = new AllocationResult();
            result.setStatus("FAILED");
            result.setRequestId(request.getId());
            result.setMessage("No center has enough resources for request #" + request.getId());
            requestService.addLog("ERROR", "Allocation failed for request #" + request.getId() + " due to insufficient stock.");
            return result;
        }

        int distance = dijkstra.shortestDistance(selected.getLocation(), request.getLocation());
        selected.setFoodStock(selected.getFoodStock() - request.getFoodNeeded());
        selected.setWaterStock(selected.getWaterStock() - request.getWaterNeeded());
        selected.setMedicineStock(selected.getMedicineStock() - request.getMedicineNeeded());

        requestService.markAllocated(request);

        AllocationResult result = new AllocationResult();
        result.setStatus("SUCCESS");
        result.setRequestId(request.getId());
        result.setAllocatedCenter(selected.getName());
        result.setDistance(distance == Integer.MAX_VALUE ? -1 : distance);
        result.setRemainingFood(selected.getFoodStock());
        result.setRemainingWater(selected.getWaterStock());
        result.setRemainingMedicine(selected.getMedicineStock());

        requestService.addLog(
                "INFO",
                "Allocated request #" + request.getId() + " to " + selected.getName() +
                        " (distance=" + result.getDistance() + " km)."
        );

        return result;
    }

    private boolean hasEnoughStock(ReliefCenter center, Request request) {
        return center.getFoodStock() >= request.getFoodNeeded()
                && center.getWaterStock() >= request.getWaterNeeded()
                && center.getMedicineStock() >= request.getMedicineNeeded();
    }

    private void seedCenters() {
        centers.add(new ReliefCenter("Center A", "City A", 500, 300, 200));
        centers.add(new ReliefCenter("Center B", "Vijayawada", 350, 600, 240));
        centers.add(new ReliefCenter("Center C", "City C", 900, 500, 350));
    }

    private void seedRoutes() {
        dijkstra.addEdge("City A", "Vijayawada", 12);
        dijkstra.addEdge("City A", "City C", 18);
        dijkstra.addEdge("City C", "Vijayawada", 7);
        dijkstra.addEdge("City B", "Vijayawada", 10);
    }
}
