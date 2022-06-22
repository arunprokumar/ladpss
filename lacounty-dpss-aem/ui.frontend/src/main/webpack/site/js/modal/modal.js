import VanillaModal from 'tingle.js';

const CLASS_FEEDBACK_YES = document.querySelector('.js-tingle-modal-yes-modal');
const CLASS_FEEDBACK_NO = document.querySelector('.js-tingle-modal-no-modal');

const initializeModalWindow = () => {
  if (CLASS_FEEDBACK_NO || CLASS_FEEDBACK_YES) {
    const modalWindow = new VanillaModal.modal({
      footer: true,
      stickyFooter: true,
      closeMethods: ['overlay', 'button', 'escape'],
    });
    CLASS_FEEDBACK_YES.addEventListener('click', () => {
      modalWindow.open();
      modalWindow.setContent(document.querySelector('.tingle-demo-yes').innerHTML);
    });

    CLASS_FEEDBACK_NO.addEventListener('click', () => {
      modalWindow.open();
      modalWindow.setContent(document.querySelector('.tingle-demo-no').innerHTML);
    });

    modalWindow.addFooterBtn('', 'tingle-modal__close tingle-modal__closeIcon fa fa-times', () => {
      modalWindow.close();
    });
  }
};
export default initializeModalWindow;
