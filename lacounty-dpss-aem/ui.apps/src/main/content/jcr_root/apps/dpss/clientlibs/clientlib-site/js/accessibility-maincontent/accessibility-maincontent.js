$(document).ready(function(){
    var breadCrumbId="breadcrumb";
    var notificationId="header-notification-static"
    var divsWithIds=$( "div[id]" );
    var mainContentId;
    var ids=[];
    var breadCrumbIdIndex;
    var notificationIdIndex;
    //Create the array of ids
    $.each( divsWithIds, function( key, value ) {
        var fetchedId=value.id;
        ids.push(fetchedId);        
   });
    //Check if breadcrumb is there which will be on all pages except home page
    if(jQuery.inArray(breadCrumbId, ids) !== -1){
            breadCrumbIdIndex= jQuery.inArray(breadCrumbId, ids);           
            mainContentId= ids[breadCrumbIdIndex +1];                      
    }else if(jQuery.inArray(notificationId, ids) !== -1){
        //Bread crumb is not there which is home page.Check for alert/notification id
         notificationIdIndex= jQuery.inArray(notificationId, ids);
         mainContentId= ids[notificationIdIndex+1];
    }else if (divsWithIds && divsWithIds.length >= 0){
        //Alert/notification is disabled so pick the 1st id from the array 
         mainContentId= ids[0];
    }

 $('.b-skip-navigation a').attr('href','#'+mainContentId);
});//