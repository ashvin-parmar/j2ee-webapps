package com.smartcarbonix.model;

import java.time.LocalDateTime;

public class User {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private int totalCoins;
    private int totalPoints;
    private String level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public User() {
        this.totalCoins = 0;
        this.totalPoints = 0;
        this.level = "Eco Beginner";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor with parameters
    public User(String email, String firstName, String lastName) {
        this();
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    // Getters
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public int getTotalCoins() { return totalCoins; }
    public int getTotalPoints() { return totalPoints; }
    public String getLevel() { return level; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
    public void setTotalCoins(int totalCoins) { this.totalCoins = totalCoins; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public void setLevel(String level) { this.level = level; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Utility methods
    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
    
    public void addCoins(int coins) {
        this.totalCoins += coins;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addPoints(int points) {
        this.totalPoints += points;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", totalCoins=" + totalCoins +
                ", totalPoints=" + totalPoints +
                ", level='" + level + '\'' +
                '}';
    }
}