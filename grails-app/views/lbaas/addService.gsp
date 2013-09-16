<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Add new service</title>
    <script type="text/javascript">
        var instanceInterfaces = {
                <g:each var="instance" in="${instances}">
                    "${instance.instanceId}" :
                        {
                            <g:each in="${instance.networks}" var="network">
                            "${network.pool}" : "${network.ip}",
                            </g:each>
                            <g:each in="${instance.floatingIps}" var="flip">
                            "${flip.pool?:flip.ip}" : "${flip.ip}",
                            </g:each>
                        },
                </g:each>
            };
        var instanceSelected = "${params.instanceId}";
        var interfaceSelected = "${params.netInterface}";
    </script>
    <script type="text/javascript" src="/js/addService.js"></script>
</head>
<body>
<div class="body">
    <h1>Add new service</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" name="id" id="input-hidden_lbaasAdd_id" value="${params.id}"/>
        <div>
            <table id="table_lbassAddService">
                <tr>
                    <td><label for="instanceId">Name instance:</label></td>
                    <td><g:select id="instanceId" name="instanceId" from="${instances}" optionKey="instanceId" optionValue="name"/></td>
                </tr>
                <tr>
                    <td><label for="netInterface">Network interface:</label></td>
                    <td><g:select id="netInterface" name="netInterface" from=""/></td>
                </tr>
                <tr>
                    <td><label for="port">Port:</label></td>
                    <td><g:textField type="text" id="port" name="port"/></td>
                </tr>
                <tr>
                    <td><label for="name">Name service:</label></td>
                    <td><g:textField type="text" id="name" name="name" value="${params.name}"/></td>
                </tr>
                <tr>
                    <td><label for="weight">Weight:</label></td>
                    <td><g:textField type="text" id="weight" name="weight" value="${params.weight}"/></td>
                </tr>
                <tr>
                    <td><label for="enabled">Enabled:</label></td>
                    <td><g:checkBox id="enabled" name="enabled" checked="true"/></td>
                </tr>
            </table>
        </div>
        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" value="Add new service" action="saveService" title="Add new service with selected parameters"/>
        </div>
    </g:form>
</div>
</body>
</html>