<%@ page import="com.paypal.aurora.Constant" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Instances</title>
</head>

<body>
<script type="text/javascript" src="/js/instances-ui.js"></script>
<script type="text/javascript" src="/js/autorefresh.js"></script>
<script type="text/javascript">
    var userName = "<shiro:principal/>"
</script>
<div id = 'credentialsHint'>
    <g:render template="credentialsHint"/>
</div>
<div class="body">
    <h1>Running Instances</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post" class="validate">
        <input type="hidden" id="input-hidden_instList_appNames" name="appNames" value="${params.id}"/>

        <div class="list">
            <div class="buttons">
                <g:link class="create" elementId="launchInstance" action="create" title="Launch new instance">Launch Instance</g:link>
                <g:if test="${instances.size() != 0}">
                    <g:buttonSubmit class="stop" value="Terminate Instance(s)" id="terminate" action="terminate"
                                    data-warning="Really terminate instance(s)?" title="Shut down and delete selected instance(s)"/>
                </g:if>
            </div>
            <div id = 'table_container'>
                <g:render template="instances"/>
            </div>
        </div>
        <div class="paginateButtons">
        </div>
    </g:form>
</div>
</body>
</html>
