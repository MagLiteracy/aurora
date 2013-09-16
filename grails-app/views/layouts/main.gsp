<%@ page import="com.paypal.aurora.OpenStackRESTService; grails.converters.JSON; com.paypal.aurora.Constant" %>
<!DOCTYPE html>
<html>
<head>
    <title><g:layoutTitle default="Aurora"/></title>
    <meta http-equiv="X-UA-Compatible" content="chrome=1">
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.js')}?v=${build}"></script>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.cookie.js')}"></script>
    <script type="text/javascript">
        var cssPath = "${resource(dir: 'css')}";
        var requiredFieldsArray = {<g:each var="constraint" in="${constraints}">
            "${constraint.key}": "${constraint.value}",
            </g:each>};
    </script>
    <script type="text/javascript" src="/js/loader.js"></script>
    <!--[if IE]>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'ie.css')}?v=${build}"/>
  <![endif]-->
    <link rel="shortcut icon" href="${resource(dir: '/', file: 'favicon.ico')}" type="image/x-icon"/>
    <script type="text/javascript" src="${resource(dir: 'js', file: 'jquery-ui-1.10.3.custom.min.js')}"></script>
    <g:layoutHead/>
</head>

<body id="body">
<div id="spinner" class="spinner" style="display:none;">
    <img src="${resource(dir: 'images', file: 'spinner.gif')}" alt="Spinner"/>
</div>
<div>
<div id="browserAlert" style="display: none;">Warning! You are using an unsupported browser. Some features may not work correctly. The supported browsers are Chrome, Firefox 3.0 and higher, and Safari 3.1.2 and higher, IE8 and higher. <a href="#" id="oldBrowserAgreement">Got it! Please turn off this warning message.</a></div>
<div class="topBar">
    <div class="titlebar">
        <div class="header">
            <a href="${resource(dir: '/')}">
                <div id="titleLogo"><div id="aurora-text">aurora</div><div id="poweredby">powered by<span class="open">open</span><span class="stack">stack</span><span class="tm">&trade;</span></div></div>
            </a>
        </div>
        <g:if test="${!pageProperty(name: 'meta.hideNav')}">

        <g:set var='targetUri' value="${request.requestURL + (request.queryString ? '?' + request.queryString : '')}"/>
        <shiro:isLoggedIn>

            <div class="siteSettings">
                <div id="siteSettingsPic"></div>
                <ul>
                    <li><div id="blueSwitcher" title="Click for switch color scheme">Switch to Blue color scheme</div></li>
                    <li><div id="orangeSwitcher" title="Click for switch color scheme">Switch to Dark color scheme</div></li>
                </ul>
            </div>
            <div id="authentication" class="authentication">
                <span>Logged in as</span> <shiro:principal/>
                <ul>
                    <li><g:link elementId="signOut" controller="auth" action="signOut"
                                params="${[targetUri: targetUri]}">Logout</g:link></li>
                </ul>
            </div>

            <div class="tenants">
                <g:form controller="userState" action="changeDataCenter" method="POST"
                        params="${[targetUri: targetUri]}">
                    <span>Datacenter:</span> <g:select title="Switch to a different datacenter" name="dataCenterName"
                                          value="${session.dataCenterName}" from="${session.dataCentersMap.entrySet()}"
                                          optionKey="key" optionValue="key" />
                </g:form>
            </div>

            <div class="tenants">
                <g:form controller="userState" action="changeTenant" method="POST" params="${[targetUri: targetUri]}">
                    <span>Tenant:</span> <g:select title="Switch to a different tenant" id="tenantId" name="tenantId"
                                      value="${session.tenant.id}" from="${session.tenants}" optionKey="id"
                                      optionValue="name" onchange="submit()"/>
                </g:form>
            </div>
        </shiro:isLoggedIn>
        <shiro:isNotLoggedIn>
            <g:link controller="auth" action="login" class="login" params="${[targetUri: targetUri]}">Login</g:link>
        </shiro:isNotLoggedIn>
        <div class="search" title="Find entities by name">
            <form action="/search" method="GET" class="allowEnterKeySubmit">
                %{--<input type="search" results="10" autosave="aurora${env}globalsearch" name="q" placeholder="Global search by names" value="${params.q}">--}%
            </form>
        </div>
    </g:if>
    </div>
</div>
<div id="navigationBar">
<g:if test="${!pageProperty(name: 'meta.hideNav')}">
    <ul id="topNav" class="nav">
        <li class="menuButton"><g:link class="instances" elementId="nav-instance-list-root" controller="instance" action="list">Compute</g:link>
            <ul>
                <li class="menuButton"><g:link class="instances" elementId="nav-instance-list" controller="instance"
                                               action="list">Instances</g:link></li>
                <li class="menuButton"><g:link class="images" elementId="nav-image-list" controller="image" action="list">Images</g:link></li>
                <li class="menuButton"><g:link class="instanceTypes" elementId="nav-flavor-list" controller="flavor"
                                               action="list">Flavors</g:link></li>
            </ul>
        </li>
        <g:ifServiceEnabled name="${OpenStackRESTService.NOVA_VOLUME}">
        <li class="menuButton"><g:link class="volumes" elementId="nav-volume-list-root" controller="volume" action="list">Storage</g:link>
            <ul>
                <li class="menuButton"><g:link class="volumes" elementId="nav-volume-list" controller="volume" action="list">Volumes</g:link></li>
                <li class="menuButton"><g:link class="volumeSnapshot" elementId="nav-snapshot-list" controller="snapshot"
                                               action="list">Snapshots</g:link></li>
            </ul>
        </li>
        </g:ifServiceEnabled>

        <li class="menuButton"><g:link class="quantum"  elementId="nav-network-index-root" controller="network" action="index">Networking</g:link>
            <ul>
                <li class="menuButton"><g:link class="networks"  elementId="nav-network-floatingIpList" controller="network" action="floatingIpList">Floating IPs</g:link></li>
                <g:ifServiceEnabled name="${OpenStackRESTService.QUANTUM}">
                    <li class="menuButton"><g:link class="quantum"  elementId="nav-networks-list" controller="network" action="list">Networking</g:link></li>
                    <li class="menuButton"><g:link class="routers" elementId="nav-routers-list" controller="router" action="list">Routers</g:link></li>
                </g:ifServiceEnabled>
            </ul>
        </li>

        <g:ifServiceEnabled name="${OpenStackRESTService.HEAT}">
            <li class="menuButton"><g:link class="heatService" elementId="nav-heat-list-root" controller="heat" action="list">Cloud Formation</g:link></li>
        </g:ifServiceEnabled>

        <li class="menuButton"><g:link class="securityGroups" elementId="nav-securityGroup-list-root" controller="securityGroup" action="list">Security</g:link>
            <ul>
                <li class="menuButton"><g:link class="securityGroups" elementId="nav-securityGroup-list" controller="securityGroup"
                                               action="list">Security Groups</g:link></li>
                <li class="menuButton"><g:link class="keyPairs" elementId="nav-keypair-list" controller="keypair"
                                               action="list">Keypairs</g:link></li>
            </ul>
        </li>
        <li class="menuButton"><g:link class="quotas" elementId="nav-quota-list-root" controller="quota" action="list">Settings</g:link>
            <ul>
                <li class="menuButton"><g:link class="quotas" elementId="nav-quotaUsage-list" controller="quotaUsage" action="list">Quota Usage</g:link></li>
                <li class="menuButton"><g:link class="quotas" elementId="nav-quota-list" controller="quota" action="list">Quotas</g:link></li>
                <li class="menuButton"><g:link class="openStackService" elementId="nav-openStackService-list" controller="openStackService"
                                               action="list">Services</g:link></li>
                <shiro:hasRole name="${Constant.ROLE_ADMIN}">
                    <li class="menuButton"><g:link class="tenants" elementId="nav-tenant-list" controller="tenant"
                                                   action="list">Tenants</g:link></li>
                    <li class="menuButton"><g:link class="openStackUser" elementId="nav-openStackUser-list" controller="openStackUser"
                                                   action="list">Users</g:link></li>
                </shiro:hasRole>
                <li class="menuButton"><g:link class="about" elementId="nav-about" controller="info"
                                           action="index">About</g:link></li>
            </ul>
        </li>
    </ul>
</g:if>
</div>
<div id="mainBar">
    <div id="main">
        <g:layoutBody/>
    </div>
</div>

<div id="footer">
    <div id="footer_row">
        <span>©2013 Aurora (Apache 2.0 Licensed).</span>
    </div>
</div>
<div id="confirmationDialog" title="Confirmation Dialog">  </div>
<script type="text/javascript" src="${resource(dir: 'js', file: 'jquery.browser.min.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'ui.js')}"></script>
<script defer type="text/javascript" src="${resource(dir: 'js', file: 'custom.js')}?v=${build}"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'combobox-widjet.js')}"></script>
<g:javascript src="back-btn.js"/>
<script type="text/javascript">
    drawBackButton("${parent}");
</script>
<g:render template="/layouts/occasion"/>
</body>
</html>
