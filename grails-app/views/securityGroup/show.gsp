<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>${securityGroup.name} Security Group</title>
</head>
<body>
  <div class="body">
    <h1>Security Group Details</h1>
    <g:if test="${flash.message}">
      <div id="error_message" class="error">${flash.message}</div>
    </g:if>
      <div class="buttons">
        <g:form>
          <input type="hidden" id="input-hidden_sgShow_id" name="id" value="${securityGroup.id}"/>
          <input type="hidden" id="input-hidden_sgShow_name" name="name" value="${securityGroup.name}"/>
          <g:link elementId="editRules" class="edit" action="editRules" params="[id: securityGroup.id]" title="Edit rules for this security group">Edit rules</g:link>
          <g:buttonSubmit class="delete" id="delete" action="delete" value="Delete Security Group" data-warning="Really delete Security Group '${securityGroup.name}'?" title="Delete this security group" />
        </g:form>
      </div>
    <div class="dialog">
      <table id="table_securityGroupShowDialog">
        <tbody>
        <tr class="prop">
          <td class="name">Name:</td>
          <td class="value">${securityGroup.name}</td>
        </tr>
        <tr class="prop">
            <td class="name">Description:</td>
            <td class="value">${securityGroup.description}</td>
        </tr>
        <tr class="prop">
            <td class="name valignTop" colspan="2"><h3>Rules:</h3>
                <div class="list">
                <table id="table_securityGroupShow" class="securityGroups">
                    <thead>
                    <tr>
                        <th>Ip protocol</th>
                        <th>From port</th>
                        <th>To port</th>
                        <th>Source</th>
                    </tr>
                    </thead>
                    <tbody>
                        <g:each var="rule" in="${securityGroup.rules}" status="i">
                            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                <td>${rule.ipProtocol}</td>
                                <td>${rule.fromPort}</td>
                                <td>${rule.toPort}</td>
                                <td>${rule.source}</td>
                            </tr>
                        </g:each>
                    </tbody>
                </table>
                </div>
            </td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>
</html>
