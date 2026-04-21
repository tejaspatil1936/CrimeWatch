// heatmap.js
const map = L.map('map').setView([18.5204, 73.8567], 12);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap',
    maxZoom: 19
}).addTo(map);

const severityColor = { HIGH: '#c0392b', CRITICAL: '#7b241c', MEDIUM: '#e67e22', LOW: '#27ae60' };

fetch('/api/public/reports')
    .then(r => r.json())
    .then(reports => {
        if (!reports || reports.length === 0) {
            document.getElementById('map').insertAdjacentHTML('afterend',
                '<p style="text-align:center;padding:1rem;color:#888">No reports to display yet.</p>');
            return;
        }

        // heatmap layer if plugin available
        const heatPoints = reports.map(r => [r.latitude, r.longitude, 0.8]);
        if (typeof L.heatLayer === 'function') {
            L.heatLayer(heatPoints, {
                radius: 35, blur: 20, maxZoom: 17,
                gradient: { 0.2: '#6b7c5b', 0.4: '#8a7730', 0.6: '#a85a2b', 0.9: '#8a2b2b' }
            }).addTo(map);
        }

        // always add circle markers so something is always visible
        reports.forEach(r => {
            const color = severityColor[r.severity] || '#3498db';
            L.circleMarker([r.latitude, r.longitude], {
                radius: 10, fillColor: color, color: '#fff',
                weight: 2, opacity: 1, fillOpacity: 0.85
            }).addTo(map).bindPopup(
                `<strong>${r.title || r.crimeType}</strong><br>
                 Type: ${r.crimeType}<br>
                 Severity: ${r.severity}<br>
                 Status: ${r.status}`
            );
        });

        // fit map to markers
        const bounds = reports.map(r => [r.latitude, r.longitude]);
        map.fitBounds(bounds, { padding: [40, 40] });
    })
    .catch(err => console.error('Failed to load report points:', err));
