<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Import Keypair</title>
</head>

<body>
<div class="body">
    <h1>Import Keypair</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="insertKeypair" method="post" class="validate">

        <div class="dialog">
            <table id="table_keypairInsert">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="name">Name:</label>
                    </td>
                    <td>
                        <g:textField  id="name" name="name" value="${params.name}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="publicKey">Public key:</label>
                    </td>
                    <td>
                        <g:textArea rows="10" cols="60" id="publicKey" name="publicKey" value="${params.publicKey}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit id="submit" class="save" action="insertKeypair" title="Import typed public key">Import Keypair</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
