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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.tweetyproject.arg.adf.reasoner.sat.parallel.Decomposer;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * @author Mathias Hofer
 *
 */
public final class ParallelExecution implements Execution {
					
	private final ExecutorService executor = Executors.newWorkStealingPool();
		
	private final BlockingQueue<Candidate> candidates;
	
	private final Map<Interpretation, Future<Boolean>> verifications;
	
	private final Map<Interpretation, Future<Interpretation>> models;
	
	private final Collection<Execution> branches;
	
	private boolean started = false;

	public ParallelExecution(AbstractDialecticalFramework adf, Semantics semantics, IncrementalSatSolver satSolver, Decomposer decomposer, int parallelism) {
		
		Collection<AbstractDialecticalFramework> decompositions = decomposer.decompose(adf, parallelism);
		this.candidates = new LinkedBlockingQueue<>(decompositions.size());
		this.branches = new ArrayList<>(decompositions.size());
		
		for (AbstractDialecticalFramework decomposition : decompositions) {
			this.branches.add(new SequentialExecution(decomposition, semantics, satSolver));
		}
		this.verifications = new ConcurrentHashMap<>(decompositions.size());
		this.models = new ConcurrentHashMap<>(decompositions.size());
	}
	
	private void startComputation() {
		for (Execution source : branches) {
			executor.execute(() -> {
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
		}
		candidates.offer(new Candidate(source, candidate));
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
		if (!started) {
			started = true;
			startComputation();
		}
		
		try {
			final Candidate result = candidates.take();
			final Execution source = result.source;
			executor.execute(() -> {
				Interpretation candidate = source.computeCandidate();
				notifyCandidate(candidate, source);
			});
			return result.interpretation;
		} catch (InterruptedException e) {
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

}
