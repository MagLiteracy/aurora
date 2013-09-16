<table id="table_snapshotList" class="sortable">
    <thead>
    <tr>
        <th class="checkboxTd">&thinsp;x</th>
        <th>Name</th>
        <th>Description</th>
        <th>Size</th>
        <th>Status</th>
        <th>Volume ID</th>
    </tr>
    </thead>
    <tbody>
    <g:each var="s" in="${snapshots}" status="i">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td><g:checkBox id="checkBox_${s.id}" name="selectedSnapshots" value="${s.id}"
                            checked="0"/></td>
            <td class="snapshot_show_link"><g:linkObject type="snapshot" displayName="${s.name}" id="${s.id}"/></td>
            <td>${s.description}</td>
            <td>${s.size}</td>
            <td class="snapshot_status">${s.status}</td>
            <td>${s.volumeId}</td>
        </tr>
    </g:each>
    </tbody>
</table>