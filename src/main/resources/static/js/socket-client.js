// WebSocket bridge client for browser-side socket connectivity
(function() {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = protocol + '//' + window.location.host + '/ws/alerts';
    let ws;

    function connect() {
        ws = new WebSocket(wsUrl);
        ws.onopen = function() {
            console.log('[CrimeWatch] WebSocket connected');
        };
        ws.onmessage = function(event) {
            try {
                const alert = JSON.parse(event.data);
                console.log('[CrimeWatch] Alert received:', alert);
                if (window.onCrimeWatchAlert) {
                    window.onCrimeWatchAlert(alert);
                }
            } catch (e) {
                console.warn('[CrimeWatch] Failed to parse WS message', e);
            }
        };
        ws.onclose = function() {
            console.log('[CrimeWatch] WebSocket disconnected, reconnecting in 5s...');
            setTimeout(connect, 5000);
        };
        ws.onerror = function(err) {
            console.error('[CrimeWatch] WebSocket error', err);
        };
    }

    connect();
})();
