<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Pools & Services</title>
</head>
<body>
<div class="body">
    <h1>Pools & Services</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <div class="list">
            <div class="buttons">
                <g:link class="create" elementId="addPool" action="addPool" title="Add new pool">Add new pool</g:link>
                <g:if test="${pools}">
                    <g:buttonSubmit class="stop" id="delete" value="Delete" action="delete" data-warning="Really delete pool(s)?" title="Delete selected pool(s)"/>
                </g:if>
            </div>
               <table id="table_lbassListPools">
                <thead>
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>Name</th>
                    <th>Method</th>
                    <th>Monitors</th>
                    <th>Enabled</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="pool" in="${pools}" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:checkBox id="checkBox_${pool.name}" name="selectedPools" value="${pool.name}" checked="0" class="requireLogin"/></td>
                        <td><g:link id="${pool.name}" controller="lbaas" action="showPool">${pool.name}</g:link></td>
                        <td>${pool.method}</td>
                        <td>${pool.monitors?.join(', ')}</td>
                        <td>${pool.enabled}</td>
                    </tr>
                </g:each>
        </tbody>
    </table>
    </div>
    </g:form>
</div>
</body>
</html>