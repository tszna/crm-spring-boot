<#setting locale="pl_PL">
<#import "/layout/layout.ftl" as layout>
<@layout.extends name="base.ftl"/>

<@layout.block name="head">
    <style>
       
    </style>
</@layout.block>

<@layout.block name="title">
</@layout.block>

<@layout.block name="content">
    <div class="container">
        <form action="/weekly-summary" method="GET" class="mb-4">
            <input type="hidden" name="weekOffset" value="${weekOffset}">

            <div class="row">
                <div class="col-md-4">
                    <select name="user_id" class="form-control" onchange="this.form.submit()">
                        <#list users as user>
                            <option value="${user.id}" <#if user.id == selectedUserId>selected</#if>>
                                ${user.name}
                            </option>
                        </#list>
                    </select>
                </div>
                <div class="col-md-8 text-right">
                    <a href="/weekly-summary?weekOffset=${weekOffset - 1}&user_id=${selectedUserId}" class="btn btn-primary me-2">
                        Poprzedni tydzień
                    </a>
                    <a href="/weekly-summary?weekOffset=${weekOffset + 1}&user_id=${selectedUserId}" class="btn btn-primary">
                        Następny tydzień
                    </a>
                </div>
            </div>
        </form>

        <h1>Podsumowanie tygodnia</h1>
        <#if errorMessage??>
            <div class="alert alert-danger">
                ${errorMessage}
            </div>
        <#else>
            <table class="table">
                <thead>
                    <tr>
                        <th>Dzień</th>
                        <th>Całkowity czas</th>
                        <th>Status</th>
                    </tr>
                </thead>
                <tbody>
                    <#list dailySummary?keys as date>
                        <tr>
                            <td style="vertical-align: middle;">
                                ${date?date("yyyy-MM-dd")?string("EEEE, yyyy-MM-dd")}
                            </td>
                            <td style="vertical-align: middle;">${dailySummary[date]["time"]}</td>
                            <td style="vertical-align: middle;">
                                <#if dailySummary[date]["is_active"]>
                                    <span style="font-size: 32px; vertical-align: middle; color: yellow">&#9679;</span>
                                <#else>
                                    <span style="font-size: 32px; vertical-align: middle;">&#9679;</span>
                                </#if>
                            </td>
                        </tr>
                    </#list>
                </tbody>
                <tfoot>
                    <tr>
                        <th style="vertical-align: middle;">Suma tygodniowa</th>
                        <th style="vertical-align: middle;">${weeklyTotal}</th>
                        <th></th>
                    </tr>
                </tfoot>
            </table>
        </#if>
    </div>
</@layout.block>
