<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Volume Snapshots</title>
</head>

<body>
<script type="text/javascript" src="/js/volume-snapshot-ui.js"></script>
<script type="text/javascript" src="/js/autorefresh.js"></script>
<div class="body">
    <h1>Volume Snapshots</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="list">
            <div class="buttons">
                <g:if test="${snapshots}">
                    <g:buttonSubmit class="delete" id="delete" action="delete" value="Delete Volume Snapshot(s)"
                                    data-warning="Really delete volume snapshot(s)?" title="Delete selected snapshot(s)"/>
                </g:if>
            </div>
            <div id = 'table_container'>
                <g:render template="snapshots"/>
            </div>
        </div>

        <div class="paginateButtons">
        </div>
    </g:form>
</div>
</body>
</html>
