/*
 * URLs of the TweetyServer
 */
var tweetyserver = "http://localhost:8080";//"http://tweety.west.uni-koblenz.de:8080";
var tweetyserverDelp = tweetyserver + "/tweety/delp";
var tweetyserverPing = tweetyserver + "/tweety/ping";

function checkServerStatus(){
    var jsonObj = {};
	$.ajax({
	  	type: "POST",
		contentType: "application/json; charset=utf-8",
		url: tweetyserverPing,
		data: JSON.stringify(jsonObj),
		dataType: "json",
		timeout:1000,
		success: function(response){ 
			document.getElementById("server_status").innerHTML = "<img style=\"vertical-align:text-top;\" width=\"15\" height=\"15\" src=\"img/green_light.png\"></img>";
  		},
		failure: function(response){
  			document.getElementById("server_status").innerHTML = "<img style=\"vertical-align:text-top;\" width=\"15\" height=\"15\" src=\"img/red_light.png\"></img>";
  			document.getElementById("server_message").innerHTML = "The Tweety Server is currently under maintenance, please come back later.";
		},
		error: function(response){
  			document.getElementById("server_status").innerHTML = "<img style=\"vertical-align:text-top;\" width=\"15\" height=\"15\" src=\"img/red_light.png\"></img>";
  			document.getElementById("server_message").innerHTML = "The Tweety Server is currently under maintenance, please come back later.";
 		}  		
	});
}

/*
 * Click "submit"
 */
function query(){
	format = "tweety";
	s = "<p style=\"font-size:8pt;\">The answer is: <span id=\"answer\"><img width=\"15\" height=\"15\" src=\"img/loading.gif\"></img></span></p>";
	var jsonObj = {};
	jsonObj.cmd = "query";
	jsonObj.email = "tweetyweb@mthimm.de",
	jsonObj.compcriterion = "genspec",
	jsonObj.kb = $('#kb').val(),
	jsonObj.query = $('#query').val(),
	$.ajax({
		type: "POST",
	 	contentType: "application/json; charset=utf-8",
	  	url: tweetyserverDelp,
	  	data: JSON.stringify(jsonObj),
	  	dataType: "json",
		success: function(response){ 
				document.getElementById("answer").innerHTML = response.answer;			
  		},
  		failure: function(response){		
				document.getElementById("answer").innerHTML = "ERROR";
		}
	});	
	document.all.results.innerHTML = s;
}
