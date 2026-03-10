package com.ddrsas.model;

public class ReliefCenter {
    private String name;
    private String location;
    private int foodStock;
    private int waterStock;
    private int medicineStock;

    public ReliefCenter() {
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getFoodStock() {
        return foodStock;
    }

    public void setFoodStock(int foodStock) {
        this.foodStock = foodStock;
    }

    public int getWaterStock() {
        return waterStock;
    }

    public void setWaterStock(int waterStock) {
        this.waterStock = waterStock;
    }

    public int getMedicineStock() {
        return medicineStock;
    }

    public void setMedicineStock(int medicineStock) {
        this.medicineStock = medicineStock;
    }
}
