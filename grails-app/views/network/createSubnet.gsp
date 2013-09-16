<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Subnet</title>
    <script type="text/javascript" src="/js/subnet-ui.js"></script>
</head>

<body>
<div class="body">
    <h1>Create Subnet</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>

    <div id="subnetTabs" class="c3Tabs">
        <ul>
            <li><a href="#subnetTab">Subnet</a></li>
            <li><a href="#subnetDetailTab">Subnet Detail</a></li>
        </ul>
        <g:form action="saveSubnet" method="post" class="validate allowEnterKeySubmit">
            <div id="subnetTab" class="dialog">
                <table id="table_createSubnet">
                    <tbody>
                    <tr class="prop">
                        <td class="name">
                            <label for="name">Subnet Name:</label>
                        </td>
                        <td>
                            <g:textField id="name" name="name" value="${params.name}"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td class="name">
                            <label for="networkAddress">Network Address:</label>
                        </td>
                        <td>
                            <g:textField id="networkAddress" name="networkAddress" value="${params.networkAddress}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td class="name">
                            <label for="ipVersion">IP Version:</label>
                        </td>
                        <td>
                            <select id="ipVersion" name="ipVersion">
                                <option value="4">IPv4</option>
                                <option value="6">IPv6</option>

                            </select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td class="name">
                            <label for="gatewayIp">Gateway IP (optional):</label>
                        </td>
                        <td>
                            <g:textField id="gatewayIp" name="gatewayIp" value="${params.gatewayIp}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                       <td class="networkId"></td>
                        <g:hiddenField id="networkId" name="networkId" value="${params.networkId}"/>
                    </tr>

                    <tr class="prop">
                        <td class="tenantId"></td>
                        <g:hiddenField id="tenantId" name="tenantId" value="${params.tenantId}"/>
                    </tr>
                    </tbody>
                </table>
            </div>


            <div id="subnetDetailTab">

                <table id="subnetDetails">
                    <tbody>
                    <tr class="prop">
                        <td class="name">
                            <label for="enableDHCP">Enable DHCP:</label>
                        </td>
                        <td>
                            <input id="enableDHCP" type="checkbox" name="enableDHCP">
                        </td>
                    </tr>

                    <tr class="prop">
                        <td class="name valignTop">
                            <label for="allocationPools">Allocation Pools:</label>
                        </td>
                        <td>
                            <g:textArea id="allocationPools" name="allocationPools" value="${params.allocationPools}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td class="name valignTop">
                            <label for="dnsName">DNS Name Servers:</label>
                        </td>
                        <td>
                            <g:textArea id="dnsName" name="dnsName" value="${params.dnsName}" wrap="soft"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td class="name valignTop">
                            <label for="hostRoutes">Host Routes:</label>
                        </td>
                        <td>
                            <g:textArea id="hostRoutes" name="hostRoutes" value="${params.hostRoutes}"/>
                        </td>
                    </tr>
                    <tbody>
                </table>

            </div>
            </div>

            <div class="buttons">
                <g:buttonSubmit id="submit" class="save" action="saveSubnet" title="Create subnet with selected parameters">Create Subnet</g:buttonSubmit>
            </div>
        </g:form>
    </div>
</body>
</html>
