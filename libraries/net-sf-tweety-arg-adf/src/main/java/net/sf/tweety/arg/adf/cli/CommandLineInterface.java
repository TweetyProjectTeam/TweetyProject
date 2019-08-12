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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.arg.adf.parser.KPPADFFormatParser;
import net.sf.tweety.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner;
import net.sf.tweety.arg.adf.reasoner.AdmissibleReasoner;
import net.sf.tweety.arg.adf.reasoner.ModelReasoner;
import net.sf.tweety.arg.adf.reasoner.NaiveReasoner;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.NativeLingelingSolver;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.sat.SatSolver;

/**
 * @author Mathias Hofer
 *
 */
public class CommandLineInterface {

	private static IncrementalSatSolver satSolver = new NativeLingelingSolver();

	private static Map<String, AbstractDialecticalFrameworkReasoner> reasonerBySemantics = new HashMap<String, AbstractDialecticalFrameworkReasoner>();

	private static KPPADFFormatParser parser = new KPPADFFormatParser();

	private static final String prompt = "USAGE: java -jar jadf.jar <file> <sem>\r\n\nCOMMAND LINE ARGUMENTS:\r\n<file>  : Input filename for ADF instance.\r\n<sem>   : ADF semantics. <sem>={mod|nai|adm}";

	static {
		SatSolver.setDefaultSolver(satSolver);
		
		// cf|nai|adm|com|prf|grd|mod
		reasonerBySemantics.put("mod", new ModelReasoner(satSolver));
		reasonerBySemantics.put("nai", new NaiveReasoner(satSolver));
		reasonerBySemantics.put("adm", new AdmissibleReasoner(satSolver));
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
				Collection<Interpretation> models = reasoner.getModels(adf);
				for (Interpretation model : models) {
					System.out.println(model);
				}
				System.out.println("Total: " + models.size());
			} catch (ParserException | IOException e) {
				e.printStackTrace();
			}
		}
	}

}
