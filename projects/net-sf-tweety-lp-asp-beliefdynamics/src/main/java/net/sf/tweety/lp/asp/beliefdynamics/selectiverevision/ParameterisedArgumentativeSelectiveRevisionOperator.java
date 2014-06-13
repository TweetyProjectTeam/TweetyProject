/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.lp.asp.beliefdynamics.selectiverevision;

import java.util.Collection;

import net.sf.tweety.arg.lp.semantics.attack.AttackStrategy;
import net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator;
import net.sf.tweety.beliefdynamics.selectiverevision.MultipleSelectiveRevisionOperator;
import net.sf.tweety.beliefdynamics.selectiverevision.MultipleTransformationFunction;
import net.sf.tweety.lp.asp.solver.Solver;
import net.sf.tweety.lp.asp.syntax.Rule;
import net.sf.tweety.lp.asp.beliefdynamics.baserevision.ELPBaseRevisionOperator;
import net.sf.tweety.lp.asp.beliefdynamics.baserevision.MonotoneGlobalMaxichoiceSelectionFunction;
import net.sf.tweety.lp.asp.beliefdynamics.baserevision.SelectionFunction;


/**
 * This class represents a selective revision using the base revision approach
 *  from [1] for the inner revision and the sceptical argumentative transformation 
 *  function from [2]. The selective revision operator is parameterised by two
 *  notions of attack used by the argumentation framework utilised by the transformation
 *  function. In [2] it is shown that there are at least 5 classes
 *  of distinct plausible instantiations of this operator: a/a, d/d, sa/sa, sa/a, and ca/ca
 *  where "a" stands for Attack, "d" for Defeat, "sa" for Strong Attack and "ca" for Confident
 *  Attack. For further details see the parameterisedhierarchy tweety project and [2]. 
 * 
 * [1] Kruempelmann, Patrick und Gabriele Kern-Isberner: 
 * 	Belief Base Change Operations for Answer Set Programming. 
 *  In: Cerro, Luis Farinas, Andreas Herzig und Jerome Mengin (Herausgeber):
 *  Proceedings of the 13th European conference on Logics in Artificial 
 *  Intelligence, Band 7519, Seiten 294-306, Toulouse, France, 2012. 
 *  Springer Berlin Heidelberg.
 * 
 * [2] Homann, Sebastian:
 *  Master thesis: Argumentationsbasierte selektive Revision von erweiterten logischen
 *  Programmen. 2013
 * 
 * @author Sebastian Homann
 *
 */
public class ParameterisedArgumentativeSelectiveRevisionOperator extends
		MultipleBaseRevisionOperator<Rule> {
	
	public enum TransformationType {
		SCEPTICAL, NAIVE;
		
		@Override
		public String toString() {
			   //only capitalize the first letter
			   String s = super.toString();
			   return s.substring(0, 1) + s.substring(1).toLowerCase();
			 }
	}
	
	private Solver solver;
	private AttackStrategy attackRelation;
	private AttackStrategy defenseRelation;
	private TransformationType transformationType;

	/**
	 * Constructs a new selective revision operator using the given attack relations
	 * and a asp solver.
	 * @param solver an answer set solver
	 * @param attackRelation a notion of attack
	 * @param defenseRelation a notion of attack
	 */
	public ParameterisedArgumentativeSelectiveRevisionOperator(Solver solver, AttackStrategy attackRelation, AttackStrategy defenseRelation, TransformationType type) {
		this.solver = solver;
		this.attackRelation = attackRelation;
		this.defenseRelation = defenseRelation;
		this.transformationType = type;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator#revise(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<Rule> revise(Collection<Rule> base,
			Collection<Rule> formulas) {
		
		// inner revision operator: base revision
		SelectionFunction<Rule> selection = new MonotoneGlobalMaxichoiceSelectionFunction();		
		MultipleBaseRevisionOperator<Rule> innerRevision;
		innerRevision = new ELPBaseRevisionOperator(solver, selection);
		
		// transformation function
		MultipleTransformationFunction<Rule> transformationFunction;
		switch(transformationType) {
			case NAIVE:
				transformationFunction = new NaiveLiteralTransformationFunction(base, attackRelation, defenseRelation);
				break;
			case SCEPTICAL:
			default:
				transformationFunction = new ScepticalLiteralTransformationFunction(base, attackRelation, defenseRelation);
				break;
		}

		MultipleSelectiveRevisionOperator<Rule> revisionOperator;
		revisionOperator = new MultipleSelectiveRevisionOperator<Rule>(transformationFunction, innerRevision);
		
		return revisionOperator.revise(base, formulas);
	}
	
	@Override
	public String toString() {
		return transformationType + " revision " + attackRelation.toAbbreviation() + "/" + defenseRelation.toAbbreviation();
	}

}
