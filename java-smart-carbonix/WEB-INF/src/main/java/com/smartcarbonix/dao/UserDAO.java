package com.smartcarbonix.dao;

import com.smartcarbonix.model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    User findById(String id) throws SQLException;
    User findByEmail(String email) throws SQLException;
    User create(User user) throws SQLException;
    User update(User user) throws SQLException;
    boolean delete(String id) throws SQLException;
    List<User> findAll() throws SQLException;
    List<User> getLeaderboard(int limit) throws SQLException;
    int getUserRank(String userId) throws SQLException;
}