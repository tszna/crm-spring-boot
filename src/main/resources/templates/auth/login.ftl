<#import "/layout/layout.ftl" as layout>
<@layout.extends name="base.ftl"/>

<@layout.block name="title">
</@layout.block>

<@layout.block name="content">
    <div class="container mt-5">
        <h2>Logowanie</h2>
        <div id="error-message" style="color: red;"></div>
        <form id="login-form">
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
            <button type="submit" class="btn btn-primary">Zaloguj się</button>
        </form>
    </div>
</@layout.block>

<@layout.block name="scripts">
    <script>
        document.getElementById('login-form').addEventListener('submit',
        function(event) {
            event.preventDefault();

            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify({ email, password })
            })
            .then(response => {
                if (response.ok) {
                    window.location.href = '/time-tracker';
                } else {
                    throw new Error('Nieprawidłowy email lub hasło');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('error-message').innerText = error.message;
            });
        });
    </script>
</@layout.block>
