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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.cli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.tweety.arg.adf.parser.KppADFFormatParser;
import net.sf.tweety.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner;
import net.sf.tweety.arg.adf.reasoner.AdmissibleReasoner;
import net.sf.tweety.arg.adf.reasoner.CompleteReasoner;
import net.sf.tweety.arg.adf.reasoner.ModelReasoner;
import net.sf.tweety.arg.adf.reasoner.NaiveReasoner;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.NativeLingelingSolver;
import net.sf.tweety.arg.adf.semantics.LinkStrategy;
import net.sf.tweety.arg.adf.semantics.SatLinkStrategy;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.sat.SatSolver;

/**
 * @author Mathias Hofer
 *
 */
public class CommandLineInterface {

	private static final IncrementalSatSolver satSolver = new NativeLingelingSolver();

	private static final Map<String, AbstractDialecticalFrameworkReasoner> reasonerBySemantics = new HashMap<String, AbstractDialecticalFrameworkReasoner>();

	private static final LinkStrategy linkStrategy = new SatLinkStrategy(satSolver);
	
	private static final KppADFFormatParser parser = new KppADFFormatParser(linkStrategy, true);

	private static final String prompt = "USAGE: java -jar jadf.jar <file> <sem>\r\n\nCOMMAND LINE ARGUMENTS:\r\n<file>  : Input filename for ADF instance.\r\n<sem>   : ADF semantics. <sem>={mod|nai|adm|com}";

	static {
		SatSolver.setDefaultSolver(satSolver);

		// cf|nai|adm|com|prf|grd|mod
		reasonerBySemantics.put("mod", new ModelReasoner(satSolver));
		reasonerBySemantics.put("nai", new NaiveReasoner(satSolver));
		reasonerBySemantics.put("adm", new AdmissibleReasoner(satSolver));
		reasonerBySemantics.put("com", new CompleteReasoner(satSolver));
	}

	public static void main(String[] args) {		
		if (args.length < 2) {
			System.out.println(prompt);
		} else {
			String filename = args[0];
			String semantics = args[1];
			AbstractDialecticalFrameworkReasoner reasoner = reasonerBySemantics.get(semantics);
			try {
				AbstractDialecticalFramework adf = parser.parseBeliefBaseFromFile(filename);
				System.out.println("Compute models... (all at once, thus it may take a while)");
				int modelCount = 0;
				Iterator<Interpretation> modelIterator = reasoner.modelIterator(adf);
				while (modelIterator.hasNext()) {
					Interpretation model = modelIterator.next();
					System.out.println(model);
					modelCount++;
				}
				System.out.println("Total: " + modelCount);
			} catch (ParserException | IOException e) {
				e.printStackTrace();
			}
		}
	}

}
