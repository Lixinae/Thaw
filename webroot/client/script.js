
var currentChannel = "default";
var username = JSON.parse(sessionStorage.userName); //retrieve the login from the login page
var getListChannelsTimer;
var getListMessageTimer;



$(document).ready(function(){
			$("#currentUser").html(username);
			$("#currentUser").val(username);//we stock the login of the user into a field
			$("#currentChannel").html(currentChannel);
			initialize();
			setReloadInterval();
		});

function initialize(){
	textAreaDefaultValueDisappearOnClick();
	getListChannels();
	getListMessageForChannel();
	getListUsersForChan();
}

function setReloadInterval(){
    // Intervals will be much lower once live
    getListChannelsTimer = setInterval(getListChannels,2000);// 10 000 for debug
    getListMessageTimer = setInterval(getListMessageForChannel,500); // 10 000 for debug
}

/*Allow to get default message in a textarea that will disappear when we click in the zone*/
function textAreaDefaultValueDisappearOnClick(){
	$('textArea')
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




function addChannel(){
    var newChannelName = $('#newChannelText').val();
	$.post("/api/private/addChannel",
	    JSON.stringify({newChannelName:newChannelName,creatorName:username}))
	    .done(function(response){
	        $('#newChannelText').val('')
            getListChannel()
        })
        .fail(function(response){
            alert("fail create channel");
        })
        .always(function() {

        });
}





// TODO
function deleteChannel(){
    //target =
    var target = "";
	$.post("/api/private/deleteChannel",
	    JSON.stringify({channelName:target,user:username}))
	    .done(function(response){
            alert("success delete channel");
        })
        .fail(function(response){
            alert("fail delete channel");
        })
        .always(function() {

        });
}


//TODO faire fonctionner selectChannel() une fois que l'on aura ajoute des images pour suppr un channel
function selectChannel(){
	var ul = $("#channels");
	ul.click(function(event) {
		var target = getEventTarget(event);
        var targetChannel = target.innerHTML;
        var oldChannel = $("#currentChannel").html();
        var curUser = $("#currentUser").val();
        $.post("/api/private/connectToChannel",
            JSON.stringify({channelName : targetChannel,userName : curUser,oldChannelName : oldChannel}))
            .done(function (response){
                $("#currentChannel").html(targetChannel);
                getListUsersForChan();
                getListMessageForChannel();
            })
            .fail(function(response){

            })
            .always(function() {

            });
	});
}


function sendMessage(){
    var  messageV = $('textArea#TextZone');
    var currentChannel = $("#currentChannel").html();
    var curUser = $("#currentUser").val();//we retrieve the current user of the application that has been stock before
    $.post("/api/private/sendMessage",
	    JSON.stringify({channelName : currentChannel, message : messageV.val(),username : curUser}))
	    .done(function(response){
            messageV.val("");
            getListMessageForChannel();
	    })
	    .fail(function(response){

		})
		.always(function(){

        });
}

function getListMessageForChannel(){
	var listMessage = $(".tchatIntern");
	var currentChannel = $("#currentChannel").html();

	$.post("/api/private/getListMessageForChannel",
		JSON.stringify({channelName:currentChannel,numberOfMessage:1000}))
	    .done(function(response){
				listMessage.children().remove();
	            var string = "";
	            $.each(response,function(key){
	                var date = response[key].date;
	            	var msg = response[key].content;
	            	var name = response[key].sender.name;
                    string = string + chatMessageFormatting(name,msg,date);
	            });
	            listMessage.append(string);
        })
        .fail(function(response){

        })
        .always(function() {

        });
}


function getListChannels(){
	var listChannel = $(".listChannelsIntern");

	$.get("/api/private/getListChannel")
	        .done(function(response){
				listChannel.children().remove();
				// Provoque un effet "On/Off" au chargement
				var string = "<ul id=\"channels\" onclick=\"selectChannel()\">"
                $.each(response,function(key,val){
                    string = string +"<li>"+ val +"</li>";
                });
                string = string + "</ul>";
                listChannel.append(string);

            })
            .fail(function(response){

            })
            .always(function() {

            });

}


// Fonctionne
function getListUsersForChan(){
	var usersListOnChan = $(".listUsersIntern");
    var currentChannel = $("#currentChannel").html();

	$.post("/api/private/getListUserForChannel",
	    JSON.stringify({channelName : currentChannel}))
	    .done(function(response){
			usersListOnChan.children().remove();
            var string ="<ul id=\"usersOnChan\">";
            $.each(response,function(key,val){
                string = string +"<li>"+ val+"</li>";
            });
            string = string + "</ul>";
            usersListOnChan.append(string);
        })
        .fail(function(response){

        })
        .always(function() {

        });
}

function disconnectFromServer(){
        var currentChannel = $("#currentChannel").html();
        var curUser = $("#currentUser").val();
		$.post("/api/private/disconnectFromServer",
	    JSON.stringify({userName : curUser, currentChannelName:currentChannel}))
	    .done(function(response){
	    //clear all the timer when logout
	    window.clearInterval(getListChannelsTimer);
	    window.clearInterval(getListMessageTimer);
        window.location.href = "../index.html";
        })
        .fail(function(response){

        })
        .always(function() {

        });
	
}

//////// FONCTION OUTILS ALLEGEMENT CODE////////////

/*Fonction formattant une chaine de message en rajoutant
les informations de date et les balises HTML necessaires au
bon formattage.

Format actuel :
<p>hh:mm DD/MM/YYYY username : <br> monMessage <br>

le split permet de recuperer les sauts de lignes et
y inserer les balises html adequat pour le formattage
*/
function chatMessageFormatting(username,msg,dateAsLong){
    var date = new Date(dateAsLong);
    var hours = addZero(date.getHours());
    var minutes = addZero(date.getMinutes());
    var day = addZero(date.getDate());
    var month = correctMonth(date);
    var year = date.getFullYear();
   return "<p>"+ hours+":"+minutes +" "+day+"/"+month+"/"+year+" "+username+" : "
                                   +"<br><br>"+msg.split("\n").join("<br>")+"</p>"+"<br>";
}

function addZero(i){
    if(i < 10){
        i = "0"+i;
    }
    return i;
}

function correctMonth(date){
    return addZero(date.getMonth()+1);
}

// Compatibilite pour IE si besoin
function getEventTarget(e) {
	e = e || window.event;
	return e.target || e.srcElement;
}