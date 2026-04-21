<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>

<section class="page-head">
    <p class="eyebrow">Administration</p>
    <h1>Zones</h1>
</section>

<!-- ── Add zone card ── -->
<div class="card" style="margin-bottom:var(--s-6)">
    <h3 style="margin-bottom:var(--s-4)">Add new zone</h3>
    <p style="font-size:var(--fs-small);color:var(--ink-tertiary);margin-bottom:var(--s-4)">
        Click anywhere on the map to drop a pin and auto-fill the coordinates.
    </p>

    <!-- map picker -->
    <div id="zone-map" style="height:340px;border:1px solid var(--rule-hair);border-radius:var(--radius-2);margin-bottom:var(--s-5);"></div>

    <form method="post" action="<c:url value='/admin/zones/add'/>" class="form-stack">
        <sec:csrfInput/>
        <div class="field-row">
            <div class="field">
                <label for="zoneName">Zone name</label>
                <input id="zoneName" name="zoneName" required/>
            </div>
            <div class="field">
                <label for="city">City</label>
                <input id="city" name="city" required/>
            </div>
            <div class="field">
                <label for="escalationThreshold">Threshold</label>
                <input id="escalationThreshold" name="escalationThreshold" type="number" min="1" max="100" value="5"/>
            </div>
        </div>
        <div class="field-row">
            <div class="field">
                <label for="latCenter">Latitude</label>
                <input id="latCenter" name="latCenter" type="number" step="0.000001" required readonly class="coord-input"/>
            </div>
            <div class="field">
                <label for="lngCenter">Longitude</label>
                <input id="lngCenter" name="lngCenter" type="number" step="0.000001" required readonly class="coord-input"/>
            </div>
            <div class="field" style="align-self:end">
                <button type="submit" class="btn btn-primary" id="addBtn" disabled>Add zone</button>
            </div>
        </div>
        <p id="pin-hint" style="font-size:var(--fs-xs);color:var(--amber-700);margin:0">
            ↑ Click the map to place a pin first
        </p>
    </form>
</div>

<!-- ── Existing zones table ── -->
<table class="data-table">
    <thead>
        <tr><th>Zone</th><th>City</th><th>Lat</th><th>Lng</th><th>Threshold</th><th>Actions</th></tr>
    </thead>
    <tbody>
        <c:forEach var="z" items="${zones}">
            <tr>
                <td><c:out value="${z.zoneName}"/></td>
                <td><c:out value="${z.city}"/></td>
                <td style="font-size:var(--fs-xs);color:var(--ink-tertiary)"><c:out value="${z.latCenter}"/></td>
                <td style="font-size:var(--fs-xs);color:var(--ink-tertiary)"><c:out value="${z.lngCenter}"/></td>
                <td>
                    <form method="post" action="<c:url value='/admin/zones/${z.zoneId}/threshold'/>" class="threshold-form">
                        <sec:csrfInput/>
                        <input type="number" name="threshold" value="${z.escalationThreshold}"
                               min="1" max="100" class="threshold-input"/>
                        <button type="submit" class="btn btn-sm btn-primary">Save</button>
                    </form>
                </td>
                <td>
                    <form method="post" action="<c:url value='/admin/zones/${z.zoneId}/delete'/>">
                        <sec:csrfInput/>
                        <button class="btn btn-sm" style="color:var(--status-error)">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty zones}">
            <tr><td colspan="6" class="empty">No zones yet.</td></tr>
        </c:if>
    </tbody>
</table>

<style>
.coord-input { background: var(--bg-inset); color: var(--ink-secondary); cursor: default; }
.threshold-form { display: flex; align-items: center; gap: var(--s-2); }
.threshold-input {
    width: 70px; font-family: var(--font-sans); font-size: var(--fs-small);
    padding: 5px 8px; border: 1px solid var(--rule-hair);
    border-radius: var(--radius-2); background: var(--bg-surface);
    color: var(--ink-primary); text-align: center;
}
.threshold-input:focus { outline: none; border-color: var(--amber-600); box-shadow: 0 0 0 3px var(--amber-100); }
</style>

<script>
// ── existing zone markers ──────────────────────────────────────
const existingZones = [
    <c:forEach var="z" items="${zones}" varStatus="s">
    { name: "<c:out value='${z.zoneName}'/>", lat: ${z.latCenter}, lng: ${z.lngCenter} }<c:if test="${!s.last}">,</c:if>
    </c:forEach>
];

// ── init map ──────────────────────────────────────────────────
const map = L.map('zone-map').setView([18.5204, 73.8567], 12);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap', maxZoom: 19
}).addTo(map);

// plot existing zones as grey markers
existingZones.forEach(z => {
    L.circleMarker([z.lat, z.lng], {
        radius: 10, fillColor: '#8a7730', color: '#fff',
        weight: 2, fillOpacity: 0.7
    }).addTo(map).bindTooltip(z.name, { permanent: true, direction: 'top', offset: [0, -10] });
});

// ── click to place new pin ────────────────────────────────────
let newMarker = null;
const latInput  = document.getElementById('latCenter');
const lngInput  = document.getElementById('lngCenter');
const addBtn    = document.getElementById('addBtn');
const pinHint   = document.getElementById('pin-hint');

map.on('click', function(e) {
    const { lat, lng } = e.latlng;

    // remove previous pin
    if (newMarker) map.removeLayer(newMarker);

    newMarker = L.marker([lat, lng], {
        icon: L.divIcon({
            className: '',
            html: '<div style="width:16px;height:16px;background:var(--amber-600);border:2px solid #fff;border-radius:50%;box-shadow:0 2px 6px rgba(0,0,0,0.3)"></div>',
            iconAnchor: [8, 8]
        })
    }).addTo(map).bindPopup(`<strong>New zone</strong><br>${lat.toFixed(6)}, ${lng.toFixed(6)}`).openPopup();

    latInput.value = lat.toFixed(6);
    lngInput.value = lng.toFixed(6);
    addBtn.disabled = false;
    pinHint.style.display = 'none';
});
</script>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
