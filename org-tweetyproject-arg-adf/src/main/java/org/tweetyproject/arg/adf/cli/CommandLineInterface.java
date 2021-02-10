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
package org.tweetyproject.arg.adf.cli;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.tweetyproject.arg.adf.io.KppADFFormatParser;
import org.tweetyproject.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner;
import org.tweetyproject.arg.adf.reasoner.AdmissibleReasoner;
import org.tweetyproject.arg.adf.reasoner.CompleteReasoner;
import org.tweetyproject.arg.adf.reasoner.ModelReasoner;
import org.tweetyproject.arg.adf.reasoner.NaiveReasoner;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.LinkStrategy;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public class CommandLineInterface {

	private static final IncrementalSatSolver satSolver = new NativeMinisatSolver();

	private static final Map<String, AbstractDialecticalFrameworkReasoner> reasonerBySemantics = new HashMap<String, AbstractDialecticalFrameworkReasoner>();

	private static final LinkStrategy linkStrategy = new SatLinkStrategy(satSolver);

	private static final KppADFFormatParser parser = new KppADFFormatParser(linkStrategy, true);

	private static final String prompt = "USAGE: java -jar jadf.jar <file> <sem>\r\n\nCOMMAND LINE ARGUMENTS:\r\n<file>  : Input filename for ADF instance.\r\n<sem>   : ADF semantics. <sem>={mod|nai|adm|com}";

	static {
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
				AbstractDialecticalFramework adf = parser.parse(new File(filename));
				int modelCount = 0;
				Iterator<Interpretation> modelIterator = reasoner.modelIterator(adf);
				while (modelIterator.hasNext()) {
					Interpretation model = modelIterator.next();
					System.out.println(model);
					modelCount++;
				}
				System.out.println("Total: " + modelCount);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
