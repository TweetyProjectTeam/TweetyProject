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
package net.sf.tweety.arg.delp.semantics;

import java.util.*;

import net.sf.tweety.arg.delp.*;
import net.sf.tweety.arg.delp.syntax.*;
import net.sf.tweety.logics.fol.syntax.*;

/**
 * This class implements a node of a dialectical tree from DeLP.
 *
 * @author Matthias Thimm
 *
 */
public class DialecticalTree{

	/**
	 * Marking indicator; indicates a defeated node
	 */
	public final static String MARK_DEFEATED = "D";

	/**
	 * Marking indicator; indicates an undefeated node
	 */
	public final static String MARK_UNDEFEATED = "U";

	/**
	 * The argument in this node
	 */
	protected DelpArgument argument;

	/**
	 * The parent node; <source>null</source> if this node is a root
	 */
	protected DialecticalTree parent;

	/**
	 * The children of this node; size=0 indicates a leaf
	 */
	protected Set<DialecticalTree> children;

	/**
	 * constructor; initializes this dialectical tree node as a root with given argument
	 * @param argument a DeLP argument
	 */
	public DialecticalTree(DelpArgument argument){
		this.argument = argument;
		this.parent = null;
		this.children = new HashSet<DialecticalTree>();
	}

	/**
	 * constructor; initializes this dialectical node with the given argument and
	 * given dialectical tree node as parent.
	 * @param parent
	 * @param argument
	 */
	public DialecticalTree(DialecticalTree parent, DelpArgument argument){
		this.parent = parent;
		this.argument = argument;
		this.children = new HashSet<DialecticalTree>();
	}

	/**
	 * Computes the set of arguments which are defeaters for the argument in this tree node and returns
	 * the corresponding dialectical tree nodes with these defeaters. For the computation of the defeaters
	 * the whole argumentation line to this tree node is considered. As a side effect the computed tree nodes
	 * are added as children of this node
	 * @param arguments a set of arguments
	 * @param delp a defeasible logic program
	 * @param comparisonCriterion a comparison criterion.
	 * @return the set of defeater nodes of the argument in this node
	 */
	public Set<DialecticalTree> getDefeaters(Set<DelpArgument> arguments, DefeasibleLogicProgram delp, ComparisonCriterion comparisonCriterion){
		Set<FolFormula> attackOpportunities = ((DelpArgument)argument).getAttackOpportunities(delp);
		Iterator<FolFormula> it = attackOpportunities.iterator();
		Set<DelpArgument> attacks = new HashSet<DelpArgument>();
		//gather attacks of last argument in the line
		while(it.hasNext()){
			FolFormula lit = it.next();
			Iterator<DelpArgument> arg_it = arguments.iterator();
			while(arg_it.hasNext()){
				DelpArgument argument = arg_it.next();
				if(((DelpArgument)argument).getConclusion().equals(lit))
					attacks.add(argument);
			}
		}
		//for each attacker check acceptability
		Set<DelpArgument> defeater = new HashSet<DelpArgument>();
		Iterator<DelpArgument> attacker_it = attacks.iterator();
		while(attacker_it.hasNext()){
			DelpArgument attack = attacker_it.next();
			if(isAcceptable((DelpArgument)attack,delp,comparisonCriterion))
				defeater.add(attack);
		}
		//build dialectical tree nodes
		Iterator<DelpArgument> defeater_it = defeater.iterator();
		Set<DialecticalTree> defeaterNodes = new HashSet<DialecticalTree>();
		while(defeater_it.hasNext()){
			DialecticalTree dtree = new DialecticalTree(this,defeater_it.next());
			defeaterNodes.add(dtree);
		}
		children = defeaterNodes;
		return defeaterNodes;
	}

	/**
	 * Checks whether the argumentation line composed of the ancestors of this node and the parameter
	 * <source>argument</source> is acceptable given the parameter <source>delp</source>
	 * @param argument a DeLP argument
	 * @param delp a defeasible logic program
	 * @param comparisonCriterion a comparison criterion.
	 * @return <source>true</source> if the corresponding argumentation line is acceptable
	 */
	public boolean isAcceptable(DelpArgument argument, DefeasibleLogicProgram delp, ComparisonCriterion comparisonCriterion){
		List<DelpArgument> argumentationLine = this.getArgumentationLine();
		DelpArgument disagreementSubargument = ((DelpArgument)argumentationLine.get(argumentationLine.size()-1)).getDisagreementSubargument(argument.getConclusion(), delp);
		//Subargument test
		Iterator<DelpArgument> it = argumentationLine.iterator();
		while(it.hasNext()){
			DelpArgument arg = (DelpArgument) it.next();
			if(argument.isSubargumentOf(arg))
				return false;
		}
		//Concordance
		Set<DefeasibleRule> rules = new HashSet<DefeasibleRule>();
		for(int i = argumentationLine.size()-2; i >= 0; i -= 2)
			rules.addAll(((DelpArgument)argumentationLine.get(i)).getSupport());
		rules.addAll(argument.getSupport());
		if(!delp.isConsistent(rules))
			return false;
		//Blocking attack
		if(comparisonCriterion.compare(argument, disagreementSubargument, delp) == ComparisonCriterion.IS_WORSE)
			return false;
		//Proper attack
		if(argumentationLine.size()>1){
			DelpArgument arg1 = ((DelpArgument)argumentationLine.get(argumentationLine.size()-1));
			DelpArgument arg2 = ((DelpArgument)argumentationLine.get(argumentationLine.size()-2)).getDisagreementSubargument(arg1.getConclusion(), delp);
			if(comparisonCriterion.compare(arg1, arg2, delp) == ComparisonCriterion.NOT_COMPARABLE)
				if(comparisonCriterion.compare(argument, disagreementSubargument, delp) != ComparisonCriterion.IS_BETTER)
					return false;
		}
		return true;
	}

	/**
	 * Returns the argumentation line which is generated by the path from this node to the root (in reverse)
	 * @return a list of arguments
	 */
	public List<DelpArgument> getArgumentationLine(){
		List<DelpArgument> argumentationLine = new ArrayList<DelpArgument>();
		if(parent != null)
			argumentationLine.addAll(parent.getArgumentationLine());
		argumentationLine.add(argument);
		return argumentationLine;
	}

	/**
	 * Computes the marking of this node by considering the marking of all child nodes
	 * @return one of DialecticalTree.MARK_DEFEATED and DialecticalTree.MARK_UNDEFEATED
	 */
	public String getMarking(){
		Iterator<DialecticalTree> it = children.iterator();
		while(it.hasNext())
			if(it.next().getMarking().equals(DialecticalTree.MARK_UNDEFEATED))
				return DialecticalTree.MARK_DEFEATED;
		return DialecticalTree.MARK_UNDEFEATED;
	}

	//Misc Methods

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String s = "["+argument;
		Iterator<DialecticalTree> it = children.iterator();
		if(it.hasNext())
			s +=  " - "+it.next();
		while(it.hasNext())
			s+= ", "+it.next();
		s += " ]";
		return s;
	}

}
