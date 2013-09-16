<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>${volume.id} Volume</title>
</head>

<body>
<div class="body">
    <h1>Edit Volume</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="update" method="post" class="validate">
        <input type="hidden" name=id value="${volume.id}">
        <div class="dialog">
            <table id="table_volumeEdit">
                <tbody>
                <tr class="prop">
                    <td class="name"><label for="name">Name:</label></td>
                    <td><g:textField id="name" name="name" value="${params.name}"/></td>
                </tr>
                <tr class="prop">
                    <td class="name"><label for="description">Description:</label></td>
                    <td><g:textField id="description" name="description" value="${params.description}"/></td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" action="update" title="Save changes">Update Volume</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
