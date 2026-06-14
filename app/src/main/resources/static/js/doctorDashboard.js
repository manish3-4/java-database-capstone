import { getAllAppointments }
from "./services/appointmentRecordService.js";

import { createPatientRow }
from "./components/patientRows.js";

const patientTableBody =
    document.getElementById("patientTableBody");

let selectedDate =
    new Date().toISOString().split("T")[0];

let token =
    localStorage.getItem("token");

let patientName = null;

document.addEventListener("DOMContentLoaded", () => {

    const searchBar =
        document.getElementById("searchBar");

    const todayButton =
        document.getElementById("todayButton");

    const datePicker =
        document.getElementById("datePicker");

    if (searchBar) {
        searchBar.addEventListener("input", () => {

            patientName =
                searchBar.value.trim() || "null";

            loadAppointments();
        });
    }

    if (todayButton) {
        todayButton.addEventListener("click", () => {

            selectedDate =
                new Date().toISOString().split("T")[0];

            datePicker.value = selectedDate;

            loadAppointments();
        });
    }

    if (datePicker) {

        datePicker.value = selectedDate;

        datePicker.addEventListener("change", () => {

            selectedDate = datePicker.value;

            loadAppointments();
        });
    }

    loadAppointments();
});

async function loadAppointments() {

    try {

        const appointments =
            await getAllAppointments(
                selectedDate,
                patientName,
                token
            );

        patientTableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {

            patientTableBody.innerHTML = `
                <tr>
                    <td colspan="5">
                        No Appointments found for today
                    </td>
                </tr>
            `;

            return;
        }

        appointments.forEach((appointment) => {

            const row =
                createPatientRow(appointment);

            patientTableBody.appendChild(row);
        });

    } catch (error) {

        console.error(error);

        patientTableBody.innerHTML = `
            <tr>
                <td colspan="5">
                    Failed to load appointments
                </td>
            </tr>
        `;
    }
}