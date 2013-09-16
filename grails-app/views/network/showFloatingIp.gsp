<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>IP '${floatingIp.ip}'</title>
</head>

<body>
<div class="body">
    <h1>IP '${floatingIp.ip}'</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>

    <g:form method="post">
        <input type="hidden" id="ip" name="ip" value="${floatingIp.ip}">
        <input type="hidden" id="id" name="id" value="${floatingIp.id}">

        <div class="buttons">
            <g:if test="${floatingIp.instanceId}">
                <input type="hidden" id="instanceId" name="instanceId" value="${floatingIp.instanceId}">
                <g:buttonSubmit id='disassociate' class="floatingIP" action="disassociateIp"
                                value="Disassociate floating IP" title="Disassociate this floating IP address"/>
            </g:if>
            <g:else>
                <g:link class="associate" elementId="associate" action="associateFloatingIp" params="[ip:floatingIp.ip]" title="Associate this floating IP address">Associate floating IP</g:link>
            </g:else>
        </div>
        <div class="dialog">
            <table id="FloatingIpShow">
                <tbody>
                <tr class="prop">
                    <td class="name">ID:</td>
                    <td class="value">${floatingIp.id}</td>
                </tr>
                <tr class="prop">
                    <td class="name">Floating IP:</td>
                    <td class="value">${floatingIp.ip}</td>
                </tr>
                <tr class="prop">
                    <td class="name">Pool:</td>
                    <td class="value">${floatingIp.pool}</td>
                </tr>
                <g:if test="${floatingIp.instanceId}">
                    <tr class="prop">
                        <td class="email">Instance:</td>
                        <td class="value"><g:linkObject type="instance" id="${floatingIp.instanceId}"
                                                        displayName="${instances.find { it.instanceId == floatingIp.instanceId }.name}"/></td>
                    </tr>
                    <tr class="prop">
                        <td class="email">Fixed IP:</td>
                        <td class="value">${floatingIp.fixedIp}</td>
                    </tr>
                </g:if>
            </table>
        </div>
        <div class="buttons">
        </div>
    </g:form>
</div>
</body>
</html>