<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Router Details</title>
</head>
<body>
<div class="body">
    <h1>Router Details</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <div class="buttons">
            <g:form>
                <input type="hidden" id="id" name="id" value="${router.id}"/>
                <g:if test="${router.externalGatewayInfo}">
                    <g:buttonSubmit id="clearGateway" class="delete" value="Clear gateway(s)" action="clearGateway"
                                    data-warning="Really clear gateway?" params="[id:router.id]" title="Clear Gateway for this router"/>
                </g:if>
                <g:else>
                    <g:link class="edit" action="setGateway" elementId="setGateway" params="[id:router.id]" title="Set Gateway for this router">Set Gateway</g:link>
                </g:else>
            </g:form>
        </div>
        <table id="table_showRouter">
            <tbody>
            <tr class="header">Router Overview</tr>
            <tr class="prop">
                <td class="name">Name:</td>
                <td class="value">${router.name == '' ? 'None' : router.name}</td>
            </tr>
            <tr class="prop">
                <td class="name">ID:</td>
                <td class="value">${router.id}</td>
            </tr>
            <tr class="prop">
                <td class="name">Status:</td>
                <td class="value">${router.status}</td>
            </tr>
            <tr class="prop">
                <td class="name">External Gateway Information:</td>
                <td class="value">${router.externalGatewayInfo ? 'Connected External Network:' + router.externalGatewayInfo.networkName : '-'}</td>
            </tr>
            </tbody>
        </table>

        <g:form method="post" class="validate">
            <h2>Interfaces</h2>
            <div class="list">
                <div class="buttons">
                    <g:link elementId="addInterface" class="create" action="addInterface" params="[id:router.id]" title="Add new interface for this router">Add Interface</g:link>
                    <g:buttonSubmit id="deleteInterface" class="delete" value="Remove Interface(s)" action="deleteInterface"
                                    data-warning="Really remove Interface(s)?" title="Delete selected interface(s)"/>
                </div>
                <table class="sortable" id="subnets">
                    <tr>
                        <th class="checkboxTd">&thinsp;x</th>
                        <th>Name</th>
                        <th>Fixed IPs</th>
                        <th>Status</th>
                        <th>Type</th>
                        <th>Admin State</th>
                    </tr>
                    <g:each in="${router.ports}" var="port" status="i">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td><g:if test="${port}"><g:checkBox name="selectedPorts" value="${port.id}"
                                                                   checked="0" class="requireLogin"/></g:if></td>
                            <td><g:linkObject type="network" displayName="${port.name == "" ? '(' + port.id.substring(0,8) + ')' : port.name}"
                                                  id ="${port.id}" action="showPort" elementId="port-${port.id}"/></td>
                            <td>
                                <g:each in="${port.fixedIps}" var="fixedIp">
                                    ${fixedIp.ip_address} <br/>
                                </g:each>
                            </td>
                            <td>${port.status}</td>
                            <td>-</td>
                            <td>${port.adminStateUp ? 'UP' : 'DOWN'}</td>
                        </tr>
                    </g:each>
                </table>
                <g:hiddenField name="id" id="id" value="${router.id}"/>
            </div>
        </g:form>
    </div>
</div>
</body>
</html>
