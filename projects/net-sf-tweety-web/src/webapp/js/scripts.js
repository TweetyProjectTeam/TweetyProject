/*
 * Click "Compute inconsistency values"
 */
function query(){
	document.all.loadingimage.style.visibility = "visible";
	$.ajax({
  		type: "POST",
  		contentType: "application/json; charset=utf-8",
  		url: "http://localhost:8080/tweety/incmes",
  		data: JSON.stringify({
    	             "cmd" : "info",
    	             "email" : "myemail",
    	             "param": "format",
    	             "entity": "tweety"
   	           }),
  		dataType: "json",
  		
  		success: function(response){ 	             	
			document.all.value.innerHTML = JSON.stringify(response);
			document.all.loadingimage.style.visibility = "hidden";
  		},
  		failure: function(response){
  		}
	});  			
}

/*
 * Open "Select Inconsistency Measures" dialog
 */
function select(){
	// add shadow div to page
	div = document.createElement("div");
	div.setAttribute('id', 'overlay');
	div.setAttribute('className', 'overlayBG');
	div.setAttribute('class', 'overlayBG');
	document.getElementsByTagName("body")[0].appendChild(div);
	// show lightbox
	document.all.lightBox.style.visibility = "visible";
	document.all.boxMeasures.style.display = "block";
	$.ajax({
  		type: "POST",
  		contentType: "application/json; charset=utf-8",
  		url: "http://localhost:8080/tweety/incmes",
  		data: JSON.stringify({
    	             "cmd" : "measures",
    	             "email" : "myemail"
   	           }),
  		dataType: "json",
  		success: function(response){
  			var s = "";
  			for(var i = 0; i < response.measures.length; i++){
  				s += response.measures[i].id + "<br/>";
  				s += response.measures[i].label + "<br/>";
  				s += response.measures[i].description + "<br/>";
  				s += "<br/>";
  			}  	             	
			document.all.boxMeasuresContent.innerHTML = s;
			document.all.boxLoading.style.display = "none";
			document.all.boxMeasures.style.display = "block";
  		},
  		failure: function(response){
  		}
	}); 
}

/*
 * Click "Apply" button on "Select Inconsistency Measures" dialog
 */
function applyMeasures(){
	// hide lightbox
	document.all.lightBox.style.visibility = "hidden";
	document.all.boxMeasures.style.display = "none";
	// remove shadow div
	document.getElementsByTagName("body")[0].removeChild(document.getElementById("overlay"));
}

/*
 * Open "Help on kb formats" dialog
 */
function formatinfo(){
	// add shadow div to page
	div = document.createElement("div");
	div.setAttribute('id', 'overlay');
	div.setAttribute('className', 'overlayBG');
	div.setAttribute('class', 'overlayBG');
	document.getElementsByTagName("body")[0].appendChild(div);
	// show lightbox
	document.all.lightBox.style.visibility = "visible";
	document.all.boxLoading.style.display = "block";
	$.ajax({
  		type: "POST",
  		contentType: "application/json; charset=utf-8",
  		url: "http://localhost:8080/tweety/incmes",
  		data: JSON.stringify({
    	             "cmd" : "formats",
    	             "email" : "myemail"
   	           }),
  		dataType: "json",
  		success: function(response){ 	             	
	  		var s = "";
  			for(var i = 0; i < response.formats.length; i++){
  				s += response.formats[i].id + "<br/>";
  				s += response.formats[i].label + "<br/>";
  				s += response.formats[i].description + "<br/>";
  				s += "<br/>";
  			}  	             	
			document.all.boxFormatsContent.innerHTML = s;
			document.all.boxLoading.style.display = "none";
			document.all.boxFormats.style.display = "block";
  		},
  		failure: function(response){
  		}
	});  
}

/*
 * Click "Close" on "Help on kb formats" dialog
 */
function closeFormats(){
	// hide lightbox
	document.all.lightBox.style.visibility = "hidden";
	document.all.boxFormats.style.display = "none";
	// remove shadow div
	document.getElementsByTagName("body")[0].removeChild(document.getElementById("overlay"));
}
