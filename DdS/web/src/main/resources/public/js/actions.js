document.addEventListener('DOMContentLoaded', () => {
    // Delegación: maneja clicks en cualquier grupo de botones
    document.body.addEventListener('click', (e) => {
        const btn = e.target.closest('.icon-btn, .action-button');
        if (!btn) return;

        const group = btn.closest('.action-buttons-group');
        if (!group) return;

        const hechoId = group.dataset.hechoId;
        const action = btn.dataset.action;

        if (!hechoId || !action) return;

        if (action === 'share') {
            copyHechoLinkToClipboard(hechoId, btn);
        } else if (action === 'edit') {
            // Redirige a la página de solicitud de modificación (GET)
            //////////////////acaaaaaaaaaaaaa
            window.location.href = `${window.location.origin}/hechos/${encodeURIComponent(hechoId)}/editar`;
        } else if (action === 'delete') {
            // Redirige a la página de solicitud de eliminación (GET)
            // aacaaaaaaaaaaaaaaaaaaaa
            window.location.href = `${window.location.origin}/hechos/${encodeURIComponent(hechoId)}/eliminar`;
        }
    });
});

// Copia al portapapeles la URL del hecho y muestra un pequeño toast de confirmación.
function copyHechoLinkToClipboard(hechoId, button) {
    const url = `${window.location.origin}/hecho/${encodeURIComponent(hechoId)}`;

    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(url).then(() => {
            showToast('Enlace copiado al portapapeles');
            // Opcional: cambio visual breve en el botón
            flashButton(button);
        }).catch(err => {
            // fallback
            fallbackCopyTextToClipboard(url, button);
        });
    } else {
        fallbackCopyTextToClipboard(url, button);
    }
}

// Fallback: crea input temporal y copia
function fallbackCopyTextToClipboard(text, button) {
    const input = document.createElement('textarea');
    input.value = text;
    input.setAttribute('readonly', '');
    input.style.position = 'absolute';
    input.style.left = '-9999px';
    document.body.appendChild(input);
    input.select();
    try {
        document.execCommand('copy');
        showToast('Enlace copiado al portapapeles');
        flashButton(button);
    } catch (err) {
        showToast('No se pudo copiar el enlace', true);
    }
    document.body.removeChild(input);
}

// Mensaje transitorio en pantalla
function showToast(message, isError = false) {
    let toast = document.getElementById('site-toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'site-toast';
        toast.className = 'site-toast';
        document.body.appendChild(toast);
    }
    toast.textContent = message;
    toast.classList.toggle('error', isError);
    toast.classList.add('visible');
    clearTimeout(toast.hideTimeout);
    toast.hideTimeout = setTimeout(() => {
        toast.classList.remove('visible');
    }, 2200);
}

// Efecto visual breve en el botón
function flashButton(button) {
    if (!button) return;
    button.classList.add('flash');
    setTimeout(() => button.classList.remove('flash'), 700);
}