<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Volume</title>
</head>

<body>
<div class="body">
    <h1>Create Volume</h1>
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
            <table id="table_volumeCreate">
                <tbody>
                <tr class="prop">
                    <td class="name"><label for="name">Name:</label></td>
                    <td><g:textField id="name" name="name" value="${params.name}"/></td>
                </tr>
                <tr class="prop">
                    <td class="name"><label for="description">Description:</label></td>
                    <td><g:textField id="description" name="description" value="${params.description}"/></td>
                </tr>
                <tr class="prop">
                    <td class="name"><label for="size">Size:</label></td>
                    <td><g:textField id="size" name="size" value="${params.size}"/></td>
                </tr>
                <tr>
                    <td class="name"><label for="type">Volume Type</label></td>
                    <td><g:select id="type" name="type" from="${volumeTypes}" optionKey="name" optionValue="name"/></td>
                </tr>
                <tr>
                    <td>GB available</td>
                    <td>${GBAvailable}</td>
                </tr>
                <tr>
                    <td>Number of volume available</td>
                    <td>${VAvailable}</td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" action="save" title="Create new volume with selected parameters">Create Volume</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
