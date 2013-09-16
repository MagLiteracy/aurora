<html>
<head>
    <title>Initialize Aurora</title>
    <meta name="layout" content="main"/>
    <meta name="hideNav" content="true"/>
</head>

<body>
<div class="body">
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <h1>Welcome to Aurora!</h1>
    <h1>Can't start Aurora because of following Config.json error: ${errorMessage}</h1>
    <h1>Enter valid parameters to Aurora configuration file at ${auroraHome}/Config.json.</h1>
</div>
</body>
</html>
