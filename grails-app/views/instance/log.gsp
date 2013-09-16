<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Instance log</title>
</head>

<body>
<div class="body">
    <h1>Instance Log</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form class="validate">
        <input type="hidden" id="input-hidden_instLog_instanceId" name="instanceId" value="${log.instanceId}"/>

        <div class="buttons">
            Log Length
            <input id="submit" name="length" style="text-align: right; width: 50px;" value="${log.length}"/>
            <g:buttonSubmit class="viewPart" id="log" action="log" value="Lines View" title="Show only some number of lines from log" style="margin-right: 15px;"/>
            <g:link class="view" id="viewLog" action="log" title="Show full log"
                    params="[instanceId: log.instanceId, showAll: 'yes']">View Full Log</g:link>
        </div>
    </g:form>
    <div class="dialog">
        <g:textArea cols="200" rows="40" id="textarea_instLog_log" name="log" value="${log.log}"/>
    </div>
</div>
</body>
</html>
