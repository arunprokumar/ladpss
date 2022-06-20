$(document).ready(function(){
    var currentPagePath=window.location.href.split('.html')[0];
    var configuredRootPagePath=$(".pgrms-srvs-list").attr("data-listrootpath");
    var componentResourcePath='.prgm-srvs-list.json';
    if(configuredRootPagePath){
  $.ajax({url: currentPagePath+componentResourcePath+"?rootPagePath="+configuredRootPagePath, success: function(result){
       //Process the json to populate in dom
          var index=1;
          $.each(result, function(key,value) {
                 $('#prgms-srvs-left-list').append($('<div>').attr('class','rte__content prgms-srvs-left-list-item prgms-srvs-left-list-item'+index).append($('<div>').attr('class','color--black offsetB15 heading--large').append(
                       $('<a>').attr('href', value['link']).attr('class', 'color--dark-blue').attr('title', value['title']).append(value['title']))));
                 $('.prgms-srvs-left-list-item'+index).append($('<p>').attr('class','color--black').append(value['description']));
          index++;
   });//For loop
 }});//Ajax call
    }
});//Method