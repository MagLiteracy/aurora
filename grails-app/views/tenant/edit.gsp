<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit Tenant</title>

</head>

<body>
<g:hasErrors bean="${cmd}">
    <div id="error_message" class="error">
        <g:renderErrors bean="${cmd}" as="list"/>
    </div>
</g:hasErrors>

<div class="body">
    <h1>Edit Tenant</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post" class="allowEnterKeySubmit">
        <input type="hidden" name="id" value="${tenant.id}"/>
        <input type="hidden" name="tenantName" value="${tenant.name}"/>
        <div class="buttons">
        </div>
        <div class="dialog">
            <table id="table_tenantEdit">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td class="value">
                        <g:textField type="text" id="name" name="name" value="${tenant.name}"/>
                    </td>
                </tr>
                <tr>
                    <td class="name">
                        <label for="description">Description:</label>
                    </td>
                    <td class="value">
                        <g:textField type="text" id="description" name="description"
                                     value="${tenant.description}"/>
                    </td>
                </tr>
                <tr>
                    <td class="name">
                        <label for="enabled">Enabled:</label>
                    </td>
                    <td class="value">
                        <g:checkBox id="enabled" name="enabled" checked="${tenant.enabled}"/>
                    </td>
                </tr>
                <g:if test="${keystoneCustomTenancy}">
                    <tr class="prop">
                        <td class="name">
                            <label for="zones">DNS zones:</label>
                        </td>
                        <td class="value">
                            <g:textArea rows="10" cols="60" id="zones" name="zones" value="${tenant.zones}"/>
                        </td>
                    </tr>
                </g:if>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" value="Update Tenant" action="update" title="Save changes"/>
        </div>
    </g:form>
</div>
</body>
</html>
