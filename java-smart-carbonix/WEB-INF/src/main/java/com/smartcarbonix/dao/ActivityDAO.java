package com.smartcarbonix.dao;

import com.smartcarbonix.model.Activity;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ActivityDAO {
    Activity findById(String id) throws SQLException;
    Activity create(Activity activity) throws SQLException;
    List<Activity> findByUserId(String userId) throws SQLException;
    List<Activity> findByUserIdAndDate(String userId, LocalDate date) throws SQLException;
    List<Activity> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate) throws SQLException;
    boolean delete(String id) throws SQLException;
    List<Activity> getRecentActivities(String userId, int limit) throws SQLException;
}