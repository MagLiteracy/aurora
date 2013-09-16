<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Add new pool</title>
</head>
<body>
<div class="body">
    <h1>Add new pool</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <div class="dialog">
        <table id="table_lbassAddPool">
            <tr>
                <td><label for="name">Pool Name:</label></td>
                <td><g:textField type="text" id="name" name="name" value="${params.name}"/></td>
            </tr>
            <tr>
                <td><label for="enabled">Enabled:</label></td>
                <td><g:checkBox id="enabled" name="enabled" value="${params.enabled}"/></td>
            </tr>
            <tr>
                <td><label for="lbMethod">LB Method:</label></td>
                <td>
                    <g:select id="lbMethod" name="lbMethod" from="${methods}" value="${params.lbMethod}"/>
                </td>
            </tr>
            <tr>
                <td class="valignTop"><label for="monitors">Monitor:</label></td>
                <td>
                    <g:each in="${monitors}" var="monitor">
                        <g:checkBox id="monitors-${monitor}" name="monitors"
                               value="${monitor}" checked="${params.monitors?.contains(monitor)}"/>${monitor}<br>
                    </g:each>
                </td>
            </tr>
            <tr>
                <td><label for="instances">Select Instance(s):</label></td>
                <td>
                    <g:each in="${instances}" var="instance">
                        <g:checkBox name="instances"
                               value="${instance.instanceId}" checked="${params.instances?.contains(instance.instanceId)}"/>${instance.name}<br>
                    </g:each>
                </td>
            </tr>
            <g:if test="${! isUseQuantumFLIP}">
                <tr>
                    <td><label for="netInterface">Network Interface:</label></td>
                    <td><select id="netInterface" name="netInterface">
                        <g:if test="${instances.size() != 0}">
                            <g:each var="netInterface" in="${instances[0].networks}">
                                <option value="${netInterface.pool}"
                                        selected="${params.netInterface == netInterface.pool}">${netInterface.pool}</option>
                            </g:each>
                        </g:if>
                    </select></td>
                </tr>
            </g:if>
            <tr>
                <td><label for="servicePort">Service Port:</label></td>
                <td><input id="servicePort" type="number" min="1" max="65535" name="servicePort" value="${params.servicePort}"/></td>
            </tr>
            <tr>
                <td><label for="serviceWeight">Service Weight:</label></td>
                <td><input id="serviceWeight" type="number" min="1" max="100" name="serviceWeight" value="${params.serviceWeight?:10}"/></td>
            </tr>
            <tr>
                <td><label for="serviceEnabled">Service Enabled:</label></td>
                <td><g:checkBox id="serviceEnabled" name="serviceEnabled" value="${params.serviceEnabled}"/></td>
            </tr>
        </table>
            </div>
        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" value="Add new pool" action="savePool" title="Add new pool with selected parameters"/>
        </div>
    </g:form>
</div>
</body>
</html>