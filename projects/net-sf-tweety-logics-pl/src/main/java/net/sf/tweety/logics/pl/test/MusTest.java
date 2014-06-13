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
package net.sf.tweety.logics.pl.test;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.commons.analysis.MusEnumerator;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.MarcoMusEnumerator;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

public class MusTest {
	public static void main(String[] args) throws ParserException, IOException{
		PlBeliefSet beliefSet = new PlBeliefSet();
		PlParser parser = new PlParser();
		for(int i = 0; i < 7; i++){
			beliefSet.add((PropositionalFormula)parser.parseFormula("a"+i));
			beliefSet.add((PropositionalFormula)parser.parseFormula("!a"+i));
		}
		beliefSet.add((PropositionalFormula)parser.parseFormula("!a1 && !a2"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("!a3 || !a4"));
		beliefSet.add((PropositionalFormula)parser.parseFormula("!a3 || !a4 || a5"));
		
		
		System.out.println(beliefSet);
		
		MusEnumerator<PropositionalFormula> enumerator = new MarcoMusEnumerator("/Users/mthimm/Projects/misc_bins/marco_py-1.0/marco.py");
		
		System.out.println(enumerator.minimalInconsistentSubsets(beliefSet));		
	}
}
