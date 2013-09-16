<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit Instance</title>
</head>

<body>
<div class="body">
    <h1>Edit Image Attributes</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" id="input-hidden_instList_id" name="id" value="${params.id}"/>

        <div class="dialog">
            <table id="table_instanceEdit">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td class="value">
                        <g:textField type="text" id="name" name="name" value="${params.name}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" value="Update Instance" action="update" title="Save changes"/>
        </div>
    </g:form>
</div>
</body>
</html>
