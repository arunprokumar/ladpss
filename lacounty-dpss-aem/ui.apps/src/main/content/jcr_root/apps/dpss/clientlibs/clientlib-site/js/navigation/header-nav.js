const NAV_OPEN_STATE = document.querySelector('.navbar__toggle');
const NAV_LIST_OPEN_STATE = document.querySelector('.navbar');
let navActive = false;

/**
 * @desc nav component is used to toggle top navigation on Mobile device.
 */
const nav = () => {
  NAV_OPEN_STATE.addEventListener('click', (event) => {
    event.preventDefault();

    if (navActive === false) {
      navActive = true;
      NAV_LIST_OPEN_STATE.classList.add('header__nav--is-active');
    } else {
      navActive = false;
      NAV_LIST_OPEN_STATE.classList.remove('header__nav--is-active');
    }
  });
};
export default nav;
