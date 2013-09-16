<%@ page import="com.paypal.aurora.Constant" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>About</title>
</head>

<body>
<div class="body">
    <h1>About</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="list">
            <table id="table_infoList" class="sortable">
                <thead>
                <tr>
                    <th>Parameter</th>
                    <th>Value</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="quota" in="${info}" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td>${quota.key}</td>
                        <td>${quota.value}</td>
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
