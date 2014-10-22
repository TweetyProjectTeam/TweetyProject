/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.web.services;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
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
import net.sf.tweety.logics.commons.analysis.NaiveMusEnumerator;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.analysis.InconsistencyMeasureFactory;
import net.sf.tweety.logics.pl.analysis.InconsistencyMeasureFactory.Measure;
import net.sf.tweety.logics.pl.parser.PlParserFactory;
import net.sf.tweety.logics.pl.parser.PlParserFactory.Format;
import net.sf.tweety.logics.pl.sat.MarcoMusEnumerator;
import net.sf.tweety.logics.pl.sat.PlMusEnumerator;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.opt.solver.ApacheCommonsSimplex;
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
	//public static AbstractMusEnumerator<PropositionalFormula> musEnumerator = new MarcoMusEnumerator("/home/shared/incmes/marco_py-1.0/marco.py");//new NaiveMusEnumerator<PropositionalFormula>(new Sat4jSolver());
	public static AbstractMusEnumerator<PropositionalFormula> musEnumerator = new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py"); //new NaiveMusEnumerator<PropositionalFormula>(new Sat4jSolver());
	/** The linear optimization solver configured for this service. */
	public static Solver linearSolver = new ApacheCommonsSimplex();
	
	
	/** Time out for the update operation (in seconds). */
	public static long timeout = 60*30;	
	
	/** Attribute "cmd" of queries referring to the requested command. */
	public static final String JSON_ATTR_CMD = "cmd";
	/** "get inconsistency value" command. */
	public static final String JSON_VAL_VALUE = "value";
	/** "list inconsistency measures" command. */
	public static final String JSON_VAL_MEASURES = "measures";
	/** "list knowledge base formats" command. */
	public static final String JSON_VAL_FORMATS = "formats";
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
	/** Attribute "description" of replies for information on measures and formats. */
	public static final String JSON_ATTR_DESC = "description";
	
	/**
	 * Default constructor
	 */
	public InconsistencyMeasurementService(){
		// set some defaults for external algorithms
		SatSolver.setDefaultSolver(InconsistencyMeasurementService.satSolver);
		PlMusEnumerator.setDefaultEnumerator(InconsistencyMeasurementService.musEnumerator);
		Solver.setDefaultLinearSolver(InconsistencyMeasurementService.linearSolver);
	}
	
	/**
	 * For handling timeouts.
	 */
	private class MeasurementCallee implements Callable<Double>{
		InconsistencyMeasure<BeliefSet<PropositionalFormula>> measure;
		BeliefSet<PropositionalFormula> beliefSet;
		public MeasurementCallee(InconsistencyMeasure<BeliefSet<PropositionalFormula>> measure, BeliefSet<PropositionalFormula> beliefSet){
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
			}else if(jsonQuery.getString(InconsistencyMeasurementService.JSON_ATTR_CMD).equals(InconsistencyMeasurementService.JSON_VAL_FORMATS)){
				jsonReply = this.handleGetFormats(jsonQuery);
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
	 * @throws JSONException 
	 */
	private JSONObject handleGetValue(JSONObject query) throws JSONException{
		if(!query.has(InconsistencyMeasurementService.JSON_ATTR_MEASURE))
			throw new JSONException("Malformed JSON: no \"measure\" attribute given");		
		InconsistencyMeasure<BeliefSet<PropositionalFormula>> measure =
				InconsistencyMeasureFactory.getInconsistencyMeasure(
						Measure.getMeasure(query.getString(InconsistencyMeasurementService.JSON_ATTR_MEASURE)));
		if(measure == null)
			throw new JSONException("Malformed JSON: unknown value for attribute \"measure\"");
		if(!query.has(InconsistencyMeasurementService.JSON_ATTR_FORMAT))
			throw new JSONException("Malformed JSON: no \"format\" attribute given");
		Parser<PlBeliefSet> parser = PlParserFactory.getParserForFormat(
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
			} catch (TimeoutException e) {
				//inconsistency value of -1 indicates that a timeout has occurred
				val = -1;
			} catch (Exception e){
				//inconsistency value of -2 indicates some general error
				TweetyServer.log(InconsistencyMeasurementService.ID, "Unhandled exception: " + e.getMessage());
				val = -2;
			}
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
			e.printStackTrace();
			throw new JSONException("An unexpected error occured. Please contact an administrator.");
		}
	}
	
	/**
	 * Handles the "List inconsistency measures" command
	 * @param query some query
	 * @return the reply
	 * @throws JSONException 
	 */
	private JSONObject handleGetMeasures(JSONObject query) throws JSONException{
		JSONObject jsonReply = new JSONObject();
		List<JSONObject> value = new LinkedList<JSONObject>();
		JSONObject jsonMes;
		for(Measure m: InconsistencyMeasureFactory.Measure.values()){
			jsonMes = new JSONObject();
			jsonMes.put(InconsistencyMeasurementService.JSON_ATTR_ID, m.id);
			jsonMes.put(InconsistencyMeasurementService.JSON_ATTR_LABEL, m.label);
			jsonMes.put(InconsistencyMeasurementService.JSON_ATTR_DESC, m.description);
			value.add(jsonMes);
		}
		jsonReply.put(InconsistencyMeasurementService.JSON_ATTR_MEASURES, value);
		return jsonReply;
	}
	
	/**
	 * Handles the "List knowledge base formats" command
	 * @param query some query
	 * @return the reply
	 * @throws JSONException 
	 */
	private JSONObject handleGetFormats(JSONObject query) throws JSONException{
		JSONObject jsonReply = new JSONObject();
		Collection<JSONObject> value = new HashSet<JSONObject>();
		JSONObject jsonFormat;
		for(Format f: PlParserFactory.Format.values()){
			jsonFormat = new JSONObject();
			jsonFormat.put(InconsistencyMeasurementService.JSON_ATTR_ID, f.id);
			jsonFormat.put(InconsistencyMeasurementService.JSON_ATTR_LABEL, f.label);
			jsonFormat.put(InconsistencyMeasurementService.JSON_ATTR_DESC, f.description);
			value.add(jsonFormat);
		}
		jsonReply.put(InconsistencyMeasurementService.JSON_ATTR_FORMATS, value);
		return jsonReply;
	}
}
