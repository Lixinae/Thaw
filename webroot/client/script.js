
var currentChannel = "default";
var username = JSON.parse(sessionStorage.userName); //retrieve the login from the login page
var getListChannelsTimer;
var getListMessageTimer;
var getListUsersForChanTimer;


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
    getListChannelsTimer = setInterval(getListChannels,10000);// 10 000 for debug
    getListMessageTimer = setInterval(getListMessageForChannel,12000); // 10 000 for debug
    getListUsersForChanTimer = setInterval(getListUsersForChan,15000);
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
	        $('#newChannelText').val('');
            getListChannels();
        })
        .fail(function(response){
            if(newChannelName.length > 80){
                alert("Channel's name cannot exceed 80 characters");
            }
            alert("fail create channel");
        })
        .always(function() {

        });
}





// TODO
function deleteChannel(){
    $("li").click(function() {
        var curUser = $("#currentUser").val();
        var targetChannel = $(this).find('span').html();
        $.post("/api/private/deleteChannel",
            JSON.stringify({channelName:targetChannel,user:curUser}))
            .done(function(response){
                if(targetChannel == $("#currentChannel").html()){
                    $("#currentChannel").html("default");
                }
                getListChannels();
            })
            .fail(function(response){
                alert("fail delete channel");
            })
            .always(function() {

            });
        });
}

// Compatibilite pour IE si besoin
function getEventTarget(e) {
	e = e || window.event;
	return e.target || e.srcElement;
}

function selectChannel(){
	$("li").click(function() {
        var targetChannel = $(this).find('span').html();
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
				// To makes an on/off effect when loading
				var string = "<ul id=\"channels\">"
                $.each(response,function(key,val){
                    string = string +"<li><button onclick =\"selectChannel()\"> <span>"+val+"</span> </button> <button id=\"deleteButton\"onclick=\"deleteChannel();return false;\">Delete</button>"+"</li>";
//                    string = string +"<li><span>"+ val +"</span><button onclick=\"deleteChannel();return false;\"> delete </button>"+"</li>";
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
            window.clearInterval(getListUsersForChanTimer);
            window.location.href = "../index.html" ;
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

