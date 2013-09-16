<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Instance Snapshot</title>
</head>

<body>
<div class="body">
    <h1>Create Instance Snapshot</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" id="input-hidden_instSnapshot_id" name="id" value="${params.id}"/>

        <div class="dialog">
            <table id="table_instanceSnapshots">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Snapshot Name:</label>
                    </td>
                    <td class="value">
                        <g:textField id="name" type="text" name="name" value="${params.name}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit class="save" value="Create Snapshot" id="submit" action="makeSnapshot" title="Create snapshot from this instance"/>
        </div>
    </g:form>
</div>
</body>
</html>
