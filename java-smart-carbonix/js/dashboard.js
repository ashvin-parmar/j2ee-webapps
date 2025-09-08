// Smart Carbonix Dashboard JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Initialize dashboard
    initializeDashboard();
    loadDashboardStats();
    
    // Set up event listeners
    setupEventListeners();
});

function initializeDashboard() {
    // Add loading states
    showLoadingStates();
    
    // Initialize any charts or interactive elements
    console.log('Dashboard initialized');
}

function setupEventListeners() {
    // Activity form submission
    const activityForm = document.getElementById('activityForm');
    if (activityForm) {
        activityForm.addEventListener('submit', handleActivitySubmission);
    }
    
    // Close modal events
    const closeButtons = document.querySelectorAll('.close-modal');
    closeButtons.forEach(button => {
        button.addEventListener('click', closeModal);
    });
    
    // Close modal on backdrop click
    const modal = document.getElementById('activityModal');
    if (modal) {
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                closeModal();
            }
        });
    }
}

function showLoadingStates() {
    const statCards = document.querySelectorAll('.stat-card h3');
    statCards.forEach(card => {
        card.style.opacity = '0.5';
        card.textContent = 'Loading...';
    });
}

function loadDashboardStats() {
    // Make AJAX request to get dashboard stats
    fetch('dashboard', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'action=getStats'
    })
    .then(response => response.json())
    .then(data => {
        updateDashboardStats(data);
    })
    .catch(error => {
        console.error('Error loading dashboard stats:', error);
        showStatsError();
    });
}

function updateDashboardStats(data) {
    // Update carbon footprint
    const carbonElement = document.getElementById('carbonFootprint');
    if (carbonElement && data.carbonFootprint !== undefined) {
        carbonElement.textContent = data.carbonFootprint.toFixed(1) + ' kg';
        carbonElement.style.opacity = '1';
    }
    
    // Update coins earned
    const coinsElement = document.getElementById('coinsEarned');
    if (coinsElement && data.coinsEarned !== undefined) {
        coinsElement.textContent = data.coinsEarned.toLocaleString();
        coinsElement.style.opacity = '1';
    }
    
    // Update waste reduced
    const wasteElement = document.getElementById('wasteReduced');
    if (wasteElement && data.wasteReduced !== undefined) {
        wasteElement.textContent = Math.round(data.wasteReduced) + '%';
        wasteElement.style.opacity = '1';
    }
    
    // Update energy saved
    const energyElement = document.getElementById('energySaved');
    if (energyElement && data.energySaved !== undefined) {
        energyElement.textContent = data.energySaved + ' kWh';
        energyElement.style.opacity = '1';
    }
}

function showStatsError() {
    const statCards = document.querySelectorAll('.stat-card h3');
    statCards.forEach(card => {
        card.style.opacity = '1';
        card.textContent = 'Error';
        card.style.color = 'var(--error-red)';
    });
}

function logActivity(type) {
    const modal = document.getElementById('activityModal');
    const typeSelect = document.getElementById('activityType');
    
    if (modal && typeSelect) {
        // Pre-select the activity type
        typeSelect.value = type;
        
        // Show modal
        modal.classList.add('show');
        
        // Focus on description field
        const descriptionField = document.getElementById('activityDescription');
        if (descriptionField) {
            setTimeout(() => {
                descriptionField.focus();
            }, 300);
        }
    }
}

function closeModal() {
    const modal = document.getElementById('activityModal');
    if (modal) {
        modal.classList.remove('show');
        
        // Reset form
        const form = document.getElementById('activityForm');
        if (form) {
            form.reset();
        }
    }
}

function handleActivitySubmission(event) {
    event.preventDefault();
    
    const form = event.target;
    const formData = new FormData(form);
    const submitButton = form.querySelector('button[type="submit"]');
    
    // Show loading state
    if (submitButton) {
        submitButton.disabled = true;
        submitButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Logging...';
    }
    
    // Convert FormData to URLSearchParams
    const params = new URLSearchParams();
    for (let [key, value] of formData) {
        params.append(key, value);
    }
    params.append('action', 'logActivity');
    
    // Submit activity
    fetch('activities', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: params.toString()
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Show success message
            showSuccessMessage('Activity logged successfully! +' + data.coinsEarned + ' EcoCoins earned');
            
            // Close modal
            closeModal();
            
            // Refresh dashboard stats
            loadDashboardStats();
            
            // Optionally reload the page to show new activity
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        } else {
            showErrorMessage(data.message || 'Failed to log activity');
        }
    })
    .catch(error => {
        console.error('Error logging activity:', error);
        showErrorMessage('Failed to log activity. Please try again.');
    })
    .finally(() => {
        // Reset button state
        if (submitButton) {
            submitButton.disabled = false;
            submitButton.innerHTML = '<i class="fas fa-plus"></i> Log Activity';
        }
    });
}

function showSuccessMessage(message) {
    showNotification(message, 'success');
}

function showErrorMessage(message) {
    showNotification(message, 'error');
}

function showNotification(message, type) {
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
        <span>${message}</span>
        <button class="notification-close" onclick="this.parentElement.remove()">&times;</button>
    `;
    
    // Add notification styles if not already present
    if (!document.querySelector('#notification-styles')) {
        const styles = document.createElement('style');
        styles.id = 'notification-styles';
        styles.textContent = `
            .notification {
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 1rem 1.5rem;
                border-radius: 0.5rem;
                box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
                display: flex;
                align-items: center;
                gap: 0.5rem;
                z-index: 1001;
                animation: slideInRight 0.3s ease;
                max-width: 400px;
            }
            
            .notification-success {
                background-color: #f0fdf4;
                color: #16a34a;
                border: 1px solid #bbf7d0;
            }
            
            .notification-error {
                background-color: #fef2f2;
                color: #ef4444;
                border: 1px solid #fecaca;
            }
            
            .notification-close {
                background: none;
                border: none;
                font-size: 1.25rem;
                cursor: pointer;
                margin-left: auto;
                opacity: 0.7;
                transition: opacity 0.2s;
            }
            
            .notification-close:hover {
                opacity: 1;
            }
            
            @keyframes slideInRight {
                from {
                    transform: translateX(100%);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
        `;
        document.head.appendChild(styles);
    }
    
    // Add to document
    document.body.appendChild(notification);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.remove();
        }
    }, 5000);
}

// Sidebar toggle for mobile
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        sidebar.classList.toggle('open');
    }
}

// Add mobile menu button if not present
function addMobileMenuButton() {
    if (window.innerWidth <= 768 && !document.querySelector('.mobile-menu-btn')) {
        const menuButton = document.createElement('button');
        menuButton.className = 'mobile-menu-btn';
        menuButton.innerHTML = '<i class="fas fa-bars"></i>';
        menuButton.onclick = toggleSidebar;
        
        const header = document.querySelector('.content-header');
        if (header) {
            header.insertBefore(menuButton, header.firstChild);
        }
        
        // Add mobile menu button styles
        const styles = document.createElement('style');
        styles.textContent = `
            .mobile-menu-btn {
                display: none;
                background: var(--primary-green);
                color: white;
                border: none;
                padding: 0.5rem;
                border-radius: 0.25rem;
                margin-bottom: 1rem;
                cursor: pointer;
            }
            
            @media (max-width: 768px) {
                .mobile-menu-btn {
                    display: block;
                }
            }
        `;
        document.head.appendChild(styles);
    }
}

// Handle responsive behavior
function handleResize() {
    addMobileMenuButton();
}

// Set up resize listener
window.addEventListener('resize', handleResize);
window.addEventListener('load', handleResize);

// Utility functions
function formatNumber(num) {
    if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'K';
    }
    return num.toLocaleString();
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
    });
}

// Export functions for global access
window.logActivity = logActivity;
window.closeModal = closeModal;
window.toggleSidebar = toggleSidebar;