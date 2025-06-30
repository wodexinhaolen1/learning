package com.example.backgroundagriculture.controller;

import com.example.backgroundagriculture.service.VisitorStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private VisitorStatisticsService visitorStatisticsService;

    @GetMapping("/visitors")
    public Map<String, Integer> getVisitorStatistics() {
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("totalVisitors", visitorStatisticsService.getTotalVisitors());
        statistics.put("activeUsers", visitorStatisticsService.getActiveUserCount());
        return statistics;
    }
} 