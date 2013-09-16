<div class="list">
    <h2>Users</h2>
    <div class="buttons">
    </div>
    <table id="table_OSUserList" class="sortable fixedWidth">
        <thead>
        <tr>
            <th>User Name</th>
            <th>User Email</th>
            <th>Enabled</th>
        </tr>
        </thead>
        <tbody>
        <g:each var="user" in="${users}" status="i">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td><g:linkObject displayName="${user.name}" elementId="openStackUser-${user.id}" type="openStackUser" id="${user.id}"/></td>
                <td>${user.email}</td>
                <td>${user.enabled ? 'enabled' : 'disabled'}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>