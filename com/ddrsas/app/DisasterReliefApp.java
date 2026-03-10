package com.ddrsas.app;

import com.ddrsas.data.Graph;
import com.ddrsas.model.ReliefCenter;
import com.ddrsas.model.Request;
import com.ddrsas.util.Allocator;
import com.ddrsas.util.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

/**
 * Main interactive console application that orchestrates UI and backend services.
 */
public class DisasterReliefApp {
    private final Scanner scanner = new Scanner(System.in);
    private final PriorityQueue<Request> pendingRequests = new PriorityQueue<>();
    private final Logger logger = new Logger();
    private final List<ReliefCenter> centers = new ArrayList<>();
    private final Graph graph = new Graph();
    private final Allocator allocator;

    private volatile boolean simulationRunning = false;
    private Thread simulatorThread;

    public DisasterReliefApp() {
        seedCenters();
        seedGraph();
        this.allocator = new Allocator(graph, centers, logger);
    }

    public static void main(String[] args) {
        new DisasterReliefApp().run();
    }

    public void run() {
        logger.log("DDR-SAS system initialized.");

        boolean running = true;
        while (running) {
            ConsoleUI.printHeader();
            ConsoleUI.printMenu();
            int choice = readInt("Choose an option: ", 1, 7);

            switch (choice) {
                case 1 -> addReliefRequest();
                case 2 -> viewPendingRequests();
                case 3 -> allocateResources();
                case 4 -> viewReliefCenters();
                case 5 -> viewEventLogs();
                case 6 -> toggleSimulation();
                case 7 -> {
                    shutdownSimulation();
                    running = false;
                    System.out.println(ConsoleUI.GREEN + "Exiting DDR-SAS. Stay safe!" + ConsoleUI.RESET);
                }
                default -> System.out.println(ConsoleUI.RED + "Invalid option." + ConsoleUI.RESET);
            }
        }
    }

    private void addReliefRequest() {
        String area = readString("Enter area name: ");
        int food = readInt("Food units required: ", 0, 100_000);
        int water = readInt("Water units required: ", 0, 100_000);
        int medicine = readInt("Medicine units required: ", 0, 100_000);
        int urgency = readInt("Urgency level (1-5): ", 1, 5);

        Request request = new Request(area, food, water, medicine, urgency);
        pendingRequests.offer(request);
        graph.addNode(area);

        logger.log("New request added: Request#" + request.getId() + " for " + area + " (Urgency=" + urgency + ")");
        System.out.println(ConsoleUI.GREEN + "Request added successfully." + ConsoleUI.RESET);
    }

    private void viewPendingRequests() {
        if (pendingRequests.isEmpty()) {
            System.out.println(ConsoleUI.YELLOW + "No pending requests." + ConsoleUI.RESET);
            return;
        }

        List<Request> sorted = new ArrayList<>(pendingRequests);
        sorted.sort(Comparator.naturalOrder());
        ConsoleUI.printPendingRequests(sorted);
    }

    private void allocateResources() {
        if (pendingRequests.isEmpty()) {
            System.out.println(ConsoleUI.YELLOW + "No pending requests to allocate." + ConsoleUI.RESET);
            return;
        }

        Request top = pendingRequests.poll();
        ConsoleUI.loadingAnimation();
        Allocator.AllocationResult result = allocator.allocate(top);

        if (result.success()) {
            System.out.println(ConsoleUI.GREEN + "Allocation successful!" + ConsoleUI.RESET);
            System.out.println("Request ID: " + top.getId());
            System.out.println("Fulfilled by: " + result.center().getName());
            System.out.println("Route distance: " + result.distance() + " km");
        } else {
            System.out.println(ConsoleUI.RED + "Allocation failed: " + result.message() + ConsoleUI.RESET);
        }
    }

    private void viewReliefCenters() {
        ConsoleUI.printReliefCenters(centers);
    }

    private void viewEventLogs() {
        List<String> logs = logger.getEvents();
        if (logs.isEmpty()) {
            System.out.println(ConsoleUI.YELLOW + "No event logs found." + ConsoleUI.RESET);
            return;
        }

        System.out.println(ConsoleUI.CYAN + "\nEvent Logs" + ConsoleUI.RESET);
        System.out.println("------------------------------------");
        for (String log : logs) {
            System.out.println(log);
        }
    }

    private void toggleSimulation() {
        if (simulationRunning) {
            shutdownSimulation();
            System.out.println(ConsoleUI.YELLOW + "Request simulation stopped." + ConsoleUI.RESET);
            return;
        }

        simulationRunning = true;
        simulatorThread = new Thread(this::simulationLoop, "ddr-simulator");
        simulatorThread.setDaemon(true);
        simulatorThread.start();
        System.out.println(ConsoleUI.GREEN + "Request simulation started." + ConsoleUI.RESET);
    }

    private void simulationLoop() {
        Random random = new Random();
        String[] areas = {"HarborTown", "NorthCamp", "EastVillage", "HillZone", "RiverSide"};

        while (simulationRunning) {
            int food = 30 + random.nextInt(120);
            int water = 40 + random.nextInt(120);
            int medicine = 10 + random.nextInt(60);
            int urgency = 1 + random.nextInt(5);
            String area = areas[random.nextInt(areas.length)];

            Request request = new Request(area, food, water, medicine, urgency);
            pendingRequests.offer(request);
            graph.addNode(area);
            logger.log("[SIM] Request#" + request.getId() + " generated for " + area + " (Urgency=" + urgency + ")");

            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void shutdownSimulation() {
        simulationRunning = false;
        if (simulatorThread != null) {
            simulatorThread.interrupt();
            simulatorThread = null;
        }
    }

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(raw);
                if (value < min || value > max) {
                    System.out.println(ConsoleUI.RED + "Enter a value between " + min + " and " + max + "." + ConsoleUI.RESET);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println(ConsoleUI.RED + "Invalid number. Try again." + ConsoleUI.RESET);
            }
        }
    }

    private String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println(ConsoleUI.RED + "Input cannot be empty." + ConsoleUI.RESET);
        }
    }

    private void seedCenters() {
        centers.add(new ReliefCenter("Central Hub", "CentralCity", 1400, 1500, 600));
        centers.add(new ReliefCenter("North Depot", "NorthPoint", 900, 1200, 500));
        centers.add(new ReliefCenter("East Station", "EastGate", 1000, 1000, 550));
    }

    private void seedGraph() {
        graph.addEdge("CentralCity", "NorthPoint", 18);
        graph.addEdge("CentralCity", "EastGate", 12);
        graph.addEdge("NorthPoint", "EastGate", 16);
        graph.addEdge("CentralCity", "HarborTown", 20);
        graph.addEdge("NorthPoint", "NorthCamp", 9);
        graph.addEdge("EastGate", "EastVillage", 7);
        graph.addEdge("CentralCity", "HillZone", 14);
        graph.addEdge("EastGate", "RiverSide", 11);
    }
}
