package com.ddrsas.app;

import com.ddrsas.model.ReliefCenter;
import com.ddrsas.model.Request;

import java.util.List;

/**
 * Presentation-only helper class for console styling and table rendering.
 */
public final class ConsoleUI {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";

    private ConsoleUI() {
    }

    public static void printHeader() {
        System.out.println(CYAN + "====================================" + RESET);
        System.out.println(BOLD + "   DDR-SAS DISASTER RELIEF SYSTEM   " + RESET);
        System.out.println(CYAN + "====================================" + RESET);
    }

    public static void printMenu() {
        System.out.println("\n1. Add Relief Request");
        System.out.println("2. View Pending Requests");
        System.out.println("3. Allocate Resources");
        System.out.println("4. View Relief Centers");
        System.out.println("5. View Event Logs");
        System.out.println("6. Simulate Requests (Threaded)");
        System.out.println("7. Exit");
        System.out.println("------------------------------------");
    }

    public static String urgencyColor(int urgency) {
        if (urgency >= 4) {
            return RED;
        }
        if (urgency == 3) {
            return YELLOW;
        }
        return GREEN;
    }

    public static void printPendingRequests(List<Request> requests) {
        System.out.printf("%-8s %-16s %-8s %-8s %-10s %-8s%n", "ID", "Area", "Food", "Water", "Medicine", "Urgency");
        System.out.println("-----------------------------------------------------------------");
        for (Request request : requests) {
            String urgency = urgencyColor(request.getUrgencyLevel()) + request.getUrgencyLevel() + RESET;
            System.out.printf("%-8d %-16s %-8d %-8d %-10d %-8s%n",
                    request.getId(),
                    request.getAreaName(),
                    request.getFoodRequired(),
                    request.getWaterRequired(),
                    request.getMedicineRequired(),
                    urgency);
        }
    }

    public static void printReliefCenters(List<ReliefCenter> centers) {
        System.out.printf("%-18s %-14s %-8s %-8s %-10s%n", "Center", "Location", "Food", "Water", "Medicine");
        System.out.println("-----------------------------------------------------------------");
        for (ReliefCenter center : centers) {
            System.out.printf("%-18s %-14s %-8d %-8d %-10d%n",
                    center.getName(),
                    center.getLocation(),
                    center.getFoodStock(),
                    center.getWaterStock(),
                    center.getMedicineStock());
        }
    }

    public static void loadingAnimation() {
        String[] frames = {"Allocating   ", "Allocating.  ", "Allocating.. ", "Allocating..."};
        for (String frame : frames) {
            System.out.print("\r" + CYAN + frame + RESET);
            sleep(250);
        }
        System.out.print("\r");
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
