var timer;

$(document).ready(function(){
    $("#header").click(function() {
        location.href = "/fixfm";
    });
    $(".btn").click(function() {
        // send request for fixing the scrobbles and show the result
        $.post("ajax/", $("#form").serialize(), function(data) {
            clearInterval(timer);
            $(".form-block").html(data);
        });

        // clear place on the page, style it
        $("form").remove();
        $(".form-block").append("Wait a little bit for response")
            .append("<span class='dots'></span>")
            .css("color", "#2C3E50")
            .css("font-weight", "bold");

        // "animate" dots
        var dots = $(".dots");
        timer = setInterval(function() {
            if (dots.text() == "...") {
               dots.text(".");
            } else {
                dots.append(".");
            }
        }, 500)
    });
});