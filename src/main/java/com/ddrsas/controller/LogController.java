package com.ddrsas.controller;

import com.ddrsas.model.EventLog;
import com.ddrsas.service.RequestService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogController {
    private final RequestService requestService;

    public LogController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public List<EventLog> getLogs() {
        return requestService.getLogs();
    }
}
