<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Download Keypair</title>
</head>

<body onload="">
<div class="body">

    <h1>The keypair "${keypair.name}" was created.</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="download" method="post" class="validate">
        <input type="hidden" id="key" name="key" value="${keypair.privateKey}"/>
        <input type="hidden" id="name" name="name" value="${keypair.name}"/>

        <div class="dialog">

            <div class="danger"> WARNING: don't forget to download your new keypair. You will not be able to do it later.</div>


            <div class="buttons">

                <g:buttonSubmit class="save" id="download" action="download" title="Download created keypair">Download keypair</g:buttonSubmit>
            </div>

        </div>
    </g:form>
</div>
</body>
</html>