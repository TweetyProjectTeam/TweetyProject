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
package org.tweetyproject.arg.adf.reasoner.sat.pipeline;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.tweetyproject.arg.adf.reasoner.sat.decomposer.Decomposer;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.FixThreeValuedPartialSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.sat.state.SynchronizedSatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.transform.FixPartialTransformer;
import org.tweetyproject.arg.adf.transform.Transformer;

/**
 * @author Mathias Hofer
 *
 */
public final class ParallelExecution implements Execution {
					
	private final ExecutorService executor = Executors.newFixedThreadPool(8);
	
	private final IncrementalSatSolver satSolver;
	
	private final Semantics semantics;
	
	private final Decomposer decomposer;
	
	private final int parallelism;
	
	private final AbstractDialecticalFramework adf;
	
	private Map<Interpretation, Future<Boolean>> verifications;
	
	private Map<Interpretation, Future<Interpretation>> models;
	
	private BlockingQueue<Candidate> candidates;
	
	private Set<Branch> branches;
	
	public ParallelExecution(AbstractDialecticalFramework adf, Semantics semantics, IncrementalSatSolver satSolver, Decomposer decomposer, int parallelism) {
		this.satSolver = Objects.requireNonNull(satSolver);
		this.adf = Objects.requireNonNull(adf);
		this.semantics = Objects.requireNonNull(semantics);
		this.decomposer = Objects.requireNonNull(decomposer);
		this.parallelism = parallelism;
	}
	
	private void start() {
		Collection<Interpretation> decompositions = decomposer.decompose(adf, parallelism);
		int numDecompositions = decompositions.size();
		this.verifications = new ConcurrentHashMap<>(numDecompositions);
		this.models = new ConcurrentHashMap<>(numDecompositions);
		this.branches = Collections.synchronizedSet(new HashSet<>(numDecompositions));
		this.candidates = new LinkedBlockingQueue<>(numDecompositions);
		
		for (Interpretation prefix : decompositions) {
			this.branches.add(new Branch(prefix));
		}
		
		for (Branch source : this.branches) {
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
		for (Execution branch : branches) {
			branch.close();
		}
	}
	
	@Override
	public boolean addClause(Clause clause) {
		boolean successful = true;
		for (Execution branch : branches) {
			successful &= branch.addClause(clause);
		}
		return successful;
	}

	@Override
	public boolean addClauses(Collection<? extends Clause> clauses) {
		boolean successful = true;
		for (Execution branch : branches) {
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
	
	private final class Branch implements Execution {

		private SatSolverState state;
								
		private CandidateGenerator generator;
				
		private final Verifier verifier;
		
		private List<InterpretationProcessor> modelProcessors;
		
		private final Interpretation prefix;
						
		/**
		 * @param prefix
		 */
		public Branch( Interpretation prefix ) {
			this.prefix = prefix;
			this.verifier = semantics.createVerifier(() -> new SynchronizedSatSolverState(satSolver.createState())).orElse(null);
		}
		
		private void prepare() {
			AbstractDialecticalFramework reduct = reduct(prefix);
			Semantics branchSemantics = semantics.forReduct(reduct);
			this.generator = branchSemantics.createCandidateGenerator();
			PropositionalMapping mapping = branchSemantics.getPropositionalMapping();
			Collection<Clause> encoding = new LinkedList<Clause>();
			generator.prepare(encoding::add);
			FixThreeValuedPartialSatEncoding.encode(prefix, encoding::add, mapping, reduct);
			
			for (StateProcessor processor : branchSemantics.createStateProcessors()) {
				processor.process(encoding::add);
			}
			
			this.state = new SynchronizedSatSolverState(createState(satSolver, encoding));
			this.modelProcessors = branchSemantics.createModelProcessors(() -> createState(satSolver, encoding));

			if (verifier != null) {
				this.verifier.prepare();			
			}
		}
		
		private AbstractDialecticalFramework reduct(Interpretation interpretation) {
			Transformer<AcceptanceCondition> fixPartials = new FixPartialTransformer(interpretation);		
			Builder builder = AbstractDialecticalFramework.builder().eager(new SatLinkStrategy(new NativeMinisatSolver()));
			for (Argument arg : adf.getArguments()) {
				if (!interpretation.decided(arg)) {
					builder.add(arg, fixPartials.transform(adf.getAcceptanceCondition(arg)));
				}
			}
			return builder.build();
		}
		
		private SatSolverState createState(IncrementalSatSolver satSolver, Collection<Clause> encoding) {
			SatSolverState state = satSolver.createState();
			for (Clause clause : encoding) {
				state.add(clause);
			}
			return state;
		}

		@Override
		public Interpretation computeCandidate() {
			return generator.generate(state);
//			if (candidate == null) return null;
//			
//			System.out.println("prefix: " + prefix + ", candidate: " + candidate);
//			Set<Argument> satisfied = new HashSet<>(prefix.satisfied());
//			satisfied.addAll(candidate.satisfied());
//			Set<Argument> unsatisfied = new HashSet<>(prefix.unsatisfied());
//			unsatisfied.addAll(candidate.unsatisfied());
//			Set<Argument> undecided = new HashSet<>(prefix.undecided());
//			undecided.addAll(candidate.undecided());
//			return Interpretation.fromSets(satisfied, unsatisfied, undecided);
		}

		@Override
		public boolean verify(Interpretation candidate) {
			if (verifier != null) {
				return verifier.verify(candidate);			
			}
			return true;
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
			if (verifier != null) {
				verifier.close();				
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
