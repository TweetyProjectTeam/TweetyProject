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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.tweetyproject.arg.aba.parser.AbaParser;
import org.tweetyproject.arg.aba.reasoner.GeneralAbaReasoner;
import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.arg.adf.reasoner.AbstractADFReasoner;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.bipolar.reasoner.AbstractBipolarExtensionReasoner;
import org.tweetyproject.arg.bipolar.syntax.BipolarArgumentationFramework;
import org.tweetyproject.arg.delp.parser.DelpParser;
import org.tweetyproject.arg.delp.reasoner.DelpReasoner;
import org.tweetyproject.arg.delp.semantics.ComparisonCriterion;
import org.tweetyproject.arg.delp.semantics.DelpAnswer;
import org.tweetyproject.arg.delp.semantics.EmptyCriterion;
import org.tweetyproject.arg.delp.semantics.GeneralizedSpecificity;
import org.tweetyproject.arg.delp.syntax.DefeasibleLogicProgram;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.IncompleteReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.syntax.SelectionFunction;
import org.tweetyproject.arg.dung.serialisability.syntax.TerminationFunction;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.IncompleteTheory;
import org.tweetyproject.arg.prob.reasoner.AbstractPafReasoner;
import org.tweetyproject.arg.prob.syntax.ProbabilisticArgumentationFramework;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.arg.rankings.semantics.RankingSemantics;
import org.tweetyproject.arg.setaf.reasoners.AbstractSetAfExtensionReasoner;
import org.tweetyproject.arg.setaf.syntax.SetAf;
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
import org.tweetyproject.web.services.adf.*;
import org.tweetyproject.web.services.bipolar.*;
import org.tweetyproject.web.services.delp.DeLPCallee;
import org.tweetyproject.web.services.delp.DeLPPost;
import org.tweetyproject.web.services.delp.DeLPResponse;
import org.tweetyproject.web.services.dung.*;
import org.tweetyproject.web.services.dung.DungReasonerCalleeFactory.Command;
import org.tweetyproject.web.services.iaf.*;
import org.tweetyproject.web.services.incmes.InconsistencyGetMeasuresResponse;
import org.tweetyproject.web.services.incmes.InconsistencyPost;
import org.tweetyproject.web.services.incmes.InconsistencyValueResponse;
import org.tweetyproject.web.services.paf.*;
import org.tweetyproject.web.services.rankings.*;
import org.tweetyproject.web.services.sequenceexplanation.*;
import org.tweetyproject.web.services.sequenceexplanation.SequenceExplanationPost.GetSequenceExplanationsCmd;
import org.tweetyproject.web.services.sequenceexplanation.SequenceExplanationPost.SequenceExplanationCmd;
import org.tweetyproject.web.services.sequenceexplanation.SequenceExplanationResponse.GetSequenceExplanationsResult;
import org.tweetyproject.web.services.sequenceexplanation.SequenceExplanationResponse.SequenceExplanationResult;
import org.tweetyproject.web.services.serialisation.*;

import javafx.util.Pair;
import org.tweetyproject.web.services.setaf.*;

import javax.validation.Valid;


/**
 * Handles HTTP POST requests for all TweetyProject web service endpoints.
 */
@RestController
public class RequestController {

	private final int SERVICES_TIMEOUT_DUNG = 600;
	private final int SERVICES_TIMEOUT_DELP = 600;
	private final int SERVICES_TIMEOUT_INCMES = 300;
	private final int SERVICES_TIMEOUT_SEQUENCE_EXPLANATION = 300;

	private final SequenceExplanationService sequenceExplanationService;

	@Autowired
	public RequestController(SequenceExplanationService sequenceExplanationService) {
		this.sequenceExplanationService = sequenceExplanationService;
	}

	/** Holds the outcome of a timed callee execution. */
	private static final class ExecutionResult {
		final String answer;
		final double time;
		final String status;

		ExecutionResult(String answer, double time, String status) {
			this.answer = answer;
			this.time = time;
			this.status = status;
		}
	}

	/**
	 * Submits {@code callee} to a single-threaded executor, waits up to {@code userTimeout},
	 * and returns an {@link ExecutionResult} with answer/time/status filled in.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static ExecutionResult runCallee(Callee callee, int userTimeout, double requestTimeout, TimeUnit unit) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			Future<Object> future = executor.submit((Callable<Object>) callee);
			Pair<Object, Long> result = Utils.runServicesWithTimeout(future, userTimeout, unit);
			executor.shutdownNow();
			return new ExecutionResult(result.getKey().toString(), (double) result.getValue(), "SUCCESS");
		} catch (TimeoutException e) {
			executor.shutdownNow();
			return new ExecutionResult(null, requestTimeout, "TIMEOUT");
		} catch (Exception e) {
			executor.shutdownNow();
			return new ExecutionResult(null, 0.0, "Error");
		}
	}

	// ── ABA ──────────────────────────────────────────────────────────────────

	@PostMapping(value = "/aba", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(@RequestBody AbaReasonerPost post)
			throws ParserException, IOException, JSONException, org.codehaus.jettison.json.JSONException {

		LoggerUtil.logger.info(String.format("User: %s  Command: %s", post.getEmail(), post.getCmd()));
		if (post.getCmd().equals("semantics"))
			return handleGetSemantics(post);

		SatSolver.setDefaultSolver(new Sat4jSolver());
		Callee callee = null;

		if (post.getKb_format().equals("pl")) {
			AbaParser<PlFormula> parser = new AbaParser<>(new PlParser());
			Assumption<PlFormula> assumption = new Assumption<>(new Proposition(post.getQuery_assumption()));
			AbaTheory<PlFormula> theory = null;
			try {
				theory = parser.parseBeliefBase(post.getKb());
			} catch (ParserException | IOException e) {
				LoggerUtil.logger.log(Level.SEVERE, String.format("Error while parsing the Beliefbase: %s", e.getClass().getSimpleName()));
			}
			GeneralAbaReasoner<PlFormula> reasoner = GeneralAbaReasonerFactory.getReasoner(
					GeneralAbaReasonerFactory.Semantics.getSemantics(post.getSemantics()));
			callee = AbaReasonerCalleeFactory.getCallee(
					AbaReasonerCalleeFactory.Command.getCommand(post.getCmd()), reasoner, theory, assumption);
		}

		if (post.getKb_format().equals("fol")) {
			FolParser folParser = new FolParser();
			FolSignature sig = folParser.parseSignature(post.getFol_signature());
			folParser.setSignature(sig);
			AbaParser<FolFormula> parser = new AbaParser<>(folParser);
			parser.setSymbolComma(";");
			Assumption<FolFormula> assumption = new Assumption<>(folParser.parseFormula(post.getQuery_assumption()));
			AbaTheory<FolFormula> theory = null;
			try {
				theory = parser.parseBeliefBase(post.getKb());
			} catch (ParserException | IOException e) {
				LoggerUtil.logger.log(Level.SEVERE, String.format("Error while parsing the Beliefbase: %s", e.getClass().getSimpleName()));
			}
			GeneralAbaReasoner<FolFormula> reasoner = GeneralAbaReasonerFactory.getReasoner(
					GeneralAbaReasonerFactory.Semantics.getSemantics(post.getSemantics()));
			try {
				callee = AbaReasonerCalleeFactory.getCallee(
						AbaReasonerCalleeFactory.Command.getCommand(post.getCmd()), reasoner, theory, assumption);
			} catch (Exception e) {
				LoggerUtil.logger.log(Level.SEVERE, String.format("Error while creating ABAReasonerCallee: %s", e.getClass().getSimpleName()));
			}
		}

		AbaReasonerResponse response = new AbaReasonerResponse(post.getCmd(), post.getEmail(), post.getKb(),
				post.getKb_format(), post.getFol_signature(), post.getQuery_assumption(),
				post.getSemantics(), post.getTimeout(), "", 0.0, post.getUnit_timeout(), "");

		if (!post.getCmd().equals("get_models") && !post.getCmd().equals("get_model") && !post.getCmd().equals("query")) {
			LoggerUtil.logger.log(Level.SEVERE, String.format("Command \"%s\" not found.", post.getCmd()));
			response.setAnswer("Command not found");
			response.setStatus("ERROR");
			return response;
		}

		TimeUnit unit = Utils.getTimeoutUnit(post.getUnit_timeout());
		int userTimeout = Utils.checkUserTimeout(post.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);
		LoggerUtil.logger.info(String.format("Run command \"%s\" with timeout: %s %s", post.getCmd(), userTimeout, post.getUnit_timeout()));

		ExecutionResult r = runCallee(callee, userTimeout, post.getTimeout(), unit);
		response.setAnswer(r.answer);
		response.setTime(r.time);
		response.setStatus(r.status);
		LoggerUtil.logger.info(String.format("Command \"%s\" finished with status: %s", post.getCmd(), r.status));
		return response;
	}

	// ── Dung ─────────────────────────────────────────────────────────────────

	@PostMapping(value = "/dung", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(@RequestBody DungReasonerPost post) {
		if (post.getCmd().equals("info"))
			return getInfo(post);

		Command cmd = Command.getCommand(post.getCmd());
		if (cmd == null)
			return new DungReasonerResponse();

		DungTheory theory = AbstractExtensionReasonerFactory.getDungTheory(post.getNr_of_arguments(), post.getAttacks());
		AbstractExtensionReasoner reasoner = AbstractExtensionReasonerFactory.getReasoner(Semantics.getSemantics(post.getSemantics()));
		Callee callee = DungReasonerCalleeFactory.getCallee(cmd, reasoner, theory);
		TimeUnit unit = Utils.getTimeoutUnit(post.getUnit_timeout());
		int userTimeout = Utils.checkUserTimeout(post.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);

		DungReasonerResponse response = new DungReasonerResponse(post.getCmd(), post.getEmail(),
				post.getNr_of_arguments(), post.getAttacks(), post.getSemantics(), post.getSolver(),
				null, 0, post.getUnit_timeout(), "ERROR");
		ExecutionResult r = runCallee(callee, userTimeout, post.getTimeout(), unit);
		response.setAnswer(r.answer);
		response.setTime(r.time);
		response.setStatus(r.status);
		return response;
	}

	@PostMapping(value = "/info", produces = "application/json")
	@ResponseBody
	public DungServicesInfoResponse getInfo(@RequestBody DungReasonerPost post) {
		DungServicesInfoResponse response = new DungServicesInfoResponse();
		response.setReply("info");
		response.setEmail(post.getEmail());
		response.setBackend_timeout(SERVICES_TIMEOUT_DUNG);
		ArrayList<String> semantics_ids = new ArrayList<>();
		for (Semantics s : AbstractExtensionReasonerFactory.getSemantics())
			semantics_ids.add(s.abbreviation());
		response.setSemantics(semantics_ids);
		ArrayList<String> command_ids = new ArrayList<>();
		for (Command c : DungReasonerCalleeFactory.getCommands())
			command_ids.add(c.id);
		response.setCommands(command_ids);
		return response;
	}

	// ── IAF ──────────────────────────────────────────────────────────────────

	@PostMapping(value = "/iaf", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(@RequestBody IafReasonerPost post) {
		if (post.getCmd().equals("info"))
			return getIafInfo(post.getEmail());

		IafReasonerCalleeFactory.Command cmd = IafReasonerCalleeFactory.Command.getCommand(post.getCmd());
		if (cmd == null)
			return new IafReasonerResponse();

		IncompleteTheory theory = IafReasonerFactory.getIncompleteTheory(post.getNr_of_arguments(),
				post.getUncertainArguments(), post.getDefiniteAttacks(), post.getUncertainAttacks());
		IncompleteReasoner reasoner = IafReasonerFactory.getReasoner(Semantics.getSemantics(post.getSemantics()));
		Callee callee = IafReasonerCalleeFactory.getCallee(cmd, reasoner, theory);
		TimeUnit unit = Utils.getTimeoutUnit(post.getUnit_timeout());
		int userTimeout = Utils.checkUserTimeout(post.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);

		IafReasonerResponse response = new IafReasonerResponse(post.getCmd(), post.getEmail(),
				post.getNr_of_arguments(), post.getUncertainArguments(), post.getDefiniteAttacks(),
				post.getUncertainAttacks(), post.getSemantics(), post.getSolver(),
				null, 0, post.getUnit_timeout(), "ERROR");
		ExecutionResult r = runCallee(callee, userTimeout, post.getTimeout(), unit);
		response.setAnswer(r.answer);
		response.setTime(r.time);
		response.setStatus(r.status);
		return response;
	}

	private IafServicesInfoResponse getIafInfo(String email) {
		IafServicesInfoResponse response = new IafServicesInfoResponse();
		response.setReply("info");
		response.setEmail(email);
		response.setBackend_timeout(SERVICES_TIMEOUT_DUNG);
		ArrayList<String> semantics_ids = new ArrayList<>();
		for (var s : IafReasonerFactory.getSemantics())
			semantics_ids.add(s.abbreviation());
		response.setSemantics(semantics_ids);
		ArrayList<String> command_ids = new ArrayList<>();
		for (var c : IafReasonerCalleeFactory.getCommands())
			command_ids.add(c.id);
		response.setCommands(command_ids);
		return response;
	}

	// ── Rankings ─────────────────────────────────────────────────────────────

	@PostMapping(value = "/rankings", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(@RequestBody RankingReasonerPost post) {
		if (post.getCmd().equals("info"))
			return getRankingInfo(post.getEmail());

		RankingReasonerCalleeFactory.Command cmd = RankingReasonerCalleeFactory.Command.getCommand(post.getCmd());
		if (cmd == null)
			return new RankingReasonerResponse();

		DungTheory theory = AbstractExtensionReasonerFactory.getDungTheory(post.getNr_of_arguments(), post.getAttacks());
		AbstractRankingReasoner<?> reasoner = AbstractRankingReasonerFactory.getReasoner(RankingSemantics.getSemantics(post.getSemantics()));
		Callee callee = RankingReasonerCalleeFactory.getCallee(cmd, reasoner, theory);
		TimeUnit unit = Utils.getTimeoutUnit(post.getUnit_timeout());
		int userTimeout = Utils.checkUserTimeout(post.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);

		RankingReasonerResponse response = new RankingReasonerResponse(post.getCmd(), post.getEmail(),
				post.getNr_of_arguments(), post.getAttacks(), post.getSemantics(),
				AbstractRankingReasonerFactory.getRankingType(post.getSemantics()),
				post.getSolver(), null, 0, post.getUnit_timeout(), "ERROR");
		ExecutionResult r = runCallee(callee, userTimeout, post.getTimeout(), unit);
		response.setAnswer(r.answer);
		response.setTime(r.time);
		response.setStatus(r.status);
		return response;
	}

	private RankingServicesInfoResponse getRankingInfo(String email) {
		RankingServicesInfoResponse response = new RankingServicesInfoResponse();
		response.setReply("info");
		response.setEmail(email);
		response.setBackend_timeout(SERVICES_TIMEOUT_DUNG);
		ArrayList<String> semantics_ids = new ArrayList<>();
		for (var s : AbstractRankingReasonerFactory.getSemantics())
			semantics_ids.add(s.getId());
		response.setSemantics(semantics_ids);
		ArrayList<String> command_ids = new ArrayList<>();
		for (var c : RankingReasonerCalleeFactory.getCommands())
			command_ids.add(c.id);
		response.setCommands(command_ids);
		return response;
	}

	// ── ADF ──────────────────────────────────────────────────────────────────

	@PostMapping(value = "/adf", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(@RequestBody AdfReasonerPost post) {
		if (post.getCmd().equals("info"))
			return getAdfInfo(post.getEmail());

		AdfReasonerCalleeFactory.Command cmd = AdfReasonerCalleeFactory.Command.getCommand(post.getCmd());
		if (cmd == null)
			return new AdfReasonerResponse();

		AbstractDialecticalFramework adf = AbstractAdfReasonerFactory.getAdf(post.getNr_of_arguments(), post.getConditions());
		AbstractADFReasoner reasoner = AbstractAdfReasonerFactory.getReasoner(Semantics.getSemantics(post.getSemantics()));
		Callee callee = AdfReasonerCalleeFactory.getCallee(cmd, reasoner, adf);
		TimeUnit unit = Utils.getTimeoutUnit(post.getUnit_timeout());
		int userTimeout = Utils.checkUserTimeout(post.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);

		AdfReasonerResponse response = new AdfReasonerResponse(post.getCmd(), post.getEmail(),
				post.getNr_of_arguments(), post.getConditions(), post.getSemantics(), post.getSolver(),
				null, 0, post.getUnit_timeout(), "ERROR");
		ExecutionResult r = runCallee(callee, userTimeout, post.getTimeout(), unit);
		// ADF uses curly braces instead of square brackets in the answer format
		response.setAnswer(r.answer != null ? r.answer.replace("[", "{").replace("]", "}") : null);
		response.setTime(r.time);
		response.setStatus(r.status);
		return response;
	}

	private AdfServicesInfoResponse getAdfInfo(String email) {
		AdfServicesInfoResponse response = new AdfServicesInfoResponse();
		response.setReply("info");
		response.setEmail(email);
		response.setBackend_timeout(SERVICES_TIMEOUT_DUNG);
		ArrayList<String> semantics_ids = new ArrayList<>();
		for (var s : AbstractAdfReasonerFactory.getSemantics())
			semantics_ids.add(s.abbreviation());
		response.setSemantics(semantics_ids);
		ArrayList<String> command_ids = new ArrayList<>();
		for (var c : AdfReasonerCalleeFactory.getCommands())
			command_ids.add(c.id);
		response.setCommands(command_ids);
		return response;
	}

	// ── Bipolar ──────────────────────────────────────────────────────────────

	@PostMapping(value = "/bipolar", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(@RequestBody BipolarReasonerPost post) {
		if (post.getCmd().equals("info"))
			return getBipolarInfo(post.getEmail());

		BipolarReasonerCalleeFactory.Command cmd = BipolarReasonerCalleeFactory.Command.getCommand(post.getCmd());
		if (cmd == null)
			return new BipolarReasonerResponse();

		BipolarArgumentationFramework bbase = AbstractBipolarFrameworkFactory.getArgumentationFramework(
				post.getNr_of_arguments(), post.getAttacks(), post.getSupports());
		AbstractBipolarExtensionReasoner reasoner = AbstractBipolarExtensionReasonerFactory.getReasoner(
				BipolarSemantics.getSemantics(post.getSemantics()));
		Callee callee = BipolarReasonerCalleeFactory.getCallee(cmd, reasoner, bbase);
		TimeUnit unit = Utils.getTimeoutUnit(post.getUnit_timeout());
		int userTimeout = Utils.checkUserTimeout(post.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);

		BipolarReasonerResponse response = new BipolarReasonerResponse(post.getCmd(), post.getEmail(),
				post.getNr_of_arguments(), post.getAttacks(), post.getSupports(), post.getSemantics(),
				post.getSolver(), null, 0, post.getUnit_timeout(), "ERROR");
		ExecutionResult r = runCallee(callee, userTimeout, post.getTimeout(), unit);
		response.setAnswer(r.answer);
		response.setTime(r.time);
		response.setStatus(r.status);
		return response;
	}

	private BipolarServicesInfoResponse getBipolarInfo(String email) {
		BipolarServicesInfoResponse response = new BipolarServicesInfoResponse();
		response.setReply("info");
		response.setEmail(email);
		response.setBackend_timeout(SERVICES_TIMEOUT_DUNG);
		ArrayList<String> semantics_ids = new ArrayList<>();
		for (var s : AbstractBipolarExtensionReasonerFactory.getSemantics())
			semantics_ids.add(s.id);
		response.setSemantics(semantics_ids);
		ArrayList<String> command_ids = new ArrayList<>();
		for (var c : BipolarReasonerCalleeFactory.getCommands())
			command_ids.add(c.id);
		response.setCommands(command_ids);
		return response;
	}

	// ── DeLP ─────────────────────────────────────────────────────────────────

	@PostMapping(value = "/delp", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(@RequestBody DeLPPost post) {
		DeLPResponse response = new DeLPResponse("query", post.getEmail(), post.getCompcriterion(),
				post.getKb(), post.getQuery(), post.getTimeout(), null, 0.0, post.getUnit_timeout(), null);
		TimeUnit unit = Utils.getTimeoutUnit(post.getUnit_timeout());
		int userTimeout = Utils.checkUserTimeout(post.getTimeout(), SERVICES_TIMEOUT_DELP, unit);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			DelpParser parser = new DelpParser();
			DefeasibleLogicProgram delp = parser.parseBeliefBase(post.getKb());
			ComparisonCriterion comp = null;
			if (post.getCompcriterion().equals("compcriterion"))
				comp = new EmptyCriterion();
			if (post.getCompcriterion().equals("genspec"))
				comp = new GeneralizedSpecificity();
			if (comp == null)
				throw new JSONException("Malformed JSON: unknown value for attribute \"compcriterion\"");
			DelpReasoner reasoner = new DelpReasoner(comp);
			FolParser folParser = new FolParser();
			folParser.setSignature(parser.getSignature());
			String qString = post.getQuery().trim();
			Formula f = qString.startsWith("~")
					? new Negation((FolFormula) folParser.parseFormula(qString.substring(1)))
					: folParser.parseFormula(qString);
			Future<DelpAnswer.Type> future = executor.submit(new DeLPCallee(delp, reasoner, f));
			Pair<DelpAnswer.Type, Long> result = Utils.runServicesWithTimeout(future, userTimeout, unit);
			response.setAnswer(result.getKey().toString());
			response.setTime(result.getValue());
			response.setStatus("SUCCESS");
		} catch (TimeoutException e) {
			response.setTime(post.getTimeout());
			response.setAnswer(null);
			response.setStatus("TIMEOUT");
		} catch (ParserException | IOException e) {
			throw new JSONException("Malformed JSON: syntax of knowledge base and/or query does not conform to the given format.");
		} catch (Exception e) {
			throw new JSONException("An unexpected error occured. Please contact an administrator.");
		} finally {
			executor.shutdownNow();
		}
		return response;
	}

	// ── Inconsistency Measures ────────────────────────────────────────────────

	@PostMapping(value = "/incmes", produces = "application/json")
	@ResponseBody
	public Response handleRequest(@RequestBody InconsistencyPost post) {
		try {
			if (post.getCmd().equals("value"))
				return handleGetICMESValue(post);
			if (post.getCmd().equals("measures"))
				return handleGetMeasures(post);
		} catch (Exception e) {
			LoggerUtil.logger.log(Level.SEVERE, e.getMessage());
		}
		return new InconsistencyValueResponse();
	}

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

	private InconsistencyValueResponse handleGetICMESValue(InconsistencyPost query) throws JSONException {
		InconsistencyValueResponse response = new InconsistencyValueResponse();
		TimeUnit unit = Utils.getTimeoutUnit(query.getUnit_timeout());
		int userTimeout = Utils.checkUserTimeout(query.getTimeout(), SERVICES_TIMEOUT_INCMES, unit);
		SatSolver.setDefaultSolver(new Sat4jSolver());
		PlMusEnumerator.setDefaultEnumerator(new NaiveMusEnumerator<PlFormula>(new Sat4jSolver()));
		Solver.setDefaultLinearSolver(new ApacheCommonsSimplex());
		Solver.setDefaultIntegerLinearSolver(new GlpkSolver());
		InconsistencyMeasure<BeliefSet<PlFormula, ?>> measure = InconsistencyMeasureFactory.getInconsistencyMeasure(
				Measure.getMeasure(query.getMeasure()));
		if (measure == null)
			throw new JSONException("Malformed JSON: unknown value for attribute \"measure\"");
		Parser<PlBeliefSet, PlFormula> parser = PlParserFactory.getParserForFormat(Format.getFormat(query.getFormat()));
		if (parser == null)
			throw new JSONException("Malformed JSON: unknown value for attribute \"format\"");
		double val = -3;
		try {
			PlBeliefSet beliefSet = parser.parseBeliefBase(query.getKb());
			ExecutorService executor = Executors.newSingleThreadExecutor();
			try {
				Future<Double> future = executor.submit(new MeasurementCallee(measure, beliefSet));
				Pair<Double, Long> result = Utils.runServicesWithTimeout(future, userTimeout, unit);
				val = result.getKey();
				response.setTime(result.getValue());
				response.setStatus("SUCCESS");
				executor.shutdownNow();
			} catch (TimeoutException e) {
				response.setTime(query.getTimeout());
				response.setStatus("TIMEOUT");
				executor.shutdownNow();
				val = -1;
			} catch (Exception e) {
				response.setStatus("ERROR");
				executor.shutdownNow();
				val = -2;
			}
			if (val == Double.POSITIVE_INFINITY)
				val = -3;
			response.setEmail(query.getEmail());
			response.setFormat(query.getFormat());
			response.setKb(query.getKb());
			response.setMeasure(query.getMeasure());
			response.setReply("value");
			response.setValue(val);
			return response;
		} catch (ParserException | IOException e) {
			throw new JSONException("Malformed JSON: syntax of knowledge base does not conform to the given format.");
		} catch (Exception e) {
			throw new JSONException("An unexpected error occured. Please contact an administrator.");
		}
	}

	private InconsistencyGetMeasuresResponse handleGetMeasures(InconsistencyPost query)
			throws JSONException, org.codehaus.jettison.json.JSONException {
		InconsistencyGetMeasuresResponse response = new InconsistencyGetMeasuresResponse();
		List<HashMap<String, String>> value = new LinkedList<>();
		for (Measure m : InconsistencyMeasureFactory.Measure.values()) {
			HashMap<String, String> jsonMes = new HashMap<>();
			jsonMes.put("id", m.id);
			jsonMes.put("label", m.label);
			value.add(jsonMes);
		}
		response.setMeasures(value);
		response.setEmail(query.getEmail());
		response.reply(query.getCmd());
		return response;
	}

	private AbaGetSemanticsResponse handleGetSemantics(AbaReasonerPost query)
			throws JSONException, org.codehaus.jettison.json.JSONException {
		LoggerUtil.logger.info(String.format("User: %s  Command: %s", query.getEmail(), query.getCmd()));
		AbaGetSemanticsResponse response = new AbaGetSemanticsResponse();
		List<HashMap<String, String>> value = new LinkedList<>();
		for (GeneralAbaReasonerFactory.Semantics m : GeneralAbaReasonerFactory.Semantics.values()) {
			HashMap<String, String> jsonMes = new HashMap<>();
			jsonMes.put("id", m.id);
			jsonMes.put("label", m.label);
			value.add(jsonMes);
		}
		response.setSemantics(value);
		response.setEmail(query.getEmail());
		response.reply(query.getCmd());
		return response;
	}

	// ── Sequence Explanation ──────────────────────────────────────────────────

	@PostMapping(value = "/sequence-explanation", produces = "application/json")
	@ResponseBody
	public SequenceExplanationResponse handleRequest(@Valid @RequestBody SequenceExplanationPost request) {
		LoggerUtil.logger.info(String.format("Run sequence explanation command \"%s\" for user \"%s\" with timeout: %s %s",
				request.getCmd().getClass().getSimpleName(), request.getEmail(),
				request.getTimeout(), request.getUnit_timeout()));

		TimeUnit timeoutUnit = Utils.getTimeoutUnit(request.getUnit_timeout());
		int timeout = Utils.checkUserTimeout(request.getTimeout(), SERVICES_TIMEOUT_SEQUENCE_EXPLANATION, timeoutUnit);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Pair<SequenceExplanationResult, Long> resultAndTime;
		try {
			var future = executor.submit(() -> processCommand(request.getCmd()));
			resultAndTime = Utils.runServicesWithTimeout(future, timeout, timeoutUnit);
		} catch (TimeoutException e) {
			LoggerUtil.logger.info("Timeout while running sequence explanation.");
			return new SequenceExplanationResponse(null, request.getEmail(), timeout,
					request.getUnit_timeout(), SequenceExplanationResponse.Status.TIMEOUT);
		} catch (ExecutionException e) {
			LoggerUtil.logger.warning(() -> "Error while running sequence explanation reasoner: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			LoggerUtil.logger.warning(() -> "Interrupt while running sequence explanation: " + e.getMessage());
			e.printStackTrace();
			Thread.currentThread().interrupt();
			throw new RuntimeException("Thread was interrupted.");
		} finally {
			executor.shutdownNow();
		}
		return new SequenceExplanationResponse(resultAndTime.getKey(), request.getEmail(),
				resultAndTime.getValue(), request.getUnit_timeout(), SequenceExplanationResponse.Status.SUCCESS);
	}

	private SequenceExplanationResult processCommand(SequenceExplanationCmd cmd) {
		if (cmd instanceof GetSequenceExplanationsCmd)
			return processSequenceExplanationCmd((GetSequenceExplanationsCmd) cmd);
		throw new IllegalStateException("Encountered invalid command:" + cmd.getClass().getSimpleName());
	}

	private GetSequenceExplanationsResult processSequenceExplanationCmd(GetSequenceExplanationsCmd cmd) {
		var theory = new DungTheory();
		for (AttackDTO attackDTO : cmd.getAttacks()) {
			var attacker = new Argument(attackDTO.getAttacker());
			var attacked = new Argument(attackDTO.getAttacked());
			theory.add(attacker);
			theory.add(attacked);
			theory.add(new Attack(attacker, attacked));
		}
		Set<Argument> argumentFilter = ArgumentFilterSerialization.deserialize(cmd.getArgumentFilter());
		if (argumentFilter != null)
			theory.addAll(argumentFilter);
		return GetSequenceExplanationsResult.from(sequenceExplanationService.querySequenceExplanations(theory, argumentFilter));
	}

	// ── PAF ──────────────────────────────────────────────────────────────────

	@PostMapping(value = "/paf", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(@RequestBody PafReasonerPost post) {
		if (post.getCmd().equals("info"))
			return getPafInfo(post.getEmail());

		PafReasonerCalleeFactory.Command cmd = PafReasonerCalleeFactory.Command.getCommand(post.getCmd());
		if (cmd == null)
			return new PafReasonerResponse();

		ProbabilisticArgumentationFramework paf = AbstractPafReasonerFactory.getPaf(
				post.getNr_of_arguments(), post.getArgument_probabilities(),
				post.getAttacks(), post.getAttack_probabilities());
		AbstractPafReasoner reasoner = AbstractPafReasonerFactory.getReasoner(
				Semantics.getSemantics(post.getSemantics()), post.getSolver(), post.getNr_of_trials());
		Callee callee = PafReasonerCalleeFactory.getCallee(cmd, reasoner, paf);
		TimeUnit unit = Utils.getTimeoutUnit(post.getUnit_timeout());
		int userTimeout = Utils.checkUserTimeout(post.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);

		PafReasonerResponse response = new PafReasonerResponse(post.getCmd(), post.getEmail(),
				post.getNr_of_arguments(), post.getArgument_probabilities(), post.getAttacks(),
				post.getAttack_probabilities(), post.getSemantics(), post.getSolver(),
				post.getArgument(), null, 0, post.getUnit_timeout(), "ERROR");
		ExecutionResult r = runCallee(callee, userTimeout, post.getTimeout(), unit);
		response.setAnswer(r.answer);
		response.setTime(r.time);
		response.setStatus(r.status);
		return response;
	}

	private PafServicesInfoResponse getPafInfo(String email) {
		PafServicesInfoResponse response = new PafServicesInfoResponse();
		response.setReply("info");
		response.setEmail(email);
		response.setBackend_timeout(SERVICES_TIMEOUT_DUNG);
		ArrayList<String> semantics_ids = new ArrayList<>();
		for (Semantics s : AbstractPafReasonerFactory.getSemantics())
			semantics_ids.add(s.abbreviation());
		response.setSemantics(semantics_ids);
		ArrayList<String> command_ids = new ArrayList<>();
		for (PafReasonerCalleeFactory.Command c : PafReasonerCalleeFactory.getCommands())
			command_ids.add(c.id);
		response.setCommands(command_ids);
		response.setSolvers(AbstractPafReasonerFactory.getSolvers());
		return response;
	}

	// ── Serialisation ─────────────────────────────────────────────────────────

	@PostMapping(value = "/serialisation", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(@RequestBody SerialisationPost post) {
		if (post.getCmd().equals("info"))
			return getSerInfo(post.getEmail());

		SerialisationCalleeFactory.Command cmd = SerialisationCalleeFactory.Command.getCommand(post.getCmd());
		if (cmd == null)
			return new SerialisationResponse();

		DungTheory theory = AbstractExtensionReasonerFactory.getDungTheory(post.getNr_of_arguments(), post.getAttacks());
		Extension<DungTheory> extension = SerialisationFactory.getExtension(post.getExtension());
		SelectionFunction alpha = SerialisationFactory.getSelectionFunction(post.getSelectionFunction());
		TerminationFunction beta = SerialisationFactory.getTerminationFunction(post.getTerminationFunction());
		Callee callee = SerialisationCalleeFactory.getCallee(cmd, alpha, beta, theory, extension);
		TimeUnit unit = Utils.getTimeoutUnit(post.getUnit_timeout());
		int userTimeout = Utils.checkUserTimeout(post.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);

		SerialisationResponse response = new SerialisationResponse(post.getCmd(), post.getEmail(),
				post.getNr_of_arguments(), post.getAttacks(), post.getExtension(),
				post.getSelectionFunction(), post.getTerminationFunction(), null, 0,
				post.getUnit_timeout(), "ERROR");
		ExecutionResult r = runCallee(callee, userTimeout, post.getTimeout(), unit);
		response.setAnswer(r.answer);
		response.setTime(r.time);
		response.setStatus(r.status);
		return response;
	}

	private SerialisationInfoResponse getSerInfo(String email) {
		SerialisationInfoResponse response = new SerialisationInfoResponse();
		response.setReply("info");
		response.setEmail(email);
		response.setBackend_timeout(SERVICES_TIMEOUT_DUNG);
		response.setSelectionFunctions(SerialisationFactory.getSelectionFunctions());
		response.setTerminationFunctions(SerialisationFactory.getTerminationFunction());
		ArrayList<String> command_ids = new ArrayList<>();
		for (SerialisationCalleeFactory.Command c : SerialisationCalleeFactory.getCommands())
			command_ids.add(c.id);
		response.setCommands(command_ids);
		return response;
	}

	// ── Serialisation ─────────────────────────────────────────────────────────

	@PostMapping(value = "/setaf", produces = "application/json", consumes = "application/json")
	@ResponseBody
	public Response handleRequest(@RequestBody SetAfReasonerPost post) {
		if (post.getCmd().equals("info"))
			return getSetAfInfo(post.getEmail());

		SetAfReasonerCalleeFactory.Command cmd = SetAfReasonerCalleeFactory.Command.getCommand(post.getCmd());
		if (cmd == null)
			return new SetAfReasonerResponse();

		SetAf theory = AbstractSetAfFactory.getSetAf(post.getNr_of_arguments(), post.getAttacks());
		AbstractSetAfExtensionReasoner reasoner = AbstractSetAfFactory.getReasoner(Semantics.getSemantics(post.getSemantics()));
		Callee callee = SetAfReasonerCalleeFactory.getCallee(cmd, reasoner, theory);
		TimeUnit unit = Utils.getTimeoutUnit(post.getUnit_timeout());
		int userTimeout = Utils.checkUserTimeout(post.getTimeout(), SERVICES_TIMEOUT_DUNG, unit);

		SetAfReasonerResponse response = new SetAfReasonerResponse(post.getCmd(), post.getEmail(),
				post.getNr_of_arguments(), post.getAttacks(), post.getSemantics(), post.getSolver(),
				null, 0, post.getUnit_timeout(), "ERROR");
		ExecutionResult r = runCallee(callee, userTimeout, post.getTimeout(), unit);
		response.setAnswer(r.answer);
		response.setTime(r.time);
		response.setStatus(r.status);
		return response;
	}

	private SetAfServicesInfoResponse getSetAfInfo(String email) {
		SetAfServicesInfoResponse response = new SetAfServicesInfoResponse();
		response.setReply("info");
		response.setEmail(email);
		response.setBackend_timeout(SERVICES_TIMEOUT_DUNG);
		ArrayList<String> command_ids = new ArrayList<>();
		for (SetAfReasonerCalleeFactory.Command c : SetAfReasonerCalleeFactory.getCommands())
			command_ids.add(c.id);
		response.setCommands(command_ids);
		ArrayList<String> semantics_ids = new ArrayList<>();
		for (Semantics s : AbstractSetAfFactory.getSemantics())
			semantics_ids.add(s.abbreviation());
		response.setSemantics(semantics_ids);
		return response;
	}

	// ── Ping ─────────────────────────────────────────────────────────────────

	@PostMapping(value = "/ping", produces = "application/json")
	@ResponseBody
	public Ping ping(@RequestBody Ping ping_Greeting) {
		return ping_Greeting;
	}
}
