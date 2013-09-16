<%@ page import="com.paypal.aurora.OpenStackRESTService; grails.converters.JSON; com.paypal.aurora.Constant" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Volumes</title>
</head>

<body>
<script type="text/javascript" src="/js/volume-ui.js"></script>
<script type="text/javascript" src="/js/autorefresh.js"></script>
<div class="body">
    <h1>Volumes</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post" class="validate">
        <div class="list">
            <div class="buttons">
                <g:buttonSubmit class="create" id="create" action="create" value="Create Volume" title="Create new volume"/>
                <g:if test="${volumes}">
                    <g:buttonSubmit class="delete" id="delete" action="delete" value="Delete Volume(s)"
                                    data-warning="Really delete volume(s)?" title="Delete selected volume(s)"/>
                </g:if>
            </div>
            <div id = 'table_container'>
                <g:render template="volumes"/>
            </div>
        </div>

        <div class="paginateButtons">
        </div>
    </g:form>
    <shiro:hasRole name="${Constant.ROLE_ADMIN}">
        <h1>Volume types</h1>
        <g:form method="post" class="validate">
            <div class="list">
                <div class="buttons">
                    <g:buttonSubmit class="create" id="createType" action="createType" value="Create Volume Type" title="Create new volume type"/>
                    <g:if test="${volumeTypes}">
                        <g:buttonSubmit class="delete" id="deleteType" action="deleteType" value="Delete Volume Type(s)"
                                        data-warning="Really delete volume type(s)?" title="Delete selected volume type(s)"/>
                    </g:if>
                </div>
                <table id="table_volumeTypes" class="sortable twoTablesOnPage">
                    <thead>
                    <tr>
                        <th class="checkboxTd">&thinsp;x</th>
                        <th>Name</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each var="volumeType" in="${volumeTypes}" status="i">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td><g:checkBox id="checkBox_${volumeType.id}" name="selectedVolumeTypes"
                                            value="${volumeType.id}" checked="0"/></td>
                            <td><g:link elementId="showType-${volumeType.id}" action="showType"
                                        params="[id: volumeType.id]">${volumeType.name}</g:link></td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>

            <div class="paginateButtons">
            </div>
        </g:form>
    </shiro:hasRole>
</div>
</body>
</html>
