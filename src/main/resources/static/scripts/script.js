// Initialize TomSelect for policy holder selection
const policyHolder = document.querySelector("#policyHolderId");

if (policyHolder) {
    new TomSelect("#policyHolderId", {
        create: false,
        sortField: {field: "text", direction: "asc"}
    });
}

// Initialize TomSelect for insured person selection
const insuredPerson = document.querySelector("#insuredPersonId");

if (insuredPerson) {
    new TomSelect("#insuredPersonId", {
        create: false,
        sortField: {field: "text", direction: "asc"}
    });
}

// Initialize TomSelect for insurance type selection
const insuranceType = document.querySelector("#insuranceType");

if (insuranceType) {
    new TomSelect("#insuranceType", {
        create: false,
        sortField: {field: "text", direction: "asc"}
    });
}

// Flash messages
document.querySelectorAll(".flash-message").forEach(message => {

    const closeButton = message.querySelector(".flash-close");

    const fadeOut = () => {
        message.classList.add("fade-out");

        message.addEventListener("transitionend", () => {
            message.remove();
        }, {once: true});
    };

    if (closeButton) {
        closeButton.addEventListener("click", fadeOut);
    }

    setTimeout(fadeOut, 4000);
});