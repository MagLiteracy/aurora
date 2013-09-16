//  update Service name function
jQuery.udpateServiceName = function (interfaceId, netInterface) {
    // TODO: validate jQuery('#port') value;
    if (interfaceId && netInterface) {
        var name =  instanceInterfaces[interfaceId][netInterface]+':'+jQuery('#port').val();
        jQuery('#name').val(name);
    }
}

//  update Network interface function
jQuery.updateNetService = function (selectVal) {
    jQuery('#netInterface').html('');
    var index = 0;
    var selectedValue = '';
    jQuery.each (instanceInterfaces[selectVal], function( interfaceName, interfaceIp ) {
        if (index == 0) selectedValue = interfaceName;
        jQuery('#netInterface').append('<option value="'+interfaceName+'">'+interfaceName+'</option>');
        index++;
    });
    jQuery('#netInterface').combobox();
    jQuery('#select_netInterface').val(selectedValue);
    //jQuery('#netInterface').val(selectedValue);
    return selectedValue;
}

jQuery(function() {
    if (instanceSelected) jQuery('#instanceId').val(instanceSelected);
    if (interfaceSelected) jQuery('#netInterface').val(interfaceSelected);

    // update Network interface on page load
    var selectedInterfaceId = jQuery('#instanceId').val();
    if (instanceInterfaces[selectedInterfaceId]) {
        jQuery.updateNetService(selectedInterfaceId);
    }

    // set default Service name on page load
    if (!jQuery('#name').val()) {
        jQuery.udpateServiceName(jQuery('#instanceId').val(), jQuery('#netInterface').val());
    }

    // "Name instance" select change handler
    jQuery('#instanceId').change(function () {
        var selectedInterfaceId = jQuery(this).val();
        if (instanceInterfaces[selectedInterfaceId]) {
            selectedValue = jQuery.updateNetService(selectedInterfaceId);
            jQuery.udpateServiceName(selectedInterfaceId, selectedValue);
        }
    });

    // "Network interface" select change handler
    jQuery('#netInterface').live("change", function() {
        jQuery.udpateServiceName(jQuery('#instanceId').val(), jQuery(this).val());
    });

    // "Port" text input change handler
    jQuery('#port').keyup(function () {
        jQuery.udpateServiceName(jQuery('#instanceId').val(), jQuery('#netInterface').val());
    });

});
