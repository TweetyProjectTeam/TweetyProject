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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner.sat.execution;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.tweetyproject.arg.adf.reasoner.sat.decomposer.Decomposer;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.sat.state.SynchronizedSatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * @author Mathias Hofer
 *
 */
public final class ParallelExecution implements Execution {
					
	private final ExecutorService executor = Executors.newWorkStealingPool();
	
	private final IncrementalSatSolver satSolver;
	
	private final Semantics semantics;
	
	private final Decomposer decomposer;
	
	private final int parallelism;
	
	private final AbstractDialecticalFramework adf;
	
	private Map<Interpretation, Future<Boolean>> verifications;
	
	private Map<Interpretation, Future<Interpretation>> models;
	
	private BlockingQueue<Candidate> candidates;
	
	private Map<Branch, Interpretation> branches;
	
	public ParallelExecution(AbstractDialecticalFramework adf, Semantics semantics, IncrementalSatSolver satSolver, int parallelism) {
		this.satSolver = Objects.requireNonNull(satSolver);
		this.adf = Objects.requireNonNull(adf);
		this.semantics = Objects.requireNonNull(semantics);
		this.decomposer = semantics.createDecomposer();
		this.parallelism = parallelism;
	}
	
	private void start() {
		Collection<Interpretation> decompositions = decomposer.decompose(adf, parallelism);
		int numDecompositions = decompositions.size();
		this.verifications = new ConcurrentHashMap<>(numDecompositions);
		this.models = new ConcurrentHashMap<>(numDecompositions);
		this.branches = new ConcurrentHashMap<>(numDecompositions);
		this.candidates = new LinkedBlockingQueue<>(numDecompositions);
		
		for (Interpretation prefix : decompositions) {
			this.branches.put(new Branch(prefix, semantics, satSolver), prefix);
		}
		
		for (Branch source : this.branches.keySet()) {
			executor.execute(() -> {
				source.prepare();
				Interpretation candidate = source.computeCandidate();
				notifyCandidate(candidate, source);
			});
		}
	}
	
	private void notifyCandidate(Interpretation candidate, Execution source) {
		if (candidate != null) {
			Future<Boolean> verification = executor.submit(() ->  {
				boolean verified = source.verify(candidate);
				notifyVerified(candidate, source, verified);
				return verified;
			});
			verifications.put(candidate, verification);
			candidates.offer(new Candidate(source, candidate));
		} else {
			source.close();
			branches.remove(source);
			if (branches.isEmpty()) {
				candidates.offer(new Candidate(source, null));
			}
		}
	}
	
	private void notifyVerified(Interpretation candidate, Execution source, boolean verified) {
		if (verified) {
			Future<Interpretation> model = executor.submit(() -> {
				return source.processModel(candidate);
			});
			models.put(candidate, model);
		}
	}

	@Override
	public Interpretation computeCandidate() {
		if (branches == null) start();
		
		try {
			final Candidate result = candidates.take();
			if (result.interpretation != null) {
				final Execution source = result.source;
				executor.execute(() -> {
					Interpretation candidate = source.computeCandidate();
					notifyCandidate(candidate, source);
				});				
			}
			return result.interpretation;
		} catch (InterruptedException e) {
			close();
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean verify(Interpretation candidate) {
		try {
			if (verifications.containsKey(candidate)) {
				return verifications.remove(candidate).get();				
			}
			return false;
		} catch (InterruptedException | ExecutionException e) {
			close();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Interpretation processModel(Interpretation model) {
		try {
			return models.get(model).get();
		} catch (InterruptedException | ExecutionException e) {
			close();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void close() {
		executor.shutdownNow();
		for (Execution branch : branches.keySet()) {
			branch.close();
		}
	}
	
	@Override
	public boolean addClause(Clause clause) {
		boolean successful = true;
		for (Execution branch : branches.keySet()) {
			successful &= branch.addClause(clause);
		}
		return successful;
	}

	@Override
	public boolean addClauses(Collection<? extends Clause> clauses) {
		boolean successful = true;
		for (Execution branch : branches.keySet()) {
			successful &= branch.addClauses(clauses);
		}
		return successful;
	}
	
	private static final class Candidate {
		
		private final Execution source;
		
		private final Interpretation interpretation;

		Candidate(Execution source, Interpretation interpretation) {
			this.source = source;
			this.interpretation = interpretation;	
		}
		
	}
	
	private static final class Branch implements Execution {
		
		private final List<StateProcessor> stateProcessors;
		
		private final CandidateGenerator generator;
		
		private final List<InterpretationProcessor> candidateProcessors;
		
		private final Optional<Verifier> verifier;

		private final List<InterpretationProcessor> modelProcessors;
		
		private final IncrementalSatSolver satSolver;
	
		private SatSolverState state;
				
		public Branch( Interpretation prefix, Semantics semantics, IncrementalSatSolver satSolver ) {
			Semantics prefixSemantics = semantics.withPrefix(prefix);
			this.satSolver = satSolver;
			this.stateProcessors = prefixSemantics.createStateProcessors();
			this.generator = prefixSemantics.createCandidateGenerator();
			this.candidateProcessors = prefixSemantics.createCandidateProcessor(satSolver::createState);
			this.verifier = prefixSemantics.createVerifier(() -> new SynchronizedSatSolverState(satSolver.createState()));
			this.modelProcessors = prefixSemantics.createModelProcessors(satSolver::createState);
		}
		
		private void prepare() {
			SatSolverState state = satSolver.createState();
			generator.prepare(state::add);

			for (StateProcessor processor : stateProcessors) {
				processor.process(state::add);
			}

			this.state = new SynchronizedSatSolverState(state);

			verifier.ifPresent(Verifier::prepare);
		}
		
		@Override
		public Interpretation computeCandidate() {
			Interpretation candidate = generator.generate(state);
			if (candidate != null) {
				return processCandidate(candidate);
			}
			return null;
		}
		
		private Interpretation processCandidate(Interpretation candidate) {
			Interpretation processed = candidate;
			for (InterpretationProcessor processor : candidateProcessors) {
				processed = processor.process(processed);
				processor.updateState(state, processed);
			}
			return processed;
		}

		@Override
		public boolean verify(Interpretation candidate) {
			return verifier.map(v -> v.verify(candidate))
						   .orElse(true);
		}
		
		@Override
		public Interpretation processModel(Interpretation model) {
			Interpretation processed = model;
			for (InterpretationProcessor processor : modelProcessors) {
				processed = processor.process(processed);
				processor.updateState(state, processed);
			}
			return processed;
		}
		
		@Override
		public boolean addClause(Clause clause) {
			return state.add(clause);
		}
		
		@Override
		public void close() {
			state.close();
			verifier.ifPresent(Verifier::close);
			for (InterpretationProcessor processor : modelProcessors) {
				processor.close();
			}
			for (InterpretationProcessor processor : candidateProcessors) {
				processor.close();
			}
		}

		@Override
		public boolean addClauses(Collection<? extends Clause> clauses) {
			for (Clause clause : clauses) {
				if(!state.add(clause)) {
					return false;
				}
			}
			return true;
		}
		
	}

}
