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
package org.tweetyproject.lp.asp.beliefdynamics.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.tweetyproject.beliefdynamics.BaseRevisionOperator;
import org.tweetyproject.beliefdynamics.MultipleBaseRevisionOperator;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.lp.asp.parser.ASPParser;
import org.tweetyproject.lp.asp.parser.ParseException;
import org.tweetyproject.lp.asp.reasoner.ASPSolver;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.Program;
import org.tweetyproject.lp.asp.syntax.ASPRule;

/**
 * The data-model used to compare two different revision approaches.
 * Based on RevisionCompareModel in the beliefdynamics.gui package.
 *
 * @author Sebastian Homann
 * @author Tim Janus
 *
 */
public class SimpleRevisionCompareModel {

	/**
	 * The current belief base as a program.
	 * <p>
	 * This represents the current set of beliefs in the system.
	 * </p>
	 */
	protected Program beliefBase;

	/**
	 * The new beliefs to be added or compared.
	 * <p>
	 * This represents a new set of beliefs that will be used for comparison or
	 * revision.
	 * </p>
	 */
	protected Program newBeliefs;

	/**
	 * The result of the left comparison or revision operation.
	 * <p>
	 * This holds the result of the left-side operation in a comparison or revision
	 * process.
	 * </p>
	 */
	protected Collection<?> leftResult;

	/**
	 * The result of the right comparison or revision operation.
	 * <p>
	 * This holds the result of the right-side operation in a comparison or revision
	 * process.
	 * </p>
	 */
	protected Collection<?> rightResult;

	/**
	 * The {@link ASPSolver} used for solving logic programs.
	 * <p>
	 * This solver is responsible for processing the logic programs and computing
	 * results based on the provided belief bases.
	 * </p>
	 */
	protected ASPSolver solver;

	/**
	 * Property change support for managing listeners.
	 * <p>
	 * This is used to notify listeners about changes in the model.
	 * </p>
	 */
	private final PropertyChangeSupport change = new PropertyChangeSupport(this);

	/**
	 * The left revision operator for the revision process.
	 * <p>
	 * This operator is used for handling the left-side revision method.
	 * </p>
	 */
	private BaseRevisionOperator<?> leftOperator;

	/**
	 * The right revision operator for the revision process.
	 * <p>
	 * This operator is used for handling the right-side revision method.
	 * </p>
	 */
	private BaseRevisionOperator<?> rightOperator;

	/**
	 * A collection of selectable revision operators.
	 * <p>
	 * This collection includes all available operators that can be selected for the
	 * revision process.
	 * </p>
	 */
	private Collection<BaseRevisionOperator<?>> selectableOperators;

	/**
	 * Constructs a {@code SimpleRevisionCompareModel} instance with default
	 * settings.
	 */
	public SimpleRevisionCompareModel() {
		// Default constructor
	}

	/**
	 * Constructs a {@code SimpleRevisionCompareModel} instance with the specified
	 * ASP solver.
	 *
	 * @param solver the {@link ASPSolver} to be used for solving logic programs
	 * @throws NullPointerException if {@code solver} is {@code null}
	 */
	public SimpleRevisionCompareModel(ASPSolver solver) {
		this.solver = Objects.requireNonNull(solver, "Solver cannot be null");
	}

	/**
	 * Sets the {@link ASPSolver} instance for this
	 * {@code SimpleRevisionCompareModel} instance.
	 *
	 * @param solver the {@link ASPSolver} to be used for solving logic programs
	 * @throws NullPointerException if {@code solver} is {@code null}
	 */
	public void setSolver(ASPSolver solver) {
		this.solver = Objects.requireNonNull(solver, "Solver cannot be null");
	}

	/**
	 * Adds a property change listener to this model.
	 *
	 * @param listener the {@link PropertyChangeListener} to be added
	 */
	public void addListener(PropertyChangeListener listener) {
		change.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a property change listener from this model.
	 *
	 * @param listener the {@link PropertyChangeListener} to be removed
	 */
	public void removeListener(PropertyChangeListener listener) {
		change.removePropertyChangeListener(listener);
	}

	/**
	 * Selects the left revision method.
	 *
	 * @param left the revision method as an operator reference
	 * @throws IllegalArgumentException if the provided operator is not in the list
	 *                                  of selectable operators
	 */
	public void setLeftOperator(BaseRevisionOperator<?> left) {
		if (selectableOperators.contains(left)) {
			BaseRevisionOperator<?> old = this.leftOperator;
			this.leftOperator = left;
			change.firePropertyChange("leftOperator", old, left);
		} else {
			throw new IllegalArgumentException("Operator not selectable");
		}
	}

	/**
	 * Selects the right revision method.
	 *
	 * @param right the revision method as an operator reference
	 * @throws IllegalArgumentException if the provided operator is not in the list
	 *                                  of selectable operators
	 */
	public void setRightOperator(BaseRevisionOperator<?> right) {
		if (selectableOperators.contains(right)) {
			BaseRevisionOperator<?> old = this.rightOperator;
			this.rightOperator = right;
			change.firePropertyChange("rightOperator", old, right);
		} else {
			throw new IllegalArgumentException("Operator not selectable");
		}
	}

	/**
	 * Sets the belief base from a string representation.
	 *
	 * @param beliefBase the string representation of the belief base
	 * @throws ParseException if parsing fails
	 */
	public void setBeliefbase(String beliefBase) throws ParseException {
		Program old = this.beliefBase;
		this.beliefBase = ASPParser.parseProgram(beliefBase); // TODO: Test with new parser
		change.firePropertyChange("beliefBase", old, beliefBase);
	}

	/**
	 * Sets the belief base from a reader.
	 *
	 * @param beliefBase the reader providing the belief base
	 * @throws ParseException if parsing fails
	 */
	public void setBeliefbase(Reader beliefBase) throws ParseException {
		Program old = this.beliefBase;
		this.beliefBase = ASPParser.parseProgram(beliefBase);
		change.firePropertyChange("beliefBase", old, this.beliefBase.toString());
	}

	/**
	 * Sets the new beliefs from a string representation.
	 *
	 * @param newBeliefs the string representation of the new beliefs
	 * @throws ParseException if parsing fails
	 */
	public void setNewBeliefs(String newBeliefs) throws ParseException {
		Program old = this.newBeliefs;
		this.newBeliefs = ASPParser.parseProgram(newBeliefs);
		change.firePropertyChange("newbeliefs", old, newBeliefs);
	}

	/**
	 * Sets the new beliefs from a reader.
	 *
	 * @param newBeliefs the reader providing the new beliefs
	 * @throws ParseException if parsing fails
	 */
	public void setNewBeliefs(Reader newBeliefs) throws ParseException {
		Program old = this.newBeliefs;
		this.newBeliefs = ASPParser.parseProgram(newBeliefs);
		change.firePropertyChange("newbeliefs", old, this.newBeliefs.toString());
	}

	/**
	 * Adds the given revision operator to the set of selectable revision methods.
	 *
	 * @param op Reference to the operator, if the operator is in the set nothing
	 *           happens
	 */
	public void addOperator(BaseRevisionOperator<?> op) {
		if (this.selectableOperators.add(op)) {
			change.firePropertyChange("selectableOperators", null, op);
		}
	}

	/**
	 * Removes the given revision operator from the set of selectable revision
	 * methods.
	 *
	 * @param op Reference to the operator which is removed if it is member of the
	 *           set of revision methods.
	 */
	public void removeOperator(BaseRevisionOperator<?> op) {
		if (this.selectableOperators.remove(op)) {
			change.firePropertyChange("selectableOperators", op, null);
		}
	}

	/**
	 * Executes the revision operations using the selected left and right revision
	 * operators.
	 * <p>
	 * This method checks if both the belief base and new beliefs are non-null. If
	 * the left operator is not null, it performs the revision using the left
	 * operator
	 * and updates the `leftResult` with the result. Similarly, it performs the
	 * revision using the right operator and updates the `rightResult`. Property
	 * change events
	 * are fired to notify listeners of any changes to `leftResult`, `rightResult`,
	 * or errors encountered during the revision process.
	 * </p>
	 */
	public void runRevisions() {
		if (beliefBase == null || newBeliefs == null) {
			return;
		}

		try {
			if (leftOperator != null) {
				if (leftOperator instanceof MultipleBaseRevisionOperator<?>) {
					Collection<?> oldValue = leftResult;
					leftResult = runRevision((MultipleBaseRevisionOperator<?>) leftOperator);
					change.firePropertyChange("leftresult", oldValue, leftResult);
				} else {
					Collection<?> oldValue = leftResult;
					leftResult = runRevision(leftOperator);
					change.firePropertyChange("leftresult", oldValue, leftResult);
				}
			}
			if (rightOperator != null) {
				if (rightOperator instanceof MultipleBaseRevisionOperator<?>) {
					Collection<?> oldValue = rightResult;
					rightResult = runRevision((MultipleBaseRevisionOperator<?>) rightOperator);
					change.firePropertyChange("rightresult", oldValue, rightResult);
				} else {
					Collection<?> oldValue = rightResult;
					rightResult = runRevision(leftOperator);
					change.firePropertyChange("rightresult", oldValue, rightResult);
				}
			}

		} catch (Exception e) {
			change.firePropertyChange("error", "", e.getMessage());
		}
	}

	/**
	 * Calculates and updates the resulting answer sets from the revision results.
	 * <p>
	 * This method uses the `ASPSolver` to compute the answer sets based on the
	 * `leftResult` and `rightResult` obtained from the revision process.
	 * It then fires property change events to notify listeners of the resulting
	 * answer sets for both left and right revisions.
	 * If an error occurs during parsing, an error property change event is fired.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public void calculateResultingAnswersets() {
		if (solver == null)
			return;
		if (leftResult != null) {
			try {
				Collection<AnswerSet> leftasl = solver.getModels(new Program((Collection<ASPRule>) leftResult));
				change.firePropertyChange("leftASL", null, leftasl);
				Collection<AnswerSet> rightasl = solver.getModels(new Program((Collection<ASPRule>) rightResult));
				change.firePropertyChange("rightASL", null, rightasl);
			} catch (Exception e) {
				change.firePropertyChange("error", "Parser Error", e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Executes the revision process using a multiple base revision operator.
	 * <p>
	 * This method converts the belief base and new beliefs to collections and
	 * applies the revision operator to produce a revised set.
	 * </p>
	 *
	 * @param op  the multiple base revision operator to use for the revision
	 *            process
	 * @param <E> the type of formula
	 * @return the collection of revised formulas
	 */
	@SuppressWarnings("unchecked")
	private <E extends Formula> Collection<E> runRevision(MultipleBaseRevisionOperator<E> op) {
		Collection<E> base = new HashSet<E>((Collection<? extends E>) beliefBase);
		Collection<E> extension = new HashSet<E>((Collection<? extends E>) newBeliefs);
		return op.revise(base, extension);
	}

	/**
	 * Executes the revision process using a single base revision operator.
	 * <p>
	 * This method converts the belief base and new beliefs to collections and
	 * applies the revision operator to produce a revised set.
	 * </p>
	 *
	 * @param op  the base revision operator to use for the revision process
	 * @param <E> the type of formula
	 * @return the collection of revised formulas
	 */
	@SuppressWarnings("unchecked")
	private <E extends Formula> Collection<E> runRevision(BaseRevisionOperator<E> op) {
		Collection<E> base = new HashSet<E>((Collection<? extends E>) beliefBase);
		E extension = (E) newBeliefs.iterator().next();
		return op.revise(base, extension);
	}
}
