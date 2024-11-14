<#import "/layout/layout.ftl" as layout>
<@layout.extends name="base.ftl"/>

<@layout.block name="title">
</@layout.block>

<@layout.block name="content">
    <div class="container mt-4">
        <h1>Dzisiaj jest: ${currentDate}</h1>
        <span id="loading-btn" class="loader" style="display: none;"></span>
        <!-- Div z time-tracker, początkowo ukryty -->
        <div id="content_container" style="display: none;">
            <button id="start-btn" class="btn btn-success">Start</button>
            <button id="stop-btn" class="btn btn-danger" disabled>Stop</button>
            <div id="session-info" style="margin-top: 20px;">
                <p>Godzina startu: <span id="start-time"></span></p>
                <p>Godzina zakończenia: <span id="end-time"></span></p>
                <p>Czas sesji: <span id="current-time">00:00:00</span></p>
            </div>
        </div>
    </div>
</@layout.block>

<@layout.block name="scripts">
    <script src="/js/time_tracker.js"></script>
</@layout.block>
