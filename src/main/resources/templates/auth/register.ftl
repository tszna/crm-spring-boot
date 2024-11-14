<#import "/layout/layout.ftl" as layout>
<@layout.extends name="base.ftl"/>

<@layout.block name="title">
</@layout.block>

<@layout.block name="content">
    <div class="container mt-5">
        <h2>Rejestracja</h2>
        <div id="error-message" style="color: red;"></div>
        <div id="success-message"></div>
        <form id="register-form">
            <div class="form-group">
                <label for="name">Imię i nazwisko:</label>
                <input type="text" name="name" id="name"
                       class="form-control" required>
            </div>
            <div class="form-group">
                <label for="email">Adres e-mail:</label>
                <input type="email" name="email" id="email"
                       class="form-control" required>
            </div>
            <div class="form-group">
                <label for="password">Hasło:</label>
                <input type="password" name="password" id="password"
                       class="form-control" required>
            </div>
            <div class="form-group">
                <label for="confirm-password">Potwierdź hasło:</label>
                <input type="password" name="confirm-password" id="confirm-password"
                       class="form-control" required>
            </div>
            <button type="submit" class="btn btn-primary" id="register-button">Zarejestruj się</button>
        </form>
    </div>
</@layout.block>

<@layout.block name="scripts">
    <script>
        document.getElementById('register-form').addEventListener('submit',
        function(event) {
            event.preventDefault();

            const name = document.getElementById('name').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirm-password').value;

            if (password !== confirmPassword) {
                document.getElementById('error-message').innerText = 'Hasła nie są zgodne.';
                return;
            }

            fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ name, email, password })
            })
            .then(response => {
                if (response.ok) {
                    // Rejestracja udana, wyświetl komunikat sukcesu
                    document.getElementById('error-message').innerText = '';
                    document.getElementById('success-message').style.color = 'green';
                    document.getElementById('success-message').innerText = 'Rejestracja zakończona sukcesem! Możesz się teraz zalogować.';
                    // Zablokuj formularz
                    document.getElementById('register-form').reset();
                    document.getElementById('register-form').style.display = 'none';

                    // Dodaj link do logowania
                    const loginLink = document.createElement('a');
                    loginLink.href = '/login';
                    loginLink.className = 'btn btn-success mt-3';
                    loginLink.innerText = 'Przejdź do logowania';
                    document.getElementById('success-message').appendChild(document.createElement('br'));
                    document.getElementById('success-message').appendChild(loginLink);
                } else if (response.status === 400) {
                    return response.json().then(data => {
                        throw new Error(data.message || 'Błąd rejestracji');
                    });
                } else {
                    throw new Error('Błąd rejestracji');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('error-message').innerText = error.message;
            });
        });
    </script>
</@layout.block>
