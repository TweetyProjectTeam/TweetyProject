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
package net.sf.tweety.arg.adf.reasoner.sat.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import net.sf.tweety.arg.adf.reasoner.ordering.Ordering;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.BipolarSatEncoding;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.RelativeBipolarSatEncoding;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.RelativeKBipolarSatEncoding;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.semantics.link.Link;
import net.sf.tweety.arg.adf.semantics.link.LinkStrategy;
import net.sf.tweety.arg.adf.semantics.link.LinkType;
import net.sf.tweety.arg.adf.semantics.link.SatLinkStrategy;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Decides if an ADF becomes k-bipolar relative to some truth assignments.
 * 
 * @author Mathias Hofer
 *
 */
public abstract class RelativeKBipolarStateProcessor implements StateProcessor {

	private final int k;
	
	private final IncrementalSatSolver solver;
	
	/**
	 * @param k
	 * @param ordering
	 */
	private RelativeKBipolarStateProcessor(int k, IncrementalSatSolver solver) {
		this.k = k;
		this.solver = Objects.requireNonNull(solver);
	}

	public static StateProcessor relativeBounded(double l, int k, Ordering<Argument> ordering, IncrementalSatSolver solver) {
		return new RelativeBoundedRelativeBipolarStateProcessor(l, k, ordering, solver);
	}

	public static StateProcessor absoluteBounded(int k, Ordering<Argument> ordering, IncrementalSatSolver solver) {
		return new AbsoluteBoundedLKBipolarStateProcessor(k, ordering, solver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.sat.processor.StateProcessor#process(net.
	 * sf.tweety.arg.adf.sat.SatSolverState,
	 * net.sf.tweety.arg.adf.reasoner.sat.encodings.SatEncodingContext,
	 * net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework)
	 */
	@Override
	public void process(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		List<Argument> arguments = arguments(adf);
		
		Argument head = arguments.get(0);
		List<Argument> tail = arguments.subList(1, arguments.size());
		
		Map<Link, Set<Interpretation>> bipolarIn = new HashMap<>();
		
		Set<Link> links = selectAffected(head, adf);
		checkLinks(links, tail, Set.of(head), Set.of(), adf, new HashSet<>(), bipolarIn);
		checkLinks(links, tail, Set.of(), Set.of(head), adf, new HashSet<>(), bipolarIn);
		
		new BipolarSatEncoding().encode(state::add, mapping, adf);
		for (Entry<Link, Set<Interpretation>> entry : bipolarIn.entrySet()) {
			Link link = entry.getKey();
			for (Interpretation interpretation : entry.getValue()) {
				new RelativeBipolarSatEncoding(interpretation, link).encode(state::add, mapping, adf);
			}
		}
		new RelativeKBipolarSatEncoding(bipolarIn).encode(state::add, mapping, adf);
	}
	
	private void checkLinks(Set<Link> links, List<Argument> remaining, Set<Argument> satisfied, Set<Argument> unsatisfied, AbstractDialecticalFramework adf,  Set<Link> decided, Map<Link, Set<Interpretation>> bipolarIn) {		
		Interpretation assumption = Interpretation.fromSets(satisfied, unsatisfied, adf);
		LinkStrategy linkStrategy = new SatLinkStrategy(solver, assumption);
		
		for (Link link : links) {
			if (!decided.contains(link)) {
				LinkType type = linkStrategy.compute(link.getFrom(), adf.getAcceptanceCondition(link.getTo()));
				if (type.isBipolar()) {
					Link newLink = Link.of(link.getFrom(), link.getTo(), type);
					Set<Interpretation> assumptions = bipolarIn.computeIfAbsent(newLink, l -> new HashSet<>());
					assumptions.add(assumption);
					decided.add(newLink);
				}
			}
		}
		
		int nonBipolarCount = adf.kBipolar() - decided.size();
		if (nonBipolarCount > k && remaining.size() > 1) {
			Argument head = remaining.get(0);			
			List<Argument> tail = remaining.subList(1, remaining.size());
			
			Set<Argument> newSatisfied = new HashSet<>(satisfied);
			newSatisfied.add(head);
			
			Set<Argument> newUnsatisfied = new HashSet<>(unsatisfied);
			newUnsatisfied.add(head);
			
			Set<Link> affected = selectAffected(head, adf);
						
			checkLinks(affected, tail, newSatisfied, unsatisfied, adf, new HashSet<>(decided), bipolarIn);
			checkLinks(affected, tail, satisfied, newUnsatisfied, adf, new HashSet<>(decided), bipolarIn);
		}
	}

	/**
	 * Selects all affected links of an value assignment to <code>arg</code>.
	 * These are all the links to all the children of <code>arg</code>, since an
	 * assignment affects the acceptance conditions of its children and
	 * therefore the links to each of its children.
	 * <p>
	 * Only returns those links which are not already bipolar.
	 * 
	 * @param arg the argument for which we assign a value
	 * @param adf the corresponding ADF
	 * @return all non-bipolar links affected by a value assignment
	 */
	private static Set<Link> selectAffected(Argument arg, AbstractDialecticalFramework adf) {
		Set<Link> affected = new HashSet<>();
		for (Argument child : adf.children(arg)) {
			for (Link link : adf.linksTo(child)) {
				if (link.getType().isNonBipolar()) {
					affected.add(link);
				}
			}
		}
		return affected;
	}

	protected abstract List<Argument> arguments(AbstractDialecticalFramework adf);

	private static final class RelativeBoundedRelativeBipolarStateProcessor extends RelativeKBipolarStateProcessor {

		private final double l;

		private final Ordering<Argument> ordering;

		public RelativeBoundedRelativeBipolarStateProcessor(double l, int k, Ordering<Argument> ordering, IncrementalSatSolver solver) {
			super(k, solver);
			if (l <= 0 || l > 1) {
				throw new IllegalArgumentException("0 < l <= 1 must hold!");
			}
			this.l = l;
			this.ordering = ordering;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sf.tweety.arg.adf.reasoner.sat.processor.
		 * SatLKBipolarStateProcessor#arguments(net.sf.tweety.arg.adf.syntax.adf
		 * .AbstractDialecticalFramework)
		 */
		@Override
		protected List<Argument> arguments(AbstractDialecticalFramework adf) {
			int count = (int) Math.ceil(adf.size() * l);
			return ordering.limit(count).order(adf);
		}

	}

	private static final class AbsoluteBoundedLKBipolarStateProcessor extends RelativeKBipolarStateProcessor {

		private final Ordering<Argument> ordering;

		public AbsoluteBoundedLKBipolarStateProcessor(int k, Ordering<Argument> ordering, IncrementalSatSolver solver) {
			super(k, solver);
			this.ordering = ordering;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.sf.tweety.arg.adf.reasoner.sat.processor.
		 * SatLKBipolarStateProcessor#arguments(net.sf.tweety.arg.adf.syntax.adf
		 * .AbstractDialecticalFramework)
		 */
		@Override
		protected List<Argument> arguments(AbstractDialecticalFramework adf) {
			return ordering.order(adf);
		}

	}

}