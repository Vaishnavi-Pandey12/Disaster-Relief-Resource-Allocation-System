package com.ddrsas.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple in-memory event logger for simulation events.
 */
public class Logger {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final List<String> events = new ArrayList<>();

    public synchronized void log(String message) {
        events.add("[" + LocalDateTime.now().format(FORMATTER) + "] " + message);
    }

    public synchronized List<String> getEvents() {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }
}
