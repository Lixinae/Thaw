$(document).ready(function(){

		});


function connectToServer(){

	$.post("/api/connectToServer",
	    JSON.stringify({userName:userName,password:password})) // Crypté le pass ?
	    .done(function(response){
            alert("success connectToServer");
			window.location.href = "./client/client.html" // Redirige vers la page du tchat
        })
        .fail(function(response){
            alert("fail connectToServer");
        })
        .always(function() {

        });
}