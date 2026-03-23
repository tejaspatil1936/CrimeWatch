// src/main/resources/static/js/dashboard.js
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-app.js";
import { getDatabase, ref, onChildAdded, query, limitToLast }
    from "https://www.gstatic.com/firebasejs/10.12.0/firebase-database.js";

const app = initializeApp(window.FIREBASE_CONFIG);
const db  = getDatabase(app);

const alertList = document.getElementById("alert-list");
const alertsRef = query(ref(db, "alerts"), limitToLast(50));

onChildAdded(alertsRef, (snap) => {
    const a = snap.val();
    const li = document.createElement("li");
    li.className = "alert-item";
    li.innerHTML = `
        <span class="alert-sev sev-${a.severity}"></span>
        <div>
            <strong>${escapeHtml(a.title)}</strong>
            <div class="alert-meta">${a.crimeType} · ${a.zoneName} · ${formatTime(a.timestamp)}</div>
        </div>
        <span class="sev sev-${a.severity}">${a.severity}</span>`;
    alertList.prepend(li);

    if (window.heatLayer) {
        window.heatLayer.addLatLng([a.latitude, a.longitude, 0.6]);
    }
});

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g,
        c => ({ "&":"&amp;","<":"&lt;",">":"&gt;","\"":"&quot;","'":"&#39;" }[c]));
}
function formatTime(ts) {
    const d = new Date(ts);
    return d.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}
