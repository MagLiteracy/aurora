<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit Users</title>
    <script type="text/javascript">
        var tenantId = "${tenant.id}";
        var usersArray = [];
        usersArray = {
            <g:each var="userRole" in="${usersRoles}">
            "${users.find{it.id == userRole.key}.name}": "${userRole.value.id}",
            </g:each>
        };

        var rolesArray = [];
        rolesArray   = {
            <g:each var="role" in="${roles}">
            "${role.id}": "${role.name}",
            </g:each>
        };

    </script>
    <script type="text/javascript"  src="${resource(dir: 'js', file: 'jquery.json-2.3.min.js')}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'usersRoles.js')}"></script>
</head>
<body>
<div class="body">
    <h1>Edit Users for "${tenant.name}"</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <div id="error" class="error"></div>
    <g:if test="${manyUsers}">
        <div id="table_container">
            <g:render template="userRole"/>
        </div>
    </g:if>
    <g:else>
        <div id="userRolesDialog" title="Edit users" style="background: #F5F5F5 !important;">
            <table id="table_tenantUsers" class="usersListContainer">
                <tr>
                    <td id="leftSideListContainer"></td>
                    <td id="rightSideListContainer"></td>
                </tr>
            </table>
        </div>
        <div class="buttons">
            <a href="#" id="usersRolesReset" class="reboot" title="Reset unsaved changes">Reset Changes</a>
            <a href="#" id="usersRolesSubmit" class="save" title="Save changes">Submit Changes</a>
        </div>
    </g:else>
</div>
</body>
</html>
