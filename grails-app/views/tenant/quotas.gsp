<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Quotas for '${tenantName}'</title>
</head>

<body>
<div class="body">
    <h1>Quotas for '${tenantName}'</h1>
    <g:if test="${params.error}">
        <div id="error_message" class="error">
            <ul><li>${params.error}</li></ul>
        </div>
    </g:if>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <input type="hidden" name="id" value="${tenantId}"/>
        <input type="hidden" name="parent" value="${parent}"/>
        <div class="list">
            <div class="buttons"></div>
            <table id="table_tenantQuotas" class="sortable">
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
                        <td><g:textField id="quota-${quota.name}" name="quota.${quota.name}" value = "${params.get("quota." + quota.name) ?: quota.limit}"/></td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" value="Save Quotas" action="saveQuotas" title="Save changes"/>
        </div>
    </g:form>
</div>
</body>
</html>
