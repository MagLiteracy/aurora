<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Port Details</title>
</head>
<body>
<div class="body">
    <h1>Port Details</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <div class="buttons">
            <g:link elementId="edit" class="edit" action="editPort" params="[id:port.id]" title="Edit this port">Edit Port</g:link>
        </div>
        <table id="table_showPort">
            <tbody>
            <tr class="header">Port Overview</tr>
            <tr class="prop" title="Application name from Cloud Application Registry">
                <td class="name">Name:</td>
                <td class="value">${port.name == "" ? 'None' : port.name}</td>
            </tr>
            <tr class="prop">
                <td class="name">ID:</td>
                <td class="value">${port.id}</td>
            </tr>
            <tr class="prop">
                <td class="name">Network ID:</td>
                <td class="value">${port.networkId}</td>
            </tr>
            <tr class="prop">
                <td class="name">Project ID:</td>
                <td class="value">${port.tenantId}</td>
            </tr>
            <tr class="prop">
                <td class="name">Fixed IPs:</td>
                <td class="value">
                    <g:each in="${port.fixedIps}" var="fixedIp">
                        <strong>Subnet ID</strong> ${fixedIp.subnet_id},  <strong>IP Address</strong> ${fixedIp.ip_address} <br/> <br/>
                    </g:each>
                </td>
            </tr>
            <tr class="prop">
                <td class="name">Mac Address:</td>
                <td class="value">${port.macAddress}</td>
            </tr>
            <tr class="prop">
                <td class="name">Status:</td>
                <td class="value">${port.status}</td>
            </tr>
            <tr class="prop">
                <td class="name">Admin State:</td>
                <td class="value">${port.adminStateUp ? 'UP' : 'DOWN'}</td>
            </tr>
            <tr class="prop">
                <td class="name">Device ID:</td>
                <td class="value">${port.deviceId ? port.deviceId : '-'}</td>
            </tr>
            <tr class="prop">
                <td class="name">Device Owner:</td>
                <td class="value">${port.deviceOwner ? port.deviceOwner : '-'}</td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
