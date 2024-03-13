/*
 * URLs of the TweetyServer
 */
var tweetyserver = "http://localhost:8080";
var tweetyserverInc = tweetyserver + "/aba";
var tweetyserverPing = tweetyserver + "/ping";

/*
 * Array of currently selected measures.
 */
var selectedSemantics = "";
/*
 * Checkboxes for measures.
 */
var checkSelectedSemantics;
/*
 * list of all measures.
 */
var allSemantics;
/*
 * list of measures which computations have been aborted.
 */
var abortedSemantics;
// counter for the number of retrieved results
// once this reaches the number of queried measures the compute button is
// enabled again
var cnt;


function checkServerStatus(){
    var jsonObj = {};
	jsonObj.id = 42;
	jsonObj.content = "PING SERVER"
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
	abortedSemantics = new Array();
	//TODO: adapt format
	format = "tweety";
	s = "<br/><table style=\"font-size:10pt;margin:5px;width:100%;margin-left:0px;\" cellPadding=\"1\" cellSpacing=\"0\">";
	s += "<th style=\"text-align:left;width:200px;\">Semantics</th>";
	s += "<th style=\"text-align:left;\">Models</th>";
	s += "<th style=\"text-align:left;width:200px;\">Runtime</th>";
	s += "<th style=\"text-align:left;width:100px;\">Status</th></tr>";
	// counter for the number of retrieved results
	// once this reaches the number of queried measures the compute button is
	// enabled again
	cnt = 0;
	// for each selected measure initiate a corresponding request
	for(var i = 0; i < selectedSemantics.length; i++){
		measure = selectedSemantics[i];
		s += "<tr><td>";
		for(var j = 0; j < allSemantics.length; j++){
			if(measure == allSemantics[j].id){
				measureDetails = allSemantics[j];
				break;
			}
		}
		s += measureDetails.label + " (<a target=\"blank\" href=\"incmes_doc_v1.pdf\">?</a>)";
		s += "</td>";
		s += "<td id=\"val_"+measure+"\"><img width=\"15\" height=\"15\" src=\"img/loading.gif\"></img></td>";
		s += "<td id=\"rt_"+measure+"\"><img width=\"15\" height=\"15\" src=\"img/loading.gif\"></img></td>";
		s += "<td id=\"status_"+measure+"\" style=\"font-size:10pt;\"><img width=\"15\" height=\"15\" src=\"img/loading.gif\"></img>";
		s += "<a href=\"#\" onclick=\"abort(\'" + measure + "\');\"><img alt=\"abort\" title=\"abort\" width=\"15\" height=\"15\" style=\"padding-left:10px;\" border=\"0\" src=\"img/abort-icon.png\"></img></a></td>";
		s += "</td></tr>";
		var jsonObj = {};
		jsonObj.cmd = "get_models";
		jsonObj.email = "tweetyweb@mthimm.de",
		jsonObj.semantics = measure,
	   	jsonObj.kb = $('#kb').val(),
	    jsonObj.kb_format = 'pl';
		jsonObj.fol_signature = '';
		jsonObj.timeout = 600;
		jsonObj.unit_timeout = "ms";
		$.ajax({
	  		type: "POST",
	  		contentType: "application/json; charset=utf-8",
	  		url: tweetyserverInc,
	  		data: JSON.stringify(jsonObj),
	  		dataType: "json",
			success: function(response){
				// first check whether this has to be ignored due to abortion
				for(var j = 0; j < abortedSemantics.length; j++){
					if(abortedSemantics[j] == response.semantics){
						return;
					}
				}
				//document.getElementById("val_"+response.semantics).innerHTML = response.answer;
				if(response.status == 'SUCCESS'){
					document.getElementById("val_"+response.semantics).innerHTML = response.answer;
					document.getElementById("status_"+response.semantics).innerHTML = "<img width=\"15\" height=\"15\" src=\"img/green_light.png\"></img>";
				}
			else if(response.status == 'TIMEOUT'){
					document.getElementById("val_"+response.semantics).innerHTML = "-";
					document.getElementById("status_"+response.semantics).innerHTML = "<span style=\"background: url(img/red_light15.png) left center no-repeat;width:100%,height:100%;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(timeout)</span>";
				}else{
					document.getElementById("val_"+response.semantics).innerHTML = "-";
					document.getElementById("status_"+response.semantics).innerHTML = "<span style=\"background: url(img/red_light15.png) left center no-repeat;width:100%,height:100%;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(unknown error)</span>";
				}
				document.getElementById("rt_"+response.semantics).innerHTML = response.time + "ms";
				cnt++;
				if(cnt >= selectedSemantics.length)
					document.all.computeButton.disabled = false;
  			},
  			failure: function(response){
				cnt++;
				if(cnt >= selectedSemantics.length)
					document.all.computeButton.disabled = false;
  			}
		});
	}
	s += "</table>";
	document.all.results.innerHTML = s;
}


function query_assumption(){
	document.all.computeButton.disabled = true;
	// reset aborted measures
	abortedSemantics = new Array();
	//TODO: adapt format
	format = "tweety";
	s = "<br/><table style=\"font-size:10pt;margin:5px;width:100%;margin-left:0px;\" cellPadding=\"1\" cellSpacing=\"0\">";
	s += "<th style=\"text-align:left;width:200px;\">Semantics</th>";
	s += "<th style=\"text-align:left;\">Result</th>";
	s += "<th style=\"text-align:left;width:200px;\">Runtime</th>";
	s += "<th style=\"text-align:left;width:100px;\">Status</th></tr>";
	// counter for the number of retrieved results
	// once this reaches the number of queried measures the compute button is
	// enabled again
	cnt = 0;
	// for each selected measure initiate a corresponding request
	for(var i = 0; i < selectedSemantics.length; i++){
		measure = selectedSemantics[i];
		s += "<tr><td>";
		for(var j = 0; j < allSemantics.length; j++){
			if(measure == allSemantics[j].id){
				measureDetails = allSemantics[j];
				break;
			}
		}
		s += measureDetails.label + " (<a target=\"blank\" href=\"incmes_doc_v1.pdf\">?</a>)";
		s += "</td>";
		s += "<td id=\"qval_"+measure+"\"><img width=\"15\" height=\"15\" src=\"img/loading.gif\"></img></td>";
		s += "<td id=\"qrt_"+measure+"\"><img width=\"15\" height=\"15\" src=\"img/loading.gif\"></img></td>";
		s += "<td id=\"qstatus_"+measure+"\" style=\"font-size:10pt;\"><img width=\"15\" height=\"15\" src=\"img/loading.gif\"></img>";
		s += "<a href=\"#\" onclick=\"abort(\'" + measure + "\');\"><img alt=\"abort\" title=\"abort\" width=\"15\" height=\"15\" style=\"padding-left:10px;\" border=\"0\" src=\"img/abort-icon.png\"></img></a></td>";
		s += "</td></tr>";
		var jsonObj = {};

		jsonObj.cmd = "query";
		jsonObj.email = "tweetyweb@mthimm.de",
		jsonObj.semantics = measure,
	   	jsonObj.kb = $('#kb').val(),
	    jsonObj.kb_format = 'pl';
		jsonObj.fol_signature = '';
		jsonObj.timeout = 600;
		jsonObj.unit_timeout = "ms";
		jsonObj.query_assumption =  $('#aquery').val()

		$.ajax({
	  		type: "POST",
	  		contentType: "application/json; charset=utf-8",
	  		url: tweetyserverInc,
	  		data: JSON.stringify(jsonObj),
	  		dataType: "json",
			success: function(response){
				// first check whether this has to be ignored due to abortion
				for(var j = 0; j < abortedSemantics.length; j++){
					if(abortedSemantics[j] == response.semantics){
						return;
					}
				}
				//document.getElementById("val_"+response.semantics).innerHTML = response.answer;
				if(response.status == 'SUCCESS'){
					document.getElementById("qval_"+response.semantics).innerHTML = response.answer;
					document.getElementById("qstatus_"+response.semantics).innerHTML = "<img width=\"15\" height=\"15\" src=\"img/green_light.png\"></img>";
				}
			else if(response.status == 'TIMEOUT'){
					document.getElementById("qval_"+response.semantics).innerHTML = "-";
					document.getElementById("qstatus_"+response.semantics).innerHTML = "<span style=\"background: url(img/red_light15.png) left center no-repeat;width:100%,height:100%;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(timeout)</span>";
				}else{
					document.getElementById("qval_"+response.semantics).innerHTML = "-";
					document.getElementById("qstatus_"+response.semantics).innerHTML = "<span style=\"background: url(img/red_light15.png) left center no-repeat;width:100%,height:100%;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(unknown error)</span>";
				}
				document.getElementById("qrt_"+response.semantics).innerHTML = response.time + "ms";
				cnt++;
				if(cnt >= selectedSemantics.length)
					document.all.computeButton.disabled = false;
  			},
  			failure: function(response){
				cnt++;
				if(cnt >= selectedSemantics.length)
					document.all.computeButton.disabled = false;
  			}
		});
	}
	s += "</table>";
	document.all.qresults.innerHTML = s;
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
    	             "cmd" : "semantics",
    	             "email" : "tweetyweb@mthimm.de",
					 "measure": "",
					 "kb":"",
					 "format": "",
					 "timeout": 60000,
					 "unit_timeout": "ms"
   	           }),
  		dataType: "json",
  		success: function(response){populateSemantics(response);},
  		failure: function(response){}
	});
}

/*
 * Generates the HTML code for a measure
 * in the measures overview.
 */
function semanticsToHtml(measure,idx){
	var result = "";
	if(idx % 3 == 1)
		result = "<tr>";
	result += "<td width=\"20\"><input type=\"checkbox\" ";
	// check whether measure is currently selected
	if(selectedSemantics != "")
		for(var i = 0; i < selectedSemantics.length; i++){
			if(selectedSemantics[i] == measure.id){
				result += " checked=\"checked\" ";
				break;
			}
		}
	result += "id=\"mes_" + measure.id + "\"/></td>";
	result += "<td>"+ measure.label +" (<a target=\"blank\" href=\"incmes_doc_v1.pdf\">?</a>)</td>";
	if(idx % 3 == 0)
		result += "</tr>";
	checkSelectedSemantics.push(measure.id);
	return result;
}

/*
 * Populate the semantics div with the given semantics.
 */
function populateSemantics(semantics){
	checkSelectedSemantics = Array();
	allSemantics = semantics.semantics;
	var s = "<table width=\"100%\" style=\"font-size:10pt;\">";
	var idx = 0;
	for(var i = 0; i < semantics.semantics.length; i++){
		idx++;
		s += semanticsToHtml(semantics.semantics[i],idx);
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
	selectedSemantics = new Array();
	for(i = 0; i < checkSelectedSemantics.length; i++){
		if(document.getElementById("mes_"+checkSelectedSemantics[i]).checked == true)
			selectedSemantics.push(checkSelectedSemantics[i]);
	}
	// populate info on main page
	s = "";
	isFirst = true;
	for(var i = 0; i < allSemantics.length; i++){
		for(var j = 0; j < selectedSemantics.length; j++)
			if(allSemantics[i].id == selectedSemantics[j]){
				if(isFirst) isFirst = false;
				else s += ", ";
				s += allSemantics[i].label;
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
	for(i = 0; i < checkSelectedSemantics.length; i++)
		document.getElementById("mes_"+checkSelectedSemantics[i]).checked = true;
}

/*
 * Uncheck all measures in the "Select Inconsistency Measures" dialog
 */
function checkNone(){
	for(i = 0; i < checkSelectedSemantics.length; i++)
		document.getElementById("mes_"+checkSelectedSemantics[i]).checked = false;
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
	abortedSemantics.push(measure);
	cnt++;
	if(cnt >= selectedSemantics.length)
		document.all.computeButton.disabled = false;
}

