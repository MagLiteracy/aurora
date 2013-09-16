<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>${vip.name}</title>
</head>
<body>
<div class="body">
    <h1>Vip Details</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <div class="buttons">
        <g:form>
            <input type="hidden" id="id" name="id" value="${vip.name}"/>
            <g:buttonSubmit class="delete" id="delete" value="Remove Vip" action="deleteVip" data-warning="Really remove vip?" title="Remove this virtual IP address"/>
        </g:form>
    </div>
    <div class="dialog">
        <table id="table_lbassShowVip">
            <tbody>
                <tr class="prop">
                    <td class="name">Ip:</td>
                    <td class="value">${vip.ip}</td>
                </tr>
                <tr class="prop">
                    <td class="name">Name:</td>
                    <td class="value">${vip.name}</td>
                </tr>
                <tr class="prop">
                    <td class="name">Port:</td>
                    <td class="value">${vip.port}</td>
                </tr>
                <tr class="prop">
                    <td class="name">Protocol:</td>
                    <td class="value">${vip.protocol}</td>
                </tr>
                <tr class="prop">
                    <td class="name">Enabled:</td>
                    <td class="value">${vip.enabled}</td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
