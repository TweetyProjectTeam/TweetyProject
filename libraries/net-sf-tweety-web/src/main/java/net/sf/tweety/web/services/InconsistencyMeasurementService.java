/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.web.services;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.analysis.AbstractMusEnumerator;
import net.sf.tweety.logics.commons.analysis.InconsistencyMeasure;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.analysis.InconsistencyMeasureFactory;
import net.sf.tweety.logics.pl.analysis.InconsistencyMeasureFactory.Measure;
import net.sf.tweety.logics.pl.parser.PlParserFactory;
import net.sf.tweety.logics.pl.parser.PlParserFactory.Format;
import net.sf.tweety.logics.pl.sat.MarcoMusEnumerator;
import net.sf.tweety.logics.pl.sat.PlMusEnumerator;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.PlSignature;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.opt.solver.ApacheCommonsSimplex;
import net.sf.tweety.math.opt.solver.GlpkSolver;
import net.sf.tweety.web.TweetyServer;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Inconsistency measurement service.
 */
@Path("incmes")
public class InconsistencyMeasurementService{

	/** The identifier of this service. */
	public static final String ID = "incmes";
	
	/** The SAT solver configured for this service. */
	public static SatSolver satSolver = new Sat4jSolver();
	/** The MUS enumerator configured for this service. */
	public static AbstractMusEnumerator<PlFormula> musEnumerator = new MarcoMusEnumerator("/home/shared/incmes/marco_py-1.0/marco.py");//new NaiveMusEnumerator<PropositionalFormula>(new Sat4jSolver());
	//public static AbstractMusEnumerator<PropositionalFormula> musEnumerator =  new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py");//new NaiveMusEnumerator<PropositionalFormula>(new Sat4jSolver());
	/** The linear optimization solver configured for this service. */
	public static Solver linearSolver = new ApacheCommonsSimplex();
	/** The integer linear optimization solver configured for this service. */
	public static Solver integerLinearSolver = new GlpkSolver();
	
	
	/** Time out for the update operation (in seconds). */
	public static long timeout = 60*30;	
	
	/** Attribute "cmd" of queries referring to the requested command. */
	public static final String JSON_ATTR_CMD = "cmd";
	/** "get inconsistency value" command. */
	public static final String JSON_VAL_VALUE = "value";
	/** "list inconsistency measures" command. */
	public static final String JSON_VAL_MEASURES = "measures";
	/** Attribute "measure" of queries requesting a specific inconsistency value. */
	public static final String JSON_ATTR_MEASURE = "measure";
	/** Attribute "format" of queries requesting a specific inconsistency value. */
	public static final String JSON_ATTR_FORMAT = "format";
	/** Attribute "kb" of queries requesting a specific inconsistency value. */
	public static final String JSON_ATTR_KB = "kb";
	/** Attribute "value" of replies containing the requested inconsistency value. */
	public static final String JSON_ATTR_VALUE = "value";
	/** Attribute "time" of replies containing the time needed to compute the value. */
	public static final String JSON_ATTR_TIME = "time";
	/** Attribute "error" of replies giving some explanation of an error. */
	public static final String JSON_ATTR_ERROR = "error";
	/** Attribute "reply" of replies referring to the reply type (same values as "cmd"). */
	public static final String JSON_ATTR_REPLY = "reply";
	/** Attribute "email" of queries/replies, used for identification and logging purposes. */
	public static final String JSON_ATTR_EMAIL = "email";
	/** Attribute "measures" of replies, used for listing all available inconsistency measures. */
	public static final String JSON_ATTR_MEASURES = "measures";
	/** Attribute "formats" of replies, used for listing all available kb formats. */
	public static final String JSON_ATTR_FORMATS = "formats";
	/** Attribute "id" of replies to list queries */
	public static final String JSON_ATTR_ID = "id";
	/** Attribute "label" of replies to list queries */
	public static final String JSON_ATTR_LABEL = "label";
	
	/**
	 * Default constructor
	 */
	public InconsistencyMeasurementService(){
		// set some defaults for external algorithms
		SatSolver.setDefaultSolver(InconsistencyMeasurementService.satSolver);
		PlMusEnumerator.setDefaultEnumerator(InconsistencyMeasurementService.musEnumerator);
		Solver.setDefaultLinearSolver(InconsistencyMeasurementService.linearSolver);
		Solver.setDefaultIntegerLinearSolver(InconsistencyMeasurementService.integerLinearSolver);		
	}
	
	/**
	 * For handling timeouts.
	 */
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
	
    /**
     * Handles all requests for the inconsistency measurement
     * service.
     * @return String A serialized JSON containing the reply.
     * @throws JSONException thrown if something is completely going wrong.
     */
	@POST    
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String handleRequest(final String query) throws JSONException {
		try {
			JSONObject jsonQuery = new JSONObject(query);
			if(!jsonQuery.has(InconsistencyMeasurementService.JSON_ATTR_CMD))
				throw new JSONException("Malformed JSON: no \"cmd\" attribute given");
			if(!jsonQuery.has(InconsistencyMeasurementService.JSON_ATTR_EMAIL))
				throw new JSONException("Malformed JSON: no \"email\" attribute given");
			TweetyServer.log(InconsistencyMeasurementService.ID, "Received request " + jsonQuery);
			JSONObject jsonReply;
			if(jsonQuery.getString(InconsistencyMeasurementService.JSON_ATTR_CMD).equals(InconsistencyMeasurementService.JSON_VAL_VALUE)){
				jsonReply = this.handleGetValue(jsonQuery);
			}else if(jsonQuery.getString(InconsistencyMeasurementService.JSON_ATTR_CMD).equals(InconsistencyMeasurementService.JSON_VAL_MEASURES)){
				jsonReply = this.handleGetMeasures(jsonQuery);
			}else
				throw new JSONException("Malformed JSON: no valid value for \"cmd\" attribute.");
			jsonReply.put(InconsistencyMeasurementService.JSON_ATTR_REPLY, jsonQuery.getString(InconsistencyMeasurementService.JSON_ATTR_CMD));
			jsonReply.put(InconsistencyMeasurementService.JSON_ATTR_EMAIL, jsonQuery.getString(InconsistencyMeasurementService.JSON_ATTR_EMAIL));
			TweetyServer.log(InconsistencyMeasurementService.ID, "Finished handling request " + jsonQuery + ", reply is " + jsonReply);
			return jsonReply.toString();
		} catch (Exception e) {
			JSONObject jsonError = new JSONObject();
			jsonError.put(InconsistencyMeasurementService.JSON_ATTR_ERROR, e.getMessage());
			TweetyServer.log(InconsistencyMeasurementService.ID, "ERROR in handling request: " + e.getMessage());
			return jsonError.toString();
		}
    }
	
	/**
	 * Handles the "Get inconsistency value" command
	 * @param query some query
	 * @return the reply
	 * @throws JSONException if some JSON issue occurs 
	 */
	private JSONObject handleGetValue(JSONObject query) throws JSONException{
		if(!query.has(InconsistencyMeasurementService.JSON_ATTR_MEASURE))
			throw new JSONException("Malformed JSON: no \"measure\" attribute given");		
		InconsistencyMeasure<BeliefSet<PlFormula,?>> measure =
				InconsistencyMeasureFactory.getInconsistencyMeasure(
						Measure.getMeasure(query.getString(InconsistencyMeasurementService.JSON_ATTR_MEASURE)));
		if(measure == null)
			throw new JSONException("Malformed JSON: unknown value for attribute \"measure\"");
		if(!query.has(InconsistencyMeasurementService.JSON_ATTR_FORMAT))
			throw new JSONException("Malformed JSON: no \"format\" attribute given");
		Parser<PlBeliefSet,PlFormula> parser = PlParserFactory.getParserForFormat(
						Format.getFormat(query.getString(InconsistencyMeasurementService.JSON_ATTR_FORMAT)));
		if(parser == null)
			throw new JSONException("Malformed JSON: unknown value for attribute \"format\"");
		try {
			PlBeliefSet beliefSet = parser.parseBeliefBase(query.getString(InconsistencyMeasurementService.JSON_ATTR_KB));			
			ExecutorService executor = Executors.newSingleThreadExecutor();
			double val;
			long millis = System.currentTimeMillis();
			try{
				// handle timeout				
				Future<Double> future = executor.submit(new MeasurementCallee(measure, beliefSet));
			    val = future.get(InconsistencyMeasurementService.timeout, TimeUnit.SECONDS);
			    executor.shutdownNow();
			} catch (TimeoutException e) {
				//inconsistency value of -1 indicates that a timeout has occurred
				executor.shutdownNow();
				val = -1;
			} catch (Exception e){
				//inconsistency value of -2 indicates some general error
				TweetyServer.log(InconsistencyMeasurementService.ID, "Unhandled exception: " + e.getMessage());
				executor.shutdownNow();
				val = -2;
			}
			//inconsistency value of -3 indicates infinity
			if(val == Double.POSITIVE_INFINITY)
				val = -3;
			millis = System.currentTimeMillis() - millis;
			JSONObject jsonReply = new JSONObject();
			jsonReply.put(InconsistencyMeasurementService.JSON_ATTR_MEASURE, query.getString(InconsistencyMeasurementService.JSON_ATTR_MEASURE));
			jsonReply.put(InconsistencyMeasurementService.JSON_ATTR_FORMAT, query.getString(InconsistencyMeasurementService.JSON_ATTR_FORMAT));
			jsonReply.put(InconsistencyMeasurementService.JSON_ATTR_KB, query.getString(InconsistencyMeasurementService.JSON_ATTR_KB));
			jsonReply.put(InconsistencyMeasurementService.JSON_ATTR_VALUE, val);
			jsonReply.put(InconsistencyMeasurementService.JSON_ATTR_TIME, millis);
			return jsonReply;
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
	 */
	private JSONObject handleGetMeasures(JSONObject query) throws JSONException{
		JSONObject jsonReply = new JSONObject();
		List<JSONObject> value = new LinkedList<JSONObject>();
		JSONObject jsonMes;
		for(Measure m: InconsistencyMeasureFactory.Measure.values()){
			jsonMes = new JSONObject();
			jsonMes.put(InconsistencyMeasurementService.JSON_ATTR_ID, m.id);
			jsonMes.put(InconsistencyMeasurementService.JSON_ATTR_LABEL, m.label);	
			value.add(jsonMes);
		}
		jsonReply.put(InconsistencyMeasurementService.JSON_ATTR_MEASURES, value);
		return jsonReply;
	}	
}
