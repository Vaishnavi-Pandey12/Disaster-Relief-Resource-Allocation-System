package com.ddrsas.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Relief request entity with urgency-based ordering for priority queue usage.
 */
public class Request implements Comparable<Request> {
    private static final AtomicInteger COUNTER = new AtomicInteger(1000);

    private final int id;
    private final String areaName;
    private final int foodRequired;
    private final int waterRequired;
    private final int medicineRequired;
    private final int urgencyLevel;
    private final LocalDateTime createdAt;

    public Request(String areaName, int foodRequired, int waterRequired, int medicineRequired, int urgencyLevel) {
        this.id = COUNTER.incrementAndGet();
        this.areaName = areaName;
        this.foodRequired = foodRequired;
        this.waterRequired = waterRequired;
        this.medicineRequired = medicineRequired;
        this.urgencyLevel = urgencyLevel;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public String getAreaName() {
        return areaName;
    }

    public int getFoodRequired() {
        return foodRequired;
    }

    public int getWaterRequired() {
        return waterRequired;
    }

    public int getMedicineRequired() {
        return medicineRequired;
    }

    public int getUrgencyLevel() {
        return urgencyLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public int compareTo(Request other) {
        int urgencyOrder = Integer.compare(other.urgencyLevel, this.urgencyLevel);
        if (urgencyOrder != 0) {
            return urgencyOrder;
        }
        return this.createdAt.compareTo(other.createdAt);
    }
}
