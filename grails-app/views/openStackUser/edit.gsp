<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit user '${user.name}'</title>
</head>

<body>
<div class="body">
    <h1>Edit user '${user.name}'</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" name="id" value="${user.id}"/>
        <div class="dialog">
            <table id="table_OSUserEdit">
                <tbody>
                <tr>
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td class="value">
                        <g:textField id="name" name="name" value="${user.name}"/>
                    </td>
                </tr>
                <tr>
                    <td class="email">
                        <label for="email">Email:</label>
                    </td>
                    <td class="value">
                        <g:textField id="email" name="email" value="${user.email}" autocomplete="off"/>
                    </td>
                </tr>
                <tr>
                    <td class="name">
                        <label for="password">Password:</label>
                    </td>
                    <td>
                        <input type="password" id="password" name="password" autocomplete="off"/>
                    </td>
                </tr>
                <tr>
                    <td class="name">
                        <label for="confirm_password">Confirm Password:</label>
                    </td>
                    <td>
                        <input type="password" id="confirm_password" name="confirm_password"/>
                    </td>
                </tr>
                <tr>
                    <td class="name">
                        <label>Default Tenant:</label>
                    </td>
                    <td>
                        <select id="tenant_id" name="tenant_id">
                            ${contain ? "" : '<option value="null" selected="true">Choose tenant</option>'}
                            <g:each var="tenant" in="${tenants}" status="i">
                                <option value="${tenant.id}" ${tenant.id == user.tenantId ? 'selected="true"' : ""}>${tenant.name}</option>
                            </g:each>
                        </select>
                    </td>
                </tr>
            </table>
        </div>
        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" value="Update User" action="update" title="Save changes"/>
        </div>
    </g:form>
</div>
</body>
</html>
