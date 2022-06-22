// Customization to OOTB Core Components
  var cancellInfoMarkup='';
  var charLeftText='';
  /*  Add asterisk sign for all the required fields */
  $('.form--group:has(input[required]) > label').append('<sup>*</sup>');
  $('.form--group:has(textarea[required]) > label').append('<sup>*</sup>');

  /*  Add description to cancel button */
  $('.btn--cancel > button').after(function() {
	  generateLanguageSpecificMsg();
	  return cancellInfoMarkup;
  	});

  /* Add $ for salary field */
  $("input[name=salary-type]").addClass('salary-type-text-box').before('<span class=\"salary-type-symbol\">$</span>');



/* Apply the text limit in textarea form element */
const defaultCharacterCountLimit = 300;
var charLimitedItems = document.querySelectorAll('.character--count'); 
charLimitedItems.forEach(function(charLimitItem) {
	var textAreaControl = charLimitItem.querySelector('textarea');
	textAreaControl.maxLength = defaultCharacterCountLimit;
	var newDiv = document.createElement('div');
	newDiv.className = 'character--count__message';
	charLimitItem.appendChild(newDiv);
	textAreaControl.addEventListener('keyup', function(){
		const textLength = textAreaControl.value.length;
		const textRemaining = defaultCharacterCountLimit - textLength;
		const displayHtml = charLeftText + " : <span class=\"character--count__remaining\">" + textRemaining + "</p>";
		$(newDiv).html(displayHtml);
	});

});





/*checking if browser is IE or Edge*/
if(window.navigator.userAgent.indexOf('Trident') > -1 || window.navigator.userAgent.indexOf('Edge') > -1){
  var cancelButtons = document.querySelectorAll('.btn--cancel .cmp-form-button');
} else {
  var cancelButtons = document.querySelectorAll(':scope .btn--cancel .cmp-form-button');
}
/* Implement form reset on Cancel click */
cancelButtons.forEach(function(cancelButton) {
	/* Reset form fields on cancel */
	cancelButton.addEventListener('click', function(){
		cancelButton.form.reset();
		displayDefaultCharacterCountLimitLeft();
	});

});



function displayDefaultCharacterCountLimitLeft() {
  /*checking if browser is IE  or Edge*/
if(window.navigator.userAgent.indexOf('Trident') > -1 || window.navigator.userAgent.indexOf('Edge') > -1){
    var charCountMessageDiv = document.querySelectorAll('.character--count .character--count__message');
  } else {
    var charCountMessageDiv = document.querySelectorAll(':scope .character--count .character--count__message');
  }

	charCountMessageDiv.forEach(function(messageDiv) {
		const displayHtml = charLeftText +" : <span class=\"character--count__remaining\">" + defaultCharacterCountLimit + "</p>";
		$(messageDiv).html(displayHtml);
	});
}

function generateLanguageSpecificMsg() {

	var currentLang = document.documentElement.lang;
	switch (currentLang) {
	case "es":
		cancellInfoMarkup = "<p>La cancelación borrará todos los campos.</p>";
		charLeftText = 'Límite de caracteres';
		break;
	default:
		cancellInfoMarkup = "<p>Canceling will erase all fields.</p>";
		charLeftText = 'Characters Left';
	}
}


$(document).ready(function(){
	generateLanguageSpecificMsg();
	displayDefaultCharacterCountLimitLeft();
});