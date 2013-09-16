<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Pool</title>
</head>
<body>
<div class="body">
    <h1>Pools & Services</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:if test="${pool}">
        <g:form controller="lbaas">
            <input type="hidden" id="id" name="id" value="${pool.name}"/>
            <div class="buttons">
                <g:link class="edit" elementId="editPool" action="editPool"
                        params="[id:pool.name]" title="Edit pool parameters">Edit Pool</g:link>
            </div>
        </g:form>
    </g:if>
    <g:form method="post">
        <div>
            <table id="table_lbassPool">
                <tbody>
                    <tr class="prop">
                        <td class="name">Name:</td>
                        <td class="value">${pool.name}</td>
                    </tr>
                    <tr class="prop">
                        <td class="name">Method:</td>
                        <td class="value">${pool.method}</td>
                    </tr>
                    <tr class="prop">
                        <td class="name">Monitors:</td>
                        <td class="value">${pool.monitors?.join(', ')}</td>
                    </tr>
                    <tr class="prop">
                        <td class="name">Enabled:</td>
                        <td class="value">${pool.enabled}</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="list">
            <div class="buttons">
                <input type="hidden" id="pool" name="pool" value="${pool.name}"/>
                <g:link elementId="addService" action="addService" class="create"  params="[id: pool.name, weight: 10]" title="Add new service to this pool">Add new service</g:link>
                <g:if test="${services}">
                    <g:buttonSubmit class="enable" value="Enable" id="enable" action="enableService" params="[id: pool.name]" data-warning="Really enable service(s)?" tittle="Enable selected service(s)"/>
                    <g:buttonSubmit class="disable" value="Disable" id="disable" action="disableService" params="[id: pool.name]" data-warning="Really disable service(s)?" tittle="Disable selected service(s)"/>
                    <g:buttonSubmit class="delete" value="Delete" id="delete" action="deleteService" params="[id: pool.name]" data-warning="Really delete service(s)?" tittle="Delete selected service(s)"/>
                </g:if>
            </div>
            <table id="table_lbassServices" class="sortable">
                <thead>
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>Name</th>
                    <th>ip:port</th>
                    <th>Enabled</th>
                    <th>Weigth</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="service" in="${services}" status="j">
                    <tr class="${(j % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:checkBox id="checkBox_${service.name}" name="selectedServices" value="${service.name}"
                                        checked="0" class="requireLogin"/></td>
                        <td>${service.name}</td>
                        <td>${service.ip}:${service.port}</td>
                        <td>${service.enabled}</td>
                        <td>${service.weight}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
    </g:form>
</div>
</body>
</html>