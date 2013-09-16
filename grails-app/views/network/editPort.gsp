<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit Port</title>
</head>

<body>
<div class="body">
    <h1>Edit Port</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="savePort" method="post" class="validate" params="[id:port.id]">

        <div class="dialog">
            <table>
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td>
                        <g:textField id="name" name="name" value="${port.name}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="adminState">
                        <label for="adminState">Admin State:</label>
                    </td>
                    <td>
                        <g:if test="${port.adminStateUp}">
                            <input id="adminState" type="checkbox" name="adminState"checked="checked">
                        </g:if>
                        <g:else>
                            <input id="adminState" type="checkbox" name="adminState">

                        </g:else>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="deviceId">
                        <label for="name">Device ID:</label>
                    </td>
                    <td>
                        <g:textField id="deviceId" name="deviceId" value="${port.deviceId}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="deviceOwner">
                        <label for="deviceOwner">Device Owner:</label>
                    </td>
                    <td>
                        <g:textField id="deviceOwner" name="deviceOwner" value="${port.deviceOwner}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit id="submit" class="edit" action="savePortEdition" title="Save changes">Edit Port</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
