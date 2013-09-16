<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Volume Type</title>
</head>

<body>
<div class="body">
    <h1>Create Volume Type</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post" class="validate">

        <div class="dialog">
            <table id="table_volumeCreateType">
                <tbody>
                <tr class="prop">
                    <td class="name"><label for="name">Name:</label></td>
                    <td><g:textField id="name" name="name" value="${params.name}"/></td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" action="saveType" title="Create new volume type with selected parameters">Create Volume Type</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
