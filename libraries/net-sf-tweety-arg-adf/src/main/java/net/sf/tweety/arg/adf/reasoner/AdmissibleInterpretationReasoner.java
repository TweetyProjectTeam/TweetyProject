package net.sf.tweety.arg.adf.reasoner;

import java.util.Collection;
import java.util.LinkedList;

import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

public class AdmissibleInterpretationReasoner extends AbstractDialecticalFrameworkReasoner {

	private IncrementalSatSolver solver;

	/**
	 * Creates a new reasoner.
	 * @param solver an incremental SAT solver
	 */
	public AdmissibleInterpretationReasoner(IncrementalSatSolver solver) {
		super();
		this.solver = solver;
	}

	@Override
	public Collection<Interpretation> getModels(AbstractDialecticalFramework adf) {
		SatEncoding enc = new SatEncoding(adf);
		Interpretation interpretation = new Interpretation(adf);
		SatSolverState state = solver.createState();
		state.add(enc.conflictFreeInterpretation());
		state.add(enc.largerInterpretation(interpretation));
		Collection<Interpretation> models = new LinkedList<Interpretation>();
		models.add(interpretation);
		int count = 0;
		while ((interpretation = existsAdm(adf, new Interpretation(adf), state, enc)) != null) {
			state.add(enc.refineUnequal(interpretation));
			System.out.println(++count + ": " + interpretation);
			models.add(interpretation);
		}
		System.out.println("done");
		try {
			state.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return models;
	}

	@Override
	public Interpretation getModel(AbstractDialecticalFramework adf) {
		return new Interpretation(adf);
	}

	private Interpretation existsAdm(AbstractDialecticalFramework adf, Interpretation interpretation,
			SatSolverState state, SatEncoding enc) {
//		Collection<PlFormula> clauses = new HashSet<PlFormula>(c);
//		clauses.addAll(enc.largerInterpretation(interpretation));
		net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness = solver.getWitness(state);
		Interpretation result = null;
		while (witness != null) {
			result = enc.interpretationFromWitness(witness);
			SatSolverState newState = solver.createState();
			Collection<Disjunction> verifyAdmissible = enc.verifyAdmissible(result);
			newState.add(verifyAdmissible);
			boolean sat = solver.isSatisfiable(newState);
			try {
				newState.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (sat) {
				Disjunction refineUnequal = enc.refineUnequal(result);
//				System.out.println(refineUnequal);
				state.add(refineUnequal);
			} else {
				return result;
			}
			witness = solver.getWitness(state);
		}
		return null;
	}
}
