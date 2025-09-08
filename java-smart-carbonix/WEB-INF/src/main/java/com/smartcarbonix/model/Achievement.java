package com.smartcarbonix.model;

import java.time.LocalDateTime;

public class Achievement {
    private String id;
    private String userId;
    private String name;
    private String description;
    private String badgeIcon;
    private int coinsReward;
    private LocalDateTime unlockedAt;
    
    // Default constructor
    public Achievement() {
        this.coinsReward = 0;
        this.unlockedAt = LocalDateTime.now();
    }
    
    // Constructor with parameters
    public Achievement(String userId, String name, String description, int coinsReward) {
        this();
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.coinsReward = coinsReward;
    }
    
    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getBadgeIcon() { return badgeIcon; }
    public int getCoinsReward() { return coinsReward; }
    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setBadgeIcon(String badgeIcon) { this.badgeIcon = badgeIcon; }
    public void setCoinsReward(int coinsReward) { this.coinsReward = coinsReward; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }
    
    @Override
    public String toString() {
        return "Achievement{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", coinsReward=" + coinsReward +
                ", unlockedAt=" + unlockedAt +
                '}';
    }
}