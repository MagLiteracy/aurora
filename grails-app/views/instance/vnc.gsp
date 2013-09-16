<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Instance VNC Console</title>
</head>

<body>
<div class="body">
    <h1>VNC Console</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    If VNC console is not responding to keyboard input, <a href="${vncUrl}"> click here to show only VNC</a>
    <div class="dialog">
        <iframe src="${vncUrl}" width="735" height="441"></iframe>
    </div>
</div>
</body>
</html>
