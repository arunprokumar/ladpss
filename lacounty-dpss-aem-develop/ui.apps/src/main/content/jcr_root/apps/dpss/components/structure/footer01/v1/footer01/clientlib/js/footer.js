$(function() {
  $(document).on("dialog-ready", function() {
    function deleteRowValidation() {
      var minLength = 0;
      $(".footer-validation .coral3-Multifield-remove").prop(
        "disabled",
        false
      );

      setTimeout(function() {
        document
          .querySelectorAll(".footer-validation")
          .forEach(function(a) {
            if (a.childNodes.length - 4 === minLength) {
              for (var i = 0; i < minLength; i++) {
                a.childNodes[i].childNodes[1].setAttribute("disabled", true);
              }
            }
          });
      }, 10);
    } // validation to disable  and enable row button if only four exist

    function checkLength() {
      var size = 7;

      var totalLinkCount = $(
        '[data-granite-coral-multifield-name="./links"]>.coral3-Multifield-item'
      ).length;

      toggleButton(totalLinkCount, size);
    }

    function toggleButton(length, size) {
      setTimeout(function() {
        if (length < size) {
          $(".footer-validation>button").attr("disabled", false);
        } else {
          $(".footer-validation>button").attr("disabled", true);
        }
      }, 10);
    }
    if ($(".footer-validation")[0]) {
      //if dialog contains this class then call functions accordingly
      setTimeout(deleteRowValidation, 10);
      setTimeout(checkLength, 10);
      $(".coral3-Multifield")[0].on(
        "click",
        ".footer-validation button[is='coral-button']",
        checkLength
      );

      $(".coral3-Multifield")[0].on(
        "click",
        ".footer-validation button[is='coral-button']",
        deleteRowValidation
      ); //adding event listeners
      $(".coral3-Multifield")[0].on(
        "click",
        ".coral3-Multifield-remove[is='coral-button']",
        deleteRowValidation
      );
      $(".coral3-Multifield")[0].on(
        "click",
        ".coral3-Multifield-remove[is='coral-button']",
        checkLength
      );
    }
  });
});
