package com.smartcarbonix.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Challenge {
    private String id;
    private String name;
    private String description;
    private String type; // daily, weekly, monthly
    private int targetValue;
    private int reward;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Challenge() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with parameters
    public Challenge(String name, String description, String type, int targetValue, int reward) {
        this();
        this.name = name;
        this.description = description;
        this.type = type;
        this.targetValue = targetValue;
        this.reward = reward;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public int getTargetValue() { return targetValue; }
    public int getReward() { return reward; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }
    public void setReward(int reward) { this.reward = reward; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setActive(boolean active) { isActive = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Utility methods
    public boolean isExpired() {
        return endDate != null && LocalDate.now().isAfter(endDate);
    }
    
    public boolean isOngoing() {
        LocalDate now = LocalDate.now();
        return isActive && 
               (startDate == null || !now.isBefore(startDate)) &&
               (endDate == null || !now.isAfter(endDate));
    }
    
    @Override
    public String toString() {
        return "Challenge{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", targetValue=" + targetValue +
                ", reward=" + reward +
                ", isActive=" + isActive +
                '}';
    }
}