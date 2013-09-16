<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Vips</title>
</head>
<body>
<div class="body">
    <h1>Vips</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="list">
            <div class="buttons">
                <g:link elementId="create" class="create" action="createVip" title="Create new virtual IP address">Create Vip</g:link>
                <g:if test="${vips}">
                    <g:buttonSubmit class="delete" id="delete" value="Remove vip(s)" action="deleteVip"
                                    data-warning="Really remove vip(s)?" title="Remove selected vip(s)"/>
                </g:if>
            </div>
            <table id="table_lbassListVips" class="sortable">
                <thead>
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>Vip name</th>
                    <th>Ip </th>
                    <th>Port</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="vip" in="${vips}" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:if test="${vip.name}"><g:checkBox id="checkBox_${vip.name}" name="selectedVips" value="${vip.name}"
                                                                  checked="0"/></g:if></td>
                        <td><g:link id="${vip.name}" action="showVip">${vip.name}</g:link>
                        <td>${vip.ip}</td>
                        <td>${vip.port}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
    </g:form>
</div>
</body>
</html>