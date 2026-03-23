document.getElementById('capture-loc').addEventListener('click', () => {
    if (!navigator.geolocation) {
        alert('Geolocation is not available in this browser.');
        return;
    }
    navigator.geolocation.getCurrentPosition(
        pos => {
            document.querySelector('input[name="latitude"]').value  = pos.coords.latitude.toFixed(6);
            document.querySelector('input[name="longitude"]').value = pos.coords.longitude.toFixed(6);
        },
        err => alert('Unable to capture location: ' + err.message),
        { enableHighAccuracy: true, timeout: 10000 }
    );
});
