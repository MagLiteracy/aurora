<%@ page import="com.paypal.aurora.Constant" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Networks</title>
</head>
<body>
<script type="text/javascript"></script>
<div class="body">
    <h1>Networks</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post" class="validate">
        <div class="list">
            <div class="buttons">
                <shiro:hasRole name="${Constant.ROLE_ADMIN}">
                    <g:link elementId="create" class="create" action="create" title="Create new network">Create New Network</g:link>
                    <g:if test="${networks}">
                        <g:buttonSubmit id="delete" class="delete" value="Remove Network(s)" action="delete"
                                        data-warning="Really remove network(s)?" title="Remove selected network(s)"/>
                    </g:if>
                </shiro:hasRole>
            </div>
            <table class="sortable" id="networks">
                <tr>
                    <shiro:hasRole name="${Constant.ROLE_ADMIN}">
                        <th class="checkboxTd">&thinsp;x</th>
                    </shiro:hasRole>
                    <th>Project</th>
                    <th>Network name</th>
                    <th>Subnets Associated</th>
                    <th>Shared</th>
                    <th>Status</th>
                    <th>Admin State</th>
                </tr>
                <g:each in="${networks}" var="network" status="i">
                    <tr>
                        <shiro:hasRole name="${Constant.ROLE_ADMIN}">
                            <td>
                                <g:if test="${network.id}"><g:checkBox id="checkBox_${network.id}" name="selectedNetworks" value="${network.id}"
                                                                     checked="0" class="requireLogin"/></g:if>
                            </td>
                        </shiro:hasRole>
                        <td>${network.project ? network.project.name : '-'}</td>
                        <td>
                            <shiro:hasRole name="${Constant.ROLE_ADMIN}">
                                <g:linkObject type="network"  displayName="${network.name == '' ? '(' + network.id.substring(0,8) + ')' : network.name}"
                                id ="${network.id}" elementId="network-${network.id}"/>
                            </shiro:hasRole>
                            <shiro:lacksRole name="${Constant.ROLE_ADMIN}">
                                ${network.name == '' ? '(' + network.id.substring(0,8) + ')' : network.name}
                            </shiro:lacksRole>
                        </td>
                        <td>
                            <g:each in="${network.subnets}" var="subnet">
                               <g:if test="${subnet}">
                                    <strong>${subnet.name}</strong> ${subnet.cidr}<br/>
                               </g:if>
                            </g:each>
                        </td>
                        <td>${network.shared ? 'YES' : 'NO'}</td>
                        <td>${network.status}</td>
                        <td>${network.adminStateUp ? 'UP' : 'DOWN'}</tr>
                </g:each>
            </table>
        </div>
    </g:form>
</div>
</body>
</html>
