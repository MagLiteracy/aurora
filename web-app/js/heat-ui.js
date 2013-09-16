jQuery(function() {
    jQuery( "#uploadFileDialog" ).dialog({
        autoOpen: false,
        width:350,
        modal: true
    });

    jQuery('#showUploadFileDialog').click(function() {
        jQuery( "#uploadFileDialog" ).dialog('open');
        jQuery( "button" ).button();
        return false;
    });
});