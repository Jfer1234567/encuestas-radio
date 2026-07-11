let miGrafico; // Variable global para guardar el gráfico
let opcionLabels = [];
let opcionVotos = [];

// Colores vibrantes para TV
const coloresTV = [
    '#3b82f6', '#10b981', '#ef4444', '#f59e0b',
    '#8b5cf6', '#14b8a6', '#f97316', '#06b6d4'
];

document.addEventListener("DOMContentLoaded", function() {
    if(window.encuestasDataLocal && window.encuestasDataLocal.length > 0) {

        // 1. Generar Código QR automáticamente
        const urlVotacion = window.location.protocol + "//" + window.location.host;
        const qrElement = document.getElementById('qr-code');

        if (qrElement) {
            new QRious({
                element: qrElement,
                value: urlVotacion,
                size: 200,
                backgroundAlpha: 0,
                foreground: '#0f172a'
            });
        }

        // 2. Inicializar el Gráfico
        inicializarGrafico(window.encuestasDataLocal[0].opciones);
    }
});

// Configuración de WebSockets
var socket = new SockJS('/ws-encuestas');
var stompClient = Stomp.over(socket);
stompClient.debug = null; // Oculta logs en consola

stompClient.connect({}, function () {
    stompClient.subscribe('/topic/resultados', function (mensaje) {
        actualizarGrafico(JSON.parse(mensaje.body));
    });
});

function inicializarGrafico(opciones) {
    opciones.forEach(op => {
        opcionLabels.push(op.texto);
        opcionVotos.push(op.cantidadVotos);
    });

    actualizarTotal(opcionVotos);

    const ctx = document.getElementById('graficoTV');
    if (!ctx) return;

    // Crear Gráfico de Dona (Pie Chart con hueco en medio)
    miGrafico = new Chart(ctx.getContext('2d'), {
        type: 'doughnut',
        data: {
            labels: opcionLabels,
            datasets: [{
                data: opcionVotos,
                backgroundColor: coloresTV,
                borderColor: '#1e293b',
                borderWidth: 5,
                hoverOffset: 10
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'right',
                    labels: {
                        color: 'white',
                        font: { size: 20, family: 'Poppins' },
                        padding: 30
                    }
                }
            },
            animation: {
                animateScale: true,
                animateRotate: true
            }
        }
    });
}

function actualizarGrafico(opcionesActualizadas) {
    if (!miGrafico || opcionesActualizadas.length === 0) return;

    let nuevosVotos = [];
    opcionesActualizadas.forEach(op => nuevosVotos.push(op.cantidadVotos));

    // Actualizar datos del gráfico
    miGrafico.data.datasets[0].data = nuevosVotos;
    miGrafico.update(); // Hace la animación de actualización en vivo

    actualizarTotal(nuevosVotos);
}

function actualizarTotal(votos) {
    let total = votos.reduce((a, b) => a + b, 0);
    const totalElement = document.getElementById('totalVotosTV');
    if (totalElement) {
        totalElement.innerText = total;
    }
}