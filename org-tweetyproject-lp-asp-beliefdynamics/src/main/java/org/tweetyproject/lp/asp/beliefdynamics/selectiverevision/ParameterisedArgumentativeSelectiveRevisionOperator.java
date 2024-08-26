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
package org.tweetyproject.lp.asp.beliefdynamics.selectiverevision;

import java.util.Collection;

import org.tweetyproject.arg.lp.semantics.attack.AttackStrategy;
import org.tweetyproject.beliefdynamics.MultipleBaseRevisionOperator;
import org.tweetyproject.beliefdynamics.selectiverevision.MultipleSelectiveRevisionOperator;
import org.tweetyproject.beliefdynamics.selectiverevision.MultipleTransformationFunction;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.beliefdynamics.baserevision.ELPBaseRevisionOperator;
import org.tweetyproject.lp.asp.beliefdynamics.baserevision.MonotoneGlobalMaxichoiceSelectionFunction;
import org.tweetyproject.lp.asp.beliefdynamics.baserevision.SelectionFunction;
import org.tweetyproject.lp.asp.reasoner.ASPSolver;


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
		MultipleBaseRevisionOperator<ASPRule> {

/**
 * Represents the type of transformation to be applied in a logic program or algorithm.
 */
public enum TransformationType {
    /**
     * Represents a skeptical transformation type.
     */
    SCEPTICAL,

    /**
     * Represents a naive transformation type.
     */
    NAIVE;

    @Override
    public String toString() {
        // Only capitalize the first letter and lowercase the rest
        String s = super.toString();
        return s.substring(0, 1) + s.substring(1).toLowerCase();
    }
}

	private ASPSolver solver;
	private AttackStrategy attackRelation;
	private AttackStrategy defenseRelation;
	private TransformationType transformationType;

	/**
	 * Constructs a new selective revision operator using the given attack relations
	 * and a asp solver.
	 * @param solver an answer set solver
	 * @param attackRelation a notion of attack
	 * @param defenseRelation a notion of attack
	 * @param type the type of transformation
	 */
	public ParameterisedArgumentativeSelectiveRevisionOperator(ASPSolver solver, AttackStrategy attackRelation, AttackStrategy defenseRelation, TransformationType type) {
		this.solver = solver;
		this.attackRelation = attackRelation;
		this.defenseRelation = defenseRelation;
		this.transformationType = type;
	}

	/*
	 * (non-Javadoc)
	 * @see org.tweetyproject.beliefdynamics.MultipleBaseRevisionOperator#revise(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<ASPRule> revise(Collection<ASPRule> base,
			Collection<ASPRule> formulas) {

		// inner revision operator: base revision
		SelectionFunction<ASPRule> selection = new MonotoneGlobalMaxichoiceSelectionFunction();
		MultipleBaseRevisionOperator<ASPRule> innerRevision;
		innerRevision = new ELPBaseRevisionOperator(solver, selection);

		// transformation function
		MultipleTransformationFunction<ASPRule> transformationFunction;
		switch(transformationType) {
			case NAIVE:
				transformationFunction = new NaiveLiteralTransformationFunction(base, attackRelation, defenseRelation);
				break;
			case SCEPTICAL:
			default:
				transformationFunction = new ScepticalLiteralTransformationFunction(base, attackRelation, defenseRelation);
				break;
		}

		MultipleSelectiveRevisionOperator<ASPRule> revisionOperator;
		revisionOperator = new MultipleSelectiveRevisionOperator<ASPRule>(transformationFunction, innerRevision);

		return revisionOperator.revise(base, formulas);
	}

	@Override
	public String toString() {
		return transformationType + " revision " + attackRelation.toAbbreviation() + "/" + defenseRelation.toAbbreviation();
	}

}
