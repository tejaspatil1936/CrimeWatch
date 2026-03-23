// src/main/resources/static/js/heatmap.js
const map = L.map('map').setView([18.5204, 73.8567], 12);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap',
    maxZoom: 19
}).addTo(map);

window.heatLayer = L.heatLayer([], {
    radius: 25,
    blur: 15,
    maxZoom: 17,
    gradient: {
        0.2: '#6b7c5b',
        0.4: '#8a7730',
        0.6: '#a85a2b',
        0.9: '#8a2b2b'
    }
}).addTo(map);

fetch('/api/public/reports/points')
    .then(r => r.json())
    .then(points => points.forEach(p => window.heatLayer.addLatLng([p.lat, p.lng, 0.5])));
