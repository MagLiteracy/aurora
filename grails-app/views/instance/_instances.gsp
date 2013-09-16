<%@ page import="com.paypal.aurora.Constant" %>
<table id="table_listInstance" class="sortable">
    <thead>
    <tr>
        <th class="checkboxTd">&thinsp;x</th>
        <th>Name</th>
        <g:if test="${isUseExternalFLIP}">
            <th>IP Address</th>
        </g:if>
        <g:else>
            <th>IP Addresses</th>
        </g:else>
        <shiro:hasRole name="${Constant.ROLE_ADMIN}">
            <th>Host</th>
        </shiro:hasRole>
        <th>Status</th>
        <th>Task</th>
        <th>Power</th>
        <g:if test="${isUseExternalFLIP}">
            <th>Action</th>
        </g:if>
    </tr>
    </thead>
    <tbody>
    <g:each var="mi" in="${instances}" status="i">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'} instance_row">
            <td><g:if test="${mi.instanceId}"><g:checkBox name="selectedInstances" value="${mi.instanceId}"
                                                          id="checkBox_${mi.instanceId}"
                                                          checked="0" class="requireLogin"/></g:if></td>
            <td class='instance_show_link'><g:linkObject type="instance" displayName="${mi.name}"
                                                      id="${mi.instanceId}"/></td>
            <td>
                <g:if test="${isUseExternalFLIP}">
                    ${mi.displayedIp}
                </g:if>
                <g:else>
                    <ul class="links">
                        <g:each in="${mi.networks}" var="network">
                            <li><b>${network.pool}</b> <a class="showIpHelp">${network.ip}</a></li>
                        </g:each>
                        <g:each in="${mi.floatingIps}" var="fip">
                            <li>
                                <g:if test="${fip.pool}"><b>${fip.pool}</b></g:if>
                                <a class="showIpHelp">${fip.ip}</a>

                            </li>
                        </g:each>
                    </ul>
                </g:else>
            </td>
            <shiro:hasRole name="${Constant.ROLE_ADMIN}">
                <td>${mi.host}</td>
            </shiro:hasRole>
            <td class='instance_status'>${mi.status}</td>
            <td class='instance_taskStatus'>
                ${mi.taskStatus}
                <g:if test="${mi.taskStatus}">
                    <img src="${resource(dir: 'images', file: 'spinner.gif')}"/>
                </g:if>
            </td>
            <td>${mi.powerStatus}</td>
            <g:if test="${isUseExternalFLIP}">
                <td><span class="buttons"><a class="showFloatIpHelp" title="${mi.displayedIp}">Login</a></span></td>
            </g:if>
            <td class="instance_id" hidden="hidden">${mi.instanceId}</td>
        </tr>
    </g:each>
    </tbody>
</table>

