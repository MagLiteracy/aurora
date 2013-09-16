jQuery(function() {
    jQuery( "#dialog" ).dialog({
        resizable: false,
        autoOpen: false,
        height:400,
        width: 400,
        modal: true
    });
    // for subnet create
    jQuery('#subnetTabs').tabs();
});