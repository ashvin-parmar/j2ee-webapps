package com.civic.platform.dao;

import com.civic.platform.dto.UserDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class UserDAO {
    private DataSource dataSource;

    public UserDAO() {
        try {
            InitialContext context = new InitialContext();
            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/CivicPlatformDB");
        } catch (NamingException e) {
            throw new RuntimeException("Failed to lookup DataSource: " + e.getMessage());
        }
    }

    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
		int ID=rs.getInt("id");
		String name=rs.getString("name");
		System.out.println("Id: "+ID+", Name: "+name);
                UserDTO user = new UserDTO(
                    ID,
                    name,
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("location"),
                    rs.getDate("join_date"),
                    rs.getInt("issues_reported"),
                    rs.getInt("issues_resolved"),
                    rs.getInt("community_validations"),
                    rs.getInt("comments_posted"),
                    rs.getInt("total_views"),
                    rs.getInt("upvotes_received"),
                    rs.getString("account_level"),
                    rs.getInt("experience_points"),
                    rs.getInt("next_level_points")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving users: " + e.getMessage());
        }
        return users;
    }

    public UserDTO getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserDTO(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("location"),
                        rs.getDate("join_date"),
                        rs.getInt("issues_reported"),
                        rs.getInt("issues_resolved"),
                        rs.getInt("community_validations"),
                        rs.getInt("comments_posted"),
                        rs.getInt("total_views"),
                        rs.getInt("upvotes_received"),
                        rs.getString("account_level"),
                        rs.getInt("experience_points"),
                        rs.getInt("next_level_points")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    public boolean updateUser(UserDTO user) {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ?, location = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhone());
            stmt.setString(4, user.getLocation());
            stmt.setInt(5, user.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }
}
