<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<script src="https://unpkg.com/lucide@latest/dist/umd/lucide.min.js"></script>

<!-- ═══════════════════════════════════════════════════════════════
     HERO
════════════════════════════════════════════════════════════════ -->
<section class="hw-hero">
  <div class="hw-hero__content">
    <div class="hw-hero__eyebrow">
      <span class="hw-live-dot"></span>
      Live · Pune City
    </div>
    <h1 class="hw-hero__title">Safer cities start<br>with one report.</h1>
    <p class="hw-hero__sub">CrimeWatch lets citizens report incidents anonymously, visualise crime density on a live heatmap, and helps officers coordinate faster responses — all in real time.</p>
    <div class="hw-hero__actions">
      <a href="<c:url value='/report/new'/>" class="btn btn-primary btn--lg">
        <i data-lucide="plus-circle" class="btn-icon"></i> Report an incident
      </a>
      <a href="<c:url value='/map'/>" class="btn hw-btn-soft btn--lg">
        <i data-lucide="map" class="btn-icon"></i> View live map
      </a>
    </div>
  </div>
  <div class="hw-hero__graphic" aria-hidden="true">
    <div class="hw-map-card">
      <div class="hw-map-card__bar">
        <span class="hw-map-card__dot hw-dot-red"></span>
        <span class="hw-map-card__dot hw-dot-amber"></span>
        <span class="hw-map-card__dot hw-dot-green"></span>
        <span class="hw-map-card__title">Live Heatmap</span>
      </div>
      <div class="hw-map-card__body">
        <div class="hw-pin hw-pin--1"><i data-lucide="map-pin"></i></div>
        <div class="hw-pin hw-pin--2"><i data-lucide="map-pin"></i></div>
        <div class="hw-pin hw-pin--3"><i data-lucide="map-pin"></i></div>
        <div class="hw-heat hw-heat--a"></div>
        <div class="hw-heat hw-heat--b"></div>
        <div class="hw-grid-lines"></div>
      </div>
      <div class="hw-map-card__footer">
        <span class="hw-pulse-badge"><span class="hw-live-dot hw-live-dot--sm"></span> 6 active reports</span>
      </div>
    </div>
  </div>
</section>

<!-- ═══════════════════════════════════════════════════════════════
     STATS STRIP
════════════════════════════════════════════════════════════════ -->
<div class="hw-stats">
  <div class="hw-stat">
    <i data-lucide="file-text" class="hw-stat__icon"></i>
    <div>
      <div class="hw-stat__val" id="statReports">—</div>
      <div class="hw-stat__lbl">Reports filed</div>
    </div>
  </div>
  <div class="hw-stats__div"></div>
  <div class="hw-stat">
    <i data-lucide="layers" class="hw-stat__icon"></i>
    <div>
      <div class="hw-stat__val">3</div>
      <div class="hw-stat__lbl">Active zones</div>
    </div>
  </div>
  <div class="hw-stats__div"></div>
  <div class="hw-stat">
    <i data-lucide="clock" class="hw-stat__icon"></i>
    <div>
      <div class="hw-stat__val" id="statPending">—</div>
      <div class="hw-stat__lbl">Pending review</div>
    </div>
  </div>
  <div class="hw-stats__div"></div>
  <div class="hw-stat">
    <i data-lucide="shield-check" class="hw-stat__icon"></i>
    <div>
      <div class="hw-stat__val">24/7</div>
      <div class="hw-stat__lbl">Officer coverage</div>
    </div>
  </div>
</div>

<!-- ═══════════════════════════════════════════════════════════════
     HOW IT WORKS
════════════════════════════════════════════════════════════════ -->
<section class="hw-section">
  <div class="hw-section__head">
    <p class="eyebrow">How it works</p>
    <h2>Three steps to a safer city</h2>
    <p class="lede" style="margin:0 auto;text-align:center;">From a citizen's report to an officer's response — the whole loop, in minutes.</p>
  </div>
  <div class="hw-steps">
    <div class="hw-step hw-step--anim" style="--d:0ms">
      <div class="hw-step__icon-wrap hw-step__icon-wrap--amber">
        <i data-lucide="plus-circle"></i>
      </div>
      <div class="hw-step__num">01</div>
      <h3>Report</h3>
      <p>Submit an incident with location, type, and description. No account needed — fully anonymous.</p>
      <a href="<c:url value='/report/new'/>" class="hw-step__link">File a report <i data-lucide="arrow-right" class="hw-link-icon"></i></a>
    </div>
    <div class="hw-step__connector" aria-hidden="true"><i data-lucide="chevron-right"></i></div>
    <div class="hw-step hw-step--anim" style="--d:120ms">
      <div class="hw-step__icon-wrap hw-step__icon-wrap--blue">
        <i data-lucide="map"></i>
      </div>
      <div class="hw-step__num">02</div>
      <h3>Visualise</h3>
      <p>Every report appears on the live heatmap instantly, showing crime density across all zones.</p>
      <a href="<c:url value='/map'/>" class="hw-step__link">Open heatmap <i data-lucide="arrow-right" class="hw-link-icon"></i></a>
    </div>
    <div class="hw-step__connector" aria-hidden="true"><i data-lucide="chevron-right"></i></div>
    <div class="hw-step hw-step--anim" style="--d:240ms">
      <div class="hw-step__icon-wrap hw-step__icon-wrap--green">
        <i data-lucide="shield"></i>
      </div>
      <div class="hw-step__num">03</div>
      <h3>Respond</h3>
      <p>Officers get real-time socket alerts, assign themselves to incidents, and close the loop.</p>
      <sec:authorize access="isAuthenticated()">
        <a href="<c:url value='/dashboard'/>" class="hw-step__link">Open dashboard <i data-lucide="arrow-right" class="hw-link-icon"></i></a>
      </sec:authorize>
      <sec:authorize access="!isAuthenticated()">
        <a href="<c:url value='/login'/>" class="hw-step__link">Officer login <i data-lucide="arrow-right" class="hw-link-icon"></i></a>
      </sec:authorize>
    </div>
  </div>
</section>

<!-- ═══════════════════════════════════════════════════════════════
     FEATURES GRID
════════════════════════════════════════════════════════════════ -->
<section class="hw-section hw-section--inset">
  <div class="hw-section__head">
    <p class="eyebrow">Platform features</p>
    <h2>Everything you need, nothing you don't</h2>
  </div>
  <div class="hw-features">
    <div class="hw-feat hw-feat--anim" style="--d:0ms">
      <i data-lucide="map-pin" class="hw-feat__icon"></i>
      <h3>Anonymous reporting</h3>
      <p>Submit incidents without creating an account. Your identity stays private.</p>
    </div>
    <div class="hw-feat hw-feat--anim" style="--d:60ms">
      <i data-lucide="activity" class="hw-feat__icon"></i>
      <h3>Live heatmap</h3>
      <p>Crime density visualised in real time across all city zones on an interactive map.</p>
    </div>
    <div class="hw-feat hw-feat--anim" style="--d:120ms">
      <i data-lucide="zap" class="hw-feat__icon"></i>
      <h3>Real-time alerts</h3>
      <p>Officers receive instant socket-based push notifications the moment a report is filed.</p>
    </div>
    <div class="hw-feat hw-feat--anim" style="--d:180ms">
      <i data-lucide="trending-up" class="hw-feat__icon"></i>
      <h3>Zone escalation</h3>
      <p>Automated escalation engine flags high-activity zones before they become hotspots.</p>
    </div>
    <div class="hw-feat hw-feat--anim" style="--d:240ms">
      <i data-lucide="users" class="hw-feat__icon"></i>
      <h3>Role-based access</h3>
      <p>Separate dashboards for citizens, officers, and admins — each with the right tools.</p>
    </div>
    <div class="hw-feat hw-feat--anim" style="--d:300ms">
      <i data-lucide="download" class="hw-feat__icon"></i>
      <h3>CSV export</h3>
      <p>Admins can export all reports as CSV for offline analysis and reporting.</p>
    </div>
  </div>
</section>

<!-- ═══════════════════════════════════════════════════════════════
     CTA
════════════════════════════════════════════════════════════════ -->
<sec:authorize access="!isAuthenticated()">
<section class="hw-cta">
  <div class="hw-cta__icon-wrap"><i data-lucide="shield-check"></i></div>
  <h2>Are you a law enforcement officer?</h2>
  <p>Sign in to access the real-time dashboard, manage reports, and coordinate responses across zones.</p>
  <a href="<c:url value='/login'/>" class="btn btn-primary btn--lg">
    <i data-lucide="log-in" class="btn-icon"></i> Officer sign in
  </a>
</section>
</sec:authorize>

<!-- ═══════════════════════════════════════════════════════════════
     STYLES
════════════════════════════════════════════════════════════════ -->
<style>
/* ── Shared ───────────────────────────────────────────────────── */
.btn--lg { height: 46px; padding: 0 1.5rem; font-size: 0.95rem; gap: 0.5rem; }
.btn-icon { width: 16px; height: 16px; flex-shrink: 0; }
.hw-btn-soft {
  background: var(--bg-inset);
  color: var(--ink-primary);
  border: 1px solid var(--rule-hair);
  border-radius: var(--radius-2);
  display: inline-flex; align-items: center; gap: 0.5rem;
  height: 46px; padding: 0 1.5rem; font-size: 0.95rem;
  font-weight: var(--fw-semibold); text-decoration: none;
  transition: background 0.15s, border-color 0.15s;
}
.hw-btn-soft:hover { background: var(--rule-soft); border-color: var(--rule-strong); color: var(--ink-primary); }

.hw-live-dot {
  display: inline-block; width: 8px; height: 8px;
  border-radius: 50%; background: #22c55e;
  animation: hw-blink 2s ease-in-out infinite;
}
.hw-live-dot--sm { width: 6px; height: 6px; }
@keyframes hw-blink { 0%,100%{opacity:1} 50%{opacity:0.25} }

/* ── Hero ─────────────────────────────────────────────────────── */
.hw-hero {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--s-8);
  align-items: center;
  padding: var(--s-8) 0 var(--s-7);
  margin-bottom: var(--s-7);
  border-bottom: 1px solid var(--rule-hair);
}
@media (max-width: 768px) {
  .hw-hero { grid-template-columns: 1fr; }
  .hw-hero__graphic { display: none; }
}
.hw-hero__eyebrow {
  display: inline-flex; align-items: center; gap: 0.5rem;
  font-size: var(--fs-xs); font-weight: var(--fw-semibold);
  text-transform: uppercase; letter-spacing: 0.1em;
  color: var(--ink-tertiary); margin-bottom: var(--s-4);
}
.hw-hero__title {
  font-family: var(--font-serif);
  font-size: clamp(2rem, 3.5vw, 3rem);
  font-weight: var(--fw-bold);
  line-height: 1.15;
  color: var(--ink-primary);
  margin-bottom: var(--s-4);
  animation: hw-fade-up 0.5s ease both;
}
.hw-hero__sub {
  color: var(--ink-secondary);
  font-size: 1.05rem;
  max-width: 46ch;
  line-height: var(--lh-loose);
  margin-bottom: var(--s-6);
  animation: hw-fade-up 0.5s 0.1s ease both;
}
.hw-hero__actions {
  display: flex; gap: var(--s-3); flex-wrap: wrap;
  animation: hw-fade-up 0.5s 0.2s ease both;
}

/* hero graphic */
.hw-hero__graphic { animation: hw-fade-up 0.6s 0.15s ease both; }
.hw-map-card {
  background: var(--bg-surface);
  border: 1px solid var(--rule-hair);
  border-radius: var(--radius-3);
  box-shadow: 0 8px 32px rgba(26,26,26,0.08), 0 1px 0 var(--rule-hair);
  overflow: hidden;
}
.hw-map-card__bar {
  display: flex; align-items: center; gap: 6px;
  padding: 10px 14px;
  background: var(--bg-inset);
  border-bottom: 1px solid var(--rule-hair);
}
.hw-map-card__dot { width: 10px; height: 10px; border-radius: 50%; }
.hw-dot-red   { background: #ff5f57; }
.hw-dot-amber { background: #febc2e; }
.hw-dot-green { background: #28c840; }
.hw-map-card__title {
  margin-left: auto; font-size: var(--fs-xs);
  font-weight: var(--fw-semibold); color: var(--ink-tertiary);
  text-transform: uppercase; letter-spacing: 0.08em;
}
.hw-map-card__body {
  position: relative; height: 200px;
  background: linear-gradient(135deg, #f0ede6 0%, #e8e3d9 100%);
  overflow: hidden;
}
.hw-grid-lines {
  position: absolute; inset: 0;
  background-image:
    linear-gradient(var(--rule-soft) 1px, transparent 1px),
    linear-gradient(90deg, var(--rule-soft) 1px, transparent 1px);
  background-size: 32px 32px;
}
.hw-heat {
  position: absolute; border-radius: 50%;
  filter: blur(28px); opacity: 0.45;
  animation: hw-pulse-heat 3s ease-in-out infinite alternate;
}
.hw-heat--a { width: 120px; height: 120px; background: #e67e22; top: 20%; left: 25%; }
.hw-heat--b { width: 90px;  height: 90px;  background: #c0392b; top: 45%; left: 55%; animation-delay: 1.2s; }
@keyframes hw-pulse-heat { from{opacity:0.35;transform:scale(0.95)} to{opacity:0.55;transform:scale(1.05)} }

.hw-pin {
  position: absolute; color: var(--amber-700);
  animation: hw-pin-drop 0.4s ease both;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.2));
}
.hw-pin svg { width: 22px; height: 22px; fill: var(--amber-600); stroke: #fff; stroke-width: 1.5; }
.hw-pin--1 { top: 22%; left: 28%; animation-delay: 0.6s; }
.hw-pin--2 { top: 48%; left: 58%; animation-delay: 0.9s; }
.hw-pin--3 { top: 30%; left: 65%; animation-delay: 1.1s; }
@keyframes hw-pin-drop { from{opacity:0;transform:translateY(-12px)} to{opacity:1;transform:translateY(0)} }

.hw-map-card__footer {
  padding: 10px 14px;
  border-top: 1px solid var(--rule-hair);
  background: var(--bg-surface);
}
.hw-pulse-badge {
  display: inline-flex; align-items: center; gap: 6px;
  font-size: var(--fs-xs); font-weight: var(--fw-semibold);
  color: var(--ink-secondary);
}

/* ── Stats strip ──────────────────────────────────────────────── */
.hw-stats {
  display: flex; align-items: center; justify-content: center;
  flex-wrap: wrap; gap: 0;
  background: var(--bg-surface);
  border: 1px solid var(--rule-hair);
  border-radius: var(--radius-3);
  padding: var(--s-5) var(--s-4);
  margin-bottom: var(--s-7);
  animation: hw-fade-up 0.5s 0.3s ease both;
}
.hw-stat {
  display: flex; align-items: center; gap: var(--s-3);
  padding: var(--s-3) var(--s-6);
}
.hw-stat__icon { width: 22px; height: 22px; color: var(--amber-600); flex-shrink: 0; }
.hw-stat__val {
  font-family: var(--font-serif); font-size: 1.7rem;
  font-weight: var(--fw-bold); color: var(--ink-primary); line-height: 1;
}
.hw-stat__lbl {
  font-size: var(--fs-xs); text-transform: uppercase;
  letter-spacing: 0.08em; color: var(--ink-tertiary); margin-top: 2px;
}
.hw-stats__div { width: 1px; height: 44px; background: var(--rule-hair); }
@media (max-width: 640px) {
  .hw-stats__div { display: none; }
  .hw-stat { padding: var(--s-3) var(--s-4); }
}

/* ── Sections ─────────────────────────────────────────────────── */
.hw-section { margin-bottom: var(--s-8); }
.hw-section--inset {
  background: var(--bg-inset);
  margin-left: calc(-1 * var(--s-5));
  margin-right: calc(-1 * var(--s-5));
  padding: var(--s-7) var(--s-5);
  margin-bottom: var(--s-8);
}
.hw-section__head {
  text-align: center; margin-bottom: var(--s-7);
}
.hw-section__head h2 {
  font-family: var(--font-serif); font-size: var(--fs-display-2);
  margin-bottom: var(--s-3);
}

/* ── Steps ────────────────────────────────────────────────────── */
.hw-steps {
  display: flex; align-items: flex-start;
  gap: 0; justify-content: center;
}
.hw-step {
  flex: 1; max-width: 300px;
  background: var(--bg-surface);
  border: 1px solid var(--rule-hair);
  border-radius: var(--radius-3);
  padding: var(--s-6);
  position: relative;
  transition: box-shadow 0.2s, transform 0.2s;
}
.hw-step:hover { box-shadow: 0 6px 24px rgba(26,26,26,0.09); transform: translateY(-3px); }
.hw-step__connector {
  display: flex; align-items: center; padding: 0 var(--s-2);
  color: var(--ink-tertiary); margin-top: 48px; flex-shrink: 0;
}
.hw-step__connector svg { width: 20px; height: 20px; }
.hw-step__icon-wrap {
  width: 48px; height: 48px; border-radius: var(--radius-3);
  display: flex; align-items: center; justify-content: center;
  margin-bottom: var(--s-4);
}
.hw-step__icon-wrap svg { width: 22px; height: 22px; }
.hw-step__icon-wrap--amber { background: var(--amber-50); color: var(--amber-700); }
.hw-step__icon-wrap--blue  { background: #eff6ff; color: #2563eb; }
.hw-step__icon-wrap--green { background: #f0fdf4; color: #16a34a; }
.hw-step__num {
  font-family: var(--font-serif); font-size: 2.5rem;
  font-weight: var(--fw-bold); color: var(--rule-soft);
  line-height: 1; position: absolute; top: var(--s-4); right: var(--s-5);
}
.hw-step h3 { font-size: var(--fs-h2); margin-bottom: var(--s-3); }
.hw-step p  { font-size: var(--fs-small); color: var(--ink-secondary); line-height: var(--lh-loose); margin-bottom: var(--s-4); }
.hw-step__link {
  display: inline-flex; align-items: center; gap: 4px;
  font-size: var(--fs-small); font-weight: var(--fw-semibold);
  color: var(--amber-700); text-decoration: none;
}
.hw-step__link:hover { color: var(--ink-primary); }
.hw-link-icon { width: 14px; height: 14px; }
@media (max-width: 768px) {
  .hw-steps { flex-direction: column; align-items: center; }
  .hw-step__connector { transform: rotate(90deg); margin: 0; padding: var(--s-1) 0; }
  .hw-step { max-width: 100%; width: 100%; }
}

/* ── Features ─────────────────────────────────────────────────── */
.hw-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: var(--s-4);
}
.hw-feat {
  background: var(--bg-surface);
  border: 1px solid var(--rule-hair);
  border-radius: var(--radius-3);
  padding: var(--s-5);
  transition: box-shadow 0.2s, transform 0.2s;
}
.hw-feat:hover { box-shadow: 0 4px 16px rgba(26,26,26,0.07); transform: translateY(-2px); }
.hw-feat__icon {
  width: 24px; height: 24px; color: var(--amber-600);
  margin-bottom: var(--s-3); display: block;
}
.hw-feat h3 { font-size: var(--fs-body); font-family: var(--font-sans); margin-bottom: var(--s-2); }
.hw-feat p  { font-size: var(--fs-small); color: var(--ink-secondary); line-height: var(--lh-loose); margin: 0; }

/* ── CTA ──────────────────────────────────────────────────────── */
.hw-cta {
  text-align: center;
  background: var(--bg-surface);
  border: 1px solid var(--rule-hair);
  border-radius: var(--radius-3);
  padding: var(--s-8) var(--s-6);
  margin-bottom: var(--s-8);
}
.hw-cta__icon-wrap {
  width: 56px; height: 56px; border-radius: 50%;
  background: var(--amber-50); color: var(--amber-700);
  display: inline-flex; align-items: center; justify-content: center;
  margin-bottom: var(--s-4);
}
.hw-cta__icon-wrap svg { width: 26px; height: 26px; }
.hw-cta h2 { font-family: var(--font-serif); font-size: var(--fs-h1); margin-bottom: var(--s-3); }
.hw-cta p  { color: var(--ink-secondary); max-width: 48ch; margin: 0 auto var(--s-5); line-height: var(--lh-loose); }

/* ── Scroll animations ────────────────────────────────────────── */
@keyframes hw-fade-up {
  from { opacity: 0; transform: translateY(18px); }
  to   { opacity: 1; transform: translateY(0); }
}
.hw-step--anim, .hw-feat--anim {
  opacity: 0; transform: translateY(18px);
  transition: opacity 0.45s ease, transform 0.45s ease;
}
.hw-step--anim.hw-visible, .hw-feat--anim.hw-visible {
  opacity: 1; transform: translateY(0);
}
</style>

<script>
/* init lucide icons */
lucide.createIcons();

/* live stats */
fetch('/api/public/reports')
  .then(r => r.json())
  .then(data => {
    document.getElementById('statReports').textContent = data.length;
    document.getElementById('statPending').textContent = data.filter(r => r.status === 'PENDING').length;
  }).catch(() => {});

/* scroll-triggered animations */
const observer = new IntersectionObserver(entries => {
  entries.forEach((e, i) => {
    if (e.isIntersecting) {
      const delay = e.target.style.getPropertyValue('--d') || '0ms';
      setTimeout(() => e.target.classList.add('hw-visible'), parseInt(delay));
      observer.unobserve(e.target);
    }
  });
}, { threshold: 0.15 });
document.querySelectorAll('.hw-step--anim, .hw-feat--anim').forEach(el => observer.observe(el));
</script>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
