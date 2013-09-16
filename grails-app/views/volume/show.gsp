<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>${volume.id} Volume</title>
</head>

<body>
<script type="text/javascript" src="/js/volume-ui.js"></script>
<script type="text/javascript" src="/js/autorefresh.js"></script>
<div class="body">
    <h1>Volume Details</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:if test="${volume}">
        <g:form controller="volume">
            <input type="hidden" id="id" name="id" value="${volume.id}"/>

            <div class="buttons">
                <g:link elementId="create" controller="snapshot" class="create" action="create" params="[id:volume.id]" title="Create snapshot from this volume">Create Volume Snapshot</g:link>
                %{--<g:buttonSubmit class="edit" action="edit" value="Edit Volume"/>--}%
                <g:buttonSubmit class="edit" id="editAttach" action="editAttach" value="Edit Attachment" title="Edit volume attachment"/>
                <g:buttonSubmit class="delete" id="delete" action="delete" value="Delete Volume"
                                data-warning="Really delete volume?" title="Delete this volume"/>
            </div>
        </g:form>
    </g:if>
    <div class="dialog">
        <table id="table_volumeShow">
            <tbody>
            <tr class="prop">
                <td class="name" title="Volume ID">Volume ID:</td>
                <td class="value">${volume.id}</td>
            </tr>
            <tr class="prop">
                <td class="name" title="Display Name">Display Name:</td>
                <td class="value">${volume.displayName}</td>
            </tr>
            <tr class="prop">
                <td class="name" title="Description">Description:</td>
                <td class="value">${volume.description}</td>
            </tr>
            <tr class="prop" >
                <td class="name" title="Status">Status:</td>
                <td class="value" id="volume_status_value" >${volume.status}</td>
            </tr>
            <tr class="prop">
                <td class="name" title="Volume Type">Volume Type:</td>
                <td class="value">${volume.volumeType}</td>
            </tr>
            <tr class="prop">
                <td class="name" title="Size">Size:</td>
                <td class="value">${volume.size} GB</td>
            </tr>
            <tr class=prop>
                <td class=name>Attached to</td>
                <td class=value><g:linkObject type="instance"  displayName="${volume.instanceName}" id ="${volume.instanceId}"/></td>
            </tr>
            <tr class=prop>
                <td class=name>Device name</td>
                <td class=value> ${volume.device}</td>
            </tr>
        </table>
     </div>
</div>
</body>
</html>
