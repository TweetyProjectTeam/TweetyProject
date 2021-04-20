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
	/** delegator for the implementation of the PropertyChange */
	private PropertyChangeSupport change = new PropertyChangeSupport(this);
	
	/** the left revision operator */
	protected BaseRevisionOperator<?> leftOperator;
	
	/** the right revision operator */
	protected BaseRevisionOperator<?> rightOperator;
	
	/** a set of selectable revison operators */
	protected Set<BaseRevisionOperator<?>> selectableOperators = new HashSet<BaseRevisionOperator<?>>();
	
	protected Program beliefBase;
	protected Program newBeliefs;
	
	protected Collection<?> leftResult;
	protected Collection<?> rightResult;
	
	protected ASPSolver solver;
	
	
	public SimpleRevisionCompareModel() {
		
	}
	
	public SimpleRevisionCompareModel(ASPSolver solver) {
		this.solver = solver;
	}
	
	public void setSolver(ASPSolver solver) {
		this.solver = solver;
	}
	
	/**
	 * Adds a property change listener
	 * @param listener	Reference to the listener
	 */
	public void addListener(PropertyChangeListener listener) {
		change.addPropertyChangeListener(listener);
	}
	
	/**
	 * Removes a property change listener
	 * @param listener	Reference to the listener
	 */
	public void removeListener(PropertyChangeListener listener) {
		change.removePropertyChangeListener(listener);
	}
	
	/**
	 * Select the left revision method.
	 * @param left	The revision method as an operator reference
	 */
	public void setLeftOperator(BaseRevisionOperator<?> left) {
		if(selectableOperators.contains(left)) {
			BaseRevisionOperator<?> old = this.leftOperator;
			this.leftOperator = left;
			change.firePropertyChange("leftOperator", old, left);
		}
	}
	
	/**
	 * Select the right revision method.
	 * @param right	The revision method as an operator reference
	 */
	public void setRightOperator(BaseRevisionOperator<?> right) {
		if(selectableOperators.contains(right)) {
			BaseRevisionOperator<?> old = this.rightOperator;
			this.rightOperator = right;
			change.firePropertyChange("rightOperator", old, right);
		}
	}
	
	/**
	 * Adds a belief base to the end of the ordered list of belief bases.
	 * @param beliefBase	Reference to the new belief base
	 * @throws ParseException if parsing failed
	 */
	public void setBeliefbase(String beliefBase) throws ParseException {
		Program old = this.beliefBase;
		this.beliefBase = ASPParser.parseProgram(beliefBase); //TODO test with new parser
		change.firePropertyChange("beliefBase", old, beliefBase);
	}
	
	public void setBeliefbase(Reader beliefBase) throws ParseException {
		Program old = this.beliefBase;
		this.beliefBase = ASPParser.parseProgram(beliefBase);
		change.firePropertyChange("beliefBase", old, this.beliefBase.toString());
	}

	public void setNewBeliefs(String newBeliefs) throws ParseException {
		Program old = this.newBeliefs;
		this.newBeliefs =  ASPParser.parseProgram(newBeliefs);
		change.firePropertyChange("newbeliefs", old, newBeliefs);
	}
	
	public void setNewBeliefs(Reader newBeliefs) throws ParseException {
		Program old = this.newBeliefs;
		this.newBeliefs =  ASPParser.parseProgram(newBeliefs);
		change.firePropertyChange("newbeliefs", old, this.newBeliefs.toString());
	}
	
	/**
	 * Adds the given revision operator to the set of selectable revision methods.
	 * @param op	Reference to the operator, if the operator is in the set nothing happens
	 */
	public void addOperator(BaseRevisionOperator<?> op) {
		if(this.selectableOperators.add(op)) {
			change.firePropertyChange("selectableOperators", null, op);
		}
	}
	
	/**
	 * Removes the given revision operator from the set of selectable revision methods.
	 * @param op Reference to the operator which is removed if it is member of the set of revision methods.
	 */
	public void removeOperator(BaseRevisionOperator<?> op) {
		if(this.selectableOperators.remove(op)) {
			change.firePropertyChange("selectableOperators", op, null);
		}
	}
	
	public void runRevisions() {
		if(beliefBase == null || newBeliefs == null) {
			return;
		}
		
		try{
			if(leftOperator != null) {
				if(leftOperator instanceof MultipleBaseRevisionOperator<?>) {
					Collection<?> oldValue = leftResult;
					leftResult = runRevision((MultipleBaseRevisionOperator<?>)leftOperator);
					change.firePropertyChange("leftresult", oldValue, leftResult);
				} else {
					Collection<?> oldValue = leftResult;
					leftResult = runRevision(leftOperator);
					change.firePropertyChange("leftresult", oldValue, leftResult);
				}
			}
			if(rightOperator != null) {
				if(rightOperator instanceof MultipleBaseRevisionOperator<?>) {
					Collection<?> oldValue = rightResult;
					rightResult = runRevision((MultipleBaseRevisionOperator<?>)rightOperator);
					change.firePropertyChange("rightresult", oldValue, rightResult);
				} else {
					Collection<?> oldValue = rightResult;
					rightResult = runRevision(leftOperator);
					change.firePropertyChange("rightresult", oldValue, rightResult);
				}
			}
			
		} catch(Exception e) {
			change.firePropertyChange("error", "", e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void calculateResultingAnswersets() {
		if(solver == null) 
			return;
		if(leftResult != null) {
			try {
				Collection<AnswerSet> leftasl = solver.getModels(new Program((Collection<ASPRule>)leftResult));
				change.firePropertyChange("leftASL", null, leftasl);
				Collection<AnswerSet> rightasl = solver.getModels(new Program((Collection<ASPRule>)rightResult));
				change.firePropertyChange("rightASL", null, rightasl);
			} catch (Exception e) {
				change.firePropertyChange("error", "Parser Error", e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <E extends Formula> Collection<E> runRevision(MultipleBaseRevisionOperator<E> op) {
		Collection<E> base = new HashSet<E>((Collection<? extends E>) beliefBase);
		Collection<E> extension = new HashSet<E>((Collection<? extends E>) newBeliefs);
		return op.revise(base, extension);
	}
	@SuppressWarnings("unchecked")
	private <E extends Formula> Collection<E> runRevision(BaseRevisionOperator<E> op) {
		Collection<E> base = new HashSet<E>((Collection<? extends E>) beliefBase);
		E extension = (E) newBeliefs.iterator().next();
		return op.revise(base, extension);
	}
}
