<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Tenant</title>
</head>

<body>
<div class="body">
    <h1>Create Tenant</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" class="validate allowEnterKeySubmit">
        <div class="dialog">
            <table id="table_tenantCreate">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td>
                        <input type="text" id="name" name="name" value="${params.name}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="name">
                        <label for="description">Description:</label>
                    </td>
                    <td>
                        <input type="text" id="description" name="description" value="${params.description}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="name">
                        <label for="enabled">Enabled:</label>
                    </td>
                    <td class="value">
                        <g:checkBox id="enabled" name="enabled" checked="${params.enabled}"/>
                    </td>
                </tr>

                <g:if test="${keystoneCustomTenancy}">
                <tr class="prop">
                    <td class="name">
                        <label for="zones">DNS zones:</label>
                    </td>
                    <td class="value">
                        <g:textArea rows="10" cols="60" id="zones" name="zones" value="${params.zones}"/>
                    </td>
                </tr>
                </g:if>

                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" action="save" title="Create new tenant">Create Tenant</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
