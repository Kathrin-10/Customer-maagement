function formatDate(dateValue) {
    if (!dateValue) {
        return "-";
    }

    const dateString = String(dateValue).substring(0, 10);
    const parts = dateString.split("-");

    if (parts.length !== 3) {
        return dateValue;
    }

    const [year, month, day] = parts;

    return `${day} / ${month} / ${year}`;
}