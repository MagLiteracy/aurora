<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Users</title>
</head>

<body>
<div class="body">
    <h1>Users</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="list">
            <div class="buttons">
                <g:link elementId="create" class="create" action="create" title="Create new user">Create User</g:link>
                <g:if test="${openStackUsers}">
                    <g:buttonSubmit class="delete" id="delete" value="Delete user(s)" action="delete"
                                    data-warning="Really delete user(s)?" title="Delete selected user(s)"/>
                </g:if>
            </div>
            <table id="table_OSUserList" class="sortable">
                <thead>
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>User Name</th>
                    <th>User Email</th>
                    <th>Enabled</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="openStackUser" in="${openStackUsers}" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:checkBox id="checkBox_${openStackUser.id}" name="selectedUsers" value="${openStackUser.id}" checked="0"/></td>
                        <td><g:linkObject displayName="${openStackUser.name}" elementId="openStackUser-${openStackUser.id}" type="openStackUser" id="${openStackUser.id}"/></td>
                        <td>${openStackUser.email}</td>
                        <td>${openStackUser.enabled ? 'enabled' : 'disabled'}</td>
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
