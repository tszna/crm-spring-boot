document.addEventListener('DOMContentLoaded', function() {
    let startTime = 0;
    let x = 0;
    let isActive = false;
    let counterInterval;

    // Ukryj przyciski i pokaż spinner ładowania
    document.getElementById('content_container').style.display = 'none';
    document.getElementById('loading-btn').style.display = 'inline-block';

    fetch('/api/time/getCurrentSession', {
        method: 'GET',
        credentials: 'include' // Upewnij się, że ciasteczka są wysyłane
    })
    .then(response => {
        if (response.status === 401) {
            // Nieautoryzowany - przekieruj na stronę logowania
            window.location.href = '/login';
            return;
        } else if (response.status === 204) {
            // Brak treści - inicjujemy wartości domyślne
            return null;
        } else if (response.ok) {
            return response.json();
        } else {
            throw new Error('Nieoczekiwany kod odpowiedzi: ' + response.status);
        }
    })
    .then(data => {
        if (!data) {
            // Inicjalizacja interfejsu gdy brak danych
            x = 0;
            isActive = false;
            document.getElementById('current-time').innerText = '00:00:00';
            document.getElementById('start-time').innerText = '-';
            document.getElementById('end-time').innerText = '-';
            document.getElementById('content_container').style.display = 'block';
            document.getElementById('loading-btn').style.display = 'none';
            document.getElementById('start-btn').disabled = false;
            document.getElementById('stop-btn').disabled = true;

            // Wywołaj startCounter()
            startCounter();
        } else {
            // Przetwarzanie danych z serwera
            x = parseInt(data.count_time, 10) || 0;
            isActive = data.is_active;
            document.getElementById('current-time').innerText = formatTime(x);
            document.getElementById('content_container').style.display = 'block';
            document.getElementById('loading-btn').style.display = 'none';

            if (isActive) {
                document.getElementById('start-time').innerText = data.start_time;
                document.getElementById('start-btn').disabled = true;
                document.getElementById('stop-btn').disabled = false;
                document.getElementById('end-time').innerText = 'czas jest w trakcie liczenia';
            } else {
                document.getElementById('start-time').innerText = data.start_time || '-';
                document.getElementById('end-time').innerText = data.end_time || '-';
                document.getElementById('start-btn').disabled = false;
                document.getElementById('stop-btn').disabled = true;
            }

            // Wywołaj startCounter()
            startCounter();
        }
    })
    .catch(error => {
        console.error('Error:', error);
        document.getElementById('loading-btn').innerText =
            'Wystąpił błąd podczas ładowania danych.';
    });

    // Obsługa przycisku Start
    document.getElementById('start-btn').addEventListener('click', function() {
        // Ukryj przyciski i pokaż spinner ładowania
        document.getElementById('content_container').style.display = 'none';
        document.getElementById('loading-btn').style.display = 'inline-block';

        fetch('/api/time/startSession', {
            method: 'POST',
            credentials: 'include' // Upewnij się, że ciasteczka są wysyłane
        })
        .then(response => {
            if (response.status === 401) {
                window.location.href = '/login';
                return;
            } else if (response.ok) {
                return response.json();
            } else {
                throw new Error('Nieoczekiwany kod odpowiedzi: ' + response.status);
            }
        })
        .then(data => {
            if (!data) return;
            document.getElementById('start-time').innerText = data.start_time;
            document.getElementById('start-btn').disabled = true;
            document.getElementById('stop-btn').disabled = false;
            document.getElementById('end-time').innerText = 'czas jest w trakcie liczenia';
            isActive = true;
            x = parseInt(data.count_time, 10) || 0;

            // Pokaż przyciski i ukryj spinner ładowania
            document.getElementById('content_container').style.display = 'block';
            document.getElementById('loading-btn').style.display = 'none';
        })
        .catch(error => {
            console.error('Error:', error);
            // Przywróć przyciski nawet jeśli wystąpił błąd
            document.getElementById('content_container').style.display = 'block';
            document.getElementById('loading-btn').style.display = 'none';
        });
    });

    // Obsługa przycisku Stop
    document.getElementById('stop-btn').addEventListener('click', function() {
        // Ukryj przyciski i pokaż spinner ładowania
        document.getElementById('content_container').style.display = 'none';
        document.getElementById('loading-btn').style.display = 'inline-block';

        fetch('/api/time/stopSession', {
            method: 'POST',
            credentials: 'include' // Upewnij się, że ciasteczka są wysyłane
        })
        .then(response => {
            if (response.status === 401) {
                window.location.href = '/login';
                return;
            } else if (response.ok) {
                return response.json();
            } else {
                throw new Error('Nieoczekiwany kod odpowiedzi: ' + response.status);
            }
        })
        .then(data => {
            if (!data) return;
            document.getElementById('end-time').innerText = data.end_time;
            document.getElementById('start-btn').disabled = false;
            document.getElementById('stop-btn').disabled = true;
            isActive = false;

            // Pokaż przyciski i ukryj spinner ładowania
            document.getElementById('content_container').style.display = 'block';
            document.getElementById('loading-btn').style.display = 'none';
        })
        .catch(error => {
            console.error('Error:', error);
            // Przywróć przyciski nawet jeśli wystąpił błąd
            document.getElementById('content_container').style.display = 'block';
            document.getElementById('loading-btn').style.display = 'none';
        });
    });

    function formatTime(seconds) {
        seconds = parseInt(seconds, 10) || 0;

        let hours = Math.floor(seconds / 3600);
        let minutes = Math.floor((seconds % 3600) / 60);
        let secs = Math.floor(seconds % 60);

        hours = hours < 10 ? '0' + hours : hours;
        minutes = minutes < 10 ? '0' + minutes : minutes;
        secs = secs < 10 ? '0' + secs : secs;

        return `${hours}:${minutes}:${secs}`;
    }

    function startCounter() {
        if (counterInterval) {
            return; // Jeśli licznik już działa, nie uruchamiaj go ponownie
        }
        counterInterval = setInterval(() => {
            document.getElementById('current-time').innerText = formatTime(x);
            if (isActive) {
                x++;
            }
        }, 1000);
    }
});
