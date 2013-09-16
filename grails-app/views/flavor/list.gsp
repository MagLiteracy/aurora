<%@ page import="com.paypal.aurora.OpenStackRESTService; grails.converters.JSON; com.paypal.aurora.Constant" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Flavors</title>
</head>

<body>
<div class="body">
    <h1>Flavors</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post" class="validate">
        <div class="list">
                <div class="buttons">
                    <shiro:hasRole name="${Constant.ROLE_ADMIN}">
                        <g:link elementId="create" class="create" action="create" title="Create new flavor">Create New Flavor</g:link>
                        <g:if test="${flavors}">
                            <g:buttonSubmit id="delete" class="delete" value="Remove Flavor(s)" action="delete"
                                            data-warning="Really remove flavor(s)?" title="Remove selected flavor(s)"/>
                        </g:if>
                    </shiro:hasRole>
                </div>
            <table id="table_listFlavor" class="sortable instanceType">
                <tr>
                    <shiro:hasRole name="${Constant.ROLE_ADMIN}"><th class="checkboxTd">&thinsp;x</th></shiro:hasRole>
                    <th>Name</th>
                    <th class="sorttable_nosort">Memory_MB</th>
                    <th class="sorttable_nosort">Disk</th>
                    <th>Ephemeral</th>
                    <th>Swap</th>
                    <th class="sorttable_nosort">VCPUs</th>
                    <th>RXTX_Factor</th>
                    <th>Is Public</th>
                </tr>
                <g:each in="${flavors}" var="flavor">
                    <tr>
                        <shiro:hasRole name="${Constant.ROLE_ADMIN}"><td><g:if test="${flavor.id}"><g:checkBox name="selectedFlavors" id="checkBox_${flavor.id}" value="${flavor.id}"
                                                                  checked="0" class="requireLogin"/></g:if></td></shiro:hasRole>
                        <td>${flavor.name}</td>
                        <td>${flavor.memory}</td>
                        <td>${flavor.disk}</td>
                        <td>${flavor.ephemeral}</td>
                        <td>${flavor.swap}</td>
                        <td>${flavor.vcpu}</td>
                        <td>${flavor.rxtxFactor}</td>
                        <td>${flavor.isPublic}</td>
                    </tr>
                </g:each>
            </table>
        </div>
        <div class="paginateButtons">
        </div>
    </g:form>
</div>
</body>
</html>
