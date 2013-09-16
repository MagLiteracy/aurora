function drawBackButton(parent)
{
    var header = jQuery("h1");
    if (parent!=""){
        header.append(" <a href='"+parent+"' id='upButton'>Up</a>");

    }

}