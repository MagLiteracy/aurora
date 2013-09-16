<%@ page import="com.paypal.aurora.Constant" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Services</title>
</head>

<body>
<div class="body">
    <h1>Services</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="list">
            <div class="buttons"></div>
            <table id="table_ossList" class="sortable">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Service</th>
                    <th>URL</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="openStackService" in="${openStackServices}" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td>${openStackService.name}</td>
                        <td>${openStackService.type}</td>
                        <td>
                            <shiro:hasRole name="${Constant.ROLE_ADMIN}">
                                <g:if test="${openStackService.adminURL}">
                                    <b>Admin URL:</b>  ${openStackService.adminURL} <br>
                                </g:if>
                                <b>Public URL:</b>
                            </shiro:hasRole>
                            ${openStackService.publicURL}
                        </td>
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
