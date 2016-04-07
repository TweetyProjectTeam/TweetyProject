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
import java.util.stream.Collectors;

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
	private ComparisonCriterion comparisonCriterion = new EmptyCriterion();

    private final DefeasibleLogicProgram groundDelp;

	/**
	 * Creates a new DelpReasoner for the given delp.
	 * @param beliefBase a delp.
	 * @param comparisonCriterion a comparison criterion used for inference
	 */
	public DelpReasoner(BeliefBase beliefBase, ComparisonCriterion comparisonCriterion) {
		super(beliefBase);
		if(!(beliefBase instanceof DefeasibleLogicProgram))
			throw new IllegalArgumentException("Knowledge base of class DefeasibleLogicProgram expected.");
        groundDelp = ((DefeasibleLogicProgram) beliefBase).ground();
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
        // check query:
		if(!(query instanceof FolFormula))
			throw new IllegalArgumentException("Formula of class FolFormula expected.");
		FolFormula f = (FolFormula) query;
		if(!f.isLiteral())
			throw new IllegalArgumentException("Formula is expected to be a literal: "+f);
		if(!f.isGround())
			throw new IllegalArgumentException("Formula is expected to be ground: "+f);

        // compute answer:
		DelpAnswer answer = new DelpAnswer(this.getKnowledgBase(),f);
        Set<FolFormula> conclusions = getWarrants().stream()
                .map(DelpArgument::getConclusion)
                .collect(Collectors.toSet());
        if (conclusions.contains(f))
            answer.setType(DelpAnswer.Type.YES);
        else if (conclusions.contains(f.complement()))
            answer.setType(DelpAnswer.Type.NO);
        else
            answer.setType(DelpAnswer.Type.UNDECIDED);
		return answer;
	}

	/**
	 * Computes the subset of the arguments of this program, that are warrants.
	 * @return a set of <source>DelpArgument</source> that are warrants
	 */
    public Set<DelpArgument> getWarrants(){
        Set<DelpArgument> all_arguments = groundDelp.getArguments();
		return all_arguments.stream()
                .filter(argument -> isWarrant(argument, all_arguments))
                .collect(Collectors.toSet());
	}

	/**
	 * Checks whether the given argument is a warrant regarding a given set of arguments
	 * @param argument a DeLP argument
	 * @param arguments a set of DeLP arguments
	 * @return <source>true</source> iff <source>argument</source> is a warrant given <source>arguments</source>.
	 */
	public boolean isWarrant(DelpArgument argument, Set<DelpArgument> arguments){
		DialecticalTree dtree = new DialecticalTree(argument);
		Deque<DialecticalTree> stack = new ArrayDeque<>();
		stack.add(dtree);
		while(!stack.isEmpty()){
			DialecticalTree dtree2 = stack.pop();
			stack.addAll(dtree2.getDefeaters(arguments,groundDelp,comparisonCriterion));
		}
		return dtree.getMarking().equals(DialecticalTree.Mark.UNDEFEATED);
	}
}
