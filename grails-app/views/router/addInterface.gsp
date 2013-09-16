<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Add Interface</title>
</head>

<body>
<div class="body">
    <h1>Add Interface</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="saveInterface" method="post" class="validate">

        <div class="dialog">
            <table id="table_createInterface">
                <tbody>
                <tr id="subnetSources" class="prop">
                    <td class="subnet">
                        <label for="subnet">Subnet:</label>
                    </td>
                    <td>
                        <select id="subnet" name="subnet">
                            <g:each in="${params.networks}" var="network">
                                <g:each in="${network.subnets}" var="subnet">
                                    <option value="${subnet.id}">${network.name}: ${subnet.cidr} ${subnet.name != '' ? '(' + subnet.name + ')':''}</option>
                                </g:each>
                            </g:each>
                        </select>
                    </td>
                </tr>

                <tr class="prop">
                    <td class="name">
                        <label for="ip">IP Address(optional):</label>
                    </td>
                    <td>
                        <g:textField id="ip" name="ip"/>
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
            <g:buttonSubmit id="submit" class="save" action="saveInterface" title="Add new interface with selected parameters">Add New Interface</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
