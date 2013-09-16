<%@ page import="com.paypal.aurora.InstanceController" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>${snapshot.id} Snapshot</title>
</head>
<body>
<script type="text/javascript" src="/js/volume-snapshot-ui.js"></script>
<script type="text/javascript" src="/js/autorefresh.js"></script>
  <div class="body">
    <h1>Snapshot Details</h1>
    <g:if test="${flash.message}">
      <div id="error_message" class="error">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${cmd}">
      <g:renderErrors bean="${cmd}" as="list"/>
    </g:hasErrors>

    <div class="buttons">
        <g:form>
            <input type="hidden" id="id" name="id" value="${snapshot.id}"/>
            <g:link class="create" controller="instance" action="create" elementId="create"
                    params="[selectedVolumeOption: InstanceController.VolumeOptions.BOOT_FROM_SNAPSHOT, snapshotId: snapshot.id]" title="Launch new instance with volume, created from this snapshot">Launch</g:link>
            <g:buttonSubmit class="delete" id="delete" action="delete" value="Delete Snapshot" data-warning="Really delete snapshot '${snapshot.id}'?" title="Delete this volume snapshot"/>
        </g:form>
    </div>

    <div class="dialog">
      <table id="table_snapshotShow">
        <tbody>
        <tr class="prop">
          <td class="name" title="Snapshot ID">Snapshot ID:</td>
          <td class="value">${snapshot.id}</td>
        </tr>
        <tr class="prop">
          <td class="name" title="Volume ID">Volume ID:</td>
          <td class="value"><g:linkObject type="volume" id="${snapshot.volumeId}"/></td>
        </tr>
        <tr class="prop">
          <td class="name" title="Status">Status:</td>
          <td class="value" id="snapshot_status_value" >${snapshot.status}</td>
        </tr>
        <tr class="prop">
          <td class="name" title="Size">Size:</td>
          <td class="value">${snapshot.size} GB</td>
        </tr>
        <tr class="prop">
          <td class="name" title="Description">Description:</td>
          <td class="value">${snapshot.description}</td>
        </tr>
        <tr class="prop">
          <td class="name" title="Created">Created:</td>
          <td class="value">${snapshot.created}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</body>
</html>
