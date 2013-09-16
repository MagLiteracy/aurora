var finalStatus = ['available','in-use', undefined, '', 'error'];

var refreshVolumeTable = function () {
    var container = jQuery('#table_container');
    jQuery.ajax({
        url: '/volume/_volumes',
        dataType: 'html',
        success: function (data) {
            container.html(data);
        }
    });
    return null;
};


function statusUpdate(cell) {
    var showLink = cell.parent().find('.volume_show_link a').attr('href');
    var jsonPath = String('volume.status');
    cellUpdater(cell, 3000, showLink, jsonPath, refreshVolumeTable, finalStatus);
}

function showPageUpdater() {
    var link = document.location.pathname;
    var status = 1;
    var interval = setInterval(function () {
        if(link) {
            jQuery.ajax({
                url: link + '.json',
                dataType: 'json',
                success: function (data) {
                    if (data.volume) {
                        status = data.volume.status;
                    } else {
                        status = undefined;
                    }
                    if (status && !in_array(status, finalStatus)) {
                        jQuery('#volume_status_value').html(status + '<img src="/images/spinner.gif"/>');
                    } else {
                        clearInterval(interval);
                        document.location = '/volume/list';
                    }
                },
                error: function (textStatus, errorThrown) {
                    clearInterval(interval);
                    document.location = '/volume/list';
                }
            });
        }

        if (in_array(status, finalStatus)) {
            clearInterval(interval);
            document.location = document.URL;
        }
    }, 3000);
}

jQuery(function () {
    // autorefresh task status on list-page
    jQuery.each(jQuery('.volume_status'), function () {
        if (!in_array(jQuery.trim(jQuery(this).html()), finalStatus)) {
            jQuery(this).html(jQuery(this).html() + '<img src="/images/spinner.gif"/>');
            statusUpdate(jQuery(this));
        }
    });

    //autorefresh task status on show-page
    var volumeStatusValue = jQuery('#volume_status_value');
    if (volumeStatusValue.length && !in_array(volumeStatusValue[0].innerText, finalStatus)) {
        volumeStatusValue.html(volumeStatusValue.html() + '<img src="/images/spinner.gif"/>');
        showPageUpdater();
    }
});




