<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Security Groups</title>
    <script type="text/javascript" src="/js/securityGroup-ui.js"></script>
</head>

<body>
<div class="body">
    <h1>Security Group Rules</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">

        <div class="buttons">
            <g:if test="${params.securityGroup.rules}">
                <g:buttonSubmit class="delete" value="Remove Rule(s)" id="delete" action="deleteRule"
                                data-warning="Really remove rule(s)?" title="Delete selected rule(s)"/>
            </g:if>
        </div>
        <div class="list">
        <input type="hidden" id="input-hidden_sgShow_id" name="id" value="${params.securityGroup.id}"/>
        <table id="table_securityGroupEditRules" class="securityGroups">
            <thead>
            <tr>
                <th class="checkboxTd">&thinsp;x</th>
                <th>Ip protocol</th>
                <th>From port</th>
                <th>To port</th>
                <th>Source</th>
            </tr>
            </thead>
            <tbody>
            <g:each var="rule" in="${params.securityGroup.rules}" status="i">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                    <td><g:if test="${rule.id}"><g:checkBox name="selectedRules" id="checkBox_${rule.id}"
                                                            value="${rule.id}"
                                                            checked="0"/></g:if></td>
                    <td>${rule.ipProtocol}</td>
                    <td>${rule.fromPort}</td>
                    <td>${rule.toPort}</td>
                    <td>${rule.source}</td>
                </tr>
            </g:each>
            </tbody>
        </table>
        </div>
    </g:form>
    <g:form method="post">

        <h2>Add Rule<h2>
        <input type="hidden" id="input-hidden_sgShow_id" name="id" value="${params.securityGroup.id}"/>

        <div class="dialog">
            <table id="table_securityGroupEditRulesDialog">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="ipProtocol">Ip protocol:</label>
                    </td>
                    <td>
                        <g:select id="select_sgShow_ipProtocol" name="ipProtocol" from="${params.ipProtocols}"
                                  value="${params.ipProtocol}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label id="fromPortLabel" for="input_sgShow_fromPort">From port:</label>
                    </td>
                    <td>
                        <input type="text" id="input_sgShow_fromPort" name="fromPort" value="${params.fromPort}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label id="toPortLabel" for="input_sgShow_toPort">To port:</label>
                    </td>
                    <td>
                        <input type="text" id="input_sgShow_toPort" name="toPort" value="${params.toPort}"/>
                    </td>
                </tr>
                <tr class="prop">
                    <td class="name">
                        <label for="sourceGroup">Source group:</label>
                    </td>
                    <td>
                        <g:select id="select_sgShow_sourceGroup" name="sourceGroup"
                                  from="${params.sourceGroups.entrySet()}" value="${params.sourceGroup}" optionKey="key"
                                  optionValue="value"/>
                    </td>
                </tr>
                <tr class="prop" id="cidrTr">
                    <td class="name">
                        <label for="input_sgShow_cidr">CIDR:</label>
                    </td>
                    <td>
                        <input type="text" id="input_sgShow_cidr" name="cidr" value="${params.cidr ?: '0.0.0.0/0'} "/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" action="addRule" title="Add new rule">Add Rule</g:buttonSubmit>
        </div>

    </g:form>
</div>
</body>
</html>
