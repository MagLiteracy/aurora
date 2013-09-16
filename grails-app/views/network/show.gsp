<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Network Details</title>
</head>
<body>
<div class="body">
    <h1>Network Details</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <div class="buttons">
            <g:link class="edit" action="edit" elementId="edit" params="[id:network.id]" title="Edit this network">Edit Network</g:link>
        </div>
        <table id="table_showNetwork">
            <tbody>
            <tr class="header">Network Overview</tr>
            <tr class="prop" title="Application name from Cloud Application Registry">
                <td class="name">Name:</td>
                <td class="value">${network.name == "" ? 'None' : network.name}</td>
            </tr>
            <tr class="prop">
                <td class="name">ID:</td>
                <td class="value">${network.id}</td>
            </tr>
            <tr class="prop">
                <td class="name">Project ID:</td>
                <td class="value">${network.projectId}</td>
            </tr>
            <tr class="prop">
                <td class="name">Status:</td>
                <td class="value">${network.status}</td>
            </tr>
            <tr class="prop">
                <td class="name">Admin State:</td>
                <td class="value">${network.adminStateUp ? 'UP' : 'DOWN'}</td>
            </tr>
            <tr class="prop">
                <td class="name">Shared:</td>
                <td class="value">${network.shared ? 'YES' : 'NO'}</td>
            </tr>
            <tr class="prop">
                <td class="name">External Network:</td>
                <td class="value">${network.external ? 'YES' : 'NO'}</td>
            </tr>
            </tbody>
        </table>

        <g:form method="post" class="validate">
        <h2>Subnets</h2>
        <div class="list">
            <div class="buttons">
                <g:link elementId="createSubnet" class="create" action="createSubnet" params="[networkId:network.id,tenantId:network.projectId]" title="Create new subnet for this network">Create Subnet</g:link>
                <g:if test="${network.subnets.size > 0}"><g:buttonSubmit id="deleteSubnet" class="delete" value="Remove Subnet(s)" action="deleteSubnet"
                                data-warning="Really remove Subnet(s)?" title="Remove selected subnet(s)"/> </g:if>
            </div>
            <table class="sortable" id="subnets">
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>Name</th>
                    <th>CIDR</th>
                    <th>IP Version</th>
                    <th>Gateway IP</th>
                </tr>
                <g:each in="${network.subnets}" var="subnet" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:if test="${subnet}"><g:checkBox name="selectedSubnets" value="${subnet.id}"
                                    checked="0" class="requireLogin"/></g:if></td>
                        <td><g:linkObject type="network" displayName="${subnet.name == "" ? '(' + subnet.id.substring(0,8) + ')' : subnet.name}"
                                    id ="${subnet.id}" action="showSubnet" elementId="subnet-${subnet.id}"/></td>
                        <td>${subnet.cidr}</td>
                        <td>IPv${subnet.ipVersion}</td>
                        <td>${subnet.gatewayIp}</td>
                    </tr>
                </g:each>
            </table>
            <g:hiddenField name="networkId" id="networkId" value="${network.id}"/>
        </div>
        </g:form>

        <g:form method="post" class="validate">
        <h2>Ports</h2>
        <div class="list">
            <div class="buttons">
                <g:link elementId="createPort" class="create" action="createPort" params="[networkId:network.id,tenantId:network.projectId]" title="Create new port for this network">Create Port</g:link>
                <g:if test="${ports.size > 0}"><g:buttonSubmit id="deletePort" class="delete" value="Remove Port(s)" action="deletePort"
                                data-warning="Really remove Port(s)?" title="Remove selected port(s)"/></g:if>
            </div>
            <table class="sortable twoTablesOnPage" id="ports">
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>Name</th>
                    <th>Fixed IPs</th>
                    <th>Device Attached</th>
                    <th>Status</th>
                    <th>Admin State</th>
                </tr>
                <g:each in="${ports}" var="port" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:if test="${port.id}"><g:checkBox name="selectedPorts" value="${port.id}"
                                                                  checked="0" class="requireLogin"/></g:if></td>
                        <td><g:linkObject type="network" displayName="${port.name == "" ? '(' + port.id.substring(0,8) + ')': port.name}"
                                 id ="${port.id}" action="showPort" elementId="port-${port.id}"/></td>
                        <td>
                            <g:each in="${port.fixedIps}" var="fixedIp">
                               ${fixedIp.ip_address} <br/>
                            </g:each>
                        </td>
                        <td>${port.deviceId == "" ? 'Not Attached' : 'Attached'} </td>
                        <td>${port.status}</td>
                        <td>${port.adminStateUp ? 'UP' : 'DOWN'}</td>
                    </tr>
                </g:each>
            </table>
            <g:hiddenField name="networkId" id="networkId" value="${network.id}"/>
        </div>
        </g:form>

    </div>
</div>
</body>
</html>
