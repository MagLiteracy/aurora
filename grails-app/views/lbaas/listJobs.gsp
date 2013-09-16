<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Jobs</title>
</head>
<body>
<script type="text/javascript" src="/js/lbaasJobs-ui.js"></script>
<script type="text/javascript" src="/js/autorefresh.js"></script>
<div class="body">
    <h1>Jobs</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="list">
            <div class="buttons">
            </div>
            <div id = 'table_container'>
                <g:render template="jobs"/>
            </div>
        </div>
    </g:form>
</div>
</body>
</html>