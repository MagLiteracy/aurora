<%@ page import="com.paypal.aurora.OpenStackRESTService; grails.converters.JSON; com.paypal.aurora.Constant" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Images</title>
</head>

<body>
<script type="text/javascript" src="/js/image-ui.js"></script>
<script type="text/javascript" src="/js/autorefresh.js"></script>
<div class="body">
    <h1>Images</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:form method="post">
        <div class="list">
            <div class="buttons">
                <shiro:hasRole name="${Constant.ROLE_ADMIN}"><g:link elementId="create" class="create" action="create" title="Create new image">Create Image</g:link></shiro:hasRole>
            </div>
            <table class="sortable" id="images">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Status</th>
                    <th>Public</th>
                    <th>Format</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="image" in="${images}" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td class='image_show_link'><g:linkObject elementId="image-${image.id}" displayName="${image.name}" type="image" id="${image.id}"/></td>
                        <td>${image.type}</td>
                        <td class='image_status'>${image.status}
                            <g:if test="${image.status != 'active' && image.status != 'killed'}">
                                <img src="${resource(dir: 'images', file: 'spinner.gif')}"/>
                            </g:if></td>
                        <td>${image.shared}</td>
                        <td>${image.diskFormat}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>

        <div class="paginateButtons">
        </div>
    </g:form>

    <h1>Instance Snapshots</h1>

    <g:form method="post">
        <div class="list">
            <div class="buttons"></div>
            <table class="sortable twoTablesOnPage" id="snapshots">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Type</th>
                    <th>Status</th>
                    <th>Public</th>
                    <th>Format</th>
                </tr>
                </thead>
                <tbody>
                <g:each var="snapshot" in="${snapshots}" status="i">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        <td class='image_show_link'><g:linkObject displayName="${snapshot.name}" elementId="snapshot-${snapshot.id}" type="image" id="${snapshot.id}"/></td>
                        <td>${snapshot.type}</td>
                        <td class='image_status'>${snapshot.status}
                            <g:if test="${snapshot.status != 'active' && snapshot.status != 'killed'}">
                                <img src="${resource(dir: 'images', file: 'spinner.gif')}"/>
                            </g:if></td>
                        <td>${snapshot.shared}</td>
                        <td>${snapshot.diskFormat}</td>
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
