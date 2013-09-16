<%--
  Created by IntelliJ IDEA.
  User: nik
  Date: 27.05.13
  Time: 13:55
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Heat</title>
</head>
<body>
<div class="body">
    <h1>Stack Details</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <table id="table_dialogHeat">
            <tbody>
            <tr class="header">Info</tr>
            <tr class="prop" title="Application name from Cloud Application Registry">
                <td class="name">Name:</td>
                <td class="value">${stack.name}</td>
            </tr>
            <tr class="prop">
                <td class="name">ID:</td>
                <td class="value">${stack.id}</td>
            </tr>
            <tr class="prop">
                <td class="name">Description:</td>
                <td class="value">${stack.description}</td>
            </tr>
            <tr class="prop">
                <td class="name">Timeout:</td>
                <td class="value">${stack.timeout} Minutes</td>
            </tr>
            <tr class="prop">
                <td class="name">Rollback:</td>
                <td class="value">
                    <g:if test="${stack.disable_rollback == false}">
                        Enabled
                    </g:if>
                    <g:else>
                        Disabled
                    </g:else>
                </td>
            </tr>
            </tbody>
        </table>

        <table id="table_statusHeat">
            <tbody>
            <tr class="header">Status</tr>
            <tr class="prop">
                <td class="name">Created:</td>
                <td class="value">${stack.created}</td>
            </tr>
            <tr class="prop">
                <td class="name">Last Updated:</td>
                <td class="value">${stack.updated}</td>
            </tr>
            <tr class="prop">
                <td class="name">Status:</td>
                <td class="value">${stack.status}</td>
            </tr>

            </tbody>
        </table>

        <table id="table_parametersHeat">
            <tbody>
            <tr class="header">Parameters</tr>
            <g:each in="${stack.parameters}" var="parameter">
             <tr class="prop">
                    <td class="name">${parameter.key}</td>
                    <td class="value">${parameter.value}</td>
             </tr>
            </g:each>

            </tbody>
        </table>

    </div>
</div>
</body>
</html>
