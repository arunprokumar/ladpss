const CLASS_ACCORDION = document.querySelectorAll('.faq.accordion .accordion-container');
// const CLASS_ACCORDION_HEADER = document.querySelectorAll('.accordion-card__header');

/**
 * @desc toggleAccordion is used to toggle Accordion.
 */
function toggleAccordion(el) {
  el.classList.toggle('active');
  if (el.classList.length > 2) {
    el.setAttribute('aria-expanded', 'true');
  } else {
    el.setAttribute('aria-expanded', 'false');
  }
  const accordionContent = el.nextElementSibling;
  if (accordionContent.style.maxHeight) {
    accordionContent.style.maxHeight = null;
    accordionContent.classList.remove('show');
    accordionContent.setAttribute('aria-hidden', 'true');
  } else {
    accordionContent.style.maxHeight = `${accordionContent.scrollHeight}px`;
    accordionContent.classList.add('show');
    accordionContent.setAttribute('aria-hidden', 'false');
  }
}

function toggleSingleExpansionAccordion(el, header) {
  const allCardContent = el.getElementsByClassName('accordion-card__content');
  const SELECTED_ACCORDION_HEADER = el.getElementsByClassName('accordion-card__header');
  for (let i = 0; i < SELECTED_ACCORDION_HEADER.length; i += 1) {
    SELECTED_ACCORDION_HEADER[i].classList.remove('active');
    allCardContent[i].style.maxHeight = null;
    allCardContent[i].classList.remove('show');
    allCardContent[i].setAttribute('aria-hidden', 'true');
  }
  header.classList.add('active');
  if (header.classList.length > 2) {
    header.setAttribute('aria-expanded', 'true');
  } else {
    header.setAttribute('aria-expanded', 'false');
  }
  const ACCORDION_CARD_CONTENT = el.getElementsByClassName('active');
  const accordionContent = ACCORDION_CARD_CONTENT[0].nextElementSibling;
  accordionContent.style.maxHeight = `${accordionContent.scrollHeight}px`;
  accordionContent.classList.add('show');
  accordionContent.setAttribute('aria-hidden', 'false');
}

/**
 * @desc accordion is used to add click event for Accordion.
 */
const initializeAccordion = () => {
  CLASS_ACCORDION.forEach((e) => {
    const ACCORDION_HEADER = e.getElementsByClassName('accordion-card__header');
    if (e.hasAttribute('data-cmp-single-expansion')) {
      for (let i = 0; i < ACCORDION_HEADER.length; i += 1) {
        ACCORDION_HEADER[i].addEventListener('click', () => {
          toggleSingleExpansionAccordion(e, ACCORDION_HEADER[i]);
        });
      }
    } else {
      for (let i = 0; i < ACCORDION_HEADER.length; i += 1) {
        ACCORDION_HEADER[i].addEventListener('click', () => {
          toggleAccordion(ACCORDION_HEADER[i]);
        });
      }
    }
  });
};
export default initializeAccordion;
