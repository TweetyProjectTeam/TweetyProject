/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.delp;

import java.util.*;

import net.sf.tweety.arg.delp.semantics.*;
import net.sf.tweety.arg.delp.syntax.*;
import net.sf.tweety.commons.*;
import net.sf.tweety.logics.fol.syntax.*;

/**
 * This reasoner performs default dialectical reasoning
 * on some given DeLP.
 *
 * @author Matthias Thimm
 *
 */
public class DelpReasoner extends Reasoner {

	/**
	 * The comparison criterion is initialized with the "empty criterion"
	 */
	protected ComparisonCriterion comparisonCriterion = new EmptyCriterion();

	/**
	 * Creates a new DelpReasoner for the given delp.
	 * @param beliefBase a delp.
	 * @param comparisonCriterion a comparison criterion used for inference
	 */
	public DelpReasoner(BeliefBase beliefBase, ComparisonCriterion comparisonCriterion) {
		super(beliefBase);
		if(!(beliefBase instanceof DefeasibleLogicProgram))
			throw new IllegalArgumentException("Knowledge base of class DefeasibleLogicProgram expected.");
		this.comparisonCriterion = comparisonCriterion;
	}

	/**
	 * returns the comparison criterion used in this program
	 * @return the comparison criterion used in this program
	 */
	public ComparisonCriterion getComparisonCriterion() {
		return comparisonCriterion;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.Reasoner#query(net.sf.tweety.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		if(!(query instanceof FolFormula))
			throw new IllegalArgumentException("Formula of class FolFormula expected.");
		FolFormula f = (FolFormula) query;
		if(!f.isLiteral())
			throw new IllegalArgumentException("Formula is expected to be a literal.");
		DelpAnswer answer = new DelpAnswer(this.getKnowledgBase(),f);
		for(DelpArgument arg: this.getWarrants()){
			if(arg.getConclusion().equals(f)){
				answer.setAnswer(0d); // true
				return answer;
			}
		}
		answer.setAnswer(-1d); // false
		return answer;
	}

	/**
	 * Computes the subset of the arguments of this program, that are warrants.
	 * @return a set of <source>DelpArgument</source>
	 */
	public Set<DelpArgument> getWarrants(){
		DefeasibleLogicProgram groundDelp = ((DefeasibleLogicProgram) this.getKnowledgBase()).ground();
		Set<DelpArgument> arguments = new HashSet<DelpArgument>();
		Set<DelpArgument> all_arguments = groundDelp.getArguments();
		Iterator<DelpArgument> it = all_arguments.iterator();
		while(it.hasNext()){
			DelpArgument argument = it.next();
			if(this.isWarrant((DelpArgument)argument,all_arguments))
				arguments.add((DelpArgument)argument);
		}
		return arguments;
	}

	/**
	 * Checks whether the given argument is a warrant regarding a given set of arguments
	 * @param argument a DeLP argument
	 * @param arguments a set of DeLP arguments
	 * @return <source>true</source> iff <source>argument</source> is a warrant given <source>arguments</source>.
	 */
	private boolean isWarrant(DelpArgument argument, Set<DelpArgument> arguments){
		DefeasibleLogicProgram groundDelp = ((DefeasibleLogicProgram) this.getKnowledgBase()).ground();
		DialecticalTree dtree = new DialecticalTree(argument);
		Stack<DialecticalTree> stack = new Stack<DialecticalTree>();
		stack.add(dtree);
		while(!stack.isEmpty()){
			DialecticalTree dtree2 = stack.pop();
			stack.addAll(dtree2.getDefeaters(arguments,groundDelp,this.comparisonCriterion));
		}
		return dtree.getMarking().equals(DialecticalTree.MARK_UNDEFEATED);
	}

}
