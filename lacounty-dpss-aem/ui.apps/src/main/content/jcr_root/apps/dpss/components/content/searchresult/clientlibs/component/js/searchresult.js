/*******************************************************************************
 * Copyright 2019 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function() {
    "use strict";
    var RESULTS_JSON = "advancesearchresults.json";
    var PARAM_RESULTS_OFFSET = "resultsOffset";
    var $getFacetFilterCheckbox = document.getElementsByName("facet");
    var currentPageUrl = window.location.href;
    var getPageURL = currentPageUrl.substring(0, currentPageUrl.lastIndexOf("."));
    var getQueryParam = window.location.search;
    var searchFieldListGroup = document.querySelector(".cmp-search-list__item-group");
    var totalRecords = document.querySelector(".cmp-search__total-records");

    var searchField = document.querySelector(".search__field--view");

	var getRelativePath = searchField.dataset.cmpRelativePath;

    var getLoadMoreBtn = document.querySelector(".search__results--footer button");
    var searchResultEndMessage = document.getElementById("js-searchResults-endData");

    var getCategory = new Array();
    var resultSize = 0;
    var LIST_GROUP;
    var NUMBER_OF_RECORDS;


	// Capture search term for data layer prior to window load:
	window.digitalData = window.digitalData || {};
	window.digitalData.pageInfo = window.digitalData.pageInfo || {};
    var urlParams = new URLSearchParams(window.location.search);
    window.digitalData.pageInfo.onsiteSearchTerm = urlParams.get('fulltext');
	
	
    // Load More Button Click Function
    getLoadMoreBtn.addEventListener("click", function(event) {
        resultSize = resultSize + parseInt(document.querySelector(".search__field--view").dataset.cmpResultsSize);
        getCategory.length > 0 ? fetchDataNew(getCategory) : fetchDataNew();
    });

    // CATEGORIES CLICK EVENT
    // changing forEach to for loop due to IE 11 and edge issues
    for(var i = 0; i < $getFacetFilterCheckbox.length; i++) {
      getFacetFilterCheckbox[i].addEventListener("click", function(event) {
          resultSize = 0;
          if (getFacetFilterCheckbox[i].checked) {
              getCategory.push(getFacetFilterCheckbox[i].value);
          } else {
              var NEW_LIST = getCategory.filter(function(item) {
                  return item !== getFacetFilterCheckbox[i].value;
              });
              getCategory = NEW_LIST;
          }
          getCategory.length > 0 ? fetchDataNew(getCategory) : fetchDataNew();
      });
    }

    // On page load function
    function onDocumentReady() {
        searchResultEndMessage.style.display = "none";
        getLoadMoreBtn.style.display = "none";
        
        fetchDataNew();
    }

    // FETCH DATA API CALL
    function fetchDataNew(getCategory) {
      //FOR EDGE V16 AND V17
      if (/Edge/.test(navigator.userAgent)) {
        $.ajax({url: getDataURL(getCategory), success: function(result){
          displayDataOnPage(result);
       }});//Ajax call
      } else {
        var apiURL = getDataURL(getCategory);
        searchResultEndMessage.style.display = "none";
        fetch(apiURL)
            .then(function(response) {
                return response.json();
            })
            .then(function(json) {
                return displayDataOnPage(json);
            });
      }
    }

    // FETCH DATA URL CREATION
    function getDataURL(getCategory) {
        var fetchAPIURL = getPageURL + "." + RESULTS_JSON + getRelativePath + getQueryParam + "&" + PARAM_RESULTS_OFFSET + "=" + resultSize;
        var fetchAPIURLNew = getCategory ? fetchAPIURL + "&tags=" + getCategory : fetchAPIURL;

        return fetchAPIURLNew;
    }

    // Check null value and replace with empty string
    function checkNull(inputValue) {
        var value = "";
        if (inputValue === null) {
            return value;
        } else {
            return inputValue;
        }
    }

    // DISPLAY DATA ON PAGE LOAD
    function displayDataOnPage(resultData) {

        totalRecords.innerHTML = "";

        NUMBER_OF_RECORDS = "";
        var showCountVal = "";

        if (resultSize === parseInt(0)) {
            searchFieldListGroup.innerHTML = "";
            LIST_GROUP = "";
        }
        var data = resultData.data;
        var dataCount = 0
        if(data !== undefined ) {
        	dataCount = Object.keys(data).length;
        }
        if (dataCount !== 0) {
            searchResultEndMessage.style.display = "none";
            if (resultData.hasMore === true) {
                getLoadMoreBtn.style.display = "inline";
                showCountVal += resultData.totalRecords + "+";
            } else {
                getLoadMoreBtn.style.display = "none";
                showCountVal += resultData.totalRecords;
                if (resultData.isLastPage !== true) {
                    getLoadMoreBtn.style.display = "inline";
                }
            }
        } else {
            searchResultEndMessage.style.display = "block";
            getLoadMoreBtn.style.display = "none";
            showCountVal += resultData.totalRecords;
        }

        for (var i = 0; i < dataCount; i++) {
            LIST_GROUP += "<li class='cmp-searchresult-item'><div class='cmp-searchresult-title heading--medium'><a class='cmp-searchresult-link color--dark-blue' target='_blank' href=" + checkNull(data[i].url) + ">" + checkNull(data[i].title) + "</a></div><p class='cmp-searchresult-description color--black'>" + checkNull(data[i].excerpt) + "</p></li>";
        }
        searchFieldListGroup.innerHTML = LIST_GROUP;
        NUMBER_OF_RECORDS += "TOTAL RECORDS : " + showCountVal;
        // Capture search term for data layer prior to window load:
	    window.digitalData = window.digitalData || {};
        window.digitalData.pageInfo.onsiteSearchResults = resultData.totalRecords;
    }

    document.addEventListener("DOMContentLoaded", onDocumentReady);
})();
