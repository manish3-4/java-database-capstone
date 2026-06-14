import { openModal } from "./components/modals.js";
import {
    getDoctors,
    filterDoctors,
    saveDoctor
} from "./services/doctorServices.js";

import { createDoctorCard } from "./components/doctorCard.js";

document.addEventListener("DOMContentLoaded", () => {

    loadDoctorCards();

    const addBtn = document.getElementById("addDocBtn");

    if (addBtn) {
        addBtn.addEventListener("click", () => {
            openModal("addDoctor");
        });
    }

    document.getElementById("searchBar")
        ?.addEventListener("input", filterDoctorsOnChange);

    document.getElementById("filterTime")
        ?.addEventListener("change", filterDoctorsOnChange);

    document.getElementById("filterSpecialty")
        ?.addEventListener("change", filterDoctorsOnChange);
});

async function loadDoctorCards() {

    const doctors = await getDoctors();

    renderDoctorCards(doctors);
}

function renderDoctorCards(doctors) {

    const contentDiv = document.getElementById("content");

    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found</p>";
        return;
    }

    doctors.forEach((doctor) => {
        contentDiv.appendChild(createDoctorCard(doctor));
    });
}

async function filterDoctorsOnChange() {

    const name =
        document.getElementById("searchBar").value;

    const time =
        document.getElementById("filterTime").value;

    const specialty =
        document.getElementById("filterSpecialty").value;

    const doctors = await filterDoctors(
        name,
        time,
        specialty
    );

    renderDoctorCards(doctors);
}

window.adminAddDoctor = async function () {

    const token = localStorage.getItem("token");

    if (!token) {
        alert("Session expired. Please login again.");
        return;
    }

    const availability = [];

    document
        .querySelectorAll(".availability-checkbox:checked")
        .forEach((item) => {
            availability.push(item.value);
        });

    const doctor = {
        name: document.getElementById("doctorName").value,
        specialization: document.getElementById("doctorSpecialty").value,
        email: document.getElementById("doctorEmail").value,
        password: document.getElementById("doctorPassword").value,
        mobileNo: document.getElementById("doctorMobile").value,
        availability
    };

    const response = await saveDoctor(
        doctor,
        token
    );

    if (response.success) {

        alert(response.message);

        document.getElementById("modal").style.display = "none";

        await loadDoctorCards();

    } else {
        alert(response.message);
    }
};