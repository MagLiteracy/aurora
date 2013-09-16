<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Policies</title>
</head>

<body>
<div class="body">
    <h1>Policies</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post" class="validate">
        <input type="hidden" name="tenantName" value="${params.tenantName}"/>
        <div class="list">
            <div class="buttons">
                <g:link elementId="create" class="create" action="createPolicy" params="${params.tenantName ? [tenantName: params.tenantName]:[:]}"  title="Create new policy">Create New Policy</g:link>
                <g:if test="${policies}">
                    <g:buttonSubmit class="delete" id="delete" value="Remove Policy(s)" action="deletePolicy" data-warning="Really remove policy(s)?" title="Remove selected policy(s)"/>
                </g:if>
            </div>
            <table class="sortable" id="policies">
                <thead>
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>Name</th>
                    <th>Rule</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="policy" in="${policies}" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td><g:if test="${policy.name}"><g:checkBox name="selectedPolicies" id="checkBox_${policy.name}" value="${policy.name}" checked="0" class="requireLogin"/></g:if></td>
                        <td><g:linkObject elementId="editPolicy" type="lbaas" id="${policy.name}" action="editPolicy" params="${params.tenantName ? [id: policy.name, tenantName: params.tenantName]:[id: policy.name]}"/></td>
                        <td><g:textArea id="name-${policy.name}" name="${policy.name}_ruleArea" value="${policy.rule}" cols="100" rows="10" style="overflow-y:scroll" readonly="readonly"/></td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
        <div class="paginateButtons">
        </div>
    </g:form>
</div>
</body>
</html>
