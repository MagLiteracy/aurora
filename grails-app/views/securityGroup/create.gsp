<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create New Security Group</title>
</head>

<body>
<div class="body">
    <h1>Create New Security Group</h1>
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
            <table id="table_securityGroupCreate">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="input_sgCreate_name">Name:</label>
                    </td>
                    <td>
                        <input type="text" id="input_sgCreate_name" name="name" value="${params.name}"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="name">
                        <label for="input_sgCreate_description">Description:</label>
                    </td>
                    <td>
                        <input type="text" id="input_sgCreate_description" name="description" value="${params.description}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" action="save" title="Create new security group">Create New Security Group</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
