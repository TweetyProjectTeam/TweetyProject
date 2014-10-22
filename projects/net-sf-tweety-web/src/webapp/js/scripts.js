/*
 * URLs of the TweetyServer
 */
var tweetyserver = "http://tweety.west.uni-koblenz.de:8080";//"http://localhost:8080";
var tweetyserverInc = tweetyserver + "/tweety/incmes";
var tweetyserverPing = tweetyserver + "/tweety/ping";

/*
 * Array of currently selected measures.
 */
var selectedMeasures = "";
/*
 * Checkboxes for measures.
 */
var checkSelectedMeasures;
/*
 * list of all measures.
 */
var allMeasures;
/*
 * list of measures which computations have been aborted.
 */
var abortedMeasures;
// counter for the number of retrieved results
// once this reaches the number of queried measures the compute button is
// enabled again
var cnt;


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
 * Click "Compute inconsistency values"
 */
function query(){
	document.all.computeButton.disabled = true;
	// reset aborted measures
	abortedMeasures = new Array();
	//TODO: adapt format
	format = "tweety";
	s = "<br/><table style=\"font-size:10pt;margin:5px;width:100%;margin-left:0px;\" cellPadding=\"1\" cellSpacing=\"0\">";
	s += "<th style=\"text-align:left;width:200px;\">Measure</th>";
	s += "<th style=\"text-align:left;\">Value</th>";
	s += "<th style=\"text-align:left;width:200px;\">Runtime</th>";
	s += "<th style=\"text-align:left;width:100px;\">Status</th></tr>"; 
	// counter for the number of retrieved results
	// once this reaches the number of queried measures the compute button is
	// enabled again
	cnt = 0;
	// for each selected measure initiate a corresponding request
	for(var i = 0; i < selectedMeasures.length; i++){
		measure = selectedMeasures[i];
		s += "<tr><td>";
		for(var j = 0; j < allMeasures.length; j++){
			if(measure == allMeasures[j].id){
				measureDetails = allMeasures[j];
				break;
			}
		}
		s += measureDetails.label + " (<a target=\"blank\" href=\"doc.html#"+ measureDetails.id +"\">?</a>)";
		s += "</td>";
		s += "<td id=\"val_"+measure+"\"><img width=\"15\" height=\"15\" src=\"img/loading.gif\"></img></td>";
		s += "<td id=\"rt_"+measure+"\"><img width=\"15\" height=\"15\" src=\"img/loading.gif\"></img></td>";
		s += "<td id=\"status_"+measure+"\" style=\"font-size:10pt;\"><img width=\"15\" height=\"15\" src=\"img/loading.gif\"></img>";
		s += "<a href=\"#\" onclick=\"abort(\'" + measure + "\');\"><img alt=\"abort\" title=\"abort\" width=\"15\" height=\"15\" style=\"padding-left:10px;\" border=\"0\" src=\"img/abort-icon.png\"></img></a></td>";
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
	  		url: tweetyserverInc,
	  		data: JSON.stringify(jsonObj),
	  		dataType: "json",
			success: function(response){ 
				// first check whether this has to be ignored due to abortion
				for(var j = 0; j < abortedMeasures.length; j++){
					if(abortedMeasures[j] == response.measure){
						return;
					}
				}	
				if(response.value >= 0){	  
					document.getElementById("val_"+response.measure).innerHTML = response.value;					
					document.getElementById("status_"+response.measure).innerHTML = "<img width=\"15\" height=\"15\" src=\"img/green_light.png\"></img>";
				}else if(response.value == -1){	  
					document.getElementById("val_"+response.measure).innerHTML = "-";
					document.getElementById("status_"+response.measure).innerHTML = "<span style=\"background: url(img/red_light15.png) left center no-repeat;width:100%,height:100%;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(timeout)</span>";
				}else{
					document.getElementById("val_"+response.measure).innerHTML = "-";
					document.getElementById("status_"+response.measure).innerHTML = "<span style=\"background: url(img/red_light15.png) left center no-repeat;width:100%,height:100%;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(unknown error)</span>";
				}
				document.getElementById("rt_"+response.measure).innerHTML = response.time + "ms";
				cnt++;
				if(cnt >= selectedMeasures.length)
					document.all.computeButton.disabled = false;
  			},
  			failure: function(response){
				cnt++;
				if(cnt >= selectedMeasures.length)
					document.all.computeButton.disabled = false;
  			}
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
  		url: tweetyserverInc,
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
function measureToHtml(measure,idx){	
	var result = "";
	if(idx % 3 == 1)
		result = "<tr>";
	result += "<td width=\"20\"><input type=\"checkbox\" ";
	// check whether measure is currently selected
	if(selectedMeasures != "")
		for(var i = 0; i < selectedMeasures.length; i++){
			if(selectedMeasures[i] == measure.id){
				result += " checked=\"checked\" ";
				break;
			}
		}
	result += "id=\"mes_" + measure.id + "\"/></td>";
	result += "<td>"+ measure.label +" (<a target=\"blank\" href=\"doc.html#"+ measure.id +"\">?</a>)</td>";
	if(idx % 3 == 0)
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
	var idx = 0;
	for(var i = 0; i < measures.measures.length; i++){
		idx++;
		s += measureToHtml(measures.measures[i],idx);
	}  	    
	if(idx % 3 != 0)
		s += "</tr>";         	
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
 * Check all measures in the "Select Inconsistency Measures" dialog
 */
function checkAll(){
	for(i = 0; i < checkSelectedMeasures.length; i++)
		document.getElementById("mes_"+checkSelectedMeasures[i]).checked = true;	
}

/*
 * Uncheck all measures in the "Select Inconsistency Measures" dialog
 */
function checkNone(){
	for(i = 0; i < checkSelectedMeasures.length; i++)
		document.getElementById("mes_"+checkSelectedMeasures[i]).checked = false;	
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
  		url: tweetyserverInc,
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

/*
 * Aborts computation of the given measure.
 */
function abort(measure){
	document.getElementById("val_"+measure).innerHTML = "-";
	document.getElementById("rt_"+measure).innerHTML = "-";
	document.getElementById("status_"+measure).innerHTML = "<span style=\"background: url(img/red_light15.png) left center no-repeat;width:100%,height:100%;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(aborted)</span>";
	abortedMeasures.push(measure);
	cnt++;
	if(cnt >= selectedMeasures.length)
		document.all.computeButton.disabled = false;
}

/*
 * Initializes the list of inconsistency measures on the documentation
 * page.
 */
function initDocPage(){
	checkServerStatus();
	// Retrieve measures
	$.ajax({
  		type: "POST",
  		contentType: "application/json; charset=utf-8",
  		url: tweetyserverInc,
  		data: JSON.stringify({
    	             "cmd" : "measures",
    	             "email" : "tweetyweb@mthimm.de"
   	           }),
  		dataType: "json",
  		success: function(response){
  					// cnt of measures
  					document.all.cnt_measures.innerHTML = response.measures.length;
  					// overview list
  					html = "";
  					for(var i = 0; i < response.measures.length; i++){
  						measure = response.measures[i];
						html += "<li><a href=\"#" + measure.id + "\">" + measure.label + "</a></li>";						
					}  	        
  					document.all.overview_list.innerHTML = html;
  					// detailed list
  					html = "";
  					for(var i = 0; i < response.measures.length; i++){
  						measure = response.measures[i];
						html += "<a name=\"" + measure.id + "\"></a>";
						html += "<h4>" + measure.label + "</h4>";
						html += measure.description;
						html += "<br/>";						
					}  	        
  					document.all.measureList.innerHTML = html;
					LatexIT.render("p",true);
  					//if the page has been opened with an anchor, jump to it
  					if(window.location.hash != '')
	  					window.location.href = window.location.href;  					
  				 },
  		failure: function(response){}
	}); 
}
