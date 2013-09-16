<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>${volumeType.id} Volume</title>
</head>

<body>
<div class="body">
    <h1>Volume Type Details</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:if test="${volumeType}">
        <g:form controller="volume">
        </g:form>
    </g:if>
    <div class="dialog">
        <table id="table_volumeShowType">
            <tbody>
            <tr class="prop">
                <td class="name" title="Volume ID">Volume Type ID:</td>
                <td class="value">${volumeType.id}</td>
            </tr>
            <tr class="prop">
                <td class="name" title="Display Name">Name:</td>
                <td class="value">${volumeType.name}</td>
            </tr>
        </table>
    </div>
</div>
</body>
</html>
