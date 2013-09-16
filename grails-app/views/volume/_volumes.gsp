<table id="table_volumeList" class="sortable">
    <thead>
    <tr>
        <th class="checkboxTd">&thinsp;x</th>
        <th>Name</th>
        <th>Status</th>
        <th>Type</th>
        <th>Attached To</th>
        <th>Size (GB)</th>
    </tr>
    </thead>
    <tbody>
    <g:each var="volume" in="${volumes}" status="i">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td><g:checkBox id="checkBox_${volume.id}" name="selectedVolumes" value="${volume.id}"
                            checked="0"/></td>
            <td class="volume_show_link"><g:linkObject type="volume" displayName="${volume.displayName}" id="${volume.id}"/></td>
            <td class='volume_status'>
                ${volume.status}
            </td>
            <td>${volume.volumeType}</td>
            <td>
                <g:if test="${volume.instanceName}">
                    Attached to <g:linkObject type="instance" displayName="${volume.instanceName}"
                                              id="${volume.instanceId}"/> on
                    ${volume.device}
                </g:if>
            </td>
            <td>${volume.size}</td>
        </tr>
    </g:each>
    </tbody>
</table>