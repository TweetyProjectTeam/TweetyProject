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
package org.tweetyproject.web.services.causal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.tweetyproject.causal.parser.CausalParser;
import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.web.services.LoggerUtil;
import org.tweetyproject.web.services.Utils;

import javafx.util.Pair;

import javax.validation.Valid;

import static org.tweetyproject.web.services.causal.CausalReasonerResponse.Status.SUCCESS;
import static org.tweetyproject.web.services.causal.CausalReasonerResponse.Status.TIMEOUT;

/**
 * Handles HTTP POST requests for the {@code /causal} endpoint.
 */
@RestController
public class CausalController {

	private static final int TIMEOUT_SECONDS = 300;

	private final ObjectMapper objectMapper;
	private final CausalReasonerService causalReasonerService;

	@Autowired
	public CausalController(ObjectMapper objectMapper, CausalReasonerService causalReasonerService) {
		this.objectMapper = objectMapper;
		this.causalReasonerService = causalReasonerService;
	}

	@PostMapping(value = "/causal", produces = "application/json")
	@ResponseBody
	public CausalReasonerResponse handleRequest(@Valid @RequestBody CausalReasonerPost request) {
		LoggerUtil.logger.info(String.format("Run causal reasoner command \"%s\" for user \"%s\" with timeout: %s %s",
				request.getCmd(), request.getEmail(), request.getTimeout(), request.getUnit_timeout()));

		TimeUnit timeoutUnit = Utils.getTimeoutUnit(request.getUnit_timeout());
		int timeout = Utils.checkUserTimeout(request.getTimeout(), TIMEOUT_SECONDS, timeoutUnit);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Pair<String, Long> resultAndTime;
		try {
			var future = executor.submit(() -> processCommand(request));
			resultAndTime = Utils.runServicesWithTimeout(future, timeout, timeoutUnit);
		} catch (TimeoutException e) {
			LoggerUtil.logger.info("Timeout while running causal reasoner.");
			return new CausalReasonerResponse(null, request.getEmail(), timeout, request.getUnit_timeout(), TIMEOUT);
		} catch (ExecutionException e) {
			LoggerUtil.logger.warning(() -> "Error while running causal reasoner: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			LoggerUtil.logger.warning(() -> "Interrupt while running causal reasoner: " + e.getMessage());
			e.printStackTrace();
			Thread.currentThread().interrupt();
			throw new RuntimeException("Thread was interrupted.");
		} finally {
			executor.shutdownNow();
		}
		return new CausalReasonerResponse(resultAndTime.getKey(), request.getEmail(),
				resultAndTime.getValue(), request.getUnit_timeout(), SUCCESS);
	}

	private String processCommand(CausalReasonerPost post) {
		return switch (post.getCmd()) {
			case GET_CONCLUSIONS -> processConclusionsCommand(post);
			case GET_SIGNIFICANT_ATOMS -> processSignificantAtomsCommand(post);
			case GET_ARGUMENTATION_FRAMEWORK -> processArgumentationFramework(post);
			case GET_SEQUENCE_EXPLANATIONS -> processSequenceExplanations(post);
		};
	}

	private String processConclusionsCommand(CausalReasonerPost post) {
		CausalKnowledgeBase kb = parseKnowledgeBase(post);
		Collection<PlFormula> observations = parseObservations(post);
		return causalReasonerService.queryConclusions(kb, observations, parseConclusionFilter(post)).toString();
	}

	private String processSignificantAtomsCommand(CausalReasonerPost post) {
		CausalKnowledgeBase kb = parseKnowledgeBase(post);
		Collection<PlFormula> observations = parseObservations(post);
		var perAtomSignificantAtoms = causalReasonerService.queryPerAtomSignificantAtoms(kb, observations, parseConclusionFilter(post));
		Map<String, Collection<String>> jsonData = new HashMap<>();
		for (Map.Entry<Proposition, Collection<Proposition>> entry : perAtomSignificantAtoms.entrySet()) {
			List<String> list = new ArrayList<>();
			for (Proposition p : entry.getValue())
				list.add(p.toString());
			jsonData.put(entry.getKey().toString(), list);
		}
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonData);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private String processSequenceExplanations(CausalReasonerPost post) {
		CausalKnowledgeBase kb = parseKnowledgeBase(post);
		Collection<PlFormula> observations = parseObservations(post);
		var reply = SequenceExplanationReply.from(causalReasonerService.querySequenceExplanations(kb, observations, parseConclusionFilter(post)));
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reply);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private String processArgumentationFramework(CausalReasonerPost post) {
		CausalKnowledgeBase kb = parseKnowledgeBase(post);
		Collection<PlFormula> observations = parseObservations(post);
		var reply = ArgumentationFrameworkReply.from(causalReasonerService.queryArgumentationFramework(kb, observations));
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reply);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private static Collection<PlFormula> parseObservations(CausalReasonerPost post) {
		try {
			return new CausalParser().parseListOfFormulae(post.getObservations(), ",");
		} catch (ParserException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static @Nullable Set<Proposition> parseConclusionFilter(CausalReasonerPost post) {
		return ConclusionsFilterSerialization.parse(post.getConclusionsFilter());
	}

	private static CausalKnowledgeBase parseKnowledgeBase(CausalReasonerPost post) {
		try {
			return new CausalParser().parseBeliefBase(post.getKb());
		} catch (ParserException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
