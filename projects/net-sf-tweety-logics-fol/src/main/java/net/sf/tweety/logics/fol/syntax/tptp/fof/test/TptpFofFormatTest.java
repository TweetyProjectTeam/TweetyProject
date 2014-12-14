package net.sf.tweety.logics.fol.syntax.tptp.fof.test;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofAndFormula;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofBeliefBase;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofConstant;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofForallQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofFormula;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofLogicFormula;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofNegation;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofSort;
import net.sf.tweety.logics.fol.syntax.tptp.fof.TptpFofVariable;

/**
 * @author Bastian Wolf
 */
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
