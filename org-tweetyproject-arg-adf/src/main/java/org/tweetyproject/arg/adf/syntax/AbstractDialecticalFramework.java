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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.graphs.DefaultGraph;
import org.tweetyproject.graphs.GeneralEdge;
import org.tweetyproject.graphs.Graph;
import org.tweetyproject.graphs.Node;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;
import org.tweetyproject.math.matrix.Matrix;
import org.tweetyproject.math.term.IntegerConstant;

/**
 * An abstract dialectical framework (ADF) D = (A, C): a set of arguments A and
 * an acceptance condition phi_a for every a ∈ A, which is a propositional formula
 * over A (Brewka and Woltran, KR 2010; Brewka et al., IJCAI 2013).
 * <p>
 * Arguments are Dung {@link Argument}s and acceptance conditions are
 * {@link PlFormula}s; the proposition {@code Proposition("a")} refers to the
 * argument {@code Argument("a")}. The links of the ADF, i.e. its graph edges,
 * are implied by the conditions: (b, a) is a link iff b occurs in phi_a.
 * <p>
 * <b>Mutation and ownership.</b> The framework is mutable, like
 * {@code DungTheory}. It stores argument names and copies of the given
 * formulas, and every getter returns detached copies, so changing a supplied or
 * returned {@code Argument} or formula never changes the framework. Every
 * condition may only mention arguments of the framework; violations are
 * rejected with an {@link IllegalArgumentException} and leave the framework
 * unchanged. Removing an argument replaces its proposition by ⊥ in all
 * remaining conditions. Every change increments {@link #getRevision()}.
 * Reasoners capture an immutable snapshot of the framework when they start, so
 * a framework must not be changed concurrently with that capture.
 *
 * @author Lars Bengel
 */
public class AbstractDialecticalFramework extends BeliefSet<Argument, DungSignature>
		implements Graph<Argument>, ArgumentationFramework<Argument> {

	/** acceptance conditions by argument name, in insertion order */
	private final Map<String, PlFormula> conditions = new LinkedHashMap<>();

	private long revision;

	/** parent and child names by argument name; null if invalidated by a mutation */
	private Map<String, Set<String>> parents;
	private Map<String, Set<String>> children;

	/**
	 * Creates the empty ADF.
	 */
	public AbstractDialecticalFramework() {
		super();
	}

	/**
	 * Creates an ADF with the given arguments and acceptance conditions. All
	 * arguments are registered before the conditions are validated, so conditions
	 * may refer to each other.
	 *
	 * @param conditions the acceptance condition of every argument
	 * @throws IllegalArgumentException if a condition mentions an argument that is
	 *                                  not a key of the map, or uses an
	 *                                  unsupported connective
	 */
	public AbstractDialecticalFramework(Map<Argument, PlFormula> conditions) {
		this();
		Map<String, PlFormula> validated = new LinkedHashMap<>();
		Set<String> domain = new HashSet<>();
		for (Argument a : conditions.keySet()) {
			domain.add(nameOf(a));
		}
		for (Map.Entry<Argument, PlFormula> entry : conditions.entrySet()) {
			validated.put(nameOf(entry.getKey()), validate(entry.getValue(), domain));
		}
		this.conditions.putAll(validated);
	}

	/**
	 * Creates a copy of the given ADF.
	 *
	 * @param other an ADF
	 */
	public AbstractDialecticalFramework(AbstractDialecticalFramework other) {
		this();
		for (Map.Entry<String, PlFormula> entry : other.conditions.entrySet()) {
			this.conditions.put(entry.getKey(), AcceptanceConditions.copy(entry.getValue()));
		}
	}

	/**
	 * Returns the ADF D_F associated with an AF F = (A, R), in which
	 * phi_a = ∧_{(b,a) ∈ R} ¬b, and phi_a = ⊤ for unattacked arguments.
	 *
	 * @param af an abstract argumentation framework
	 * @return the associated ADF
	 */
	public static AbstractDialecticalFramework fromDungTheory(DungTheory af) {
		AbstractDialecticalFramework adf = new AbstractDialecticalFramework();
		for (Argument a : af) {
			List<String> attackers = new ArrayList<>();
			for (Argument b : af.getAttackers(a)) {
				attackers.add(b.getName());
			}
			Collections.sort(attackers);
			adf.conditions.put(nameOf(a), AcceptanceConditions.attackedBy(attackers));
		}
		return adf;
	}

	// ------------------------------------------------------------------
	// mutation
	// ------------------------------------------------------------------

	/**
	 * Adds an argument with the acceptance condition ⊤ (as for an unattacked
	 * argument in D_F).
	 *
	 * @param argument an argument
	 * @return false if the argument already exists, in which case nothing changes
	 */
	@Override
	public boolean add(Argument argument) {
		return add(argument, new Tautology());
	}

	/**
	 * Adds a new argument with the given acceptance condition. The condition may
	 * mention the new argument itself.
	 *
	 * @param argument  an argument
	 * @param condition its acceptance condition
	 * @return false if the argument already exists, in which case nothing changes
	 *         (use {@link #setAcceptanceCondition(Argument, PlFormula)} to change
	 *         a condition)
	 * @throws IllegalArgumentException if the condition mentions an unknown
	 *                                  argument
	 */
	public boolean add(Argument argument, PlFormula condition) {
		String name = nameOf(argument);
		if (conditions.containsKey(name)) {
			return false;
		}
		Set<String> domain = new HashSet<>(conditions.keySet());
		domain.add(name);
		PlFormula copy = validate(condition, domain);
		conditions.put(name, copy);
		changed();
		return true;
	}

	/**
	 * Adds the given arguments with acceptance condition ⊤.
	 *
	 * @param arguments some arguments
	 * @return true if at least one argument was added
	 */
	@Override
	public boolean addAll(Collection<? extends Argument> arguments) {
		boolean changed = false;
		for (Argument a : arguments) {
			changed |= add(a);
		}
		return changed;
	}

	/**
	 * Links are determined by the acceptance conditions and cannot be added
	 * directly.
	 *
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public boolean add(GeneralEdge<Argument> edge) {
		throw new UnsupportedOperationException("ADF links are implied by the acceptance conditions.");
	}

	/**
	 * Replaces the acceptance condition of an existing argument.
	 *
	 * @param argument  an argument of this ADF
	 * @param condition its new acceptance condition
	 * @throws IllegalArgumentException if the argument does not exist or the
	 *                                  condition mentions an unknown argument
	 */
	public void setAcceptanceCondition(Argument argument, PlFormula condition) {
		String name = requireArgument(argument);
		conditions.put(name, validate(condition, conditions.keySet()));
		changed();
	}

	/**
	 * Removes an argument and replaces its proposition by ⊥ in the conditions of
	 * the remaining arguments.
	 *
	 * @param o an argument
	 * @return true if the argument was part of this ADF
	 */
	@Override
	public boolean remove(Object o) {
		if (!contains(o)) {
			return false;
		}
		removeNames(Set.of(((Argument) o).getName()));
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		Set<String> names = new HashSet<>();
		for (Object o : c) {
			if (contains(o)) {
				names.add(((Argument) o).getName());
			}
		}
		return removeNames(names);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		Set<String> keep = new HashSet<>();
		for (Object o : c) {
			if (o instanceof Argument) {
				Argument a = (Argument) o;
				keep.add(a.getName());
			}
		}
		Set<String> names = new HashSet<>(conditions.keySet());
		names.removeAll(keep);
		return removeNames(names);
	}

	@Override
	public boolean removeIf(Predicate<? super Argument> filter) {
		Set<String> names = new HashSet<>();
		for (String name : conditions.keySet()) {
			if (filter.test(new Argument(name))) {
				names.add(name);
			}
		}
		return removeNames(names);
	}

	/**
	 * Removes all arguments.
	 */
	@Override
	public void clear() {
		if (!conditions.isEmpty()) {
			conditions.clear();
			changed();
		}
	}

	private boolean removeNames(Set<String> names) {
		if (names.isEmpty()) {
			return false;
		}
		conditions.keySet().removeAll(names);
		Map<String, PlFormula> falsum = new HashMap<>();
		for (String name : names) {
			falsum.put(name, new Contradiction());
		}
		for (Map.Entry<String, PlFormula> entry : conditions.entrySet()) {
			if (!Collections.disjoint(AcceptanceConditions.atomNames(entry.getValue()), names)) {
				entry.setValue(AcceptanceConditions.substitute(entry.getValue(), falsum));
			}
		}
		changed();
		return true;
	}

	private void changed() {
		revision++;
		parents = null;
		children = null;
	}

	/**
	 * Returns a counter that changes with every modification of this framework.
	 * Analyses can use it to detect whether a cached result is still valid.
	 *
	 * @return the current revision
	 */
	public long getRevision() {
		return revision;
	}

	// ------------------------------------------------------------------
	// access
	// ------------------------------------------------------------------

	/**
	 * @param argument an argument of this ADF
	 * @return a copy of its acceptance condition
	 * @throws IllegalArgumentException if the argument does not exist
	 */
	public PlFormula getAcceptanceCondition(Argument argument) {
		return AcceptanceConditions.copy(conditions.get(requireArgument(argument)));
	}

	/**
	 * @param argument an argument of this ADF
	 * @return the proposition referring to it in acceptance conditions
	 * @throws IllegalArgumentException if the argument does not exist
	 */
	public Proposition toProposition(Argument argument) {
		return new Proposition(requireArgument(argument));
	}

	/**
	 * @param proposition a proposition occurring in acceptance conditions
	 * @return the argument it refers to
	 * @throws IllegalArgumentException if there is no such argument
	 */
	public Argument toArgument(Proposition proposition) {
		if (!conditions.containsKey(proposition.getName())) {
			throw new IllegalArgumentException("Unknown argument: " + proposition.getName());
		}
		return new Argument(proposition.getName());
	}

	@Override
	public boolean contains(Object o) {
		return o instanceof Argument && ((Argument) o).getName() != null
				&& conditions.containsKey(((Argument) o).getName());
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int size() {
		return conditions.size();
	}

	@Override
	public boolean isEmpty() {
		return conditions.isEmpty();
	}

	/**
	 * @return an iterator over copies of the arguments; it does not support removal
	 */
	@Override
	public Iterator<Argument> iterator() {
		return getNodes().iterator();
	}

	@Override
	public Object[] toArray() {
		return getNodes().toArray();
	}

	@Override
	public <R> R[] toArray(R[] a) {
		return getNodes().toArray(a);
	}

	/**
	 * @return an unmodifiable set of copies of the arguments, in insertion order
	 */
	@Override
	public Set<Argument> getNodes() {
		return toArguments(conditions.keySet());
	}

	@Override
	public int getNumberOfNodes() {
		return conditions.size();
	}

	/**
	 * @param node an argument of this ADF
	 * @return the arguments occurring in its acceptance condition
	 * @throws IllegalArgumentException if the argument does not exist
	 */
	@Override
	public Set<Argument> getParents(Node node) {
		return toArguments(parentMap().get(requireArgument(node)));
	}

	/**
	 * @param node an argument of this ADF
	 * @return the arguments whose acceptance conditions mention it
	 * @throws IllegalArgumentException if the argument does not exist
	 */
	@Override
	public Set<Argument> getChildren(Node node) {
		requireArgument(node);
		return toArguments(childMap().get(((Argument) node).getName()));
	}

	@Override
	public Collection<Argument> getNeighbors(Argument node) {
		Set<Argument> result = new LinkedHashSet<>(getParents(node));
		result.addAll(getChildren(node));
		return Collections.unmodifiableSet(result);
	}

	/**
	 * @return all links (b, a) such that b occurs in phi_a
	 */
	public Collection<Link> getLinks() {
		List<Link> links = new ArrayList<>();
		for (Map.Entry<String, Set<String>> entry : parentMap().entrySet()) {
			for (String parent : entry.getValue()) {
				links.add(new Link(new Argument(parent), new Argument(entry.getKey())));
			}
		}
		return Collections.unmodifiableList(links);
	}

	/**
	 * @return all links, see {@link #getLinks()}
	 */
	@Override
	public Collection<Link> getEdges() {
		return getLinks();
	}

	@Override
	public int getNumberOfEdges() {
		int count = 0;
		for (Set<String> p : parentMap().values()) {
			count += p.size();
		}
		return count;
	}

	/**
	 * @return true if a occurs in the acceptance condition of b
	 */
	@Override
	public boolean areAdjacent(Argument a, Argument b) {
		return parentMap().get(requireArgument(b)).contains(requireArgument(a));
	}

	/**
	 * @return the link (a, b) if a occurs in the acceptance condition of b,
	 *         otherwise null
	 */
	@Override
	public Link getEdge(Argument a, Argument b) {
		return areAdjacent(a, b) ? new Link(new Argument(a.getName()), new Argument(b.getName())) : null;
	}

	@Override
	public boolean existsDirectedPath(Argument node1, Argument node2) {
		return DefaultGraph.existsDirectedPath(this, node1, node2);
	}

	@Override
	public Matrix getAdjacencyMatrix() {
		List<Argument> nodes = new ArrayList<>(getNodes());
		Matrix m = new Matrix(nodes.size(), nodes.size());
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.size(); j++) {
				m.setEntry(i, j, new IntegerConstant(areAdjacent(nodes.get(i), nodes.get(j)) ? 1 : 0));
			}
		}
		return m;
	}

	/**
	 * There is no meaningful complement of an ADF.
	 *
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public Graph<Argument> getComplementGraph(int selfloops) {
		throw new UnsupportedOperationException("ADFs have no complement graph.");
	}

	@Override
	public Collection<Collection<Argument>> getConnectedComponents() {
		return DefaultGraph.getConnectedComponents(this);
	}

	@Override
	public Collection<Collection<Argument>> getStronglyConnectedComponents() {
		return DefaultGraph.getStronglyConnectedComponents(this);
	}

	@Override
	public Collection<Graph<Argument>> getSubgraphs() {
		return DefaultGraph.getSubgraphs(this);
	}

	@Override
	public boolean hasSelfLoops() {
		for (Map.Entry<String, Set<String>> entry : parentMap().entrySet()) {
			if (entry.getValue().contains(entry.getKey())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isWeightedGraph() {
		return false;
	}

	/**
	 * @return the restriction to the given arguments, see {@link #restrict(Collection)}
	 */
	@Override
	public AbstractDialecticalFramework getRestriction(Collection<Argument> nodes) {
		return restrict(nodes);
	}

	/**
	 * An argument a is attacked by a set S if the acceptance condition of a is
	 * already false when S is accepted and nothing else is known, i.e. Kleene
	 * evaluation of phi_a under the interpretation mapping S to t and all other
	 * arguments to u yields f. For the ADF D_F of an AF this is exactly the case
	 * if some argument of S attacks a.
	 *
	 * @param argument an argument of this ADF
	 * @param ext      a set of arguments of this ADF
	 * @return true if the set attacks the argument
	 */
	@Override
	public boolean isAttacked(Argument argument, Extension<? extends ArgumentationFramework<?>> ext) {
		requireArgument(argument);
		ThreeValuedInterpretation v = ThreeValuedInterpretation.of(new HashSet<>(ext), Set.of(), getNodes());
		return v.evaluate(conditions.get(argument.getName())) == TruthValue.FALSE;
	}

	// ------------------------------------------------------------------
	// derived frameworks
	// ------------------------------------------------------------------

	/**
	 * Returns the reduct D^v of this ADF for a two-valued interpretation v
	 * (Brewka et al. 2013, Definition 6): it contains the arguments that v maps to
	 * true, and every argument that v maps to false is replaced by ⊥ in their
	 * conditions.
	 *
	 * @param v a two-valued interpretation over exactly the arguments of this ADF
	 * @return the reduct
	 * @throws IllegalArgumentException if v is not two-valued or has another domain
	 */
	public AbstractDialecticalFramework reduct(ThreeValuedInterpretation v) {
		requireDomain(v);
		if (!v.isTwoValued()) {
			throw new IllegalArgumentException("The reduct is only defined for two-valued interpretations.");
		}
		Set<String> kept = new LinkedHashSet<>();
		for (String name : conditions.keySet()) {
			if (v.get(new Argument(name)) == TruthValue.TRUE) {
				kept.add(name);
			}
		}
		return restrictToNames(kept);
	}

	/**
	 * Returns the sub-framework on the given arguments, in which every other
	 * argument is replaced by ⊥ in the remaining conditions.
	 *
	 * @param arguments arguments of this ADF
	 * @return the restricted ADF
	 * @throws IllegalArgumentException if an argument does not exist
	 */
	public AbstractDialecticalFramework restrict(Collection<Argument> arguments) {
		Set<String> kept = new HashSet<>();
		for (Argument a : arguments) {
			kept.add(requireArgument(a));
		}
		return restrictToNames(kept);
	}

	private AbstractDialecticalFramework restrictToNames(Set<String> kept) {
		Map<String, PlFormula> falsum = new HashMap<>();
		for (String name : conditions.keySet()) {
			if (!kept.contains(name)) {
				falsum.put(name, new Contradiction());
			}
		}
		AbstractDialecticalFramework result = new AbstractDialecticalFramework();
		for (Map.Entry<String, PlFormula> entry : conditions.entrySet()) {
			if (kept.contains(entry.getKey())) {
				result.conditions.put(entry.getKey(), AcceptanceConditions.substitute(entry.getValue(), falsum));
			}
		}
		return result;
	}

	// ------------------------------------------------------------------
	// signature, equality, printing
	// ------------------------------------------------------------------

	@Override
	protected DungSignature instantiateSignature() {
		return new DungSignature();
	}

	@Override
	public DungSignature getSignature() {
		DungSignature sig = new DungSignature();
		for (Argument a : getNodes()) {
			sig.add(a);
		}
		return sig;
	}

	@Override
	public DungSignature getMinimalSignature() {
		return getSignature();
	}

	@Override
	public int hashCode() {
		return conditions.hashCode();
	}

	/**
	 * Two ADFs are equal if they have the same arguments and structurally equal
	 * acceptance conditions.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return conditions.equals(((AbstractDialecticalFramework) obj).conditions);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("{ ");
		boolean first = true;
		for (Map.Entry<String, PlFormula> entry : conditions.entrySet()) {
			if (!first) {
				s.append(", ");
			}
			first = false;
			s.append(entry.getKey()).append('[').append(entry.getValue()).append(']');
		}
		return s.append(" }").toString();
	}

	@Override
	public String toString(boolean showSignature) {
		return toString();
	}

	// ------------------------------------------------------------------
	// helpers
	// ------------------------------------------------------------------

	private Map<String, Set<String>> parentMap() {
		if (parents == null) {
			Map<String, Set<String>> p = new LinkedHashMap<>();
			Map<String, Set<String>> c = new LinkedHashMap<>();
			for (String name : conditions.keySet()) {
				c.put(name, new LinkedHashSet<>());
			}
			for (Map.Entry<String, PlFormula> entry : conditions.entrySet()) {
				Set<String> atoms = AcceptanceConditions.atomNames(entry.getValue());
				p.put(entry.getKey(), atoms);
				for (String parent : atoms) {
					c.get(parent).add(entry.getKey());
				}
			}
			parents = p;
			children = c;
		}
		return parents;
	}

	private Map<String, Set<String>> childMap() {
		parentMap();
		return children;
	}

	private String requireArgument(Object o) {
		if (!contains(o)) {
			throw new IllegalArgumentException("Not an argument of this ADF: " + o);
		}
		return ((Argument) o).getName();
	}

	private void requireDomain(ThreeValuedInterpretation v) {
		Set<String> names = new HashSet<>();
		for (Argument a : v.getDomain()) {
			names.add(a.getName());
		}
		if (!names.equals(conditions.keySet())) {
			throw new IllegalArgumentException("The interpretation is not defined over the arguments of this ADF.");
		}
	}

	private static PlFormula validate(PlFormula condition, Set<String> domain) {
		Objects.requireNonNull(condition, "condition");
		for (String atom : AcceptanceConditions.atomNames(condition)) {
			if (!domain.contains(atom)) {
				throw new IllegalArgumentException("The acceptance condition " + condition
						+ " mentions the unknown argument " + atom + ".");
			}
		}
		return AcceptanceConditions.copy(condition);
	}

	private static String nameOf(Argument argument) {
		Objects.requireNonNull(argument, "argument");
		return Objects.requireNonNull(argument.getName(), "argument name");
	}

	private static Set<Argument> toArguments(Collection<String> names) {
		Set<Argument> result = new LinkedHashSet<>();
		for (String name : names) {
			result.add(new Argument(name));
		}
		return Collections.unmodifiableSet(result);
	}

}
