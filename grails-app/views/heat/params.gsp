<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Heat. Create Stack</title>
</head>
<body>
<div class="body">
    <h1>Create Stack</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post" class="validate">
        <input type="hidden" id="templateInd" name="templateInd" value="${templateInd}"/>
        <table id="table_paramsHeat">
            <tr>
                <td>Stack name:</td>
                <td><input id="stack_name" name="stack_name"/></td>
            </tr>
            <g:each in="${templateParams}" var="param">
                <tr>
                    <td>${param.name}:</td>
                    <td>
                        <g:if test="${param.allowedValues}">
                            <g:select id="names-${param.name}" name="${param.name}" from="${param.allowedValues}"/>
                        </g:if>
                        <g:else>
                            <input id="name-${param.name}" name="${param.name}" value="${param.default}"/>
                        </g:else>
                    </td>
                </tr>
            </g:each>
        </table>
        <g:buttonSubmit class="create" id="submit" value="Create Stack" action="createStack" title="Create new stack with selected parameters"/>
    </g:form>
</div>
</body>
</html>
