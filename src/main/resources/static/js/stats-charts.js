// Chart.js powered stats for admin dashboard
function initWeeklyResponseChart(canvasId, data) {
    const ctx = document.getElementById(canvasId);
    if (!ctx || !data) return;
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.map(d => d.week),
            datasets: [{
                label: 'Avg Response Time (min)',
                data: data.map(d => d.avgMinutes),
                borderColor: '#b47527',
                backgroundColor: 'rgba(180,117,39,0.1)',
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true, title: { display: true, text: 'Minutes' } }
            }
        }
    });
}

function initReportsByZoneChart(canvasId, labels, values) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Reports',
                data: values,
                backgroundColor: '#b47527'
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
        }
    });
}

function initCrimeTypeChart(canvasId, labels, values) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return;
    new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: values,
                backgroundColor: ['#6b7c5b', '#8a7730', '#a85a2b', '#8a2b2b', '#4a4a4a']
            }]
        },
        options: { responsive: true }
    });
}
