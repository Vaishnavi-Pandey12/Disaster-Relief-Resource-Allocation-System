package com.ddrsas.controller;

import com.ddrsas.model.AllocationResult;
import com.ddrsas.service.AllocationService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/allocate")
@CrossOrigin(origins = "*")
public class AllocationController {
    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @PostMapping
    public AllocationResult allocate() {
        return allocationService.allocateNextRequest();
    }
}
