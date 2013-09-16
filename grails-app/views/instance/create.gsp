<%@ page import="com.paypal.aurora.InstanceController; com.paypal.aurora.OpenStackRESTService" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Launch Instance</title>
    <script type="text/javascript">
        var snapshotString = "${InstanceController.InstanceSources.SNAPSHOT}";
        var imageString = "${InstanceController.InstanceSources.IMAGE}";
        var bootFromSnapshotString = "${InstanceController.VolumeOptions.BOOT_FROM_SNAPSHOT}";
        var notBootString = "${InstanceController.VolumeOptions.NOT_BOOT}";
        var bootFromVolumeString = "${InstanceController.VolumeOptions.BOOT_FROM_VOLUME}";
    </script>
    <script type="text/javascript" src="/js/instances-ui.js"></script>
</head>

<body>
<div class="body">
<h1>Launch Instance</h1>
<g:if test="${flash.message}">
    <div id="error_message" class="error">${flash.message}</div>
</g:if>
<g:hasErrors bean="${cmd}">
    <div id="error_message" class="error">
        <g:renderErrors bean="${cmd}" as="list"/>
    </div>
</g:hasErrors>


<div id="instanceTabs" class="c3Tabs">
<ul>
    <li><a href="#detailsTab" id="details">Details</a></li>
    <li><a href="#accessAndSecurityTab" id="accessAndSecurity">Access and security</a></li>
    <g:if test="${params.networks}">
        <li><a href="#networkingTab" id="networking">Networking</a></li>
    </g:if>
    <g:ifServiceEnabled name="${OpenStackRESTService.NOVA_VOLUME}">
        <li><a href="#volumeOptionsTab" id="volumeOptions">Volume options</a></li>
    </g:ifServiceEnabled>
    <li><a href="#postCreationTab" id="postCreation">Post-Creation</a></li>
</ul>
<g:form action="save" method="post" class="validate allowEnterKeySubmit">
    <div id="detailsTab" class="dialog">
        <table id="table_instanceSource">
            <tbody>
            <tr class="prop">
                <td class="name">
                    <label for="cb_instance_source">Instance source:</label>
                </td>
                <td>
                    <g:select id="cb_instance_source" name="instanceSources" from="${params.instanceSourcesArray}"
                              optionValue="displayName" value="${params.instanceSources}"/>
                </td>
            </tr>
            <tr id="imageSources" class="prop">
                <td class="name">
                    <label for="cb_image">Image:</label>
                </td>
                <td>
                    <g:select id="cb_image" name="image" from="${params.images}"
                              optionKey="id" optionValue="name" value="${params.image}"/>
                </td>
            </tr>
            <tr id="snapshotSources" class="prop">
                <td class="name">
                    <label for="select_instCreate_snapshot">Snapshot:</label>
                </td>
                <td>
                    <g:select id="select_instCreate_snapshot" name="snapshot" from="${params.snapshots}"
                              optionKey="id" optionValue="name" value="${params.snapshot}"/>
                </td>
            </tr>
            <tr class="prop">
                <td class="name">
                    <label for="input_instCreate_name">Name:</label>
                </td>
                <td>
                    <g:textField id="input_instCreate_name" name="name" value="${params.name}"/>
                </td>
            </tr>
            <tr class="prop">
                <td class="name">
                    <label for="cb_flavor">Flavor:</label>
                </td>
                <td>
                    <g:select id="cb_flavor" name="flavor" from="${params.flavors}"
                              optionKey="id" optionValue="name" value="${params.flavor}"/>
                </td>
            </tr>
            <g:if test="${params.needDatacenter}">
                <tr class="prop">
                    <td class="name">
                        <label for="select_instCreate_datacenter">Datacenter:</label>
                    </td>
                    <td>
                        <g:select id="select_instCreate_datacenter" name="datacenter" from="${params.datacenters}" value="${params.datacenter}"/>
                    </td>
                </tr>
            </g:if>
            <tr class="prop">
                <td class="name">
                    <label for="input_instCreate_count">Instance Count:</label>
                </td>
                <td>
                    <g:textField id="input_instCreate_count" name="count" value="${params.count}"/>
                </td>
            </tr>
            </tbody>
        </table>
    </div>


    <div id="accessAndSecurityTab">
        <table id="table_instanceKeypair">
            <tbody>
            <tr class="prop">
                <td class="name">
                    <label for="cb_keypair">Keypair:</label>
                </td>
                <td>
                    <g:select id="cb_keypair" name="keypair" from="${params.keypairs}" value="${params.keypair}"/>
                </td>
            </tr>

            <tr class="prop">
                <td class="name valignTop">
                    <label for="securityGroups">Security Groups:</label>
                </td>
                <td>
                    <g:each in="${params.securityGroupsArray}" var="securityGroup">
                        <g:checkBox name="securityGroups" id="checkbox_${securityGroup.name}"
                               value="${securityGroup.name}" checked="${params.securityGroups?.contains(securityGroup.name)}"/>${securityGroup.name}<br>
                    </g:each>
                </td>
            </tr>
            <tbody>
        </table>

    </div>

    <g:if test="${params.networks}">
        <div id="networkingTab" class="prop">
            <table id="table_networks">
                <tbody>
                    <tr>
                        <td>
                            <g:each in="${params.networks}" var="network">
                                <g:checkBox name="isNetworks" id="checkbox_${network.name}"
                                            value="${network.id}" checked="${params.isNetworks?.contains(network.id)}"/>${network.name}<br>
                            </g:each>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </g:if>
    <g:ifServiceEnabled name="${OpenStackRESTService.NOVA_VOLUME}">
        <div id="volumeOptionsTab">
            <table id="table_instanceVolumeOptions">
                <tbody>
                <tr class="prop">
                    <td class="name">
                        <label for="cb_options">Volume options:</label>
                    </td>
                    <td>
                        <g:select id="cb_options" name="volumeOptions" from="${params.volumeOptionsArray}" optionValue="displayName" value="${params.volumeOptions}"/>
                    </td>
                </tr>
                <tr id="volumes" class="prop">
                    <td class="name">
                        <label for= "cb_vol_type">Volume</label>
                    </td>
                    <td>
                        <g:select id="cb_vol_type" name="volume" from="${params.volumes}"
                                  optionKey="id" optionValue="displayName" value="${params.volume}"/>
                    </td>
                </tr>
                <tr id="volumeSnapshots" class="prop">
                    <td class="name">
                        <label for="cb_volumeSnapshot">Volume snapshot:</label>
                    </td>
                    <td>
                        <g:select id="cb_volumeSnapshot" name="volumeSnapshot" from="${params.volumeSnapshots}"
                                  optionKey="id" optionValue="name" value="${params.volumeSnapshot}"/>
                    </td>
                </tr>
                <tr id="deviceName" class="prop">
                    <td class="name">
                        <label>Device name:</label>
                    </td>
                    <td>
                        <g:textField id="input_instCreate_deviceName" name="deviceName" value="${params.deviceName}"/>
                    </td>
                </tr>
                <tr id="deleteOnTerminate" class="prop">
                    <td class="name">
                        <label for="deviceName">Delete on Terminate</label>
                    </td>
                    <td>
                        <g:checkBox id="checkbox_instCreate_deleteOnTerminate" name="deleteOnTerminate" value="${params.deleteOnTerminate ?: false}"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </g:ifServiceEnabled>

    <div id="postCreationTab">
        <table id="table_instanceScript">
            <tbody>
            <tr class="prop">
                <td class="name valignTop">
                    <label for="customizationScript">Customization script:</label>
                </td>
                <td>
                    <g:textArea id="customizationScript" name="customizationScript" value="${params.customizationScript}"/>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    </div>

    <div class="buttons">
        <g:buttonSubmit class="save" id="submit" action="save" title="Create new instance with selected parameters">Launch Instance</g:buttonSubmit>
    </div>
</g:form>
</div>


</body>
</html>