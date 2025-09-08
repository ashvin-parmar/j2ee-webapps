package com.smartcarbonix.dao.impl;

import com.smartcarbonix.dao.UserDAO;
import com.smartcarbonix.model.User;
import com.smartcarbonix.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAOImpl implements UserDAO {

    @Override
    public User findById(String id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    @Override
    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (id, email, first_name, last_name, profile_image_url, total_coins, total_points, level) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING *";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            user.setId(UUID.randomUUID().toString());
            stmt.setString(1, user.getId());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getProfileImageUrl());
            stmt.setInt(6, user.getTotalCoins());
            stmt.setInt(7, user.getTotalPoints());
            stmt.setString(8, user.getLevel());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return user;
    }

    @Override
    public User update(User user) throws SQLException {
        String sql = "UPDATE users SET email = ?, first_name = ?, last_name = ?, " +
                     "profile_image_url = ?, total_coins = ?, total_points = ?, level = ?, updated_at = ? " +
                     "WHERE id = ? RETURNING *";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getProfileImageUrl());
            stmt.setInt(5, user.getTotalCoins());
            stmt.setInt(6, user.getTotalPoints());
            stmt.setString(7, user.getLevel());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(9, user.getId());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        }
        return user;
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY total_points DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    @Override
    public List<User> getLeaderboard(int limit) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY total_points DESC, total_coins DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    @Override
    public int getUserRank(String userId) throws SQLException {
        String sql = "SELECT rank FROM (" +
                     "SELECT id, ROW_NUMBER() OVER (ORDER BY total_points DESC, total_coins DESC) as rank " +
                     "FROM users" +
                     ") ranked WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("rank");
            }
        }
        return 0;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setEmail(rs.getString("email"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setProfileImageUrl(rs.getString("profile_image_url"));
        user.setTotalCoins(rs.getInt("total_coins"));
        user.setTotalPoints(rs.getInt("total_points"));
        user.setLevel(rs.getString("level"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return user;
    }
}