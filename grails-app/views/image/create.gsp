<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Image</title>
</head>

<body>
<div class="body">
    <h1>Create Image</h1>
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
            <table id="table_imageCreate">
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
                    <td class="name">
                        <label for="location">Image Location (URL):</label>
                    </td>
                    <td>
                        <g:textField id="location" name="location" value="${params.location}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="diskFormat">Format:</label>
                    </td>
                    <td>
                        <g:select id="diskFormat" name="diskFormat" from="${formats}" value="${params.diskFormat}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="minDisk">Minimum Disk (GB):</label>
                    </td>
                    <td>
                        <g:textField id="minDisk" name="minDisk" value="${params.minDisk}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="minRam">Minimum Ram (MB):</label>
                    </td>
                    <td>
                        <g:textField id="minRam" name="minRam" value="${params.minRam}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="shared">Public</label>
                    </td>
                    <td>
                        <g:checkBox id="shared" name="shared" value="${params.shared}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <g:buttonSubmit id="submit" class="save" action="save" title="Create new image with selected parameters">Create Image</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>