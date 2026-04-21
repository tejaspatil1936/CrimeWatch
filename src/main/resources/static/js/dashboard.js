// dashboard.js — polls /api/public/reports every 5s for live updates
// (replaces Firebase RTDB listener which requires relaxed security rules)

const alertList = document.getElementById("alert-list");
const seenIds = new Set();

function renderAlert(r) {
    const li = document.createElement("li");
    li.className = "alert-item";
    li.dataset.id = r.reportId;
    li.innerHTML = `
        <span class="alert-sev sev-${r.severity}"></span>
        <div>
            <strong>${escapeHtml(r.title)}</strong>
            <div class="alert-meta">${r.crimeType} · ${formatTime(r.reportedAt)}</div>
        </div>
        <span class="sev sev-${r.severity}">${r.severity}</span>`;
    alertList.prepend(li);

    if (window.heatLayer && r.latitude && r.longitude) {
        window.heatLayer.addLatLng([r.latitude, r.longitude, 0.6]);
    }
    // also drop a marker on the dashboard map
    if (window.dashMap && r.latitude && r.longitude) {
        L.circleMarker([r.latitude, r.longitude], {
            radius: 9, fillColor: severityColor(r.severity),
            color: '#fff', weight: 2, fillOpacity: 0.85
        }).addTo(window.dashMap)
          .bindPopup(`<strong>${escapeHtml(r.title)}</strong><br>${r.crimeType} · ${r.severity}`);
    }
}

function severityColor(s) {
    return { HIGH:'#a85a2b', CRITICAL:'#8a2b2b', MEDIUM:'#8a7730', LOW:'#6b7c5b' }[s] || '#3498db';
}

function poll() {
    fetch('/api/public/reports')
        .then(r => r.json())
        .then(reports => {
            // sort newest first
            reports.sort((a, b) => b.reportedAt - a.reportedAt);
            let hasNew = false;
            reports.forEach(r => {
                if (!seenIds.has(r.reportId)) {
                    seenIds.add(r.reportId);
                    renderAlert(r);
                    hasNew = true;
                }
            });
            if (hasNew && alertList.children.length === 0) {
                alertList.innerHTML = '<li style="padding:1rem;color:var(--ink-tertiary);font-size:0.85rem">No reports yet.</li>';
            }
        })
        .catch(err => console.error('[CrimeWatch] Poll error', err));
}

// initial load + poll every 5s
poll();
setInterval(poll, 5000);

function escapeHtml(s) {
    return String(s || '').replace(/[&<>"']/g,
        c => ({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;' }[c]));
}
function formatTime(ts) {
    if (!ts) return '';
    return new Date(ts).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}
