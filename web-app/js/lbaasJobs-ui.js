var finalStatus = ['COMPLETED', 'FAILED'];

var refreshJobsTable = function () {
    var container = jQuery('#table_container');
    jQuery.ajax({
        url: '/lbaas/_jobs',
        dataType: 'html',
        success: function (data) {
            container.html(data);
        }
    });
    return null;
};


function statusUpdate(cell) {
    var showLink = cell.parent().find('.job_show_link a').attr('href');
    var jsonPath = String('job.status');
    cellUpdater(cell,5000,showLink,jsonPath,refreshJobsTable, finalStatus);
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
                    if (data.job) {
                        status = data.job.status;
                    }
                    if (status && !in_array(status, finalStatus)) {
                        jQuery('#job_status_value').html(status + '<img src="/images/spinner.gif"/>');
                    } else {
                        clearInterval(interval);
                        document.location = '/lbaas/listJobs';
                    }
                },
                error: function (textStatus, errorThrown) {
                    clearInterval(interval);
                    document.location = '/lbaas/listJobs';
                }
            });
        }

        if (in_array(status, finalStatus)) {
            clearInterval(interval);
            document.location = document.URL;
        }
    }, 5000);
}

jQuery(function () {
    // autorefresh task status on list-page
    jQuery.each(jQuery('.job_status'), function () {
        if (!in_array(jQuery.trim(jQuery(this).html()), finalStatus)) {
            jQuery(this).html(jQuery(this).html() + '<img src="/images/spinner.gif"/>');
            statusUpdate(jQuery(this));
        }
    });

    //autorefresh task status on show-page
    var jobStatusValue = jQuery('#job_status_value');
    if (jobStatusValue.length && !in_array(jobStatusValue[0].innerText, finalStatus)) {
        jQuery('#job_status_value').html(jQuery('#job_status_value').html() + '<img src="/images/spinner.gif"/>');
        showPageUpdater();
    }
});


