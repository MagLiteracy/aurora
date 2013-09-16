<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Volumes</title>
</head>
<body>
<div class="body">
    <h1>Attachment</h1>
    <g:if test="${volume.instanceName}">
        <g:form method="post" class="validate">
            <input type="hidden" name="instanceId" value="${volume.instanceId}">
            <input type="hidden" name="id" value="${volume.id}">
            <div class="buttons">
                <g:buttonSubmit class="delete" id="detach" action="detach" value="Detach"
                                data-warning="Really detach this instance?" title="Detach instance"/>
            </div>
        </g:form>
        <table id="table_volumeEditAttach">
            <tbody>
                <tr class=prop>
                    <td class=name>Attached to</td>
                    <td class=value><g:linkObject elementId="${volume.instanceId}" type="instance"  displayName="${volume.instanceName}" id ="${volume.instanceId}"/></td>
                </tr>
                <tr class=prop>
                    <td class=name>Device name</td>
                    <td class=value> ${volume.device}</td>
                </tr>
            </tbody>
        </table>
    </g:if>
    <g:else>
        <g:form method="post" class="validate">
            <input type="hidden"  id="id" name="id" value="${params.id}"/>
            <div class="buttons">
                <g:buttonSubmit class="create" id="attach" action="attach" value="Attach" title="Attach volume to selected instance"/>
            </div>
            <table id="table_volumeEditAttach">
                <tbody>
                <tr class="prop">
                    <td class="name">Attach to Instance</td>
                    <td class="value"><g:select id="instanceId" name="instanceId" from="${instances}" optionValue="name" optionKey="instanceId"/></td>
                </tr>
                <tr class="prop">
                    <td class="name">Device name</td>
                    <td class="value"><g:textField  id="device" name="device" value="/dev/vdc"/></td>
                </tr>
                </tbody>
            </table>
            <div class="paginateButtons">
            </div>
        </g:form>
    </g:else>

</div>
</body>
</html>
