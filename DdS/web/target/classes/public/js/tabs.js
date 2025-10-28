function showTab(tabId, button) {
    const contents = document.querySelectorAll('.tab-content');
    const buttons = document.querySelectorAll('.tabs-nav button');

    contents.forEach(content => content.classList.remove('active'));
    buttons.forEach(btn => btn.classList.remove('active'));

    const target = document.getElementById(tabId);
    if (target) target.classList.add('active');
    if (button) button.classList.add('active');
}

document.addEventListener('DOMContentLoaded', () => {
    // Inicia con la pestaña Descripción activa
    const descButton = document.querySelector('[data-tab="description"]');
    if (descButton) showTab('description-content', descButton);
});