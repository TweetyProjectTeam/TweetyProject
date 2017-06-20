package net.sf.tweety.arg.dung.prover;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.prover.constants.Problem;
import net.sf.tweety.arg.dung.prover.constants.Semantics;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Labeling;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;

/**
 * This is an example of a solver based on the "Tweety libraries for
 * logical aspects of artificial intelligence and knowledge representation".
 * Most of the used algorithms naively implement Dung's original semantics. 
 * 
 * @author Matthias Thimm
 */
public class TweetySolver extends AbstractDungSolver {

	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#versionInfo()
	 */
	@Override
	public String versionInfo() {
		return "TweetySolver v1.2\nMatthias Thimm (thimm@uni-koblenz.de)";
	}

	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#supportedProblems()
	 */
	@Override
	public Collection<Problem> supportedProblems() {
		Collection<Problem> problems = new HashSet<Problem>();
		for(Problem problem: Problem.values())
			problems.add(problem);
		return problems;
	}

	/**
	 * Returns the integer constant from Tweety of the given semantics. 
	 * @param semantics some semantics
	 * @return the corresponding integer constant from Tweety.
	 */
	private int translateSemantics(Semantics semantics){
		if(semantics.equals(Semantics.CO))
			return net.sf.tweety.arg.dung.semantics.Semantics.COMPLETE_SEMANTICS;
		if(semantics.equals(Semantics.GR))
			return net.sf.tweety.arg.dung.semantics.Semantics.GROUNDED_SEMANTICS;
		if(semantics.equals(Semantics.PR))
			return net.sf.tweety.arg.dung.semantics.Semantics.PREFERRED_SEMANTICS;
		if(semantics.equals(Semantics.ST))
			return net.sf.tweety.arg.dung.semantics.Semantics.STABLE_SEMANTICS;
		if(semantics.equals(Semantics.ADM))
			return net.sf.tweety.arg.dung.semantics.Semantics.ADMISSIBLE_SEMANTICS;
		if(semantics.equals(Semantics.CF))
			return net.sf.tweety.arg.dung.semantics.Semantics.CONFLICTFREE_SEMANTICS;
		if(semantics.equals(Semantics.SST))
			return net.sf.tweety.arg.dung.semantics.Semantics.SEMISTABLE_SEMANTICS;
		if(semantics.equals(Semantics.ID))
			return net.sf.tweety.arg.dung.semantics.Semantics.IDEAL_SEMANTICS;
		if(semantics.equals(Semantics.STG))
			return net.sf.tweety.arg.dung.semantics.Semantics.STAGE_SEMANTICS;
		if(semantics.equals(Semantics.CF2))
			return net.sf.tweety.arg.dung.semantics.Semantics.CF2_SEMANTICS;
		return -1;
	}
	
	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#solveDC(argc.constants.Semantics, net.sf.tweety.arg.dung.DungTheory, net.sf.tweety.arg.dung.syntax.Argument)
	 */
	@Override
	public boolean solveDC(Semantics semantics, DungTheory aaf, Argument arg) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(aaf, this.translateSemantics(semantics), net.sf.tweety.arg.dung.semantics.Semantics.CREDULOUS_INFERENCE);
		return reasoner.query(arg).getAnswerBoolean();
	}

	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#solveDS(argc.constants.Semantics, net.sf.tweety.arg.dung.DungTheory, net.sf.tweety.arg.dung.syntax.Argument)
	 */
	@Override
	public boolean solveDS(Semantics semantics, DungTheory aaf, Argument arg) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(aaf, this.translateSemantics(semantics), net.sf.tweety.arg.dung.semantics.Semantics.SCEPTICAL_INFERENCE);		
		return reasoner.query(arg).getAnswerBoolean();
	}

	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#solveDE(argc.constants.Semantics, net.sf.tweety.arg.dung.DungTheory, java.util.Collection)
	 */
	@Override
	public boolean solveDE(Semantics semantics, DungTheory aaf,	Collection<Argument> args) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(aaf, this.translateSemantics(semantics), net.sf.tweety.arg.dung.semantics.Semantics.SCEPTICAL_INFERENCE);
		for(Extension ext: reasoner.getExtensions()){
			if(ext.containsAll(args) && args.containsAll(ext))
				return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#solveDL(argc.constants.Semantics, net.sf.tweety.arg.dung.DungTheory, net.sf.tweety.arg.dung.semantics.Labeling)
	 */
	@Override
	public boolean solveDL(Semantics semantics, DungTheory aaf, Labeling lab) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(aaf, this.translateSemantics(semantics), net.sf.tweety.arg.dung.semantics.Semantics.SCEPTICAL_INFERENCE);
		for(Extension ext: reasoner.getExtensions())
			if(lab.equals(new Labeling(aaf,ext)))
				return true;		
		return false;
		
	}
	
	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#solveDX(argc.constants.Semantics, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public boolean solveDX(Semantics semantics, DungTheory aaf) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(aaf, this.translateSemantics(semantics), net.sf.tweety.arg.dung.semantics.Semantics.SCEPTICAL_INFERENCE);
		return !reasoner.getExtensions().isEmpty();
	}

	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#solveDN(argc.constants.Semantics, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public boolean solveDN(Semantics semantics, DungTheory aaf) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(aaf, this.translateSemantics(semantics), net.sf.tweety.arg.dung.semantics.Semantics.SCEPTICAL_INFERENCE);
		for(Extension ext: reasoner.getExtensions())
			if(!ext.isEmpty())
				return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#solveEC(argc.constants.Semantics, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public Collection<Argument> solveEC(Semantics semantics, DungTheory aaf) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(aaf, this.translateSemantics(semantics), net.sf.tweety.arg.dung.semantics.Semantics.SCEPTICAL_INFERENCE);
		Collection<Argument> result = new HashSet<Argument>();
		for(Extension ext: reasoner.getExtensions())
			result.addAll(ext);
		return result;
	}

	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#solveES(argc.constants.Semantics, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public Collection<Argument> solveES(Semantics semantics, DungTheory aaf) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(aaf, this.translateSemantics(semantics), net.sf.tweety.arg.dung.semantics.Semantics.SCEPTICAL_INFERENCE);
		Collection<Argument> result = new HashSet<Argument>(aaf);		
		for(Extension ext: reasoner.getExtensions())
			result.retainAll(ext);
		return result;
	}

	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#solveEE(argc.constants.Semantics, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public Collection<Collection<Argument>> solveEE(Semantics semantics, DungTheory aaf) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(aaf, this.translateSemantics(semantics), net.sf.tweety.arg.dung.semantics.Semantics.SCEPTICAL_INFERENCE);
		Collection<Collection<Argument>> result = new HashSet<Collection<Argument>>();
		result.addAll(reasoner.getExtensions());
		return result;
	}

	/* (non-Javadoc)
	 * @see argc.AbstractDungSolver#solveEL(argc.constants.Semantics, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public Collection<Labeling> solveEL(Semantics semantics, DungTheory aaf) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(aaf, this.translateSemantics(semantics), net.sf.tweety.arg.dung.semantics.Semantics.SCEPTICAL_INFERENCE);
		Collection<Labeling> result = new HashSet<Labeling>();
		for(Extension e: reasoner.getExtensions())
			result.add(new Labeling(aaf,e));		
		return result;
		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.probo.AbstractDungSolver#solveSE(net.sf.probo.constants.Semantics, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public Collection<Argument> solveSE(Semantics semantics, DungTheory aaf) {
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(aaf, this.translateSemantics(semantics), net.sf.tweety.arg.dung.semantics.Semantics.SCEPTICAL_INFERENCE);
		Collection<Extension> args = reasoner.getExtensions();
		if(args.isEmpty()) return null;
		return args.iterator().next();
	}
	
	/**
	 * The main method.
	 * @param args command line arguments
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException{
//		args = new String[8];
//		args[0] = "-p";
//		args[1] = "EE-CO";
//		args[2] = "-f";
//		args[3] = "/Users/mthimm/Shared/SVN/sourceforge-probo/trunk/examples/ex1.tgf";
//		args[4] = "-fo";
//		args[5] = "tgf";
//		args[6] = "-a";
//		args[7] = "";
		SatSolver.setDefaultSolver(new Sat4jSolver());
		new TweetySolver().execute(args);
	}
}
