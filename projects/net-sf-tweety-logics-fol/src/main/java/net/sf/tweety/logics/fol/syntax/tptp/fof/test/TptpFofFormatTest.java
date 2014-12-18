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
package net.sf.tweety.logics.fol.syntax.tptp.fof.test;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofBeliefBase;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofConstant;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofFormula;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofNegation;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofSort;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofVariable;

/**
 * @author Bastian Wolf
 */
@SuppressWarnings("unused")
public class TptpFofFormatTest {

    public static void main(String[] args) {
    	
    	TptpFofBeliefBase beliefset = new TptpFofBeliefBase();
    	Set<TptpFofConstant> constants = new HashSet<TptpFofConstant>();
    	Set<TptpFofSort> sorts = new HashSet<TptpFofSort>();
    	
    	Set<TptpFofVariable> varset1 = new HashSet<TptpFofVariable>();
    	varset1.add(new TptpFofVariable("X"));
    	varset1.add(new TptpFofVariable("Y"));
    	
    	TptpFofSort sort1 = new TptpFofSort("Animal");
    	TptpFofSort sort2 = new TptpFofSort("Plant");
    	sorts.add(sort1);sorts.add(sort2);
//    	TptpFofLogicFormula formula3 = new TptpFofAndFormula(new TptpFor, second)
    	
//    	TptpFofFormula formula2 = new TptpFofForallQuantifiedFormula(variables, formula)
    	TptpFofFormula formula1 = new TptpFofNegation();
    	
    }
}
