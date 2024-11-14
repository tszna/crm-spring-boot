<#import "/layout/layout.ftl" as layout>
<@layout.extends name="base.ftl"/>

<@layout.block name="title">
    Dodaj nieobecność
</@layout.block>

<@layout.block name="content">
    <div class="container">
        <h1>Dodaj nieobecność</h1>

        <#if (errors?? && errors?size > 0)>
            <div class="alert alert-danger">
                <ul>
                    <#list errors as error>
                        <li>${error}</li>
                    </#list>
                </ul>
            </div>
        </#if>

        <form action="/calendar/store" method="POST">
            <div class="form-group">
                <label for="start_date">Data początkowa</label>
                <input type="date" name="startDate" id="start_date" class="form-control" required>
            </div>

            <div class="form-group mt-3">
                <label for="end_date">Data końcowa</label>
                <input type="date" name="endDate" id="end_date" class="form-control" required>
            </div>

            <div class="form-group mt-3">
                <label for="reason">Powód</label>
                <select name="reason" id="reason" class="form-control" required>
                    <option value="Urlop_zwykły">Urlop zwykły</option>
                    <option value="Urlop_bezpłatny">Urlop bezpłatny</option>
                    <option value="Nadwyżka">Nadwyżka</option>
                    <option value="Praca_zdalna">Praca zdalna</option>
                    <option value="Delegacja">Delegacja</option>
                    <option value="Choroba">Choroba</option>
                    <option value="Inny">Inny</option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary mt-4">Zapisz</button>
        </form>
    </div>
</@layout.block>
