var timer;

$(document).ready(function(){
    $(".btn").click(function() {

        // go for a token
        //window.open("http://www.last.fm/api/auth/?api_key=599a9514090a65b21d9c7d0e47605090");

        // send request for fixing and show result
        $.post("ajax/", $("#form").serialize(), function(data) {
            clearInterval(timer);
            $(".form-block").html(data);
        });

        // clear place on the page, style up
        $("form").remove();
        $(".form-block").append("Wait a little bit for response")
            .append("<span class='dots'></span>")
            .css("color", "#2C3E50")
            .css("font-weight", "bold");

        // "animate" dots
        timer = setInterval(function() {
            if ($(".dots").text() == "...") {
                $(".dots").text(".");
            } else {
                $(".dots").append(".");
            }
        }, 500)
    });
});