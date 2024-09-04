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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.ml.examples;

import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.ml.parser.MlParser;
import org.tweetyproject.logics.ml.reasoner.AbstractMlReasoner;
import org.tweetyproject.logics.ml.reasoner.SPASSMlReasoner;
import org.tweetyproject.logics.ml.syntax.MlBeliefSet;

/**
 * More examples for testing ModalParser and ModalReasoner. Shows how to
 * construct a modal logic knowledge base programmatically and how to query it
 * using the SPASS reasoner.
 *
 * @author Matthias Thimm
 */
public class MlExample2 {

	/**
	 * Default Constructor
	 */
	public MlExample2(){
		super();
	}

	/**
	 * Demonstrates the creation and reasoning over a modal logic belief set using the `MlParser`
	 * and `AbstractMlReasoner` classes.
	 *
	 * @param args command-line arguments (not used in this example).
	 * @throws ParserException if there is an error during parsing of the formulas.
	 * @throws IOException if there is an error accessing resources (e.g., the SPASS reasoner).
	 */
	public static void main(String[] args) throws ParserException, IOException {
		MlBeliefSet bs = new MlBeliefSet();
		MlParser parser = new MlParser();
		FolSignature sig = new FolSignature();
		sig.add(new Predicate("p", 0));
		sig.add(new Predicate("q", 0));
		sig.add(new Predicate("r", 0));
		parser.setSignature(sig);
		bs.add((RelationalFormula) parser.parseFormula("!(<>(p))"));
		bs.add((RelationalFormula) parser.parseFormula("p || r"));
		bs.add((RelationalFormula) parser.parseFormula("!r || [](q && r)"));
		bs.add((RelationalFormula) parser.parseFormula("[](r && <>(p || q))"));
		bs.add((RelationalFormula) parser.parseFormula("!p && !q"));
		System.out.println("Modal knowledge base: " + bs);
		AbstractMlReasoner reasoner = new SPASSMlReasoner("/add/path/to/SPASS");
		System.out.println("[](!p)      " + reasoner.query(bs, (FolFormula) parser.parseFormula("[](!p)")));
		System.out.println("<>(q || r)  " + reasoner.query(bs, (FolFormula) parser.parseFormula("<>(q || r)")));
		System.out.println("p           " + reasoner.query(bs, (FolFormula) parser.parseFormula("p")));
		System.out.println("r           " + reasoner.query(bs, (FolFormula) parser.parseFormula("r")));
		System.out.println("[](r)       " + reasoner.query(bs, (FolFormula) parser.parseFormula("[](r)")));
		System.out.println("[](q)       " + reasoner.query(bs, (FolFormula) parser.parseFormula("[](q)")));
	}
}
