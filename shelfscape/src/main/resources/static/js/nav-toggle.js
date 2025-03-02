document.addEventListener("DOMContentLoaded", function () {
    const navToggleBtn = document.querySelector(".mobile-nav-toggle");
    const nav = document.querySelector(".main-nav");

    navToggleBtn.addEventListener("click", function () {
        nav.classList.toggle("active");

        document.body.classList.toggle("no-scroll")
        navToggleBtn.classList.toggle("rotate")
    });
});