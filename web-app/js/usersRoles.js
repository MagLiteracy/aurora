// Preparing containers for users list
jQuery.initializeUsersList = function() {
    jQuery('#leftSideList').remove();
    jQuery('#rightSideList').remove();
    jQuery('<ul/>', {
        id: 'leftSideList',
        class: 'leftSideList connectedSortable'
    }).appendTo('#leftSideListContainer');
    jQuery('<ul/>', {
        id: 'rightSideList',
        class: 'rightSideList connectedSortable'
    }).appendTo('#rightSideListContainer');

    jQuery.each(usersArray, function(userName, userRole){
        // Select for roles
        userRoleSelect = '<select id="userName_' + userName + '">';
        jQuery.each(rolesArray, function(roleId, role){
            if (roleId == userRole)
                userRoleSelect += '<option value="' + roleId + '" selected>' + role + '</value>';
            else
                userRoleSelect += '<option value="' + roleId + '">' + role + '</value>';
        });
        userRoleSelect += '</select>';

        userNameFormatted = '';
        maxlenght = 28;
        if (userName.length > maxlenght)  userNameFormatted = userName.substring(0, maxlenght) + '...';
        else userNameFormatted = userName;
        if (userRole == "")
            jQuery("#leftSideList").append('<li><span id="user_' + userName + '" class="userName" title="' + userName + '">' + userNameFormatted + '</span><a id="user_plus_' + userName + '" href="#" class="moveToRight"> + </a></li>');
        else {
            jQuery("#rightSideList").append('<li><a id="user_minus_' + userName + '" href="#" class="moveToLeft"> - </a><span id="user_' + userName + '" class="userName" title="' + userName + '">' + userNameFormatted + '</span> <span class="userRole">' + userRoleSelect + '</span></li>');
        }
    });
    jQuery( "#leftSideList, #rightSideList" ).sortable({
        connectWith: ".connectedSortable"
    }).disableSelection();

    // Actions when item drops to the right side container
    jQuery( "#rightSideList" ).droppable({
        drop: function( event, ui ) {
            jQuery.prepareUserMoveToRight(jQuery(ui.helper));
        }
    });

    // Actions when item drops to the left side container
    jQuery( "#leftSideList" ).droppable({
        drop: function( event, ui ) {
            jQuery.prepareUserMoveToLeft(jQuery(ui.helper));
        }
    });
};

// preparing user node for moving from left to right
jQuery.prepareUserMoveToRight = function(movedItem) {
    userName = movedItem.find(".userName").html();
    userRoleSelect = '<select id="userName_' + userName + '">';
    jQuery.each(rolesArray, function(roleId, role){ userRoleSelect += '<option value="' + roleId + '">' + role + '</value>';});
    userRoleSelect += '</select>';
    movedItem.find(".moveToRight").remove();

    if (!movedItem.find(".userRole").length)  {
        movedItem.append('<span class="userRole"> ' + userRoleSelect + ' </span>');
        jQuery( "select" ).combobox();
    }

    if (!movedItem.find(".moveToLeft").length)  {
        movedItem.prepend('<a id="user_minus_' + userName + '" href="#" class="moveToLeft"> - </a>');
    }

    return movedItem;
};

// preparing user node for moving from right to left
jQuery.prepareUserMoveToLeft = function (movedItem) {
    //console.log(movedItem);
    movedItem.find(".userRole").remove();
    movedItem.find(".moveToLeft").remove();

    if (!movedItem.find(".moveToRight").length) {
        movedItem.append('<a id="user_plus_' + userName + '" href="#" class="moveToRight"> + </a>');
    }

    return movedItem;
};


jQuery(function() {
    //initialize Users List
    jQuery.initializeUsersList();

    // plus button click functionality
    jQuery( ".moveToRight" ).live('click',function(){
        var movedItem = jQuery(this).parent();
        var nodeIndex = movedItem.index();
        movedItem.hide( "slow", function() {
            if (nodeIndex >= jQuery("#rightSideList").children('li').length)
                jQuery("#rightSideList").append(movedItem);
            else
                jQuery("#rightSideList").find("li:eq("+nodeIndex+")").before(movedItem);
            jQuery.prepareUserMoveToRight(movedItem);
            movedItem.show( "slow" ) ;
        });
    });

    // minus button click functionality
    jQuery( ".moveToLeft" ).live('click',function(){
        var movedItem = jQuery(this).parent();
        var nodeIndex = movedItem.index();
        movedItem.hide( "slow", function() {
            if (nodeIndex >= jQuery("#leftSideList").children('li').length)
                jQuery("#leftSideList").append(movedItem);
            else
                jQuery("#leftSideList").find("li:eq("+nodeIndex+")").before(movedItem)
            jQuery.prepareUserMoveToLeft(movedItem);
            movedItem.show( "slow" ) ;
        });
    });

    //usersRolesEdit dialog initialize
//    jQuery( "#userRolesDialog" ).dialog({
//        autoOpen: false,
//        width:980,
//        modal: true
//    });

    //usersRolesEdit  dialog open
//    jQuery( "#usersRolesEdit").click(function() {
//        jQuery( "#userRolesDialog" ).dialog('open');
//        jQuery( "button" ).button();
//        return false;
//    });

    // Submit changes button
    jQuery( "#usersRolesSubmit").click(function() {
        newUsersArray = {};
        jQuery.each(usersArray, function(userName, userRole){
            var curVal = jQuery( "#userRolesDialog").find("#userName_"+ userName + " option:selected").val();
            if((curVal || userRole) && curVal != userRole)
                if (curVal)
                    newUsersArray[userName] = curVal;
                else
                    newUsersArray[userName] = null;

        });
        jQuery.usersSubmit(newUsersArray,tenantId);
//        alert(jQuery.toJSON( newUsersArray ));

        return false;
    });

    // Submit changes button for custom user
    jQuery("#userRoleSubmit").click(function(){
        newUsersArray = {};
        var userName = jQuery("#userName").val();
        newUsersArray[userName] = jQuery( "#userRole").val();

        jQuery.usersSubmit(newUsersArray,tenantId);
//        alert(jQuery.toJSON( newUsersArray ));
        return false;
    });

    // Remove role button for custom user
    jQuery("#userRoleRemove").click(function(){
        newUsersArray = {};
        var userName = jQuery("#userNameRemove").val();
        newUsersArray[userName] = null;

        jQuery.usersSubmit(newUsersArray,tenantId);
//        alert(jQuery.toJSON( newUsersArray ));
        return false;
    });
    // Reset changes button
    jQuery( "#usersRolesReset").click(function() {
        jQuery.initializeUsersList();
        jQuery( "select" ).combobox();
    });
});

jQuery.usersSubmit = function (newUsersArray, tenantId) {
    jQuery.ajax({
        url: '/tenant/usersSave.json',
        type: 'POST',
        data: 'tenantId='+tenantId+'&newUsersRoles='+jQuery.toJSON(newUsersArray),
        dataType: 'json',
        success: function(){
           // jQuery( "#userRolesDialog").dialog('close');
            jQuery(window.location.replace('/tenant/show/'+tenantId));
        },
        error: function(data) {
            jQuery.initializeUsersList();
            jQuery( "select" ).combobox();
            if (JSON.parse(data.responseText).error) {
                console.log(JSON.parse(data.responseText).error);
                jQuery("#error").text(JSON.parse(data.responseText).error);
                jQuery('html, body').animate({scrollTop: 0}, 'fast');
            }
        }
    });
};
