package org.tweetyproject.logics.translators.adfrevision;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.adf.io.KppADFFormatParser;
import org.tweetyproject.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner;
import org.tweetyproject.arg.adf.reasoner.PreferredReasoner;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * This class implements a revision operator for Abstract Dialectical Frameworks (ADFs)
 * @author Jonas Schumacher
 *
 */
public class PreferredRevisionerADF {
	
	/**
	 * Revise an ADF with a Propositional Formula information-modular preorders
	 * @param args arguments
	 * @throws FileNotFoundException FileNotFoundException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		// Set path to ADF and psi
		String path_to_adf = "src/main/resources/preferred_ex8_adf.txt";
		String path_to_psi = "src/main/resources/preferred_ex8_psi.txt";
		
		/*
		 * Step 1: Create an ADF D from text file
		 */
		System.out.println("Step 1: Read ADF from file:");
		NativeMinisatSolver solver = new NativeMinisatSolver();
		LinkStrategy strat = new SatLinkStrategy(solver);
		KppADFFormatParser parser_adf = new KppADFFormatParser(strat, true);
		AbstractDialecticalFramework adf = parser_adf.parse(new File(path_to_adf));
		for (Argument ar : adf.getArguments()) {
			System.out.println("Argument: \t\t " + ar);
			AcceptanceCondition ac = adf.getAcceptanceCondition(ar);
			System.out.println("Acceptance Condition \t" + ac);
		}
		
		System.out.println("------------------------------------------");
		
		/*
		 * Step 2: Calculate preferred models of D
		 */
		System.out.println("Step 2: Calculate preferred extensions:");
		AbstractDialecticalFrameworkReasoner reasoner_adf = new PreferredReasoner(solver);
		Collection<Interpretation> extensions = reasoner_adf.getModels(adf);
		System.out.println(extensions);
		System.out.println("------------------------------------------");
		
		/*
		 * Step 3: Move from ADF vocabulary to 3-valued propositional logic:
		 */
		
		// Translate extensions into PriestWorlds
		System.out.println("Step 3: Translate extensions to (3-valued) PriestWorlds:");
		HashSet<PriestWorldAdapted> extensions_priest = new HashSet<PriestWorldAdapted>();
		for (Interpretation ex : extensions) {
			
			PriestWorldAdapted pw = new PriestWorldAdapted();
			for (Argument arg : ex.satisfied()) {
				pw.set(new Proposition(arg.getName()), PriestWorldAdapted.TruthValue.TRUE);
			}
			for (Argument arg : ex.unsatisfied()) {
				pw.set(new Proposition(arg.getName()), PriestWorldAdapted.TruthValue.FALSE);
			}
			for (Argument arg : ex.undecided()) {
				pw.set(new Proposition(arg.getName()), PriestWorldAdapted.TruthValue.BOTH);
			}
			extensions_priest.add(pw);
			System.out.println(ex + " >>> " + pw);
		}
		
		// Extract Signature from ADF
		PlSignature sig = new PlSignature();
		for (Argument arg : adf.getArguments()) {
			sig.add(new Proposition(arg.getName()));
		}
		System.out.println("Signature: " + sig);
		
		System.out.println("------------------------------------------");
		
		/*
		 * Step 4: Create preorder induced by function f which:
		 * A) must be faithful (as was necessary for 2-valued models)
		 * B) must be information-modular (incomparable with regards to their information content)
		 * C) must have an additional quality measure (e.g. Dalal) 
		 * Motivation: take lexicographic approach (A > B > C)
		 */
		System.out.println("Step 4: Create preorder using a lexicographic approach:");
		
		// Create empty 3-valued preorder:
		RankingFunctionThreeValued kappa = new RankingFunctionThreeValued(sig);
		
		/*
		 * Guarantee A) by setting the rank of all worlds to +1
		 * While leaving the rank of the extensions at 0
		 */
		System.out.println("Step 4.1: guarantee faithfulness");
		
		PriestWorldIterator pwIterator = new PriestWorldIterator(sig);
		while (pwIterator.hasNext()) {
			PriestWorldAdapted pw = pwIterator.next();
			if (!extensions_priest.contains(pw)) {
				kappa.setRank(pw, 1);
			}
		}

		System.out.println(kappa);
		
		/**
		 * Guarantee B) by sorting in order of information content
		 * Make sure Dalal doesnt re-merge partitions
		 */
		
		/*
		 * reverse_order = false: the more information the lower the rank (example 8)
		 * reverse_order = true: the more information the higher the rank (example 9)
		 */
		System.out.println("Step 4.2: guarantee information-modularity");

		boolean reverse_order = false;
		if (reverse_order) {
			System.out.println("Here: Use inverse imf-ordering (more information = higher rank)");;
		}
		else {
			System.out.println("Here: Use regular imf-ordering (more information = lower rank)");
		}
		
		int largest_num_undec_possible = sig.size();
		int largest_shift_by_dalal = 2*sig.size();
		pwIterator = (PriestWorldIterator) pwIterator.reset();
		while (pwIterator.hasNext()) {
			PriestWorldAdapted pw = pwIterator.next();
			if (!extensions_priest.contains(pw)) {
				int old_rank = kappa.rank(pw);
				int num_undec = pw.countUndecided();
				if (reverse_order) {
					kappa.setRank(pw, old_rank + (largest_shift_by_dalal+1)*(largest_num_undec_possible-num_undec));
				}
				else {
					kappa.setRank(pw, old_rank + (largest_shift_by_dalal+1)*num_undec);
				}
				
			}
		}
		
		System.out.println(kappa);
		
		/*
		 * Guarantee C) by using Dalal Distance Measure
		 * Set ranks according to their Dalal Distance to the nearest extension
		 * As distance come in (.5)-steps, simply multiply with (*2) and convert to Integer
		 */
		System.out.println("Step 4.3: guarantee additional quality measure (here: Distance function Delta // 3-valued Dalal Distance)");

		DalalDistanceThreeValued dd3 = new DalalDistanceThreeValued();
		pwIterator = (PriestWorldIterator) pwIterator.reset();
		while (pwIterator.hasNext()) {
			PriestWorldAdapted pw = pwIterator.next();
			int old_rank = kappa.rank(pw);
			double distance = dd3.distance(extensions_priest, pw);
			kappa.setRank(pw, old_rank + (int) (2*distance));
		}
		
		System.out.println(kappa);
		
		System.out.println("------------------------------------------");
		
		/*
		 * Step 5: Read propositional formula psi from file
		 */
		System.out.println("Step 5: Read propositional formula psi from file / directly");
		
		// Option 1: Strong negation
		PlFormula psi = new Negation(new Proposition("b"));
		System.out.println("Strong negation: " + psi);
		
		// Option 2: Weak negation
		psi = new WeakNegation(new Proposition("b"));
		System.out.println("Weak negation: " + psi);
		
		// Option 3: New indecision operator which stipulates a formula is undecided
		psi = new Indecision(new Proposition("b"));
		System.out.println("Indecision: " + psi);
		
		psi = (new Negation(new Proposition("b"))).combineWithAnd(new Negation(new Proposition("c")));
		System.out.println("Custom: " + psi);
		
		// PARSE FROM FILE
		PlParserThreeValued parser_pl = new PlParserThreeValued();
		psi = parser_pl.parseFormulaFromFile(path_to_psi);
		
		System.out.println("Formula psi used for revision: " + psi);
		System.out.println("------------------------------------------");
		
		/*
		 * Step 6: Revise the preorder from Step 4 with formula \psi
		 * Return the 3-valued worlds on the same level
		 * This result is equivalent to the models of the revised ADF
		 */
		System.out.println("Step 6: Revise Extensions of ADF with formula psi by use of the preorder");

		int rank_of_psi = kappa.rank(psi);
		System.out.println("Rank of psi: " + rank_of_psi);
		
		Collection<PriestWorldAdapted> worlds_after_revision = new HashSet<PriestWorldAdapted>();
		pwIterator = (PriestWorldIterator) pwIterator.reset();
		while (pwIterator.hasNext()) {
			PriestWorldAdapted pw = pwIterator.next();
			if (pw.satisfies(psi) && kappa.rank(pw) == rank_of_psi) {
				worlds_after_revision.add(pw);
			}
		}

		System.out.println("Possible 3-valued worlds in extensions after revision: " + worlds_after_revision);
		
	}

    /** Default Constructor */
    public PreferredRevisionerADF(){}
}

