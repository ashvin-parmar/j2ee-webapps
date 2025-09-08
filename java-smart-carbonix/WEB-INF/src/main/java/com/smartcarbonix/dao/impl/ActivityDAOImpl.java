package com.smartcarbonix.dao.impl;

import com.smartcarbonix.dao.ActivityDAO;
import com.smartcarbonix.model.Activity;
import com.smartcarbonix.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActivityDAOImpl implements ActivityDAO {

    @Override
    public Activity findById(String id) throws SQLException {
        String sql = "SELECT * FROM activities WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToActivity(rs);
            }
        }
        return null;
    }

    @Override
    public Activity create(Activity activity) throws SQLException {
        String sql = "INSERT INTO activities (id, user_id, type, description, carbon_impact, coins_earned, date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING *";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            activity.setId(UUID.randomUUID().toString());
            stmt.setString(1, activity.getId());
            stmt.setString(2, activity.getUserId());
            stmt.setString(3, activity.getType());
            stmt.setString(4, activity.getDescription());
            stmt.setBigDecimal(5, activity.getCarbonImpact());
            stmt.setInt(6, activity.getCoinsEarned());
            stmt.setDate(7, Date.valueOf(activity.getDate()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToActivity(rs);
            }
        }
        return activity;
    }

    @Override
    public List<Activity> findByUserId(String userId) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activities WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        }
        return activities;
    }

    @Override
    public List<Activity> findByUserIdAndDate(String userId, LocalDate date) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activities WHERE user_id = ? AND date = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        }
        return activities;
    }

    @Override
    public List<Activity> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activities WHERE user_id = ? AND date BETWEEN ? AND ? ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        }
        return activities;
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM activities WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<Activity> getRecentActivities(String userId, int limit) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT * FROM activities WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        }
        return activities;
    }

    private Activity mapResultSetToActivity(ResultSet rs) throws SQLException {
        Activity activity = new Activity();
        activity.setId(rs.getString("id"));
        activity.setUserId(rs.getString("user_id"));
        activity.setType(rs.getString("type"));
        activity.setDescription(rs.getString("description"));
        activity.setCarbonImpact(rs.getBigDecimal("carbon_impact"));
        activity.setCoinsEarned(rs.getInt("coins_earned"));
        
        Date date = rs.getDate("date");
        if (date != null) {
            activity.setDate(date.toLocalDate());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            activity.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return activity;
    }
}