//////// FONCTION OUTILS ALLEGEMENT CODE////////////

/*Fonction formattant une chaine de message en rajoutant
les informations de date et les balises HTML necessaires au
bon formattage.

Format actuel :
<p>hh:mm:ss [username] : <br> monMessage <br>

le split permet de recuperer les sauts de lignes et
y inserer les balises html adequat pour le formattage
*/
function chatMessageFormatting(username,msg,dateAsLong){
    var date = new Date(dateAsLong);
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var seconds = date.getSeconds();
    if(msg.length > 512){
       msg=msg.slice(0,512)
    }
    return "<p>"+ hours+":"+minutes+ ":"+ seconds+" ["+username+"] : "
                                   +"<br>"+msg.split("\n").join("<br>")+"</p>"+"<br>";
}





//////// TOUT FAIRE EN AJAX ////////////

var currentChannel = "default";
var username = "blork";

// var  messageV = $("#TextZone"); -> Textzone est un id
// var  messageV = $(".TextZone"); -> TextZone est une classe


$(document).ready(function(){
			$("#currentUser").html(username);
			$("#currentChannel").html(currentChannel);
			initialize();
			setReloadInterval();
		});

function initialize(){
	textAreaDefaultValueDisappearOnClick();
	getListChannels();
	getListMessageForChannel();
}

function setReloadInterval(){

    setInterval(getListChannels,500);
    setInterval(getListMessageForChannel,600);
}

/* Permet d'avoir un texte par defaut dans la zone de d'ecriture de message et qui disparait lors du clique sur la zone */
function textAreaDefaultValueDisappearOnClick(){
	$('textArea#TextZone')
    .focus(function() {
        if (this.value === this.defaultValue) {
            this.value = '';
        }
	})
	.blur(function() {
        if (this.value === '') {
            this.value = this.defaultValue;
        }
	});
}

// L'utilisateur saisie un message
// Envoie le message au serveur qui l'ajoutera à la base de donné du channel
function sendMessage(){
    var  messageV = $('textArea#TextZone');
    //TODO : a jeter alert(messageV.val()); //pour verifier que j'ai bien recuperer la bonne chaine de caractere
	$.post("/api/sendMessage",
	    JSON.stringify({channel : currentChannel, message : messageV.val(),username : username}))
	    .done(function(response){
            alert("sucess send message");
            messageV.val("");
	    })
	    .fail(function(response){
            alert("fail send message");
		})
		.always(function() {
            alert( "finished" );
        });

}

// Permet de creer un channel
// Et envoie l'information au serveur
// TODO
function createChannel(){

	$.post("/api/createChannel",
	    JSON.stringify({channelName:newChannelName,userName:username}))
	    .done(function(response){
            alert("sucess send message");
        })
        .fail(function(response){
            alert("fail send message");
        })
        .always(function() {
            alert( "finished" );
        });
}

// TODO
function deleteChannel(){

	$.post("/api/deleteChannel",
	    JSON.stringify({channelName:target,userName:username}))
	    .done(function(response){
            alert("sucess send message");
        })
        .fail(function(response){
            alert("fail send message");
        })
        .always(function() {
            alert( "finished" );
        });
}

// TODO
function getListMessageForChannel(){
	var listChannel = $(".listChannels");
	listChannel.children().remove();

	$.post("/api/getListMessageForChannel",
			JSON.stringify({channelName:currentChannel})
	    .done(function(response){
            alert("sucess send message");
        })
        .fail(function(response){
            alert("fail send message");
        })
        .always(function() {
            alert( "finished" );
        });
}


// TODO A changer !
function getListChannels(){
	var listChannel = $(".listChannels");
	listChannel.children().remove();

	// Va creer une liste cliquable avec chacun des channel
	listChannel.append("<h2>List of Channels</h2>");
	listChannel.append("<ul id=\"channels\" onclick=\"selectChannel()\">");
	$.get("/api/getListChannels")
	        .done(function(response){
                $.each(response,function(key,val){
                    listChannel.append("<li> "+ val+" </li>");
                });
            })
            .fail(function(response){
                alert("fail send message");
            })
            .always(function() {
                alert( "finished" );
            });
        ;
	listChannel.append("</ul>");
}


// Compatibilite pour IE si besoin
function getEventTarget(e) {
	e = e || window.event;
	return e.target || e.srcElement;
}

// Fonctionne
function selectChannel(){
	var ul = $("#channels");
	ul.click(function(event) {
		var target = getEventTarget(event);
		var tmpChannel = target.innerHTML;
		var oldChannel = currentChannel;
        if (!(tmpChannel == currentChannel)){
            currentChannel=target.innerHTML;
        }
        else if (currentChannel == ""){
            currentChannel=target.innerHTML;
        }
        else{
            return
        }
		$("#currentChannel").html(currentChannel);
        $.post("/api/connectToChannel",
            JSON.stringify({channelName : currentChannel,userName : username,oldChannelName : oldChannel}))
            .done(function (response){
                getListUsersFromChan(currentChannel);
            })
            .fail(function(response){
                alert("fail send message");
            })
            .always(function() {
                alert( "finished" );
            });
	});
}

// Fonctionne
function getListUsersFromChan(currentChannel){
	var usersListOnChan = $(".listUsers");
	usersListOnChan.children().remove();

    usersListOnChan.append("<h2>ListUsers </h2>");
	usersListOnChan.append("<ul id=\"usersOnChan\">");
	$.post("/api/getListUserForChannel",
	    JSON.stringify({channelName : currentChannel}))
	    .done(function(response){
            alert(response);
            $.each(response,function(key,val){
                usersListOnChan.append("<li> "+ val+" </li>");
            });
        })
        .fail(function(response){
            alert("fail send message");
        })
        .always(function() {
            alert( "finished" );
        });
    );
	usersListOnChan.append("</ul>");
}

//// TODO -> Test uniquement
//function testAjax() {
//		var username ="testify"
//		$.get("/api/test/"+username, JSON.stringify({username: username}), function () {
//				//load();
//				// Do stuff
//				alert("bla")
//		},"json");
//	}
//
//function testAjax3() {
//  	alert("a");
//    $.post("/api/testJson/", JSON.stringify({currentChannelName: currentChannel}), function () {
//   	    //load();
//   		// Do stuff
//   	    alert("bla")
//    },"json");
//}