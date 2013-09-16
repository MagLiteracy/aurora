<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Subnet Details</title>
</head>
<body>
<div class="body">
    <h1>Subnet Details</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <div class="buttons">
            <g:link elementId="edit" class="edit" action="editSubnet" params="[id:subnet.id]" title="Edit this subnet">Edit Subnet</g:link>
        </div>
        <table id="table_showSubnet">
            <tbody>
            <tr class="header">Subnet Overview</tr>
            <tr class="prop" title="Application name from Cloud Application Registry">
                <td class="name">Name:</td>
                <td class="value">${subnet.name == "" ? 'None' : subnet.name}</td>
            </tr>
            <tr class="prop">
                <td class="name">ID:</td>
                <td class="value">${subnet.id}</td>
            </tr>
            <tr class="prop">
                <td class="name">Network ID:</td>
                <td class="value">${subnet.networkId}</td>
            </tr>
            <tr class="prop">
                <td class="name">CIDR:</td>
                <td class="value">${subnet.cidr}</td>
            </tr>
            <tr class="prop">
                <td class="name">IP Version:</td>
                <td class="value">${subnet.ipVersion}</td>
            </tr>
            <tr class="prop">
                <td class="name">Gateway IP:</td>
                <td class="value">${subnet.gatewayIp}</td>
            </tr>
            <tr class="prop">
                <td class="name">DHCP Enable:</td>
                <td class="value">${subnet.enableDhcp ? 'YES' : 'NO'}</td>
            </tr>
            <tr class="prop">
                <td class="name">IP allocation pool:</td>
                <td class="value">
                    <g:each in="${subnet.allocationPools}" var="allocationPool">
                        Start ${allocationPool.start} - End ${allocationPool.end}
                    </g:each>
                </td>
            </tr>
            <tr class="prop">
                <td class="name">DNS:</td>
                <td class="value">
                    <g:each in="${subnet.dnsNameservers}" var="dnsNameServer">
                        ${dnsNameServer}
                    </g:each>
                </td>
            </tr>
            <tr class="prop">
                <td class="name">Additional routes:</td>
                <td class="value">
                    <g:each in="${subnet.hostRoutes}" var="hostRouter">
                        Destination ${hostRouter.destination} Next hop ${hostRouter.nexthop} <br/> <br/>
                    </g:each>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
