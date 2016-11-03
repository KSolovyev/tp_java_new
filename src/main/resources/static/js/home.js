$(document).ready(function () {
    // var working = false;
    $("#form").submit(function(ev){
        var login = $("#login").val();
        if (!login || login == "") {
            return;
        }
        var request = JSON.stringify({"login" : login});
        $.ajax({
            type: 'POST',
            contentType: "application/json",
            url: "/api/guest/",
            data: request,
            success: function(response) {
                if (response.login) {
                    $("#answer").html("Hi, " + response.login);
                }
                setTimeout(function () {
                    window.location = "/squares.html";

                }, 2000);
            },
            error: function(data) {
                window.console.error("error logining in: " + data);
                //$("#commentList").append($("#name").val() + "<br/>" + $("#body").val());
                // alert("There was an error submitting comment");
            }
        });
        ev.stopPropagation();
        return false;

    });
});


