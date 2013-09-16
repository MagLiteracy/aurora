<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Job</title>
</head>
<body>
<script type="text/javascript" src="/js/lbaasJobs-ui.js"></script>
<script type="text/javascript" src="/js/autorefresh.js"></script>
<div class="body">
    <h1>Job</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="buttons">
        </div>
        <table id="table_lbassJob" class="sortable">
            <tr>
                <td class="name">ID:</td>
                <td class="prop">${job.jobId}</td>
            </tr>
            <tr>
                <td class="name">Creation date</td>
                <td class="prop">${job.creationDate}</td>
            </tr>
            <tr>
                <td class="name">Completion date:</td>
                <td class="prop">${job.completionDate}</td>
            </tr>
            <tr>
                <td class="name">Task type:</td>
                <td class="prop">${job.taskType}</td>
            </tr>
            <tr>
                <td class="name">Status:</td>
                <td class="prop" id="job_status_value">${job.status}</td>
            </tr>
            <tr>
                <td class="name">Comments:</td>
                <td class="prop">${job.comments}</td>
            </tr>
            <tr>
                <td class="name">Payload:</td>
                <td class="prop">${job.requestBody}</td>
            </tr>
        </table>
    </g:form>
</div>
</body>
</html>