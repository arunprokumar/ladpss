/**
 * @desc Cross icon click event for hidding alerts and modal window.
 */
const initializeAlertsPrintDisableUtils = () => {
  const CLASS_CLOSE_BUTTON = document.querySelectorAll('.close');
  for (let i = 0; i < CLASS_CLOSE_BUTTON.length; i += 1) {
    CLASS_CLOSE_BUTTON[i].addEventListener('click', (e) => {
      e.target.parentElement.classList.add('is-hidden');
    });
  }


  /**
   * @desc Add disable attribute to all disabled class button.
   */
  const CLASS_DISABLE_BUTTON = document.querySelectorAll('.disabled');
  if (CLASS_DISABLE_BUTTON.length !== 0) {
    for (let j = 0; j < CLASS_DISABLE_BUTTON.length; j += 1) {
      CLASS_DISABLE_BUTTON[j].querySelector('button').disabled = true;
    }
  }

  /**
   * @desc Print page functionality with "js-print" class.
   */
  const CLASS_PRINT_BUTTON = document.querySelectorAll('.js-print');
  if (CLASS_PRINT_BUTTON.length !== 0) {
    for (let k = 0; k < CLASS_PRINT_BUTTON.length; k += 1) {
      CLASS_PRINT_BUTTON[k].addEventListener('click', () => {
        window.print();
      });
    }
  }
};
export default initializeAlertsPrintDisableUtils;
