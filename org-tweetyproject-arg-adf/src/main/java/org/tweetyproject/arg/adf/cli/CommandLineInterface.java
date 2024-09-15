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
 * This class provides a command-line interface for working with Abstract Dialectical Frameworks (ADFs).
 * It allows users to specify an ADF instance via a file and apply different reasoning semantics to it.
 * The program then prints out the interpretations based on the specified reasoning semantics.
 *
 * <p>Supported semantics include:</p>
 * <ul>
 *   <li>mod: Model semantics</li>
 *   <li>nai: Naive semantics</li>
 *   <li>adm: Admissible semantics</li>
 *   <li>com: Complete semantics</li>
 * </ul>
 *
 * <p>Usage: java -jar jadf.jar &lt;file&gt; &lt;sem&gt;</p>
 * <p><code>&lt;file&gt;</code> : Input filename for the ADF instance</p>
 * <p><code>&lt;sem&gt;</code>  : ADF semantics. Options: mod | nai | adm | com</p>
 *
 * <p>Example: <code>java -jar jadf.jar myADFInstance.txt mod</code></p>
 *
 * @author Mathias Hofer
 */
public class CommandLineInterface {

    /** The SAT solver used by the reasoners. */
    private static final IncrementalSatSolver satSolver = new NativeMinisatSolver();

    /** A map to store reasoners corresponding to different ADF semantics. */
    private static final Map<String, AbstractDialecticalFrameworkReasoner> reasonerBySemantics = new HashMap<>();

    /** The link strategy used for parsing the ADF. */
    private static final LinkStrategy linkStrategy = new SatLinkStrategy(satSolver);

    /** The parser for reading ADF instances in the KPP ADF format. */
    private static final KppADFFormatParser parser = new KppADFFormatParser(linkStrategy, true);

    /** The usage prompt for the command-line interface. */
    private static final String prompt = "USAGE: java -jar jadf.jar <file> <sem>\r\n\nCOMMAND LINE ARGUMENTS:\r\n<file>  : Input filename for ADF instance.\r\n<sem>   : ADF semantics. <sem>={mod|nai|adm|com}";

    static {
        // Initialize reasoner mappings for supported semantics
        reasonerBySemantics.put("mod", new ModelReasoner(satSolver));
        reasonerBySemantics.put("nai", new NaiveReasoner(satSolver));
        reasonerBySemantics.put("adm", new AdmissibleReasoner(satSolver));
        reasonerBySemantics.put("com", new CompleteReasoner(satSolver));
    }

    /**
     * The main method serves as the entry point for the command-line interface.
     * It takes two arguments:
     * <ul>
     *   <li><code>&lt;file&gt;</code> - The input file containing the ADF instance</li>
     *   <li><code>&lt;sem&gt;</code> - The ADF semantics to apply (e.g., mod, nai, adm, com)</li>
     * </ul>
     * It then parses the ADF instance, applies the specified reasoning semantics,
     * and prints out the interpretations based on the model iterator.
     *
     * @param args Command-line arguments: the input file and the semantics type.
     */
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

