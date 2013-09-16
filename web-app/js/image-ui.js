var finalStatus = ['active',undefined, '', 'killed'];

var refreshPage = function () {
    document.location = document.URL;
    return null;
};


function statusUpdate(cell) {
    var showLink = cell.parent().find('.image_show_link a').attr('href');
    var jsonPath = String('image.status');
    cellUpdater(cell, 3000, showLink, jsonPath, refreshPage, finalStatus);
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
                    if (data.image) {
                        status = data.image.status;
                    } else {
                        status = undefined;
                    }
                    if (status && !in_array(status, finalStatus)) {
                        jQuery('#image_status_value').html(status + ' <img src="/images/spinner.gif"/>');
                    } else {
                        clearInterval(interval);
                        document.location = document.URL;
                    }
                },
                error: function (textStatus, errorThrown) {
                    clearInterval(interval);
                    document.location = '/image/list';
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
    jQuery.each(jQuery('.image_status'), function () {
        if (!in_array(jQuery.trim(jQuery(this).text()), finalStatus)) statusUpdate(jQuery(this));
    });

    //autorefresh task status on show-page
    var imageStatusValue = jQuery('#image_status_value');
    if (imageStatusValue.length && !in_array(imageStatusValue[0].innerText, finalStatus)) {
        showPageUpdater();
    }
});




