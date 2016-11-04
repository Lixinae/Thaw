
// Ces fonctions sont la uniquement pour tester des truc
// Elle ne seront pas dans le rendu final
// TEST FUNCTIONS 
	// IE does not know about the target attribute. It looks for srcElement
	// This function will get the event target in a browser-compatible way
	
	function getEventTarget(e) {
		e = e || window.event;
		return e.target || e.srcElement; 
	}
	
	// Recupère la valeur de l'element de la liste sur lequel on clique
	// -> Pour change de chan efficacement
	function selectChannel(){
		var ul = document.getElementById('channels');
		ul.onclick = function(event) {
			var target = getEventTarget(event);
			//alert(target.innerHTML);
			currentChannel=target.innerHTML;
			alert(currentChannel)
		};
	}
	
		// Très pratique
// https://github.com/cescoffier/my-vertx-first-app/blob/post-3/src/main/resources/assets/index.html	
//https://github.com/cescoffier/my-vertx-first-app/blob/post-3/src/main/java/io/vertx/blog/first/MyFirstVerticle.java
	function testAjax() {
		var username ="testify"
		$.post("/test/"+username, JSON.stringify({username: username}), function () {
				//load();
				// Do stuff
				alert("bla")
		},"json");
	}
	
	