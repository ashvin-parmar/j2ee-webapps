<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Smart Carbonix</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body class="auth-page">
    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-header">
                <div class="auth-logo">
                    <i class="fas fa-leaf"></i>
                    <h1>Smart Carbonix</h1>
                </div>
                <p class="auth-subtitle">Join the sustainability revolution</p>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${error}
                </div>
            </c:if>

            <form class="auth-form" action="auth" method="post" id="loginForm">
                <input type="hidden" name="action" value="login">
                
                <div class="form-group">
                    <label for="email">
                        <i class="fas fa-envelope"></i>
                        Email Address
                    </label>
                    <input type="email" id="email" name="email" required 
                           placeholder="Enter your email">
                </div>

                <div class="form-group">
                    <label for="firstName">
                        <i class="fas fa-user"></i>
                        First Name
                    </label>
                    <input type="text" id="firstName" name="firstName" required 
                           placeholder="Enter your first name">
                </div>

                <div class="form-group">
                    <label for="lastName">
                        <i class="fas fa-user"></i>
                        Last Name
                    </label>
                    <input type="text" id="lastName" name="lastName" required 
                           placeholder="Enter your last name">
                </div>

                <button type="submit" class="btn btn-primary btn-full">
                    <i class="fas fa-sign-in-alt"></i>
                    Get Started
                </button>
            </form>

            <div class="auth-footer">
                <p>By continuing, you agree to our sustainability mission</p>
                <div class="auth-features">
                    <div class="feature-item">
                        <i class="fas fa-coins"></i>
                        <span>Earn EcoCoins</span>
                    </div>
                    <div class="feature-item">
                        <i class="fas fa-chart-line"></i>
                        <span>Track Impact</span>
                    </div>
                    <div class="feature-item">
                        <i class="fas fa-trophy"></i>
                        <span>Unlock Achievements</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="auth-background">
            <div class="bg-pattern">
                <div class="eco-icon"><i class="fas fa-leaf"></i></div>
                <div class="eco-icon"><i class="fas fa-recycle"></i></div>
                <div class="eco-icon"><i class="fas fa-solar-panel"></i></div>
                <div class="eco-icon"><i class="fas fa-wind"></i></div>
                <div class="eco-icon"><i class="fas fa-seedling"></i></div>
            </div>
        </div>
    </div>

    <script src="js/auth.js"></script>
</body>
</html>
