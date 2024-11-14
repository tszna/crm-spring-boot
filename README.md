# CRM written in spring boot
Jest to prosta aplikacja, która pozwala liczyć swój czas pracy. Liczba sesji pracy w ciągu dnia jest dowolna. Aplikacja pozwala przejrzeć swoje podsumowanie tygodnia, w którym jest napisane ile czasu danego dnia się pracowało oraz ile czasu się przepracowało w danym tygodniu. W podsumowaniu czasu pracy jest również lista użytkowników, dzięki której można sprawdzić czy dany kolega z pracy dzisiaj pracuje czy nie. Jest to komunikowane poprzez kropkę, która zapala się na żółto jeśli dany kolega z pracy obecnie pracuje. 
Aplikacja pozwala również przejrzeć swój i kolegów z pracy kalendarz nieobecności, dzięki któremu można się zorientować kiedy ma się jaką nieobecność oraz kiedy można się spodziewać kolegi z pracy, który aktualnie nie pracuje, a który jest potrzebny do konsultacji przy bieżącym projekcie.

Licznik czasu pracy: 

<img src="https://i.imgur.com/Vg17yCK.gif" alt="operation in app">

Podsumowanie tygodnia:

<img src="https://i.imgur.com/cwrXMRK.gif" alt="operation in app">

Kalendarz:

<img src="https://i.imgur.com/yBA8wFv.gif" alt="operation in app">

Formularz logowania:

<img src="https://i.imgur.com/FIoSGuK.gif" alt="operation in app">


<h4>Uruchomienie projektu</h4>
Utworzyć w bazie mysql bazę danych o nazwie: java.
W kolejnym kroku należy upewnić się, czy dane dostępowe do bazy danych w pliku application.properties zgadzają się z ustawieniami systemowej bazy danych, jeśli tak, to w głównym katalogu aplikacji należy wpisać w terminalu komendę:
<pre><code>mvn clean install</code></pre>
A następnie:
<pre><code>mvn spring-boot:run</code></pre>
Aplikacja zawiera migracje bazy danych, więc automatycznie zostaną utworzone odpowiednie tabele w bazie danych.
Po zakończeniu procesu inicjalizacji aplikacja będzie gotowa do wyświetlenia pod adresem `http://localhost:8080`.

<h4>Uruchomienia projektu poprzez docker</h4>
Po pobraniu projektu należy skopiować zawartość katalogu Docker i wkleić do głównego katalogu aplikacji, w taki sposób aby nadpisać istniejące pliki. Następnie należy uruchomić terminal w głównym katalogu aplikacji i wpisać komendy:
<pre><code>docker-compose build</code></pre>
<pre><code>docker compose up</code></pre>
Po zakończeniu procesu inicjalizacji aplikacja będzie gotowa do wyświetlenia pod adresem `http://localhost:8080`.