// There are many ways to pick a DOM node; here we get the form itself and the email
// input box, as well as the span element into which we will place the error message.
import $ from 'jquery';

const form = document.getElementById('main');
const email = document.getElementById('email');
const fname = document.getElementById('fname');
const lname = document.getElementById('lname');
const organisation = document.getElementById('organisation');

const initializeFormValidation = () => {
  // ADD asterisk sign for all the required fields
  $(() => {
    $('.form--group:has(input[required]) > label').append('<sup>*</sup>');
    $('.form--group:has(textarea[required]) > label').append('<sup>*</sup>');
    $('.cmp-form-text:has(input[required]) > label').append('<sup>*</sup>');
    $('.cmp-form-text:has(textarea[required]) > label').append('<sup>*</sup>');
  });

  /* Apply the text limit in textare form element */
  const maxText = 300;
  if (document.getElementById('character--count')) {
    document.getElementById('character--count').innerHTML = `Characters Left : <span>${maxText} </span>`;
    document.querySelector('textarea').addEventListener('keyup', () => {
      const textLength = document.querySelector('textarea').value.length;
      const textRemaining = maxText - textLength;
      document.getElementById('character--count').innerHTML = `Characters Left : <span>${textRemaining} </span>`;
    });
  }

  if (form !== null) {
    email.addEventListener('input', () => {
      // Each time the user types something, we check if the
      // email field is valid.
      if (email.validity.valid) {
        // In case there is an error message visible, if the field
        // is valid, we remove the error message.
        email.parentElement.querySelector('.error').innerHTML = ''; // Reset the content of the message
        email.parentElement.querySelector('.error').className = 'error'; // Reset the visual state of the message
      }
    }, false);
    fname.addEventListener('input', () => {
      // Each time the user types something, we check if the
      // email field is valid.
      if (fname.validity.valid) {
        // In case there is an error message visible, if the field
        // is valid, we remove the error message.
        fname.parentElement.querySelector('.error').innerHTML = ''; // Reset the content of the message
        fname.parentElement.querySelector('.error').className = 'error'; // Reset the visual state of the message
      }
    }, false);

    organisation.addEventListener('input', () => {
      // Each time the user types something, we check if the
      // email field is valid.
      if (organisation.validity.valid) {
        // In case there is an error message visible, if the field
        // is valid, we remove the error message.
        organisation.parentElement.querySelector('.error').innerHTML = ''; // Reset the content of the message
        organisation.parentElement.querySelector('.error').className = 'error'; // Reset the visual state of the message
      }
    }, false);

    lname.addEventListener('input', () => {
      // Each time the user types something, we check if the
      // email field is valid.
      if (lname.validity.valid) {
        // In case there is an error message visible, if the field
        // is valid, we remove the error message.
        lname.parentElement.querySelector('.error').innerHTML = ''; // Reset the content of the message
        lname.parentElement.querySelector('.error').className = 'error'; // Reset the visual state of the message
      }
    }, false);

    form.addEventListener('submit', (event) => {
      // Each time the user tries to send the data, we check
      // if the email field is valid.
      if (!email.validity.valid) {
        // If the field is not valid, we display a custom
        // error message.
        email.parentElement.querySelector('.error').innerHTML = 'Please enter a valid Email Address';
        email.parentElement.querySelector('.error').className = 'error active';
        // And we prevent the form from being sent by canceling the event
        event.preventDefault();
      }
      if (!fname.validity.valid) {
        // If the field is not valid, we display a custom
        // error message.
        fname.parentElement.querySelector('.error').innerHTML = 'Please enter First Name';
        fname.parentElement.querySelector('.error').className = 'error active';
        // And we prevent the form from being sent by canceling the event
        event.preventDefault();
      }
      if (!lname.validity.valid) {
        // If the field is not valid, we display a custom
        // error message.
        lname.parentElement.querySelector('.error').innerHTML = 'Please enter Last Name';
        lname.parentElement.querySelector('.error').className = 'error active';
        // And we prevent the form from being sent by canceling the event
        event.preventDefault();
      }
      if (!organisation.validity.valid) {
        // If the field is not valid, we display a custom
        // error message.
        organisation.parentElement.querySelector('.error').innerHTML = 'Please enter your Organisation URL';
        organisation.parentElement.querySelector('.error').className = 'error active';
        // And we prevent the form from being sent by canceling the event
        event.preventDefault();
      }
    }, false);
  }
};
export default initializeFormValidation;
