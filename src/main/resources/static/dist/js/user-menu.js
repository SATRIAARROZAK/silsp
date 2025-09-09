document.addEventListener('DOMContentLoaded', function() {
    // Initialize all dropdowns
    const dropdowns = document.querySelectorAll('.dropdown-toggle');
    dropdowns.forEach(dropdown => {
        new bootstrap.Dropdown(dropdown);
    });

    // Optional: Close dropdown when clicking outside
    document.addEventListener('click', function(event) {
        const userMenu = document.querySelector('.user-menu');
        if (!userMenu.contains(event.target)) {
            const dropdownMenu = userMenu.querySelector('.dropdown-menu');
            const dropdown = bootstrap.Dropdown.getInstance(userMenu.querySelector('.dropdown-toggle'));
            if (dropdown && dropdownMenu.classList.contains('show')) {
                dropdown.hide();
            }
        }
    });

    // Optional: Update user image on profile photo change
    function updateUserImage(newImageUrl) {
        document.getElementById('navbar-user-image').src = newImageUrl;
        document.getElementById('dropdown-user-image').src = newImageUrl;
    }

    // Optional: Add hover effect
    const userMenuDropdown = document.querySelector('.user-menu');
    if (userMenuDropdown) {
        userMenuDropdown.addEventListener('mouseenter', function() {
            this.querySelector('.dropdown-toggle').click();
        });
    }
});