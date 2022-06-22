import 'slick-carousel';
import $ from 'jquery';
// polyfills
require('es6-promise').polyfill();
const axios = require('axios');


const initializeNewsCrousel = () => {
  if (document.querySelector('.slider-wrapper')) {
    const getListPath = document.querySelector('.news__content-carousel').dataset.listpath;
    if (getListPath) {
      const componentResourcePath = `.newslist.json?path=${getListPath}&limit=4`;
      let dataText = '';
      axios(getListPath + componentResourcePath)
        .then((response) => {
          for (let i = 0; i < response.data.pages.length; i += 1) {
            if (response.data.pages[i].thumbnailUrl) {
              dataText += `<div class="news__item">
                          <div><picture class="news__img"><source srcset="${response.data.pages[i].thumbnailUrl}" media="(min-width: 576)">
                          <img src="${response.data.pages[i].thumbnailVariation}" alt="${response.data.pages[i].title}"/></picture>
                          </div><div class="news__detail"><p class="news__info"><a href="${response.data.pages[i].link}" title="${response.data.pages[i].title}">${response.data.pages[i].title}</a></p>
                      </div></div>`;
            } else {
              dataText += `<div class="news__item">
                          <div><picture class="news__img"><source srcset="/content/dam/dpss/DPSS_News@2x.png" media="(min-width: 576)">
                          <img src="/content/dam/dpss/DPSS_News@2x.png" alt="Default LA County logo image"/
                          </picture>
                          </div><div class="news__detail"><p class="news__info"><a href="${response.data.pages[i].link}" title="${response.data.pages[i].title}">${response.data.pages[i].title}</a></p>
                      </div></div>`;
            }
          }
          document.querySelector('.news__list').innerHTML = dataText;
          $('.slider-wrapper').slick({
            dots: false,
            infinite: false,
            arrows: false,
            speed: 300,
            slidesToShow: 4,
            slidesToScroll: 4,
            responsive: [
              {
                breakpoint: 1046,
                settings: {
                  slidesToShow: 2,
                  slidesToScroll: 2,
                  infinite: true,
                  dots: true,
                  arrows: false,
                },
              },
              {
                breakpoint: 767,
                settings: {
                  slidesToShow: 1,
                  slidesToScroll: 1,
                  infinite: true,
                  dots: true,
                  arrows: false,
                },
              },
            ],
          });
        });
    }
  }
};
export default initializeNewsCrousel;
