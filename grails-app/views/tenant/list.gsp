<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Tenants</title>
</head>
<body>
<div class="body">
    <h1>Tenants</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post" class="validate">
        <div class="list">
            <div class="buttons">
                <g:link elementId="create" class="create" action="create" title="Create new tenant">Create Tenant</g:link>
                <g:if test="${tenants}">
                    <g:buttonSubmit class="delete" id="delete" value="Remove Tenant(s)" action="delete"
                                    data-warning="Really remove tenant(s)?" title="Remove selected tenant(s)"/>
                </g:if>
            </div>
            <table id="table_tenantList" class="sortable">
                <thead>
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Enabled</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="tenant" in="${tenants}" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:if test="${tenant.id}"><g:checkBox id="checkBox_${tenant.id}" name="selectedTenants" value="${tenant.id}"
                                                                      checked="0"/></g:if></td>
                        <td><g:linkObject elementId="tenant-${tenant.id}" displayName="${tenant.name}" type="tenant" id="${tenant.id}"/></td>
                        <td>${tenant.description}</td>
                        <td>${tenant.enabled}</td>
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
