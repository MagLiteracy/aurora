<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.js')}?v=${build}"></script>
  <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.cookie.js')}"></script>
  <script type="text/javascript">
      var cssPath = "${resource(dir: 'css')}";
      var requiredFieldsArray = {<g:each var="constraint" in="${constraints}">
            "${constraint.key}": "${constraint.value}",
            </g:each>};
  </script>
  <script type="text/javascript" src="/js/loader.js"></script>
  <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-ui-1.10.3.custom.min.js')}"></script>
  <script type="text/javascript" src="${resource(dir: 'js', file: 'combobox-widjet.js')}"></script>
  <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.browser.min.js')}"></script>
  <script type="text/javascript" src="/js/ui.js"></script>
  <script type="text/javascript" src="/js/login.js"></script>
  <title>Login</title>
</head>
<body class="loginBody">
<div id="browserAlert" style="display: none;">Warning! You are using an unsupported browser. Some features may not work correctly. The supported browsers are Chrome, Firefox 3.0 and higher, and Safari 3.1.2 and higher, IE8 and higher. <a href="#" id="oldBrowserAgreement">Got it! Please turn off this warning message.</a></div>
<g:javascript>
    var awsEnvironments = [];
    var loginHints = [];
</g:javascript>
<g:each in="${grailsApplication.config.properties.environments}" var="environment">
    <g:if test="${environment.redirect_url}">
        <g:javascript>
            awsEnvironments.push("${environment.name}")
        </g:javascript>
    </g:if>
    <g:if test="${environment.loginHint}">
        <g:javascript>
            loginHints["${environment.name}"] = "${environment.loginHint}";
        </g:javascript>
    </g:if>
</g:each>
<div id="spinner" class="spinner" style="display:none;">
    <img src="/images/spinner.gif" alt="Spinner">
</div>
  <g:if test="${flash.message}">
    <div id="error_message" class="error">${flash.message}</div>
  </g:if>
  <g:form action="signIn">
    <div id="loginForm">
    <div class="loginToolTip"></div>
    <div class="loginTitle"><div id="aurora-text">aurora</div><div id="poweredby">powered by<span class="open">open</span><span class="stack">stack</span><span class="tm">&trade;</span></div></div>
    <div class="loginTable">
        <input type="hidden" name="targetUri" value="${targetUri}" />
        <table id="table_login">
          <tbody>
          <tr>
              <td>&nbsp;</td>
              <td class="loginThemesSwitcher">
                  <div class="siteSettings">
                      <div id="siteSettingsPic"></div>
                      <ul>
                          <li><div id="blueSwitcher" title="Click for switch color scheme">Switch to Blue color scheme</div></li>
                          <li><div id="orangeSwitcher" title="Click for switch color scheme">Switch to Dark color scheme</div></li>
                      </ul>
                  </div>
              </td>
          </tr>

            <tr>
                <td class="loginLabel">Environment:</td>
                <td><g:select title="Switch to a different environment" id="environment" name="environment" from="${grailsApplication.config.properties.environments}" value="${params.environment}" optionKey="name" optionValue="name"/></td>
            </tr>
            <tr class="c3specific">
              <td class="loginLabel">Username:</td>
              <td><input type="text" maxlength='200' id="username" name="username" value="${username}" class="required"/></td>
            </tr>
            <tr class="c3specific">
              <td class="loginLabel">Password:</td>
              <td><input type="password" maxlength='200' id="password" name="password" value="" class="required"/></td>
            </tr>
            <tr>
              <td></td>
              <td class="buttons"> <g:buttonSubmit id="submit" action="submit" class="save" title="Login with typed credentials">Sign in</g:buttonSubmit>
                  <div class="loginLoading" style="display:none;">
                      <img src="/images/spinner.gif" alt="Spinner" >
                  </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="loginHint"></div>
    </div>
    </div>
  </g:form>


<div id="loginConfirmationDialog" title="Confirmation Dialog">  </div>
</body>
</html>
