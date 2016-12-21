
$(document).ready(function(){

		});


function connectToServer(userName,password){

    var userName = $('#userNameEntry').val();
    var password = $('#userPasswordEntry').val();

	$.post("/api/connectToServer",
	    JSON.stringify({userName:userName,password:password}))
	    .done(function(response){
            sessionStorage.userName = JSON.stringify(userName);//to memorize login through session
			window.location.href = "./client/client.html";
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
    var patt = new RegExp("^[a-zA-Z][\\w]+$")//pattern that will be use by the server,
	$.post("/api/createAccount",
	    JSON.stringify({userName:userName,password:password}))
	    .done(function(response){
            alert("Login created. Now try to connect");
        })
        .fail(function(response){
        if(! patt.test(userName)){
        alert("username must contains only alphanumeric characters must begin with a letter and contains at least 2 characters);
        }
            alert("Impossible to create login.");
        })
        .always(function() {
        });
}