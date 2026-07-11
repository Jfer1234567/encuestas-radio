let contadorOpciones = 2;

// Función para agregar más campos al crear encuestas
function agregarOpcion() {
    contadorOpciones++;
    const contenedor = document.getElementById('contenedor-opciones');

    const nuevoDiv = document.createElement('div');
    nuevoDiv.className = 'col-md-6 mb-3';

    nuevoDiv.innerHTML = `
        <div class="input-group">
            <input type="text" name="opcionesForm" class="form-control" placeholder="Opción ${contadorOpciones}" required>
            <button class="btn btn-outline-danger" type="button" onclick="this.parentElement.parentElement.remove()" title="Quitar opción">✖</button>
        </div>
    `;

    contenedor.appendChild(nuevoDiv);
}

// Función para abrir la ventana modal de edición y pasarle los datos
function abrirModalEditar(boton) {

    const id = boton.dataset.id;
    const username = boton.dataset.username;

    document.getElementById('edit-id').value = id;
    document.getElementById('edit-username').value = username;

    const modalEditar = new bootstrap.Modal(
        document.getElementById('modalEditarLocutor')
    );

    modalEditar.show();
}
// ---- LÓGICA DE ALERTAS FLOTANTES (TOASTS) ----
document.addEventListener('DOMContentLoaded', function () {
    // Busca todas las notificaciones tipo "toast" en la página
    var toastElList = [].slice.call(document.querySelectorAll('.toast'))

    // Las inicializa con Bootstrap y les pone un temporizador de 4 segundos (4000ms)
    var toastList = toastElList.map(function (toastEl) {
        return new bootstrap.Toast(toastEl, { delay: 4000 })
    });

    // Muestra automáticamente cualquier toast que exista en el HTML
    toastList.forEach(toast => toast.show());
});