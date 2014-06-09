/*
 * Array of currently selected measures.
 */
var selectedMeasures;
/*
 * Checkboxes for measures.
 */
var checkSelectedMeasures;
/*
 * list of all measures.
 */
var allMeasures;

/*
 * Click "Compute inconsistency values"
 */
function query(){
	document.all.computeButton.disabled = false;
	//TODO: adapt format
	format = "tweety";
	s = "<table style=\"font-size:10pt;margin:5px;\">";
	// for each selected measure initiate a corresponding request
	for(var i = 0; i < selectedMeasures.length; i++){
		measure = selectedMeasures[i];
		s += "<tr><td>"
		s += measure;
		s += "</td>";
		s += "<td id=\"res_"+measure+"\"><img width=\"20\" height=\"20\" src=\"img/loading.gif\"></img></td>";
		s += "</td></tr>";
		var jsonObj = {};
		jsonObj.cmd = "value";
		jsonObj.email = "tweetyweb@mthimm.de",
		jsonObj.measure = measure,
	   	jsonObj.kb = $('#kb').val(),
	    jsonObj.format = format;
		$.ajax({
	  		type: "POST",
	  		contentType: "application/json; charset=utf-8",
	  		url: "http://localhost:8080/tweety/incmes",
	  		data: JSON.stringify(jsonObj),
	  		dataType: "json",
			success: function(response){ 	  
				document.getElementById("res_"+response.measure).innerHTML = response.value;
  			},
  			failure: function(response){}
		});
	}
	s += "</table>";
	document.all.results.innerHTML = s;
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
    	             "email" : "tweetyweb@mthimm.de"
   	           }),
  		dataType: "json",
  		success: function(response){populateMeasures(response);},
  		failure: function(response){}
	}); 
}

/*
 * Generates the HTML code for a measure
 * in the measures overview.
 */
function measureToHtml(measure){
	var result = "<tr>";
	result += "<td><input type=\"checkbox\" id=\"mes_" + measure.id + "\"/></td>";
	result += "<td>"+ measure.label +"</td>";
	result += "<td><a target=\"blank\" href=\"doc.html#"+ measure.id +"\">doc</a></td>";
	result += "</tr>";
	checkSelectedMeasures.push(measure.id);
	return result;
}

/*
 * Populate the measures div with the given measures.
 */
function populateMeasures(measures){
	checkSelectedMeasures = Array();
	allMeasures = measures.measures;
	var s = "<table width=\"100%\" style=\"font-size:10pt;\">";
	for(var i = 0; i < measures.measures.length; i++){
		s += measureToHtml(measures.measures[i]);
	}  	             	
	s += "</table>";
	document.all.boxMeasuresContent.innerHTML = s;
	document.all.boxLoading.style.display = "none";
	document.all.boxMeasures.style.display = "block";
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
	// check checkboxes
	selectedMeasures = new Array();
	for(i = 0; i < checkSelectedMeasures.length; i++){
		if(document.getElementById("mes_"+checkSelectedMeasures[i]).checked == true)
			selectedMeasures.push(checkSelectedMeasures[i]);
	}
	// populate info on main page
	s = "";
	isFirst = true;
	for(var i = 0; i < allMeasures.length; i++){
		for(var j = 0; j < selectedMeasures.length; j++)
			if(allMeasures[i].id == selectedMeasures[j]){
				if(isFirst) isFirst = false;
				else s += ", ";
				s += allMeasures[i].label;
			}
	}
	document.all.selectedmeasures.innerHTML = s;
	if(s != "")
		document.all.computeButton.disabled = false;
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
    	             "email" : "tweetyweb@mthimm.de"
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
