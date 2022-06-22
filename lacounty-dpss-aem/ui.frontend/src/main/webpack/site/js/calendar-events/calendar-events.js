// polyfills
require('es6-promise').polyfill();
const axios = require('axios');

const CLASS_DISPLAY_EVENTS = document.querySelector('.calendar__items');
const currentPageUrl = window.location.href;

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

/**
 * @desc printData component is used to display a list of Events.
 */

const displayEventData = (data) => {
  let dataText = '';
  data.forEach((val) => {
    const splitStartDate = `${val.startDate}`.split(' ');
    const splitEndDate = `${val.endDate}`.split(' ');

    if (currentPageUrl.indexOf('/es/') > -1 || currentPageUrl.indexOf('/es.html') > -1) {
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

      // splitStartDate[0] = localeDay.get(splitStartDate[0]);
      // splitStartDate[1] = localeMonth.get(splitStartDate[1]);
      // splitEndDate[1] = localeMonth.get(splitEndDate[1]);
    }
    dataText += `<li><section class="calendar__events"><div class="event__scheduler">
    <span class="event__day">${splitStartDate[0] === 'null' ? '' : splitStartDate[0]}</span>
    <span class="event__date">${splitStartDate[1] === undefined ? '' : splitStartDate[1]} ${splitStartDate[2] === undefined ? '' : splitStartDate[2]}</span>`;
    if (splitStartDate[2] !== splitEndDate[2]) {
      dataText += `<span class="event__date"> to ${splitEndDate[1] === undefined ? '' : splitEndDate[1]} ${splitEndDate[2] === undefined ? '' : splitEndDate[2]}</span>`;
    }
    dataText += `</div > <div class="event__details">
        <a href="${val.link || val.url}" class="event__name">${val.title || ''}</a>
        <strong class="event__time">${val.startTime || ''} ${val.startTime ? '-' : ''}  ${val.endTime || ''}</strong></div></section ></li > `;
  });

  CLASS_DISPLAY_EVENTS.innerHTML = dataText;
};
/**
 * @desc calendarEvents component is used to fetch the data from API.
 */

const initializeCalendarEvents = () => {
  if (CLASS_DISPLAY_EVENTS !== null) {
    const getEventsData = document.querySelector('.events__list').getAttribute('data-listpath');
    const getCategoryListArray = document.querySelector('.events__list').dataset.categories;
    const getPagePath = document.querySelector('.events__list').dataset.pagepath;

    const getCategoryList = getCategoryListArray.replace('[', '').replace(']', '');
    const componentResourcePath = '.eventslist.json';
    let getDataUrl;
    if (getEventsData !== '') {
      getDataUrl = `${getPagePath + componentResourcePath}?path=${getEventsData}&tags=${getCategoryList}`;
    } else {
      getDataUrl = `https://api.myjson.com/bins/v44ch ?path=${getEventsData}&tags=${getCategoryList}`;
    }
    axios(getDataUrl)
      .then((response) => {
        displayEventData(response.data);
      });
  }
};
export default initializeCalendarEvents;
