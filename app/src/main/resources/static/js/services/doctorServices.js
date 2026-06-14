import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";

/**
 * Get all doctors
 */
export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API);

        if (!response.ok) {
            throw new Error("Failed to fetch doctors");
        }

        const doctors = await response.json();
        return doctors;

    } catch (error) {
        console.error("Error fetching doctors:", error);
        return [];
    }
}

/**
 * Delete doctor
 */
export async function deleteDoctor(id, token) {
    try {

        const response = await fetch(`${DOCTOR_API}/${id}`, {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${token}`
            }
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Doctor deleted successfully"
        };

    } catch (error) {

        console.error("Delete doctor error:", error);

        return {
            success: false,
            message: "Failed to delete doctor"
        };
    }
}

/**
 * Save doctor
 */
export async function saveDoctor(doctor, token) {
    try {

        const response = await fetch(DOCTOR_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Doctor added successfully"
        };

    } catch (error) {

        console.error("Save doctor error:", error);

        return {
            success: false,
            message: "Failed to add doctor"
        };
    }
}

/**
 * Filter doctors
 */
export async function filterDoctors(name, time, specialty) {
    try {

        const queryParams = new URLSearchParams();

        if (name) queryParams.append("name", name);
        if (time) queryParams.append("time", time);
        if (specialty) queryParams.append("specialty", specialty);

        const response = await fetch(
            `${DOCTOR_API}/filter?${queryParams.toString()}`
        );

        if (!response.ok) {
            throw new Error("Failed to filter doctors");
        }

        return await response.json();

    } catch (error) {

        console.error("Filter doctors error:", error);
        alert("Unable to filter doctors");

        return [];
    }
}