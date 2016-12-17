
$(document).ready(function(){

		});


function connectToServer(userName,password){

    var userName = $('#userNameEntry').val();
    var password = $('#userPasswordEntry').val();

	$.post("/api/connectToServer",
	    JSON.stringify({userName:userName,password:password})) // Crypté le pass ?
	    .done(function(response){
            alert("success connectToServer");
            sessionStorage.userName = JSON.stringify(userName);//to memorize login through session
			window.location.href = "./client/client.html"; // Redirige vers la page du tchat
        })
        .fail(function(response){
            alert("fail connectToServer");
        })
        .always(function() {

        });
}
function createLogin(userName,password){
    var userName = $('#userNameEntry').val();
    var password = $('#userPasswordEntry').val();
	$.post("/api/createAccount",
	    JSON.stringify({userName:userName,password:password}))
	    .done(function(response){
            alert("Login created. Now try to connect");
        })
        .fail(function(response){
            alert("Impossible to create login.");
        })
        .always(function() {

        });
}