const CLASS_NAV_LIST_OPEN_STATE = document.querySelector('.navbar');
const CLASS_OPEN_LANGUAGE_SELECTOR = document.querySelector('.header__item-dropdown');
const CLASS_GET_LANGUAGE = document.querySelectorAll('.header__item-list li');
const CLASS_SELECTED_LANGUAGE = document.querySelector('.header__item-current');
const CLASS_LIST_LANGAUGE = document.querySelector('.header__item-list');
let navActive = false;
const MED_BREAKPOINT = 767;

/**
 * @desc nav function is used to toggle top navigation on Mobile device.
 */
const nav = () => {
  document.addEventListener('click', ($el) => {
    if ($el.target.parentElement.classList.contains('navbar__toggle') ||
      $el.target.classList.contains('header__menu-close') ||
      $el.target.classList.contains('navbar__toggle')) {
      if (navActive === false) {
        navActive = true;
        CLASS_NAV_LIST_OPEN_STATE.classList.add('header__nav--is-active');
        if (CLASS_LIST_LANGAUGE) {
          CLASS_OPEN_LANGUAGE_SELECTOR.classList.remove('up-icon');
          CLASS_LIST_LANGAUGE.classList.add('hide');
          CLASS_LIST_LANGAUGE.classList.remove('show');
        }
      } else {
        navActive = false;
        CLASS_NAV_LIST_OPEN_STATE.classList.remove('header__nav--is-active');
      }
    }
  });
};

/**
 * @desc languageSelectior funtion is used to select the language preference from dropdown.
 */
const languageSelectior = () => {
  CLASS_OPEN_LANGUAGE_SELECTOR.addEventListener('click', ($el) => {
    $el.preventDefault();
    CLASS_OPEN_LANGUAGE_SELECTOR.classList.add('up-icon');
    CLASS_LIST_LANGAUGE.classList.add('show');
    CLASS_LIST_LANGAUGE.classList.remove('hide');
  });
  CLASS_GET_LANGUAGE.forEach((el) => {
    el.addEventListener('click', () => {
      CLASS_SELECTED_LANGUAGE.innerText = el.innerText;
      document.querySelector('li.active').classList.remove('active');
      el.classList.add('active');
      CLASS_OPEN_LANGUAGE_SELECTOR.classList.remove('up-icon');
      CLASS_LIST_LANGAUGE.classList.add('hide');
      CLASS_LIST_LANGAUGE.classList.remove('show');
    });
  });
};
if (document.querySelector('.language-close-icon')) {
  document.querySelector('.language-close-icon').addEventListener('click', () => {
    CLASS_OPEN_LANGUAGE_SELECTOR.classList.remove('up-icon');
    CLASS_LIST_LANGAUGE.classList.add('hide');
    CLASS_LIST_LANGAUGE.classList.remove('show');
  });
}
/**
 * @desc initializeHeader is to Initialized the header JS functions
 */
function initializeHeader() {
  nav();
  const windowWidth = window.innerWidth;
  if (windowWidth >= MED_BREAKPOINT && CLASS_LIST_LANGAUGE) {
    CLASS_OPEN_LANGUAGE_SELECTOR.classList.remove('up-icon');
    CLASS_LIST_LANGAUGE.classList.add('hide');
    CLASS_LIST_LANGAUGE.classList.remove('show');
  }
  if (CLASS_LIST_LANGAUGE) {
    languageSelectior();
  }
  // Detect all clicks on the document
  document.addEventListener('click', (event) => {
    // If user clicks inside the element, do nothing
    if (event.target.closest('.header__item-dropdown')) return;

    // If user clicks outside the element, hide it!
    CLASS_OPEN_LANGUAGE_SELECTOR.classList.remove('up-icon');
    CLASS_LIST_LANGAUGE.classList.add('hide');
    CLASS_LIST_LANGAUGE.classList.remove('show');
  });
}


export default initializeHeader;
