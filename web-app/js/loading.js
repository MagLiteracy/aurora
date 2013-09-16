jQuery(function() {
    var url = window.location.href;
    var reloadPage = function() {
        window.location.replace(url);
    };
    var statusChecker = function() {
        jQuery.ajax({
            url: url,
            success: reloadPage,
            error: function(jqXHR, textStatus, errorThrown){
                if (jqXHR.status == 503) {
                    window.setTimeout(statusChecker, 5000);
                } else {
                    // If the page is throwing another type of error let the user see it
                    reloadPage();
                }
            }
        });
    }
    window.setTimeout(statusChecker, 5000);
});