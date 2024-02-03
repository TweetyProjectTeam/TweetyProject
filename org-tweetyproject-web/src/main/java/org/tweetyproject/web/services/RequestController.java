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
package org.tweetyproject.web.services;

import java.io.FileNotFoundException;
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
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.pl.analysis.InconsistencyMeasureFactory;
import org.tweetyproject.logics.pl.analysis.InconsistencyMeasureFactory.Measure;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.parser.PlParserFactory;
import org.tweetyproject.logics.pl.parser.PlParserFactory.Format;
import org.tweetyproject.logics.pl.sat.PlMusEnumerator;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.math.opt.solver.ApacheCommonsSimplex;
import org.tweetyproject.math.opt.solver.GlpkSolver;
import org.tweetyproject.math.opt.solver.Solver;
import org.tweetyproject.web.services.aba.AbaGetSemanticsResponse;
import org.tweetyproject.web.services.aba.AbaReasonerCalleeFactory;
import org.tweetyproject.web.services.aba.AbaReasonerPost;
import org.tweetyproject.web.services.aba.AbaReasonerResponse;
import org.tweetyproject.web.services.aba.GeneralAbaReasonerFactory;
import org.tweetyproject.web.services.delp.DeLPCallee;
import org.tweetyproject.web.services.delp.DeLPPost;
import org.tweetyproject.web.services.delp.DeLPResponse;
import org.tweetyproject.web.services.dung.AbstractExtensionReasonerFactory;
import org.tweetyproject.web.services.dung.DungReasonerCalleeFactory;
import org.tweetyproject.web.services.dung.DungReasonerPost;
import org.tweetyproject.web.services.dung.DungReasonerResponse;
import org.tweetyproject.web.services.dung.DungServicesInfoResponse;
import org.tweetyproject.web.services.dung.AbstractExtensionReasonerFactory.Semantics;
import org.tweetyproject.web.services.dung.DungReasonerCalleeFactory.Command;
import org.tweetyproject.web.services.incmes.InconsistencyGetMeasuresResponse;
import org.tweetyproject.web.services.incmes.InconsistencyPost;
import org.tweetyproject.web.services.incmes.InconsistencyValueResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.tweetyproject.arg.aba.parser.AbaParser;
import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.arg.delp.parser.DelpParser;
import org.tweetyproject.arg.delp.reasoner.DelpReasoner;
import org.tweetyproject.arg.delp.semantics.ComparisonCriterion;
import org.tweetyproject.arg.delp.semantics.DelpAnswer;
import org.tweetyproject.arg.delp.semantics.EmptyCriterion;
import org.tweetyproject.arg.delp.semantics.GeneralizedSpecificity;
import org.tweetyproject.arg.delp.syntax.DefeasibleLogicProgram;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;

import javafx.util.Pair;
import java.util.logging.Level;


/**
 * *description missing*
 */
@RestController
public class RequestController {

	private final int SERVICES_TIMEOUT_DUNG = 600;
	private final int SERVICES_TIMEOUT_DELP = 600;
	private final int SERVICES_TIMEOUT_INCMES = 300;



/**
 * Handles HTTP POST requests at the endpoint "/aba". Parses and processes the incoming
 * JSON payload to perform various ABA reasoning commands.
 * Supported commands are:
 * - get_models: Return all models for a given semantics and beliefbase
 * - get_model: Return some model for a given semantics and beliefbase
 * - query: Query an assumption
 * - semantics: Return supported semantics
 *
 * @param AbaReasonerPost The request payload containing information for ABA reasoning.
 * @return A Response object containing the result of the ABA reasoning operation.
 * @throws ParserException If there is an error while parsing the input data.
 * @throws IOException If there is an error reading or writing data.
 * @throws JSONException If there is an error with JSON processing.
 * @throws org.codehaus.jettison.json.JSONException If there is an error with Jettison JSON processing.
 */
	@PostMapping(value = "/aba", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(
			@RequestBody AbaReasonerPost AbaReasonerPost) throws ParserException, IOException, JSONException, org.codehaus.jettison.json.JSONException {

			LoggerUtil.logger.info(String.format("User: %s  Command: %s", AbaReasonerPost.getEmail(), AbaReasonerPost.getCmd()));
			if (AbaReasonerPost.getCmd().equals("semantics")) {
				return handleGetSemantics(AbaReasonerPost);
			}
			Callee callee = null;
			SatSolver.setDefaultSolver(new Sat4jSolver());
			if (AbaReasonerPost.getKb_format().equals("pl")) {
				AbaParser<PlFormula> parser1 = new AbaParser<PlFormula>(new PlParser());
				Assumption<PlFormula> a = new Assumption<>(new Proposition(AbaReasonerPost.getQuery_assumption()));
				AbaTheory<PlFormula> abat1 = null;
				try {
					abat1 = parser1.parseBeliefBase(AbaReasonerPost.getKb());
				} catch (FileNotFoundException e) {
					LoggerUtil.logger.log(Level.SEVERE,(String.format("Error while parsing the Beliefbase: %s", e.getClass().getSimpleName())));

					e.printStackTrace();
				} catch (ParserException e) {
					LoggerUtil.logger.log(Level.SEVERE,(String.format("Error while parsing the Beliefbase: %s", e.getClass().getSimpleName())));

					e.printStackTrace();
				} catch (IOException e) {
					LoggerUtil.logger.log(Level.SEVERE,(String.format("Error while parsing the Beliefbase: %s", e.getClass().getSimpleName())));
					e.printStackTrace();
				}

				GeneralAbaReasoner<PlFormula> r1 = GeneralAbaReasonerFactory
						.getReasoner(GeneralAbaReasonerFactory.Semantics.getSemantics(AbaReasonerPost.getSemantics()));
				callee = AbaReasonerCalleeFactory
						.getCallee(AbaReasonerCalleeFactory.Command.getCommand(AbaReasonerPost.getCmd()), r1, abat1,a);
			}

			if (AbaReasonerPost.getKb_format().equals("fol")) {
				FolParser folparser = new FolParser();
				FolSignature sig = folparser.parseSignature(AbaReasonerPost.getFol_signature());
				AbaParser<FolFormula> parser1 = new AbaParser<FolFormula>(folparser);
				Assumption<FolFormula> a = new Assumption<>(folparser.parseFormula(AbaReasonerPost.getQuery_assumption()));
				parser1.setSymbolComma(";");
				AbaTheory<FolFormula> abat1 = null;
				folparser.setSignature(sig);
				try {
					abat1 = parser1.parseBeliefBase(AbaReasonerPost.getKb());
					System.out.println("Parsed belief base: " + abat1);
				} catch (FileNotFoundException e) {
					LoggerUtil.logger.log(Level.SEVERE,(String.format("Error while parsing the Beliefbase: %s", e.getClass().getSimpleName())));
					e.printStackTrace();
				} catch (ParserException e) {
					LoggerUtil.logger.log(Level.SEVERE,(String.format("Error while parsing the Beliefbase: %s", e.getClass().getSimpleName())));

					e.printStackTrace();
				} catch (IOException e) {
					LoggerUtil.logger.log(Level.SEVERE,(String.format("Error while parsing the Beliefbase: %s", e.getClass().getSimpleName())));

					e.printStackTrace();
				}

				GeneralAbaReasoner<FolFormula> r1 = GeneralAbaReasonerFactory
						.getReasoner(GeneralAbaReasonerFactory.Semantics.getSemantics(AbaReasonerPost.getSemantics()));

				try {
					callee = AbaReasonerCalleeFactory
						.getCallee(AbaReasonerCalleeFactory.Command.getCommand(AbaReasonerPost.getCmd()), r1, abat1,a);
				}
				catch (Exception e) {
					LoggerUtil.logger.log(Level.SEVERE,(String.format("Error while creating ABAReasonerCallee:%s", e.getClass().getSimpleName())));
				}

			}

			ExecutorService executor = Executors.newSingleThreadExecutor();
			AbaReasonerResponse reasonerResponse = new AbaReasonerResponse(AbaReasonerPost.getCmd(),
					AbaReasonerPost.getEmail(), AbaReasonerPost.getKb(), AbaReasonerPost.getKb_format(),
					AbaReasonerPost.getFol_signature(), AbaReasonerPost.getQuery_assumption(),
					AbaReasonerPost.getSemantics(), AbaReasonerPost.getTimeout(), "", 0.0,
					AbaReasonerPost.getUnit_timeout(), "");
			TimeUnit unit = Utils.getTimoutUnit(AbaReasonerPost.getUnit_timeout());
			int user_timeout = Utils.checkUserTimeout(AbaReasonerPost.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);
			try {
				// handle timeout
				LoggerUtil.logger.info(String.format("Run command \"%s\" with timeout: %s %s", AbaReasonerPost.getCmd(),user_timeout, AbaReasonerPost.getUnit_timeout()));
				if (AbaReasonerPost.getCmd().equals("get_models") || AbaReasonerPost.getCmd().equals("get_model")) {

					Future<Collection<AbaExtension<Formula>>> future = executor.submit(callee);
					Pair<Collection<AbaExtension<Formula>>, Long> result = Utils.runServicesWithTimeout(future,
						user_timeout, unit);
					LoggerUtil.logger.info(String.format("Execution of command \"%s\" finished after %s %s ", AbaReasonerPost.getCmd(),result.getValue(),AbaReasonerPost.getUnit_timeout()));
						reasonerResponse.setTime(result.getValue());
						reasonerResponse.setAnswer(result.getKey().toString());
						reasonerResponse.setStatus("SUCCESS");
					}
					else if (AbaReasonerPost.getCmd().equals("query")){
					Future<Boolean> future = executor.submit(callee);
					Pair<Boolean, Long> result = Utils.runServicesWithTimeout(future,
						user_timeout, unit);

					LoggerUtil.logger.info(String.format("Execution of command \"%s\" finished after %s %s ", AbaReasonerPost.getCmd(),result.getValue(),AbaReasonerPost.getUnit_timeout()));

						reasonerResponse.setTime(result.getValue());
					reasonerResponse.setAnswer(result.getKey().toString());
					reasonerResponse.setStatus("SUCCESS");
				}
				else{
					LoggerUtil.logger.log(Level.SEVERE,String.format("Command \"%s\"  not found.", AbaReasonerPost.getCmd()));
					Pair<String,Integer> result = new Pair<String,Integer>("Command not found",0 );
					System.out.println(result.getKey().toString());
					reasonerResponse.setTime(result.getValue());
					reasonerResponse.setAnswer(result.getKey().toString());
					reasonerResponse.setStatus("ERROR");
				}

				executor.shutdownNow();
			} catch (TimeoutException e) {
				LoggerUtil.logger.info(String.format("Execution of command \"%s\" reached timeout of %s %s and was aborted.", AbaReasonerPost.getCmd(),AbaReasonerPost.getTimeout(),AbaReasonerPost.getUnit_timeout()));
				reasonerResponse.setTime(AbaReasonerPost.getTimeout());
				reasonerResponse.setAnswer(null);
				reasonerResponse.setStatus("TIMEOUT");
				executor.shutdownNow();
			} catch (Exception e) {
				LoggerUtil.logger.log(Level.SEVERE,(String.format("Error while running command \"%s\": %s",AbaReasonerPost.getCmd(), e.getClass().getSimpleName())));
				reasonerResponse.setTime(0.0);
				reasonerResponse.setAnswer(null);
				reasonerResponse.setStatus("Error");

				executor.shutdownNow();
			}
			return reasonerResponse;

	}

	/**
 	* Handles HTTP POST requests for Dung Reasoner operations.
 	*
 	* <p>This method processes requests with the endpoint "/dung" that have the specified content types
 	* for both request and response. It takes a DungReasonerPost object as the request body and returns
 	* a Response object as the response body.</p>
 	*
 	* <p>The method checks the command (cmd) from the DungReasonerPost object and performs different
 	* operations based on the command. If the command is "info," it delegates the request to the getInfo
 	* method. If the command is "get_models" or "get_model," it processes the request using the DungTheory,
 	* AbstractExtensionReasoner, and other components. The result includes information about the execution
 	* time, answer, and status, which is encapsulated in a DungReasonerResponse object.</p>
 	*
 	* <p>In case of a timeout during execution, the method sets the response status to "TIMEOUT" and includes
 	* the specified timeout duration. If any other exception occurs, the response status is set to "Error,"
 	* and the method provides a generic response with a time of 0.0 and a null answer.</p>
 	*
 	* <p>If the command is not recognized or not applicable, the method returns a default DungReasonerResponse.</p>
 	*
 	* @param dungReasonerPost The DungReasonerPost object representing the request payload.
 	* @return A Response object representing the response payload.
 	*/
	@PostMapping(value = "/dung", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(
			@RequestBody DungReasonerPost dungReasonerPost) {
		if (dungReasonerPost.getCmd().equals("info"))
			return (Response) getInfo(dungReasonerPost);

		if (dungReasonerPost.getCmd().equals("get_models") || dungReasonerPost.getCmd().equals("get_model")) {
			DungTheory dungTheory = Utils.getDungTheory(dungReasonerPost.getNr_of_arguments(),
					dungReasonerPost.getAttacks());

			AbstractExtensionReasoner reasoner = AbstractExtensionReasonerFactory.getReasoner(
					Semantics.getSemantics(dungReasonerPost.getSemantics()));
			ExecutorService executor = Executors.newSingleThreadExecutor();
			DungReasonerResponse reasonerResponse = new DungReasonerResponse(dungReasonerPost.getCmd(),
					dungReasonerPost.getEmail(), dungReasonerPost.getNr_of_arguments(), dungReasonerPost.getAttacks(),
					dungReasonerPost.getSemantics(), dungReasonerPost.getSolver(), null, 0,
					dungReasonerPost.getUnit_timeout(), "ERRORs");
			TimeUnit unit = Utils.getTimoutUnit(dungReasonerPost.getUnit_timeout());
			Callee callee = DungReasonerCalleeFactory.getCallee(Command.getCommand(dungReasonerPost.getCmd()), reasoner,
					dungTheory);
			int user_timeout = Utils.checkUserTimeout(dungReasonerPost.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);
			try {
				// handle timeout
				Future<Collection<Extension<DungTheory>>> future = executor.submit(callee);
				Pair<Collection<Extension<DungTheory>>, Long> result = Utils.runServicesWithTimeout(future,
						user_timeout, unit);
				executor.shutdownNow();
				reasonerResponse.setTime(result.getValue());
				reasonerResponse.setAnswer(result.getKey().toString());
				reasonerResponse.setStatus("SUCCESS");
			} catch (TimeoutException e) {
				reasonerResponse.setTime(dungReasonerPost.getTimeout());
				reasonerResponse.setAnswer(null);
				reasonerResponse.setStatus("TIMEOUT");
				executor.shutdownNow();
			} catch (Exception e) {
				reasonerResponse.setTime(0.0);
				reasonerResponse.setAnswer(null);
				reasonerResponse.setStatus("Error");

				executor.shutdownNow();
			}
			return reasonerResponse;
		} else {
			return new DungReasonerResponse();
		}
	}

	/**
 	* Handles HTTP POST requests for ping operations.
 	*
 	* <p>This method processes requests with the endpoint "/ping" that have the specified content type
 	* for the request and response. It takes a Ping object as the request body and returns the same
 	* Ping object as the response body.</p>
 	*
 	* <p>The method echoes the received Ping object, providing a simple way to test the connectivity
 	* or response of the service. The response contains the same information as the request, including
 	* the identifier (id) and content of the Ping object.</p>
 	*
 	* @param ping_Greeting The Ping object representing the request payload.
 	* @return A Ping object representing the response payload.
 	*/
	@PostMapping(value = "/ping", produces = "application/json")
	@ResponseBody
	public Ping ping(@RequestBody Ping ping_Greeting) {
		return ping_Greeting;
	}

	/**
	 * *description missing*
	 * @param dungPost *description missing*
	 * @return *description missing*
	 */
	@PostMapping(value = "/info", produces = "application/json")
	@ResponseBody
	public DungServicesInfoResponse getInfo(@RequestBody DungReasonerPost dungPost) {
		DungServicesInfoResponse response = new DungServicesInfoResponse();
		response.setReply("info");
		response.setEmail(dungPost.getEmail());
		response.setBackend_timeout(SERVICES_TIMEOUT_DUNG);
		Semantics[] sem = AbstractExtensionReasonerFactory.getSemantics();
		ArrayList<String> semantics_ids = new ArrayList<String>();
		for (Semantics s : sem) {
			semantics_ids.add(s.id);
		}
		response.setSemantics(semantics_ids);

		Command[] com = DungReasonerCalleeFactory.getCommands();
		ArrayList<String> command_ids = new ArrayList<String>();
		for (Command c : com) {
			command_ids.add(c.id);
		}
		response.setCommands(command_ids);

		// response.setSemantics(AbstractExtensionReasonerFactory.Semantics.values());
		return response;
	}

	/**
 * Handles HTTP POST requests for Defeasible logic programming DeLP Reasoner operations.
 *
 * This method processes requests with the endpoint "/delp" that have the specified content types
 * for both request and response. It takes a DeLPPost object as the request body and returns a
 * DelpResponse object as the response body.
 *
 * The method prints the received DelpPost object to the console for debugging purposes. It then
 * constructs a DeLPResponse object with default values, prepares the DelpReasoner, and executes it
 * asynchronously using a separate thread. The result includes information about the execution time,
 * answer, and status, encapsulated in a DelpResponse object.
 *
 * If the execution times out, the method sets the response status to "TIMEOUT" and includes the
 * specified timeout duration. If a parsing exception occurs during knowledge base or query parsing,
 * the method throws a JSONException with an appropriate error message. If any other unexpected exception
 * occurs, the method prints the stack trace and throws a JSONException with a generic error message.
 *
 * @param delpPost The DelpPost object representing the request payload.
 * @return A Response object representing the response payload.
 * @throws JSONException If there is a parsing error or an unexpected error occurs during processing.
 */
	@PostMapping(value = "/delp", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(
			@RequestBody DeLPPost delpPost) {

		System.out.println(delpPost.toString());

		DeLPResponse delpResponse = new DeLPResponse("query", delpPost.getEmail(), delpPost.getCompcriterion(),
				delpPost.getKb(), delpPost.getQuery(), delpPost.getTimeout(), null, 0.0, delpPost.getUnit_timeout(),
				null);
		TimeUnit unit = Utils.getTimoutUnit(delpPost.getUnit_timeout());
		int user_timeout = Utils.checkUserTimeout(delpPost.getTimeout(), SERVICES_TIMEOUT_DELP, unit);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {

			DelpParser parser = new DelpParser();
			DefeasibleLogicProgram delp = parser.parseBeliefBase(delpPost.getKb());
			ComparisonCriterion comp = null;
			if (delpPost.getCompcriterion().equals("compcriterion"))
				comp = new EmptyCriterion();
			if (delpPost.getCompcriterion().equals("genspec"))
				comp = new GeneralizedSpecificity();
			if (comp == null)
				throw new JSONException("Malformed JSON: unknown value for attribute \"compcriterion\"");
			DelpReasoner reasoner = new DelpReasoner(comp);
			FolParser folParser = new FolParser();
			folParser.setSignature(parser.getSignature());
			String qString = delpPost.getQuery().trim();
			Formula f = null;
			if (qString.startsWith("~"))
				f = new Negation((FolFormula) folParser.parseFormula(qString.substring(1)));
			else
				f = folParser.parseFormula(qString);
			Callee delpCallee = new DeLPCallee(delp, reasoner, f);
			Future<DelpAnswer.Type> future = executor.submit(delpCallee);
			Pair<DelpAnswer.Type, Long> result = Utils.runServicesWithTimeout(future, user_timeout, unit);

			System.out.println(result.toString());

			delpResponse.setAnswer(result.getKey().toString());
			delpResponse.setTime(result.getValue());
			delpResponse.setStatus("SUCCESS");
			System.out.println(delpResponse.toString());

		} catch (TimeoutException e) {
			delpResponse.setTime(delpPost.getTimeout());
			delpResponse.setAnswer(null);
			delpResponse.setStatus("TIMEOUT");
			executor.shutdownNow();
		} catch (ParserException e) {
			executor.shutdownNow();
			throw new JSONException(
					"Malformed JSON: syntax of knowledge base and/or query does not conform to the given format.");
		} catch (IOException e) {
			executor.shutdownNow();
			throw new JSONException(
					"Malformed JSON: syntax of knowledge base and/or query does not conform to the given format.");
		} catch (Exception e) {
			executor.shutdownNow();
			e.printStackTrace();
			throw new JSONException("An unexpected error occured. Please contact an administrator.");
		}
		return delpResponse;
	}

	/**
 	* Handles HTTP POST requests for Inconsistency Measures operations.
 	*
 	* This method processes requests with the endpoint "/incmes" that have the specified content type
 	* for the request and response. It takes an InconsistencyPost object as the request body and returns a
 	* Response object as the response body.
 	*
 	* The method initializes an InconsistencyValueResponse object for the response. It then checks the
 	* command (cmd) from the InconsistencyPost object and delegates the request to specific methods based on
 	* the command. If the command is "value," it calls the handleGetICMESValue method. If the command is
 	* "measures," it calls the handleGetMeasures method. Any exceptions that occur during the processing are
 	* caught, and a generic error message is printed to the console.
 	*
 	* If the command is not recognized or not applicable, the method returns a default InconsistencyValueResponse.
 	*
 	* @param incmesPost The InconsistencyPost object representing the request payload.
 	* @return A Response object representing the response payload.
 	*/

	@PostMapping(value = "/incmes", produces = "application/json")
	@ResponseBody
	public Response handleRequest(
			@RequestBody InconsistencyPost incmesPost) {
		InconsistencyValueResponse icmesResponse = new InconsistencyValueResponse();

		try {

			if (incmesPost.getCmd().equals("value")) {
				return handleGetICMESValue(incmesPost);
			}

			if (incmesPost.getCmd().equals("measures")) {

				return handleGetMeasures(incmesPost);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return icmesResponse;
	}

	/**
 	* Private inner class representing a Callable task for measuring inconsistency of a belief set.
 	*
 	* The MeasurementCallee class implements the Callable interface with a generic type of Double.
 	* Instances of this class are used to perform asynchronous calculations of inconsistency measures
 	* for a given BeliefSet using a specific InconsistencyMeasure.
 	*
 	* The class has two fields: "measure," representing the InconsistencyMeasure to be applied,
 	* and "beliefSet," representing the BeliefSet for which the inconsistency measure is calculated.
 	*
 	* The constructor initializes the MeasurementCallee with a specific InconsistencyMeasure and
 	* a BeliefSet.
 	*
 	* The call method, required by the Callable interface, invokes the inconsistencyMeasure method
 	* of the provided InconsistencyMeasure on the given BeliefSet, returning the calculated inconsistency
 	* measure value as a Double.
 	*
 	* @param <T> The type of formulas in the belief set, e.g., PlFormula.
 	* @param <S> The type of signature associated with the belief set, e.g., PlSignature.
 	*/
	private class MeasurementCallee implements Callable<Double> {
		InconsistencyMeasure<BeliefSet<PlFormula, ?>> measure;
		BeliefSet<PlFormula, PlSignature> beliefSet;

		public MeasurementCallee(InconsistencyMeasure<BeliefSet<PlFormula, ?>> measure,
				BeliefSet<PlFormula, PlSignature> beliefSet) {
			this.measure = measure;
			this.beliefSet = beliefSet;
		}

		@Override
		public Double call() throws Exception {
			return this.measure.inconsistencyMeasure(this.beliefSet);
		}
	}

	/**
	 *
	 * Handles the computation of the inconsistency value for a given belief set and inconsistency measure.
	 * This method takes an InconsistencyPost object representing the request payload and calculates
	 * the inconsistency value based on the specified belief set, inconsistency measure, and other parameters.
	 *
	 * The method initializes an InconsistencyValueResponse object for the response and sets up the necessary
	 * sub-solvers for the computation. It then retrieves the inconsistency measure and parser based on the request's
	 * attributes. The belief set is parsed from the knowledge base using the selected parser, and an asynchronous
	 * calculation of the inconsistency measure is performed using a separate thread. The execution time and result
	 * are then encapsulated in the InconsistencyValueResponse object.
	 *
	 * If the calculation times out, the method sets the response status to "TIMEOUT" and includes the specified
	 * timeout duration. If a parsing exception occurs during knowledge base parsing, the method throws a JSONException
	 * with an appropriate error message. If any other unexpected exception occurs, the method logs the error and
	 * throws a JSONException with a generic error message.
	 *
	 * The inconsistency value is set to specific sentinel values (-1 for timeout, -2 for general error, -3 for infinity),
	 * and the response object includes information about the request and the computed inconsistency value.
	 *
	 * @param query The InconsistencyPost object representing the request payload.
	 * @return An InconsistencyValueResponse object representing the response payload.
	 * @throws JSONException If there is a parsing error or an unexpected error occurs during processing.
	 */

	private InconsistencyValueResponse handleGetICMESValue(InconsistencyPost query) throws JSONException {
		InconsistencyValueResponse icmesResponse = new InconsistencyValueResponse();
		TimeUnit unit = Utils.getTimoutUnit(query.getUnit_timeout());
		int user_timeout = Utils.checkUserTimeout(query.getTimeout(), SERVICES_TIMEOUT_INCMES, unit);
		// set sub-solvers
		SatSolver.setDefaultSolver(new Sat4jSolver());
		PlMusEnumerator.setDefaultEnumerator(new NaiveMusEnumerator<PlFormula>(new Sat4jSolver()));
		Solver.setDefaultLinearSolver(new ApacheCommonsSimplex());
		Solver.setDefaultIntegerLinearSolver(new GlpkSolver());
		// get measure
		InconsistencyMeasure<BeliefSet<PlFormula, ?>> measure = InconsistencyMeasureFactory.getInconsistencyMeasure(
				Measure.getMeasure(query.getMeasure()));
		if (measure == null)
			throw new JSONException("Malformed JSON: unknown value for attribute \"measure\"");
		Parser<PlBeliefSet, PlFormula> parser = PlParserFactory.getParserForFormat(
				Format.getFormat(query.getFormat()));
		if (parser == null)
			throw new JSONException("Malformed JSON: unknown value for attribute \"format\"");
		double val = -3;
		try {
			PlBeliefSet beliefSet = parser.parseBeliefBase(query.getKb());
			ExecutorService executor = Executors.newSingleThreadExecutor();
			long millis = System.currentTimeMillis();
			try {
				// handle timeout
				Future<Double> future = executor.submit(new MeasurementCallee(measure, beliefSet));
				Pair<Double, Long> result = Utils.runServicesWithTimeout(future, user_timeout, unit);
				val = result.getKey();
				icmesResponse.setTime(result.getValue());
				icmesResponse.setStatus("SUCCESS");
				// val = future.get(InconsistencyMeasurementService.timeout, TimeUnit.SECONDS);
				executor.shutdownNow();
			} catch (TimeoutException e) {
				// inconsistency value of -1 indicates that a timeout has occurred
				icmesResponse.setTime(query.getTimeout());
				icmesResponse.setStatus("TIMEOUT");
				executor.shutdownNow();
				val = -1;
			} catch (Exception e) {
				// inconsistency value of -2 indicates some general error
				icmesResponse.setStatus("ERROR");
				executor.shutdownNow();
				val = -2;
			}
			// inconsistency value of -3 indicates infinity
			if (val == Double.POSITIVE_INFINITY)
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
		} catch (Exception e) {
			throw new JSONException("An unexpected error occured. Please contact an administrator.");
		}
	}

	/**
	 * Handles the "List inconsistency measures" command
	 *
	 * @param query some query
	 * @return the reply
	 * @throws JSONException                            if some JSON issue occurs.
	 * @throws org.codehaus.jettison.json.JSONException *description missing*
	 */
	private InconsistencyGetMeasuresResponse handleGetMeasures(InconsistencyPost query)
			throws JSONException, org.codehaus.jettison.json.JSONException {
		InconsistencyGetMeasuresResponse response = new InconsistencyGetMeasuresResponse();
		List<HashMap<String, String>> value = new LinkedList<HashMap<String, String>>();
		HashMap<String, String> jsonMes;
		for (Measure m : InconsistencyMeasureFactory.Measure.values()) {
			jsonMes = new HashMap<String, String>();
			jsonMes.put("id", m.id);
			jsonMes.put("label", m.label);
			value.add(jsonMes);
		}
		response.setMeasures(value);
		response.setEmail(query.getEmail());
		response.reply(query.getCmd());
		return response;
	}


	/**
	 * Handles the "List semanctics" command for aba
	 *
	 * @param query some query
	 * @return the reply
	 * @throws JSONException                            if some JSON issue occurs.
	 * @throws org.codehaus.jettison.json.JSONException *description missing*
	 */
	private AbaGetSemanticsResponse handleGetSemantics(AbaReasonerPost query)
			throws JSONException, org.codehaus.jettison.json.JSONException {

		LoggerUtil.logger.info(String.format("User: %s  Command: %s", query.getEmail(), query.getCmd()));
		AbaGetSemanticsResponse response = new AbaGetSemanticsResponse();
		List<HashMap<String, String>> value = new LinkedList<HashMap<String, String>>();
		HashMap<String, String> jsonMes;
		for (org.tweetyproject.web.services.aba.GeneralAbaReasonerFactory.Semantics m : GeneralAbaReasonerFactory.Semantics.values()) {
			jsonMes = new HashMap<String, String>();
			jsonMes.put("id", m.id);
			jsonMes.put("label", m.label);
			value.add(jsonMes);
		}
		response.setSemantics(value);
		response.setEmail(query.getEmail());
		response.reply(query.getCmd());
		return response;
	}


}
