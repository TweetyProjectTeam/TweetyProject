package org.tweetyproject.arg.rankings.extensionreasoner;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;



/**
 * An example on how to utilize the "CopelandBasedComparisonExtensionReasoner".
 * This example refers to example 2 in the paper "On Supported Inference and Extension Selection in
 * Abstract Argumentation Frameworks – long version–".
 * @author Benajmin Birner
 *
 */
public class CopelandBasedComparisonExtensionReasonerExample {

	public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
	
	
		DungTheory dung = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        
        dung.add(a);
        dung.add(b);
        dung.add(c);
        dung.add(d);
        
        dung.addAttack(a, c);
        dung.addAttack(c, a);
        dung.addAttack(a, d);
        dung.addAttack(b, c);
        dung.addAttack(d, b);
        
        Extension<DungTheory> e1 = new Extension<DungTheory>();
        Extension<DungTheory> e2 = new Extension<DungTheory>();
        
        e1.add(a);
        e1.add(b);
        
        e2.add(c);
        e2.add(d);

        //it is also possible to add more then two Extensions to the list. The reasoner returns the best one(s)
        Collection<Extension<DungTheory>> extensionList = new HashSet<>();
        extensionList.add(e1);
        extensionList.add(e2);
        
        StronglyDefendedComparator strDef = new StronglyDefendedComparator();
        ArgumentsNonAttackedComparator argNonAtt = new ArgumentsNonAttackedComparator();
        SubsetDeleteAttacksComparator subDelCo = new SubsetDeleteAttacksComparator(ExtensionRankingSemantics.R_PR, false);
        DeleteAttacksComparator DelCo = new DeleteAttacksComparator(ExtensionRankingSemantics.R_PR, false);
        
        CopelandBasedComparisonExtensionReasoner coba = new CopelandBasedComparisonExtensionReasoner();
        
        LinkedList<Extension<DungTheory>> res0 = coba.getModels(extensionList, dung, strDef);
        LinkedList<Extension<DungTheory>> res1 = coba.getModels(extensionList, dung, argNonAtt);
        LinkedList<Extension<DungTheory>> res2 = coba.getModels(extensionList, dung, subDelCo);
        LinkedList<Extension<DungTheory>> res3 = coba.getModels(extensionList, dung, DelCo);
        
        
        System.out.println("results Strongly Defended:");
        for( Extension<DungTheory> ex : res0) {
        	System.out.println(ex.toString());
        }
        System.out.println();
        
        System.out.println("results Arguments Non Attacked:");
        for( Extension<DungTheory> ex : res1) {
        	System.out.println(ex.toString());
        }
        System.out.println();
        
        System.out.println("results Subset Delete Attacks:");
        for( Extension<DungTheory> ex : res2) {
        	System.out.println(ex.toString());
        }
        System.out.println();
        
        System.out.println("results Delete Attacks:");
        for( Extension<DungTheory> ex : res3) {
        	System.out.println(ex.toString());
        }
        
	}

}
