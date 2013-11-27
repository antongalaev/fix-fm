var timer;

$(document).ready(function(){
    $(".btn").click(function() {
        $("form").remove();
        $(".form-block").append("Wait a little bit for response")
            .append("<span class='dots'></span>")
            .css("color", "#2C3E50")
            .css("font-weight", "bold");



        $.get("ajax/", function(data) {
            clearInterval(timer);
            $(".form-block").html(data);
        })
    });
});

$(document).ajaxStart(function() {
    timer = setInterval(function() {
        if ($(".dots").text() == "...") {
            $(".dots").text(".");
        } else {
            $(".dots").append(".");
        }
    }, 500)
});