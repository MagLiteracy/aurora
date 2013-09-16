<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit pool</title>
</head>
<body>
<div class="body">
    <h1>Edit pool</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div class="errors">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>

    <g:form method="post">
        <input type="hidden" id="id" name="id" value="${params.id}"/>
        <div class="dialog">
            <table id="table_lbassEditPool">
                <tr>
                    <td><label for="name">Pool Name:</label></td>
                    <td><g:textField type="text" id="name" name="name" value="${params.name}"/></td>
                </tr>
                <tr>
                    <td><label for="enabled">Enabled:</label></td>
                    <td><g:checkBox id="enabled" name="enabled" value="${params.enabled == 'true' || params.enabled == 'on'}"/></td>
                </tr>
                <tr>
                    <td><label for="lbMethod">LB Method:</label></td>
                    <td>
                        <g:select id="lbMethod" name="lbMethod" from="${params.methods}" value="${params.lbMethod}"/>
                    </td>
                </tr>
                <tr>
                    <td class="valignTop"><label for="monitors">Monitor:</label></td>
                    <td>
                        <g:each in="${params.allMonitors}" var="monitor">
                            <g:checkBox id="monitors-${monitor}" name="monitors"
                                  value="${monitor}" checked="${params.monitors?.contains(monitor)}"/>${monitor}<br>
                        </g:each>
                    </td>
                </tr>
            </table>
        </div>
        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" value="Edit pool" action="updatePool" params="" title="Save changes"/>
        </div>
    </g:form>
</div>
</body>
</html>