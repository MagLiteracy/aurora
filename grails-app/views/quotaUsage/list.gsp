<%@ page import="com.paypal.aurora.Constant" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Quota Usages</title>
    <script type="text/javascript">
        var quotaUsageArray = {<g:each var="quotaUsage" in="${quotaUsages}">
            <g:if test="${quotaUsage.limit > 0}">
                "${quotaUsage.name}": ["${quotaUsage.limit}", "${quotaUsage.usage}", "${quotaUsage.left}","${quotaUsage.displayName}"],
            </g:if>
            </g:each>};
        //console.log(quotaUsageArray.vcpu[0]);
    </script>
    <script type="text/javascript" src="/js/quota-ui.js"></script>
</head>

<body>
<div class="body">
    <h1>Quota Summary</h1>
    <div id="quotaContainer">
    </div>
    <h1>Quota Usages</h1>
    <g:if test="${flash.message}">
        <div class="error" id="error_message">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="list">
            <shiro:hasRole name="${Constant.ROLE_ADMIN}">
                <div class="buttons">
                    <g:link elementId="edit" class="edit" action="quotas" controller="tenant" params="[parent: '/quotaUsage/list']" title="Edit quotas for this tenant">Edit</g:link>
                </div>
            </shiro:hasRole>
            <table id="table_quotaList" class="sortable">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Limit</th>
                    <th>Usage</th>
                    <th>Left</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="quotaUsage" in="${quotaUsages}" status="i">
                    <g:if test="${quotaUsage.limit > 0}">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td>${quotaUsage.displayName}</td>
                            <td>${quotaUsage.limit}</td>
                            <td>${quotaUsage.usage}</td>
                            <td>${quotaUsage.left}</td>
                        </tr>
                    </g:if>
                </g:each>
                </tbody>
            </table>
        </div>

        <div class="paginateButtons">
        </div>
    </g:form>
</div>
</body>
</html>