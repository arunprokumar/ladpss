import 'slick-carousel';
import $ from 'jquery';

const initializeCardCrousel = () => {
  $('.card__slider-wrapper').slick({
    dots: false,
    infinite: false,
    speed: 300,
    slidesToShow: 2,
    slidesToScroll: 2,
    responsive: [
      {
        breakpoint: 1046,
        settings: {
          slidesToShow: 2,
          slidesToScroll: 2,
          infinite: false,
          dots: false,
        },
      },
      {
        breakpoint: 767,
        settings: {
          slidesToShow: 1,
          slidesToScroll: 1,
          infinite: true,
          dots: false,
          prevArrow: "<span class='fa fa-chevron-left slick-prev'></span>",
          nextArrow: "<span class='fa fa-chevron-right slick-next'></span>",
        },
      },
    ],
  });
};
export default initializeCardCrousel;
