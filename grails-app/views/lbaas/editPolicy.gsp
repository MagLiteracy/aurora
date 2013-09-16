<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit Policy</title>
</head>

<body>
<div class="body">
    <h1>Edit Policy</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form>
        <input type="hidden" name="id" value="${params.id}"/>
        <div class="buttons">
            <g:buttonSubmit class="delete" id="deletePolicy" value="Remove Policy" action="deletePolicy"
                            data-warning="Really remove policy?" title="Remove this policy"/>
        </div>
    </g:form>
    <g:form method="post" class="validate">
        <input type="hidden" name="id" value="${params.id}"/>
        <input type="hidden" name="tenantName" value="${params.tenantName}"/>

        <div class="dialog">
            <table id="table_lbassEditPolicy">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td>
                        <g:textField id="name" name="name" value="${policy.name}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="rule">Rule:</label>
                    </td>
                    <td>
                        <g:textArea rows="10" cols="60" id="rule" name="rule" value="${policy.rule}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" action="updatePolicy" title="Save changes">Update Policy</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
