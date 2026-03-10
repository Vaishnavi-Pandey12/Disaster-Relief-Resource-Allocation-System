package com.ddrsas.controller;

import com.ddrsas.model.ReliefCenter;
import com.ddrsas.service.AllocationService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/centers")
@CrossOrigin(origins = "*")
public class CenterController {
    private final AllocationService allocationService;

    public CenterController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @GetMapping
    public List<ReliefCenter> getCenters() {
        return allocationService.getCenters();
    }
}
