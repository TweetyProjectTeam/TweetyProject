package net.sf.tweety.arg.adf.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

public class AdmissibleInterpretationReasoner extends AbstractDialecticalFrameworkReasoner {

	private SatSolver solver;

	/**
	 * @param solver
	 */
	public AdmissibleInterpretationReasoner(SatSolver solver) {
		super();
		this.solver = solver;
	}

	@Override
	public Collection<Interpretation> getModels(AbstractDialecticalFramework adf) {
		SatEncoding enc = new SatEncoding(adf);
		Set<PlFormula> clauses = new HashSet<PlFormula>();
		clauses.add(enc.conflictFreeInterpretation());
		Collection<Interpretation> models = new LinkedList<Interpretation>();
		Interpretation interpretation;
		int count = 0;
		while ((interpretation = existsAdm(adf, new Interpretation(adf), clauses, enc)) != null) {
			clauses.add(enc.refineLarger(interpretation));
			System.out.println(++count + ": " + interpretation);
			models.add(interpretation);
		}

		return models;
	}

	@Override
	public Interpretation getModel(AbstractDialecticalFramework adf) {
		// TODO Auto-generated method stub
		return null;
	}

	private Interpretation existsAdm(AbstractDialecticalFramework adf, Interpretation interpretation,
			Collection<PlFormula> clauses, SatEncoding enc) {
		clauses.add(enc.largerInterpretation(interpretation));
		net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness = solver.getWitness(clauses);
		Interpretation result = null;
		while (witness != null) {
			result = enc.interpretationFromWitness(witness);
			Collection<PlFormula> verifyAdmissible = enc.verifyAdmissible(result);
			boolean sat = solver.isSatisfiable(verifyAdmissible);
			if (sat) {
				clauses.add(enc.refineUnequal(result));
			} else {
				return result;
			}
			witness = solver.getWitness(clauses);
		}
		return null;
	}
}
