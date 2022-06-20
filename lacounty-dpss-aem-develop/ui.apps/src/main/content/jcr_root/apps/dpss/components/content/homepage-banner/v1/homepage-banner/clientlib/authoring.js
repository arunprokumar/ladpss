$(function() {
    $(document).on("dialog-ready", function() {
        setTimeout(replace, 10);

        // for chaging names to custom names like Add ->Add Items
        function replace() {
            setTimeout(function() {
                $(".banner-add-button>button").find("coral-button-label").replaceWith("Add Button");
            }, 10);
        }
    });



});