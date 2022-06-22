/* eslint-disable no-unused-vars */

import $ from 'jquery';
import moment from 'moment';
import 'url-search-params-polyfill';
import Fullcalendar from 'fullcalendar';
import { Calendar } from '@fullcalendar/core';
import esLocale from '@fullcalendar/core/locales/es';
import interactionPlugin from '@fullcalendar/interaction';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import listPlugin from '@fullcalendar/list';

let getCalendarTag;
let getQueryParam;
let getDataUrl;
let localeVar;
let todayText;

const localeDay = {
  Sun: 'dom',
  Mon: 'lun',
  Tue: 'mar',
  Wed: 'mié',
  Thu: 'jue',
  Fri: 'vie',
  Sat: 'sáb',
};

const localeMonth = {
  Jan: 'ene',
  Feb: 'feb',
  Mar: 'mar',
  Apr: 'abr',
  May: 'may',
  Jun: 'jun',
  Jul: 'jul',
  Aug: 'ago',
  Sep: 'sep',
  Oct: 'oct',
  Nov: 'nov',
  Dec: 'dic',
};

const GET_URL_PARAMS = new URLSearchParams(window.location.search);
const componentResourcePath = '.eventslist.json';

const initializeFullCalendar = () => {
  if (document.querySelector('.calendar__content')) {
    const GET_EVENTS_LIST_PATH_ATTRIBUTE = document.querySelector('.calendar__content').getAttribute('data-listpath');
    const GET_EVENTS_PAGE_PATH_ATTRIBUTE = document.querySelector('.calendar__content').getAttribute('data-pagepath');
    // Implement Calendar JS
    const miniCalendarUi = (e) => {
      // Get the page URL
      if (GET_EVENTS_LIST_PATH_ATTRIBUTE !== '') {
        getDataUrl = `${GET_EVENTS_PAGE_PATH_ATTRIBUTE + componentResourcePath}?path=${GET_EVENTS_LIST_PATH_ATTRIBUTE}`;
      } else {
        getDataUrl = 'https://api.myjson.com/bins/a1tt9';
      }
      if (getDataUrl.indexOf('/es/') > -1) {
        localeVar = 'es';
        todayText = 'hoy';
      } else {
        localeVar = 'en';
        todayText = 'today';
      }
      $('#dpss-detailCalendarDiv').fullCalendar({
        events: {
          url: getDataUrl,
          data: {
            q: getQueryParam,
            tags: getCalendarTag,
            limit: -1,
          },
        }, // change to appropriate CQ URL
        locale: localeVar,
        timezone: 'PST',
        editable: false,
        aspectRatio: 1.1,
        plugins: [interactionPlugin, dayGridPlugin, timeGridPlugin, listPlugin],
        header: {
          left: 'prev next',
          center: 'title',
          right: 'exportButton today',
        },
        buttonText: {
          today: todayText,
        },
        timeFormat: 'ha : ',
        nextDayThreshold: '00:00:00',
        eventLimit: true,
        loading(isLoading, view) {
          if (isLoading) $('#dpss-miniCalendarEventList').text(''); // clear old events from list
        },
        eventAfterRender(event, element, view) {
          const startMoment = moment(event.start);
          const endMoment = event.end ? moment(event.end) : false;
          let sDate = startMoment.format('YYYY-MM-DD');

          // mark start date table cell as an event date
          $(`td[data-date="${sDate}"]`, '#dpss-detailCalendarDiv').addClass('dpss-eventDate');

          // mark the rest of the days of multi-day event
          if (endMoment && !endMoment.isSame(startMoment, 'day')) {
            const thisMoment = moment(sDate);
            thisMoment.add(1, 'days');
            while (thisMoment.isBefore(endMoment)) {
              sDate = thisMoment.format('YYYY-MM-DD');
              $(`td[data-date="${sDate}"]`, '#dpss-detailCalendarDiv').addClass('dpss-eventDate');
              thisMoment.add(1, 'days');
            }
          }
          // do the default render
          return event;
        },
        eventDataTransform(event) {
          const startDateCalendar = parseInt(($($('td[data-date]')[0]).data('date')).replace(/-/g, ''));
          const endDateCalendar = parseInt(($($('td[data-date]')[$('td[data-date]').length - 1]).data('date')).replace(/-/g, ''));

          // add event to the event list below calendar
          const eventStart = event.start.substr(0, 10).replace(/-/g, '');
          const eventEnd = parseInt(event.end.substr(0, 10).replace(/-/g, ''));

          if ($('#dpss-miniCalendarEventList').text() === 'No events this month') $('#dpss-miniCalendarEventList').text(''); // clear
          const startMoment = moment(event.start);

          const splitStartDate = `${event.startDate}`.split(' ');
          const splitEndDate = `${event.endDate}`.split(' ');

          if (localeVar === 'es') {
            // splitStartDate[0] = localeDay.get(splitStartDate[0]);
            // splitStartDate[1] = localeMonth.get(splitStartDate[1]);
            // splitEndDate[1] = localeMonth.get(splitEndDate[1]);

            // translate day of the week
            Object.keys(localeDay).forEach((key) => {
              if (splitStartDate[0] === key) {
                splitStartDate[0] = localeDay[key];
              }
            });

            // translate start and end month
            Object.keys(localeMonth).forEach((key) => {
              if (splitStartDate[1] === key) {
                splitStartDate[1] = localeMonth[key];
              }

              if (splitEndDate[1] === key) {
                splitEndDate[1] = localeMonth[key];
              }
            });
          }

          if ((eventEnd >= startDateCalendar && eventEnd <= endDateCalendar)
              ||
             (eventStart >= startDateCalendar && eventStart <= endDateCalendar)
             ||
             (eventStart <= startDateCalendar && eventEnd >= endDateCalendar)) {
            let s = '<li class="dpss-miniCalendarEvent"><section class="calendar__events"><div class="event__scheduler">';
            s += `<span class="event__day">${splitStartDate[0] === 'undefined' ? `${startMoment.format('ddd')}` : splitStartDate[0]}</span>
                <span class="event__date"> ${splitStartDate[1] === undefined ? '' : splitStartDate[1]} ${splitStartDate[2] === undefined ? '' : splitStartDate[2]}</span>`;
            if ((splitStartDate[2] !== splitEndDate[2])
               ||
               (splitStartDate[1] !== splitEndDate[1])) {
              s += `<span class="event__date"> to ${splitEndDate[1] === undefined ? '' : splitEndDate[1]} ${splitEndDate[2] === undefined ? '' : splitEndDate[2]}</span>`;
            }
            s += '</div>';
            // Event title
            s += '<div class="event__details">';
            if (event.link || event.url) s += `<a href="${event.link || event.url}">`;
            s += event.title;
            if (event.link || event.url) s += '</a>';
            // Event description
            s += `<strong class="event__time">${event.startTime || ''} ${event.startTime ? '-' : ''}  ${event.endTime || ''}</strong></div>`;
            s += '</section></li>';
            $('#dpss-miniCalendarEventList').append(s);
          }
          return event;
        },
      });
    };

    getQueryParam = GET_URL_PARAMS.get('search-events');
    getCalendarTag = document.querySelector('.dpss-miniCalendarTag').value;
    document.querySelector('.dpss-miniCalendarTag').addEventListener('change', (e) => {
      getCalendarTag = e.target.value;
      if (GET_EVENTS_LIST_PATH_ATTRIBUTE !== '') {
        getDataUrl = `${GET_EVENTS_PAGE_PATH_ATTRIBUTE + componentResourcePath}?path=${GET_EVENTS_LIST_PATH_ATTRIBUTE}`;
      } else {
        getDataUrl = 'https://api.myjson.com/bins/a1tt9';
      }
      const events = {
        url: getDataUrl,
        data: {
          q: getQueryParam,
          tags: getCalendarTag,
          limit: -1,
        },
      };
      $('#dpss-detailCalendarDiv').fullCalendar('removeEventSource', events);
      $('#dpss-detailCalendarDiv').fullCalendar('addEventSource', events);
    });

    /**
     * @desc This funtion is used to search a particular event from calendar.
    */
    document.querySelector('#dpss-miniCalendarSearchForm').addEventListener('submit', ($el) => {
      getQueryParam = document.querySelector('#dpss-miniCalendarSearch').value;
      $el.preventDefault();
      const events = {
        url: getDataUrl,
        data: {
          q: getQueryParam,
          tags: getCalendarTag,
          limit: -1,
        },
      };
      $('#dpss-detailCalendarDiv').fullCalendar('removeEventSource', events);
      $('#dpss-detailCalendarDiv').fullCalendar('addEventSource', events);
    });

    miniCalendarUi();
  }
};
export default initializeFullCalendar;
