var alertNotificationExf=$("#alert-notification-exf").attr("data-alert-notification-exf-path");
var alertNotificationIsoLang=$("#alert-notification-iso-lang").attr("data-alert-notification-iso-lang-name");
var alertcookieName="alertNotificationAggreed_"+alertNotificationIsoLang;
function createCookie(name, value) {
	var c_name = name + "=" + value;
	var c_path = "path=/";
	// create the cookie
	document.cookie = c_name+";"+c_path;
}

function getCookie(cname) {
	var name = cname + "=", ca = document.cookie.split(';'), c, i;
	for (i = 0; i < ca.length; i++) {
		c = ca[i];
		while (c.charAt(0) === ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) === 0) {
			return c.substring(name.length, c.length);
		}
	}
	return null;
}

function getAlertAndNotification() {
	$.ajax({url: alertNotificationExf+".model.json", success: function(result){
       //Process the json to populate in dom
      $.each(result, function(key,value) {
          if(key == ':items'){
              var root=value.root;
              if(root){
                   $.each(root, function(key,value) {
                       if(key == ':items'){
                           var alertComp=value.alert_notification;
                           if(alertComp){
                               var alertOrNotification=alertComp.alertOrNotification;                               
                               if(alertOrNotification == 'alert'){ 
                                   $("#header-alert-static").append(alertComp.alertNotificationContent);  
                                   $("#header-alert-static").removeClass("is-hidden");
                               }else if(alertOrNotification == 'notification'){
                                  $("#header-notification-static").append(alertComp.alertNotificationContent);
                                  $("#header-notification-static").removeClass("is-hidden");
                               }

                           }
                       }
                       });//For loop 2

              }

          }
   });//For loop        
 }});//Ajax call
}

function createCookeOnAcceptance(){
     $("#header-alert-static a").click(function() {
        createCookie(alertcookieName,alertNotificationIsoLang);       
        });
        $("#header-notification-static a").click(function() {
        createCookie(alertcookieName,alertNotificationIsoLang);       
       });
}

$(document).ready(function(){
    //Execute the flow only if we have the Exf attribute in DOM which is a flag that alert/notification is enabled
    if(alertNotificationExf){
    //Check cookie is present for current language
    var cookieAvailable= getCookie(alertcookieName);
    //Validate cookie for current lang
    if(cookieAvailable != alertNotificationIsoLang){
     //Get the alert and notification
        getAlertAndNotification();
        createCookeOnAcceptance();
    }
    }
});//