import { API_BASE_URL } from "../config/config.js";

const PATIENT_API = API_BASE_URL + "/patient";

/**
 * Patient Signup
 */
export async function patientSignup(data) {

    try {

        const response = await fetch(`${PATIENT_API}/signup`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        const result = await response.json();

        return {
            success: response.ok,
            message: result.message
        };

    } catch (error) {

        console.error("Signup Error:", error);

        return {
            success: false,
            message: "Unable to complete signup"
        };
    }
}

/**
 * Patient Login
 */
export async function patientLogin(data) {

    try {

        console.log("Patient Login:", data);

        return await fetch(`${PATIENT_API}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

    } catch (error) {

        console.error("Login Error:", error);
        throw error;
    }
}

/**
 * Get Logged-in Patient Data
 */
export async function getPatientData(token) {

    try {

        const response = await fetch(`${PATIENT_API}/profile`, {
            method: "GET",
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error("Unable to fetch patient");
        }

        return await response.json();

    } catch (error) {

        console.error("Patient Data Error:", error);
        return null;
    }
}

/**
 * Get Patient Appointments
 */
export async function getPatientAppointments(id, token, user) {

    try {

        const response = await fetch(
            `${PATIENT_API}/${user}/appointments/${id}`,
            {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        if (!response.ok) {
            throw new Error("Unable to fetch appointments");
        }

        return await response.json();

    } catch (error) {

        console.error("Appointments Error:", error);
        return null;
    }
}

/**
 * Filter Appointments
 */
export async function filterAppointments(
    condition,
    name,
    token
) {

    try {

        const queryParams = new URLSearchParams();

        if (condition) {
            queryParams.append("condition", condition);
        }

        if (name) {
            queryParams.append("name", name);
        }

        const response = await fetch(
            `${PATIENT_API}/appointments/filter?${queryParams.toString()}`,
            {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`
                }
            }
        );

        if (!response.ok) {
            throw new Error("Unable to filter appointments");
        }

        return await response.json();

    } catch (error) {

        console.error("Filter Appointments Error:", error);
        alert("Something went wrong while filtering appointments.");

        return [];
    }
}