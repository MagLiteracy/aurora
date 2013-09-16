var finalStatusesMap = {instance_taskStatus : ['', null], instance_status:['Active','Error','Shutoff',null]};

var refreshInstanceTable = function () {
    var container = jQuery('#table_container');
    jQuery.ajax({
        url: '/instance/_instances',
        dataType: 'html',
        success: function (data) {
            container.html(data);
            buttonInitialization();
        }
    });
    jQuery.onPageReady();
    return null;
};


function statusUpdate(row) {
    var instanceId = row.find('.instance_id').text();
    var rowLink = "/instance/_instanceRow/"+instanceId;
    rowUpdater(row,3000,rowLink, refreshInstanceTable, finalStatusesMap);
}

function showPageUpdater(){
    var link = document.location.pathname;
    var taskStatus = 1;
    var interval = setInterval(function () {
        if (link) {
            jQuery.ajax({
                url: link + '.json',
                dataType: 'json',
                success: function (data) {
                    if(data.instance) {
                        taskStatus = data.instance.taskStatus;
                        if (taskStatus){
                            jQuery('#instance_show_buttons_area').html('<b>'+taskStatus+'</b><img src="/images/spinner.gif"/>');
                            if (taskStatus == 'Deleted'){
                                clearInterval(interval);
                                document.location = '/instance/list';
                            }
                        }
                    } else {
                        clearInterval(interval);
                        document.location = '/instance/list';
                    }
                },
                error: function (textStatus, errorThrown) {
                    clearInterval(interval);
                    document.location = '/instance/list';
                }
            });
        }
        if (!taskStatus) {
            clearInterval(interval);
            document.location = document.URL;
        }
    }, 3000);
}

jQuery(function () {
    // for instance create
    jQuery('#instanceTabs').tabs();

    jQuery('#snapshotSources').hide();

    jQuery('#cb_instance_source').change(function () {
        var selectVal = jQuery(this).val();
        if (selectVal == imageString) {
            jQuery('#snapshotSources').hide();
            jQuery('#imageSources').show();
        } else if (selectVal == snapshotString) {
            jQuery('#snapshotSources').show();
            jQuery('#imageSources').hide();
        }
    });

    jQuery('#cb_instance_source').change();

    jQuery('#volumes').hide();
    jQuery('#volumeSnapshots').hide();
    jQuery('#deviceName').hide();
    jQuery('#deleteOnTerminate').hide();

    jQuery('#cb_options').change(function () {
        var selectVal = jQuery(this).val();
        if (selectVal == notBootString) {
            jQuery('#volumes').hide();
            jQuery('#volumeSnapshots').hide();
            jQuery('#deviceName').hide();
            jQuery('#deleteOnTerminate').hide();
        } else if (selectVal == bootFromVolumeString) {
            jQuery('#volumes').show();
            jQuery('#volumeSnapshots').hide();
            jQuery('#deviceName').show();
            jQuery('#deleteOnTerminate').show();
        } else if (selectVal == bootFromSnapshotString) {
            jQuery('#volumes').hide();
            jQuery('#volumeSnapshots').show();
            jQuery('#deviceName').show();
            jQuery('#deleteOnTerminate').show();
        }
    });

    jQuery('#cb_options').change();


    // autorefresh status and task status on list-page
    jQuery.each(jQuery('.instance_row'), function () {
        var needRefresh = false;
        for(var key in finalStatusesMap){
            var statusCell = jQuery("."+key, this);
            if (!in_array(statusCell.text().trim(),finalStatusesMap[key])){
                needRefresh = true;
                statusCell.html(statusCell.text()+' <img src="/images/spinner.gif"/>');
            }
        }
        if (needRefresh){
            statusUpdate(jQuery(this));
        }
    });

    //autorefresh task status on show-page
    var buttonsAreas = jQuery('#instance_show_buttons_area');
    if(buttonsAreas.length && jQuery('#taskStatus',buttonsAreas[0]).length){
        showPageUpdater();
    }

    jQuery("#showUsageDialog").dialog({
        modal: true,
        width: 400,
        autoOpen: false
    });

    jQuery("#root_credentials, #corp_credentials").click(function() {
        jQuery(this).select();
    });

    function showLoginDialog(address) {
        jQuery("#corp_credentials").val("ssh " + userName + "@" + address);
        jQuery("#root_credentials").val("ssh root@"+address);
        jQuery("#loginDialogSpinner")[0].style.display = 'none'
        jQuery("#credentialsArea")[0].style.display = 'block'
    }

    function prepareLoginDialog(address) {
        jQuery("#loginDialogSpinner")[0].style.display = 'table-cell'
        jQuery("#credentialsArea")[0].style.display = 'none'
        jQuery("#showUsageDialog").dialog("open");
        jQuery.ajax({
            type: 'POST',
            url: '/instance/getFQDN.json?ip=' + address,
            dataType: 'json',
            success: function (data) {
                if (data.address) {
                    showLoginDialog(data.address);
                } else {
                    showLoginDialog(address);
                }
            },
            error: function () {
                showLoginDialog(address);
            }
        });
    }

    jQuery(document).on("click",".showIpHelp",function() {
        prepareLoginDialog(jQuery(this).html());

    });

    jQuery(".showFloatIpHelp").click(function() {
        prepareLoginDialog(jQuery(this).attr('title'));
    });

});




