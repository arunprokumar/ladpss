const initializeToggleShowMoreBtn = () => {
  const CLASS_ALL_RTE_ELEMENT = document.querySelectorAll('.js-more-content');
  const CLASS_ALL_SHOW_MORE_BTN = document.querySelectorAll('.accordion-card__showmore-btn');
  if (CLASS_ALL_RTE_ELEMENT.length !== 0 && CLASS_ALL_SHOW_MORE_BTN.length !== 0) {
    CLASS_ALL_RTE_ELEMENT.forEach((getText) => {
      const getShowMoreDataAttri = getText.getAttribute('data-showmore');
      if (getShowMoreDataAttri !== 'false') {
        const moreBtn = getText.parentElement.querySelector('.showmore__border--btn');
        moreBtn.classList.remove('is-hidden');
        if (getText.offsetHeight > 90) {
          getText.classList.add('accordion-card__description--para');
        } else {
          moreBtn.classList.add('is-hidden');
        }
        moreBtn.addEventListener('click', () => {
          moreBtn.querySelector('.accordion-card__showmore-btn').querySelector('.more-text').classList.toggle('is-hidden');
          moreBtn.querySelector('.accordion-card__showmore-btn').querySelector('.less-text').classList.toggle('is-hidden');
          moreBtn.querySelector('.accordion-card__showmore-btn').querySelector('i').classList.toggle('fa-chevron-up');
          getText.classList.toggle('accordion-card__description--para');
        });
      }
    });
  }
};

export default initializeToggleShowMoreBtn;
