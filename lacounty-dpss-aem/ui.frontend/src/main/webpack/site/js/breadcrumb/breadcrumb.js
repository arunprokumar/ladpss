/**
 * @desc addClassSecondLastElement is used to add class in the second last element of breadcrumb.
 */
const CLASS_BREADCRUM_LIST = document.querySelectorAll('.breadcrumb__item');

const initializeBreadcrumb = () => {
  const totalOfBreadcrumb = CLASS_BREADCRUM_LIST.length;
  if (CLASS_BREADCRUM_LIST.length !== 0) {
    if (CLASS_BREADCRUM_LIST.length > 1) {
      CLASS_BREADCRUM_LIST[totalOfBreadcrumb - 2].classList.add('breadcrumb__link--immePreviousLink');
    } else {
      CLASS_BREADCRUM_LIST[totalOfBreadcrumb - 1].classList.add('breadcrumb__link--immePreviousLink');
    }
  }
};
export default initializeBreadcrumb;

