package com.ddrsas.model;

public class Request {
    private Long id;
    private String location;
    private String urgency;
    private int foodNeeded;
    private int waterNeeded;
    private int medicineNeeded;
    private boolean allocated;

    public Request() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public int getFoodNeeded() {
        return foodNeeded;
    }

    public void setFoodNeeded(int foodNeeded) {
        this.foodNeeded = foodNeeded;
    }

    public int getWaterNeeded() {
        return waterNeeded;
    }

    public void setWaterNeeded(int waterNeeded) {
        this.waterNeeded = waterNeeded;
    }

    public int getMedicineNeeded() {
        return medicineNeeded;
    }

    public void setMedicineNeeded(int medicineNeeded) {
        this.medicineNeeded = medicineNeeded;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }
}
