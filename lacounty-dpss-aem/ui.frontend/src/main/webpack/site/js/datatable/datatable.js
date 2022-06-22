import $ from 'jquery';
import 'datatables';

const TABLE_ELEMENT = document.getElementsByName('table');
const initializePaymentTabularData = () => {
  if (TABLE_ELEMENT) {
    $('.rte__content table').dataTable({
      paginate: true,
      bFilter: true,
      lengthChange: false,
      pagingType: 'full_numbers',
      bSort: true,
      language: {
        info: '_START_ to _END_ of _TOTAL_ records',
        search: 'Search tabular data',
        searchPlaceholder: 'Search tabular data',
      },
      drawCallback: (settings) => {
        if (settings.aoData.length <= 10) {
          $(this).parent().find('.dataTables_paginate').css('display', 'none');
          $(this).parent().find('.dataTables_info').css('display', 'none');
        }
      },
      pageLength: 10,
    });
  }
};
export default initializePaymentTabularData;
