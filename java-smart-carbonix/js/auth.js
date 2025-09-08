// Smart Carbonix Authentication JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeAuthPage();
    setupFormValidation();
});

function initializeAuthPage() {
    // Add smooth animations to form elements
    const formGroups = document.querySelectorAll('.form-group');
    formGroups.forEach((group, index) => {
        group.style.opacity = '0';
        group.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            group.style.transition = 'all 0.5s ease';
            group.style.opacity = '1';
            group.style.transform = 'translateY(0)';
        }, index * 150);
    });
    
    // Add floating background animations
    animateBackgroundIcons();
    
    console.log('Authentication page initialized');
}

function setupFormValidation() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleFormSubmission);
        
        // Add real-time validation
        const inputs = loginForm.querySelectorAll('input[required]');
        inputs.forEach(input => {
            input.addEventListener('blur', validateField);
            input.addEventListener('input', clearFieldError);
        });
    }
}

function handleFormSubmission(event) {
    event.preventDefault();
    
    const form = event.target;
    const submitButton = form.querySelector('button[type="submit"]');
    
    // Validate form
    if (!validateForm(form)) {
        return;
    }
    
    // Show loading state
    if (submitButton) {
        submitButton.disabled = true;
        submitButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Getting Started...';
    }
    
    // Submit form after a brief delay for UX
    setTimeout(() => {
        form.submit();
    }, 500);
}

function validateForm(form) {
    const inputs = form.querySelectorAll('input[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!validateField({ target: input })) {
            isValid = false;
        }
    });
    
    return isValid;
}

function validateField(event) {
    const input = event.target;
    const value = input.value.trim();
    const fieldName = input.name;
    
    // Remove existing error
    clearFieldError(event);
    
    // Validation rules
    let isValid = true;
    let errorMessage = '';
    
    if (!value) {
        isValid = false;
        errorMessage = `${getFieldDisplayName(fieldName)} is required`;
    } else {
        switch (fieldName) {
            case 'email':
                if (!isValidEmail(value)) {
                    isValid = false;
                    errorMessage = 'Please enter a valid email address';
                }
                break;
            case 'firstName':
            case 'lastName':
                if (value.length < 2) {
                    isValid = false;
                    errorMessage = `${getFieldDisplayName(fieldName)} must be at least 2 characters`;
                }
                if (!/^[a-zA-Z\s-']+$/.test(value)) {
                    isValid = false;
                    errorMessage = `${getFieldDisplayName(fieldName)} can only contain letters, spaces, hyphens, and apostrophes`;
                }
                break;
        }
    }
    
    if (!isValid) {
        showFieldError(input, errorMessage);
    }
    
    return isValid;
}

function clearFieldError(event) {
    const input = event.target;
    const formGroup = input.closest('.form-group');
    const existingError = formGroup.querySelector('.field-error');
    
    if (existingError) {
        existingError.remove();
    }
    
    input.classList.remove('error');
}

function showFieldError(input, message) {
    const formGroup = input.closest('.form-group');
    
    // Create error element
    const errorElement = document.createElement('div');
    errorElement.className = 'field-error';
    errorElement.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
    
    // Add error styles if not already present
    if (!document.querySelector('#field-error-styles')) {
        const styles = document.createElement('style');
        styles.id = 'field-error-styles';
        styles.textContent = `
            .field-error {
                color: var(--error-red);
                font-size: var(--font-size-sm);
                margin-top: var(--spacing-xs);
                display: flex;
                align-items: center;
                gap: var(--spacing-xs);
                animation: shake 0.5s ease;
            }
            
            .form-group input.error {
                border-color: var(--error-red);
                background-color: #fef2f2;
            }
            
            @keyframes shake {
                0%, 100% { transform: translateX(0); }
                25% { transform: translateX(-5px); }
                75% { transform: translateX(5px); }
            }
        `;
        document.head.appendChild(styles);
    }
    
    // Add error to form group
    formGroup.appendChild(errorElement);
    input.classList.add('error');
}

function getFieldDisplayName(fieldName) {
    const displayNames = {
        email: 'Email address',
        firstName: 'First name',
        lastName: 'Last name'
    };
    return displayNames[fieldName] || fieldName;
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function animateBackgroundIcons() {
    const icons = document.querySelectorAll('.eco-icon');
    
    icons.forEach((icon, index) => {
        // Set random initial positions
        const x = Math.random() * 100;
        const y = Math.random() * 100;
        const delay = Math.random() * 5;
        
        icon.style.left = x + '%';
        icon.style.top = y + '%';
        icon.style.animationDelay = delay + 's';
        
        // Add floating animation
        icon.style.animation = 'float 10s ease-in-out infinite';
    });
    
    // Add floating animation styles
    if (!document.querySelector('#floating-animation-styles')) {
        const styles = document.createElement('style');
        styles.id = 'floating-animation-styles';
        styles.textContent = `
            .eco-icon {
                position: absolute;
                font-size: 2rem;
                color: rgba(34, 197, 94, 0.1);
                pointer-events: none;
                transition: color 0.3s ease;
            }
            
            @keyframes float {
                0%, 100% {
                    transform: translateY(0) rotate(0deg);
                }
                25% {
                    transform: translateY(-20px) rotate(5deg);
                }
                50% {
                    transform: translateY(-10px) rotate(-3deg);
                }
                75% {
                    transform: translateY(-15px) rotate(2deg);
                }
            }
            
            .bg-pattern:hover .eco-icon {
                color: rgba(34, 197, 94, 0.3);
                animation-duration: 3s;
            }
        `;
        document.head.appendChild(styles);
    }
}

// Add smooth scrolling for auth features
function setupSmoothScrolling() {
    const featureItems = document.querySelectorAll('.feature-item');
    featureItems.forEach((item, index) => {
        item.style.opacity = '0';
        item.style.transform = 'translateY(10px)';
        
        setTimeout(() => {
            item.style.transition = 'all 0.3s ease';
            item.style.opacity = '1';
            item.style.transform = 'translateY(0)';
        }, 1000 + (index * 200));
    });
}

// Initialize smooth scrolling after page load
window.addEventListener('load', setupSmoothScrolling);

// Handle form field focus effects
document.addEventListener('focusin', function(event) {
    if (event.target.tagName === 'INPUT' || event.target.tagName === 'TEXTAREA') {
        const formGroup = event.target.closest('.form-group');
        if (formGroup) {
            formGroup.classList.add('focused');
        }
    }
});

document.addEventListener('focusout', function(event) {
    if (event.target.tagName === 'INPUT' || event.target.tagName === 'TEXTAREA') {
        const formGroup = event.target.closest('.form-group');
        if (formGroup) {
            formGroup.classList.remove('focused');
        }
    }
});

// Add focus effect styles
if (!document.querySelector('#focus-effect-styles')) {
    const styles = document.createElement('style');
    styles.id = 'focus-effect-styles';
    styles.textContent = `
        .form-group.focused {
            transform: scale(1.02);
            transition: transform 0.2s ease;
        }
        
        .form-group.focused label {
            color: var(--primary-green);
            transition: color 0.2s ease;
        }
    `;
    document.head.appendChild(styles);
}