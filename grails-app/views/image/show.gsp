<%@ page import="com.paypal.aurora.InstanceController; com.paypal.aurora.OpenStackRESTService; grails.converters.JSON; com.paypal.aurora.Constant" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>${image.id} ${image.name} ${bType}</title>
</head>

<body>
<script type="text/javascript" src="/js/image-ui.js"></script>
<script type="text/javascript" src="/js/autorefresh.js"></script>
<div class="body">
    <h1>${bType} Details</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <shiro:hasRole name="${Constant.ROLE_ADMIN}">
        <div class="buttons">
            <g:form>
                <input type="hidden" id="id" name="id" value="${image.id}"/>
                <g:if test="${isSnapshot}">
                    <g:link elementId="create" class="create" controller="instance" action="create" title="Launch instance from this snapshot"
                            params="[selectedInstanceSource: InstanceController.InstanceSources.SNAPSHOT,
                                    snapshotId: image.id]">Launch</g:link>
                </g:if>
                <g:link elementId="edit" class="edit" action="edit" params="[id: image.id]" title="Edit ${sType} name and sharing">Edit ${bType} Attributes</g:link>
                <g:buttonSubmit id="delete" class="delete" action="delete" value="Delete ${bType}"
                                data-warning="Really delete ${sType} '${image.id}' with name '${image.name}'?" title="Delete this ${sType}"/>
            </g:form>
        </div>
    </shiro:hasRole>

    <div class="dialog">
        <table id="table_showImage">
            <tbody>
            <tr class="prop">
                <td class="name">ID:</td>
                <td class="value">${image.id}</td>
            </tr>
            <tr class="prop">
                <td class="name">Name:</td>
                <td class="value">${image.name}</td>
            </tr>
            <tr>
                <td class="name">Status:</td>
                <td class="value" id="image_status_value">${image.status}
                    <g:if test="${image.status != 'active' && image.status != 'killed'}">
                        <img src="${resource(dir: 'images', file: 'spinner.gif')}"/>
                    </g:if></td>
            </tr>
            <tr>
                <td class="name">Public:</td>
                <td class="value">${image.shared}</td>
            </tr>
            <tr>
                <td class="name">Checksum:</td>
                <td class="value">${image.checksum}</td>
            </tr>
            <tr class="prop">
                <td class="name">Created:</td>
                <td class="value">${image.created}</td>
            </tr>
            <tr class="prop">
                <td class="name">Updated:</td>
                <td class="value">${image.updated}</td>
            </tr>
            <tr class="prop">
                <td class="name">Container format:</td>
                <td class="value">${image.containerFormat}</td>
            </tr>
            <tr class="prop">
                <td class="name">Disk format:</td>
                <td class="value">${image.diskFormat}</td>
            </tr>
            </tbody>
            <g:if test="${isSnapshot}">
                <tbody>
                <tr class="prop">
                    <td class="name">RAM disk ID:</td>
                    <td class="value">${image.properties.ramdisk_id}</td>
                </tr>
                <tr class="prop">
                    <td class="name">Image location:</td>
                    <td class="value">${image.properties.image_location}</td>
                </tr>
                <tr>
                    <td class="name">Image state:</td>
                    <td class="value">${image.properties.image_state}</td>
                </tr>
                <tr>
                    <td class="name">Kernel ID:</td>
                    <td class="value">${image.properties.kernel_id}</td>
                </tr>
                <tr>
                    <td class="name">Owner ID:</td>
                    <td class="value">${image.properties.owner_id}</td>
                </tr>
                <tr class="prop">
                    <td class="name">User ID:</td>
                    <td class="value">${image.properties.user_id}</td>
                </tr>
                <tr class="prop">
                    <td class="name">Instance UUID:</td>
                    <td class="value">${image.properties.instance_uuid}</td>
                </tr>
                <tr class="prop">
                    <td class="name">Base image ref:</td>
                    <td class="value">${image.properties.base_image_ref}</td>
                </tr>
                </tbody>
            </g:if>
        </table>
    </div>
</div>
</body>
</html>
