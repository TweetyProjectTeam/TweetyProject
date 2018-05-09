/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.ml.examples;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.RelationalFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.ml.ModalBeliefSet;
import net.sf.tweety.logics.ml.parser.ModalParser;
import net.sf.tweety.logics.ml.reasoner.ModalReasoner;
import net.sf.tweety.logics.ml.reasoner.SPASSModalReasoner;

/**
 * More examples for testing ModalParser and ModalReasoner
 * @author Matthias Thimm
 */
public class MlExample2 {
	public static void main(String[] args) throws ParserException, IOException {
		ModalBeliefSet bs = new ModalBeliefSet();
		ModalParser parser = new ModalParser();
		FolSignature sig = new FolSignature();
		sig.add(new Predicate("p",0));
		sig.add(new Predicate("q",0));
		sig.add(new Predicate("r",0));
		parser.setSignature(sig);
		bs.add((RelationalFormula) parser.parseFormula("!(<>(p))"));
		bs.add((RelationalFormula) parser.parseFormula("p || r"));
		bs.add((RelationalFormula) parser.parseFormula("!r || [](q && r)"));
		bs.add((RelationalFormula) parser.parseFormula("[](r && <>(p || q))"));
		bs.add((RelationalFormula) parser.parseFormula("!p && !q"));
		System.out.println("Modal knowledge base: " + bs);		
		ModalReasoner reasoner = new SPASSModalReasoner("/add/path/to/SPASS");		
		System.out.println("[](!p)      " + reasoner.query(bs,parser.parseFormula("[](!p)")).getAnswerBoolean());
		System.out.println("<>(q || r)  " + reasoner.query(bs,parser.parseFormula("<>(q || r)")).getAnswerBoolean());
		System.out.println("p           " + reasoner.query(bs,parser.parseFormula("p")).getAnswerBoolean());
		System.out.println("r           " + reasoner.query(bs,parser.parseFormula("r")).getAnswerBoolean());
		System.out.println("[](r)       " + reasoner.query(bs,parser.parseFormula("[](r)")).getAnswerBoolean());
		System.out.println("[](q)       " + reasoner.query(bs,parser.parseFormula("[](q)")).getAnswerBoolean());
	}
}
