package com.smartcarbonix.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Activity {
    private String id;
    private String userId;
    private String type; // transport, food, energy, waste
    private String description;
    private BigDecimal carbonImpact; // kg CO2 saved/produced
    private int coinsEarned;
    private LocalDate date;
    private LocalDateTime createdAt;
    
    // Default constructor
    public Activity() {
        this.carbonImpact = BigDecimal.ZERO;
        this.coinsEarned = 0;
        this.date = LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor with parameters
    public Activity(String userId, String type, String description) {
        this();
        this.userId = userId;
        this.type = type;
        this.description = description;
    }
    
    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public BigDecimal getCarbonImpact() { return carbonImpact; }
    public int getCoinsEarned() { return coinsEarned; }
    public LocalDate getDate() { return date; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }
    public void setCarbonImpact(BigDecimal carbonImpact) { this.carbonImpact = carbonImpact; }
    public void setCoinsEarned(int coinsEarned) { this.coinsEarned = coinsEarned; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Activity{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", carbonImpact=" + carbonImpact +
                ", coinsEarned=" + coinsEarned +
                ", date=" + date +
                '}';
    }
}