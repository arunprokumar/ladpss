// polyfills
require('es6-promise').polyfill();
const axios = require('axios');

const getNewsArchieParent = document.querySelector('.news__archive-list-pagination');
let offsetVal = 0;
const initializePagination = () => {
  if (getNewsArchieParent) {
    const getShowPerPageResultsVal = parseInt(document.querySelector('.news__archive-list').getAttribute('data-maxresults'));
    const getListPath = document.querySelector('.news__archive-list').getAttribute('data-listpath');
    const currentPagePath1 = window.location.href.split('.html')[0];
    const getPaginationDiv = document.querySelector('.easyPaginateNav');

    const textTruncate = (str, noOfWords) => `${str.split(' ').splice(0, noOfWords).join(' ')}...`;

    const addRemoveDisable = (staticOffSet, numberOfPages) => {
      if (staticOffSet === 0) {
        document.querySelector('.pagination-first').classList.add('pagination--disabled');
        document.querySelector('.pagination-previous').classList.add('pagination--disabled');
        document.querySelector('.pagination-next').classList.remove('pagination--disabled');
        document.querySelector('.pagination-last').classList.remove('pagination--disabled');
        document.querySelector('.pagination-next').removeAttribute('disabled', '');
        document.querySelector('.pagination-last').removeAttribute('disabled', '');
        document.querySelector('.pagination-first').setAttribute('disabled', '');
        document.querySelector('.pagination-previous').setAttribute('disabled', '');
      } else if (staticOffSet > 0 &&
        staticOffSet < (numberOfPages - 1) * getShowPerPageResultsVal) {
        document.querySelector('.pagination-first').classList.remove('pagination--disabled');
        document.querySelector('.pagination-previous').classList.remove('pagination--disabled');
        document.querySelector('.pagination-next').classList.remove('pagination--disabled');
        document.querySelector('.pagination-last').classList.remove('pagination--disabled');
        document.querySelector('.pagination-next').removeAttribute('disabled', '');
        document.querySelector('.pagination-last').removeAttribute('disabled', '');
        document.querySelector('.pagination-first').removeAttribute('disabled', '');
        document.querySelector('.pagination-previous').removeAttribute('disabled', '');
      } else {
        document.querySelector('.pagination-first').classList.remove('pagination--disabled');
        document.querySelector('.pagination-previous').classList.remove('pagination--disabled');
        document.querySelector('.pagination-next').classList.add('pagination--disabled');
        document.querySelector('.pagination-last').classList.add('pagination--disabled');
        document.querySelector('.pagination-next').setAttribute('disabled', '');
        document.querySelector('.pagination-last').setAttribute('disabled', '');
        document.querySelector('.pagination-first').removeAttribute('disabled', '');
        document.querySelector('.pagination-previous').removeAttribute('disabled', '');
      }
    };

    const fetchNewsData = () => {
      let dataText = '';
      axios(`${currentPagePath1}.newslist.json?path=${getListPath}&limit=${getShowPerPageResultsVal}&offset=${offsetVal}`)
        .then((response) => {
          for (let i = 0; i < response.data.pages.length; i += 1) {
            dataText += `<div class="title__img__list">
            <div class="offsetB15 heading--medium">${response.data.pages[i].title || ''}</div>
            <div><picture class="news__img">
            <source srcset="${response.data.pages[i].newsListTabImage || ''}" media="(min-width: 576)">
            <source srcset="${response.data.pages[i].thumbnailUrl || ''}" media="(min-width: 1046px)">
            <img src="${response.data.pages[i].newsListMobileImage || ''}" alt="${response.data.pages[i].title || ''}"></picture>
            </div><div class="rte__content js-more-content"><p>${response.data.pages[i].description || ''}<br />
            <a href="${response.data.pages[i].link || ''}" title="${response.data.pages[i].title || 'Read More'}">Read More</a></p>
        </div></div>`;
          }
          getNewsArchieParent.setAttribute('data-totalPages', response.data.numberOfPages);
          getNewsArchieParent.setAttribute('data-totalResults', response.data.totalResults);
          getNewsArchieParent.innerHTML = dataText;
          const OriginalString = document.querySelectorAll('.js-more-content span');
          OriginalString.forEach(($el) => {
            let StrippedString = $el.innerHTML.replace(/<[^>]*>/g, '');
            StrippedString = StrippedString.replace(/[\r\n]/g, '');
            const truncateText = textTruncate(StrippedString, 20);
            $el.innerHTML = truncateText;
          });
        });
    };

    const pagination = () => {
      fetchNewsData();
      let dataText = '';
      setTimeout(() => {
        const numberOfPages = parseInt(getNewsArchieParent.getAttribute('data-totalPages'));
        const totalResults = parseInt(getNewsArchieParent.getAttribute('data-totalResults'));
        if (numberOfPages > 0 && totalResults !== getShowPerPageResultsVal) {
          for (let k = 0; k <= numberOfPages - 1; k += 1) {
            if (k === 0) {
              dataText += `<a href="#" class="pagination-page current" data-pagenumber="${k}">${k + 1}</a>`;
            } else {
              dataText += `<a href="#" class="pagination-page" data-pagenumber="${k}">${k + 1}</a>`;
            }
          }
          getPaginationDiv.innerHTML = `<button class="pagination-first pagination--disabled" disabled>First</button>
                                    <button class="pagination-previous pagination--disabled" disabled>Previous</button>
                                    ${dataText}
                                    <button class="pagination-next">Next</button>
                                    <button class="pagination-last">Last</button>`;
          if (getShowPerPageResultsVal >= totalResults) {
            document.querySelector('.pagination-next').setAttribute('disabled', '');
            document.querySelector('.pagination-last').setAttribute('disabled', '');
            document.querySelector('.pagination-first').setAttribute('disabled', '');
            document.querySelector('.pagination-previous').setAttribute('disabled', '');
          }
        }
      }, 1000);
    };

    setTimeout(() => {
      const getNumberOfPages = parseInt(getNewsArchieParent.getAttribute('data-totalpages'));
      if (getNumberOfPages > 0) {
        document.querySelectorAll('.pagination-page').forEach(($el) => {
          $el.addEventListener('click', (event) => {
            event.preventDefault();
            document.querySelector('a.current').classList.remove('current');
            $el.classList.add('current');
            const getClickedlink = parseInt($el.getAttribute('data-pagenumber'));
            offsetVal = getShowPerPageResultsVal * getClickedlink;
            addRemoveDisable(offsetVal, getNumberOfPages);
            fetchNewsData();
          });
        });
        document.querySelector('.pagination-first').addEventListener('click', (e) => {
          e.preventDefault();
          offsetVal = 0;
          document.querySelector('.pagination-first').classList.add('pagination--disabled');
          document.querySelector('.pagination-previous').classList.add('pagination--disabled');
          document.querySelector('.pagination-next').classList.remove('pagination--disabled');
          document.querySelector('.pagination-last').classList.remove('pagination--disabled');

          document.querySelector('.pagination-next').removeAttribute('disabled', '');
          document.querySelector('.pagination-last').removeAttribute('disabled', '');
          document.querySelector('.pagination-first').setAttribute('disabled', '');
          document.querySelector('.pagination-previous').setAttribute('disabled', '');
          document.querySelector('a.current').classList.remove('current');
          document.querySelector('a.pagination-page').classList.add('current');
          fetchNewsData();
        });
        document.querySelector('.pagination-previous').addEventListener('click', (e) => {
          e.preventDefault();
          offsetVal -= parseInt(getShowPerPageResultsVal);
          addRemoveDisable(offsetVal, getNumberOfPages);
          document.querySelector('a.current').previousSibling.classList.add('current');
          document.querySelector('a.current').nextSibling.classList.remove('current');
          fetchNewsData();
        });
        document.querySelector('.pagination-next').addEventListener('click', (e) => {
          e.preventDefault();
          offsetVal += parseInt(getShowPerPageResultsVal);
          addRemoveDisable(offsetVal, getNumberOfPages);
          document.querySelector('a.current').nextSibling.classList.add('current');
          document.querySelector('a.current').classList.remove('current');
          fetchNewsData();
        });
        document.querySelector('.pagination-last').addEventListener('click', (e) => {
          e.preventDefault();
          offsetVal = getShowPerPageResultsVal * (getNumberOfPages - 1);
          document.querySelector('.pagination-first').classList.remove('pagination--disabled');
          document.querySelector('.pagination-previous').classList.remove('pagination--disabled');
          document.querySelector('.pagination-next').classList.add('pagination--disabled');
          document.querySelector('.pagination-last').classList.add('pagination--disabled');

          document.querySelector('.pagination-first').removeAttribute('disabled', '');
          document.querySelector('.pagination-previous').removeAttribute('disabled', '');
          document.querySelector('.pagination-next').setAttribute('disabled', '');
          document.querySelector('.pagination-last').setAttribute('disabled', '');
          document.querySelector('a.current').classList.remove('current');
          const lastNode = document.querySelectorAll('a.pagination-page');
          lastNode[lastNode.length - 1].classList.add('current');
          fetchNewsData();
        });
      }
    }, 3000);
    document.addEventListener('DOMContentLoaded', () => {
      pagination();
    });
  }
};
export default initializePagination;
