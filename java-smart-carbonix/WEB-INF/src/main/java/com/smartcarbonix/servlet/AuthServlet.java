package com.smartcarbonix.servlet;

import com.smartcarbonix.dao.UserDAO;
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

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("login".equals(action)) {
            handleLogin(request, response);
        } else if ("logout".equals(action)) {
            handleLogout(request, response);
        } else if ("register".equals(action)) {
            handleRegister(request, response);
        }
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        
        try {
            User user = userDAO.findByEmail(email);
            
            // If user doesn't exist, create new user (simplified auth for demo)
            if (user == null) {
                user = new User(email, firstName, lastName);
                user = userDAO.create(user);
            }
            
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            
            response.sendRedirect("dashboard");
            
        } catch (SQLException e) {
            request.setAttribute("error", "Authentication failed");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect("index.jsp");
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        
        try {
            // Check if user already exists
            User existingUser = userDAO.findByEmail(email);
            if (existingUser != null) {
                request.setAttribute("error", "User already exists");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }
            
            // Create new user
            User newUser = new User(email, firstName, lastName);
            newUser = userDAO.create(newUser);
            
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", newUser.getId());
            session.setAttribute("user", newUser);
            
            response.sendRedirect("dashboard");
            
        } catch (SQLException e) {
            request.setAttribute("error", "Registration failed");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}