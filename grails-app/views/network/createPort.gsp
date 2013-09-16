<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Port</title>
</head>

<body>
<div class="body">
    <h1>Create Port</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="savePort" method="post" class="validate">

        <div class="dialog">
            <table id="table_createPort">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td>
                        <g:textField id="name" name="name"/>
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
                    <td class="deviceId">
                        <label for="name">Device ID:</label>
                    </td>
                    <td>
                        <g:textField id="deviceId" name="deviceId"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="deviceOwner">
                        <label for="deviceOwner">Device Owner:</label>
                    </td>
                    <td>
                        <g:textField id="deviceOwner" name="deviceOwner"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="networkId">
                        <label for="networkId">Network ID:</label>
                    </td>
                    <td>
                        <g:textField id="networkId" name="networkId" value="${params.networkId}" readonly="readonly"/>
                    </td>
                </tr>


                <tr class="prop">
                    <td class="tenantId"></td>
                    <g:hiddenField id="tenantId" name="tenantId" value="${params.tenantId}"/>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit id="submit" class="save" action="savePort" title="Create port with selected parameters">Create Port</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
