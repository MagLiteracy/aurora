<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Keypairs</title>
</head>

<body>
<div class="body">
    <h1>Keypairs</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:form method="post" class="validate">
        <div class="list">
            <div class="buttons">
                <g:link elementId="create" class="create" action="create" title="Create new keypair">Create New Keypair</g:link>
                <g:link elementId="insert" class="create" action="insert" title="Import existing public key">Import Keypair</g:link>
                <g:if test="${keypairs}">
                    <g:buttonSubmit id="delete" class="delete" value="Remove Keypair(s)" action="delete"
                                    data-warning="Really remove keypair(s)?" title="Remove selected keypair(s)"/>
                </g:if>
            </div>
            <table id="table_keypairList" class="sortable instanceType">
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>Name</th>
                    <th class="sorttable_nosort">Fingerprint</th>
                </tr>
                <g:each in="${keypairs}" var="keypair">
                    <tr>
                        <td><g:if test="${keypair.name}"><g:checkBox id="checkBox_${keypair.name}" name="selectedKeypairs" value="${keypair.name}"
                                                                     checked="0" class="requireLogin"/></g:if></td>
                        <td>${keypair.name}</td>
                        <td>${keypair.fingerprint}</td>
                    </tr>
                </g:each>
            </table>
        </div>
    </g:form>
</div>
</body>
</html>
