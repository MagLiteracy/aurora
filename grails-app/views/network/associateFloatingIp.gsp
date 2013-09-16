<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Associate Floating IP</title>
</head>

<body>
<div class="body">
    <h1>Associate Floating IP</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" name="fromInstance" value="${fromInstance}">

        <div class="dialog">
            <table id="table_associateFIP">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="ip">IP:</label>
                    </td>
                    <td>
                        <g:select name="ip" id="ip" from="${floatingIps}" optionKey="ip" optionValue="ip"
                                  value="${defaultIp}"/><g:buttonSubmit id="allocate"
                                                                                          class="plusButton"
                                                                                          action="allocateFloatingIp"
                                                                                          value="allocate"
                                                                                          title="Allocate new floating IP address"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="instanceId">Instance:</label>
                    </td>
                    <td>
                        <g:select id="instanceId" name="instanceId" from="${instances}" optionKey="instanceId"
                                  optionValue="name" value="${defaultInstance}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit id="submit" class="floatingIP" action="associateIp" title="Associate floating IP address">Associate Floating IP</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>