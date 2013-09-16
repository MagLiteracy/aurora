jQuery.portsChange = function(selectVal) {
    if (selectVal != 'ICMP') {
        jQuery("#fromPortLabel").html('From port:');
        jQuery("#toPortLabel").html('To port:');
    } else {
        jQuery("#fromPortLabel").html('Type:');
        jQuery("#toPortLabel").html('Code:');
    }
}

jQuery.cidrTrChange = function(selectVal) {
    if (selectVal == '0') {
        jQuery('#cidrTr').show();
    } else {
        jQuery('#cidrTr').hide();
    }
}

jQuery(function() {
    jQuery.portsChange(jQuery('#select_sgShow_ipProtocol').val());
    jQuery.cidrTrChange(jQuery('#select_sgShow_sourceGroup').val());

    jQuery('#select_sgShow_ipProtocol').change(function() { jQuery.portsChange(jQuery(this).val()); });
    jQuery('#select_sgShow_sourceGroup').change(function() { jQuery.cidrTrChange(jQuery(this).val()); });
});