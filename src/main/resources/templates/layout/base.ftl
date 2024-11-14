<#import "/layout/layout.ftl" as layout>

<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <title><@layout.block name="title"/></title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/style.css">
</head>
    <body>
        <nav id="main-navbar" class="navbar navbar-expand-lg navbar-light bg-light">
            <a class="navbar-brand" href="<#if authenticated?? && authenticated>/time-tracker<#else>/</#if>">CRM</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Przełącz nawigację">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ml-auto">
                    <#if authenticated?? && authenticated>
                        <li class="nav-item">
                            <a class="nav-link<#if currentPage == 'time-tracker'> active</#if>" href="/time-tracker">Time Tracker</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link<#if currentPage == 'weekly-summary'> active</#if>" href="/weekly-summary">Podsumowanie tygodnia</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link<#if currentPage == 'calendar'> active</#if>" href="/calendar">Kalendarz</a>
                        </li>
                        <li class="nav-item">
                            <form id="logout-form" method="post" action="/logout">
                                <button type="submit" class="btn btn-link nav-link">Wyloguj</button>
                            </form>
                        </li>
                    <#else>
                        <li class="nav-item">
                            <a class="nav-link<#if currentPage == 'login'> active</#if>" href="/login">Zaloguj</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link<#if currentPage == 'register'> active</#if>" href="/register">Zarejestruj</a>
                        </li>
                    </#if>
                </ul>
            </div>
        </nav>

        <div class="container mt-4">
            <@layout.block name="content"/>
        </div>

        <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>
        <@layout.block name="scripts"/>
    </body>
</html>
