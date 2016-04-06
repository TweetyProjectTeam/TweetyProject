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

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.tweety.arg.delp.semantics.GeneralizedSpecificity;
import net.sf.tweety.arg.delp.syntax.DefeasibleRule;
import net.sf.tweety.arg.delp.syntax.DelpArgument;
import net.sf.tweety.arg.delp.syntax.DelpFact;
import net.sf.tweety.arg.delp.syntax.DelpRule;
import net.sf.tweety.arg.delp.syntax.StrictRule;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.commons.util.rules.Derivation;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;

/**
 * This class models a defeasible logic program (DeLP).
 *
 * @author Matthias Thimm
 *
 */
public class DefeasibleLogicProgram extends BeliefSet<DelpRule>{

	/**
	 * Default constructor; initializes empty delpFacts, strict and defeasible rules
	 * and empty comparison criterion.
	 */
	public DefeasibleLogicProgram(){
		super();		
	}

	/**
	 * constructor; initializes this program with the given program
	 * @param delp a defeasible logic program
	 */
	public DefeasibleLogicProgram(DefeasibleLogicProgram delp){
		super(delp);		
	}

	/**
	 * In general, a delp comprises of rule schemes with variables. This methods returns the
	 * corresponding grounded theory, i.e., all schematic elements are replaced with all their grounded instances, where
	 * all occurring variables are replaced with constants in every possible way. The set of constants used is the set
	 * of constants appearing in this delp.
	 * @return the grounded version of <source>this</source>
	 */
	DefeasibleLogicProgram ground(){
		return this.ground(((FolSignature)this.getSignature()).getConstants());
	}
	
	/**
	 * In general, a delp comprises of rule schemes with variables. This methods returns the
	 * corresponding grounded theory, i.e., all schematic elements are replaced with all their grounded instances, where
	 * all occurring variables are replaced with constants in every possible way.
	 * @param constants some set of constants. 
	 * @return the grounded version of <source>this</source>
	 */
	private DefeasibleLogicProgram ground(Set<Constant> constants){
		if(this.isGround()) return new DefeasibleLogicProgram(this);
		DefeasibleLogicProgram groundedDelp = new DefeasibleLogicProgram();
		for(DelpRule rule: this)
			groundedDelp.addAll(rule.allGroundInstances(constants).stream()
					.map(groundedRule -> (DelpRule) groundedRule)
					.collect(Collectors.toList()));
		return groundedDelp;
	}

	/** 
	 * This method translates this delp into an abstract Dung theory. All arguments, that can
	 * be built in this theory are interpreted as abstract arguments. The attack relation is built using
	 * the dialectical proof theory of delp.
	 * @return the abstract Dung theory induced by this delp.
	 */
	public DungTheory getDungTheory(){
		DungTheory dungTheory = new DungTheory();
        for (DelpArgument arg1 : getArguments()) {
            //add arguments
            dungTheory.add(new Argument(arg1.toString()));
            //add attacks
            for (DelpArgument arg2 : getArguments()) {
                if (arg1.getDisagreementSubargument(arg2.getConclusion(), this) != null) {
                    dungTheory.add(new Attack(new Argument(arg2.toString()), new Argument(arg1.toString())));
                }
            }
        }
		return dungTheory;
	}
	
	/**
	 * Returns the set of all possible arguments, that can be built in this delp.
	 * @return the set of all possible arguments, that can be built in this delp.
	 */
	public Set<DelpArgument> getArguments(){
		if(!this.isGround())
			throw new IllegalArgumentException("This program must be grounded first before computing arguments.");
		Set<Derivation<DelpRule>> derivations = Derivation.allDerivations(this);
		Set<DelpArgument> arguments = new HashSet<>();
		for(Derivation<DelpRule> derivation: derivations){
			Set<DefeasibleRule> rules = derivation.stream()
                    .filter(rule -> rule instanceof DefeasibleRule)
                    .map(rule -> (DefeasibleRule) rule)
                    .collect(Collectors.toSet());
            // consistency check: rules have to be consistent with strict knowledge part
			if(isConsistent(rules))
				arguments.add(new DelpArgument(rules,(FolFormula)derivation.getConclusion()));
		}
		// subargument test
		Set<DelpArgument> result = new HashSet<>();
		for(DelpArgument argument1: arguments){
			boolean is_minimal = true;
			for(DelpArgument argument2: arguments){
				if(argument1.getConclusion().equals(argument2.getConclusion()) && argument2.isStrongSubargumentOf(argument1)){
					is_minimal = false;
					break;
				}
			}
			if(is_minimal) result.add(argument1);
		}
		return result;
	}

	/**
	 * Computes the strict closure of the program, i.e., the set of all strictly derivable literals. For this computation the program
	 * may be extended by the given parameters
	 * @param literals a set of literals
	 * @param defeasibleRules a set of defeasible rules
	 * @param usefacts set to <source>true</source> iff the delpFacts of this program shall be used in computing the closure
	 * @return the closure of this program and the given parameters
	 */
	public Set<FolFormula> getStrictClosure(Set<FolFormula> literals,
                                            Set<DefeasibleRule> defeasibleRules,
                                            boolean usefacts){
		if(!isGround())
			throw new IllegalArgumentException("Delp must be grounded first.");
		Set<FolFormula> strictClosure = new HashSet<>(literals);
		if(usefacts){
            strictClosure.addAll(this.stream()
                    .filter(rule -> rule instanceof DelpFact)
                    .map(DelpRule::getConclusion)
                    .collect(Collectors.toList()));
		}
		boolean modified = true;
		Set<StrictRule> rules = this.stream()
                .filter(rule -> rule instanceof StrictRule)
                .map(rule -> (StrictRule) rule)
                .collect(Collectors.toSet());
        for (DefeasibleRule rule : defeasibleRules) {
            Set<FolFormula> premise = rule.getPremise().stream()
                    .map(f -> (FolFormula) f)
                    .collect(Collectors.toSet());
            rules.add(new StrictRule(rule.getConclusion(), premise));
        }
		while(modified){
			modified = false;
			Set<StrictRule> rules2 = new HashSet<>();
            for (StrictRule rule : rules) {
                if (rule.isApplicable(strictClosure)) {
                    strictClosure.add(rule.getConclusion());
                    modified = true;
                } else rules2.add(rule);
            }
			rules = rules2;
		}
		return strictClosure;
	}

	/**
	 * Computes the strict closure of the program, i.e., the set of all strictly derivable literals.
     * The program is extended with delpFacts and defeasible rules (which are interpreted as strict rules here)
     * described by the parameters <source>literals</source> and <source>defeasibleRules</source>.
	 * @param literals a set of literals
	 * @param defeasibleRules a set of defeasible rules
	 * @return the set of all strictly derivable literals.
	 */
    public Set<FolFormula> getStrictClosure(Set<FolFormula> literals,
                                             Set<DefeasibleRule> defeasibleRules){
		return getStrictClosure(literals,defeasibleRules,true);
	}

	/**
	 * Computes the strict closure of the program, i.e., the set of all strictly derivable literals.
     * The program is extended with delpFacts described by the parameter <source>literals</source>
	 * @param literals a set of literals
	 * @return the set of all strictly derivable literals.
	 */
	public Set<FolFormula> getStrictClosure(Set<FolFormula> literals){
		return getStrictClosure(literals,Collections.emptySet());
	}

	/**
	 * Computes the strict closure of the program, i.e., the set of all strictly derivable literals.
	 * @return the set of all strictly derivable literals.
	 */
	public Set<FolFormula> getStrictClosure(){
		return getStrictClosure(Collections.emptySet());
	}

	/**
	 * Checks whether the given set of defeasible rules are consistent given the strict part of this
	 * program.
	 * @param rules a set of defeasible rules
	 * @return <source>false</source> if the union of this program's delpFacts and strict rules with the given set
	 * 	of defeasible rules defeasibly derives two complementary literals
	 */
	public boolean isConsistent(Set<DefeasibleRule> rules){
		if(!isGround())	
			throw new IllegalArgumentException("Delp must be ground.");
		DefeasibleLogicProgram delp = this.stream()
                .filter(rule -> rule instanceof DelpFact || rule instanceof StrictRule)
                .collect(Collectors.toCollection(DefeasibleLogicProgram::new));
        delp.addAll(rules.stream()
                .map(DefeasibleRule::toStrictRule)
                .collect(Collectors.toList()));
		Set<FolFormula> closure = delp.getStrictClosure();
        for (FolFormula aClosure : closure)
            if (closure.contains(aClosure.complement()))
                return false;
		return true;
	}

	/**
	 * Checks whether the given set of literals disagree with respect to the strict part of this program.
	 * @param literals a set of literals
	 * @return <source>true</source> if the union of this program's delpFacts and strict rules with the given set
	 * 	of literals defeasibly derives two complementary literals
	 */
	public boolean disagree(Set<FolFormula> literals){
		if(!isGround()) 
			throw new IllegalArgumentException("Delp must be grounded first.");
		DefeasibleLogicProgram delp = new DefeasibleLogicProgram(this);
        delp.addAll(literals.stream()
                .map(DelpFact::new)
                .collect(Collectors.toList()));
		Set<FolFormula> closure = delp.getStrictClosure();
        for (FolFormula aClosure : closure)
            if (closure.contains(aClosure.complement()))
                return true;
		return false;
	}

    private boolean isGround(){
        return this.stream().allMatch(DelpRule::isGround);
	}

	public String toString(){
        return this.stream().map(Object::toString).collect(Collectors.joining("\n"))+"\n";
	}

	/**
	 * Returns all defeasible and strict rules appearing in this program with the given literal as head
	 * @param l a literal
	 * @return a set of strict and defeasible rules
	 */
	public Set<DelpRule> getRulesWithHead(FolFormula l){
		return this.stream()
                .filter(rule -> (rule instanceof DefeasibleRule || rule instanceof StrictRule)
                        && rule.getConclusion().equals(l))
                .collect(Collectors.toSet());
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefBase#getSignature()
	 */
	@Override
	public Signature getSignature() {
		FolSignature signature = new FolSignature();
		for(DelpRule rule: this){
			signature.addAll(rule.getPredicates());
			signature.addAll(rule.getTerms(Constant.class));
		}
		return signature;
	}
}
