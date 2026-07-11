document.addEventListener("DOMContentLoaded", function() {
    // Al cargar la página, pinta los gráficos iniciales recibidos desde Spring Boot
    if(window.encuestasDataLocal && window.encuestasDataLocal.length > 0) {
        actualizarGraficos(window.encuestasDataLocal[0].opciones);
    }
    // Verifica si el usuario ya votó anteriormente para bloquearle los botones
    verificarCandadosLocales();
});

// Configuración de WebSockets con SockJS y STOMP
var socket = new SockJS('/ws-encuestas');
var stompClient = Stomp.over(socket);
stompClient.debug = null; // Evita llenar la consola de mensajes técnicos

// Conexión y Suscripción al canal de resultados
stompClient.connect({}, function () {
    stompClient.subscribe('/topic/resultados', function (mensaje) {
        // Cuando llega un nuevo voto, actualizamos las barras animadas
        actualizarGraficos(JSON.parse(mensaje.body));
    });
});

// Verificación de seguridad (Candado LocalStorage)
function verificarCandadosLocales() {
    const botones = document.querySelectorAll('.btn-voto');
    botones.forEach(boton => {
        const encuestaId = boton.getAttribute('data-encuesta-id');

        // Si encontramos la huella digital en su navegador y el botón no estaba ya bloqueado
        if (localStorage.getItem('votado_encuesta_' + encuestaId) && !boton.disabled) {
            boton.disabled = true; // Bloqueamos el botón
            const m = document.getElementById('mensaje-' + encuestaId);
            if (m) {
                m.classList.remove('d-none', 'alert-danger', 'alert-success');
                m.classList.add('alert-info');
                m.textContent = "Ya participaste en esta encuesta desde tu dispositivo.";
            }
        }
    });
}

// Función que calcula porcentajes y mueve las barras
function actualizarGraficos(opciones) {
    if (opciones.length === 0) return;

    let totalVotos = opciones.reduce((sum, opcion) => sum + opcion.cantidadVotos, 0);
    document.getElementById('totalVotosTexto').innerText = "Total de votos: " + totalVotos;

    // Detectar si el administrador usó el botón de "Reiniciar a 0"
    let encuestaIdEncontrada = null;
    if (window.encuestasDataLocal && window.encuestasDataLocal.length > 0) {
        const primeraOpcionId = opciones[0].id;
        const encuestaMatch = window.encuestasDataLocal.find(e => e.opciones.some(o => o.id === primeraOpcionId));
        if (encuestaMatch) encuestaIdEncontrada = encuestaMatch.id;
    }

    // Si los votos son 0, liberamos el candado del celular para que puedan volver a votar
    if (totalVotos === 0 && encuestaIdEncontrada) {
        if (localStorage.getItem('votado_encuesta_' + encuestaIdEncontrada)) {
            localStorage.removeItem('votado_encuesta_' + encuestaIdEncontrada);
            const m = document.getElementById('mensaje-' + encuestaIdEncontrada);
            if (m) m.classList.add('d-none');
        }
    }

    // Aplicar el nuevo tamaño a cada barra (ahora en formato de gráfico vertical)
    opciones.forEach((opcion) => {
        let porcentaje = totalVotos === 0 ? 0 : Math.round((opcion.cantidadVotos / totalVotos) * 100);
        let texto = document.getElementById('texto-votos-' + opcion.id);
        let cantidad = document.getElementById('cantidad-' + opcion.id);
        let barra = document.getElementById('barra-' + opcion.id);

        if (texto) texto.innerText = `${porcentaje}%`;
        if (cantidad) cantidad.innerText = `${opcion.cantidadVotos} ${opcion.cantidadVotos === 1 ? 'voto' : 'votos'}`;
        if (barra) barra.style.height = porcentaje + '%';
    });

    // Mejora visual: colocar medallas 🥇🥈🥉 en las opciones líderes (no afecta la lógica de votos)
    marcarLideres(opciones, totalVotos);
}

// NUEVA FUNCIÓN (aditiva): resalta con medallas a las opciones mejor ubicadas
function marcarLideres(opciones, totalVotos) {
    // Limpia todas las medallas primero
    opciones.forEach(op => {
        const badge = document.getElementById('badge-' + op.id);
        if (badge) badge.textContent = '';
    });

    // Si no hay votos aún, no mostramos medallas
    if (!totalVotos || totalVotos === 0) return;

    // Ordena una copia de las opciones de mayor a menor cantidad de votos
    const ranking = [...opciones].sort((a, b) => b.cantidadVotos - a.cantidadVotos);
    const medallas = ['🥇', '🥈', '🥉'];

    ranking.forEach((op, index) => {
        // Solo asigna medalla si esa opción tiene al menos 1 voto
        if (op.cantidadVotos > 0 && index < medallas.length) {
            const badge = document.getElementById('badge-' + op.id);
            if (badge) badge.textContent = medallas[index];
        }
    });
}

// Función para registrar un nuevo voto
async function enviarVoto(opcionId, encuestaId) {
    const m = document.getElementById('mensaje-' + encuestaId);

    // Candado 1: Verificación de LocalStorage antes de enviar al servidor
    if (localStorage.getItem('votado_encuesta_' + encuestaId)) {
        m.classList.remove('d-none', 'alert-danger', 'alert-success');
        m.classList.add('alert-info');
        m.textContent = "Voto denegado: Ya registramos tu participación.";
        return;
    }

    try {
        // Enviar voto a nuestra API Java
        const res = await fetch(`/api/votar?opcionId=${opcionId}`, { method: 'POST' });
        const text = await res.text();

        m.classList.remove('d-none', 'alert-danger', 'alert-success', 'alert-info');

        if (text.includes("Éxito")) {
            m.classList.add('alert-success');
            m.textContent = text;

            // Activar candado en la memoria del celular
            localStorage.setItem('votado_encuesta_' + encuestaId, 'true');

            // Bloquear todos los botones al instante
            const botones = document.querySelectorAll(`.btn-voto[data-encuesta-id="${encuestaId}"]`);
            botones.forEach(btn => btn.disabled = true);
        } else {
            // Si el backend lo rechaza (ej. por IP repetida)
            m.classList.add('alert-danger');
            m.textContent = text;
        }
    } catch (error) {
        // Fallo de internet o servidor caído
        m.classList.remove('d-none', 'alert-success', 'alert-info');
        m.classList.add('alert-danger');
        m.textContent = "Error de comunicación con el servidor.";
    }
}