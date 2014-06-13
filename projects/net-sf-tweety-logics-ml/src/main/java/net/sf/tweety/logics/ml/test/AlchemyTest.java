/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.ml.test;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.ml.AlchemyMlnReasoner;
import net.sf.tweety.logics.ml.MarkovLogicNetwork;

public class AlchemyTest {

	public static void main(String[] args) throws ParserException, IOException{
		Pair<MarkovLogicNetwork,FolSignature> exp1 = MlnTest.SmokersExample(4);
		AlchemyMlnReasoner reasoner = new AlchemyMlnReasoner(exp1.getFirst(),exp1.getSecond());
		FolParser parser = new FolParser();
		parser.setSignature(exp1.getSecond());
		FolFormula query = (FolFormula) parser.parseFormula("cancer(d0)");
		reasoner.setAlchemyInferenceCommand("/Users/mthimm/Desktop/infer");
		reasoner.query(query);
	}
}
