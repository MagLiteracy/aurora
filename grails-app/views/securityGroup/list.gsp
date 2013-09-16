<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Security Groups</title>
</head>

<body>
<div class="body">
    <h1>Security Groups</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="list">
            <div class="buttons">
                <g:link elementId="create" class="create" action="create" title=" Create new security group">Create New Security Group</g:link>
            </div>
            <table id="table_securityGroupList" class="sortable">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="grp" in="${securityGroups}" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:linkObject elementId="securityGroup-${grp.id}" displayName="${grp.name}" type="securityGroup" id="${grp.id}"/></td>
                        <td>${grp.description}</td>
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
