<table id="table_lbassJobs" class="sortable">
    <thead>
    <tr>
        <th>ID</th>
        <th>Creation date</th>
        <th>Completion date</th>
        <th>Task type</th>
        <th>Status</th>
        <th>Comments</th>
    </tr>
    </thead>
    <tbody>
    <g:each var="job" in="${jobs}" status="i">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td class="job_show_link"><g:link id="${job.jobId}" controller="lbaas" action="showJob">${job.jobId}</g:link></td>
            <td>${job.creationDate}</td>
            <td>${job.completionDate}</td>
            <td>${job.taskType}</td>
            <td class="job_status">${job.status}</td>
            <td>${job.comments}</td>
        </tr>
    </g:each>
    </tbody>
</table>