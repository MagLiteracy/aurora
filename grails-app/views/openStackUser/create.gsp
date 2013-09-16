<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create User</title>
</head>

<body>
<div class="body">
    <h1>Create User</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" class="validate">
        <div class="dialog">
            <table id="table_OSUserCreate">
                <tbody>
                <tr class="prop">
                    <td class="name"><label for="name">Name:</label></td>
                    <td><g:textField id="name" name="name" value="${params.name}"/></td>
                </tr>

                <tr class="prop">
                    <td class="name"><label for="email">Email:</label></td>
                    <td><g:textField id="email" name="email" value="${params.email}" autocomplete="off"/></td>
                </tr>
                <tr>
                    <td class="name"><label for="password">Password:</label></td>
                    <td><input type="password" id="password" name="password" autocomplete="off"/></td>
                </tr>
                <tr>
                    <td class="name"><label for="confirm_password">Confirm Password:</label></td>
                    <td><input type="password" id="confirm_password" name="confirm_password"/></td>
                </tr>
                <tr>
                    <td><label for="tenant_id">Default Tenant:</label></td>
                    <td>
                        <g:select id="tenant_id" name="tenant_id" from="${tenants}" optionKey="id" optionValue="name" value="${currentTenantId}" />
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="role_id">Role:</label>
                    </td>
                    <td>
                        <g:select id="role_id" name="role_id" from="${roles}" optionKey="id" optionValue="name" />
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit class="create" id="submit" action="save" title="Create new user">Create User</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
