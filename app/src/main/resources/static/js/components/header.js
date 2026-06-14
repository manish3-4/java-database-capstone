// header.js

function renderHeader() {
    const headerDiv = document.getElementById("header");

    if (!headerDiv) return;

    // Clear session on landing page
    if (
        window.location.pathname === "/" ||
        window.location.pathname.endsWith("/")
    ) {
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    // Validate logged-in users
    if (
        (role === "loggedPatient" ||
            role === "admin" ||
            role === "doctor") &&
        !token
    ) {
        localStorage.removeItem("userRole");

        alert("Session expired or invalid login. Please log in again.");

        window.location.href = "/";
        return;
    }

    let headerContent = `
        <header class="header">
            <div class="logo">
                <h2>Clinic Management System</h2>
            </div>
            <nav class="nav-links">
    `;

    if (role === "admin") {
        headerContent += `
            <button id="addDocBtn" class="adminBtn">
                Add Doctor
            </button>

            <a href="#" id="logoutBtn">
                Logout
            </a>
        `;
    }

    else if (role === "doctor") {
        headerContent += `
            <a href="/doctor/dashboard">
                Home
            </a>

            <a href="#" id="logoutBtn">
                Logout
            </a>
        `;
    }

    else if (role === "patient") {
        headerContent += `
            <button id="loginBtn" class="adminBtn">
                Login
            </button>

            <button id="signupBtn" class="adminBtn">
                Sign Up
            </button>
        `;
    }

    else if (role === "loggedPatient") {
        headerContent += `
            <a href="/pages/patientDashboard.html">
                Home
            </a>

            <a href="#" id="appointmentBtn">
                Appointments
            </a>

            <a href="#" id="logoutPatientBtn">
                Logout
            </a>
        `;
    }

    headerContent += `
            </nav>
        </header>
    `;

    headerDiv.innerHTML = headerContent;

    attachHeaderButtonListeners();
}

function attachHeaderButtonListeners() {

    const addDocBtn = document.getElementById("addDocBtn");

    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            if (typeof openModal === "function") {
                openModal("addDoctor");
            }
        });
    }

    const loginBtn = document.getElementById("loginBtn");

    if (loginBtn) {
        loginBtn.addEventListener("click", () => {
            if (typeof openModal === "function") {
                openModal("login");
            }
        });
    }

    const signupBtn = document.getElementById("signupBtn");

    if (signupBtn) {
        signupBtn.addEventListener("click", () => {
            if (typeof openModal === "function") {
                openModal("signup");
            }
        });
    }

    const appointmentBtn = document.getElementById("appointmentBtn");

    if (appointmentBtn) {
        appointmentBtn.addEventListener("click", () => {
            window.location.href = "/pages/appointments.html";
        });
    }

    const logoutBtn = document.getElementById("logoutBtn");

    if (logoutBtn) {
        logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logout();
        });
    }

    const logoutPatientBtn = document.getElementById("logoutPatientBtn");

    if (logoutPatientBtn) {
        logoutPatientBtn.addEventListener("click", (e) => {
            e.preventDefault();
            logoutPatient();
        });
    }
}

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");

    window.location.href = "/";
}

function logoutPatient() {
    localStorage.removeItem("token");

    localStorage.setItem("userRole", "patient");

    window.location.href = "/pages/patientDashboard.html";
}

document.addEventListener("DOMContentLoaded", () => {
    renderHeader();
});

window.renderHeader = renderHeader;
window.logout = logout;
window.logoutPatient = logoutPatient;