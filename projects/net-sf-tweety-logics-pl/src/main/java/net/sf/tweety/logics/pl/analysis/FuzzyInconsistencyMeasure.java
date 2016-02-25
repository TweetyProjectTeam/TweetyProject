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
package net.sf.tweety.logics.pl.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.logics.pl.semantics.FuzzyInterpretation;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Contradiction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.logics.pl.syntax.Tautology;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.func.fuzzy.TCoNorm;
import net.sf.tweety.math.func.fuzzy.TNorm;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.Solver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * This measure implements the approach presented in [Thimm, 2016, to appear].
 * 
 * @author Matthias Thimm
 *
 */
public class FuzzyInconsistencyMeasure extends BeliefSetInconsistencyMeasure<PropositionalFormula>{

	/** static constant for the T-version of the measure */
	public static final byte TFUZZY_MEASURE = 0;
	/** static constant for the Sum-version of the measure */
	public static final byte SUMFUZZY_MEASURE = 1;
	
	/** The used T-norm */
	private TNorm tnorm;
	/** The used T-conorm*/
	private TCoNorm tconorm;
	
	/** One of TFUZZY_MEASURE, SUMFUZZY_MEASURE */
	private byte measure_version;
	
	/**
	 * Creates a new measure for the given T-norm and T-conorm.
	 * @param tnorm some T-norm
	 * @param tconorm some T-conorm
	 * @param measure_version one of TFUZZY_MEASURE, SUMFUZZY_MEASURE
	 */
	public FuzzyInconsistencyMeasure(TNorm tnorm, TCoNorm tconorm, byte measure_version){
		// both tnorm and tconorm cannot be nilpotent
		if(tnorm.isNilpotent())
			throw new IllegalArgumentException("T-norm must not be nilpotent");
		if(tconorm.isNilpotent())
			throw new IllegalArgumentException("T-conorm must not be nilpotent");
		this.tnorm = tnorm;
		this.tconorm = tconorm;
		this.measure_version = measure_version;
	}
	
	/**
	 * Creates a new measure (T Version) for the given T-norm and T-conorm.
	 * @param tnorm some T-norm
	 * @param tconorm some T-conorm
	 */
	public FuzzyInconsistencyMeasure(TNorm tnorm, TCoNorm tconorm){
		this(tnorm,tconorm,FuzzyInconsistencyMeasure.TFUZZY_MEASURE);
	}
	
	/**
	 * Creates a new measure for the given T-norm. Its dual
	 * is used as t-conorm.
	 * @param tnorm some T-norm
	 */
	public FuzzyInconsistencyMeasure(TNorm tnorm){
		this(tnorm, tnorm.getDualCoNorm());
	}
	
	/**
	 * Creates a new measure for the given T-norm. Its dual
	 * is used as t-conorm.
	 * @param tnorm some T-norm
	 * @param measure_version one of TFUZZY_MEASURE, SUMFUZZY_MEASURE
	 */
	public FuzzyInconsistencyMeasure(TNorm tnorm, byte measure_version){
		this(tnorm, tnorm.getDualCoNorm(), measure_version);
	}
	
	/**
	 * Returns a mathematical term representation of the given formula by replacing
	 * proposition by their given mathematical variables and replacing conjunction, disjunction,
	 * and negation by their fuzzy counterparts (taking the given t-norm and t-conorm into account). 
	 * @param formula some propositional formula
	 * @param assignments an assignment of proposition to variables
	 * @return the term representing the given formula
	 */
	private Term getTerm(PropositionalFormula formula, Map<Proposition,Variable> assignments){
		// we define this method recursively
		if(formula instanceof Proposition)
			return assignments.get((Proposition) formula);
		if(formula instanceof Tautology)
			return new FloatConstant(1);
		if(formula instanceof Contradiction)
			return new FloatConstant(0);
		if(formula instanceof Negation)
			return new FloatConstant(1).minus(this.getTerm(((Negation)formula).getFormula(), assignments));
		if(formula instanceof Conjunction){
			List<PropositionalFormula> conjuncts = ((Conjunction)formula).getFormulas();
			List<Term> conjunctTerms = new LinkedList<Term>();
			for(PropositionalFormula f: conjuncts)
				conjunctTerms.add(this.getTerm(f, assignments));
			return this.tnorm.evalTerm(conjunctTerms);
		}
		if(formula instanceof Disjunction){
			List<PropositionalFormula> disjuncts = ((Disjunction)formula).getFormulas();
			List<Term> disjunctsTerms = new LinkedList<Term>();
			for(PropositionalFormula f: disjuncts)
				disjunctsTerms.add(this.getTerm(f, assignments));
			return this.tconorm.evalTerm(disjunctsTerms);
		}		
		// this should not happen
		throw new RuntimeException("Unexpected type of formula");
	}

	/**
	 * Utility method 
	 * @param formulas
	 * @param assignments
	 * @return
	 */
	private Pair<Map<Variable,Term>,Double> constructAndSolveProblem(Collection<PropositionalFormula> formulas, Map<Proposition,Variable> assignments){
		Term t;		
		if(this.measure_version == FuzzyInconsistencyMeasure.TFUZZY_MEASURE){
			t = new FloatConstant(1).minus(this.getTerm(new Conjunction(formulas), assignments));
		}else{
			t = new FloatConstant(0);
			FloatConstant one = new FloatConstant(1);
			for(PropositionalFormula f: formulas)
				t = t.add(one.minus(this.getTerm(f, assignments)));
		}
		Solver solver = Solver.getDefaultGeneralSolver();
		OptimizationProblem p = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		p.setTargetFunction(t);
		try {
			Map<Variable,Term> result = solver.solve(p);
			return new Pair<Map<Variable,Term>,Double>(result,t.replaceAllTerms(result).doubleValue());
		} catch (GeneralMathException e) {
			return null;
		}
	}

	
	/**
	 * Returns an optimal interpretation as a witness for the inconsistency value.
	 * @param formulas a set of formulas
	 * @return an optimal interpretation as a witness for the inconsistency value.
	 */
	public FuzzyInterpretation getOptimalInterpretation(Collection<PropositionalFormula> formulas) {
		Map<Proposition,Variable> assignments = new HashMap<Proposition,Variable>();
		int idx = 0;		
		for(Proposition p: new Conjunction(formulas).getSignature())
			assignments.put(p, new FloatVariable("x" + idx++, 0, 1));
		Pair<Map<Variable,Term>,Double> result = this.constructAndSolveProblem(formulas, assignments);
		if(result != null){
			FuzzyInterpretation i = new FuzzyInterpretation();
			for(Proposition p: assignments.keySet())
				i.put(p, result.getFirst().get(assignments.get(p)).doubleValue());
			return i;
		}else return null;		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<PropositionalFormula> formulas) {
		Map<Proposition,Variable> assignments = new HashMap<Proposition,Variable>();
		int idx = 0;		
		for(Proposition p: new Conjunction(formulas).getSignature())
			assignments.put(p, new FloatVariable("x" + idx++, 0, 1));
		Pair<Map<Variable,Term>,Double> result = this.constructAndSolveProblem(formulas, assignments);
		if(result != null)
			return result.getSecond();
		else return -1d;
	}	
}
