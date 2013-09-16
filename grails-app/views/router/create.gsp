<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Router</title>
</head>

<body>
<div class="body">
    <h1>Create Router</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" class="validate">

        <div class="dialog">
            <table id="table_createRouter">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td>
                        <g:textField id="name" name="name"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit id="submit" class="save" action="save" title="Create new router with selected parameters">Create Router</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
