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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.Equivalence;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.util.CardinalityConstraintEncoder;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;

/**
 * This class implements a SAT encoding of the contension inconsistency measure,
 * originally proposed in [Grant, Hunter. "Measuring consistency gain and information 
 * loss in step-wise inconsistency resolution",  ECSQARU'11]. 
 * <br>This measure is defined on paraconsistent models (three-valued models with
 * truth values F,T,B) of a knowledge base by taking the minimal number of
 * B-valued propositions needed in a model of the knowledge base.
 * 
 * @author Anna Gessler
 */
public class ContensionSatInconsistencyMeasure extends SatBasedInconsistencyMeasure {
	/**
	 * Create a new ContensionSatInconsistencyMeasure with the given SAT solver.
	 * @param solver some SAT solver
	 */
	public ContensionSatInconsistencyMeasure(SatSolver solver) {
		super(solver);
	}

	/**
	 * Create a new ContensionSatInconsistencyMeasure with the default SAT solver.
	 */
	public ContensionSatInconsistencyMeasure() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.tweetyproject.logics.commons.analysis.BeliefSetInconsistencyMeasure#
	 * inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<PlFormula> kb) {
		return super.binarySearchValue(kb, 0, ((PlBeliefSet) kb).getMinimalSignature().size());
	}

	@Override
	public PlBeliefSet getSATEncoding(Collection<PlFormula> kb, int upper_bound) {
		if (kb.isEmpty())
			return new PlBeliefSet();
		if (upper_bound == 0)
			return (PlBeliefSet) kb;
		PlSignature atoms = ((PlBeliefSet) kb).getMinimalSignature();
		if (upper_bound == atoms.size())
			return new PlBeliefSet();
			
		PlBeliefSet encoding = new PlBeliefSet();
		// For each atom, create 3 new atoms representing the 3 truth values
		// of three-valued logic.
		Set<Proposition> bAtoms = new HashSet<Proposition>(); // collect B atoms separately
		for (Proposition a : atoms) {
			Proposition a_T = new Proposition( "Tx" + a.getName() + "__T");
			Proposition a_B = new Proposition( "Tx"  +a.getName() + "__B");
			Proposition a_F = new Proposition( "Tx"  + a.getName() + "__F");
			bAtoms.add(a_B);

			// Then create a formula that represents that for each atom exactly one of the
			// 3 newly created atoms (T,B,F) is true
			Disjunction d1 = new Disjunction();
			d1.add(a_T, a_B, a_F);
			Disjunction d2 = new Disjunction();
			d2.add(new Negation(a_T), new Negation(a_B));
			Disjunction d3 = new Disjunction();
			d3.add(new Negation(a_T), new Negation(a_F));
			Disjunction d4 = new Disjunction();
			d4.add(new Negation(a_B), new Negation(a_F));
			Conjunction tvlRule = new Conjunction();
			tvlRule.add(d1, d2, d3, d4);
			encoding.add(tvlRule.toCnf());
		}

		for (PlFormula f : kb) {
			encoding.addAll(encodeSubformulas(f.clone()));
			String nT = generateFormulaAlias(f) + "__T";
			String nB = generateFormulaAlias(f) + "__B";
			Disjunction tvModels = new Disjunction();
			tvModels.add(new Proposition(nT), new Proposition(nB));
			encoding.add(tvModels);
		}

		CardinalityConstraintEncoder c = new CardinalityConstraintEncoder(bAtoms, upper_bound);
		PlBeliefSet cardinality_constraints = c.getSatEncoding();
		encoding.addAll(cardinality_constraints.toCnf());
		return encoding;
	}

	private PlBeliefSet encodeSubformulas(PlFormula f) {
		PlBeliefSet result = new PlBeliefSet();
		if (f instanceof Proposition)
			return result;
		else if (f instanceof Negation) {
			Negation n = (Negation) f;
			Proposition pT = new Proposition(generateFormulaAlias(f) + "__T");
			Proposition pB = new Proposition(generateFormulaAlias(f) + "__B");
			Proposition pF = new Proposition(generateFormulaAlias(f) + "__F");
			String iT = generateFormulaAlias(n.getFormula()) + "__T";
			String iB = generateFormulaAlias(n.getFormula()) + "__B";
			String iF = generateFormulaAlias(n.getFormula()) + "__F";
			Proposition piT = new Proposition(iT);
			Proposition piB = new Proposition(iB);
			Proposition piF = new Proposition(iF);
			Equivalence eT = new Equivalence(pT, piF);
			Equivalence eF = new Equivalence(pF, piT);
			Equivalence eB = new Equivalence(pB, piB);
			result.add(eT.toCnf(), eF.toCnf(), eB.toCnf());
			result.addAll(encodeSubformulas(n.getFormula()));
			return result;
		} else if (f instanceof Disjunction && !((Disjunction) f).isEmpty()) {
			Proposition pT = new Proposition(generateFormulaAlias(f) + "__T");
			Proposition pB = new Proposition(generateFormulaAlias(f) + "__B");
			Proposition pF = new Proposition(generateFormulaAlias(f) + "__F");
			Disjunction rest = (Disjunction) f.clone();
			Iterator<PlFormula> it = rest.iterator();
			PlFormula first = it.next();
			rest.remove(first);
			Proposition firstT = new Proposition(generateFormulaAlias(first) + "__T");
			Proposition firstB = new Proposition(generateFormulaAlias(first) + "__B");
			Proposition firstF = new Proposition(generateFormulaAlias(first) + "__F");
			result.addAll(encodeSubformulas(first));
			if (rest.isEmpty()) {
				Equivalence eT = new Equivalence(pT, firstT);
				Equivalence eF = new Equivalence(pF, firstF);
				Equivalence eB = new Equivalence(pB, firstB);
				result.add(eT.toCnf(), eF.toCnf(), eB.toCnf());
				return result;
			}
			Proposition restT = new Proposition(generateFormulaAlias(rest) + "__T");
			Proposition restF = new Proposition(generateFormulaAlias(rest) + "__F");
			result.addAll(encodeSubformulas(rest));
			Disjunction deT = new Disjunction();
			deT.add(firstT, restT);
			Conjunction ceF = new Conjunction();
			ceF.add(firstF, restF);
			Negation neB1 = new Negation(pT);
			Negation neB2 = new Negation(pF);
			Conjunction ceB = new Conjunction();
			ceB.add(neB1, neB2);
			Equivalence eT = new Equivalence(pT, deT);
			Equivalence eF = new Equivalence(pF, ceF);
			Equivalence eB = new Equivalence(pB, ceB);
			result.add(eT.toCnf(), eF.toCnf(), eB.toCnf());
			return result;
		} else if (f instanceof Conjunction && !((Conjunction) f).isEmpty()) {
			Conjunction rest = (Conjunction) f.clone();
			Iterator<PlFormula> it = rest.iterator();
			PlFormula first = it.next();
			rest.remove(first);
			Proposition pT = new Proposition(generateFormulaAlias(f) + "__T");
			Proposition pB = new Proposition(generateFormulaAlias(f) + "__B");
			Proposition pF = new Proposition(generateFormulaAlias(f) + "__F");
			String iT = generateFormulaAlias(first) + "__T";
			String iB = generateFormulaAlias(first) + "__B";
			String iF = generateFormulaAlias(first) + "__F";
			Proposition firstT = new Proposition(iT);
			Proposition firstB = new Proposition(iB);
			Proposition firstF = new Proposition(iF);
			result.addAll(encodeSubformulas(first));
			if (rest.isEmpty()) {
				Equivalence eT = new Equivalence(pT, firstT);
				Equivalence eF = new Equivalence(pF, firstF);
				Equivalence eB = new Equivalence(pB, firstB);
				result.add(eT.toCnf(), eF.toCnf(), eB.toCnf());
				return result;
			}
			Proposition restT = new Proposition(generateFormulaAlias(rest) + "__T");
			Proposition restF = new Proposition(generateFormulaAlias(rest) + "__F");
			Conjunction deT = new Conjunction();
			deT.add(firstT, restT);
			Disjunction ceF = new Disjunction();
			ceF.add(firstF, restF);
			Negation neB1 = new Negation(pT);
			Negation neB2 = new Negation(pF);
			Conjunction ceB = new Conjunction();
			ceB.add(neB1, neB2);
			Equivalence eT = new Equivalence(pT, deT);
			Equivalence eF = new Equivalence(pF, ceF);
			Equivalence eB = new Equivalence(pB, ceB);
			result.addAll(encodeSubformulas(rest));
			result.add(eT.toCnf(), eF.toCnf(), eB.toCnf());
			return result;
		} else if (f instanceof Implication) {
			Implication i = (Implication) f;
			Disjunction di = new Disjunction();
			di.add(new Negation(i.getFormulas().getFirst()), i.getFormulas().getSecond());
			result.addAll(encodeSubformulas(di));
			return result;
		}
		else
			return result;
	}

	private String generateFormulaAlias(PlFormula input) {
		String formula = input.toString();
		formula = formula.replaceAll("&&", "aND");
		formula = formula.replaceAll("\\|\\|", "OR");
		formula = formula.replaceAll("\\^\\^", "XOR");
		formula = formula.replaceAll("\\<\\=\\>", "IFF");
		formula = formula.replaceAll("\\=\\>", "IMPL");
		formula = formula.replaceAll("\\+", "TRUE");
		formula = formula.replaceAll("\\-", "FaLSE");
		formula = formula.replaceAll("\\!\\(", "NOTkK");
		formula = formula.replaceAll("\\!\\)", "NOTzZ");
		formula = formula.replaceAll("\\(", "FKLM");
		formula = formula.replaceAll("\\)", "ZKLM");
		formula = formula.replaceAll("\\s", "");	
		formula = formula.replaceAll("\\!", "NOT");
		return "Tx" + formula;
	}
	
	/**
	 * Returns the given formula in conjunctive normal form.
	 * 
	 * @param f formula
	 * @return formula in CNF
	 */
	@Override
	protected Conjunction getCnfEncoding(PlBeliefSet f) {
		return new Conjunction(f);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "contension (SAT-based)";
	}

}