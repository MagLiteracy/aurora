var finalStatus = ['available', undefined];

var refreshVolumeTable = function () {
    var container = jQuery('#table_container');
    jQuery.ajax({
        url: '/snapshot/_snapshots',
        dataType: 'html',
        success: function (data) {
            container.html(data);
        }
    });
    return null;
};


function statusUpdate(cell) {
    var showLink = cell.parent().find('.snapshot_show_link a').attr('href');
    var jsonPath = String('snapshot.status');
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
                    if (data.snapshot) {
                        status = data.snapshot.status;
                    } else {
                        status = undefined;
                    }
                    if (status && !in_array(status, finalStatus)) {
                        jQuery('#snapshot_status_value').html(status + '<img src="/images/spinner.gif"/>');
                    } else {
                        clearInterval(interval);
                        document.location = '/snapshot/list';
                    }
                },
                error: function (textStatus, errorThrown) {
                    clearInterval(interval);
                    document.location = '/snapshot/list';
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
    // autorefresh status on list-page
    jQuery.each(jQuery('.snapshot_status'), function () {
        if (!in_array(jQuery.trim(jQuery(this).html()), finalStatus)) {
            jQuery(this).html(jQuery(this).html() + '<img src="/images/spinner.gif"/>');
            statusUpdate(jQuery(this));
        }
    });

    //autorefresh status on show-page
    var snapshotStatusValue = jQuery('#snapshot_status_value');
    if (snapshotStatusValue.length && !in_array(snapshotStatusValue[0].innerText, finalStatus)) {
        snapshotStatusValue.html(snapshotStatusValue.html() + '<img src="/images/spinner.gif"/>');
        showPageUpdater();
    }
});




