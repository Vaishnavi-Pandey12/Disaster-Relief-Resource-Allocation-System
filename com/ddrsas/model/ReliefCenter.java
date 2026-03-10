package com.ddrsas.model;

/**
 * Represents a relief center and its available resources.
 */
public class ReliefCenter {
    private final String name;
    private final String location;
    private int foodStock;
    private int waterStock;
    private int medicineStock;

    public ReliefCenter(String name, String location, int foodStock, int waterStock, int medicineStock) {
        this.name = name;
        this.location = location;
        this.foodStock = foodStock;
        this.waterStock = waterStock;
        this.medicineStock = medicineStock;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getFoodStock() {
        return foodStock;
    }

    public int getWaterStock() {
        return waterStock;
    }

    public int getMedicineStock() {
        return medicineStock;
    }

    public boolean canFulfill(Request request) {
        return foodStock >= request.getFoodRequired()
                && waterStock >= request.getWaterRequired()
                && medicineStock >= request.getMedicineRequired();
    }

    public void allocate(Request request) {
        foodStock -= request.getFoodRequired();
        waterStock -= request.getWaterRequired();
        medicineStock -= request.getMedicineRequired();
    }
}
