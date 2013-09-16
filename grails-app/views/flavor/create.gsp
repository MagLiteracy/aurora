<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create New Flavor</title>
</head>

<body>
<div class="body">
    <h1>Create New Flavor</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" class="validate">
        <input type="hidden" name="fromUser" value="true"/>

        <div class="dialog">
            <table id="table_createFlavor">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td>
                        <g:textField id="name" name="name" value="${params.name}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="ram">Memory size in MB:</label>
                    </td>
                    <td>
                        <g:textField id="ram" name="ram" value="${params.ram}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="disk">Disk size in GB:</label>
                    </td>
                    <td>
                        <g:textField id="disk" name="disk" value="${params.disk}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="vcpus">Number of vcpus:</label>
                    </td>
                    <td>
                        <g:textField id="vcpus" name="vcpus" value="${params.vcpus}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="isPublic">Make flavor accessible to the public:</label>
                    </td>
                    <td>
                        <g:checkBox id="isPublic" name="isPublic" checked="${params.isPublic == 'on' ? 'true' : ''}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="ephemeral">Ephemeral space size in GB (optional):</label>
                    </td>
                    <td>
                        <g:textField id="ephemeral" name="ephemeral" value="${params.ephemeral}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="swap">Swap space size in MB (optional):</label>
                    </td>
                    <td>
                        <g:textField id="swap" name="swap" value="${params.swap}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="rxtxFactor">RX/TX factor (optional):</label>
                    </td>
                    <td>
                        <g:textField id="rxtxFactor" name="rxtxFactor" value="${params.rxtxFactor}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit id="submit" class="save" action="save" title="Create new flavor with selected parameters">Create New Fravor</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
