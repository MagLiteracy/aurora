
<div class="list">
    <h2>Quotas</h2>
    <div class="buttons"></div>
    <table id="table_tenantQuotas" class="sortable fixedWidth">
        <thead>
        <tr>
            <th>Name</th>
            <th>Limit</th>
        </tr>
        </thead>
        <tbody>
        <g:each var="quota" in="${quotas}" status="i">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <td>${quota.displayName}</td>
                <td>${quota.limit}</td>
            </tr>
        </g:each>
        </tbody>
    </table>
</div>