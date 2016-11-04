

//////// TOUT FAIRE EN AJAX ////////////

var currentChannel = ""
var numberChannels = 0
var numberUsers = 0
var username = "blork"

// L'utilisateur saisie un message
// Envoie le message au serveur qui l'ajoutera à la base de donné du channel
function sendMessage(){
	var messageV= $("#textEntry");
	
	$.post("/sendMessage",JSON.stringify({channel : currentChannel, message : messageV.val(),username : username}),
			function(){
				
//				element.append(data);
			},"json")
}

// Permet de creer un channel
// Et envoie l'information au serveur
function createChannel(){
	
	$.post("/createChannel",
		function(data){
			
		}
}

function getListUsers(){
	
	
	
}

// TODO A changer !
function getListChannels(){
	var listChannel = document.getElementById('listChannels')
	listChannel.children().remove();
	
	//var delimiter = "/"
	// format : $.get(nomRequete,fonction a appliquer)
	/*$.get("/getListChannels",function(data){
			// Mettre un delimiteur entre chaque nom de channel pour split
			// Exemple :
			//   MonSuperChannel / UnAutreChannel / EncoreUn 
			var tab = data.split(delimiter)
			tab.foreach(printElements)
			for(val in tab){
				
			}
		}
	)*/
	// TODO verifier fonctionnement
	
	// Va creer une liste cliquable avec chacun des channel
	listChannel.append("<ul id=\"channels\" onclick=\"selectChannel()\">");
	$.getJSON("/getListChannels",function(data){
		$.each(data,function(val){
			listChannel.append("<li> "+ val+" </li>");
		}
	}
	listChannel.append("</ul>");
}

// TODO A changer !
function getListUsersFromChan(){
	var usersListOnChan = document.getElementById('listUsers');
	usersListOnChan.children().remove();
	
	usersListOnChan.append("<ul id=\"usersOnChan\">");
	$.getJSON("/getListUsersForChanChannels/"+currentChannel,function(data){
		$.each(data,function(val){
			usersListOnChan.append("<li> "+ val+" </li>");
		}
	}
	usersListOnChan.append("</ul>");
}

// Fonctionne
function selectChannel(){
	var ul = document.getElementById('channels');
	ul.onclick = function(event) {
		var target = getEventTarget(event);
		//alert(target.innerHTML);
			currentChannel=target.innerHTML;
			alert(currentChannel)
	};
	
}
