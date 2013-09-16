<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Policy</title>
</head>

<body>
<div class="body">
    <h1>Create Policy</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="savePolicy" method="post" class="validate allowEnterKeySubmit">
        <input type="hidden" id="tenantName" name="tenantName" value="${params.tenantName}"/>
        <div class="dialog">
            <table id="table_lbassCreatePolicy">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td>
                        <g:textField id="name" name="name" value="${params.name}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name valignTop">
                        <label for="rule">Rule:</label>
                    </td>
                    <td>
                        <g:textArea rows="10" cols="60" id="rule" name="rule" value="${params.rule}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit id="submit" class="save" action="savePolicy" title="Create new policy with selected parameters">Create Policy</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
