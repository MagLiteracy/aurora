<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>User '${user.name}'</title>
</head>

<body>
<div class="body">
    <h1>User '${user.name}'</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" id="id" name="id" value="${user.id}"/>
        <div class="buttons">
            <g:link elementId="edit" class="edit" action="edit" params="[id: user.id]" title="Edit user attributes">Edit User</g:link>
            <g:buttonSubmit class="delete" id="delete" value="Delete user" action="delete"
                            data-warning="Really delete user?" title="Delete this user"/>
        </div>
        <div class="dialog">
            <table id="OSUserShow">
                <tbody>
                <tr class="prop">
                    <td class="name">Name:</td>
                    <td class="value">${user.name}</td>
                </tr>
                <tr class="prop">
                    <td class="email">Email:</td>
                    <td class="value">${user.email}</td>
                </tr>
                <tr class="prop">
                    <td class="name">Default Tenant:</td>
                    <td class="value">${tenant.name}</td>
                </tr>
                <g:if test="${tenant.id}">
                    <tr class="prop">
                        <td class="name">Default Tenant ID:</td>
                        <td class="value"><g:linkObject elementId="${tenant.id}" type="tenant" id="${tenant.id}"/></td>
                    </tr>
                </g:if>
            </table>
        </div>
        <div class="buttons">
        </div>
    </g:form>
</div>
</body>
</html>
