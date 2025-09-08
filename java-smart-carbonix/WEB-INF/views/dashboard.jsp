<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Smart Carbonix</title>
    <link rel="stylesheet" href="../css/style.css">
    <link rel="stylesheet" href="../css/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/chart.js@4.2.1/dist/chart.min.css">
</head>
<body class="dashboard-page">
    <div class="dashboard-container">
        <!-- Sidebar Navigation -->
        <nav class="sidebar">
            <div class="sidebar-header">
                <div class="brand">
                    <i class="fas fa-leaf"></i>
                    <h2>Smart Carbonix</h2>
                </div>
            </div>
            
            <div class="user-profile">
                <div class="profile-avatar">
                    <c:choose>
                        <c:when test="${not empty user.profileImageUrl}">
                            <img src="${user.profileImageUrl}" alt="Profile">
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-user-circle"></i>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="profile-info">
                    <h3>${user.firstName} ${user.lastName}</h3>
                    <p class="user-level">${user.level}</p>
                    <div class="user-stats">
                        <span class="coins">
                            <i class="fas fa-coins"></i>
                            ${user.totalCoins} EcoCoins
                        </span>
                        <span class="rank">
                            Rank #${userRank}
                        </span>
                    </div>
                </div>
            </div>

            <ul class="nav-menu">
                <li class="nav-item active">
                    <a href="dashboard" class="nav-link">
                        <i class="fas fa-tachometer-alt"></i>
                        <span>Dashboard</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="carbon-tracker" class="nav-link">
                        <i class="fas fa-chart-line"></i>
                        <span>Carbon Tracker</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="waste-management" class="nav-link">
                        <i class="fas fa-recycle"></i>
                        <span>Waste Management</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="energy-monitor" class="nav-link">
                        <i class="fas fa-bolt"></i>
                        <span>Energy Monitor</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="rewards" class="nav-link">
                        <i class="fas fa-gift"></i>
                        <span>Rewards</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a href="achievements" class="nav-link">
                        <i class="fas fa-trophy"></i>
                        <span>Achievements</span>
                    </a>
                </li>
            </ul>

            <div class="sidebar-footer">
                <form action="auth" method="post">
                    <input type="hidden" name="action" value="logout">
                    <button type="submit" class="logout-btn">
                        <i class="fas fa-sign-out-alt"></i>
                        <span>Logout</span>
                    </button>
                </form>
            </div>
        </nav>

        <!-- Main Content -->
        <main class="main-content">
            <header class="content-header">
                <h1>Welcome back, ${user.firstName}!</h1>
                <p>Here's your sustainability overview for today</p>
            </header>

            <!-- Statistics Cards -->
            <section class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon carbon">
                        <i class="fas fa-leaf"></i>
                    </div>
                    <div class="stat-content">
                        <h3 id="carbonFootprint">0.0 kg</h3>
                        <p>Carbon Footprint</p>
                        <span class="stat-change positive">-12% today</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon coins">
                        <i class="fas fa-coins"></i>
                    </div>
                    <div class="stat-content">
                        <h3 id="coinsEarned">${user.totalCoins}</h3>
                        <p>EcoCoins Earned</p>
                        <span class="stat-change positive">+25 today</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon waste">
                        <i class="fas fa-recycle"></i>
                    </div>
                    <div class="stat-content">
                        <h3 id="wasteReduced">85%</h3>
                        <p>Waste Reduced</p>
                        <span class="stat-change positive">+5% this week</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon energy">
                        <i class="fas fa-bolt"></i>
                    </div>
                    <div class="stat-content">
                        <h3 id="energySaved">120 kWh</h3>
                        <p>Energy Saved</p>
                        <span class="stat-change positive">+8% this month</span>
                    </div>
                </div>
            </section>

            <!-- Quick Actions -->
            <section class="quick-actions">
                <h2>Quick Actions</h2>
                <div class="actions-grid">
                    <button class="action-btn transport" onclick="logActivity('transport')">
                        <i class="fas fa-bus"></i>
                        <span>Eco Transport</span>
                    </button>
                    <button class="action-btn food" onclick="logActivity('food')">
                        <i class="fas fa-apple-alt"></i>
                        <span>Sustainable Food</span>
                    </button>
                    <button class="action-btn energy" onclick="logActivity('energy')">
                        <i class="fas fa-solar-panel"></i>
                        <span>Energy Saving</span>
                    </button>
                    <button class="action-btn waste" onclick="logActivity('waste')">
                        <i class="fas fa-trash-alt"></i>
                        <span>Waste Sorting</span>
                    </button>
                </div>
            </section>

            <!-- Recent Activities -->
            <section class="recent-activities">
                <h2>Recent Activities</h2>
                <div class="activities-list">
                    <c:choose>
                        <c:when test="${not empty recentActivities}">
                            <c:forEach var="activity" items="${recentActivities}">
                                <div class="activity-item ${activity.type}">
                                    <div class="activity-icon">
                                        <c:choose>
                                            <c:when test="${activity.type == 'transport'}">
                                                <i class="fas fa-bus"></i>
                                            </c:when>
                                            <c:when test="${activity.type == 'food'}">
                                                <i class="fas fa-apple-alt"></i>
                                            </c:when>
                                            <c:when test="${activity.type == 'energy'}">
                                                <i class="fas fa-bolt"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fas fa-recycle"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="activity-content">
                                        <h4>${activity.description}</h4>
                                        <p><fmt:formatDate value="${activity.createdAt}" pattern="MMM dd, yyyy HH:mm"/></p>
                                    </div>
                                    <div class="activity-reward">
                                        <span class="coins">+${activity.coinsEarned}₡</span>
                                        <span class="carbon">${activity.carbonImpact} kg CO₂</span>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <i class="fas fa-seedling"></i>
                                <p>No activities yet. Start your sustainability journey!</p>
                                <button class="btn btn-primary" onclick="logActivity('transport')">
                                    Log Your First Activity
                                </button>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </section>
        </main>
    </div>

    <!-- Activity Logger Modal -->
    <div id="activityModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Log Sustainable Activity</h3>
                <button class="close-modal" onclick="closeModal()">&times;</button>
            </div>
            <form id="activityForm" class="activity-form">
                <div class="form-group">
                    <label for="activityType">Activity Type</label>
                    <select id="activityType" name="type" required>
                        <option value="transport">Eco Transport</option>
                        <option value="food">Sustainable Food</option>
                        <option value="energy">Energy Saving</option>
                        <option value="waste">Waste Management</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="activityDescription">Description</label>
                    <textarea id="activityDescription" name="description" 
                              placeholder="Describe your sustainable action..." required></textarea>
                </div>
                <div class="form-actions">
                    <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Log Activity</button>
                </div>
            </form>
        </div>
    </div>

    <script src="../js/dashboard.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.2.1/dist/chart.min.js"></script>
</body>
</html>