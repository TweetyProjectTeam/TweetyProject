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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.delp.examples;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.tweetyproject.arg.delp.parser.DelpParser;
import org.tweetyproject.arg.delp.reasoner.DelpReasoner;
import org.tweetyproject.arg.delp.semantics.GeneralizedSpecificity;
import org.tweetyproject.arg.delp.syntax.DefeasibleLogicProgram;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.fol.syntax.FolFormula;

/**
 * Shows how to parse and query a DeLP program.
 *
 * @author Matthias Thimm
 *
 */
public class DeLPExample {

    /**
     * The entry point of the application that parses a defeasible logic program from a file,
     * creates a reasoner with a specific criterion, and performs queries on the logic program.
     *
     * @param args Command-line arguments (not used in this example).
     * @throws FileNotFoundException If the specified file cannot be found.
     * @throws ParserException If there is an error while parsing the belief base or formula.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static void main(String[] args) throws FileNotFoundException, ParserException, IOException {
        DelpParser parser = new DelpParser();

        // Parse the defeasible logic program from the specified file
        DefeasibleLogicProgram delp = parser.parseBeliefBaseFromFile(
            DeLPExample.class.getResource("/birds2.txt").getFile()
        );

        // Create a reasoner with a Generalized Specificity criterion
        DelpReasoner reasoner = new DelpReasoner(new GeneralizedSpecificity());

        // Parse and query a formula, then print the result
        FolFormula query = (FolFormula) parser.parseFormula("Fly(opus)");
        System.out.println(query + "\t" + reasoner.query(delp, query));

        // Parse and query another formula, then print the result
        query = (FolFormula) parser.parseFormula("Fly(tweety)");
        System.out.println(query + "\t" + reasoner.query(delp, query));
    }

    /** Default Constructor */
    public DeLPExample() {}
}
