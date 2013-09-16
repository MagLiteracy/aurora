<div id="showUsageDialog" align="center" title="Using Credentials hint">
    <div id="loginDialogSpinner" align="center" hidden="hidden"><img src="/images/spinner_big.gif" ></div>
    <div id="credentialsArea" hidden="hidden">
    <g:if test="${showAdminCredentials}">
        <h3>${adminLoginHint}</h3>
        <input id="root_credentials" title="Press Ctrl+C to copy selected row into the clipboard" readonly="true"/>
    </g:if>
    <g:if test="${showUserCredentials}">
        <h3>${userLoginHint}</h3>
        <input id="corp_credentials" title="Press Ctrl+C to copy selected row into the clipboard" readonly="true"/>
    </g:if>
    </div>
</div>