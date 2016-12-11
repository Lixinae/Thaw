






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
    // Intervals will be much lower once live
    setInterval(getListChannels,10000);// 10 000 for debug
    setInterval(getListMessageForChannel,10000); // 10 000 for debug
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


// Permet de creer un channel
// Et envoie l'information au serveur
// TODO
function createChannel(){

	$.post("/api/private/createChannel",
	    JSON.stringify({channelName:newChannelName,creatorName:username}))
	    .done(function(response){
            alert("success create channel");
        })
        .fail(function(response){
            alert("fail create channel");
        })
        .always(function() {

        });
}

// TODO
function deleteChannel(){

	$.post("/api/private/deleteChannel",
	    JSON.stringify({channelName:target,userName:username}))
	    .done(function(response){
            alert("success delete channel");
        })
        .fail(function(response){
            alert("fail delete channel");
        })
        .always(function() {

        });
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
        $.post("/api/private/connectToChannel",
            JSON.stringify({channelName : currentChannel,userName : username,oldChannelName : oldChannel}))
            .done(function (response){
                getListUsersForChan(currentChannel);
            })
            .fail(function(response){
                alert("fail connectToChannel");
            })
            .always(function() {

            });
	});
}

// L'utilisateur saisie un message
// Envoie le message au serveur qui l'ajoutera à la base de donné du channel
function sendMessage(){
    var  messageV = $('textArea#TextZone');
    $.post("/api/private/sendMessage",
	    JSON.stringify({channel : currentChannel, message : messageV.val(),username : username}))
	    .done(function(response){
            alert("success send message");
            messageV.val("");
	    })
	    .fail(function(response){
            alert("fail send message");
		})
		.always(function(){

        });

}

// TODO : Test it
function getListMessageForChannel(){
	var listChannel = $(".listChannels");
	listChannel.children().remove();

	$.post("/api/private/getListMessageForChannel",
		JSON.stringify({channelName:currentChannel,numberOfMessage:1000}))
	    .done(function(response){
	        // Formater correctement les messages

	        // Utiliser JSON.parse
	        // Format reçu : l'ordre des éléments change systématiquement
	        // L'exemple est la à titre indicatif
	        /* [{
                     'sender': {
                                'userHuman': True,
                                'name': 'superUser',
                                'userBot': False
                               },
                     'content': 'Message 2',
                     'date': 1480814172039
	            },
	            {
	                 'sender': {
                	            'userHuman': True,
                	            'name': 'superUser',
                	            'userBot': False
                	            },
                	 'content': 'Message 2',
                	 'date': 1480814172039
	            }]
	             */
            //alert("success getListMessageForChannel");
        })
        .fail(function(response){
            //alert("fail getListMessageForChannel");
        })
        .always(function() {

        });
}

// TODO : Test it
function getListChannels(){
	var listChannel = $(".listChannels");
	listChannel.children().remove();

    //listChannel.load("index.html .listChannels");
	$.get("/api/private/getListChannel")
	        .done(function(response){
				// Provoque un effet "On/Off" au chargement
				var string = "<h2>Channels</h2>"+"<ul id=\"channels\" onclick=\"selectChannel()\">"
                $.each(response,function(key,val){
                    string = string +"<li> "+ val+" </li>";
                });
                string = string +"</ul>";
                alert(string);
                listChannel.append(string);

            })
            .fail(function(response){
                //alert("fail getListChannels");
            })
            .always(function() {

            });

}


// Fonctionne
function getListUsersForChan(currentChannel){
	var usersListOnChan = $(".listUsers");
	usersListOnChan.children().remove();


	$.post("/api/private/getListUserForChannel",
	    JSON.stringify({channelName : currentChannel}))
	    .done(function(response){
            alert(response);
            var string ="<h2>Users</h2>"+"<ul id=\"usersOnChan\">";
            $.each(response,function(key,val){
                string = string +"<li> "+ val+" </li>";
            });
            string = string + "</ul>";
            usersListOnChan.append(string);
        })
        .fail(function(response){
            //alert("fail getListUsersForChan");
        })
        .always(function() {

        });
}

function disconnectFromServer(){
		$.post("/api/private/disconnectFromServer",
	    JSON.stringify({userName : userName}))
	    .done(function(response){

        })
        .fail(function(response){
            //alert("fail getListUsersForChan");
        })
        .always(function() {

        });
	
}

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

// Compatibilite pour IE si besoin
function getEventTarget(e) {
	e = e || window.event;
	return e.target || e.srcElement;
}