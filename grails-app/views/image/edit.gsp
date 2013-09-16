<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit ${bType}</title>
</head>

<body>
<div class="body">
    <h1>Edit ${bType} Attributes</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" id="id" name="id" value="${image.id}"/>

        <div class="dialog">
            <table id="table_editImage">
                <tbody>
                <tr class="prop">
                    <td class="name">ID:</td>
                    <td class="value">${image.id}</td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td class="value">
                        <g:textField type="text" id="name" name="name" value="${image.name}"/>
                    </td>
                </tr>
                <tr>
                    <td class="name">
                        <label for="shared">Public:</label>
                    </td>
                    <td class="value">
                        <g:checkBox id="shared" name="shared" checked="${image.shared}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit id="submit" class="save" value="Update ${bType} Attributes" action="update" title="Save changes"/>
        </div>
    </g:form>
</div>
</body>
</html>
