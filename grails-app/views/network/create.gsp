<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create New Network</title>
</head>

<body>
<div class="body">
    <h1>Create New Network</h1>
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
            <table id="table_createNetwork">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td>
                        <g:textField id="name" name="name"/>
                    </td>
                </tr>

                <tr id="tenantSources" class="prop">
                    <td class="tenant">
                        <label for="tenant">Project:</label>
                    </td>
                    <td>
                        <select id="tenant" name="tenant">
                            <g:each in="${params.tenants}" var="tenant">
                                <option value="${tenant.id}">${tenant.name}</option>
                            </g:each>
                        </select>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="adminState">
                        <label for="adminState">Admin State:</label>
                    </td>
                    <td>
                        <input id="adminState" type="checkbox" name="adminState">
                    </td>
                </tr>

                <tr class="prop">
                    <td class="shared">
                        <label for="shared">Shared:</label>
                    </td>
                    <td>
                        <input id="shared" type="checkbox" name="shared">
                    </td>
                </tr>

                <tr class="prop">
                    <td class="external">
                        <label for="external">External Network:</label>
                    </td>
                    <td>
                        <input id="external" type="checkbox" name="external">
                    </td>
                </tr>

                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit id="submit" class="save" action="save" title="Create network with selected parameters">Create New Network</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
