<%@ page import="com.paypal.aurora.Constant" %>

<td><g:if test="${instance.instanceId}"><g:checkBox name="selectedInstances" value="${instance.instanceId}"
                                                    id="checkBox_${instance.instanceId}"
                                                    checked="0" class="requireLogin"/></g:if></td>
<td class='instance_show_link'><g:linkObject type="instance" displayName="${instance.name}"
                                             id="${instance.instanceId}"/></td>
<td>
    <g:if test="${isUseExternalFLIP}">
        ${instance.displayedIp}
    </g:if>
    <g:else>
        <ul class="links">
            <g:each in="${instance.networks}" var="network">
                <li><b>${network.pool}</b> <a class="showIpHelp">${network.ip}</a></li>
            </g:each>
            <g:each in="${instance.floatingIps}" var="fip">
                <li>
                    <g:if test="${fip.pool}"><b>${fip.pool}</b></g:if>
                    <a class="showIpHelp">${fip.ip}</a>

                </li>
            </g:each>
        </ul>
    </g:else>
</td>
<shiro:hasRole name="${Constant.ROLE_ADMIN}">
    <td>${instance.host}</td>
</shiro:hasRole>
<td class='instance_status'>${instance.status}</td>
<td class='instance_taskStatus'>
    ${instance.taskStatus}
    <g:if test="${instance.taskStatus}">
        <img src="${resource(dir: 'images', file: 'spinner.gif')}"/>
    </g:if>
</td>
<td>${instance.powerStatus}</td>
<g:if test="${isUseExternalFLIP}">
    <td><span class="buttons"><a class="showFloatIpHelp" title="${instance.displayedIp}">Login</a></span></td>
</g:if>
<td class="instance_id" hidden="hidden">${instance.instanceId}</td>
