<%@ page import="com.paypal.aurora.Constant" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Quotas</title>
</head>

<body>
<div class="body">
    <h1>Quotas</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="list">
            <shiro:hasRole name="${Constant.ROLE_ADMIN}">
            <div class="buttons">
                <g:link elementId="edit" class="edit" action="quotas" controller="tenant" params="[parent: '/quota/list']" title="Edit quotas for this tenant">Edit</g:link>
            </div>
            </shiro:hasRole>
            <table id="table_quotaList" class="sortable">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Limit</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="quota" in="${quotas}" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td>${quota.displayName}</td>
                        <td>${quota.limit}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>

        <div class="paginateButtons">
        </div>
    </g:form>
</div>
</body>
</html>
