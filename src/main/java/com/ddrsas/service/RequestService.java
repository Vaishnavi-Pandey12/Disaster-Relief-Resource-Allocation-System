package com.ddrsas.service;

import com.ddrsas.model.EventLog;
import com.ddrsas.model.Request;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RequestService {
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final List<Request> requests = new CopyOnWriteArrayList<>();
    private final List<EventLog> logs = new CopyOnWriteArrayList<>();

    public Request createRequest(Request request) {
        request.setId(idGenerator.getAndIncrement());
        request.setAllocated(false);
        requests.add(request);
        addLog("INFO", "New request created: #" + request.getId() + " at " + request.getLocation());
        return request;
    }

    public List<Request> getAllRequests() {
        return requests;
    }

    public Request getNextPendingRequest() {
        return requests.stream()
                .filter(request -> !request.isAllocated())
                .min(Comparator
                        .comparingInt((Request req) -> urgencyRank(req.getUrgency()))
                        .thenComparingLong(Request::getId))
                .orElse(null);
    }

    public void markAllocated(Request request) {
        request.setAllocated(true);
    }

    public List<EventLog> getLogs() {
        return new ArrayList<>(logs);
    }

    public void addLog(String level, String message) {
        logs.add(new EventLog(level, message));
    }

    private int urgencyRank(String urgency) {
        if (urgency == null) {
            return 3;
        }
        return switch (urgency.toUpperCase()) {
            case "HIGH" -> 1;
            case "MEDIUM" -> 2;
            default -> 3;
        };
    }
}
