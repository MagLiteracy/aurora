<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>${instance?.name} Instance</title>
</head>

<body>
<script type="text/javascript" src="/js/instances-ui.js"></script>
<script type="text/javascript">
    var userName = "<shiro:principal/>"
</script>
<div id = 'credentialsHint'>
    <g:render template="credentialsHint"/>
</div>
<div class="body">
    <h1>Instance Details</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:if test="${instance}">
        <g:form class="validate">
            <input type="hidden" id="input-hidden_instShow_instanceId" name="instanceId"
                   value="${instance.instanceId}"/>

            <div class="buttons" id="instance_show_buttons_area">
                <g:if test="${!instance.taskStatus}">
                    <g:link class="edit" elementId="rename" action="edit"
                            params="[id: instance.instanceId]" title="Change instance name">Rename</g:link>
                    <g:if test="${instance.status == 'Active'}">
                        <g:link class="takeSnapshot" action="snapshot" elementId="snapshot"
                                params="[id: instance.instanceId]" title="Create snapshot from this instance">Create Snapshot</g:link>
                        <g:buttonSubmit class="log" id="log" action="log" value="View Log" title="View instance work log"/>
                        <g:buttonSubmit class="console" id="vnc" action="vnc" value="VNC Console" title="Open VNC console for instance"/>
                        <g:buttonSubmit class="pause" id="pause" action="pause" value="Pause Instance" title="Stop VM with saving content in RAM"/>
                        <g:buttonSubmit class="pause" id="suspend" action="suspend" value="Suspend Instance" title="Stop VM with saving content on disk"/>
                    </g:if>
                    <g:if test="${instance.status == 'Active' || instance.status == 'Shutoff'}">
                        <g:buttonSubmit class="reboot" id="reboot" data-warning="Really Reboot: ${instance.instanceId}?"
                                        action="reboot" value="Reboot Instance"
                                        title="Restart the OS of the instance"/>
                    </g:if>
                    <g:if test="${instance.status == 'Paused'}">
                        <g:buttonSubmit class="resume" id="unpause" action="unpause" value="Unpause Instance" title="Resume VM work"/>
                    </g:if>
                    <g:if test="${instance.status == 'Suspended'}">
                        <g:buttonSubmit class="resume" id="resume" action="resume" value="Resume Instance" title="Resume VM work"/>
                    </g:if>
                    <g:buttonSubmit class="stop" id="terminate" data-warning="Really Terminate: ${instance.instanceId}?"
                                    action="terminate" value="Terminate Instance"
                                    title="Shut down and delete this instance"/>
                </g:if>
                <g:else>
                    <b id="taskStatus">${instance.taskStatus}</b><img src="/images/spinner.gif">
                </g:else>
            </div>
        </g:form>
    </g:if>
    <div class="dialog">
        <table id="table_instanceShow">
            <tbody>
            <tr class="prop" title="Application name from Cloud Application Registry">
                <td class="name">Name:</td>
                <td class="value">${instance.name}</td>
            </tr>
            <tr class="prop">
                <td class="name">ID:</td>
                <td class="value">${instance.instanceId}</td>
            </tr>
            <tr class="prop">
                <td class="name">Status:</td>
                <td class="value">${instance.status}</td>
            </tr>
            <tr><td colspan="2"><h3>Specs</h3></td></tr>
            <tr class="prop">
                <td class="name">Flavor:</td>
                <td class="value">${flavor.name}</td>
            </tr>
            <tr class="prop">
                <td class="name">RAM:</td>
                <td class="value">${flavor.memory} MB</td>
            </tr>
            <tr class="prop">
                <td class="name">VCPUs:</td>
                <td class="value">${flavor.vcpu}</td>
            </tr>
            <tr class="prop">
                <td class="name">Disk:</td>
                <td class="value">${flavor.disk} GB</td>
            </tr>
            <tr><td colspan="2"><h3>IP Addresses & FQDN</h3></td></tr>
            <g:each in="${instance.networks}" var="network">
                <tr>
                    <td class="name">${network.pool} :</td>
                    <td class="value"><a class="showIpHelp">${network.ip}</a>
                    <g:if test="${network.fqdn}">
                        (${network.fqdn})
                    </g:if>
                    </td>
                </tr>
            </g:each>
            <tr>
                <td colspan="3">
                    <h3>Floating IP Addresses & FQDN<g:if test="${showAssociateButton}"> <g:link controller="network" elementId="associate" action="associateFloatingIp" title="Associate floating IP to instance" params="[instanceId:instance.instanceId]"><img src="/images/uidarkicons/16/add_colored.png"></g:link></g:if>
                </h3></td>
            </tr>
            <g:each in="${instance.floatingIps}" var="fip">
                <tr>
                    <td class="name">
                        <g:if test="${fip.pool}">
                            ${fip.pool} :
                        </g:if>
                    </td>
                    <td class="value"><g:form controller="network" action="disassociateIp" method="post" >${fip.ip}
                        <g:if test="${fip.fqdn}">(${fip.fqdn})</g:if>
                        <g:if test="${fip.canDelete}">
                            <input type="hidden" name="instanceId" value="${instance.instanceId}">
                            <input type="hidden" name="fromInstance" value=true>
                            <input type="hidden" name="ip" value="${fip.ip}">
                            <g:buttonSubmit class='minusButton' id='associate' action="disassociateIp" value="disassociateIp" title="Disassociate this IP" data-warning="Really Disassociate IP: ${fip.ip}?"/>
                        </g:if>
                    </g:form></td>
                </tr>
            </g:each>
            <tr>
                <td class="name">Security Groups:</td>
                <td class="value">${instance.securityGroups.join('<br>')}</td>
            </tr>
            <tr class="prop">
                <td class="name">Key Name:</td>
                <td class="value">${instance.keyName}</td>
            </tr>
            <tr class="prop">
                <td class="name">Image Name:</td>
                <td class="value">${image.name}</td>
            </tr>
            <tr class="prop">
                <td class="name">Image id:</td>
                <td class="value"><g:linkObject type="image" id="${image.id}"/></td>
            </tr>
            <g:if test="${services.size() != 0}">
                <tr>
                    <td class="name">LBaaS services:</td>
                    <td colspan="5">
                        <table id="table_servicesInstance">
                            <thead>
                            <tr>
                                <th>Name</th>
                                <th>IP:port</th>
                                <th>Pool</th>
                                <th>Enabled</th>
                            </tr>
                            </thead>
                            <tbody>
                            <g:each in="${services}" var="service">
                                <tr>
                                    <td>${service.name}</td>
                                    <td>${service.ip}:${service.port}</td>
                                    <td>${service.pool}</td>
                                    <td>${service.enabled}</td>
                                </tr>
                            </g:each>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </g:if>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
