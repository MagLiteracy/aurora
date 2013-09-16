<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit Network</title>
</head>

<body>
<div class="body">
    <h1>Edit Network</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="saveEdition" method="post" class="validate">
        <div class="dialog">
            <table id="table_editNetwork">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td>
                        <g:textField id="name" name="name" value="${network.name}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="id">
                        <label for="id">ID:</label>
                    </td>
                    <td>
                        <g:textField id="id" name="id" value="${network.id}" readonly="readonly"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="adminState">
                        <label for="adminState">Admin State:</label>
                    </td>
                    <td>
                        <g:if test="${network.adminStateUp}">
                            <input id="adminState" type="checkbox" name="adminState" checked="checked">
                        </g:if>
                        <g:else>
                            <input id="adminState" type="checkbox" name="adminState">
                        </g:else>

                    </td>
                </tr>

                <tr class="prop">
                    <td class="shared">
                        <label for="shared">Shared:</label>
                    </td>
                    <td>
                        <g:if test="${network.shared}">
                            <input id="shared" type="checkbox" name="shared" checked="checked">
                        </g:if>
                        <g:else>
                            <input id="shared" type="checkbox" name="shared">
                        </g:else>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="external">
                        <label for="external">External Network:</label>
                    </td>
                    <td>
                        <g:if test="${network.external}">
                            <input id="external" type="checkbox" name="external" checked="checked">
                        </g:if>
                        <g:else>
                            <input id="external" type="checkbox" name="external">
                        </g:else>

                    </td>
                </tr>

                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit id="submit" class="save" action="saveEdition" title="Save changes">Edit Network</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
