<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Floating IPs</title>
</head>

<body>
<div class="body">
    <h1>Floating IPs</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="list">
            <div class="buttons">
                <g:link elementId="allocate" class="floatingIP" action="allocateFloatingIp" title="Allocate new floating IP address">Allocate new IP</g:link>
                <g:if test="${floatingIps}">
                    <g:buttonSubmit id="release" class="delete" value="Release floating IP(s)" action="releaseFloatingIp"
                                    data-warning="Really release IP(s)?" title="Release selected IP(s)"/>
                </g:if>
            </div>
            <table id="table_floatingIpList" class="sortable">
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>IP</th>
                    <g:if test="${showFqdn}">
                        <th>FQDN</th>
                    </g:if>
                    <th>Instance</th>
                    <th>Pool</th>
                </tr>
                <g:each in="${floatingIps}" var="fip">

                    <tr>
                        <td>
                            <g:if test="${fip.id}">
                                <g:checkBox id="checkBox_${fip.id}" name="selectedIps"
                                            value="${fip.id}"
                                            checked="0" class="requireLogin"/>
                            </g:if>
                        </td>
                        <td>
                            <g:linkObject type="network" action="showFloatingIp" id="${fip.id}" displayName="${fip.ip}"/>
                        </td>
                        <g:if test="${showFqdn}">
                            <td>${fip.fqdn}</td>
                        </g:if>
                        <td>
                            <g:if test="${fip.instanceId}">
                                <g:linkObject type="instance" id="${fip.instanceId}"
                                              displayName="${instances.find { it.instanceId == fip.instanceId }.name}"/>
                            </g:if>
                        </td>
                        <td>${fip.pool}</td>

                    </tr>
                </g:each>
            </table>
        </div>
    </g:form>
</div>
</body>
</html>