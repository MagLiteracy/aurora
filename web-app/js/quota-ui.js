jQuery(function() {
    // Script for quota progressbars
    jQuery.each ( quotaUsageArray, function( key, value ) {
        jQuery('<div/>', {
            text: 'Used ' + value[1] + ' of ' + value[0] + ' ' + value[3],
            class: 'quotaTitle'
        }).appendTo('#quotaContainer');
        jQuery('<div/>', {
            id: 'quota_'+key
        }).appendTo('#quotaContainer');
        jQuery( "#quota_"+key ).progressbar({value: parseInt(value[1]), max: parseInt(value[0]) });
    });
});
