<%@ page import="com.paypal.aurora.Constant" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Routers</title>
</head>
<body>
<script type="text/javascript" src="/js/heat-ui.js"></script>
<div class="body">
    <h1>Routers</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post" class="validate">
        <div class="list">
            <div class="buttons">
                <shiro:hasRole name="${Constant.ROLE_ADMIN}">
                    <g:link elementId="create" class="create" action="create" title="Create new router">Create Router</g:link>
                    <g:if test="${routers}">
                        <g:buttonSubmit id="delete" class="delete" value="Remove Router(s)" action="delete"
                                        data-warning="Really remove router(s)?" title="Remove selected router(s)"/>
                    </g:if>
                </shiro:hasRole>
            </div>
            <table class="sortable" id="routers">
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>Name</th>
                    <th>Status</th>
                    <th>External Network</th>
                </tr>
                <g:each in="${routers}" var="router" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:if test="${router.id}"><g:checkBox id="checkBox_${router.id}" name="selectedRouters" value="${router.id}"
                                                                   checked="0" class="requireLogin"/></g:if></td>
                        <td><g:linkObject type="router"  displayName="${router.name == '' ? '(' + router.id.substring(0,8) + ')' : router.name}"
                                              id ="${router.id}" elementId="router-${router.id}"/></td>
                        <td>${router.status}</td>
                        <td>${router.externalGatewayInfo ? router.externalGatewayInfo.networkName : '-'}</tr>
                </g:each>
            </table>
        </div>
    </g:form>
</div>
</body>
</html>
