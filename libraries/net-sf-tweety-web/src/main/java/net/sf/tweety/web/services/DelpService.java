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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.arg.delp.reasoner.DelpReasoner;
import net.sf.tweety.arg.delp.semantics.ComparisonCriterion;
import net.sf.tweety.arg.delp.semantics.DelpAnswer;
import net.sf.tweety.arg.delp.semantics.EmptyCriterion;
import net.sf.tweety.arg.delp.semantics.GeneralizedSpecificity;
import net.sf.tweety.arg.delp.syntax.DefeasibleLogicProgram;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.web.TweetyServer;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Web service for defeasible logic programming.
 */
@Path("delp")
public class DelpService{

	/** The identifier of this service. */
	public static final String ID = "delp";
	
	/** Attribute "cmd" of queries referring to the requested command. */
	public static final String JSON_ATTR_CMD = "cmd";
	/** Attribute "reply" of replies referring to the reply type (same values as "cmd"). */
	public static final String JSON_ATTR_REPLY = "reply";
	/** "query" command. */
	public static final String JSON_VAL_QUERY = "query";
	/** Attribute "email" of queries/replies, used for identification and logging purposes. */
	public static final String JSON_ATTR_EMAIL = "email";
	/** Attribute "answer" of replies. */
	public static final String JSON_ATTR_ANSWER = "answer";
	/** Attribute "error" of replies giving some explanation of an error. */
	public static final String JSON_ATTR_ERROR = "error";
	/** Attribute "kb" of queries. */
	public static final String JSON_ATTR_KB = "kb";
	/** Attribute "query" of queries. */
	public static final String JSON_ATTR_QUERY = "query";
	/** Attribute "comparison criterion" of queries. */
	public static final String JSON_ATTR_COMP = "compcriterion";
	/** Value "generalized specificity" of attribute "comparison criterion" of queries. */
	public static final String JSON_ATTR_COMP_GENSPEC = "genspec";
	/** Value "empty criterion" of attribute "comparison criterion" of queries. */
	public static final String JSON_ATTR_COMP_EMPTY = "empty";
	
	 /**
     * Handles all requests for the delp service.
	 * @param query the query
     * @return String A serialized JSON containing the reply.
     * @throws JSONException thrown if something is completely going wrong.
     */
	@POST    
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String handleRequest(final String query) throws JSONException {
		try {
			JSONObject jsonQuery = new JSONObject(query);
			if(!jsonQuery.has(DelpService.JSON_ATTR_CMD))
				throw new JSONException("Malformed JSON: no \"cmd\" attribute given");
			if(!jsonQuery.has(DelpService.JSON_ATTR_EMAIL))
				throw new JSONException("Malformed JSON: no \"email\" attribute given");
			TweetyServer.log(DelpService.ID, "Received request " + jsonQuery);
			JSONObject jsonReply;
			if(jsonQuery.getString(DelpService.JSON_ATTR_CMD).equals(DelpService.JSON_VAL_QUERY)){
				jsonReply = this.handleQuery(jsonQuery);
			}else
				throw new JSONException("Malformed JSON: no valid value for \"cmd\" attribute.");
			jsonReply.put(DelpService.JSON_ATTR_REPLY, jsonQuery.getString(DelpService.JSON_ATTR_CMD));
			jsonReply.put(DelpService.JSON_ATTR_EMAIL, jsonQuery.getString(DelpService.JSON_ATTR_EMAIL));
			TweetyServer.log(DelpService.ID, "Finished handling request " + jsonQuery + ", reply is " + jsonReply);
			return jsonReply.toString();
		} catch (Exception e) {
			JSONObject jsonError = new JSONObject();
			jsonError.put(DelpService.JSON_ATTR_ERROR, e.getMessage());
			TweetyServer.log(DelpService.ID, "ERROR in handling request: " + e.getMessage());
			return jsonError.toString();
		}
    }
	
	/**
	 * Handles the "query" command
	 * @param query some query
	 * @return the reply
	 * @throws JSONException if some JSON issue occurs. 
	 */
	private JSONObject handleQuery(JSONObject query) throws JSONException{
		JSONObject jsonReply = new JSONObject();
		if(!query.has(DelpService.JSON_ATTR_KB))
			throw new JSONException("Malformed JSON: no \"kb\" attribute given");	
		if(!query.has(DelpService.JSON_ATTR_QUERY))
			throw new JSONException("Malformed JSON: no \"query\" attribute given");
		if(!query.has(DelpService.JSON_ATTR_COMP))
			throw new JSONException("Malformed JSON: no \"compcriterion\" attribute given");
		try {
			DelpParser parser = new DelpParser();
			DefeasibleLogicProgram delp = parser.parseBeliefBase(query.getString(DelpService.JSON_ATTR_KB));
			ComparisonCriterion comp = null;
			if(query.getString(DelpService.JSON_ATTR_COMP).equals(DelpService.JSON_ATTR_COMP_EMPTY))
					comp = new EmptyCriterion();
			if(query.getString(DelpService.JSON_ATTR_COMP).equals(DelpService.JSON_ATTR_COMP_GENSPEC))
					comp = new GeneralizedSpecificity();
			if(comp == null)
				throw new JSONException("Malformed JSON: unknown value for attribute \"compcriterion\"");
			DelpReasoner reasoner = new DelpReasoner(comp);
			FolParser folParser = new FolParser();
			folParser.setSignature(parser.getSignature());
			String qString = query.getString(DelpService.JSON_ATTR_QUERY).trim();
			Formula f;
			if(qString.startsWith("~"))
				f = new Negation((FolFormula)folParser.parseFormula(qString.substring(1)));
			else f = folParser.parseFormula(qString);
			DelpAnswer.Type ans = reasoner.query(delp,(FolFormula) f);
			jsonReply.put(DelpService.JSON_ATTR_KB, query.getString(DelpService.JSON_ATTR_KB));
			jsonReply.put(DelpService.JSON_ATTR_QUERY, query.getString(DelpService.JSON_ATTR_QUERY));
			jsonReply.put(DelpService.JSON_ATTR_COMP, query.getString(DelpService.JSON_ATTR_COMP));
			jsonReply.put(DelpService.JSON_ATTR_ANSWER, ans);
			return jsonReply;
		} catch (ParserException e) {			
			throw new JSONException("Malformed JSON: syntax of knowledge base and/or query does not conform to the given format.");
		} catch (IOException e) {			
			throw new JSONException("Malformed JSON: syntax of knowledge base and/or query does not conform to the given format.");
		} catch(Exception e){
			e.printStackTrace();
			throw new JSONException("An unexpected error occured. Please contact an administrator.");
		}		
	}
}
