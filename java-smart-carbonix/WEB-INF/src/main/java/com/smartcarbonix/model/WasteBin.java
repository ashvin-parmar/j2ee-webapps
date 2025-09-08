package com.smartcarbonix.model;

import java.time.LocalDateTime;

public class WasteBin {
    private String id;
    private String location;
    private String type; // recycling, compost, electronic, general
    private int fillLevel; // percentage 0-100
    private String status; // online, offline, maintenance
    private LocalDateTime lastEmptied;
    private LocalDateTime createdAt;
    
    // Default constructor
    public WasteBin() {
        this.fillLevel = 0;
        this.status = "online";
        this.lastEmptied = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with parameters
    public WasteBin(String location, String type) {
        this();
        this.location = location;
        this.type = type;
    }
    
    // Getters
    public String getId() { return id; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public int getFillLevel() { return fillLevel; }
    public String getStatus() { return status; }
    public LocalDateTime getLastEmptied() { return lastEmptied; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setLocation(String location) { this.location = location; }
    public void setType(String type) { this.type = type; }
    public void setFillLevel(int fillLevel) { this.fillLevel = fillLevel; }
    public void setStatus(String status) { this.status = status; }
    public void setLastEmptied(LocalDateTime lastEmptied) { this.lastEmptied = lastEmptied; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Utility methods
    public boolean needsEmptying() {
        return fillLevel >= 80;
    }
    
    public boolean isOnline() {
        return "online".equals(status);
    }
    
    public String getFillLevelStatus() {
        if (fillLevel >= 90) return "Critical";
        if (fillLevel >= 70) return "High";
        if (fillLevel >= 40) return "Medium";
        return "Low";
    }
    
    @Override
    public String toString() {
        return "WasteBin{" +
                "id='" + id + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", fillLevel=" + fillLevel +
                ", status='" + status + '\'' +
                '}';
    }
}