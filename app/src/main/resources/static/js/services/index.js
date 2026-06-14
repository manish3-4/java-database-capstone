import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";
import { selectRole } from "../render.js";

const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor/login";

window.onload = function () {

    const adminBtn = document.getElementById("adminLogin");
    const doctorBtn = document.getElementById("doctorLogin");

    if (adminBtn) {
        adminBtn.addEventListener("click", () => {
            openModal("adminLogin");
        });
    }

    if (doctorBtn) {
        doctorBtn.addEventListener("click", () => {
            openModal("doctorLogin");
        });
    }
};

/* -------------------- Admin Login -------------------- */

window.adminLoginHandler = async function () {

    const username = document.getElementById("adminUsername").value;
    const password = document.getElementById("adminPassword").value;

    const admin = {
        username,
        password
    };

    try {

        const response = await fetch(ADMIN_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(admin)
        });

        if (response.ok) {

            const data = await response.json();

            localStorage.setItem("token", data.token);

            selectRole("admin");

            window.location.href = "/admin/dashboard";

        } else {
            alert("Invalid credentials!");
        }

    } catch (error) {

        console.error(error);
        alert("Something went wrong. Please try again.");
    }
};

/* -------------------- Doctor Login -------------------- */

window.doctorLoginHandler = async function () {

    const email = document.getElementById("doctorEmail").value;
    const password = document.getElementById("doctorPassword").value;

    const doctor = {
        email,
        password
    };

    try {

        const response = await fetch(DOCTOR_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {

            const data = await response.json();

            localStorage.setItem("token", data.token);

            selectRole("doctor");

            window.location.href = "/doctor/dashboard";

        } else {
            alert("Invalid credentials!");
        }

    } catch (error) {

        console.error(error);
        alert("Something went wrong. Please try again.");
    }
};