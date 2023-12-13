package org.tweetyproject.web.pyargservices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Parser;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.analysis.InconsistencyMeasure;
import org.tweetyproject.logics.commons.analysis.NaiveMusEnumerator;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.pl.analysis.InconsistencyMeasureFactory;
import org.tweetyproject.logics.pl.analysis.InconsistencyMeasureFactory.Measure;
import org.tweetyproject.logics.pl.parser.PlParserFactory;
import org.tweetyproject.logics.pl.parser.PlParserFactory.Format;
import org.tweetyproject.logics.pl.sat.PlMusEnumerator;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.math.opt.solver.ApacheCommonsSimplex;
import org.tweetyproject.math.opt.solver.GlpkSolver;
import org.tweetyproject.math.opt.solver.Solver;
import org.tweetyproject.web.TweetyServer;
import org.tweetyproject.web.pyargservices.dung.AbstractExtensionReasonerFactory;
import org.tweetyproject.web.pyargservices.dung.DungReasonerCalleeFactory;
import org.tweetyproject.web.pyargservices.dung.DungReasonerResponse;
import org.tweetyproject.web.pyargservices.dung.DungServicesInfoResponse;
import org.tweetyproject.web.pyargservices.dung.AbstractExtensionReasonerFactory.Semantics;
import org.tweetyproject.web.pyargservices.dung.DungReasonerCalleeFactory.Command;
import org.tweetyproject.web.pyargservices.incmes.InconsistencyGetMeasuresResponse;
import org.tweetyproject.web.pyargservices.incmes.InconsistencyPost;
import org.tweetyproject.web.pyargservices.incmes.InconsistencyValueResponse;
import org.tweetyproject.web.services.DelpService;
import org.tweetyproject.web.services.InconsistencyMeasurementService;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.tweetyproject.arg.delp.parser.DelpParser;
import org.tweetyproject.arg.delp.reasoner.DelpReasoner;
import org.tweetyproject.arg.delp.semantics.ComparisonCriterion;
import org.tweetyproject.arg.delp.semantics.DelpAnswer;
import org.tweetyproject.arg.delp.semantics.EmptyCriterion;
import org.tweetyproject.arg.delp.semantics.GeneralizedSpecificity;
import org.tweetyproject.arg.delp.syntax.DefeasibleLogicProgram;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.web.pyargservices.delp.DelpCallee;
import org.tweetyproject.web.pyargservices.delp.DelpPost;
import org.tweetyproject.web.pyargservices.delp.DelpResponse;
import org.tweetyproject.web.pyargservices.dung.DungReasonerPost;
import javafx.util.Pair;

@RestController
public class RequestController {

	private final int SERVICES_TIMEOUT_DUNG = 600;
	private final int SERVICES_TIMEOUT_DELP = 600;
	private final int SERVICES_TIMEOUT_INCMES = 300;

	@PostMapping(value = "/dung", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(
  	@RequestBody DungReasonerPost dungReasonerPost) {
		if (dungReasonerPost.getCmd().equals("info"))
			return (Response) getInfo(dungReasonerPost);

		
		if (dungReasonerPost.getCmd().equals("get_models") || dungReasonerPost.getCmd().equals("get_model")) {
		DungTheory dungTheory = Utils.getDungTheory(dungReasonerPost.getNr_of_arguments(), dungReasonerPost.getAttacks());
		
		AbstractExtensionReasoner reasoner = AbstractExtensionReasonerFactory.getReasoner(
							Semantics.getSemantics(dungReasonerPost.getSemantics()));
		ExecutorService executor = Executors.newSingleThreadExecutor();
		DungReasonerResponse reasonerResponse = new DungReasonerResponse(dungReasonerPost.getCmd(), dungReasonerPost.getEmail(), dungReasonerPost.getNr_of_arguments(),dungReasonerPost.getAttacks(), dungReasonerPost.getSemantics(),dungReasonerPost.getSolver(),null,0,dungReasonerPost.getUnit_timeout(),"ERRORs");
		TimeUnit unit = Utils.getTimoutUnit(dungReasonerPost.getUnit_timeout());
		Callee callee = DungReasonerCalleeFactory.getCallee(Command.getCommand(dungReasonerPost.getCmd()), reasoner,dungTheory);
		int user_timeout = Utils.checkUserTimeout(dungReasonerPost.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);
		try{
			// handle timeout
				Future<Collection<Extension<DungTheory>>> future = executor.submit(callee);
				Pair<Collection<Extension<DungTheory>>,Long> result = Utils.runServicesWithTimeout(future, user_timeout, unit);
				executor.shutdownNow();
				reasonerResponse.setTime(result.getValue());
				reasonerResponse.setAnswer(result.getKey().toString());
				reasonerResponse.setStatus("SUCCESS");
			} catch (TimeoutException e) {
				reasonerResponse.setTime(dungReasonerPost.getTimeout());
				reasonerResponse.setAnswer(null);
				reasonerResponse.setStatus("TIMEOUT");
				executor.shutdownNow();
			} catch (Exception e){
				reasonerResponse.setTime(0.0);
				reasonerResponse.setAnswer(null);
				reasonerResponse.setStatus("Error");
				
				executor.shutdownNow();
			}
		return reasonerResponse;
		}
		else{
			return new DungReasonerResponse();
		}
	}

	@PostMapping(value = "/ping", produces = "application/json")
	@ResponseBody
	public Ping ping(@RequestBody Ping ping_Greeting) {
		System.out.println("==== PING ====");
		return ping_Greeting;
	}
	
	@PostMapping(value = "/info", produces = "application/json")
	@ResponseBody
	public DungServicesInfoResponse getInfo(@RequestBody DungReasonerPost pyArgPost){
		DungServicesInfoResponse response = new DungServicesInfoResponse();
		response.setReply("info");
		response.setEmail(pyArgPost.getEmail());
		response.setBackend_timeout(SERVICES_TIMEOUT_DUNG);
		Semantics [] sem = AbstractExtensionReasonerFactory.getSemantics();
		ArrayList<String> semantics_ids = new ArrayList<String>();
		for (Semantics s : sem){
			semantics_ids.add(s.id);
		}
		response.setSemantics(semantics_ids);

		Command [] com = DungReasonerCalleeFactory.getCommands();
		ArrayList<String> command_ids = new ArrayList<String>();
		for (Command c : com){
			command_ids.add(c.id);
		}
		response.setCommands(command_ids);


		//response.setSemantics(AbstractExtensionReasonerFactory.Semantics.values());
		return response;
	}

	
	@PostMapping(value = "/delp", produces = "application/json", consumes="application/json")
	@ResponseBody
	public Response handleRequest(
  	@RequestBody DelpPost delpPost) {
		  
		DelpResponse delpResponse =  new DelpResponse("query", delpPost.getEmail(), delpPost.getCompcriterion(), delpPost.getKb(), delpPost.getQuery(), delpPost.getTimeout(),null, 0.0, delpPost.getUnit_timeout(),null);
		TimeUnit unit = Utils.getTimoutUnit(delpPost.getUnit_timeout());
		int user_timeout = Utils.checkUserTimeout(delpPost.getTimeout(), SERVICES_TIMEOUT_DELP, unit);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			
			DelpParser parser = new DelpParser();
			DefeasibleLogicProgram delp = parser.parseBeliefBase(delpPost.getKb());
			ComparisonCriterion comp = null;
			if(delpPost.getCompcriterion().equals(DelpService.JSON_ATTR_COMP_EMPTY))
			comp = new EmptyCriterion();
			if(delpPost.getCompcriterion().equals(DelpService.JSON_ATTR_COMP_GENSPEC))
			comp = new GeneralizedSpecificity();
			if(comp == null)
			throw new JSONException("Malformed JSON: unknown value for attribute \"compcriterion\"");
			DelpReasoner reasoner = new DelpReasoner(comp);
			FolParser folParser = new FolParser();
			folParser.setSignature(parser.getSignature());
			String qString = delpPost.getQuery().trim();
			Formula f = null;
			if(qString.startsWith("~"))
			f = new Negation((FolFormula)folParser.parseFormula(qString.substring(1)));
			else f = folParser.parseFormula(qString);
			Callee delpCallee = new DelpCallee(delp, reasoner, f);
			Future<DelpAnswer.Type> future = executor.submit(delpCallee);
			Pair<DelpAnswer.Type,Long> result = Utils.runServicesWithTimeout(future, user_timeout, unit);
			
			System.out.println(result.toString());
				

			delpResponse.setAnswer(result.getKey().toString());
			delpResponse.setTime(result.getValue());
			delpResponse.setStatus("SUCCESS");
			System.out.println(delpResponse.toString());
			
		} 	catch (TimeoutException e) {
			delpResponse.setTime(delpPost.getTimeout());
			delpResponse.setAnswer(null);
			delpResponse.setStatus("TIMEOUT");
			executor.shutdownNow();
		} catch (ParserException e) {
			executor.shutdownNow();		
			throw new JSONException("Malformed JSON: syntax of knowledge base and/or query does not conform to the given format.");
		} catch (IOException e) {	
			executor.shutdownNow();		
			throw new JSONException("Malformed JSON: syntax of knowledge base and/or query does not conform to the given format.");
		} catch(Exception e){
			executor.shutdownNow();
			e.printStackTrace();
			throw new JSONException("An unexpected error occured. Please contact an administrator.");
		}
		return delpResponse;
	}


	@PostMapping(value = "/incmes", produces = "application/json")
	@ResponseBody
	public Response handleRequest(
  	@RequestBody InconsistencyPost incmesPost) {
		InconsistencyValueResponse icmesResponse = new InconsistencyValueResponse();

		try {
			
			if (incmesPost.getCmd().equals("value")){
				return handleGetICMESValue(incmesPost);
			}
	
			if (incmesPost.getCmd().equals("measures")){
				
				return handleGetMeasures(incmesPost);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


		return icmesResponse;
	}



	private class MeasurementCallee implements Callable<Double>{
		InconsistencyMeasure<BeliefSet<PlFormula,?>> measure;
		BeliefSet<PlFormula,PlSignature> beliefSet;
		public MeasurementCallee(InconsistencyMeasure<BeliefSet<PlFormula,?>> measure, BeliefSet<PlFormula,PlSignature> beliefSet){
			this.measure = measure;
			this.beliefSet = beliefSet;
		}
		@Override
		public Double call() throws Exception {
			return this.measure.inconsistencyMeasure(this.beliefSet);
		}		
	}



	private InconsistencyValueResponse handleGetICMESValue(InconsistencyPost query) throws JSONException{
		InconsistencyValueResponse icmesResponse = new InconsistencyValueResponse();
		TimeUnit unit = Utils.getTimoutUnit(query.getUnit_timeout());
		int user_timeout = Utils.checkUserTimeout(query.getTimeout(), SERVICES_TIMEOUT_INCMES, unit);
		// if(!query.has(InconsistencyMeasurementService.JSON_ATTR_MEASURE))
		// 	throw new JSONException("Malformed JSON: no \"measure\" attribute given");		
		// set sub-solvers
		SatSolver.setDefaultSolver(new Sat4jSolver());
		PlMusEnumerator.setDefaultEnumerator(new NaiveMusEnumerator<PlFormula>(new Sat4jSolver()));
		Solver.setDefaultLinearSolver(new ApacheCommonsSimplex());
		Solver.setDefaultIntegerLinearSolver(new GlpkSolver());	
		// get measure
		InconsistencyMeasure<BeliefSet<PlFormula,?>> measure =
				InconsistencyMeasureFactory.getInconsistencyMeasure(
						Measure.getMeasure(query.getMeasure()));
		if(measure == null)
			throw new JSONException("Malformed JSON: unknown value for attribute \"measure\"");
		// if(!query.has(InconsistencyMeasurementService.JSON_ATTR_FORMAT))
		// 	throw new JSONException("Malformed JSON: no \"format\" attribute given");
		Parser<PlBeliefSet,PlFormula> parser = PlParserFactory.getParserForFormat(
						Format.getFormat(query.getFormat()));
		if(parser == null)
			throw new JSONException("Malformed JSON: unknown value for attribute \"format\"");
			double val = -3;
		try {
			PlBeliefSet beliefSet = parser.parseBeliefBase(query.getKb());		
			ExecutorService executor = Executors.newSingleThreadExecutor();
			long millis = System.currentTimeMillis();
			try{
				// handle timeout				
				Future<Double> future = executor.submit(new MeasurementCallee(measure, beliefSet));
				Pair<Double,Long> result = Utils.runServicesWithTimeout(future, user_timeout, unit);
				val = result.getKey();
				icmesResponse.setTime(result.getValue());
				icmesResponse.setStatus("SUCCESS");
			    //val = future.get(InconsistencyMeasurementService.timeout, TimeUnit.SECONDS);
			    executor.shutdownNow();
			} catch (TimeoutException e) {
				//inconsistency value of -1 indicates that a timeout has occurred
				icmesResponse.setTime(query.getTimeout());
				icmesResponse.setStatus("TIMEOUT");
				executor.shutdownNow();
				val = -1;
			} catch (Exception e){
				//inconsistency value of -2 indicates some general error
				TweetyServer.log(InconsistencyMeasurementService.ID, "Unhandled exception: " + e.getMessage());
				icmesResponse.setStatus("ERROR");
				executor.shutdownNow();
				val = -2;
			}
			//inconsistency value of -3 indicates infinity
			if(val == Double.POSITIVE_INFINITY)
				val = -3;
			millis = System.currentTimeMillis() - millis;

			icmesResponse.setEmail(query.getEmail());
			icmesResponse.setFormat(query.getFormat());
			icmesResponse.setKb(query.getKb());
			icmesResponse.setMeasure(query.getMeasure());
			icmesResponse.setReply("value");
			icmesResponse.setValue(val);
	
			return icmesResponse;
		} catch (ParserException e) {			
			throw new JSONException("Malformed JSON: syntax of knowledge base does not conform to the given format.");
		} catch (IOException e) {			
			throw new JSONException("Malformed JSON: syntax of knowledge base does not conform to the given format.");
		} catch(Exception e){
			TweetyServer.log(InconsistencyMeasurementService.ID, "Unhandled exception: " + e.getMessage());
			throw new JSONException("An unexpected error occured. Please contact an administrator.");
		}
	}



		/**
	 * Handles the "List inconsistency measures" command
	 * @param query some query
	 * @return the reply
	 * @throws JSONException if some JSON issue occurs.
		 * @throws org.codehaus.jettison.json.JSONException
	 */
	private InconsistencyGetMeasuresResponse handleGetMeasures(InconsistencyPost query) throws JSONException, org.codehaus.jettison.json.JSONException{
		InconsistencyGetMeasuresResponse response = new InconsistencyGetMeasuresResponse();
		List<HashMap<String,String>> value = new LinkedList<HashMap<String,String>>();
		HashMap<String,String> jsonMes;
		for(Measure m: InconsistencyMeasureFactory.Measure.values()){
			jsonMes = new HashMap<String,String>();
			jsonMes.put(InconsistencyMeasurementService.JSON_ATTR_ID, m.id);
			jsonMes.put(InconsistencyMeasurementService.JSON_ATTR_LABEL, m.label);	
			value.add(jsonMes);
		}
		response.setMeasures(value);
		response.setEmail(query.getEmail());
		response.reply(query.getCmd());
		return response;
	}	

	




}
