package org.tweetyproject.logics.translators.adfcl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.tweetyproject.commons.ParserException;

import org.tweetyproject.logics.cl.syntax.ClBeliefSet;
import org.tweetyproject.logics.cl.semantics.RankingFunction;
import org.tweetyproject.logics.cl.reasoner.ZReasoner;

import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.io.KppADFFormatParser;
import org.tweetyproject.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner;
import org.tweetyproject.arg.adf.reasoner.GroundReasoner;
import org.tweetyproject.arg.adf.reasoner.ModelReasoner;
import org.tweetyproject.arg.adf.reasoner.PreferredReasoner;
import org.tweetyproject.arg.adf.reasoner.StableReasoner;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;

/**
 * This class compares the inference behavior of Abstract Dialectical Frameworks (AFDs) and Ranking Functions (OCFs)
 * @author Jonas Schumacher
 *
 */
public class CompareInference {
	/**
	 * Compare inference behavior of an ADF with that of an OCF induced by translation function Theta
	 * @param args args
	 * @throws FileNotFoundException FileNotFoundException
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException {
		
		// Set path to ADF and chose translation function Theta
		String path_to_file = "src/main/resources/adf.txt";
		int theta = 3;
		
		/*
		 * Step 1: Create ADF from text file
		 */
		NativeMinisatSolver solver = new NativeMinisatSolver();
		LinkStrategy strat = new SatLinkStrategy(solver);
		KppADFFormatParser parser = new KppADFFormatParser(strat, true);
		AbstractDialecticalFramework adf = parser.parse(new File(path_to_file));
		
		/*
		 * Step 2: Create conditional belief base from ADF
		 */
		System.out.println("Apply translation Theta " + theta);
		
		ConverterADF2CL converter = new ConverterADF2CL();
		ClBeliefSet bs = converter.getBeliefSetFromADF(adf,theta);
		
		System.out.println("Resulting belief base:");
		System.out.println(bs);
		
		/*
		 * Step 3: Compare inference behavior of ADF and OCF
		 */
		System.out.println("----------------------------------------");
		System.out.println("Compare inference behavior:");
		
		// OCF System Z semantics
		System.out.println("Ranking function based on System Z:");
		ZReasoner reasoner_ocf = new ZReasoner();
		RankingFunction kappa = reasoner_ocf.getModel(bs);
		if (kappa == null) {
			System.out.println("The translation of this ADF is inconsistent.");
		}
		else {
			System.out.println(kappa);
			
			// ADF semantics
			// grounded
			AbstractDialecticalFrameworkReasoner reasoner_adf = new GroundReasoner(solver);
			System.out.println("----------------------------------------");
			System.out.println("ADF grounded semantics:");		
			converter.compareInference(adf,reasoner_adf,kappa);
			// preferred
			reasoner_adf = new PreferredReasoner(solver);
			System.out.println("----------------------------------------");
			System.out.println("ADF preferred semantics:");
			converter.compareInference(adf,reasoner_adf,kappa);
			// 2-valued
			reasoner_adf = new ModelReasoner(solver);
			System.out.println("----------------------------------------");
			System.out.println("ADF 2-valued semantics:");
			converter.compareInference(adf,reasoner_adf,kappa);
			// stable
			reasoner_adf = new StableReasoner(solver);
			System.out.println("----------------------------------------");
			System.out.println("ADF stable semantics:");
			converter.compareInference(adf,reasoner_adf,kappa);
		}
	}

    /** Default Constructor */
    public CompareInference(){}
}
