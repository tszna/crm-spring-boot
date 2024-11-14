<#import "/layout/layout.ftl" as layout>
<@layout.extends name="base.ftl"/>

<@layout.block name="title">
</@layout.block>

<@layout.block name="content">
    <div class="container">
        <h1>Kalendarz nieobecności - ${formattedCurrentMonth}</h1>

        <#if (successMessage??)>
            <div class="alert alert-success">
                ${successMessage}
            </div>
        </#if>

        <form action="/calendar" method="GET" class="mb-4">
            <input type="hidden" name="monthOffset" value="${monthOffset}">

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
                    <a href="/calendar?monthOffset=${monthOffset - 1}&user_id=${selectedUserId}" class="btn btn-primary">
                        Poprzedni miesiąc
                    </a>
                    <a href="/calendar?monthOffset=${monthOffset + 1}&user_id=${selectedUserId}" class="btn btn-primary">
                        Następny miesiąc
                    </a>
                </div>
            </div>
        </form>
        <#if (errorMessage??)>
            <div class="alert alert-danger">
                ${errorMessage}
            </div>
        <#else>

            <a href="/calendar/create" class="btn btn-info mb-3">Dodaj nieobecność</a>

            <table class="table table-bordered">
                <thead>
                    <tr>
                        <th style="width: 33.33%;">Dzień miesiąca</th>
                        <th style="width: 33.33%;">Dzień tygodnia</th>
                        <th style="width: 33.33%;">Status</th>
                    </tr>
                </thead>
                <tbody>
                    <#list calendar?keys as day>
                    <tr <#if calendar[day]["is_today"]>style="border: 3px solid;"</#if>>
                        <td style="width: 33.33%;">${day}</td>
                        <td style="width: 33.33%;">${calendar[day]["day_of_week"]}</td>
                        <td style="width: 33.33%;">${calendar[day]["status"]}</td>
                    </tr>
                    </#list>
                </tbody>
            </table>
        </#if>
    </div>
</@layout.block>
