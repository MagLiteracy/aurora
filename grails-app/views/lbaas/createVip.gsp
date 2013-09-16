<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Vip</title>
    <script>
        jQuery(function() {
            // on page load script
            var portVal = jQuery('#port').val();
            var selectProtocolVal = jQuery('#protocol').val();
            if (portVal == '')
                if (selectProtocolVal=='HTTP') jQuery('#port').val("80") ;

            // on #protocol change script
            jQuery('#protocol').change(function() {
                var selectVal = jQuery(this).val();
                if (selectVal=='HTTP')  jQuery('#port').val("80") ;
                else jQuery('#port').val("") ;
            });
        });
    </script>
</head>

<body>
<div class="body">
    <h1>Create Vip</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
        <div id="error_message" class="error">
            <g:renderErrors bean="${cmd}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post" controller="lbaas" class="validate">
        <div class="dialog">
            <table id="table_lbassCreateVip">
                <tbody>
                    <tr class="prop">
                        <td class="name">
                            <label for="ip">Ip:</label>
                        </td>
                        <td>
                            <input type="text" id="ip" name="ip" value="${params.ip}"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td>
                            <input type="text" id="name" name="name" value="${params.name}"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td class="name">
                            <label for="protocol">Protocol:</label>
                        </td>
                        <td>
                            <g:select id="protocol" name="protocol" value="${params.protocol}" from="${params.allowedProtocols}"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td class="name">
                            <label for="port">Port:</label>
                        </td>
                        <td>
                            <input type="text" id="port" name="port" value="${params.port}"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td class="name">
                            <label for="enabled">Enabled:</label>
                        </td>
                        <td class="value">
                            <g:checkBox id="enabled" name="enabled" checked="${params.enabled == 'on'}"/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div class="buttons">
            <g:buttonSubmit class="save" id="submit" action="saveVip" title="Create new virtual IP address with selected parameters">Create Vip</g:buttonSubmit>
        </div>
    </g:form>
</div>
</body>
</html>
