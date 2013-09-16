<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit Security Group</title>
</head>

<body>
<div class="body">
    <h1>Edit Security Group</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <input type="hidden" name="id" id="input-hidden_sgEdit_id" value="${securityGroup.id}"/>
        <input type="hidden" name="name" id="input-hidden_sgEdit_name" value="${securityGroup.name}"/>

        <div class="dialog">
            <table id="table_securityGroupEdit">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="input_sgEdit_name">Name:</label>
                    </td>
                    <td class="value">
                        <g:textField type="text" id="input_sgEdit_name" name="name" value="${securityGroup.name}"/>
                    </td>
                </tr>
                <tr>
                    <td class="name">
                        <label for="input_sgEdit_description">Description:</label>
                    </td>
                    <td class="value">
                        <g:textField type="text" id="input_sgEdit_description" name="description"
                                     value="${securityGroup.description}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" value="Update Security Group" action="update" title="Save changes"/>
        </div>
    </g:form>
</div>
</body>
</html>
