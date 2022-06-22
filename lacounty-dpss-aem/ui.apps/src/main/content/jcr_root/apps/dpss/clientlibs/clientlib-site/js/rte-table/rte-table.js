$(document).ready(function(){
  $('.rte__content table').each(function(){
    if ($(this).find('caption').length > 0) {
      $(this).parent().find('.dataTables_filter').addClass('filter-absolute');
    } else {
      $(this).parent().find('.dataTables_filter').addClass('filter-relative');
    }
  })
});
