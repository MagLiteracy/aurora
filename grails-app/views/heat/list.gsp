<%--
  Created by IntelliJ IDEA.
  User: nik
  Date: 27.05.13
  Time: 12:07
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Heat</title>
</head>
<body>
<script type="text/javascript" src="/js/heat-ui.js"></script>
<div class="body">
    <h1>Stacks</h1>
    <g:if test="${flash.message}">
        <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <div id="uploadFileDialog" title="Upload template">
        <g:uploadForm action="upload">
            <input id="file" type="file" name="template" />
            <input id="submit" type="submit" />
        </g:uploadForm>
    </div>
    <g:form method="post" class="validate">
        <div class="list">
            <div class="buttons">
                <button type="button" class="create" id="showUploadFileDialog"><div>Create stack</div></button>
                <g:if test="${stacks}">
                    <g:buttonSubmit class="delete" id="delete" value="Remove Stack(s)" action="delete"
                                    data-warning="Really remove stack(s)?" title="Remove selected stack(s)"/>
                </g:if>
            </div>
            <table id="table_listHeat" class="sortable instanceType">
                <tr>
                    <th class="checkboxTd">&thinsp;x</th>
                    <th>Name</th>
                    <th>Created</th>
                    <th>Updated</th>
                    <th>Status</th>
                </tr>
                <g:each in="${stacks}" var="stack">
                    <tr>
                        <td><g:if test="${stack.id}"><g:checkBox name="selectedStacks" id="checkBox_${stack.id}" value="${stack.id}"
                                                                     checked="0" class="requireLogin"/></g:if></td>
                        <td><g:linkObject type="heat" displayName="${stack.name}" id="${stack.id}"/></td>
                        <td>${stack.created}</td>
                        <td>${stack.updated}</td>
                        <td>${stack.status}</td>
                    </tr>
                </g:each>
            </table>
        </div>
    </g:form>
</div>
</body>
</html>
