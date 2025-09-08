package com.smartcarbonix.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcarbonix.dao.ActivityDAO;
import com.smartcarbonix.dao.UserDAO;
import com.smartcarbonix.dao.impl.ActivityDAOImpl;
import com.smartcarbonix.dao.impl.UserDAOImpl;
import com.smartcarbonix.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAOImpl();
    private ActivityDAO activityDAO = new ActivityDAOImpl();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            // Set user in request for JSP
            request.setAttribute("user", user);
            request.setAttribute("userRank", userDAO.getUserRank(userId));
            
            // Get recent activities
            request.setAttribute("recentActivities", activityDAO.getRecentActivities(userId, 5));
            
            // Forward to dashboard JSP
            request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
            
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String userId = (String) session.getAttribute("userId");
        String action = request.getParameter("action");
        
        if ("getStats".equals(action)) {
            try {
                // Calculate dashboard stats
                Map<String, Object> stats = new HashMap<>();
                stats.put("carbonFootprint", 0.0); // Calculate from activities
                stats.put("coinsEarned", userDAO.findById(userId).getTotalCoins());
                stats.put("wasteReduced", 85); // Example data
                stats.put("energySaved", 120); // Example data
                
                response.setContentType("application/json");
                response.getWriter().write(objectMapper.writeValueAsString(stats));
                
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Database error\"}");
            }
        }
    }
}