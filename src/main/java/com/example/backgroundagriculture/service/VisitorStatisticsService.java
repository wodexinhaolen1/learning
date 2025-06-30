package com.example.backgroundagriculture.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VisitorStatisticsService {
    private final AtomicInteger totalVisitors = new AtomicInteger(0);
    private final ConcurrentHashMap<String, Long> activeUsers = new ConcurrentHashMap<>();

    public void addVisitor() {
        totalVisitors.incrementAndGet();
    }

    public void addActiveUser(String username) {
        activeUsers.put(username, System.currentTimeMillis());
    }

    public void removeActiveUser(String username) {
        activeUsers.remove(username);
    }

    public int getTotalVisitors() {
        return totalVisitors.get();
    }

    public int getActiveUserCount() {
        long currentTime = System.currentTimeMillis();
        // 清理超过30分钟未活动的用户
        activeUsers.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue()) > 30 * 60 * 1000
        );
        return activeUsers.size();
    }
} 