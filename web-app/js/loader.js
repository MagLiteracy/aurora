
  // this code loads css based on cookie theme value
  var themeSettings = jQuery.cookie("theme");
  if (themeSettings == "dark") {
      jQuery('head').append('<link id="pathToMainCss" rel="stylesheet" href="'+cssPath+'/main_dark.css" type="text/css" />');
      jQuery('head').append('<link id="pathToJQCss" rel="stylesheet" href="'+cssPath+'/dark-theme/jquery-ui-1.10.3.custom.min.css" type="text/css" />');
  } else {
      jQuery('head').append('<link id="pathToMainCss" rel="stylesheet" href="'+cssPath+'/main.css" type="text/css" />');
      jQuery('head').append('<link id="pathToJQCss" rel="stylesheet" href="'+cssPath+'/custom-theme/jquery-ui-1.10.3.custom.min.css" type="text/css" />');
  }



