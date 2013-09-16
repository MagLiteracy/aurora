<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>${tenant.name}</title>
    <script>
        jQuery(function(){
            jQuery( "#accordion > div" ).accordion({
                collapsible: true,
                heightStyle: 'content',
                active: false
            });
        });
    </script>
</head>
<body>
<div class="body">
    <h1>Tenant Details</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${tenant.id}"/>
            <input type="hidden" name="name" value="${tenant.name}"/>
            <g:link class="edit" elementId="edit" action="edit" params="[id:tenant.id]" title="Edit this tenant">Edit Tenant</g:link>
            <g:buttonSubmit class="delete" id="delete" value="Remove Tenant" action="delete" data-warning="Really remove tenant?" title="Remove this tenant"/>
        </g:form>
    </div>
    <div class="dialog">
        <table id="table_tenantShow">
            <tbody>
            <tr class="prop">
                <td class="name">Id:</td>
                <td class="value">${tenant.id}</td>
            </tr>
            <tr class="prop">
                <td class="name">Name:</td>
                <td class="value">${tenant.name}</td>
            </tr>
            <tr class="prop">
                <td class="name">Description:</td>
                <td class="value">${tenant.description}</td>
            </tr>
            <tr class="prop">
                <td class="name">Enabled:</td>
                <td class="value">${tenant.enabled}</td>
            </tr>
            <tr class="prop">
                <td class="name">Zones:</td>
                <td class="value">
                    <g:each in="${tenant.zones}" var="zone">
                        ${zone}<br>
                    </g:each>
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    <div id="accordion">
        <div>
            <h4>Show/Hide Quotas</h4>
            <div>
                <g:render template="quotas"/>
                <div class="buttons"><g:link elementId="editQuotas" class="edit" action="quotas" params="[id:tenant.id]" title="Edit quotas for this tenant">Edit Quotas</g:link> </div>
            </div>
        </div>
        <div>
            <h4>Show/Hide Users</h4>
            <div>
                <g:render template="users"/>
                <div class="buttons"><g:link class="edit" elementId="editUsers"  action="users" params="[id:tenant.id]" title="Edit user roles for this tenant">Edit Users</g:link> </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
