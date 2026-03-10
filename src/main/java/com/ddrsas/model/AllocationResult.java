package com.ddrsas.model;

public class AllocationResult {
    private String status;
    private Long requestId;
    private String allocatedCenter;
    private String centerName;
    private int distance;
    private Integer remainingFood;
    private Integer remainingWater;
    private Integer remainingMedicine;
    private String message;

    public AllocationResult() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getAllocatedCenter() {
        return allocatedCenter;
    }

    public void setAllocatedCenter(String allocatedCenter) {
        this.allocatedCenter = allocatedCenter;
        this.centerName = allocatedCenter;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
        this.allocatedCenter = centerName;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Integer getRemainingFood() {
        return remainingFood;
    }

    public void setRemainingFood(Integer remainingFood) {
        this.remainingFood = remainingFood;
    }

    public Integer getRemainingWater() {
        return remainingWater;
    }

    public void setRemainingWater(Integer remainingWater) {
        this.remainingWater = remainingWater;
    }

    public Integer getRemainingMedicine() {
        return remainingMedicine;
    }

    public void setRemainingMedicine(Integer remainingMedicine) {
        this.remainingMedicine = remainingMedicine;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
