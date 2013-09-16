<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Allocate Floating IP</title>
</head>

<body>
<div class="body">
    <h1>Allocate Floating IP</h1>
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
            <table id="table_allocateFloatingIp">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="pool">Pool:</label>
                    </td>
                    <td>
                        <g:select id="pool" name="pool" from="${pools}" optionKey="name" optionValue="name"/>
                    </td>
                </tr>
                <g:if test="${isDns}">
                    <tr class="prop">
                        <td class="name">
                            <label for="hostname">DNS Name:</label>
                        </td>
                        <td>
                            <g:textField id="hostname" name="hostname"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td class="name">
                            <label for="zone">DNS Zone:</label>
                        </td>
                        <td>
                            <g:select id="zone" name="zone" from="${zones}"/>
                        </td>
                    </tr>
                </g:if>
                </tbody>
            </table>
        </div>
        <input type="hidden" name="parent" value="${parent}">
        <div class="buttons">
            <g:buttonSubmit id="submit" class="floatingIP" action="allocateIp" title="Allocate floating IP address with selected parameters">Allocate Floating IP</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
