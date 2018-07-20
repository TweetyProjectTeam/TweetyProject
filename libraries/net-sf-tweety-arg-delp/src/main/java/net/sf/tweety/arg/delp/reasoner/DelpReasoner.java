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
package net.sf.tweety.arg.delp.reasoner;

import net.sf.tweety.arg.delp.semantics.ComparisonCriterion;
import net.sf.tweety.arg.delp.semantics.DelpAnswer;
import net.sf.tweety.arg.delp.semantics.DelpAnswer.Type;
import net.sf.tweety.arg.delp.semantics.DialecticalTree;
import net.sf.tweety.arg.delp.semantics.EmptyCriterion;
import net.sf.tweety.arg.delp.syntax.DefeasibleLogicProgram;
import net.sf.tweety.arg.delp.syntax.DelpArgument;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.logics.fol.syntax.FolFormula;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This reasoner performs default dialectical reasoning
 * on some given DeLP.
 *
 * @author Matthias Thimm
 *
 */
public class DelpReasoner implements Reasoner<DelpAnswer.Type,DefeasibleLogicProgram,FolFormula> {

	/**
	 * The comparison criterion is initialized with the "empty criterion"
	 */
	private ComparisonCriterion comparisonCriterion = new EmptyCriterion();

	/**
	 * Creates a new DelpReasoner for the given delp.
	 * @param beliefBase a delp.
	 * @param comparisonCriterion a comparison criterion used for inference
	 */
	public DelpReasoner(ComparisonCriterion comparisonCriterion) {
		this.comparisonCriterion = comparisonCriterion;
	}

	/**
	 * returns the comparison criterion used in this program
	 * @return the comparison criterion used in this program
	 */
	public ComparisonCriterion getComparisonCriterion() {
		return comparisonCriterion;
	}

	/**
	 * Computes the subset of the arguments of this program, that are warrants.
	 * @return a set of <source>DelpArgument</source> that are warrants
	 */
    public Set<DelpArgument> getWarrants(DefeasibleLogicProgram delp){
    	DefeasibleLogicProgram groundDelp = delp.ground();
        Set<DelpArgument> all_arguments = groundDelp.ground().getArguments();
		return all_arguments.stream()
                .filter(argument -> isWarrant(groundDelp,argument, all_arguments))
                .collect(Collectors.toSet());
	}

	/**
	 * Checks whether the given argument is a warrant regarding a given set of arguments
	 * @param argument a DeLP argument
	 * @param arguments a set of DeLP arguments
	 * @return <source>true</source> iff <source>argument</source> is a warrant given <source>arguments</source>.
	 */
	public boolean isWarrant(DefeasibleLogicProgram groundDelp, DelpArgument argument, Set<DelpArgument> arguments){
		DialecticalTree dtree = new DialecticalTree(argument);
		Deque<DialecticalTree> stack = new ArrayDeque<>();
		stack.add(dtree);
		while(!stack.isEmpty()){
			DialecticalTree dtree2 = stack.pop();
			stack.addAll(dtree2.getDefeaters(arguments,groundDelp,comparisonCriterion));
		}
		return dtree.getMarking().equals(DialecticalTree.Mark.UNDEFEATED);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.Reasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Type query(DefeasibleLogicProgram delp, FolFormula f) {
		// check query:
		if(!f.isLiteral())
			throw new IllegalArgumentException("Formula is expected to be a literal: "+f);
		if(!f.isGround())
			throw new IllegalArgumentException("Formula is expected to be ground: "+f);

        // compute answer:
        Set<FolFormula> conclusions = getWarrants(delp).stream()
                .map(DelpArgument::getConclusion)
                .collect(Collectors.toSet());
        if (conclusions.contains(f))
            return DelpAnswer.Type.YES;
        else if (conclusions.contains(f.complement()))
            return DelpAnswer.Type.NO;
        return DelpAnswer.Type.UNDECIDED;		
	}	
}
