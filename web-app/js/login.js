// enable loading indicator and disable "sign in" button during ajax request
jQuery.loginLoadingStart = function (){
    jQuery("#submit").prop("disabled", true).addClass("ui-state-disabled");
    jQuery(".loginLoading").show();
}
// disable loading indicator and enable "sign in" button after ajax request
jQuery.loginLoadingEnd = function (){
    jQuery("#submit").prop("disabled", false).removeClass("ui-state-disabled");
    jQuery(".loginLoading").hide();
}

jQuery.setLoginHint = function(selectEnv) {
    var loginHint = jQuery('.loginHint');
    if (loginHints[selectEnv]) {
        loginHint.html(loginHints[selectEnv]);
        loginHint.show();
    } else {
        loginHint.hide();
    }
}
jQuery(function() {
    // Prepare login info dialog
    jQuery("#loginConfirmationDialog").dialog({
        modal: true,
        width: 500,
        autoOpen: false,
        close: function() {
            jQuery.ajax({
                url : '/auth/signOut'
            })
            jQuery.loginLoadingEnd();
        }
    });

    // Adding login hint

    var selectedEnv = jQuery('#environment').val();
    //jQuery.setLoginHint(selectedEnv);

    // Singing in handler
    jQuery("#submit").click(function() {
        var highlightColor = '#51A2CA';
        if (themeSettings == 'dark') highlightColor = '#FF7F0F';

        if ((!jQuery("#username").val() || !jQuery("#password").val()) && jQuery(".c3specific").is(":visible")) {
            jQuery(".loginToolTip").show().delay(1800).fadeOut();
            if (!jQuery("#username").val())
                jQuery( "#username" ).effect( 'highlight', {color:highlightColor}, 1200 );
            if (!jQuery("#password").val())
                jQuery( "#password" ).effect( 'highlight', {color:highlightColor}, 1200 );
            return false;
        }  else {
            jQuery.loginLoadingStart();

            jQuery.ajax({
                url : '/auth/signIn.json',
                type : 'POST',
                data : {
                    password : jQuery("#password").val(),
                    username : jQuery("#username").val(),
                    environment : jQuery("#environment").val()
                },
                dataType: 'json',
                success : function (data){
                    if (data.errors.redirectUrl) {
                        window.location.replace(data.errors.redirectUrl)
                        return
                    }
                    // preparing Errors array, and compose message with errors list
                    var errorsArray = {};
                    var errorsArraySize = 0;
                    var msg = '<div class="loginLoadingProceed" style="display:none; text-align: center;"><img src="/images/spinner.gif" alt="Spinner" ></div><div class = "moreInfo">Failed to connect to the following datacenters:</div><table cellspacing="0" cellpadding="0">';
                    for(var key in data.errors) {
                        var msg = msg + '<tr><td class="loginDataCenter">' + key + ': </td><td>' + data.errors[key] + '</td></tr>';
                        errorsArray[key] = data.errors[key];
                        errorsArraySize++;
                    }
                    msg = msg + '</table>';

                    if (!window.location.origin)
                        window.location.origin = window.location.protocol+"//"+window.location.host;
                    var currentPath = window.location.origin;

                    var targetUri = document.getElementsByName('targetUri')[0].value;

                    if (errorsArraySize == 0) {
                        // if everything ok - just load main page
                        window.location.replace(currentPath + targetUri);
                    } else {
                        //some errors happens and needs more clarification
                        jQuery("#loginConfirmationDialog").html(msg);
                        // if  possibleToConnect == true then show "Proceed anyway" button
                        jQuery("#loginConfirmationDialog").dialog({
                            title: 'Connection problems',
                            buttons: [{
                                id:"proceed-anyway-button",
                                text: "Proceed anyway",
                                click: function() {
                                    jQuery("#proceed-anyway-button").prop("disabled", true).addClass("ui-state-disabled");
                                    jQuery(".loginLoadingProceed").show();
                                    jQuery.ajax({
                                        url : '/',
                                        type : 'GET',
                                        success : function(){
                                            jQuery.loginLoadingEnd();
                                            window.location.replace(currentPath + targetUri)
                                        },
                                        error : function(){
                                            jQuery.loginLoadingEnd();
                                            jQuery.ajax({
                                                url : '/auth/signOut'
                                            })
                                            jQuery(this).dialog("close")
                                        }
                                    })
                                }
                            },
                                {
                                    id : "close-button",
                                    text : "Return to login page",
                                    click : function(){
                                        jQuery.ajax({
                                            url : '/auth/signOut'
                                        })
                                        jQuery(this).dialog("close");
                                    }
                                }
                            ]
                        });
                        jQuery("#loginConfirmationDialog").dialog("open");
                    }
                },
                error : function(xhr){
                    jQuery.loginLoadingEnd();
                    var data = jQuery.parseJSON(xhr.responseText);
                    var errorsArray = {};
                    var errorsArraySize = 0;
                    var msg = '<div class="loginLoadingProceed" style="display:none; text-align: center;"><img src="/images/spinner.gif" alt="Spinner" ></div><div class = "moreInfo">Failed to connect to the following datacenters:</div><table cellspacing="0" cellpadding="0">';
                    for(var key in data.errors) {
                        var msg = msg + '<tr><td class="loginDataCenter">' + key + ': </td><td>' + data.errors[key] + '</td></tr>';
                        errorsArray[key] = data.errors[key];
                        errorsArraySize++;
                    }
                    msg = msg + '</table>';
                    jQuery("#loginConfirmationDialog").html(msg);
                    jQuery("#loginConfirmationDialog").dialog({
                        title: 'Connection problems',
                        buttons: [
                            {
                                id : "close-button",
                                text : "Return to login page",
                                click : function(){
                                    jQuery.ajax({
                                        url : '/auth/signOut'
                                    })
                                    jQuery(this).dialog("close");
                                }
                            }
                        ]
                    });
                    jQuery("#loginConfirmationDialog").dialog("open");
                }
            })
            return false
        }

    });

    jQuery('#environment').change(function() {
        var selectVal = jQuery(this).val();

        if (awsEnvironments.indexOf(selectVal) >= 0) {
            jQuery('.c3specific').hide();
        } else { jQuery('.c3specific').show();
        }
        //jQuery.setLoginHint(selectVal);
    });


});
