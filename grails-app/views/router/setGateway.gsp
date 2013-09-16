<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Set Gateway</title>
</head>

<body>
<div class="body">
    <h1>Set Gateway</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="saveGateway" method="post" class="validate">

        <div class="dialog">
            <table id="table_setGateway">
                <tbody>
                <tr id="networkSources" class="prop">
                    <td class="network">
                        <label for="network">Network:</label>
                    </td>
                    <td>
                        <select id="network" name="network">
                            <g:each in="${params.networks}" var="network">
                                <option value="${network.id}">${network.name}</option>
                            </g:each>
                        </select>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="name">
                        <label for="name">Router name:</label>
                    </td>
                    <td>
                        <g:textField id="name" name="name" value="${params.router.name == '' ? 'None' : params.router.name}" readonly="readonly"/>
                    </td>
                </tr>

                <tr class="prop">
                    <td>
                        <input type="hidden" id="id" name="id" value="${params.router.id}"/>
                    </td>
                </tr>

                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit id="submit" class="save" action="saveGateway" title="Set Gateway for network">Set Gateway</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
