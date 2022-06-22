/* eslint-disable no-shadow */
/* eslint-disable no-unused-expressions */
// polyfills
require('es6-promise').polyfill();
const axios = require('axios');

const RESULTS_JSON = 'newslist.json';
const $getFacetFilterCheckbox = document.getElementsByName('facet');
const currentPageUrl = window.location.href;
const getPageURL = currentPageUrl.substring(0, currentPageUrl.lastIndexOf('.'));
const searchFieldListGroup = document.querySelector('.cmp-news-search-list__item-group');
const searchField = document.querySelector('.news-search__field--view');
const getRelativePath = searchField !== null ? searchField.dataset.cmpRelativePath : '';

const getLoadMoreBtn = document.querySelector('.news-search__results--footer button');
const searchResultEndMessage = document.getElementById('js-news-searchResults-endData');

const $getSortAscDesVal = document.getElementById('js-sorting-des-asc');
const $getSortDirVal = document.getElementById('js-sort-dir');

let getCategory = new Array();
let resultSize = parseInt(0);
let LIST_GROUP;

let getSortAscDesVal = $getSortAscDesVal !== null ? getSortingVal($getSortAscDesVal) : '';
let getSortDirVal = $getSortDirVal !== null ? getSortingVal($getSortDirVal) : '';


// GET SORTING DROP-DOWN VALUES
function getSortingVal(val) {
  return val[val.selectedIndex].value;
}

if (getLoadMoreBtn !== null) {
  // Load More Button Click Function
  getLoadMoreBtn.addEventListener('click', () => {
    resultSize += parseInt(document.querySelector('.news-search__field--view').dataset.cmpResultsSize);
    getCategory.length > 0 ? fetchDataNew(getCategory) : fetchDataNew(false, document.getElementById('news-search-input').value);
  });
}
// CATEGORIES CLICK EVENT
if ($getFacetFilterCheckbox.length !== 0) {
  $getFacetFilterCheckbox.forEach((getFacetFilterCheckbox) => {
    getFacetFilterCheckbox.addEventListener('click', () => {
      resultSize = 0;
      if (getFacetFilterCheckbox.checked) {
        getCategory.push(getFacetFilterCheckbox.value);
      } else {
        const NEW_LIST = getCategory.filter(item => item !== getFacetFilterCheckbox.value);
        getCategory = NEW_LIST;
      }
      getCategory.length > 0 ? fetchDataNew(getCategory) : fetchDataNew();
    });
  });
}
// SORT BY CLICK EVENT AND PAGE LOAD
if ($getSortAscDesVal !== null) {
  $getSortAscDesVal.addEventListener('change', () => {
    resultSize = 0;
    getSortAscDesVal = getSortingVal($getSortAscDesVal);
    getCategory.length > 0 ? fetchDataNew(getCategory) : fetchDataNew();
  });
}
if ($getSortDirVal !== null) {
  $getSortDirVal.addEventListener('change', () => {
    resultSize = 0;
    getSortDirVal = getSortingVal($getSortDirVal.options);
    getCategory.length > 0 ? fetchDataNew(getCategory) : fetchDataNew();
  });
}
// On page load function
function onDocumentReady() {
  if (searchResultEndMessage !== null) {
    searchResultEndMessage.style.display = 'none';
    fetchDataNew();
  }
}

// FETCH DATA API CALL
function fetchDataNew(getCategory, qParam) {
  const apiURL = getDataURL(getCategory, qParam);
  axios(apiURL)
    .then((response) => {
      displayDataOnPage(response.data);
    });
}

// FETCH DATA URL CREATION
function getDataURL(getCategory, qParam) {
  const limitOfResultsPerPage = parseInt(document.querySelector('.news-search__field--view').dataset.cmpResultsSize);
  const fetchAPIURL = `${getPageURL}.${RESULTS_JSON}?path=${getRelativePath}&limit=${limitOfResultsPerPage}&offset=${resultSize}&orderby=${getSortDirVal}&sort=${getSortAscDesVal}&q=${qParam || ''}`;
  const fetchAPIURLNew = getCategory ? `${fetchAPIURL}&tags=${getCategory}` : fetchAPIURL;
  return fetchAPIURLNew;
}

// DISPLAY DATA ON PAGE LOAD
function displayDataOnPage(resultData) {
  if (resultSize === parseInt(0)) {
    searchFieldListGroup.innerHTML = '';
    LIST_GROUP = '';
  }
  const data = resultData.pages;

  for (let i = 0; i < data.length; i += 1) {
    LIST_GROUP += `<div class="title__img__list">
    <div class="offsetB15 heading--medium"><a href="${data[i].link || ''}" title="${data[i].title || 'Read More'}">${data[i].title || ''}</a></div>
    <div><picture class="news__img">
    <source srcset="${data[i].newsListTabImage || ''}" media="(min-width: 576)">
    <source srcset="${data[i].thumbnailUrl || ''}" media="(min-width: 1046px)">
    <img src="${data[i].newsListMobileImage || ''}" alt="${data[i].title || ''}"></picture>
    </div><div class="rte__content js-more-content"><p>${data[i].description || ''}<br />
    </p>
</div></div>`;
  }

  searchFieldListGroup.innerHTML = '';
  searchFieldListGroup.innerHTML = LIST_GROUP;
  if (resultData.hasMore === false) {
    getLoadMoreBtn.style.display = 'none';
    searchResultEndMessage.style.display = 'block';
  } else {
    getLoadMoreBtn.style.display = 'inline-block';
    searchResultEndMessage.style.display = 'none';
  }
}

// DISPLAY FILTERED DATA ON ENTER
function displayFilteredData(event) {
  resultSize = parseInt(0);
  const searchTerm = event.target.value;
  if (event.keyCode === 13) {
    fetchDataNew(false, searchTerm);
  }
}

document.addEventListener('DOMContentLoaded', onDocumentReady);
if (document.getElementById('news-search-input')) {
  document.getElementById('news-search-input').addEventListener('keydown', displayFilteredData);
}
