function cellUpdater(cell, timeout, checkLink, jsonPath, refreshFunc, finalStatus) {
    var status = cell.html();
    var path = (String(jsonPath)).split(".");
    
    var interval = setInterval(function () {
        if (checkLink) {
            jQuery.ajax({
                url: checkLink + '.json',
                dataType: 'json',
                success: function (data){
                    status = data;
                    for (var i = 0;i<path.length;i++){
                        status = status[path[i]];
                    }                      
                    cell.html(!in_array(status, finalStatus) ? status + ' <img src="/images/spinner.gif"/>' : status);
                },
                error: function () {
                    status = null;
                }

            });
        }
        if (in_array(status, finalStatus)) {
            clearInterval(interval);
            refreshFunc();
        }
    }, timeout);
}

function rowUpdater(row, timeout, checkLink, refreshFunc, finalStatusesMap) {
    var continueRefresh = true;
    var interval = setInterval(function(){
        if (checkLink) {
            jQuery.ajax({
                url: checkLink,
                dataType: 'html',
                success: function(data){
                    continueRefresh = false;
                    row.eq(0).html(data);
                    for(var key in finalStatusesMap){
                        var statusCell = jQuery("."+key, row);
                        if (!in_array(statusCell.text().trim(),finalStatusesMap[key])){
                            continueRefresh = true;
                            statusCell.html(statusCell.text()+' <img src="/images/spinner.gif"/>');
                        }
                    }

                },
                error: function(){
                    clearInterval(interval);
                    refreshFunc();
                }
            });
        }
        if (!continueRefresh){
            clearInterval(interval);
            refreshFunc();
        }

    },timeout)

}

function in_array(value, array)
{
    for(var i = 0; i < array.length; i++)
    {
        if(array[i] == value) return true;
    }
    return false;
}